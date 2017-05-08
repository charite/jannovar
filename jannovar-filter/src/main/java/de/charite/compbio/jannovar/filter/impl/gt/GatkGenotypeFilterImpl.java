package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Implementation of genotype filter application for Freebayes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GatkGenotypeFilterImpl implements GenotypeFilterImpl {

	@Override
	public int getCoverage(Genotype gt) {
		return gt.getDP();
	}

	@Override
	public int getGenotypeQuality(Genotype gt) {
		return gt.getGQ();
	}

	@Override
	public double getAlternativeAlleleFraction(Genotype gt) {
		int[] ads = gt.getAD();
		if (ads == null)
			return 0.0;
		int sum = 0;
		for (int i = 1; i < ads.length; ++i)
			sum += ads[i];
		return ((double) sum) / gt.getDP();
	}

	@Override
	public double getAlleleFraction(Genotype gt, int alleleNo) {
		if (gt.getDP() == 0)
			return 0.0;
		else
			return ((double) gt.getAD()[alleleNo]) / gt.getDP();
	}

}
