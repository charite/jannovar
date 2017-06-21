package de.charite.compbio.jannovar.filter.impl.var;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class for pushing genotype-based filters to the variant level, also consider population
 * frequencies.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VariantThresholdFilterAnnotator {

	/** Configuration for threshold-based filter */
	private final ThresholdFilterOptions options;

	/**
	 * Names of samples of affected individuals; used for pushing information from genotype-level to
	 * variant level
	 */
	private final ImmutableList<String> affecteds;

	public VariantThresholdFilterAnnotator(ThresholdFilterOptions options,
			List<String> affecteds) {
		this.options = options;
		this.affecteds = ImmutableList.copyOf(affecteds);
	}

	/**
	 * Annotate FILTER of <code>vc</code> with genotype-based filters and based on the list of
	 * affected samples
	 * 
	 * @param builder {@link VariantContextBuilder} to create new variant context for
	 * @param vc {@link VariantContext} to annotate
	 * @return reference to <code>builder</code> after the update
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);
		
		// If all genotype calls are filtered out then add filter to variant-level FILTER column
		HashSet<String> filters = new HashSet<String>(vc.getFilters());
		if (!affecteds.isEmpty()) {
			HashSet<String> unfilteredAffecteds = new HashSet<>(affecteds);
			for (Genotype gt : vc.getGenotypes())
				if (affecteds.contains(gt.getSampleName()) && gt.isFiltered())
					unfilteredAffecteds.remove(gt.getSampleName());
			if (unfilteredAffecteds.isEmpty())
				filters.add(ThresholdFilterHeaderExtender.FILTER_VAR_ALL_AFFECTED_GTS_FILTERED);
		}

		// Check best frequency from EXAC
		final String keyExacBestAf = options.getExacPrefix() + "BEST_AF";
		@SuppressWarnings("unchecked")
		final ArrayList<Double> exacBestAfs = ((ArrayList<Double>) vc.getAttribute(keyExacBestAf));
		final double exacBestAf = (exacBestAfs == null) ? -1 : Collections.max(exacBestAfs);
		// Check best frequency from dbSNP
		final String keyDbSnpCaf = options.getDbSnpPrefix() + "CAF";
		@SuppressWarnings("unchecked")
		final ArrayList<Double> dbSnpCaf = ((ArrayList<Double>) vc.getAttribute(keyDbSnpCaf));
		double dbSnpBestAf;
		try {
			dbSnpBestAf =
					(dbSnpCaf == null) ? -1 : Collections.max(dbSnpCaf.subList(1, dbSnpCaf.size()));
		} catch (NoSuchElementException e) {
			dbSnpBestAf = 0;
		}
		// Check best frequency from gnomAD genomes
		final String keyGnomAdGenomesAfPopmax = options.getGnomAdGenomesPrefix() + "AF_POPMAX";
		@SuppressWarnings("unchecked")
		final ArrayList<Double> gnomadGenomesAfs =
				((ArrayList<Double>) vc.getAttribute(keyGnomAdGenomesAfPopmax));
		final double gnomAdGenomesAf =
				(gnomadGenomesAfs == null) ? -1 : Collections.max(gnomadGenomesAfs);
		// Check best frequency from gnomAD exomes
		final String keyGnomAdExomesAfPopmax = options.getGnomAdExomesPrefix() + "AF_POPMAX";
		@SuppressWarnings("unchecked")
		final ArrayList<Double> gnomadExomesAfs =
				((ArrayList<Double>) vc.getAttribute(keyGnomAdExomesAfPopmax));
		final double gnomAdExomesAf =
				(gnomadExomesAfs == null) ? -1 : Collections.max(gnomadExomesAfs);
		// Get maximum of all frequencies
		final double highestAf = Collections
				.max(ImmutableList.of(exacBestAf, dbSnpBestAf, gnomAdGenomesAf, gnomAdExomesAf));
		if (highestAf > 0) {
			if (highestAf > options.getMaxAlleleFrequencyAd())
				filters.add(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_FREQUENCY_AD);
			if (highestAf > options.getMaxAlleleFrequencyAr())
				filters.add(ThresholdFilterHeaderExtender.FILTER_VAR_MAX_FREQUENCY_AR);
		}

		builder.filters(filters);
		return builder.make();
	}

}
