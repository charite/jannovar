package de.charite.compbio.jannovar.impl.parse;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FASTARecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		FASTARecord record = new FASTARecord("id", "comment foo", "ACGT");

		Assert.assertEquals("id", record.getID());
		Assert.assertEquals("comment foo", record.getComment());
		Assert.assertEquals("ACGT", record.getSequence());
	}

}
