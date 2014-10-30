package jannovar;

/** Command line functions from apache */
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import jannovar.CommandLineParser.HelpRequestedException;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantAnnotator;
import jannovar.common.ChromosomeMap;
import jannovar.exception.AnnotationException;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

import java.io.File;
import java.io.IOException;
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
 * @author mjaeger
 * @version 0.33 (29 December, 2013)
 */
public class Jannovar {
	/** Map of Chromosomes, used in the annotation. */
	private HashMap<Byte, Chromosome> chromosomeMap = null;

	/** Configuration for the Jannovar program. */
	JannovarOptions options = null;

	public static void main(String argv[]) {
		// Create Jannovar object, this includes parsing the command line.
		Jannovar anno = null;
		try {
			anno = new Jannovar(argv);
		} catch (ParseException e1) {
			System.exit(1); // something went wrong, return 1
		} catch (HelpRequestedException e1) {
			return;  // help requested and printed, return 0
		}

		// Option 1: download transcript files and serialize
		try {
			if (anno.download())
				return;  // stop after downloading
		} catch (FileDownloadException e) {
			System.err.println("[ERROR] Error while attempting to parse transcript definition files.");
			System.err.println("[ERROR] " + e.toString());
			System.err.println("[ERROR] A common error is the failure to set the network proxy (see tutorial).");
			System.exit(1);
		} catch (JannovarException e) {
			System.err.println("[ERROR] Error while attempting to parse transcript definition files.");
			System.err.println("[ERROR] " + e.toString());
			System.err.println("[ERROR] A common error is the failure to set the network proxy (see tutorial).");
			System.exit(1);
		}

		// Option 2/3, Step 1: deserialize data, if any, required for annotation of VCF or creating annotation file
		try {
			if (!anno.deserialize())
			{
				System.err.println("[INFO] You need to pass a file to deserialize for annotation.");
				System.exit(1);
			}
		} catch (JannovarException je) {
			System.err.println("[ERROR] Could not deserialize UCSC data: " + je.toString());
			System.exit(1);
		}

		// Option 2, Step 2: perform VCF annotation or create annotation file
		if (anno.options.hasVCFfile()) {
			try {
				anno.annotateVCF(); // annotate VCF or create Jannovar output file
				return;
			} catch (JannovarException je) {
				System.err.println("[ERROR] Could not annotate VCF data: " + je.toString());
				System.exit(1);
			}
		}

		// Option 3, Step 2: output chromosomal change at the given position
		if (anno.options.chromosomalChange == null) {
			System.err.println("[ERROR] No VCF file found and no chromosomal position and variation was found");
			System.exit(1);
		}
		try {
			anno.annotatePosition();
		} catch (JannovarException je) {
			System.err.println("[ERROR] Could not annotate input data: " + anno.options.chromosomalChange);
			System.exit(1);
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
	 * Perform downloading and serialization of transcript data.
	 *
	 * @return true if the user instructed us to download data, the program hast to stop afterwards
	 *
	 * @throws JannovarException
	 *             on problems with parsing or serialization
	 * @throws FileDownloadException
	 *             on problems with file download
	 */
	boolean download() throws FileDownloadException, JannovarException {
		DownloadManager manager = DownloadManagerFactory.build(options);
		if (manager == null)
			return false;
		manager.run();
		return true;
	}

	/**
	 * Perform deserialization of transcript data.
	 *
	 * @return true if the user gave a path to serialized data that was then serialized
	 * @throws JannovarException
	 *             on deserialization problems
	 */
	boolean deserialize() throws JannovarException {
		if (!options.deserialize())
			return false;
		deserializeTranscriptDefinitionFile();
		return true;
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

		VariantAnnotator annotator = new VariantAnnotator(chromosomeMap);
		AnnotationList anno = annotator.getAnnotationList(chr, pos, ref, alt);
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
		// initialize the VCF reader
		VCFFileReader parser = new VCFFileReader(new File(this.options.VCFfilePath), false);

		AnnotatedVariantWriter writer = null;
		try {
			// construct the variant writer
			if (this.options.jannovarFormat)
				writer = new AnnotatedJannovarWriter(chromosomeMap, options);
			else
				writer = new AnnotatedVCFWriter(parser, chromosomeMap, options);

			// annotate and write out all variants
			for (VariantContext vc : parser)
				writer.put(vc);

			// close parser writer again
			parser.close();
			writer.close();
		} catch (IOException e) {
			// convert exception to JannovarException and throw, writer can only be null here
			parser.close();
			throw new JannovarException(e.getMessage());
		}

		// TODO(holtgrem): use logger
		System.err.println("[INFO] Wrote annotations to \"" + writer.getOutFileName() + "\"");
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
	 * A simple printout of the chromosome map for debugging purposes.
	 */
	public void debugShowChromosomeMap() {
		for (Byte c : chromosomeMap.keySet()) {
			Chromosome chromo = chromosomeMap.get(c);
			System.out.println("Chrom. " + c + ": " + chromo.getNumberOfGenes() + " genes");
		}
	}

}
