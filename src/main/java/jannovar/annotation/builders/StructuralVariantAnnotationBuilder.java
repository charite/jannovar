package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;

/**
 * Class providing static functions for creating {@link Annotation} objects for SVs.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class StructuralVariantAnnotationBuilder {

	/**
	 * Returns a {@link Annotation} for the structural variant {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for, use <code>null</code> for
	 *            intergenic variants
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for, must describe a structural variant affecting
	 *            <code>transcript</code>
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position or variant in <code>change</code>
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Project the change to the forward strand.
		change = change.withStrand('+');

		// Forward everything to the helper.
		return getStructuralVariantAnnotation(transcript, change);
	}

	private static Annotation getStructuralVariantAnnotation(TranscriptInfo transcript, GenomeChange change) {
		// Obtain shortcuts.
		GenomePosition position = change.pos.withPositionType(PositionType.ZERO_BASED);
		final int beginPos = position.pos;
		final String ref = change.ref;
		final String alt = change.alt;

		// Build reverse-complement of alt string.
		StringBuilder altRC = new StringBuilder(alt).reverse();
		for (int i = 0; i < altRC.length(); ++i)
			if (altRC.charAt(i) == 'A')
				altRC.setCharAt(i, 'T');
			else if (altRC.charAt(i) == 'T')
				altRC.setCharAt(i, 'A');
			else if (altRC.charAt(i) == 'C')
				altRC.setCharAt(i, 'G');
			else if (altRC.charAt(i) == 'G')
				altRC.setCharAt(i, 'C');

		// TODO(holtgrem): we should care about breakpoints within genes

		if (ref.length() == alt.length() && ref.equals(altRC.toString())) { // SV inversion
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%dinv", VariantType.INTERGENIC, beginPos + 1,
						beginPos + ref.length()), VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript.transcriptModel, String.format("%s:g.%d_%dinv",
						VariantType.SV_INVERSION, beginPos + 1, beginPos + ref.length()), VariantType.SV_INVERSION);

			}
		} else if (ref.length() == 0) { // SV insertion
			// if transcript is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%dins%s..%s",
						VariantType.INTERGENIC, beginPos, beginPos + 1, alt.substring(0, 2),
						alt.substring(alt.length() - 2, alt.length())), VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript.transcriptModel, String.format("%s:g.%d_%dins%s..%s",
						VariantType.SV_INSERTION, beginPos, beginPos + 1, alt.substring(0, 2),
						alt.substring(alt.length() - 2, alt.length())), VariantType.SV_INSERTION);
			}
		} else if (alt.length() == 0) { // SV deletion
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%ddel",
						VariantType.INTERGENIC, beginPos + 1, beginPos + ref.length()), VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript.transcriptModel, String.format("%s:g.%d_%ddel",
						VariantType.SV_DELETION, beginPos + 1, beginPos + ref.length()), VariantType.SV_DELETION);
			}
		} else { // SV substitution
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%ddelins%s..%s",
						VariantType.INTERGENIC, beginPos + 1, beginPos + ref.length(), alt.substring(0, 2),
						alt.substring(alt.length() - 2, alt.length())), VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript.transcriptModel, String.format("%s:g.%d_%ddelins%s..%s",
						VariantType.SV_SUBSTITUTION, beginPos + 1, beginPos + ref.length(), alt.substring(0, 2),
						alt.substring(alt.length() - 2, alt.length())), VariantType.SV_SUBSTITUTION);
			}
		}
	}
}
