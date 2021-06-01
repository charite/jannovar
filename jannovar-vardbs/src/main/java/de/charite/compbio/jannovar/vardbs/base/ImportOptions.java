package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

import java.util.Objects;

/**
 * Options for importing VCF file to H2 database.
 */
@Immutable
public final class ImportOptions {
	private final ImmutableList<String> vcfPaths;
	private final String tableName;
	private final String defaultPrefix;
	private final ImmutableList<String> vcfInfoFields;
	private final boolean truncateTable;

	/**
	 * Construct object.
	 *
	 * @param vcfPaths 		Paths to the VCF files to import.
	 * @param tableName 	The name to use for the table internally.  Users will refer to the data
	 *                     	by this name after import.
	 * @param defaultPrefix The default prefix to use
	 * @param vcfInfoFields The VCF INFO fields to import.
	 * @param truncateTable Whether or not to truncate table before importing.
	 */
	public ImportOptions(Iterable<String> vcfPaths, String tableName, String defaultPrefix, Iterable<String> vcfInfoFields, boolean truncateTable) {
		this.vcfPaths = ImmutableList.copyOf(vcfPaths);
		this.tableName = tableName;
		this.defaultPrefix = defaultPrefix;
		this.vcfInfoFields = ImmutableList.copyOf(vcfInfoFields);
		this.truncateTable = truncateTable;
	}

	public ImmutableList<String> getVcfPaths() {
		return vcfPaths;
	}

	public String getTableName() {
		return tableName;
	}

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public ImmutableList<String> getVcfInfoFields() {
		return vcfInfoFields;
	}

	public boolean isTruncateTable() {
		return truncateTable;
	}

	@Override
	public String toString() {
		return "ImportOptions{" +
			"vcfPaths=" + vcfPaths +
			", tableName='" + tableName + '\'' +
			", defaultPrefix='" + defaultPrefix + '\'' +
			", vcfInfoFields=" + vcfInfoFields +
			", truncateTable=" + truncateTable +
			'}';
	}
}
