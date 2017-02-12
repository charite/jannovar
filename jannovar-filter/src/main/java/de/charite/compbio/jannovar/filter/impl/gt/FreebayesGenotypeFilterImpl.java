package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Implementation of genotype filter application for Freebayes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FreebayesGenotypeFilterImpl implements GenotypeFilterImpl {

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
		String refObsStr = (String) gt.getExtendedAttribute("RO");
		String altObsStr = (String) gt.getExtendedAttribute("AO");
		int refObs = Integer.parseInt(refObsStr);
		int altObs = Integer.parseInt(altObsStr);
		return ((double) altObs) / (refObs + altObs);
	}

	/**
	 * Note that the result is not exact as Freebayes does not report the number of observations for each allele
	 */
	@Override
	public double getAlleleFraction(Genotype gt, int alleleNo) {
		String refObsStr = (String) gt.getExtendedAttribute("RO");
		String altObsStr = (String) gt.getExtendedAttribute("AO");
		int refObs = Integer.parseInt(refObsStr);
		int altObs = Integer.parseInt(altObsStr);
		return ((double) altObs) / (refObs + altObs);
	}

}
