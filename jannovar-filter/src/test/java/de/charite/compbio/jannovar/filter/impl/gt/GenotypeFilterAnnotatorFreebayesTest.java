package de.charite.compbio.jannovar.filter.impl.gt;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Tests for GenotypeFilterAnnotator
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenotypeFilterAnnotatorFreebayesTest extends GenotypeFilterTestBase {

	GenotypeFilterAnnotator annotator;

	@Before
	public void setUp() {
		annotator = new GenotypeFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions());
	}

	@Test
	public void testHighCoverage() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/1:50.5085:20001:10000:228:10001:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowGq() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/1:19.1:100:50:228:50:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetHighAaf() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/1:50.5085:100:19:228:81:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowAaf() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/1:50.5085:100:81:228:19:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowCoverage() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/1:50.5085:7:3:228:4:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowCoverage() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t1/1:50.5085:3:0:228:3:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowAaf() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t1/1:50.5085:10:6:228:4:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomRefHighAaf() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t.\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/0:50.5085:10:6:228:4:24:0,-0.568818,-14.1241\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF),
				annotator.getFiltersFor(gt));
	}

}
