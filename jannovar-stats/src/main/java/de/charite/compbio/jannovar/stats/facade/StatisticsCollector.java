package de.charite.compbio.jannovar.stats.facade;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Facade class for collecting statistics from a {@link VariantContext} and a list of {@link VariantAnnotations}
 * objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class StatisticsCollector {

	/** Sample names */
	ImmutableList<String> sampleNames;

	/** Per-sample statistics, <code>null</code> used for VCFs without samples */
	private Map<String, Statistics> perSampleStats;

	public StatisticsCollector(Collection<String> sampleNames) {
		this.sampleNames = ImmutableList.copyOf(sampleNames);
		perSampleStats = new HashMap<>();
		for (String name : sampleNames)
			perSampleStats.put(name, new Statistics());
	}

	/**
	 * Register {@link VariantContext} and allele {@link VariantAnnotations}
	 * 
	 * @param vc
	 *            {@link VariantContext} to register for
	 * @param alleleAnnotations
	 *            {@link VariantAnnotations} objects for each allele in <code>vc</code>
	 */
	public void put(VariantContext vc, Collection<VariantAnnotations> alleleAnnotations) {
	}

	public ImmutableList<String> getSampleNames() {
		return sampleNames;
	}

	public Map<String, Statistics> getPerSampleStats() {
		return perSampleStats;
	}

}
