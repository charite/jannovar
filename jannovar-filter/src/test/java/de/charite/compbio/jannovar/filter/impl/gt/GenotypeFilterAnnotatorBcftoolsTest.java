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
public class GenotypeFilterAnnotatorBcftoolsTest extends GenotypeFilterTestBase {

	GenotypeFilterAnnotator annotator;

	@Before
	public void setUp() {
		annotator = new GenotypeFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions());
	}

	@Test
	public void testHighCoverage() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t0/1:139,0,255:10001:43:43:86,9,48,27:5000,5000:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowGq() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t0/1:139,0,255:170:75:43:86,9,48,27:95,75:138,0,261:19\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetHighAaf() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t0/1:139,0,255:170:75:43:86,9,48,27:20,150:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowAaf() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t0/1:139,0,255:170:75:43:86,9,48,27:150,20:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowCoverage() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t0/1:139,0,255:7:75:43:86,9,48,27:4,3:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowCoverage() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t1/1:139,0,255:3:75:43:86,9,48,27:0,3:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowAaf() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t1/1:139,0,255:10:75:43:86,9,48,27:4,6:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomRefHighAaf() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t.\t.\t.\t"
				+ "GT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t0/0:139,0,255:10:75:43:86,9,48,27:6,4:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF),
				annotator.getFiltersFor(gt));
	}

}
