package de.charite.compbio.jannovar.vardbs.exac;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;

public class ExacVCFHeaderExtenderTest {

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

		new ExacVCFHeaderExtender().addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(26, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(26, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_BEST_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_BEST_AF"));
	}

}
