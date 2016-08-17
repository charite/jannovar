package de.charite.compbio.jannovar.vardbs.dbsnp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DBSNPInfoTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		// 	public DBSNPInfo(String fileDate, String source, int dbSNPBuildID, String reference, String phasing) {

		DBSNPInfo info = new DBSNPInfo("date", "source", 123, "ref", "phasing");
		
		Assert.assertEquals("date", info.getFileDate());
		Assert.assertEquals("source", info.getSource());
		Assert.assertEquals(123, info.getDbSNPBuildID());
		Assert.assertEquals("ref", info.getReference());
		Assert.assertEquals("phasing", info.getPhasing());
	}

}
