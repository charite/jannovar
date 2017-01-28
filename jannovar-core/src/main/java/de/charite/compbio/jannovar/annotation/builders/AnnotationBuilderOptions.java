package de.charite.compbio.jannovar.annotation.builders;

import de.charite.compbio.jannovar.reference.GenomeVariant;

/**
 * Configuration for the {@link AnnotationBuilder} subclasses.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class AnnotationBuilderOptions {
	/**
	 * whether or not to shift variants towards the 3' end of the transcript
	 * (default is <code>true</code>)
	 */
	private final boolean nt3PrimeShifting;

	/**
	 * whether or not to override transcript sequence with user input, that is
	 * the sequence in the {@link GenomeVariant} (default is
	 * <code>false</code>).
	 */
	private final boolean overrideTxSeqWithGenomeVariantRef;

	public AnnotationBuilderOptions() {
		this.nt3PrimeShifting = true;
		this.overrideTxSeqWithGenomeVariantRef = false;
	}

	public AnnotationBuilderOptions(boolean nt3PrimeShifting, boolean overrideTxSeqWithGenomeVariantRef) {
		this.nt3PrimeShifting = nt3PrimeShifting;
		this.overrideTxSeqWithGenomeVariantRef = overrideTxSeqWithGenomeVariantRef;
	}

	/**
	 * @return whether or not to shift variants towards the 3' end of the
	 *         transcript (default is <code>true</code>)
	 */
	public boolean isNt3PrimeShifting() {
		return nt3PrimeShifting;
	}

	/**
	 * @return whether or not to override transcript sequence with user input,
	 *         that is the sequence in the {@link GenomeVariant}
	 */
	public boolean isOverrideTxSeqWithGenomeVariantRef() {
		return overrideTxSeqWithGenomeVariantRef;
	}

}