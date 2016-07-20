package de.charite.compbio.jannovar.vardbs.dbsnp;

import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

public class DBSNPGeneInfoTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		DBSNPGeneInfo info = new DBSNPGeneInfo("SYMBOL", 123);
		Assert.assertEquals(info.getSymbol(), "SYMBOL");
		Assert.assertEquals(info.getId(), 123);
	}

}
