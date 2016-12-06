package de.charite.compbio.jannovar.vardbs.clinvar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import htsjdk.variant.vcf.VCFHeader;

public class ClinVarVCFHeaderExtenderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
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

		new ClinVarVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(20, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(20, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_HGVS"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_ALLELE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_SOURCE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_ORIGIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_SIGNIFICANCE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_DB"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_DB_ID"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_DB_NAME"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_REVISION_STATUS"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_CLINICAL_ACCESSION"));

		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_HGVS"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_ALLELE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_SOURCE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_ORIGIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_SIGNIFICANCE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_DISEASE_DB"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_DISEASE_DB_ID"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_DISEASE_DB_NAME"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_REVISION_STATUS"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_OVL_CLINICAL_ACCESSION"));
	}

}
