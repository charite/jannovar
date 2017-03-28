package de.charite.compbio.jannovar.mendel.impl;

import de.charite.compbio.jannovar.mendel.GenotypeCalls;

/**
 * Helper type for collecting candidate pairs of {@link GenotypeCalls} objects
 * The paternal list of genotypes refers to the genotypes in all pedigree members for a variant that is heterozygous in an affected
 * person and either HET or NOCALL in the father and HOM-REF or NOCALL in the mother, and analogously for maternal.
 * Together, the variants referred to by maternal and paternal represent a candidate compound heterozygous pair of variants that
 * were found in one trio. Subsequent code will determine whether the candidate pair is compatible with AR compoound-het
 * inheritance across the entire pedigree.
 * 
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
class Candidate {

	/** one VCF record compatible with mutation in father */
	private final GenotypeCalls paternal;
	/** one VCF record compatible with mutation in mother */
	private final GenotypeCalls maternal;

	public Candidate(GenotypeCalls paternal, GenotypeCalls maternal) {
		this.paternal = paternal;
		this.maternal = maternal;
	}

	/**
	 * @return one VCF record compatible with mutation in father
	 */
	public GenotypeCalls getPaternal() {
		return paternal;
	}

	/**
	 * @return one VCF record compatible with mutation in mother
	 */
	public GenotypeCalls getMaternal() {
		return maternal;
	}

}
