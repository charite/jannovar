package de.charite.compbio.jannovar.vardbs.gnomad;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;

public class GnomadVCFHeaderExtenderTest {

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

		new GnomadVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(122, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(122, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_ALL"));
		
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_ALL"));
		
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_POPMAX"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_ALL"));
		
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_ALL"));
		
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_POPMAX"));
	}

}
