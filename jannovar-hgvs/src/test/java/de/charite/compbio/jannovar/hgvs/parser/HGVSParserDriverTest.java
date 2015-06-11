package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Before;
import org.junit.Test;

public class HGVSParserDriverTest {

	HGVSParserDriver driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParserDriver();
	}

	@Test
	public void testParseNTSubstitution() {
		System.err.println(driver.parseHGVSString("NM_000109.3:g.123C>T").toHGVSString());
	}

}
