package de.charite.compbio.jannovar.filter.facade;

import de.charite.compbio.jannovar.filter.impl.gt.GenotypeFilterAnnotator;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;

/**
 * Perform annotation (sof-filtering) based on coverage/alternative allele fraction/genotype call
 * quality
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenotypeThresholdFilterAnnotator {

	/** Configuration for the threshold-based filter */
	private final ThresholdFilterOptions options;

	/** Helper for genotype-wide application of filters */
	private final GenotypeFilterAnnotator gtAnnotator;

	public GenotypeThresholdFilterAnnotator(ThresholdFilterOptions options) {
		this.options = options;
		this.gtAnnotator = new GenotypeFilterAnnotator(this.options);
	}

	/**
	 * Annotate VariantContext with the threshold-based filters.
	 *
	 * @param vc the {@link VariantContext} to annotate
	 * @return copy of <code>vc</code> with applied annotations
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);
		annotateGenotypes(builder, vc);
		return builder.make();
	}

	/**
	 * Annotate genotypes individually in <code>vc</code>
	 * 
	 * @param builder the {@link VariantContextBuilder} used for building variant
	 * @param vc {@link VariantContext} to annotate
	 */
	private void annotateGenotypes(VariantContextBuilder builder, VariantContext vc) {
		ArrayList<Genotype> gts = new ArrayList<>();
		for (Genotype gt : vc.getGenotypes())
			gts.add(gtAnnotator.gtWithAppliedFilters(gt));
		builder.genotypes(gts);
	}

}
