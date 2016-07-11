package de.charite.compbio.jannovar.vardbs.dbsnp;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;

public class DBSNPVCFHeaderExtenderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws JannovarVarDBException {
		VCFHeader header = new VCFHeader();

		// Check header before extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(0, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(0, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		new DBSNPVCFHeaderExtender().addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(4, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(4, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_COMMON"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_CAF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_G5A"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_MATCH"));
	}

}
