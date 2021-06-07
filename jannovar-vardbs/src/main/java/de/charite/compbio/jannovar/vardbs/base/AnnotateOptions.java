package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

import java.util.Objects;

/**
 * Options for annotating VCF file with H2 database(s).
 */
@Immutable
public final class AnnotateOptions {
	private final String genomeBuild;
	private final String dbPath;
	private final String inputVcfPath;
	private final String outputVcfPath;
	private final ImmutableList<String> tableNames;

	/**
	 * Constructor.
	 *
	 * @param genomeBuild 		Name of the genome build.
	 * @param dbPath			Path to the H2 database to use for annotation.
	 * @param inputVcfPath		Path to input VCF file to annotate.
	 * @param outputVcfPath		Path to output VCF file to write to.
	 * @param tableNames		Name of the table(s) to use for annotation.
	 */
	public AnnotateOptions(String genomeBuild, String dbPath, String inputVcfPath, String outputVcfPath, Iterable<String> tableNames) {
		this.genomeBuild = genomeBuild;
		this.dbPath = dbPath;
		this.inputVcfPath = inputVcfPath;
		this.outputVcfPath = outputVcfPath;
		this.tableNames = ImmutableList.copyOf(tableNames);
	}

	public String getGenomeBuild() {
		return genomeBuild;
	}

	public String getDbPath() {
		return dbPath;
	}

	public String getInputVcfPath() {
		return inputVcfPath;
	}

	public String getOutputVcfPath() {
		return outputVcfPath;
	}

	public ImmutableList<String> getTableNames() {
		return tableNames;
	}

	@Override
	public String toString() {
		return "AnnotateOptions{" +
			"genomeBuild='" + genomeBuild + '\'' +
			", dbPath='" + dbPath + '\'' +
			", inputVcfPath='" + inputVcfPath + '\'' +
			", outputVcfPaths='" + outputVcfPath + '\'' +
			", tableNames=" + tableNames +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnnotateOptions that = (AnnotateOptions) o;
		return Objects.equals(genomeBuild, that.genomeBuild) && Objects.equals(dbPath, that.dbPath) && Objects.equals(inputVcfPath, that.inputVcfPath) && Objects.equals(outputVcfPath, that.outputVcfPath) && Objects.equals(tableNames, that.tableNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(genomeBuild, dbPath, inputVcfPath, outputVcfPath, tableNames);
	}
}
