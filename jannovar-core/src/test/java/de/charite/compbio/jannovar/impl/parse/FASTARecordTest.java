package de.charite.compbio.jannovar.impl.parse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FASTARecordTest {

	@Test
	public void test() {
		FASTARecord record = new FASTARecord("id", "comment foo", "ACGT");

		Assertions.assertEquals("id", record.getID());
		Assertions.assertEquals("comment foo", record.getComment());
		Assertions.assertEquals("ACGT", record.getSequence());
	}

}
