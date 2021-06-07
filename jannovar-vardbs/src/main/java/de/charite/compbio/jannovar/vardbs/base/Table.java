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
	private final String defaultPrefix;
	private final ImmutableList<TableField> fields;

	/**
	 * Constructor.
	 *
	 * @param name Name of the table.
	 * @param defaultPrefix The default prefix to use for annotation.
	 * @param fields The fields available in the table.
	 */
	public Table(String name, String defaultPrefix, Iterable<TableField> fields) {
		this.name = name;
		this.defaultPrefix = defaultPrefix;
		this.fields = ImmutableList.copyOf(fields);
	}

	public String getName() {
		return name;
	}

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
			", defaultPrefix='" + defaultPrefix + '\'' +
			", fields=" + fields +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Table table = (Table) o;
		return Objects.equal(getName(), table.getName()) && Objects.equal(getDefaultPrefix(), table.getDefaultPrefix()) && Objects.equal(getFields(), table.getFields());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName(), getDefaultPrefix(), getFields());
	}
}
