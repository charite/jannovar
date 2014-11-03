package jannovar;

/** Command line functions from apache */
import jannovar.CommandLineParser.HelpRequestedException;
import jannovar.cmd.JannovarCommand;
import jannovar.cmd.annotate_pos.AnnotatePositionCommand;
import jannovar.cmd.annotate_vcf.AnnotateVCFCommand;
import jannovar.cmd.download.DownloadCommand;
import jannovar.exception.FileDownloadException;
import jannovar.exception.JannovarException;

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

		// Option 2: perform VCF annotation or create annotation file
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
		if (!options.createUCSC && !options.createEnsembl && !options.createRefseq)
			return false;
		JannovarCommand cmd = new DownloadCommand(options);
		cmd.run();
		return true;
	}

	/**
	 * Perform annotation of a VCF file.
	 */
	void annotateVCF() throws JannovarException {
		JannovarCommand cmd = new AnnotateVCFCommand(options);
		cmd.run();
	}

	/**
	 * Perform annotation of one position.
	 */
	void annotatePosition() throws JannovarException {
		JannovarCommand cmd = new AnnotatePositionCommand(options);
		cmd.run();
	}
}
