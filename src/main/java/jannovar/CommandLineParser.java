package jannovar;

import jannovar.common.Constants;
import jannovar.common.Constants.Release;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

/**
 * Helper class for parsing the command line.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter Robinson <peter.robinson@charite.de>
 */
public class CommandLineParser {

	public class HelpRequestedException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	/** The Options object. */
	private Options options = null;

	/** The Parser object to use for parsing. */
	private Parser parser = null;

	/**
	 * Construct a new parser with appropriate org.apache.commons.cli parser.
	 */
	public CommandLineParser() {
		initializeParser();
	}

	/**
	 * Initialize the command line parser in result.
	 */
	private void initializeParser() {
		options = new Options();
		options.addOption(new Option("h", "help", false, "Shows this help"));
		options.addOption(new Option("U", "downloaded-data", true,
				"Path to directory with previously downloaded transcript definition files."));
		options.addOption(new Option("S", "serialize", false, "Serialize"));
		options.addOption(new Option("D", "deserialize", true, "Path to serialized file with UCSC data"));
		options.addOption(new Option("d", "data", true,
				"Path to write data storage folder (genome files, serialized files, ...)"));
		options.addOption(new Option("O", "output", true, "Path to output folder for the annotated VCF file"));
		options.addOption(new Option("V", "vcf", true, "Path to VCF file"));
		options.addOption(new Option("a", "showall", false, "report annotations for all transcripts to VCF file"));
		options.addOption(new Option("P", "position", true, "chromosomal position to HGVS (e.g. chr1:909238G>C)"));
		options.addOption(new Option("J", "janno", false, "Output Jannovar format"));
		options.addOption(new Option("g", "genome", true,
				"genome build (mm9, mm10, hg18, hg19, hg38 - only refseq), default hg19"));
		options.addOption(new Option(null, "create-ucsc", false, "Create UCSC definition file"));
		options.addOption(new Option(null, "create-refseq", false, "Create RefSeq definition file"));
		options.addOption(new Option(null, "create-refseq-c", false,
				"Create RefSeq definition file w/o predicted transcripts"));
		options.addOption(new Option(null, "create-ensembl", false, "Create Ensembl definition file"));
		options.addOption(new Option(null, "proxy", true, "FTP Proxy"));
		options.addOption(new Option(null, "proxy-port", true, "FTP Proxy Port"));

		parser = new GnuParser();
	}

	/**
	 * Print usage to stderr.
	 */
	public static void printUsage() {
		System.err.println("***   Jannovar: Usage     ****");
		System.err.println("Use case 1: Download UCSC data and create transcript data file (ucsc_hg19.ser)");
		System.err.println("$ java -jar Jannovar.jar --create-ucsc [-U name of output directory]");
		System.err.println("Use case 2: Add annotations to a VCF file");
		System.err.println("$ java -jar Jannovar.jar -D ucsc_hg19.ser -V example.vcf");
		System.err.println("Use case 3: Write new file with Jannovar-format annotations of a VCF file");
		System.err.println("$ java -jar Jannovar -D ucsc_hg19.ser -V vcfPath -J");
		System.err.println("*** See the tutorial for details ***");
	}

	/**
	 * Parse command line arguments in args and populate JannovarOptions object.
	 *
	 * @param args
	 *            command line arguments to parse
	 * @return populated JannovarOptions object.
	 *
	 * @throws ParseException
	 *             if there are problems parsing the command line (program should return != 0)
	 * @throws HelpRequestedException
	 *             if help was requested and printed (program should return == 0)
	 */
	public JannovarOptions parseCommandLine(String[] args) throws ParseException, HelpRequestedException {
		CommandLine cmd = parser.parse(options, args);
		if (cmd.hasOption("h") || cmd.hasOption("H") || args.length == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar Jannovar.jar [-options]", options);
			printUsage();
			throw new HelpRequestedException();
		}

		JannovarOptions result = new JannovarOptions(); // initialize result

		result.jannovarFormat = cmd.hasOption("J");
		result.showAll = cmd.hasOption('a');
		if (cmd.hasOption("create-ucsc")) {
			result.createUCSC = true;
			result.performSerialization = true;
		}

		if (cmd.hasOption("create-refseq") || cmd.hasOption("create-refseq-c")) {
			result.createRefseq = true;
			result.performSerialization = true;
			if (cmd.hasOption("create-refseq-c"))
				result.onlyCuratedRefSeq = true;
		}

		if (cmd.hasOption("create-ensembl")) {
			result.createEnsembl = true;
			result.performSerialization = true;
		}

		// path to the data storage
		if (cmd.hasOption('d'))
			result.dirPath = cmd.getOptionValue('d');
		else
			result.dirPath = Constants.DEFAULT_DATA;
		if (!result.dirPath.endsWith(System.getProperty("file.separator")))
			result.dirPath += System.getProperty("file.separator");

		if (cmd.hasOption("genome")) {
			String g = cmd.getOptionValue("genome");
			if (g.equals("mm9")) {
				result.genomeRelease = Release.MM9;
			}
			if (g.equals("mm10")) {
				result.genomeRelease = Release.MM10;
			}
			if (g.equals("hg18")) {
				result.genomeRelease = Release.HG18;
			}
			if (g.equals("hg19")) {
				result.genomeRelease = Release.HG19;
			}
			if (g.equals("hg38")) {
				if (result.createRefseq)
					result.genomeRelease = Release.HG38;
				else {
					System.err.println("[INFO] Genome release hg38 only available for Refseq");
					System.exit(0);
				}
			}
		} else {
			if (result.performSerialization)
				System.err.println("[INFO] Genome release set to default: hg19");
			result.genomeRelease = Release.HG19;
		}

		if (cmd.hasOption('O')) {
			result.outVCFfolder = cmd.getOptionValue('O');
			File file = new File(result.outVCFfolder);
			if (!file.exists())
				file.mkdirs();
		}

		if (cmd.hasOption('S'))
			result.performSerialization = true;

		if (cmd.hasOption("proxy"))
			result.proxy = cmd.getOptionValue("proxy");

		if (cmd.hasOption("proxy-port"))
			result.proxyPort = cmd.getOptionValue("proxy-port");

		if (cmd.hasOption("U"))
			result.dirPath = getRequiredOptionValue(cmd, 'U');

		if (cmd.hasOption('D'))
			result.serializedFile = cmd.getOptionValue('D');

		if (cmd.hasOption("V"))
			result.VCFfilePath = cmd.getOptionValue("V");

		if (cmd.hasOption("P"))
			result.chromosomalChange = cmd.getOptionValue("P");

		// Make sure that result.dirPath and result.outVCFfolder end in a directory separator/slash.
		if (!result.dirPath.endsWith(System.getProperty("file.separator")))
			result.dirPath += System.getProperty("file.separator");
		if (result.outVCFfolder != null && !result.outVCFfolder.endsWith(System.getProperty("file.separator")))
			result.outVCFfolder += System.getProperty("file.separator");

		return result;
	}

	/**
	 * This function is used to ensure that certain options are passed to the program before we start execution.
	 *
	 * @param cmd
	 *            An apache CommandLine object that stores the command line arguments
	 * @param name
	 *            Name of the argument that must be present
	 * @return Value of the required option as a String.
	 * @throws ParseException
	 *             if the option could not be retrieved.
	 */
	private static String getRequiredOptionValue(CommandLine cmd, char name) throws ParseException {
		String val = cmd.getOptionValue(name);
		if (val == null)
			throw new ParseException("Aborting because the required argument \"-" + name
					+ "\" wasn't specified! Use the -h for more help.");
		return val;
	}
}
