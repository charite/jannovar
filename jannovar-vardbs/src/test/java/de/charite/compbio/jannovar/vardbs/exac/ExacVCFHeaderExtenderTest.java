package de.charite.compbio.jannovar.vardbs.exac;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
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

		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);

		new ExacVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(52, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(52, header.getIDHeaderLines().size());
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

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_BEST_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_BEST_AF"));
	}

}
