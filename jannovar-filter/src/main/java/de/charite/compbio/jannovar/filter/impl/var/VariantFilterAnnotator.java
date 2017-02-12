package de.charite.compbio.jannovar.filter.impl.var;

import java.util.HashSet;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Class for pushing genotype-based filters to the variant level
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VariantFilterAnnotator {

	/** Configuration for threshold-based filter */
	private final ThresholdFilterOptions options;

	/** Names of samples of affected individuals; used for pushing information from genotype-level to variant level */
	private final ImmutableList<String> affecteds;

	public VariantFilterAnnotator(ThresholdFilterOptions options, ImmutableList<String> affecteds) {
		this.options = options;
		this.affecteds = affecteds;
	}

	/**
	 * Annotate FILTER of <code>vc</code> with genotype-based filters and based on the list of affected samples
	 * 
	 * @param builder
	 *            {@link VariantContextBuilder} to create new variant context for
	 * @param vc
	 *            {@link VariantContext} to annotate
	 * @param gts
	 *            Updated genotypes with filter annotation
	 * @return reference to <code>builder</code> after the update
	 */
	public VariantContextBuilder annotateVariant(VariantContextBuilder builder, VariantContext vc, List<Genotype> gts) {
		if (affecteds.isEmpty())
			return builder; // short-circuit, nothing to do

		HashSet<String> filters = new HashSet<String>(vc.getFilters());

		HashSet<String> unfilteredAffecteds = new HashSet<>(affecteds);
		for (Genotype gt : gts)
			if (affecteds.contains(gt.getSampleName()) && gt.isFiltered())
				unfilteredAffecteds.remove(gt.getSampleName());

		if (unfilteredAffecteds.isEmpty())
			filters.add(ThresholdFilterHeaderExtender.FILTER_VAr_ALL_AFFECTED_GTS_FILTERED);
		builder.filters(filters);
		return builder;
	}

}
