package jannovar;

/** Command line functions from apache */
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import jannovar.CommandLineParser.HelpRequestedException;
import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
import jannovar.common.ChromosomeMap;
import jannovar.common.Constants;
import jannovar.common.Constants.Release;
import jannovar.common.VCFStrings;
import jannovar.exception.AnnotationException;
import jannovar.exception.FileDownloadException;
import jannovar.exception.InvalidAttributException;
import jannovar.exception.JannovarException;
import jannovar.exome.Variant;
import jannovar.io.EnsemblFastaParser;
import jannovar.io.FastaParser;
import jannovar.io.GFFparser;
import jannovar.io.RefSeqFastaParser;
import jannovar.io.SerializationManager;
import jannovar.io.TranscriptDataDownloader;
import jannovar.io.UCSCKGParser;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;

/**
 * This is the driver class for a program called Jannovar. It has two purposes
 * <OL>
 * <LI>Take the UCSC files knownGene.txt, kgXref.txt, knownGeneMrna.txt, and knownToLocusLink.txt, and to create
 * corresponding {@link jannovar.reference.TranscriptModel TranscriptModel} objects and to serialize them. The resulting
 * serialized file can be used both by this program itself (see next item) or by the main Exomizer program to annotated
 * VCF file.
 * <LI>Using the serialized file of {@link jannovar.reference.TranscriptModel TranscriptModel} objects (see above item)
 * annotate a VCF file using annovar-type program logic. Note that this functionality is also used by the main Exomizer
 * program and thus this program can be used as a stand-alone annotator ("Jannovar") or as a way of testing the code for
 * the Exomizer.
 * </OL>
 * <P>
 * To run the "Jannovar" executable:
 * <P>
 * {@code java -Xms1G -Xmx1G -jar Jannovar.jar -V xyz.vcf -D $SERIAL}
 * <P>
 * This will annotate a VCF file. The results of jannovar annotation are shown in the form
 *
 * <PRE>
 * Annotation {original VCF line}
 * </PRE>
 * <P>
 * Just a reminder, to set up annovar to do this, use the following commands.
 *
 * <PRE>
 *   perl annotate_variation.pl --downdb knownGene --buildver hg19 humandb/
 * </PRE>
 *
 * then, to annotate a VCF file called BLA.vcf, we first need to convert it to Annovar input format and run the main
 * annovar program as follows.
 *
 * <PRE>
 * $ perl convert2annovar.pl BLA.vcf -format vcf4 > BLA.av
 * $ perl annotate_variation.pl -buildver hg19 --geneanno BLA.av --dbtype knowngene humandb/
 * </PRE>
 *
 * This will create two files with all variants and a special file with exonic variants.
 * <p>
 * There are three ways of using this program.
 * <ol>
 * <li>To create a serialized version of the UCSC gene definition data. In this case, the command-line flag <b>- S</b>
 * is provide as is the path to the four UCSC files. Then, {@code anno.serialize()} is true and a file <b>ucsc.ser</b>
 * is created.
 * <li>To deserialize the serialized data (<b>ucsc.ser</b>). In this case, the flag <b>- D</b> must be used.
 * <li>To simply read in the UCSC data without creating a serialized file.
 * </ol>
 * Whichever of the three versions is chosen, the user may additionally pass the path to a VCF file using the <b>-v</b>
 * flag. If so, then this file will be annotated using the UCSC data, and a new version of the file will be written to a
 * file called test.vcf.jannovar (assuming the original file was named test.vcf). The
 *
 * @author Peter N Robinson
 * @version 0.33 (29 December, 2013)
 */
public class Jannovar {
	/** List of all lines from knownGene.txt file from UCSC */
	private ArrayList<TranscriptModel> transcriptModelList = null;
	/** Map of Chromosomes */
	private HashMap<Byte, Chromosome> chromosomeMap = null;
	/** List of variants from input file to be analysed. */
	private final ArrayList<Variant> variantList = null;
	/** Name of the UCSC serialized data file that will be created by Jannovar. */
	private static final String UCSCserializationFileName = "ucsc_%s.ser";
	/**
	 * Name of the Ensembl serialized data file that will be created by Jannovar.
	 */
	private static final String EnsemblSerializationFileName = "ensembl_%s.ser";
	/**
	 * Name of the refSeq serialized data file that will be created by Jannovar.
	 */
	private static final String RefseqSerializationFileName = "refseq_%s.ser";

