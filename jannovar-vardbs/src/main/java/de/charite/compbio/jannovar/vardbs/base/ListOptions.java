package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

/**
 * Options for importing VCF file to H2 database.
 */
@Immutable
public final class ListOptions {
	private final String dbPath;

	/**
	 * Construct object.
	 *
	 * @param dbPath 		Path to the H2 database.
	 */
	public ListOptions(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getDbPath() { return dbPath; }

	@Override
	public String toString() {
		return "ListOptions{" +
			"dbPath='" + dbPath + '\'' +
			'}';
	}
}
