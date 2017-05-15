package de.charite.compbio.jannovar.filter.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.filter.impl.gt.GenotypeFilterAnnotator;
import de.charite.compbio.jannovar.filter.impl.var.VariantFilterAnnotator;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Perform annotation (sof-filtering) based on coverage/alternative allele
 * fraction/genotype call quality
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ThresholdFilterAnnotator {

	/** Configuration for the threshold-based filter */
	private final ThresholdFilterOptions options;

	/**
	 * Names of samples of affected individuals; used for pushing information
	 * from genotype-level to variant level
	 */
	private final ImmutableList<String> affecteds;

	/** Helper for genotype-wide application of filters */
	private final GenotypeFilterAnnotator gtAnnotator;

	/** Helper for pushing genotype-based filters to variant-wise ones */
	private final VariantFilterAnnotator varAnnotator;

	public ThresholdFilterAnnotator(ThresholdFilterOptions options, Collection<String> affected) {
		this.options = options;
		this.affecteds = ImmutableList.copyOf(affected);
		this.gtAnnotator = new GenotypeFilterAnnotator(this.options);
		this.varAnnotator = new VariantFilterAnnotator(this.options, this.affecteds);
	}

	/**
	 * Annotate VariantContext with the threshold-based filters
	 *
	 * @param vc
	 *            the {@link VariantContext} to annotate
	 * @return copy of <code>vc</code> with applied annotations
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);
		List<Genotype> gts = annotateGenotypes(builder, vc);
		varAnnotator.annotateVariant(builder, vc, gts);
		return builder.make();
	}

	/**
	 * Annotate <code>vc</code> and return modified object
	 * 
	 * @param builder
	 *            the {@link VariantContextBuilder} used for building variant
	 * @param vc
	 *            the {@link VariantContext} to base builder configuration upon
	 * @param affected
	 *            names of the samples from affected individuals
	 * @return modified <code>vc</code> object
	 */
	VariantContextBuilder annotate(VariantContextBuilder builder, VariantContext vc) {
		List<Genotype> gts = annotateGenotypes(builder, vc);
		varAnnotator.annotateVariant(builder, vc, gts);
		return builder;
	}

	/**
	 * Annotate genotypes individually in <code>vc</code>
	 * 
	 * @param builder
	 *            the {@link VariantContextBuilder} used for building variant
	 * @param vc
	 *            {@link VariantContext} to annotate
	 * @return {@link List} of genotypes after annotation
	 */
	private List<Genotype> annotateGenotypes(VariantContextBuilder builder, VariantContext vc) {
		ArrayList<Genotype> gts = new ArrayList<>();
		for (Genotype gt : vc.getGenotypes())
			gts.add(gtAnnotator.gtWithAppliedFilters(gt));
		builder.genotypes(gts);
		return gts;
	}

}
