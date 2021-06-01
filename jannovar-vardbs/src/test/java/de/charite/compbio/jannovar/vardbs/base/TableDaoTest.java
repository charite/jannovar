package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertEquals;

public class TableDaoTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public String dbPath;
	public Connection conn;
	private TableDao tableDao;

	@Before
	public void setUp() throws Exception {
		this.dbPath = folder.getRoot() + "/test";
		this.conn = DriverManager.getConnection(
			"jdbc:h2:"
				+ this.dbPath
				+ ";TRACE_LEVEL_FILE=0;MV_STORE=FALSE;DB_CLOSE_ON_EXIT=FALSE",
			"sa",
			"");
		this.tableDao = new TableDao(this.conn);
	}

	@After
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
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);

		this.tableDao.initializeDatabase();
		this.tableDao.createTable(table);

		final Table tableDb = this.tableDao.getTable(table.getName());
		assertEquals(table.toString(), tableDb.toString());
	}

	@Test
	public void testUpdateTable() throws Exception {
		final Table table = new Table(
			"name",
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);
		final Table table2 = new Table(
			"name",
			"PREFIX2",
			Lists.newArrayList(
				new TableField("field2", "Integer", "1", "Some description")
			)
		);
		this.tableDao.initializeDatabase();
		this.tableDao.createTable(table);
		this.tableDao.updateTable(table2);

		final Table tableDb = this.tableDao.getTable(table.getName());
		assertEquals(table2.toString(), tableDb.toString());
	}

	@Test
	public void testGetAllTables() throws Exception {
		this.tableDao.initializeDatabase();

		assertEquals("[]", this.tableDao.getAllTables().toString());

		final Table table = new Table(
			"name",
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);
		this.tableDao.createTable(table);

		assertEquals(
			"[Table{name='name', defaultPrefix='PREFIX', fields=[" +
			"TableField{name='field1', type='Integer', count='1', description='Some description'}]}]",
			this.tableDao.getAllTables().toString()
		);
	}
}
