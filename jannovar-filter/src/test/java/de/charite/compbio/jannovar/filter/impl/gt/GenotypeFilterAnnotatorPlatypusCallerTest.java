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
public class GenotypeFilterAnnotatorPlatypusCallerTest extends GenotypeFilterTestBase {

	GenotypeFilterAnnotator annotator;

	@Before
	public void setUp() {
		annotator = new GenotypeFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions());
	}

	@Test
	public void testHighCoverage() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\t"
				+ "GT:GL:GOF:GQ:NR:NV\t0/1:-14.5,-0.9,0.0:29.0:30:20001:10000\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowGq() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t0/1:-14.5,-0.9,0.0:29.0:19:10:5\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetHighAaf() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t0/1:-14.5,-0.9,0.0:29.0:30:10:9\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowAaf() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t0/1:-14.5,-0.9,0.0:29.0:30:9:1\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHetLowCoverage() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t0/1:-14.5,-0.9,0.0:29.0:30:7:4\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowCoverage() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t1/1:-14.5,-0.9,0.0:29.0:30:0:3\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomAltLowAaf() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t1/1:-14.5,-0.9,0.0:29.0:30:10:6\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT),
				annotator.getFiltersFor(gt));
	}

	@Test
	public void testHomRefHighAaf() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\t.\tGT:GL:GOF:GQ:NR:NV\t0/0:-14.5,-0.9,0.0:29.0:30:10:4\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(ImmutableList.of(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF),
				annotator.getFiltersFor(gt));
	}

}
