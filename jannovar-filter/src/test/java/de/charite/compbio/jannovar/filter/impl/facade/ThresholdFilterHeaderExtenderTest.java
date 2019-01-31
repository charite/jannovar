package de.charite.compbio.jannovar.filter.impl.facade;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.Assert;
import org.junit.Test;

public class ThresholdFilterHeaderExtenderTest extends TresholdFilterTestBase {

	@Test
	public void test() {
		VCFHeader header = new VCFHeader();

		// Check header before extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(0, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(0, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		new ThresholdFilterHeaderExtender(ThresholdFilterOptions.buildDefaultOptions())
			.addHeaders(header);

		// Check header after extension
		Assert.assertEquals(13, header.getFilterLines().size());
		Assert.assertEquals(0, header.getInfoHeaderLines().size());
		Assert.assertEquals(1, header.getFormatHeaderLines().size());
		Assert.assertEquals(14, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV));
		Assert.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET));
		Assert.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT));
		Assert.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ));
		Assert.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET));
		Assert.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET));
		Assert.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT));
		Assert.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF));

		Assert.assertNotNull(header.getFilterHeaderLine(
			ThresholdFilterHeaderExtender.FILTER_VAR_ALL_AFFECTED_GTS_FILTERED));

		Assert.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_FREQUENCY_AD));
		Assert.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_FREQUENCY_AR));

		Assert.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_HOM_EXAC));
		Assert.assertNotNull(header.getFilterHeaderLine(
			ThresholdFilterHeaderExtender.FILTER_VAR_MAX_HOM_THOUSAND_GENOMES));
	}

}
