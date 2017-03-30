package de.charite.compbio.jannovar.stats.facade;

import java.util.Collection;

import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Facade class for collecting statistics from a {@link VariantContext} and a list of {@link VariantAnnotations}
 * objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class StatisticsCollector {

	public StatisticsCollector() {
		throw new RuntimeException("Implement me!");
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
		throw new RuntimeException("Implement me!");
	}

}