	/** Configuration for the Jannovar program. */
	JannovarOptions options = new JannovarOptions();

	public static void main(String argv[]) {
		// Create Jannovar object, this includes parsing the command line.
		Jannovar anno = null;
		try {
			anno = new Jannovar(argv);
		} catch (ParseException e1) {
			System.exit(1); // something went wrong, return 1
		} catch (HelpRequestedException e1) {
			System.exit(0); // help requested and printed, return 0
		}

		/*
		 * Option 1. Download the UCSC files from the server, create the ucsc.ser file, and return.
		 */
		try {
			if (anno.createUCSC()) {
				anno.downloadTranscriptFiles(jannovar.common.Constants.UCSC, anno.options.genomeRelease);
				anno.inputTranscriptModelDataFromUCSCFiles();
				anno.serializeUCSCdata();
				return;
			} else if (anno.createEnsembl()) {
				anno.downloadTranscriptFiles(jannovar.common.Constants.ENSEMBL, anno.options.genomeRelease);
				anno.inputTranscriptModelDataFromEnsembl();
				anno.serializeEnsemblData();
				return;
			} else if (anno.createRefseq()) {
				anno.downloadTranscriptFiles(jannovar.common.Constants.REFSEQ, anno.options.genomeRelease);
				anno.inputTranscriptModelDataFromRefseq();
				anno.serializeRefseqData();
				return;
			}
		} catch (JannovarException e) {
			System.err.println("[ERROR] Error while attempting to download transcript definition files.");
			System.err.println("[ERROR] " + e.toString());
			System.err.println("[ERROR] A common error is the failure to set the network proxy (see tutorial).");
			System.exit(1);
		}

		/*
		 * Option 2. The user must provide the ucsc.ser file to do analysis. (or the ensembl.ser or refseq.ser files).
		 * We can either annotate a VCF file (3a) or create a separate annotation file (3b).
		 */
		if (anno.deserialize()) {
			try {
				anno.deserializeTranscriptDefinitionFile();
			} catch (JannovarException je) {
				System.err.println("[ERROR] Could not deserialize UCSC data: " + je.toString());
				System.exit(1);
			}
		} else {
			System.err.println("[ERROR] You need to pass ucscs.ser file to perform analysis.");
			CommandLineParser.printUsage();
			System.exit(1);
		}
		/*
		 * When we get here, the program has deserialized data and put it into the Chromosome objects. We can now start
		 * to annotate variants.
		 */
		if (anno.hasVCFfile()) {
			try {
				anno.annotateVCF(); /* 3a or 3b */
			} catch (JannovarException je) {
				System.err.println("[ERROR] Could not annotate VCF data: " + je.toString());
				System.exit(1);
			}
		} else {
			if (anno.options.chromosomalChange == null) {
				System.err.println("[ERROR] No VCF file found and no chromosomal position and variation was found");
			} else {
				try {
					anno.annotatePosition();
				} catch (JannovarException je) {
					System.err.println("[ERROR] Could not annotate input data: " + anno.options.chromosomalChange);
					System.exit(1);
				}

			}
		}
	}

	/**
	 * The constructor parses the command-line arguments.
	 *
	 * @param argv
	 *            the arguments passed through the command
	 * @throws HelpRequestedException
	 *             if help was requested
	 * @throws ParseException
	 *             if there was a problem parsing the command line.
	 */
	public Jannovar(String argv[]) throws ParseException, HelpRequestedException {
		this.options = new CommandLineParser().parseCommandLine(argv);
	}

	/**
	 * @return true if user wants to download UCSC files
	 */
	public boolean createUCSC() {
		return options.createUCSC;
	}

	/**
	 * @return true if user wants to download refseq files
	 */
	public boolean createRefseq() {
		return options.createRefseq;
	}

	/**
	 * @return true if user wants to download ENSEMBL files
	 */
	public boolean createEnsembl() {
		return options.createEnsembl;
	}

