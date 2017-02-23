package de.charite.compbio.jannovar.filter.impl.gt;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;

/**
 * Facade class for performing annotation on a genotype-wide level
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenotypeFilterAnnotator {

	/** Mapping from supported variant caller to the actual genotype filter implementation */
	private final ImmutableMap<SupportedVarCaller, GenotypeFilterImpl> impls;

	/** Threshold based filter configuration */
	private final ThresholdFilterOptions options;

	public GenotypeFilterAnnotator(ThresholdFilterOptions options) {
		this.options = options;

		ImmutableMap.Builder<SupportedVarCaller, GenotypeFilterImpl> builder = ImmutableMap.builder();
		builder.put(SupportedVarCaller.GATK_CALLER, new GatkGenotypeFilterImpl());
		builder.put(SupportedVarCaller.BCFTOOLS, new BcftoolsGenotypeFilterImpl());
		builder.put(SupportedVarCaller.FREEBAYES, new FreebayesGenotypeFilterImpl());
		builder.put(SupportedVarCaller.PLATYPUS, new PlatypusGenotypeFilterImpl());
		impls = builder.build();
	}

	/** Augment genotype with the given filters and return modified GenotypeBuilder */
	public Genotype gtWithAppliedFilters(Genotype gt) {
		GenotypeBuilder gtBuilder = new GenotypeBuilder(gt);

		ArrayList<String> filters = new ArrayList<>();
		if (gt.isFiltered())
			filters.add(gt.getFilters());
		filters.addAll(getFiltersFor(gt));
		gtBuilder.filters(filters);

		return gtBuilder.make();
	}

	public ImmutableList<String> getFiltersFor(Genotype gt) {
		GenotypeFilterImpl impl = impls.get(SupportedVarCaller.guessFromGenotype(gt));
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		checkCoverage(gt, builder, impl);
		checkGenotypeQuality(gt, builder, impl);
		checkAaf(gt, builder, impl);
		return builder.build();
	}

	private void checkCoverage(Genotype gt, Builder<String> builder, GenotypeFilterImpl impl) {
		if (gt.isHet()) {
			if (impl.getCoverage(gt) < options.getMinGtCovHet())
				builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HET);
		} else if (!gt.isHomRef()) {
			if (impl.getCoverage(gt) < options.getMinGtCovHomAlt())
				builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MIN_COV_HOM_ALT);
		}
		if (impl.getCoverage(gt) > options.getMaxCov())
			builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MAX_COV);
	}

	private void checkGenotypeQuality(Genotype gt, Builder<String> builder, GenotypeFilterImpl impl) {
		if (impl.getGenotypeQuality(gt) < options.getMinGtGq())
			builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MIN_GQ);
	}

	private void checkAaf(Genotype gt, Builder<String> builder, GenotypeFilterImpl impl) {
		if (gt.isHet()) {
			if (impl.getAlternativeAlleleFraction(gt) < options.getMinGtAafHet())
				builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HET);
			if (impl.getAlternativeAlleleFraction(gt) > options.getMaxGtAafHet())
				builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HET);
		} else if (gt.isHomRef()) {
			if (impl.getAlternativeAlleleFraction(gt) > options.getMaxGtAafHomRef())
				builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MAX_AAF_HOM_REF);
		} else {
			if (impl.getAlternativeAlleleFraction(gt) < options.getMinGtAafHomAlt())
				builder.add(ThresholdFilterHeaderExtender.FILTER_GT_MIN_AAF_HOM_ALT);
		}
	}

}
