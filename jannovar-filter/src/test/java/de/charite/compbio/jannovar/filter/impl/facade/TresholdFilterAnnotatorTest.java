package de.charite.compbio.jannovar.filter.impl.facade;

import de.charite.compbio.jannovar.filter.facade.GenotypeThresholdFilterAnnotator;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.VariantContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for ThresholdFilterAnnotator
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TresholdFilterAnnotatorTest extends TresholdFilterTestBase {

	GenotypeThresholdFilterAnnotator annotator;

	@Before
	public void setUp() {
		annotator =
				new GenotypeThresholdFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions());
	}

	/**
	 * Test that all genotypes are annotated and genotype filters are pulled into the variant filter
	 */
	@Test
	public void testAnnotateVariant() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t.\tGT:AD:DP:GQ:PL\t0/1:4,0:5:10:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);

		VariantContext updatedVC = annotator.annotateVariantContext(variant);

		Assert.assertEquals("[]", updatedVC.getFilters().toString());
		Assert.assertEquals(
				"[VC Unknown @ 1:17452 Q35.74 of type=SNP alleles=[G*, A] attr={} "
						+ "GT=[[individual G*/A GQ 10 DP 5 AD 4,0 PL 63,6,0 FT MinAafHet;MinCovHet;MinGq]]",
				updatedVC.toString());
	}

}
