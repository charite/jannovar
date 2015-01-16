package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.annotation.VariantType;
import jannovar.impl.util.StringUtil;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptModel;

// TODO(holtgrem): Mae AnnotationBuilder an interface and rename AnnotationBuilder to AnnotationBuilderBase?

/**
 * Class providing static functions for creating {@link Annotation} objects for SVs.
 *
 * This is currently not inheriting from {@link AnnotationBuilder} since it uses non of its functionality.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class StructuralVariantAnnotationBuilder {

	/** the transcript to build the annotation for */
	private final TranscriptModel transcript;
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
	public StructuralVariantAnnotationBuilder(TranscriptModel transcript, GenomeChange change) {
		this.transcript = transcript;
		this.change = change.withPositionType(PositionType.ZERO_BASED);
	}

	/**
	 * Build annotation for {@link #transcript} and {@link #change}
	 *
	 * @return {@link Annotation} for the given {@link #transcript} and {@link #change}.
	 */
	public Annotation build() {
		// Obtain shortcuts.
		GenomePosition position = change.pos;
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
				return new Annotation(VariantType.INTERGENIC, 0, StringUtil.concatenate(VariantType.INTERGENIC, ":g.",
						beginPos + 1, "_", beginPos + ref.length(), "inv"), null);
			} else {
				return new Annotation(VariantType.SV_INVERSION, beginPos, StringUtil.concatenate(
						VariantType.SV_INVERSION, ":g.", beginPos + 1, "_", beginPos + ref.length(), "inv"), transcript);

			}
		} else if (ref.length() == 0) { // SV insertion
			// if transcript is null it is intergenic
			if (transcript == null) {
				return new Annotation(VariantType.INTERGENIC, 0, StringUtil.concatenate(VariantType.INTERGENIC, ":g.",
						beginPos, "_", beginPos + 1, "ins", alt.substring(0, 2), "..",
						alt.substring(alt.length() - 2, alt.length())), null);
			} else {
				return new Annotation(VariantType.SV_INSERTION, beginPos, StringUtil.concatenate(
						VariantType.SV_INSERTION, ":g.", beginPos, "_", beginPos + 1, "ins", alt.substring(0, 2), "..",
						alt.substring(alt.length() - 2, alt.length())), transcript);
			}
		} else if (alt.length() == 0) { // SV deletion
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(VariantType.INTERGENIC, 0, StringUtil.concatenate(VariantType.INTERGENIC, ":g.",
						beginPos + 1, "_", beginPos + ref.length(), "del"), null);
			} else {
				return new Annotation(VariantType.SV_DELETION, beginPos, StringUtil.concatenate(
						VariantType.SV_DELETION, ":g.", beginPos + 1, "_", beginPos + ref.length(), "del"), null);
			}
		} else { // SV substitution
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(VariantType.INTERGENIC, 0, StringUtil.concatenate(VariantType.INTERGENIC, ":g.",
						beginPos + 1, "_", beginPos + ref.length(), "delins", alt.substring(0, 2), "..",
						alt.substring(alt.length() - 2, alt.length())), null);
			} else {
				return new Annotation(VariantType.SV_SUBSTITUTION, beginPos, StringUtil.concatenate(
						VariantType.SV_SUBSTITUTION, ":g.", beginPos + 1, "_", beginPos + ref.length(), "delins",
						alt.substring(0, 2), "..", alt.substring(alt.length() - 2, alt.length())), transcript);
			}
		}
	}
}
