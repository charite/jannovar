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
public class GenotypeFilterAnnotatorGatkCallerTest extends GenotypeFilterTestBase {

	GenotypeFilterAnnotator annotator;

	@Before
	public void setUp() {
		annotator = new GenotypeFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions());
	}

	@Test
	public void testHighCoverage() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:10000,10000:20001:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowGq() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:117,133:250:19:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetHighAaf() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:19,81:100:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowAaf() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:81,19:100:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowCoverage() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:3,4:7:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowCoverage() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t1/1:0,3:3:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowAaf() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t1/1:31,69:100:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomRefHighAaf() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/0:117,133:250:20:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF),
				annotator.getFiltersFor(gt));
	}

}
