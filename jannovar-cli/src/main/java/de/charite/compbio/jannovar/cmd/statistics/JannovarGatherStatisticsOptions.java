package de.charite.compbio.jannovar.cmd.statistics;

import java.util.function.BiFunction;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Configuration for the <code>statistics</code> command
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarGatherStatisticsOptions extends JannovarBaseOptions {

	/** Path to database file */
	private String databaseFilePath = null;

	/** Path to input VCF file */
	private String pathInputVCF = null;

	/** Path to output report TXT file */
	private String pathOutputReport = null;

	/**
	 * Setup {@link ArgumentParser}
	 * 
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, GatherStatisticsCommand> handler = (argv, args) -> {
			try {
				return new GatherStatisticsCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("statistics", true).help("compute statistics about VCF file")
				.setDefault("cmd", handler);
		subParser.description("Compute statistics about variants in VCF file");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("-i", "--input-vcf").help("Path to input VCF file").required(true);
		requiredGroup.addArgument("-o", "--output-report").help("Path to output report TXT file").required(true);
		requiredGroup.addArgument("-d", "--database").help("Path to database .ser file").required(true);

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		pathInputVCF = args.getString("input_vcf");
		pathOutputReport = args.getString("output_report");
		databaseFilePath = args.getString("database");
	}

	public String getPathInputVCF() {
		return pathInputVCF;
	}

	public void setPathInputVCF(String pathInputVCF) {
		this.pathInputVCF = pathInputVCF;
	}

	public String getPathOutputReport() {
		return pathOutputReport;
	}

	public void setPathOutputReport(String pathOutputReport) {
		this.pathOutputReport = pathOutputReport;
	}

	public String getDatabaseFilePath() {
		return databaseFilePath;
	}

	public void setDatabaseFilePath(String databaseFilePath) {
		this.databaseFilePath = databaseFilePath;
	}

	@Override
	public String toString() {
		return "JannovarGatherStatisticsOptions [databaseFilePath=" + databaseFilePath + ", pathInputVCF="
				+ pathInputVCF + ", pathOutputReport=" + pathOutputReport + "]";
	}

}
