package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

/**
 * Configuration for annotating from previous import to H2 database.
 */
@Immutable
public final class AnnotationOptions {
	private final String dbPath;
	private final ImmutableList<TableOptions> tableOptions;

	/**
	 * Create an {@code AnnotationOptions} class from the database file at {@code dbPath}.
	 *
	 * In contrast to the constructor, you can leave any entry of the {@link TableOptions} objects empty (except
	 * for table name), and the fields will be filled from the defaults in the H2 database.
	 *
	 * @param dbPath
	 * @param defaultTableOptions
	 */
	public static AnnotationOptions createAnnotationOptions(String dbPath, Iterable<TableOptions> defaultTableOptions) {
		// XXX TODO
		return new AnnotationOptions(dbPath, defaultTableOptions);
	}

	/**
	 * Constructor.
	 *
	 * Only initialize the class properties.  That means the whole {@code tableOptions} must be properly initialized.
	 * If you want to construct with partially filled (e.g., some fields are {@code null}) {@code tableOptions} then
	 * use {@link #createAnnotationOptions(String, Iterable)}.
	 *
	 * @param dbPath 		Path to H2 database (excluding the .h2 suffix) to use with annotation.
	 * @param tableOptions 	The per-table configuration to use for annotation.
	 */
	public AnnotationOptions(String dbPath, Iterable<TableOptions> tableOptions) {
		this.dbPath = dbPath;
		this.tableOptions = ImmutableList.copyOf(tableOptions);
	}

	public String getDbPath() {
		return dbPath;
	}

	public ImmutableList<TableOptions> getTableOptions() {
		return tableOptions;
	}

	@Override
	public String toString() {
		return "AnnotationOptions{" +
			"dbPath='" + dbPath + '\'' +
			", tableOptions=" + tableOptions +
			'}';
	}

	/**
	 * Configuration for annotation per table.
	 */
	public final class TableOptions {
		private final String tableName;
		private final String prefix;
		private final ImmutableList<String> fields;

		/**
		 * Constructor.
		 *
		 * @param tableName Name of the table.
		 * @param prefix The prefix to use, {@code null} if to use default prefix.
		 * @param fields The fields to use, {@code null} if to use default fields.
		 */
		public TableOptions(String tableName, String prefix, ImmutableList<String> fields) {
			this.tableName = tableName;
			this.prefix = prefix;
			this.fields = fields;
		}

		public String getTableName() {
			return tableName;
		}

		public String getPrefix() {
			return prefix;
		}

		public ImmutableList<String> getFields() {
			return fields;
		}

		@Override
		public String toString() {
			return "PerTableOptions{" +
				"tableName='" + tableName + '\'' +
				", prefix='" + prefix + '\'' +
				", fields=" + fields +
				'}';
		}
	}
}
