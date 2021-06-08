package de.charite.compbio.jannovar.vardbs.base;

import de.charite.compbio.jannovar.Immutable;

import java.sql.Connection;

/**
 * Show the contents of a Jannovar H2 database file.
 */
@Immutable
public final class ListCommand {
	private final ListOptions options;

	/**
     * Constructor.
	 *
	 * @param options The {@code ListOptions} to use.
	 */
	public ListCommand(ListOptions options) {
		this.options = options;
	}

    /**
     * Execute the Listing.
	 */
	public void run(Connection conn) throws JannovarVarDBException {
		final long startTime = System.nanoTime();
		final TableDao tableDao = new TableDao(conn);
		for (Table table: tableDao.getAllTables()) {
			System.out.println(table + "\n\n");
		}

		final long endTime = System.nanoTime();
		System.err.println(String.format("Listing took %.2f sec.",
			(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
	}
}
