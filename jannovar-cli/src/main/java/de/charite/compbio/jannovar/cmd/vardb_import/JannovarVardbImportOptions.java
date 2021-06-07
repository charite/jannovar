package de.charite.compbio.jannovar.cmd.vardb_import;

import com.google.common.base.Objects;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import de.charite.compbio.jannovar.cmd.statistics.GatherStatisticsCommand;
import de.charite.compbio.jannovar.cmd.statistics.JannovarGatherStatisticsOptions;
import net.sourceforge.argparse4j.inf.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

/**
 * Configuration for the <code>vardb-import</code> command
 *
 * @author <a href="mailto:manuel.holtgrewe@bih-charite.de">Manuel Holtgrewe</a>
 */
public class JannovarVardbImportOptions extends JannovarBaseOptions {
	private String genomeBuild = null;
	private String dbPath = null;
	private List<String> vcfPaths = new ArrayList<>();
	private String tableName = null;
	private String dbName = null;
	private String dbVersion = null;
	private String defaultPrefix = null;
	private List<String> vcfInfoFields = new ArrayList<>();
	private boolean truncateTable = false;

	/**
	 * Setup {@link ArgumentParser}
	 *
	 * @param subParsers {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, VardbImportCommand> handler = (argv, args) -> {
			try {
				return new VardbImportCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("vardb-import", true)
			.help("Import from VCF file into Jannovar H2 vardb file")
			.setDefault("cmd", handler);
		subParser.description("Import into Jannovar H2 vardb file");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("--genome-build").help("String to use for genome build").required(true);
		requiredGroup.addArgument("--database-file").help("Path to database file").required(true);
		requiredGroup.addArgument("--vcf-files").help("Path to VCF file(s)").nargs("+").required(true);
		requiredGroup.addArgument("--table-name").help("Name of table after import").required(true);
		requiredGroup.addArgument("--db-name").help("Datbase name").required(true);
		requiredGroup.addArgument("--db-version").help("Database version").required(true);
		requiredGroup.addArgument("--default-prefix").help("Default prefix for annotating").required(true);
		requiredGroup.addArgument("--vcf-info-fields").help("INFO fields to import").nargs("+").required(true);
		requiredGroup.addArgument("--truncate-table").help("Truncate table before first import")
			.action(storeTrue())
			.setDefault(false);

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		genomeBuild = args.get("genome_build");
		dbPath = args.getString("database_file");
		final List<String> vcfFilesList = new ArrayList<>();
		for (Object s: args.getList("vcf_files")) {
			vcfFilesList.addAll(Arrays.asList(((String) s).split(",")));
		}
		vcfPaths = vcfFilesList;
		tableName = args.getString("table_name");
		dbName = args.get("db_name");
		dbVersion = args.get("db_version");
		defaultPrefix = args.getString("default_prefix");
		final List<String> vcfInfoFieldsList = new ArrayList<>();
		for (Object s: args.getList("vcf_info_fields")) {
			vcfInfoFieldsList.addAll(Arrays.asList(((String) s).split(",")));
		}
		vcfInfoFields = vcfInfoFieldsList;
		truncateTable = args.getBoolean("truncate_table");
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbVersion() {
		return dbVersion;
	}

	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
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

	public List<String> getVcfPaths() {
		return vcfPaths;
	}

	public void setVcfPaths(List<String> vcfPaths) {
		this.vcfPaths = vcfPaths;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public void setDefaultPrefix(String defaultPrefix) {
		this.defaultPrefix = defaultPrefix;
	}

	public List<String> getVcfInfoFields() {
		return vcfInfoFields;
	}

	public void setVcfInfoFields(List<String> vcfInfoFields) {
		this.vcfInfoFields = vcfInfoFields;
	}

	public boolean isTruncateTable() {
		return truncateTable;
	}

	public void setTruncateTable(boolean truncateTable) {
		this.truncateTable = truncateTable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JannovarVardbImportOptions that = (JannovarVardbImportOptions) o;
		return isTruncateTable() == that.isTruncateTable() && Objects.equal(getGenomeBuild(), that.getGenomeBuild()) && Objects.equal(getDbPath(), that.getDbPath()) && Objects.equal(getVcfPaths(), that.getVcfPaths()) && Objects.equal(getTableName(), that.getTableName()) && Objects.equal(getDefaultPrefix(), that.getDefaultPrefix()) && Objects.equal(getVcfInfoFields(), that.getVcfInfoFields());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getGenomeBuild(), getDbPath(), getVcfPaths(), getTableName(), getDefaultPrefix(), getVcfInfoFields(), isTruncateTable());
	}

	@Override
	public String toString() {
		return "JannovarVardbImportOptions{" +
			"genomeBuild='" + genomeBuild + '\'' +
			", dbPath='" + dbPath + '\'' +
			", vcfPaths=" + vcfPaths +
			", tableName='" + tableName + '\'' +
			", defaultPrefix='" + defaultPrefix + '\'' +
			", vcfInfoFields=" + vcfInfoFields +
			", truncateTable=" + truncateTable +
			'}';
	}
}
