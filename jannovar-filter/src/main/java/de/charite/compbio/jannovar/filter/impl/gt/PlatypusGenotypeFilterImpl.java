package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Implementation of genotype filter application for Platypus
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PlatypusGenotypeFilterImpl implements GenotypeFilterImpl {

	@Override
	public int getCoverage(Genotype gt) {
		int numRef = Integer.parseInt((String) gt.getExtendedAttribute("NR"));
		String arr[] = ((String) gt.getExtendedAttribute("NV")).split(",");
		int sum = numRef;
		for (int i = 0; i < arr.length; ++i)
			sum += Integer.parseInt(arr[i]);
		return sum;
	}

	@Override
	public int getGenotypeQuality(Genotype gt) {
		return gt.getGQ();
	}

	@Override
	public double getAlternativeAlleleFraction(Genotype gt) {
		int numRef = Integer.parseInt((String) gt.getExtendedAttribute("NR"));
		String arr[] = ((String) gt.getExtendedAttribute("NV")).split(",");
		int sumAlt = 0;
		for (int i = 0; i < arr.length; ++i)
			sumAlt += Integer.parseInt(arr[i]);
		return ((double) sumAlt) / (sumAlt + numRef);
	}

	@Override
	public double getAlleleFraction(Genotype gt, int alleleNo) {
		int numRef = Integer.parseInt((String) gt.getExtendedAttribute("NR"));
		String arr[] = ((String) gt.getExtendedAttribute("NV")).split(",");
		int intArr[] = new int[arr.length + 1];
		intArr[0] = numRef;
		for (int i = 1; i < intArr.length - 1; ++i)
			intArr[i] = Integer.parseInt(arr[i - 1]);
		int sum = 0;
		for (int i = 0; i < intArr.length; ++i)
			sum += intArr[i];
		return ((double) intArr[alleleNo]) / sum;
	}

}