	/**
	 * This function creates a {@link TranscriptDataDownloader} object in order to download the required transcript data
	 * files. If the user has set the proxy and proxy port via the command line, we use these to download the files.
	 *
	 * @param source
	 *            the source of the transcript data (e.g. RefSeq, Ensembl, UCSC)
	 * @param rel
	 *            the genome {@link Release}
	 */
	public void downloadTranscriptFiles(int source, Release rel) {
		TranscriptDataDownloader downloader;
		try {
			if (this.options.proxy != null && this.options.proxyPort != null) {
				downloader = new TranscriptDataDownloader(options.dirPath
						+ options.genomeRelease.getUCSCString(options.genomeRelease), this.options.proxy,
						this.options.proxyPort);
			} else {
				downloader = new TranscriptDataDownloader(options.dirPath
						+ options.genomeRelease.getUCSCString(options.genomeRelease));
			}
			downloader.downloadTranscriptFiles(source, rel);
		} catch (FileDownloadException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	/**
	 * @return true if we should serialize the UCSC data.
	 */
	public boolean serialize() {
		return this.options.performSerialization;
	}

	/**
	 * @return true if we should deserialize a file with UCSC data to perform analysis
	 */
	public boolean deserialize() {
		return this.options.serializedFile != null;
	}

	/**
	 * @return true if we should annotate a VCF file
	 */
	public boolean hasVCFfile() {
		return this.options.VCFfilePath != null;
	}

	/**
	 * Temporary helper code for converting VCF data to Jannovar representation.
	 */
	private class VariantDataCorrector {
		private String ref;
		private String alt;
		private int position;

		public VariantDataCorrector(String ref, String alt, int position) {
			this.ref = ref;
			this.alt = alt;
			this.position = position;

			correct();
		}

		private void correct() {
			int idx = 0;
			// beginning
			while (idx < ref.length() && idx < alt.length() && ref.charAt(idx) == alt.charAt(idx)) {
				idx++;
			}
			position += idx;
			ref = ref.substring(idx);
			alt = alt.substring(idx);

			// end
			int xdi = ref.length();
			int diff = ref.length() - alt.length();
			while (xdi > 0 && xdi - diff > 0 && ref.charAt(xdi - 1) == alt.charAt(xdi - 1 - diff)) {
				xdi--;
			}
			ref = xdi == 0 ? "-" : ref.substring(0, xdi);
			alt = xdi - diff == 0 ? "-" : alt.substring(0, xdi - diff);
		}

		public String getRef() {
			return ref;
		}

		public void setRef(String ref) {
			this.ref = ref;
		}

		public String getAlt() {
			return alt;
		}

		public void setAlt(String alt) {
			this.alt = alt;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
	}

	/**
	 * Annotate a single line of a VCF file, and output the line together with the new INFO fields representing the
	 * annotations.
	 *
	 * @param variantContext
	 *            an object representing the original VCF line
	 * @param out
	 *            A file handle to write to.
	 */
	private void annotateVCFLine(VariantContext variantContext, VariantContextWriter out) throws IOException,
			AnnotationException {
		// Catch the case that variantContext.getChr() is not in ChromosomeMap.identifier2chromosom. This is the case
		// for the "random" contigs etc. In this case, we simply write the record out unmodified.
		Byte boxedChr = ChromosomeMap.identifier2chromosom.get(variantContext.getChr());
		if (boxedChr == null) {
			out.add(variantContext);
			return;
		}
		byte chr = boxedChr.byteValue();

		// FIXME(mjaeger): We should care about more than just the first alternative allele.
		VariantDataCorrector corr = new VariantDataCorrector(variantContext.getReference().getBaseString(),
				variantContext.getAlternateAllele(0).getBaseString(), variantContext.getStart());
		String ref = corr.ref;
		String alt = corr.alt;
		int pos = corr.position;

		if (!(alt.contains("[") || alt.contains("]") || alt.equals("."))) { // is not break-end
			Chromosome c = chromosomeMap.get(chr);
			if (c == null) {
				String e = String.format("Could not identify chromosome \"%d\"", chr);
				throw new AnnotationException(e);
			}
			AnnotationList anno = c.getAnnotationList(pos, ref, alt);
			if (anno == null) {
				String e = String.format("No annotations found for variant %s", variantContext.toString());
				throw new AnnotationException(e);
			}
			String annotation;
			String effect;
			if (anno.isStructural()) {
				annotation = anno.getCombinedAnnotationForStructuralVariant();// String.format("%s:%s",
																				// anno.getMultipleGeneList(),anno.getCombinedAnnotationForVariantAffectingMultipleGenes());
				effect = anno.getVariantType().toString();
			} else {
				if (this.options.showAll) {
					annotation = anno.getAllTranscriptAnnotations();
					effect = anno.getAllTranscriptVariantEffects();
				} else {
					annotation = anno.getSingleTranscriptAnnotation();
					effect = anno.getVariantType().toString();
				}
			}

			/* Now add the stuff to the INFO line */
			variantContext.getCommonInfo().putAttribute("EFFECT", effect);
			variantContext.getCommonInfo().putAttribute("HGVS", annotation);
		}

		/* Write out variantContext to out. */
		out.add(variantContext);
	}

	/**
	 * Return genotype string as in VCF for the i-th individual at the position in variantContext.
	 *
	 * @param variantContext
	 *            The VariantContext to query.
	 * @param i
	 *            Index of individual.
	 * @return String with the genotype call string, e.g. "0/1" or "1|1".
	 */
	private String stringForGenotype(VariantContext variantContext, int i) {
		Genotype gt = variantContext.getGenotype(i);
		StringBuilder builder = new StringBuilder();
		for (Allele allele : gt.getAlleles()) {
			if (builder.length() > 0)
				builder.append(gt.isPhased() ? '|' : '/');
			builder.append(variantContext.getAlleleIndex(allele));
		}
		return builder.toString();
	}

	/**
	 * This function outputs a single line in Jannovar format.
	 *
	 * @param n
	 *            The current number (one for each variant in the VCF file)
	 * @param variantContext
	 *            The current variant with one or more annotations
	 * @param out
	 *            File handle to write Jannovar file.
	 */
	private void outputJannovarLine(int n, VariantContext variantContext, Writer out) throws IOException,
			AnnotationException {
		byte chr = ChromosomeMap.identifier2chromosom.get(variantContext.getChr());
		String chrStr = variantContext.getChr();
		int pos = variantContext.getStart();
		String ref = variantContext.getReference().getBaseString();
		// FIXME(mjaeger): We should care about more than just the first alternative allele.
		String alt = variantContext.getAlternateAllele(0).getBaseString();
		String gtype = stringForGenotype(variantContext, 0);
		float qual = (float) variantContext.getPhredScaledQual();
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			String e = String.format("Could not identify chromosome \"%d\"", chr);
			throw new AnnotationException(e);
		}
		AnnotationList anno = c.getAnnotationList(pos, ref, alt);
		if (anno == null) {
			String e = String.format("No annotations found for variant %s", variantContext.toString());
			throw new AnnotationException(e);
		}

		ArrayList<Annotation> lst = anno.getAnnotationList();
		for (Annotation a : lst) {
			String effect = a.getVariantTypeAsString();
			String annt = a.getVariantAnnotation();
			String sym = a.getGeneSymbol();
			String s = String.format("%d\t%s\t%s\t%s\t%s\t%d\t%s\t%s\t%s\t%.1f", n, effect, sym, annt, chrStr, pos,
					ref, alt, gtype, qual);
			out.write(s + "\n");
		}
	}

	/**
	 * This function outputs a VCF file that corresponds to the original VCF file but additionally has annotations for
	 * each variant. A new file is created with the suffix "jv.vcf";
	 */
	private void outputAnnotatedVCF(VCFFileReader parser) throws JannovarException {
		File f = new File(this.options.VCFfilePath);
		String outname = f.getName();
		if (options.outVCFfolder != null)
			outname = options.outVCFfolder + outname;
		int i = outname.lastIndexOf("vcf");
		if (i < 0) {
			i = outname.lastIndexOf("VCF");
		}
		if (i < 0) {
			outname = outname + ".jv.vcf";
		} else {
			outname = outname.substring(0, i) + "jv.vcf";
		}
		try {
			VariantContextWriterBuilder builder = new VariantContextWriterBuilder();
			builder.setReferenceDictionary(parser.getFileHeader().getSequenceDictionary());
			builder.setOutputFile(new File(outname));
			VariantContextWriter out = builder.build();

			VCFHeader header = parser.getFileHeader();

			// Extend header before writing out to file.
			VCFInfoHeaderLine effectLine = new VCFInfoHeaderLine("EFFECT", 1, VCFHeaderLineType.String,
					VCFStrings.INFO_EFFECT);
			header.addMetaDataLine(effectLine);
			VCFInfoHeaderLine hgvsLine = new VCFInfoHeaderLine("HGVS", 1, VCFHeaderLineType.String,
					VCFStrings.INFO_HGVS);
			header.addMetaDataLine(hgvsLine);
			// Write the header of the new VCF file
			out.writeHeader(header);

			/** Now write each of the variants. */
			for (VariantContext variantContext : parser) {
				try {
					annotateVCFLine(variantContext, out);
				} catch (AnnotationException e) {
					System.err.println("[WARN] Annotation error on line: " + variantContext.toString() + "\n"
							+ e.toString());
				}
			}
			out.close();
		} catch (IOException e) {
			System.err.println("[ERROR] Error writing annotated VCF file");
			System.err.println("[ERROR] " + e.toString());
			System.exit(1);
		}
		System.err.println("[INFO] Wrote annotated VCF file to \"" + outname + "\"");
	}

	/**
	 * This function writes detailed annotations to file. One annotation is written for each of the transcripts affected
	 * by a variant, and the file is a tab-separated file in "Jannovar" format.
	 *
	 * @param parser
	 *            The VCFParser that has extracted a list of variants from the VCF file.
	 */
	private void outputJannovarFormatFile(VCFFileReader parser) throws JannovarException {
		File f = new File(this.options.VCFfilePath);
		String outname = f.getName() + ".jannovar";

		// Error handling can be improved with Java 7.
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(outname);
			out = new BufferedWriter(fstream);
			/** Output each of the variants. */
			int n = 0;
			for (VariantContext variantContext : parser) {
				n++;
				try {
					outputJannovarLine(n, variantContext, out);
				} catch (AnnotationException e) {
					System.err.println("[WARN] Annotation error: " + e.toString());
				}
			}
			out.close();
		} catch (IOException e) {
			try {
				if (out != null)
					out.close();
			} catch (IOException e2) {
				// swallow, nothing we can do about it
			}
			System.err.println("[ERROR] Error writing annotated VCF file");
			System.err.println("[ERROR] " + e.toString());
			System.exit(1);
		}
		System.err.println("[INFO] Wrote annotations to \"" + outname + "\"");
	}

	/**
	 * THis function will simply annotate given chromosomal position with HGVS compliant output e.g. chr1:909238G>C -->
	 * PLEKHN1:NM_032129.2:c.1460G>C,p.(Arg487Pro)
	 *
	 * @throws AnnotationException
	 */
	private void annotatePosition() throws AnnotationException {
		System.err.println("input: " + options.chromosomalChange);
		Pattern pat = Pattern.compile("(chr[0-9MXY]+):([0-9]+)([ACGTN])>([ACGTN])");
		Matcher mat = pat.matcher(options.chromosomalChange);

		if (!mat.matches() | mat.groupCount() != 4) {
			System.err
					.println("[ERROR] Input string for the chromosomal change does not fit the regular expression ... :(");
			System.exit(3);
		}

		byte chr = ChromosomeMap.identifier2chromosom.get(mat.group(1));
		int pos = Integer.parseInt(mat.group(2));
		String ref = mat.group(3);
		String alt = mat.group(4);

		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			String e = String.format("Could not identify chromosome \"%d\"", chr);
			throw new AnnotationException(e);
		}
		AnnotationList anno = c.getAnnotationList(pos, ref, alt);
		if (anno == null) {
			String e = String.format("No annotations found for variant %s", options.chromosomalChange);
			throw new AnnotationException(e);
		}
		String annotation;
		String effect;
		if (options.showAll) {
			annotation = anno.getAllTranscriptAnnotations();
			effect = anno.getAllTranscriptVariantEffects();
		} else {
			annotation = anno.getSingleTranscriptAnnotation();
			effect = anno.getVariantType().toString();
		}

		System.out.println(String.format("EFFECT=%s;HGVS=%s", effect, annotation));

	}

