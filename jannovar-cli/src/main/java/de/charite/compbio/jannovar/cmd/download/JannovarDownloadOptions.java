package de.charite.compbio.jannovar.cmd.download;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import de.charite.compbio.jannovar.cmd.JannovarDBOptions;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Configuration for the <tt>download</tt> command
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarDownloadOptions extends JannovarDBOptions {

	/** Path to download directory */
	private String downloadDir = null;

	/** Names of the databases to download */
	private List<String> databaseNames = new ArrayList<>();

	/** List of gene identifiers to limit creation of database to. */
	private List<String> geneIdentifiers = new ArrayList<>();

	/** Path to output file (if it should not be generated from download name). */
	private String outputFile = "";

	/**
	 * Setup {@link ArgumentParser}
	 *
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, DownloadCommand> handler = (argv, args) -> {
			try {
				return new DownloadCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("download", true).help("download transcript databases")
				.setDefault("cmd", handler);
		subParser.description("Download transcript database");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("-d", "--database").help("Name of database to download, can be given multiple times")
				.setDefault(new ArrayList<String>()).action(Arguments.append()).required(true);

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Optional Arguments");
		optionalGroup.addArgument("-s", "--data-source-list").help("INI file with data source list")
				.setDefault(new ArrayList<String>(Arrays.asList("bundle:///default_sources.ini"))).action(Arguments.append());
		optionalGroup.addArgument("--download-dir").help("Path to download directory").setDefault("data");

		optionalGroup.addArgument("--gene-ids").help("Optional list of genes to limit creation of database to")
				.setDefault(new ArrayList<String>()).nargs("+");
		optionalGroup.addArgument("-o", "--output-file").help("Optional path to output file").setDefault("");

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		downloadDir = args.getString("download_dir");
		databaseNames = args.getList("database");
		geneIdentifiers = args.getList("gene_ids");
		outputFile = args.getString("output_file");
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public List<String> getDatabaseNames() {
		return databaseNames;
	}

	public void setDatabaseNames(List<String> databaseNames) {
		this.databaseNames = databaseNames;
	}

	public List<String> getGeneIdentifiers() {
		return geneIdentifiers;
	}

	public void setGeneIdentifiers(List<String> geneIdentifiers) {
		this.geneIdentifiers = geneIdentifiers;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public String toString() {
		return "JannovarDownloadOptions [downloadDir=" + downloadDir + ", getDataSourceFiles()=" + getDataSourceFiles()
				+ ", isReportProgress()=" + isReportProgress() + ", getHttpProxy()=" + getHttpProxy()
				+ ", getHttpsProxy()=" + getHttpsProxy() + ", getFtpProxy()=" + getFtpProxy() + ", geneIdentifiers="
				+ geneIdentifiers + ", outputFile=" + outputFile + "]";
	}

}
