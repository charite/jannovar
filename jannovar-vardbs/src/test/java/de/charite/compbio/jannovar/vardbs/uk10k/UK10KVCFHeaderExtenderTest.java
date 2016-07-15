package de.charite.compbio.jannovar.vardbs.uk10k;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;

public class UK10KVCFHeaderExtenderTest {

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

		new UK10KVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(6, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(6, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AN"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AF"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AN"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AF"));
	}

}
