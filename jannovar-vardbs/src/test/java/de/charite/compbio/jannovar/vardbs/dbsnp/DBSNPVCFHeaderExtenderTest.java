package de.charite.compbio.jannovar.vardbs.dbsnp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
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

		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);

		new DBSNPVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(12, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(12, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_COMMON"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_CAF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_G5"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_G5A"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_IDS"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_SAO"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_COMMON"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_CAF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5A"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_IDS"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_SAO"));
	}

}
