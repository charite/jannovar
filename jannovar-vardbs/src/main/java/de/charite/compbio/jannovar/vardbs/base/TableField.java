package de.charite.compbio.jannovar.vardbs.base;

import de.charite.compbio.jannovar.Immutable;

/**
 * Describe an annotation field in the H2 table.
 */
@Immutable
public final class TableField {
	private final String name;
	private final String type;
	private final String count;
	private final String description;

	/**
	 * Constructor.
	 *
	 * @param name Name of the field.
	 * @param type Type of the field (from VCF).
	 * @param count Count of the field (from VCF, only "1" and "A" are currently supported).
	 * @param description Description of the field (from VCF).
	 */
	public TableField(String name, String type, String count, String description) {
		this.name = name;
		this.type = type;
		this.count = count;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getCount() {
		return count;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "TableField{" +
			"name='" + name + '\'' +
			", type='" + type + '\'' +
			", count='" + count + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}
