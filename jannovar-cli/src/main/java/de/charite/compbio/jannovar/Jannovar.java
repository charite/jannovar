package de.charite.compbio.jannovar;

/** Command line functions from apache */
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.cmd.annotate_pos.AnnotatePositionCommand;
import de.charite.compbio.jannovar.cmd.annotate_vcf.AnnotateVCFCommand;
import de.charite.compbio.jannovar.cmd.db_list.DatabaseListCommand;
import de.charite.compbio.jannovar.cmd.download.DownloadCommand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * This is the driver class for a program called Jannovar. It has two purposes
 * <OL>
 * <LI>Take the UCSC files knownGene.txt, kgXref.txt, knownGeneMrna.txt, and knownToLocusLink.txt, and to create
 * corresponding {@link TranscriptModel} objects and to serialize them. The resulting serialized file can be used both
 * by this program itself (see next item) or by the main Exomizer program to annotated VCF file.
 * <LI>Using the serialized file of {@link TranscriptModel} objects (see above item) annotate a VCF file using
 * annovar-type program logic. Note that this functionality is also used by the main Exomizer program and thus this
 * program can be used as a stand-alone annotator ("Jannovar") or as a way of testing the code for the Exomizer.
 * </OL>
 * <P>
 * To run the "Jannovar" executable:
 * <P>
 * {@code java -Xms1G -Xmx1G -jar Jannovar.jar -V xyz.vcf -D $SERIAL}
 * <P>
 * This will annotate a VCF file. The results of de.charite.compbio.jannovar annotation are shown in the form
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
 * $ perl convert2annovar.pl BLA.vcf -format vcf4 &gt; BLA.av
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
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public final class Jannovar {
	/** Configuration for the Jannovar program. */
	JannovarOptions options = null;

	public static void main(String argv[]) {
		if (argv.length == 0) {
			// No arguments, print top level help and exit.
			printTopLevelHelp();
			System.exit(1);
		}
		
		String[] newArgs = new String[argv.length - 1];
		for (int i = 0; i < newArgs.length; i++) {
			newArgs[i] = argv[i + 1];
		}

		// Create the corresponding command.
		JannovarCommand cmd = null;
		try {
			if (argv[0].equals("download")) {
				cmd = new DownloadCommand(newArgs);
			} else if (argv[0].equals("db-list")) {
				cmd = new DatabaseListCommand(newArgs);
			} else if (argv[0].equals("annotate")) {
				cmd = new AnnotateVCFCommand(newArgs);
			} else if (argv[0].equals("annotate-pos")) {
				cmd = new AnnotatePositionCommand(newArgs);
			} else {
				System.err.println("unrecognized command " + argv[0]);
				printTopLevelHelp();
			}
		} catch (CommandLineParsingException e) {
			System.err.println("ERROR: problem with parsing command line options: " + e.getMessage());
			System.err.println("");
			System.err.println("Use --help for obtaining usage instructions.");
		} catch (HelpRequestedException e) {
			return; // no error, user wanted help
		}

		// Stop if no command could be created.
		if (cmd == null)
			System.exit(1);

		// Execute the command.
		try {
			cmd.run();
		} catch (JannovarException e) {
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Print top level help (without any command).
	 */
	private static void printTopLevelHelp() {
		System.err.println("Program: de.charite.compbio.jannovar (functional annotation of VCF files)");
		System.err.println("Version: " + JannovarOptions.JANNOVAR_VERSION);
		System.err.println("Contact: Peter N Robinson <peter.robinson@charite.de>");
		System.err.println("");
		System.err.println("Usage: java -jar de.charite.compbio.jannovar.jar <command> [options]");
		System.err.println("");
		System.err.println("Command: download      download transcript database");
		System.err.println("         db-list       list downloadable databases");
		System.err.println("         annotate      functional annotation of VCF files");
		System.err.println("         annotate-pos  functional annotation of genomic change");
		System.err.println("");
		System.err.println("Example: java -jar de.charite.compbio.jannovar.jar download -d hg19/ucsc");
		System.err.println("         java -jar de.charite.compbio.jannovar.jar db-list");
		System.err
				.println("         java -jar de.charite.compbio.jannovar.jar annotate -d data/hg19_ucsc.ser -i variants.vcf");
		System.err
				.println("         java -jar de.charite.compbio.jannovar.jar annotate-pos -d data/hg19_ucsc.ser -c 'chr1:12345C>A'");
		System.err.println("");
	}

}