	/**
	 * This function inputs a VCF file, and prints the annotated version thereof to a file (name of the original file
	 * with the suffix .jannovar).
	 *
	 * @throws jannovar.exception.JannovarException
	 */
	public void annotateVCF() throws JannovarException {
		// VCFReader parser = new VCFReader(this.VCFfilePath);
		// VCFLine.setStoreVCFLines();
		// try {
		// parser.inputVCFheader();
		// } catch (VCFParseException e) {
		// System.err.println("[ERROR] Unable to parse VCF file");
		// System.err.println(e.toString());
		// System.exit(1);
		// }
		VCFFileReader parser = new VCFFileReader(new File(this.options.VCFfilePath), false);
		if (this.options.jannovarFormat) {
			outputJannovarFormatFile(parser);
		} else {
			outputAnnotatedVCF(parser);
		}
	}

	/**
	 * Inputs the GFF data from RefSeq files, convert the resulting {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} objects to {@link jannovar.interval.Interval Interval} objects, and store these in a serialized
	 * file.
	 *
	 * @throws JannovarException
	 */
	public void serializeRefseqData() throws JannovarException {
		SerializationManager manager = new SerializationManager();
		String combiStringRelease = options.onlyCuratedRefSeq ? "cur_"
				+ options.genomeRelease.getUCSCString(options.genomeRelease) : options.genomeRelease
				.getUCSCString(options.genomeRelease);
		System.err.println("[INFO] Serializing RefSeq data as "
				+ String.format(options.dirPath + Jannovar.RefseqSerializationFileName, combiStringRelease));
		manager.serializeKnownGeneList(
				String.format(options.dirPath + Jannovar.RefseqSerializationFileName, combiStringRelease),
				this.transcriptModelList);
	}

