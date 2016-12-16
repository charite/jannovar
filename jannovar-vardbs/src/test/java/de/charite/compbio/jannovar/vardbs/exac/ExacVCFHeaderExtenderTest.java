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
		Assert.assertEquals(100, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(100, header.getIDHeaderLines().size());
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
		
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_ALL"));
		
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
		
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_ALL"));
		
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_BEST_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_BEST_AF"));
	}

}
