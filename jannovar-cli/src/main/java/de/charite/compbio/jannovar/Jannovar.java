package de.charite.compbio.jannovar;

import java.util.function.BiFunction;

import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.cmd.annotate_pos.JannovarAnnotatePosOptions;
import de.charite.compbio.jannovar.cmd.annotate_vcf.JannovarAnnotateVCFOptions;
import de.charite.compbio.jannovar.cmd.db_list.JannovarDBListOptions;
import de.charite.compbio.jannovar.cmd.download.JannovarDownloadOptions;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * This is the driver class for a program called Jannovar.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class Jannovar {

	/** the version of the program */
	public static final String JANNOVAR_VERSION = "0.17";

	public static void main(String argv[]) {
		// Setup command line parser
		ArgumentParser parser = ArgumentParsers.newArgumentParser("jannovar-cli");
		parser.version(JANNOVAR_VERSION);
		parser.description("Jannovar CLI performs a series of VCF annotation tasks, including predicted "
				+ "molecular impact of variants and annotation of compatible Mendelian inheritance.");
		Subparsers subParsers = parser.addSubparsers();
		JannovarAnnotatePosOptions.setupParser(subParsers);
		JannovarAnnotateVCFOptions.setupParser(subParsers);
		JannovarDBListOptions.setupParser(subParsers);
		JannovarDownloadOptions.setupParser(subParsers);
		parser.defaultHelp(true);
		parser.epilog("You can find out more at http://jannovar.rtfd.org");

		// Parse command line arguments
		Namespace args = null;
		try {
			args = parser.parseArgs(argv);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}

		BiFunction<String[], Namespace, JannovarCommand> factory = args.get("cmd");
		JannovarCommand cmd = factory.apply(argv, args);
		if (cmd == null)
			System.exit(1);
		try {
			cmd.run();
		} catch (JannovarException e) {
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
