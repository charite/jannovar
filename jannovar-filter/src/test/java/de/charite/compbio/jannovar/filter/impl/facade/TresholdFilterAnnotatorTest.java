package de.charite.compbio.jannovar.filter.impl.facade;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterAnnotator;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Test for ThresholdFilterAnnotator
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TresholdFilterAnnotatorTest extends TresholdFilterTestBase {

	ThresholdFilterAnnotator annotator;

	@Before
	public void setUp() {
		ArrayList<String> affected = Lists.newArrayList("individual");
		annotator = new ThresholdFilterAnnotator(ThresholdFilterOptions.buildDefaultOptions(), affected);
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

		Assert.assertEquals("[AllAffGtFiltered]", updatedVC.getFilters().toString());
		Assert.assertEquals(
				"[VC Unknown @ 1:17452 Q35.74 of type=SNP alleles=[G*, A] attr={} "
						+ "GT=[[individual G*/A GQ 10 DP 5 AD 4,0 PL 63,6,0 FT MinAafHet;MinCovHet;MinGq]]",
				updatedVC.toString());
	}

}
