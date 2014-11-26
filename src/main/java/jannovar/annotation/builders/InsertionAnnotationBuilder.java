package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.GenomeChange;
import jannovar.reference.TranscriptInfo;

/**
 * This class provides static methods to generate annotations for insertions in exons.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jäger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InsertionAnnotationBuilder {

	// TODO(holtgrew): Should also forward for splice/intro/utr changes and not throw.

	/**
	 * Returns a {@link Annotation} for the insertion {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * <h2>Duplications</h2>
	 *
	 * In the case of insertions that are duplications are annotated as such. These insertions must be in the coding
	 * region, such that the duplication can be recognized from the transcript sequence.
	 *
	 * <h2>Shifting of Insertions</h2>
	 *
	 * In the case of ambiguities, the HGVS specification requires the variant to be shifted towards the 3' end of the
	 * transcript ("rightmost" position). This can cause an insertion to be shifted into the 3' UTR or a splice site.
	 * The function detects this and forwards to the {@link UTRAnnotationBuilder}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe an insertion
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Guard against invalid genome change.
		if (change.ref.length() != 0 || change.alt.length() == 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe an insertion.");

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());

		// Forward everything to the helper.
		return new InsertionAnnotationBuilderHelper(transcript, change).build();
	}

}
