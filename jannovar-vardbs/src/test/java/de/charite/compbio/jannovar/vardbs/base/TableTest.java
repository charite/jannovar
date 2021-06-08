package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TableTest {
	@Test
	public void testToString() {
		final Table table = new Table(
			"name",
			"db_name",
			"db_version",
			"PREFIX",
			Lists.newArrayList(
				new TableField("field1", "Integer", "1", "Some description")
			)
		);

		Assertions.assertEquals(
			"Table{name='name', dbName='db_name', dbVersion='db_version', defaultPrefix='PREFIX', " +
				"fields=[TableField{name='field1', type='Integer', count='1', description='Some description'}]}",
			table.toString()
		);
	}
}
