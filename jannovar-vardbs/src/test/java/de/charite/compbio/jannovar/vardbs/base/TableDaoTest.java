package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertEquals;

public class TableDaoTest {
	@TempDir
	public File folder;

	public String dbPath;
	public Connection conn;
	private TableDao tableDao;

	@BeforeEach
	public void setUp() throws Exception {
		this.dbPath = new File(folder, "testdb").toString();
		this.conn = DriverManager.getConnection(
			"jdbc:h2:"
				+ this.dbPath
				+ ";TRACE_LEVEL_FILE=0;MV_STORE=FALSE;DB_CLOSE_ON_EXIT=FALSE",
			"sa",
			"");
		this.tableDao = new TableDao(this.conn);
	}

	@AfterEach
	public void tearDown() throws Exception {
		this.conn.close();
	}

	@Test
	public void testInitializeDatabase() throws Exception {
		this.tableDao.initializeDatabase();
		this.tableDao.initializeDatabase();
	}

	@Test
	public void testCreateTable() throws Exception {
		final Table table = new Table(
			"name",
			"db_name",
			"db_version",
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);

		this.tableDao.initializeDatabase();
		this.tableDao.createTable(table);

		final Table tableDb = this.tableDao.getTable(table.getName());
		Assertions.assertEquals(table.toString(), tableDb.toString());
	}

	@Test
	public void testUpdateTable() throws Exception {
		final Table table = new Table(
			"name",
			"db_name",
			"db_version",
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);
		final Table table2 = new Table(
			"name",
			"db_name",
			"db_version",
			"PREFIX2",
			Lists.newArrayList(
				new TableField("field2", "Integer", "1", "Some description")
			)
		);
		this.tableDao.initializeDatabase();
		this.tableDao.createTable(table);
		this.tableDao.updateTable(table2);

		final Table tableDb = this.tableDao.getTable(table.getName());
		Assertions.assertEquals(table2.toString(), tableDb.toString());
	}

	@Test
	public void testGetAllTables() throws Exception {
		this.tableDao.initializeDatabase();

		Assertions.assertEquals("[]", this.tableDao.getAllTables().toString());

		final Table table = new Table(
			"name",
			"db_name",
			"db_version",
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);
		this.tableDao.createTable(table);

		Assertions.assertEquals(
			"[Table{name='name', dbName='db_name', dbVersion='db_version', defaultPrefix='PREFIX', fields=[" +
			"TableField{name='field1', type='Integer', count='1', description='Some description'}]}]",
			this.tableDao.getAllTables().toString()
		);
	}
}
