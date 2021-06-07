package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to the Table records stored in the class.
 */
@Immutable
public final class TableDao implements Closeable {
	/** Maximal length of reference/alternative allele. */
	public static final int MAX_ALLELE_LENGTH = 1000;
	/** Database table name that stores tables. */
	public static final String TABLE_NAME_TABLE = "jannovar_meta_table";
	/** Database table name that store fields. */
	public static final String TABLE_NAME_FIELD = "jannovar_meta_field";

	private final Connection conn;

	/**
	 * Constructor.
	 *
	 * @param conn Create DAO object with the given connection.  The {@code TableDao} class takes over ownership and
	 *             will close the connection when {@link #close()} is called.
	 */
	public TableDao(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Returns all tables.
	 *
	 * @return List of all table entries.
	 */
	public ImmutableList<Table> getAllTables() throws JannovarVarDBException {
		final Map<String, List<TableField>> fieldsByTable = new HashMap<>();

		try (final PreparedStatement stmtTable = this.conn.prepareStatement(
				"SELECT * FROM " + TABLE_NAME_TABLE);
			 final PreparedStatement stmtField = this.conn.prepareStatement(
				 "SELECT * FROM " + TABLE_NAME_FIELD + " ORDER BY id")) {
			final ResultSet rsField = stmtField.executeQuery();
			while (rsField.next()) {
				final TableField field = new TableField(
					rsField.getString("name"),
					rsField.getString("type"),
					rsField.getString("count"),
					rsField.getString("description")
				);

				final String key = rsField.getString("table_name");
				fieldsByTable.putIfAbsent(key, new ArrayList<>());
				fieldsByTable.get(key).add(field);
			}

			final ResultSet rsTable = stmtTable.executeQuery();
			final ImmutableList.Builder resultBuilder = ImmutableList.builder();
			while (rsTable.next()) {
				final String tableName = rsTable.getString("name");
				resultBuilder.add(new Table(
					tableName,
					rsTable.getString("default_prefix"),
					ImmutableList.copyOf(fieldsByTable.getOrDefault(tableName, new ArrayList<>()))
				));
			}
			rsTable.close();
			return resultBuilder.build();
		} catch (SQLException e) {
			throw new JannovarVarDBException("There was a problem with the H2 database", e);
		}
	}

	/**
	 * Delete table entry with the given name.
	 *
	 * @param tableName The name of the table to remove.
	 * @throws JannovarVarDBException In the case of problems with the H2 database.
	 */
	public void deleteTable(String tableName) throws JannovarVarDBException {
		try (
			final PreparedStatement stmtTable = this.conn.prepareStatement(
				"DELETE FROM " + TABLE_NAME_TABLE + " WHERE name = ?");
			final PreparedStatement stmtField = this.conn.prepareStatement(
				"DELETE FROM " + TABLE_NAME_FIELD + " WHERE table_name = ?");
			final PreparedStatement stmtDrop = this.conn.prepareStatement(
				"DROP TABLE IF EXISTS " + tableName
			)) {
			stmtTable.setString(1, tableName);
			stmtTable.executeUpdate();
			stmtField.setString(1, tableName);
			stmtField.executeUpdate();
			stmtDrop.executeUpdate();
		} catch (SQLException e) {
			throw new JannovarVarDBException("Problem with H2 query", e);
		}
	}

	/**
	 * Create new entry for a table in the datbase.
	 *
	 * @param table The table definition.
	 * @throws JannovarVarDBException In the case of problems with the H2 database.
	 */
	public void createTable(Table table) throws JannovarVarDBException {
		final Table prevTable = getTable(table.getName());
		if (prevTable != null) {
			// Do not perform creation twice but guard against change in definition.
			if (!prevTable.equals(table)) {
				throw new JannovarVarDBException(
					"Table " + table.getName() + " already exist, and definition differs " +
						prevTable + " != " + table + "(update not implemented yet!"
				);
			} else {
				return;
			}
		}

		try (
			final PreparedStatement stmtTable = this.conn.prepareStatement(
				"INSERT INTO " + TABLE_NAME_TABLE + " (name, default_prefix) VALUES (?, ?)");
			final PreparedStatement stmtField = this.conn.prepareStatement(
				"INSERT INTO " + TABLE_NAME_FIELD + " (table_name, name, count, type, description) VALUES (?, ?, ?, ?, ?)")) {

			stmtTable.setString(1, table.getName());
			stmtTable.setString(2, table.getDefaultPrefix());
			stmtTable.executeUpdate();

			for (final TableField field: table.getFields()) {
				stmtField.setString(1, table.getName());
				stmtField.setString(2, field.getName());
				stmtField.setString(3, field.getCount());
				stmtField.setString(4, field.getType());
				stmtField.setString(5, field.getDescription());
				stmtField.executeUpdate();
			}

			final List<String> fieldLines = new ArrayList<>();
			for (TableField field : table.getFields()) {
				String fieldType;
				switch (field.getType()) {
					case "Integer":
						fieldType = "INT";
						break;
					case "Float":
						fieldType = "DOUBLE";
						break;
					case "Boolean":
						fieldType = "BOOLEAN";
						break;
					case "String":
						fieldType = "VARCHAR (" + MAX_ALLELE_LENGTH + ")";
						break;
					default:
						throw new RuntimeException("Invalid field type " + field.getType());
				}

				fieldLines.add(String.format("%s %s", field.getName(), fieldType));
			}

			this.conn.prepareStatement(
				"CREATE TABLE " + table.getName() + " (\n" +
					"  genome_build VARCHAR(50) NOT NULL,\n" +
					"  contig VARCHAR(50) NOT NULL,\n" +
					"  start VARCHAR(50) NOT NULL,\n" +
					"  end VARCHAR(50) NOT NULL,\n" +
					"  ref VARCHAR(" + MAX_ALLELE_LENGTH + ") NOT NULL,\n" +
					"  alt VARCHAR(" + MAX_ALLELE_LENGTH + ") NOT NULL,\n" +
					Joiner.on(", \n  ").join(fieldLines) +
				"\n)"
			).executeUpdate();
			this.conn.prepareStatement(
				"CREATE PRIMARY KEY ON " + table.getName() +
					" (genome_build, contig, start, end, ref, alt);"
			).executeUpdate();
		} catch (SQLException e) {
			throw new JannovarVarDBException("Problem with H2 query", e);
		}
	}

	/**
	 * Update (delete and re-create) the table with the given name.
	 *
	 * @param table The {@code Table} to update.
	 * @throws JannovarVarDBException In the case of problems with the H2 database.
	 */
	public void updateTable(Table table) throws JannovarVarDBException {
		try {
			this.conn.setAutoCommit(false);
			deleteTable(table.getName());
			createTable(table);
			this.conn.commit();
		} catch (SQLException e) {
			throw new JannovarVarDBException("Problem with H2 transaction", e);
		}
	}

	public Table getTable(String tableName) throws JannovarVarDBException {
		try (final PreparedStatement stmtTable = this.conn.prepareStatement(
			"SELECT * FROM " + TABLE_NAME_TABLE + " WHERE name = ?");
			 final PreparedStatement stmtField = this.conn.prepareStatement(
			 	"SELECT * FROM " + TABLE_NAME_FIELD + " WHERE table_name = ? ORDER BY id")
		) {
			stmtField.setString(1, tableName);
			final ResultSet rsField = stmtField.executeQuery();
			ImmutableList.Builder fieldBuilder = ImmutableList.builder();
			while (rsField.next()) {
				fieldBuilder.add(new TableField(
					rsField.getString("name"),
					rsField.getString("type"),
					rsField.getString("count"),
					rsField.getString("description")
				));
			}

			stmtTable.setString(1, tableName);
			final ResultSet rsTable = stmtTable.executeQuery();
			if (!rsTable.next()) {
				rsTable.close();
				return null;
			}
			final Table result = new Table(
				rsTable.getString("name"),
				rsTable.getString("default_prefix"),
				fieldBuilder.build()
			);
			rsTable.close();
			return result;
		} catch (SQLException e) {
			throw new JannovarVarDBException("There was a problem with the H2 database", e);
		}
	}

	/**
	 * Update meta data schema (creates tables if necessary).
	 *
	 * @throws JannovarVarDBException In the case of problems with the H2 database.
	 */
	public void initializeDatabase() throws JannovarVarDBException {
		try {
			this.conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TABLE + "(\n" +
					"id IDENTITY NOT NULL PRIMARY KEY,\n" +
					"name VARCHAR(100) NOT NULL,\n" +
					"default_prefix VARCHAR(100) NOT NULL\n" +
				");"
			).executeUpdate();
			this.conn.prepareStatement(
				"CREATE UNIQUE INDEX IF NOT EXISTS " + TABLE_NAME_TABLE + "_name ON " +
					TABLE_NAME_TABLE + " (name);"
			).executeUpdate();

			this.conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FIELD + "(\n" +
					"id IDENTITY NOT NULL PRIMARY KEY,\n" +
					"table_name VARCHAR(100) NOT NULL,\n" +
					"name VARCHAR(100) NOT NULL,\n" +
					"type VARCHAR(100) NOT NULL,\n" +
					"count VARCHAR(100) NOT NULL,\n" +
					"description VARCHAR(255)\n" +
					");"
			).executeUpdate();
			this.conn.prepareStatement(
				"CREATE UNIQUE INDEX IF NOT EXISTS " + TABLE_NAME_FIELD + "_table_name_name ON " +
					TABLE_NAME_FIELD + " (table_name, name);"
			).executeUpdate();
		} catch (SQLException e) {
			throw new JannovarVarDBException("Problem querying H2 database", e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			this.conn.close();
		} catch (SQLException e) {
			throw new IOException("Could not close H2 connection", e);
		}
	}
}