	/**
	 * Inputs the GFF data from Ensembl files, convert the resulting {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} objects to {@link jannovar.interval.Interval Interval} objects, and store these in a serialized
	 * file.
	 *
	 * @throws jannovar.exception.JannovarException
	 */
	public void serializeEnsemblData() throws JannovarException {
		SerializationManager manager = new SerializationManager();
		System.err.println("[INFO] Serializing Ensembl data as "
				+ String.format(options.dirPath + Jannovar.EnsemblSerializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)));
		manager.serializeKnownGeneList(
				String.format(options.dirPath + Jannovar.EnsemblSerializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)), this.transcriptModelList);
	}

	/**
	 * Inputs the KnownGenes data from UCSC files, convert the resulting {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} objects to {@link jannovar.interval.Interval Interval} objects, and store these in a serialized
	 * file.
	 *
	 * @throws jannovar.exception.JannovarException
	 */
	public void serializeUCSCdata() throws JannovarException {
		SerializationManager manager = new SerializationManager();
		System.err.println("[INFO] Serializing UCSC data as "
				+ String.format(options.dirPath + Jannovar.UCSCserializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)));
		manager.serializeKnownGeneList(
				String.format(options.dirPath + Jannovar.UCSCserializationFileName,
						options.genomeRelease.getUCSCString(options.genomeRelease)),
				this.transcriptModelList);
	}

	/**
	 * To run Jannovar, the user must pass a transcript definition file with the -D flag. This can be one of the files
	 * ucsc.ser, ensembl.ser, or refseq.ser (or a comparable file) containing a serialized version of the
	 * TranscriptModel objects created to contain info about the transcript definitions (exon positions etc.) extracted
	 * from UCSC, Ensembl, or Refseq and necessary for annotation.
	 *
	 * @throws JannovarException
	 */
	public void deserializeTranscriptDefinitionFile() throws JannovarException {
		ArrayList<TranscriptModel> kgList;
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(this.options.serializedFile);
		this.chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
	}

	/**
	 * Input the RefSeq data.
	 */
	private void inputTranscriptModelDataFromRefseq() {
		// parse GFF/GTF
		GFFparser gff = new GFFparser();
		String path = options.dirPath + options.genomeRelease.getUCSCString(options.genomeRelease);
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		switch (this.options.genomeRelease) {
		case MM9:
			gff.parse(path + Constants.refseq_gff_mm9);
			break;
		case MM10:
			gff.parse(path + Constants.refseq_gff_mm10);
			break;
		case HG18:
			gff.parse(path + Constants.refseq_gff_hg18);
			break;
		case HG19:
			gff.parse(path + Constants.refseq_gff_hg19);
			break;
		case HG38:
			gff.parse(path + Constants.refseq_gff_hg38);
			break;
		default:
			System.err.println("[ERROR] Unknown release: " + options.genomeRelease);
			System.exit(20);
			break;
		}
		try {
			this.transcriptModelList = gff.getTranscriptModelBuilder().buildTranscriptModels(options.onlyCuratedRefSeq);
		} catch (InvalidAttributException e) {
			System.err.println("[ERROR] Unable to input data from the Refseq files");
			e.printStackTrace();
			System.exit(1);
		}
		// add sequences
		FastaParser efp = new RefSeqFastaParser(path + Constants.refseq_rna, transcriptModelList);
		int before = transcriptModelList.size();
		transcriptModelList = efp.parse();
		int after = transcriptModelList.size();
		// System.out.println(String.format("[INFO] removed %d (%d --> %d) transcript models w/o rna sequence",
		// before-after,before, after));
		if (options.onlyCuratedRefSeq)
			System.err.println(String.format(
					"[INFO] Found %d curated transcript models from Refseq GFF resource, %d of which had sequences",
					before, after));
		else
			System.err.println(String.format(
					"[INFO] Found %d transcript models from Refseq GFF resource, %d of which had sequences", before,
					after));
	}

	/**
	 * Input the Ensembl data.
	 */
	private void inputTranscriptModelDataFromEnsembl() {
		// parse GFF/GTF

		GFFparser gff = new GFFparser();
		String path;
		path = options.dirPath + options.genomeRelease.getUCSCString(options.genomeRelease);
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		switch (this.options.genomeRelease) {
		case MM9:
			path += Constants.ensembl_mm9;
			break;
		case MM10:
			path += Constants.ensembl_mm10;
			break;
		case HG18:
			path += Constants.ensembl_hg18;
			break;
		case HG19:
			path += Constants.ensembl_hg19;
			break;
		default:
			System.err.println("[ERROR] Unknown release: " + options.genomeRelease);
			System.exit(20);
			break;
		}
		gff.parse(path + Constants.ensembl_gtf);
		try {
			this.transcriptModelList = gff.getTranscriptModelBuilder().buildTranscriptModels();
			// System.out.println("[INFO] Got: "+this.transcriptModelList.size()
			// + " Ensembl transcripts");
		} catch (InvalidAttributException e) {
			System.err.println("[ERROR] Unable to input data from the Ensembl files");
			e.printStackTrace();
			System.exit(1);
		}
		// add sequences
		EnsemblFastaParser efp = new EnsemblFastaParser(path + Constants.ensembl_cdna, transcriptModelList);
		int before = transcriptModelList.size();
		transcriptModelList = efp.parse();
		int after = transcriptModelList.size();
		// System.out.println(String.format("[INFO] removed %d (%d --> %d) transcript models w/o rna sequence",
		// before-after,before, after));

		System.err.println(String
				.format("[INFO] Found %d transcript models from Ensembl GFF resource, %d of which had sequences",
						before, after));
	}

	/**
	 * Input the four UCSC files for the KnownGene data.
	 */
	private void inputTranscriptModelDataFromUCSCFiles() {
		String path = options.dirPath + options.genomeRelease.getUCSCString(options.genomeRelease);
		if (!path.endsWith(System.getProperty("file.separator")))
			path += System.getProperty("file.separator");
		UCSCKGParser parser = new UCSCKGParser(path);
		try {
			parser.parseUCSCFiles();
		} catch (Exception e) {
			System.err.println("[ERROR] Unable to input data from the UCSC files");
			e.printStackTrace();
			System.exit(1);
		}
		this.transcriptModelList = parser.getKnownGeneList();
	}

	/**
	 * A simple printout of the chromosome map for debugging purposes.
	 */
	public void debugShowChromosomeMap() {
		for (Byte c : chromosomeMap.keySet()) {
			Chromosome chromo = chromosomeMap.get(c);
			System.out.println("Chrom. " + c + ": " + chromo.getNumberOfGenes() + " genes");
		}
	}

}
