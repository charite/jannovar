package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.GenomeChange;
import jannovar.reference.TranscriptInfo;

// TODO(holtgrem): Add case for non-coding transcripts.
// TODO(holtgrem): Add deleted nucleotide base values for non-transcript parts?

/**
 * This class provides static methods to generate annotations for deletions in exons.
 *
 * <h2>Older Comment</h2>
 *
 * This class provides static methods to generate annotations for deletion mutations. Updated on 27 December 2013 to
 * provide HGVS conformation annotations for frameshirt deletion mutations. Note that if we have the following VCF line:
 *
 * <pre>
 * chr11	76895771	.	GGAGGCGGGGACACCAGGGCCTG	G	55.5	.	DP=9;VDB...
 * </pre>
 *
 * then the position refers to the nucleotide right before the deletion. That is, the first nucleotide [G]GAGGC.. (the
 * one that is enclosed in square brackets) has the position 76895771, and the deletion begins at chromosomal position
 * 76895772 and comprises 22 bases: GAG-GCG-GGG-ACA-CCA-GGG-CCT-G. (Note we are using one-based numbering here). This
 * particular deletion corresponds to NM_001127179(MYO7A_v001):c.3515_3536del
 * NM_001127179(MYO7A_i001):p.(Gly1172Glufs*34).
 *
 * @version 0.17 (14 January, 2014)
 * @author Peter N Robinson
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */

public class DeletionAnnotationBuilder {

	/**
	 * Returns a {@link Annotation} for the deletion {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @note In some cases, the position of deletions cannot be normalized/shifted since we have no sequence for the
	 *       intronic regions.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for, must describe a deletion in
	 *            <code>transcript</code>
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe an insertion
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Guard against invalid genome change.
		if (change.getRef().length() == 0 || change.getAlt().length() != 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a deletion.");

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());

		// Forward everything to the helper.
		return new DeletionAnnotationBuilderHelper(transcript, change).build();
	}

}