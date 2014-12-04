package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.common.Constants.Release;
import jannovar.exception.HelpRequestedException;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import com.google.common.net.HostAndPort;

/**
 * Helper class for parsing the commandline of the download command.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class DownloadCommandLineParser {

	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected Parser parser;

	/**
	 * Calls initializeParser().
	 */
	public DownloadCommandLineParser() {
		initializeParser();
	}

	/**
	 * Initialize {@link #parser} and {@link #options}.
	 */
	private void initializeParser() {
		options = new Options();
		options.addOption(new Option("h", "help", false, "show this help"));
		options.addOption(new Option("d", "data-dir", true,
				"target folder for downloaded and serialized files, defaults to \"data\""));
		options.addOption(new Option("g", "genome", true,
				"genome build, default is hg19, one of {mm9, mm10, hg18, hg19, hg38 (only refseq)}"));
		options.addOption(new Option(null, "proxy", true, "proxy to use for download as <HOST>:<PORT>"));

		parser = new GnuParser();
	}

	/**
	 * Parse the command line and
	 *
	 * @throws ParseException
	 *             on problems with the command line
	 */
	public JannovarOptions parse(String argv[]) throws ParseException, HelpRequestedException {
		// Parse the command line.
		CommandLine cmd = parser.parse(options, argv);

		// Fill the resulting JannovarOptions.
		JannovarOptions result = new JannovarOptions();

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("data-dir"))
			result.downloadPath = cmd.getOptionValue("data-dir");

		// TODO(holtgrem): improve the following if/then/else's by looping over enums

		String args[] = cmd.getArgs(); // get remaining arguments
		if (args.length != 2)
			throw new ParseException("must have one none-option argument, had: " + (args.length - 1));
		if (args[1].equals("ensembl"))
			result.dataSource = JannovarOptions.DataSource.ENSEMBL;
		else if (args[1].equals("refseq"))
			result.dataSource = JannovarOptions.DataSource.REFSEQ;
		else if (args[1].equals("refseq_curated"))
			result.dataSource = JannovarOptions.DataSource.REFSEQ_CURATED;
		else if (args[1].equals("ucsc"))
			result.dataSource = JannovarOptions.DataSource.UCSC;
		else
			throw new ParseException("invalid data source name: " + args[1]);

		if (cmd.hasOption("genome")) {
			String g = cmd.getOptionValue("genome");
			if (g.equals("mm9"))
				result.genomeRelease = Release.MM9;
			else if (g.equals("mm10"))
				result.genomeRelease = Release.MM10;
			else if (g.equals("hg18"))
				result.genomeRelease = Release.HG18;
			else if (g.equals("hg19"))
				result.genomeRelease = Release.HG19;
			else if (g.equals("hg38"))
				result.genomeRelease = Release.HG38;
			else
				throw new ParseException("invalid genome name " + g);

			if (result.genomeRelease == Release.HG38 && result.dataSource != JannovarOptions.DataSource.ENSEMBL)
				throw new ParseException("genome release hg38 is only available for RefSeq");
		}

		try {
			if (cmd.hasOption("proxy"))
				result.proxy = HostAndPort.fromString(cmd.getOptionValue("proxy")).withDefaultPort(8080);
		} catch (IllegalArgumentException e) {
			throw new ParseException("could not parse proxy from " + cmd.getOptionValue("proxy"));
		}

		return result;
	}

	private void printHelp() {
		final String HEADER = new StringBuilder().append("Jannovar Command: download\n\n")
				.append("Use this command to download a transcript database and build a serialization file \n")
				.append("of it. This file can then be later loaded by the annotation commands.\n\n")
				.append("Usage: java -jar jannovar.jar download [options] <database>\n\n")
				.append("The following values of <download> are supported: refseq, refseq_curated, ensembl, \n")
				.append("and ucsc.\n\n").toString();

		final String FOOTER = new StringBuilder().append("\n\nExample: java -jar jannovar.jar download ucsc\n\n")
				.toString();

		System.err.print(HEADER);

		HelpFormatter hf = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.err, true);
		hf.printOptions(pw, 78, options, 2, 2);

		System.err.print(FOOTER);
	}
}
