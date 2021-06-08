package de.charite.compbio.jannovar.vardbs.dbsnp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DBSNPInfoTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		// 	public DBSNPInfo(String fileDate, String source, int dbSNPBuildID, String reference, String phasing) {

		DBSNPInfo info = new DBSNPInfo("date", "source", 123, "ref", "phasing");

		Assertions.assertEquals("date", info.getFileDate());
		Assertions.assertEquals("source", info.getSource());
		Assertions.assertEquals(123, info.getDbSNPBuildID());
		Assertions.assertEquals("ref", info.getReference());
		Assertions.assertEquals("phasing", info.getPhasing());
	}

}
