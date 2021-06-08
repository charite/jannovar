package de.charite.compbio.jannovar.filter.impl.facade;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThresholdFilterHeaderExtenderTest extends TresholdFilterTestBase {

	@Test
	public void test() {
		VCFHeader header = new VCFHeader();

		// Check header before extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(0, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(0, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		new ThresholdFilterHeaderExtender(ThresholdFilterOptions.buildDefaultOptions())
			.addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(13, header.getFilterLines().size());
		Assertions.assertEquals(0, header.getInfoHeaderLines().size());
		Assertions.assertEquals(1, header.getFormatHeaderLines().size());
		Assertions.assertEquals(14, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV));
		Assertions.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET));
		Assertions.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT));
		Assertions.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ));
		Assertions.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET));
		Assertions.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET));
		Assertions.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT));
		Assertions.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF));

		Assertions.assertNotNull(header.getFilterHeaderLine(
			ThresholdFilterHeaderExtender.FILTER_VAR_ALL_AFFECTED_GTS_FILTERED));

		Assertions.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_FREQUENCY_AD));
		Assertions.assertNotNull(header
			.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_FREQUENCY_AR));

		Assertions.assertNotNull(
			header.getFilterHeaderLine(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_HOM_EXAC));
		Assertions.assertNotNull(header.getFilterHeaderLine(
			ThresholdFilterHeaderExtender.FILTER_VAR_MAX_HOM_THOUSAND_GENOMES));
	}

}
