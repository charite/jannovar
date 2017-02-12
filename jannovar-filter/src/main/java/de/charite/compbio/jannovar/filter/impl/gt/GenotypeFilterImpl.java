package de.charite.compbio.jannovar.filter.impl.gt;

import htsjdk.variant.variantcontext.Genotype;

/**
 * Interface for tool-specific extraction from depth of coverage, genotype quality, and alternative allele fraction
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface GenotypeFilterImpl {

	/** @return coverage at the genotype call */
	public int getCoverage(Genotype gt);

	/** @return genotype PHRED-scale genotype call */
	public int getGenotypeQuality(Genotype gt);

	/** @return overall alternative allele fraction */
	public double getAlternativeAlleleFraction(Genotype gt);

	/** @return allele fraction for the given <code>alleleNo</code> (0 is reference, 1.. alternative alleles) */
	public double getAlleleFraction(Genotype gt, int alleleNo);

}
