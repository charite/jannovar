package de.charite.compbio.jannovar.impl.parse.gff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.impl.util.ProgressBar;

/**
 * Parsing for GFF2, GTF, and GFF3 files.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class GFFParser {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = LoggerFactory.getLogger(GFFParser.class);

	/** whether or not to print progress bars */
	private boolean printProgressBars = false;

	/** {@link File} to parse */
	private final File file;

	/** version of the underlying GFF file */
	private final GFFVersion gffVersion;

	/**
	 * Initalize with path and enforce the given GFF version.
	 *
	 * @param path
	 *            path to the GFF file to parse
	 * @param gffVersion
	 *            GFF version to enforce
	 * @param printProgressBars
	 *            whether or not to print progress bars
	 */
	public GFFParser(String path, GFFVersion gffVersion, boolean printProgressBars) {
		this.file = new File(path);
		this.gffVersion = gffVersion;
		this.printProgressBars = printProgressBars;
	}

	/** @return whether or not to print progress bars */
	public boolean isPrintProgressBars() {
		return printProgressBars;
	}

	/** Set whether or not to print progress bars */
	public void setPrintProgressBars(boolean printProgressBars) {
		this.printProgressBars = printProgressBars;
	}

	/** @return {@link File} to parse */
	public File getFile() {
		return file;
	}

	/** @return version of the underlying GFF file */
	public GFFVersion getGffVersion() {
		return gffVersion;
	}

	/**
	 * Initialize with path to the file to parse and detect GFF version.
	 *
	 * For the version detection, the file will be opened and the header will be parsed.
	 *
	 * @param path
	 *            to the GFF file to parse
	 * @throws IOException
	 *             on problems of opening/reading/parsing <code>path</code> for version detection
	 */
	public GFFParser(String path) throws IOException {
		this.file = new File(path);
		LOGGER.info("Determining GFF version...");
		this.gffVersion = determineGFFVersion(file);
		LOGGER.info("  GFF version is {}", gffVersion.getVersion());
	}

	/**
	 * Parses the file and feed the {@link Feature} objects into <code>tmBuilder</code>.
	 *
	 * @param fp
	 *            {@link FeatureProcessor} to use during parsing
	 */
	public void parse(FeatureProcessor fp) {
		LOGGER.info("Parsing GFF...");
		// We use ProgressBar to display our progress in GFF parsing.
		ProgressBar bar = new ProgressBar(0, file.length(), printProgressBars);

		BufferedReader in = null;
		try {
			// Open GFF/GTF file.
			FileInputStream fip = new FileInputStream(file);
			if (file.getName().endsWith(".gz"))
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(fip)));
			else
				in = new BufferedReader(new InputStreamReader(fip));
			// Read file line by line, adding read features to result.
			String str;
			final int CHUNK_SIZE = 1000;
			int lineNo = 0;
			while ((str = in.readLine()) != null) {
				// skip info lines
				if (str.startsWith("#"))
					continue;
				fp.addFeature(parseFeature(str));

				if (++lineNo == CHUNK_SIZE) {
					bar.print(fip.getChannel().position());
					lineNo = 0;
				}
			}
			if (fip.getChannel().position() != bar.getMax())
				bar.print(bar.getMax());
		} catch (FeatureFormatException e) {
			LOGGER.warn("GFF with wrong Feature format: {}", e);
		} catch (IOException e) {
			LOGGER.warn("failed to read the GFF file: {}", e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				LOGGER.warn("Failed to close the GFF file reader: {}", e);
			}
		}
	}

	/**
	 * Processes a single feature / line from a GTF or GFF file.
	 *
	 * For example:
	 *
	 * <pre>
	 * chr1 mm9_knownGene exon 3195985 3197398 0.000000 - . gene_id "uc007aet.1"; transcript_id "uc007aet.1";
	 * </pre>
	 *
	 * Returns a {@link Feature} storing the data represented by this line or <code>null</code> if the line contains
	 * less than 8 columns separated by <code>'\t'</code>.
	 *
	 * @param featureLine
	 *            the line of the file as a {@link String}
	 * @return {@link Feature} with the parsed data of <code>featureLine</code>
	 * @throws FeatureFormatException
	 *             in the case of problems in parsing.
	 */
	public Feature parseFeature(String featureLine) throws FeatureFormatException {
		ArrayList<String> myfields = new ArrayList<String>();
		int index = 0;
		int start = 0;
		while ((index = featureLine.indexOf('\t', start)) >= 0) {
			myfields.add(featureLine.substring(start, index));
			start = index + 1;
		}

		if (start != featureLine.length()) {
			myfields.add(featureLine.substring(start));
		}

		if (myfields.size() < 9) {
			Object params[] = { myfields.size(), featureLine };
			LOGGER.warn("Skipping malformed feature line (missing columns ({})): {}", params);
			return null;
		}

		// Build the resulting feature.
		Feature feature = new Feature();
		feature.setSequenceID(myfields.get(Indices.SEQID));
		feature.setType(codeType(myfields.get(Indices.TYPE)));
		feature.setStart(Integer.parseInt(myfields.get(Indices.START)));
		feature.setEnd(Integer.parseInt(myfields.get(Indices.END)));
		feature.setStrand(codeStrand(myfields.get(Indices.STRAND)));
		feature.setPhase((byte) codePhase(myfields.get(Indices.PHASE)));
		processAttributes(myfields.get(Indices.ATTRIBUTES), feature);
		return feature;
	}

	/**
	 * Processes the attributes in GFF3 file format, e.g.
	 *
	 * <pre>
	 * ID=rna0;Name=NM_001011874.1;Parent=gene0;Dbxref=GeneID:497097
	 * </pre>
	 *
	 * or GTF2.2 file format, e.g.
	 *
	 * <pre>
	 * gene_id "uc007aet.1"; transcript_id "uc007aet.1";
	 * </pre>
	 *
	 * Adds attributes to <code>feature</code>.
	 *
	 * @throws FeatureFormatException
	 *             on problems with the given <code>feature</code>
	 */
	private void processAttributes(String attributeString, Feature feature) throws FeatureFormatException {
		int start = 0;
		int index = 0;
		if (attributeString.startsWith(" "))
			attributeString = attributeString.substring(1);
		while ((index = attributeString.indexOf(";", start)) > 0) {
			splitAndAddAttribute(attributeString.substring(start, index), feature);

			if (gffVersion.getVersion() == 3)
				start = index + 1;
			else
				start = index + 2;
		}
		// for GFF3 we need to add the last element
		if (start < attributeString.length())
			splitAndAddAttribute(attributeString.substring(start), feature);
	}

	/**
	 * Split up the attribute, value pair and add this attribute pair to the <code>feature</code>
	 *
	 * @throws FeatureFormatException
	 *             is thrown if attribute String does not contain the {@link GFFVersion#valueSeparator separator} for
	 *             this GFF file format.
	 */
	private void splitAndAddAttribute(String attribute, Feature feature) throws FeatureFormatException {
		int subIndex = 0;
		if ((subIndex = attribute.indexOf(gffVersion.getValueSeparator())) > 0) {
			if (gffVersion.getVersion() == 3) {
				feature.addAttribute(attribute.substring(0, subIndex), attribute.substring(subIndex + 1));
				// System.out.println(String.format("%s\t%s",attribute.substring(0, subIndex),
				// attribute.substring(subIndex+1)));
			} else {
				feature.addAttribute(attribute.substring(0, subIndex),
						attribute.substring(subIndex + 2, attribute.length() - 1));
				// System.out.println(String.format("%s\t%s",attribute.substring(0, subIndex),
				// attribute.substring(subIndex+2,rawfeature.length()-1)));
			}
		} else {
			throw new FeatureFormatException("attribut String without valid value separator ('"
					+ gffVersion.getValueSeparator() + "'): '" + attribute + "'");
		}
	}

	/**
	 * Codes the phase of the CDS reading frame in the exon. A simple cast from String to byte.
	 *
	 * @param phase
	 *            The phase of the CDS reading frame
	 * @return phase of the CDS reading frame
	 */
	private int codePhase(String phase) {

		if (phase.equals("0"))
			return 0;
		if (phase.equals("1"))
			return 1;
		if (phase.equals("2"))
			return 2;

		return -1;
	}

	/**
	 * Returns the strand as boolean. <code>true</code> for the positive strand (<ocde>'+'</code>) and
	 * <code>false</code> for the minus strand (<code>'-'</code>). If an other character or String is present in the
	 * strand field (e.g. <code>'?'</code>) a {@link FeatureFormatException} is thrown.
	 *
	 * @param strand
	 *            representation of the strand in the feature line
	 * @return <code>true</code> for the positive strand (<code>'+'</code>) and <code>false</code> for the minus strand
	 *         (<code>'-'</code>)
	 * @throws FeatureFormatException
	 *             if <code>strand</code> is not <code>'-'</code> or <code>'+'</code>.
	 */
	private static boolean codeStrand(String strand) throws FeatureFormatException {
		if (strand.equals("+"))
			return true;
		else if (strand.equals("-"))
			return false;
		else
			throw new FeatureFormatException("unknown strand: " + strand);
	}

	/**
	 * @param type
	 *            text from the GFF/GTF file to convert into a {@link FeatureType}
	 * @return {@link FeatureType} value for the given text
	 */
	private static FeatureType codeType(String type) {
		if (type.equals("exon"))
			return FeatureType.EXON;
		if (type.equals("CDS"))
			return FeatureType.CDS;
		if (type.equals("start_codon"))
			return FeatureType.START_CODON;
		if (type.equals("stop_codon"))
			return FeatureType.STOP_CODON;
		if (type.equals("gene"))
			return FeatureType.GENE;
		if (type.equals("mRNA"))
			return FeatureType.MRNA;
		if (type.equals("transcript"))
			return FeatureType.TRANSCRIPT;
		if (type.equals("region"))
			return FeatureType.REGION;
		if (type.equals("ncRNA"))
			return FeatureType.NCRNA;
		if (type.equals("tRNA"))
			return FeatureType.TRNA;
		return FeatureType.UNKNOWN;
	}

	/**
	 * Check and return the version of the given GFF file.
	 *
	 * The following versions are known here:
	 *
	 * <ul>
	 * <li>Version 2 - attributes separated by '; ' and gene_id, transcript_id tags<br>
	 * <code>GL456350.1      protein_coding  exon    993     1059    .       -       .        gene_id "ENSMUSG00000094121"; transcript_id "ENSMUST00000177695"; </code>
	 * </li>
	 * <li>Version 2.5 - also known as GTF and quite similar to Version 2<br>
	 * <code>chr1    mm9_knownGene   exon    3195985 3197398 0.000000        -       .       gene_id "uc007aet.1"; transcript_id "uc007aet.1";</code>
	 * </li>
	 * <li>Version 3 - the current recommended Version.<br>
	 * <code>NC_000067.6     RefSeq  mRNA    3214482 3671498 .       -       .       ID=rna0;Name=NM_001011874.1;Parent=gene0;</code>
	 * </li>
	 * </ul>
	 *
	 * If there is no header containing the <code>##gff-version</code> tag, we assume it is GFF version 2/2.5 aka. GTF.
	 *
	 * @throws IOException
	 *             if the file could not be read
	 */
	private static GFFVersion determineGFFVersion(File file) throws IOException {
		GFFVersion gffVersion = new GFFVersion(2); // default is version 2

		// Open compressed or uncompressed file.
		BufferedReader in = null;
		if (file.getName().endsWith(".gz"))
			in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		else
			in = new BufferedReader(new FileReader(file));

		// Determine the version.
		String str;
		while ((str = in.readLine()) != null) {
			if (!str.startsWith("#"))
				break;

			if (str.startsWith("##gff-version")) {
				String fields[] = str.split(" ");
				try {
					gffVersion = new GFFVersion(Integer.parseInt(fields[1]));
				} catch (NumberFormatException e) {
					LOGGER.warn("Failed to parse gff-version: {}", str);
				}
			}
		}

		in.close(); // close file again
		return gffVersion;
	}

	/**
	 * Simple static container class for indices in GFF/GTF files.
	 */
	private static class Indices {
		/** index of seq ID field */
		private final static int SEQID = 0;
		/** index of source field */
		@SuppressWarnings("unused")
		private final static int SOURCE = 1;
		/** index of type field */
		private final static int TYPE = 2;
		/** index of start field */
		private final static int START = 3;
		/** index of end field */
		private final static int END = 4;
		/** index of score field */
		@SuppressWarnings("unused")
		private final static int SCORE = 5;
		/** index of strand field */
		private final static int STRAND = 6;
		/** index of phase field */
		private final static int PHASE = 7;
		/** index of attributes field */
		private final static int ATTRIBUTES = 8;
	}

}
