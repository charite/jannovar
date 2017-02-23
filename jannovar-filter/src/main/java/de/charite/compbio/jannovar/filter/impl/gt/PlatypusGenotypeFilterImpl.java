package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Implementation of genotype filter application for Platypus
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PlatypusGenotypeFilterImpl implements GenotypeFilterImpl {
	// TODO: this needs updates, see https://github.com/andyrimmer/Platypus/issues/61

	@Override
	public int getCoverage(Genotype gt) {
		return Integer.parseInt(((String) gt.getExtendedAttribute("NR")).split(",")[0]);
	}

	@Override
	public int getGenotypeQuality(Genotype gt) {
		return gt.getGQ();
	}

	@Override
	public double getAlternativeAlleleFraction(Genotype gt) {
		int coverage = getCoverage(gt);
		int numVar = Integer.parseInt(((String) gt.getExtendedAttribute("NV")).split(",")[0]);
		return ((double) numVar) / coverage;
	}

	@Override
	public double getAlleleFraction(Genotype gt, int alleleNo) {
		int coverage = getCoverage(gt);
		int numVar = Integer.parseInt(((String) gt.getExtendedAttribute("NV")).split(",")[0]);
		return ((double) numVar) / coverage;
	}

}
