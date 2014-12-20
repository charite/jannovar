package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.annotation.VariantType;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;

/**
 * Class providing static functions for creating {@link Annotation} objects for SVs.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class StructuralVariantAnnotationBuilder {

	/** the transcript to build the annotation for */
	private final TranscriptInfo transcript;
	/** the genome change to build the annotation for */
	private final GenomeChange change;

	/**
	 * Initialize the builder for the structural variant {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for, use <code>null</code> for
	 *            intergenic variants
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for, must describe a structural variant affecting
	 *            <code>transcript</code>
	 */
	public StructuralVariantAnnotationBuilder(TranscriptInfo transcript, GenomeChange change) {
		this.transcript = transcript;
		this.change = change;
	}

	public Annotation build() {
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
				return new Annotation(transcript, String.format("%s:g.%d_%dinv", VariantType.SV_INVERSION,
						beginPos + 1, beginPos + ref.length()), VariantType.SV_INVERSION);

			}
		} else if (ref.length() == 0) { // SV insertion
			// if transcript is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%dins%s..%s", VariantType.INTERGENIC, beginPos,
						beginPos + 1, alt.substring(0, 2), alt.substring(alt.length() - 2, alt.length())),
						VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript, String.format("%s:g.%d_%dins%s..%s", VariantType.SV_INSERTION,
						beginPos, beginPos + 1, alt.substring(0, 2), alt.substring(alt.length() - 2, alt.length())),
						VariantType.SV_INSERTION);
			}
		} else if (alt.length() == 0) { // SV deletion
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%ddel", VariantType.INTERGENIC, beginPos + 1,
						beginPos + ref.length()), VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript, String.format("%s:g.%d_%ddel", VariantType.SV_DELETION, beginPos + 1,
						beginPos + ref.length()), VariantType.SV_DELETION);
			}
		} else { // SV substitution
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, String.format("%s:g.%d_%ddelins%s..%s", VariantType.INTERGENIC,
						beginPos + 1, beginPos + ref.length(), alt.substring(0, 2),
						alt.substring(alt.length() - 2, alt.length())), VariantType.INTERGENIC);
			} else {
				return new Annotation(transcript, String.format("%s:g.%d_%ddelins%s..%s", VariantType.SV_SUBSTITUTION,
						beginPos + 1, beginPos + ref.length(), alt.substring(0, 2),
						alt.substring(alt.length() - 2, alt.length())), VariantType.SV_SUBSTITUTION);
			}
		}
	}
}
