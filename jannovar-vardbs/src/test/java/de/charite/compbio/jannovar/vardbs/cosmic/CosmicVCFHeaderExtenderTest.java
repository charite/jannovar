package de.charite.compbio.jannovar.vardbs.cosmic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;

public class CosmicVCFHeaderExtenderTest {

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

		new CosmicVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(6, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(6, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_CNT"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_SNP"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_IDS"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_CNT"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_SNP"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_IDS"));
	}

}
