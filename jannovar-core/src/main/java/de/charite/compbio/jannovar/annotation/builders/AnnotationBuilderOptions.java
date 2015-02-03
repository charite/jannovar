package de.charite.compbio.jannovar.annotation.builders;

/**
 * Configuration for the {@link AnnotationBuilder} subclasses.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AnnotationBuilderOptions {
	/** whether or not to shift variants towards the 3' end of the transcript (default is <code>true</code>) */
	public final boolean nt3PrimeShifting;

	public AnnotationBuilderOptions() {
		this.nt3PrimeShifting = true;
	}

	public AnnotationBuilderOptions(boolean nt3PrimeShifting) {
		this.nt3PrimeShifting = nt3PrimeShifting;
	}
}