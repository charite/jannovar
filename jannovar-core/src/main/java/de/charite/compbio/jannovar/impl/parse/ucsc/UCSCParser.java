package de.charite.compbio.jannovar.impl.parse.ucsc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.datasource.TranscriptModelBuilderHGNCExtender;
import de.charite.compbio.jannovar.hgnc.AltGeneIDType;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.parse.TranscriptParser;
import de.charite.compbio.jannovar.impl.parse.TranscriptSupportLevelsSetterFromLengths;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptSupportLevels;

// TODO(holtgrem): Interpret knownCanonical!

/**
 * Parser for the UCCSC knownGene and related files.
 *
 * Parses the four files from the UCSC database relating the KnownGenes. The main file, <code>knownGene.txt</code>, is
 * tab-separated and has the following fields:
 *
 * <ol>
 * <li>`name` e.g., uc001irt.4. This is a UCSC knownGene identifier. We will use the kgXref table to convert to gene
 * symbol etc.</li>
 * <li>`chrom` e.g., chr10</li>
 * <li>`strand` e.g., +</li>
 * <li>`txStart` e.g., 24497719</li>
 * <li>`txEnd` e.g., 24836772</li>
 * <li>`cdsStart` e.g., 24498122</li>
 * <li>`cdsEnd` e.g., 24835253</li>
 * <li>`exonCount` e.g., 17</li>
 * <li>`exonStarts` e.g., 24497719,24508554,24669797,.... (total of 17 ints for this example)</li>
 * <li>`exonEnds` e.g., 24498192,24508838,24669996, .... (total of 17 ints for this example)</li>
 * <li>`proteinID` e.g., NP_001091971</li>
 * <li>`alignID` e.g., uc001irt.4 (Note: We do not need this field for our app).</li>
 * </ol>
 *
 * Note that this file is a MySQL dump file used at the UCSC database. We will use this program to create a serialized
 * java object that can quickly be input to the Jannovar program.
 *
 * This class additionally parses the ucsc <code>KnownToLocusLink.txt</code> file, which contains cross references from
 * the ucsc IDs to the corresponding Entrez Gene ids (earlier known as Locus Link):
 *
 * <pre>
 * uc010eve.3      3805
 * uc002qug.4      3805
 * uc010evf.3      3805
 * ...
 * </pre>
 *
 * The class additionally parses the files <code>knownGeneMrna.txt</code> and <code>kgXref.txt</code>.
 *
 * The result of parsing is the creation of a list of {@link TranscriptModel} objects.
 *
 * It is possible to parse directly from the gzip file without decompressing them, or the start from the decompressed
 * files. The class checks of the files exist and if they have the suffix "gz". *
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class UCSCParser implements TranscriptParser {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(UCSCParser.class);

	/**
	 * Number of tab-separated fields in then UCSC knownGene.txt file (build hg19).
	 */
	private static final int NFIELDS = 12;

	/**
	 * Path to the {@link ReferenceDictionary} to use for name/id and id/length mapping
	 */
	private final ReferenceDictionary refDict;

	/** Path to directory where the to-be-parsed files live */
	private final String basePath;

	/** INI {@link Section} from the configuration. */
	private final Section iniSection;

	/**
	 * Map of all genes loaded so far. The key is the UCSC id, e.g. uc0001234.3.
	 */
	private HashMap<String, TranscriptModelBuilder> knownGeneMap;

	/**
	 * @param refDict
	 *            path to {@link ReferenceDictionary} to use for name/id and id/length mapping.
	 * @param basePath
	 *            path to where the to-be-parsed files live
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public UCSCParser(ReferenceDictionary refDict, String basePath, Section iniSection) {
		this.refDict = refDict;
		this.basePath = basePath;
		this.iniSection = iniSection;
		this.knownGeneMap = new HashMap<String, TranscriptModelBuilder>();
	}

	public ImmutableList<TranscriptModel> run() throws TranscriptParseException {
		// Build paths to UCSC files.
		final String knownGenePath = PathUtil.join(basePath, getINIFileName("knownGene"));
		final String knownGeneMrnaPath = PathUtil.join(basePath, getINIFileName("knownGeneMrna"));
		final String kgXrefPath = PathUtil.join(basePath, getINIFileName("kgXref"));
		final String knownToLocusLinkPath = PathUtil.join(basePath, getINIFileName("knownToLocusLink"));
		String knownCanonicalPath = null;
		if (getINIFileName("knownCanonical") != null && !"".equals(getINIFileName("knownCanonical")))
			knownCanonicalPath = PathUtil.join(basePath, getINIFileName("knownCanonical"));

		// Parse the UCSC files.
		parseKnownGeneFile(knownGenePath);
		parseKnownGeneMrna(knownGeneMrnaPath);
		parseKnownGeneXref(kgXrefPath);
		parseKnown2LocusLink(knownToLocusLinkPath);
		if (knownCanonicalPath != null)
			parseKnownCanonical(knownCanonicalPath);
		else
			TranscriptSupportLevelsSetterFromLengths.run(this.knownGeneMap.values());

		// Augment information in builders with
		try {
			new TranscriptModelBuilderHGNCExtender(basePath, r -> Lists.newArrayList(r.getEntrezID()),
					tx -> tx.getGeneID()).run(this.knownGeneMap);
		} catch (JannovarException e) {
			throw new UncheckedJannovarException("Problem extending transcripts with HGNC information", e);
		}

		// Build result list.
		ImmutableList.Builder<TranscriptModel> result = new ImmutableList.Builder<TranscriptModel>();
		for (Map.Entry<String, TranscriptModelBuilder> entry : knownGeneMap.entrySet()) {
			if (entry.getValue().getAltGeneIDs().isEmpty() && entry.getValue().getGeneID() != null) {
				LOGGER.info("Using UCSC Entrez ID {} for transcript {} as HGNC did not provide alternative gene ID",
						new Object[] { entry.getValue().getGeneID(), entry.getValue().getAccession() });
				entry.getValue().getAltGeneIDs().put(AltGeneIDType.ENTREZ_ID.toString(), entry.getValue().getGeneID());
			}
			TranscriptModel model = entry.getValue().build();
			if (checkTranscriptModel(model))
				result.add(model);
		}
		return result.build();
	}

	/**
	 * Check whether the <code>model</code> has problems or not.
	 *
	 * Known problems that is checked for:
	 *
	 * <ul>
	 * <li>mRNA sequence is shorter than the sum of the exon lengths</li>
	 * </ul>
	 *
	 * @param model
	 *            {@link TranscriptModel} to check for consistency
	 * @return <code>false</code> if known problems have been found
	 */
	private boolean checkTranscriptModel(TranscriptModel model) {
		if (model.transcriptLength() > model.getSequence().length()) {
			LOGGER.debug("Transcript {} is indicated to be longer than its sequence. Ignoring.", model.getAccession());
			return false;
		}
		return true;
	}

	/**
	 * The function parses a single line of the knownGene.txt file.
	 *
	 * The fields of the file are tab separated and have the following structure:
	 *
	 * <ul>
	 * <li>0: name (UCSC known gene id, e.g., "uc021olp.1"</li>
	 * <li>1: chromosome, e.g., "chr1"</li>
	 * <li>2: strand, e.g., "-"</li>
	 * <li>3: transcription start, e.g., "38674705"</li>
	 * <li>4: transcription end, e.g., "38680439"</li>
	 * <li>5: CDS start, e.g., "38677458"</li>
	 * <li>6: CDS end, e.g., "38678111"</li>
	 * <li>7: exon count, e.g., "4"</li>
	 * <li>8: exonstarts, e.g., "38674705,38677405,38677769,38680388,"</li>
	 * <li>9: exonends, e.g., "38676494,38677494,38678123,38680439,"</li>
	 * <li>10: name, again (?), e.g., "uc021olp.1"</li>
	 * </ul>
	 *
	 * The function additionally parses the start and end of the exons. Note that in the UCSC database, positions are
	 * represented using half-open, zero-based coordinates. That is, if start is 2 and end is 7, then the first
	 * nucleotide is at position 3 (one-based) and the last nucleotide is at positon 7 (one-based). For now, we are
	 * switching the coordinates to fully-closed one based by incrementing all start positions by one. This is the way
	 * coordinates are typically represented in VCF files and is the way coordinate calculations are done in annovar. At
	 * a later date, it may be worthwhile to switch to the UCSC-way of half-open zero based coordinates.
	 *
	 * @param line
	 *            A single line of the UCSC knownGene.txt file
	 * @return {@link TranscriptModelBuilder} representing the line
	 * @throws TranscriptParseException
	 *             on problems parsing the data
	 */
	public TranscriptModelBuilder parseTranscriptModelFromLine(String line) throws TranscriptParseException {
		TranscriptModelBuilder tib = new TranscriptModelBuilder();
		String A[] = line.split("\t");
		if (A.length != NFIELDS) {
			String error = String.format(
					"Malformed line in UCSC knownGene.txt file:\n%s\nExpected %d fields but there were %d", line,
					NFIELDS, A.length);
			throw new TranscriptParseException(error);
		}
		/* Field 0 has the accession number, e.g., uc010nxr.1. */
		tib.setAccession(A[0]);
		tib.setGeneSymbol(tib.getAccession()); // will be replaced when parsing
												// geneXref file.
		Integer chrID = refDict.getContigNameToID().get(A[1]);
		if (chrID == null) // scaffolds such as chrUn_gl000243 cause Exception
							// to be thrown.
			throw new TranscriptParseException("Could not parse chromosome field: " + A[1]);

		char strandC = A[2].charAt(0);
		if (strandC != '+' && strandC != '-') {
			throw new TranscriptParseException("Malformed strand: " + A[2]);
		}
		Strand strand = (strandC == '+') ? Strand.FWD : Strand.REV;
		tib.setStrand(strand);

		int txStart, txEnd;
		try {
			txStart = Integer.parseInt(A[3]) + 1; // +1 to convert to one-based
													// fully closed numbering
		} catch (NumberFormatException e) {
			throw new TranscriptParseException("Could not parse txStart:" + A[3]);
		}
		try {
			txEnd = Integer.parseInt(A[4]);
		} catch (NumberFormatException e) {
			throw new TranscriptParseException("Could not parse txEnd:" + A[4]);
		}
		tib.setTXRegion(
				new GenomeInterval(refDict, Strand.FWD, chrID.intValue(), txStart, txEnd, PositionType.ONE_BASED)
						.withStrand(strand));

		int cdsStart, cdsEnd;
		try {
			cdsStart = Integer.parseInt(A[5]) + 1;// +1 to convert to one-based
													// fully closed numbering
		} catch (NumberFormatException e) {
			throw new TranscriptParseException("Could not parse cdsStart:" + A[5]);
		}
		try {
			cdsEnd = Integer.parseInt(A[6]);
		} catch (NumberFormatException e) {
			throw new TranscriptParseException("Could not parse cdsEnd:" + A[6]);
		}
		tib.setCDSRegion(
				new GenomeInterval(refDict, Strand.FWD, chrID.intValue(), cdsStart, cdsEnd, PositionType.ONE_BASED)
						.withStrand(strand));

		// Get number of exons.
		short exonCount;
		try {
			exonCount = Short.parseShort(A[7]);
		} catch (NumberFormatException e) {
			throw new TranscriptParseException("Could not parse exonCount:" + A[7]);
		}

		/* Now parse the exon ends and starts */
		int[] exonStarts = new int[exonCount];
		/** End positions of each of the exons of this transcript */
		int[] exonEnds = new int[exonCount];
		String starts = A[8];
		String ends = A[9];
		String B[] = starts.split(",");
		if (B.length != exonCount) {
			String error = String.format("[UCSCKGParser] Malformed exonStarts list: found %d but I expected %d exons",
					B.length, exonCount);
			error = String.format("%s. This should never happen, the knownGene.txt file may be corrupted", error);
			throw new TranscriptParseException(error);
		}
		for (int i = 0; i < exonCount; ++i) {
			try {
				exonStarts[i] = Integer.parseInt(B[i]) + 1; // Change 0-based to
															// 1-based numbering
			} catch (NumberFormatException e) {
				String error = String.format("[UCSCKGParser] Malformed exon start at position %d of line %s", i,
						starts);
				error = String.format("%s. This should never happen, the knownGene.txt file may be corrupted", error);
				throw new TranscriptParseException(error);
			}
		}
		// Now do the ends.
		B = ends.split(",");
		for (int i = 0; i < exonCount; ++i) {
			try {
				exonEnds[i] = Integer.parseInt(B[i]);
			} catch (NumberFormatException e) {
				String error = String.format("[UCSCKGParser] Malformed exon end at position %d of line %s", i, ends);
				error = String.format("%s. This should never happen, the knownGene.txt file may be corrupted", error);
				throw new TranscriptParseException(error);
			}
		}

		for (int i = 0; i < exonStarts.length; ++i)
			tib.addExonRegion(new GenomeInterval(refDict, Strand.FWD, chrID.intValue(), exonStarts[i], exonEnds[i],
					PositionType.ONE_BASED));

		return tib;
	}

	/**
	 * Parses the UCSC knownGene.txt file.
	 *
	 * @param kgPath
	 *            path to the knownGene.txt file
	 * @throws TranscriptParseException
	 *             on problems parsing the file
	 */
	private void parseKnownGeneFile(String kgPath) throws TranscriptParseException {
		// Error handling can be improved with Java 7.
		String s = null;
		BufferedReader br = null;
		// int linecount=0;
		// int exceptionCount=0;
		try {
			br = getBufferedReaderFromFilePath(kgPath, kgPath.endsWith(".gz"));

			String line;

			while ((line = br.readLine()) != null) {
				// linecount++;
				try {
					TranscriptModelBuilder tib = parseTranscriptModelFromLine(line);
					this.knownGeneMap.put(tib.getAccession(), tib);
				} catch (TranscriptParseException e) {
					// exceptionCount++;
				}
			}
			// System.out.println("[INFO] Parsed " + knownGeneMap.size() +
			// " transcripts from UCSC knownGene resource");
		} catch (FileNotFoundException fnfe) {
			s = String.format("[Jannovar/USCSKGParser] Could not find KnownGene.txt file: %s\n%s", kgPath,
					fnfe.toString());
		} catch (IOException e) {
			s = String.format("[Jannovar/USCSKGParser] Exception while parsing UCSC KnownGene file at \"%s\"\n%s",
					kgPath, e.toString());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// swallow, nothing we can do about it
			}
		}
		if (s != null)
			throw new TranscriptParseException(s);
	}

	/**
	 * Parses the ucsc knownToLocusLink.txt file, which contains cross references from ucsc KnownGene ids to Entrez Gene
	 * ids. The function than adds an Entrez gene id to the corresponding {@link TranscriptModelBuilder} objects.
	 */
	private void parseKnown2LocusLink(String locusPath) throws TranscriptParseException {
		try {
			BufferedReader br = getBufferedReaderFromFilePath(locusPath, locusPath.endsWith(".gz"));
			String line;

			int foundID = 0;
			int notFoundID = 0;

			while ((line = br.readLine()) != null) {
				String A[] = line.split("\t");
				if (A.length != 2) {
					String msg = String.format("Bad format for UCSC KnownToLocusLink.txt file: %s. "
							+ "Got %d fields instead of the expected 2.", line, A.length);
					throw new TranscriptParseException(msg);
				}
				String id = A[0];
				TranscriptModelBuilder tbi = this.knownGeneMap.get(id);
				if (tbi == null) {
					/**
					 * Note: many of these sequences seem to be for genes on scaffolds, e.g., chrUn_gl000243
					 */
					notFoundID++;
					continue;
				}
				foundID++;
				tbi.setGeneID(A[1]);
			}
			br.close();
			LOGGER.info("knownToLocusLink contained ids for {} knownGenes (no ids available for {})", foundID,
					notFoundID);
		} catch (FileNotFoundException fnfe) {
			String s = String.format("Exception while parsing UCSC  knownToLocusLink file at \"%s\"\n%s", locusPath,
					fnfe.toString());
			throw new TranscriptParseException(s);
		} catch (IOException e) {
			String s = String.format("Exception while parsing UCSC KnownToLocusfile at \"%s\"\n%s", locusPath,
					e.toString());
			throw new TranscriptParseException(s);
		}
	}

	/**
	 * Parse the knownCanonical.txt file and set the transcript support level of {@link #tmb}.
	 *
	 * @param knownCanonicalPath
	 *            path to the knownCanonical.txt file
	 * @throws TranscriptParseException
	 *             in case of problems
	 */
	private void parseKnownCanonical(String knownCanonicalPath) throws TranscriptParseException {
		// assign LOW_PRIORITY to all transcripts
		for (TranscriptModelBuilder tmb : knownGeneMap.values())
			tmb.setTranscriptSupportLevel(TranscriptSupportLevels.LOW_PRIORITY);

		// actually parse the file
		try {
			BufferedReader br = getBufferedReaderFromFilePath(knownCanonicalPath, knownCanonicalPath.endsWith(".gz"));
			String line;

			int foundID = 0;
			int notFoundID = 0;

			while ((line = br.readLine()) != null) {
				String A[] = line.split("\t");
				if (A.length != 6) {
					String msg = String.format("Bad format for UCSC knownCanonicalPath.txt file: %s. "
							+ "Got %d fields instead of the expected 6.", line, A.length);
					throw new TranscriptParseException(msg);
				}
				final String primaryTranscriptID = A[5];
				TranscriptModelBuilder tbi = this.knownGeneMap.get(primaryTranscriptID);
				if (tbi != null)
					tbi.setTranscriptSupportLevel(TranscriptSupportLevels.UCSC_CANONICAL);
			}
			br.close();
			LOGGER.info("knownCanonicalPath contained ids for {} knownGenes (no ids available for {})", foundID,
					notFoundID);
		} catch (FileNotFoundException fnfe) {
			String s = String.format("Exception while parsing UCSC knownCanonicalPath file at \"%s\"\n%s",
					knownCanonicalPath, fnfe.toString());
			throw new TranscriptParseException(s);
		} catch (IOException e) {
			String s = String.format("Exception while parsing UCSC knownCanonicalPath at \"%s\"\n%s",
					knownCanonicalPath, e.toString());
			throw new TranscriptParseException(s);
		}
	}

	/**
	 * Input FASTA sequences from the UCSC hg19 file {@code knownGeneMrna.txt} Note that the UCSC sequences are all in
	 * lower case, but we convert them here to all upper case letters to simplify processing in other places of this
	 * program. The sequences are then added to the corresponding {@link TranscriptModelBuilder} objects.
	 */
	private void parseKnownGeneMrna(String mRNAPath) throws TranscriptParseException {
		try {
			BufferedReader br = getBufferedReaderFromFilePath(mRNAPath, mRNAPath.endsWith(".gz"));
			String line;
			int kgWithNoSequence = 0;
			int foundSequence = 0;

			while ((line = br.readLine()) != null) {
				String A[] = line.split("\t");
				if (A.length != 2) {
					String msg = String.format("Bad format for UCSC KnownToLocusLink.txt file: %s. "
							+ "Got %d fields instead of the expected 2.", line, A.length);
					throw new TranscriptParseException(msg);
				}

				String id = A[0];
				String seq = A[1].toUpperCase();
				TranscriptModelBuilder tbi = this.knownGeneMap.get(id);
				if (tbi == null) {
					/**
					 * Note: many of these sequences seem to be for genes on scaffolds, e.g., chrUn_gl000243
					 */
					// System.err.println("Error, could not find FASTA sequence for known gene \""
					// + id + "\"");
					kgWithNoSequence++;
					continue;
					// System.exit(1);
				}
				foundSequence++;
				tbi.setSequence(seq);
			}
			br.close();
			LOGGER.info("Found {} transcript models from UCSC KnownGenes resource, {} of which had sequences",
					foundSequence, (foundSequence - kgWithNoSequence));
		} catch (FileNotFoundException fnfe) {
			String s = String.format("Could not find file: %s\n%s", mRNAPath, fnfe.toString());
			throw new TranscriptParseException(s);
		} catch (IOException ioe) {
			String s = String.format("Exception while parsing UCSC KnownGene FASTA file at \"%s\"\n%s", mRNAPath,
					ioe.toString());
			throw new TranscriptParseException(s);
		}
	}

	/**
	 * Input xref information for the known genes. This method parses the ucsc xref table to get the gene symbol that
	 * corresponds to the ucsc kgID. The information is then added to the corresponding {@link TranscriptModelBuilder}
	 * object.
	 * <P>
	 * Note that some of the fields are empty, which can cause a problem for Java's split function, which then conflates
	 * neighboring fields. Therefore, we instead just count the number of tab signs to get to the 5th field.
	 * <P>
	 * uc001aca.2 NM_198317 Q6TDP4 KLH17_HUMAN KLHL17 NM_198317 NP_938073 Homo sapiens kelch-like 17 (Drosophila)
	 * (KLHL17), mRNA.
	 * <P>
	 * The structure of the file is
	 * <UL>
	 * <LI>0: UCSC knownGene id, e.g., "uc001aca.2" (this is the key used to match entries to the knownGene.txt file)
	 * <LI>1: Accession number (refseq if availabl), e.g., "NM_198317"
	 * <LI>2: Uniprot accession number, e.g., "Q6TDP4"
	 * <LI>3: UCSC stable id, e.g., "KLH17_HUMAN"
	 * <LI>4: Gene symbol, e.g., "KLH17"
	 * <LI>5: (?) Additional mRNA accession
	 * <LI>6: (?) Protein accession number
	 * <LI>7: Description
	 * </UL>
	 */
	private void parseKnownGeneXref(String xRefPath) throws TranscriptParseException {
		// Error handling can be improved in Java 7.
		String err = null;
		BufferedReader br = null;

		try {
			br = getBufferedReaderFromFilePath(xRefPath, xRefPath.endsWith(".gz"));
			String line;
			// int kgWithNoXref=0;
			// int kgWithXref=0;

			while ((line = br.readLine()) != null) {
				if (line.startsWith("#"))
					continue; /* Skip comment line */
				String A[] = line.split("\t");
				if (A.length < 8) {
					err = String.format("Error, malformed ucsc xref line: %s\nExpected 8 fields but got %d", line,
							A.length);
					throw new TranscriptParseException(err);
				}
				String transcriptID = A[0];
				String geneSymbol = A[4];
				TranscriptModelBuilder tbi = this.knownGeneMap.get(transcriptID);
				if (tbi == null) {
					/**
					 * Note: many of these sequences seem to be for genes on scaffolds, e.g., chrUn_gl000243
					 */
					// System.err.println("Error, could not find xref sequence for known gene \""
					// + id + "\"");
					// kgWithNoXref++;
					continue;
					// System.exit(1);
				}
				// kgWithXref++;
				tbi.setGeneSymbol(geneSymbol);
				// System.out.println("x: \"" + geneSymbol + "\"");
			}
		} catch (FileNotFoundException fnfe) {
			err = String.format("Could not find file: %s\n%s", xRefPath, fnfe.toString());
		} catch (IOException e) {
			err = String.format("Exception while parsing UCSC KnownGene xref file at \"%s\"\n%s", xRefPath,
					e.toString());
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// swallow, nothing we can do about it
				}
		}
		if (err != null)
			throw new TranscriptParseException(err);
	}

	/**
	 * @param key
	 *            name of the INI entry
	 * @return file name from INI <code>key</code.
	 */
	private String getINIFileName(String key) {
		return new File(iniSection.get(key)).getName();
	}

	/**
	 * Open a file handle from a gzip-compressed or uncompressed file
	 *
	 * @param path
	 *            Path to the file to be opened
	 * @param isGzip
	 *            whether or not the file is gzip-compressed
	 * @return Corresponding BufferedReader file handle.
	 * @throws IOException
	 *             on I/O errors
	 */
	private static BufferedReader getBufferedReaderFromFilePath(String path, boolean isGzip) throws IOException {
		FileInputStream fin = new FileInputStream(path);
		BufferedReader br;
		if (isGzip)
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fin)));
		else
			br = new BufferedReader(new InputStreamReader(new DataInputStream(fin)));
		return br;
	}

}
