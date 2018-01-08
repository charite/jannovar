package de.charite.compbio.jannovar.impl.parse;

import org.junit.Assert;
import org.junit.Test;

public class FASTARecordTest {

	@Test
	public void test() {
		FASTARecord record = new FASTARecord("id", "comment foo", "ACGT");

		Assert.assertEquals("id", record.getID());
		Assert.assertEquals("comment foo", record.getComment());
		Assert.assertEquals("ACGT", record.getSequence());
	}

}
