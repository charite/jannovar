package de.charite.compbio.jannovar.cmd.vardb_annotate.vardb_import;

import com.google.common.base.Objects;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import net.sourceforge.argparse4j.inf.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Configuration for the <code>vardb-annotate</code> command
 *
 * @author <a href="mailto:manuel.holtgrewe@bih-charite.de">Manuel Holtgrewe</a>
 */
public class JannovarVardbAnnotateOptions extends JannovarBaseOptions {
	private String genomeBuild = null;
	private String dbPath = null;
	private String inputVcfPath = null;
	private String outputVcfPath = null;
	private List<String> tableNames = new ArrayList<>();

	/**
	 * Setup {@link ArgumentParser}
	 *
	 * @param subParsers {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, VardbAnnotateCommand> handler = (argv, args) -> {
			try {
				return new VardbAnnotateCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("vardb-annotate", true)
			.help("Annotate using Jannovar H2 vardb file")
			.setDefault("cmd", handler);
		subParser.description("Annotate using Jannovar H2 vardb file");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("--genome-build").help("String to use for genome build").required(true);
		requiredGroup.addArgument("--database-file").help("Path to database file").required(true);
		requiredGroup.addArgument("--input-vcf").help("Path to input VCF file").required(true);
		requiredGroup.addArgument("--output-vcf").help("Name to output VCf file").required(true);
		requiredGroup.addArgument("--table-names").help("Names of tables to use for annotating")
			.nargs("+").required(true);

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		genomeBuild = args.get("genome_build");
		dbPath = args.getString("database_file");
		inputVcfPath = args.getString("input_vcf");
		outputVcfPath = args.getString("output_vcf");
		final List<String> tableNameList = new ArrayList<>();
		for (Object s: args.getList("table_names")) {
			for (String t: ((String)s).split(",")) {
				tableNameList.add(t);
			}
		}
		tableNames = tableNameList;
	}

	public String getGenomeBuild() {
		return genomeBuild;
	}

	public void setGenomeBuild(String genomeBuild) {
		this.genomeBuild = genomeBuild;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getInputVcfPath() {
		return inputVcfPath;
	}

	public void setInputVcfPath(String inputVcfPath) {
		this.inputVcfPath = inputVcfPath;
	}

	public String getOutputVcfPath() {
		return outputVcfPath;
	}

	public void setOutputVcfPath(String outputVcfPath) {
		this.outputVcfPath = outputVcfPath;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JannovarVardbAnnotateOptions that = (JannovarVardbAnnotateOptions) o;
		return Objects.equal(getGenomeBuild(), that.getGenomeBuild()) && Objects.equal(getDbPath(), that.getDbPath()) && Objects.equal(getInputVcfPath(), that.getInputVcfPath()) && Objects.equal(getOutputVcfPath(), that.getOutputVcfPath()) && Objects.equal(getTableNames(), that.getTableNames());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getGenomeBuild(), getDbPath(), getInputVcfPath(), getOutputVcfPath(), getTableNames());
	}

	@Override
	public String toString() {
		return "JannovarVardbAnnotateOptions{" +
			"genomeBuild='" + genomeBuild + '\'' +
			", dbPath='" + dbPath + '\'' +
			", inputVcfPath='" + inputVcfPath + '\'' +
			", outputVcfPath='" + outputVcfPath + '\'' +
			", tableNames=" + tableNames +
			'}';
	}
}
