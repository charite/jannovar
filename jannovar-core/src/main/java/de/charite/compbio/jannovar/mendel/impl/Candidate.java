package de.charite.compbio.jannovar.mendel.impl;

import de.charite.compbio.jannovar.mendel.GenotypeCalls;

/**
 * Helper type for collecting candidate pairs of {@link GenotypeCalls} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
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