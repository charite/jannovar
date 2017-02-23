package de.charite.compbio.jannovar.filter.impl.gt;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Tests for GenotypeFilterAnnotator augmenting of Genotypes
 * 
 * It's sufficient to test this for GATK variant files only
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenotypeFilterAnnotatorAugmentGenotypeTest extends GenotypeFilterTestBase {

	GenotypeFilterAnnotator annotator;

	@Before
	public void setUp() {
		annotator = new GenotypeFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions());
	}

	@Test
	public void testHetLowCoverageLowQualityLowAaaf() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:4,0:5:10:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals("[individual G*/A GQ 10 DP 5 AD 4,0 PL 63,6,0 FT MinAafHet;MinCovHet;MinGq]",
				annotator.gtWithAppliedFilters(gt).toString());
	}

}
