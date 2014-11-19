package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.Chromosome;
import jannovar.reference.GenomeChange;
import jannovar.reference.TranscriptInfo;

/**
 * This class is intended to provide a static method to generate annotations for block substitution mutations. This
 * method is put in its own class only for convenience and to at least have a name that is easy to find.
 *
 * Block substitutions are recognized in the calling class {@link Chromosome} by the fact that the length of the variant
 * sequence is greater than 1.
 *
 * @version 0.08 (22 April, 2014)
 * @author Peter N Robinson
 * @author Marten Jäger
 */
public class BlockSubstitutionAnnotationBuilder {

	/**
	 * Returns a {@link Annotation} for the block substitution {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for, must describe a block substitution in
	 *            <code>transcript</code>
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe a block substitution
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Guard against invalid genome change.
		if (change.getRef().length() == 0 || change.getAlt().length() == 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a block substitution.");

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());

		// Forward everything to the helper.
		return new BlockSubstitutionAnnotationBuilderHelper(transcript, change).build();
	}

}
