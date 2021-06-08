package de.charite.compbio.jannovar.vardbs.dbsnp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DBSNPGeneInfoTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		DBSNPGeneInfo info = new DBSNPGeneInfo("SYMBOL", 123);
		Assertions.assertEquals(info.getSymbol(), "SYMBOL");
		Assertions.assertEquals(info.getId(), 123);
	}

}
