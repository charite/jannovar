package de.charite.compbio.jannovar.vardbs.gnomad;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GnomadVCFHeaderExtenderTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws JannovarVarDBException {
		VCFHeader header = new VCFHeader();

		// Check header before extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(0, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(0, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
		options.setIdentifierPrefix("GNOMAD_");

		new GnomadVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(122, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(122, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_POPMAX"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_POPMAX"));
	}

}
