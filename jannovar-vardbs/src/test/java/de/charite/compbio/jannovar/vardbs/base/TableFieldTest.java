package de.charite.compbio.jannovar.vardbs.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class TableFieldTest {
	@Test
	public void testToString() {
		final TableField tableField = new TableField("field1", "Integer", "1", "Some description");
		Assertions.assertEquals(
			"TableField{name='field1', type='Integer', count='1', description='Some description'}",
			tableField.toString()
		);
	}
}
