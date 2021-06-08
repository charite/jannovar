package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

/**
 * Description of a table in the H2 database for annotation.
 */
@Immutable
public final class Table {
	private final String name;
	private final String dbName;
	private final String dbVersion;
	private final String defaultPrefix;
	private final ImmutableList<TableField> fields;

	/**
	 * Constructor.
	 *
	 * @param name Name of the table.
	 * @param dbName Name of the database.
	 * @param dbVersion Version of the databse.
	 * @param defaultPrefix The default prefix to use for annotation.
	 * @param fields The fields available in the table.
	 */
	public Table(String name, String dbName, String dbVersion, String defaultPrefix, Iterable<TableField> fields) {
		this.name = name;
		this.dbName = dbName;
		this.dbVersion = dbVersion;
		this.defaultPrefix = defaultPrefix;
		this.fields = ImmutableList.copyOf(fields);
	}

	public String getName() {
		return name;
	}

	public String getDbName() { return dbName; }

	public String getDbVersion() { return dbVersion; }

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public ImmutableList<TableField> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return "Table{" +
			"name='" + name + '\'' +
			", dbName='" + dbName + '\'' +
			", dbVersion='" + dbVersion + '\'' +
			", defaultPrefix='" + defaultPrefix + '\'' +
			", fields=" + fields +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Table table = (Table) o;
		return Objects.equal(getName(), table.getName()) && Objects.equal(getDbName(), table.getDbName()) && Objects.equal(getDbVersion(), table.getDbVersion()) && Objects.equal(getDefaultPrefix(), table.getDefaultPrefix()) && Objects.equal(getFields(), table.getFields());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName(), getDbName(), getDbVersion(), getDefaultPrefix(), getFields());
	}
}
