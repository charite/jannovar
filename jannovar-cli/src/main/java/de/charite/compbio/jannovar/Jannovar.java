package de.charite.compbio.jannovar;

import java.io.PrintWriter;

import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.cmd.annotate_pos.AnnotatePositionCommand;
import de.charite.compbio.jannovar.cmd.annotate_vcf.AnnotateVCFCommand;
import de.charite.compbio.jannovar.cmd.db_list.DatabaseListCommand;
import de.charite.compbio.jannovar.cmd.download.DownloadCommand;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * The Jannovar main class is the gateway into the Jannovar CLI application
 * 
 * Call Jannovar with `--help` to see details on usage
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public final class Jannovar {

	/** Configuration for the Jannovar program. */
	JannovarOptions options = null;

	public static void main(String argv[]) {
		ArgumentParser parser = buildTopLevelParser();
		try {
			parser.parseArgs(argv);
		} catch (ArgumentParserException e) {
			parser.printHelp(new PrintWriter(System.err, true));
			System.exit(1);
		}

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
		System.err.println(
				"         java -jar de.charite.compbio.jannovar.jar annotate -d data/hg19_ucsc.ser -i variants.vcf");
		System.err.println(
				"         java -jar de.charite.compbio.jannovar.jar annotate-pos -d data/hg19_ucsc.ser -c 'chr1:12345C>A'");
		System.err.println("");
	}

	private static ArgumentParser buildTopLevelParser() {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("jannovar");

		parser.addArgument("--verbose").action(Arguments.storeTrue()).help("Enable verbose logging");
		parser.addArgument("--very-verbose").action(Arguments.storeTrue()).help("Enable very verbose logging");
		
		// Register sub commands with ArgumentParser
		Subparsers subparsers = parser.addSubparsers();
		DownloadCommand.addSubparser(subparsers);
		DatabaseListCommand.addSubparser(subparsers);
		AnnotatePositionCommand.addSubparser(subparsers);
		AnnotateVCFCommand.addSubparser(subparsers);

		return parser;
	}

}
