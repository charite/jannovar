package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Implementation of genotype filter application for GATK UG and GATK HC
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class BcftoolsGenotypeFilterImpl implements GenotypeFilterImpl {

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
		String strValue = (String) gt.getExtendedAttribute("DPR");
		String[] arr = strValue.split(",");
		return (getCoverage(gt) - Integer.parseInt(arr[0])) / (double) getCoverage(gt);
	}

	@Override
	public double getAlleleFraction(Genotype gt, int alleleNo) {
		String strValue = (String) gt.getExtendedAttribute("DPR");
		String[] arr = strValue.split(",");
		double countAllele = Integer.parseInt(arr[alleleNo]);
		return countAllele / (double) getCoverage(gt);
	}

}
