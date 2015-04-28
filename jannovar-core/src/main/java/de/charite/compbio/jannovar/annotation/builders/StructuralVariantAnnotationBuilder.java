package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation.RankType;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.impl.util.StringUtil;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

// TODO(holtgrem): Make AnnotationBuilder an interface and rename AnnotationBuilder to AnnotationBuilderBase?

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
	private final GenomeVariant change;

	/**
	 * Initialize the builder for the structural variant {@link GenomeVariant} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for, use <code>null</code> for
	 *            intergenic variants
	 * @param change
	 *            {@link GenomeVariant} to compute the annotation for, must describe a structural variant affecting
	 *            <code>transcript</code>
	 */
	public StructuralVariantAnnotationBuilder(TranscriptModel transcript, GenomeVariant change) {
		this.transcript = transcript;
		this.change = change;
	}

	/**
	 * Build annotation for {@link #transcript} and {@link #change}
	 *
	 * @return {@link Annotation} for the given {@link #transcript} and {@link #change}.
	 */
	public Annotation build() {
		// Obtain shortcuts.
		GenomePosition position = change.getGenomePos();
		final int beginPos = position.getPos();
		final String ref = change.getRef();
		final String alt = change.getAlt();

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

		final AnnotationLocation annoLoc = new AnnotationLocation(null, RankType.UNDEFINED,
				AnnotationLocation.INVALID_RANK, AnnotationLocation.INVALID_RANK, null);

		if (ref.length() == alt.length() && ref.equals(altRC.toString())) { // SV inversion
			if (transcript == null) {
				return new Annotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, StringUtil.concatenate("g.", beginPos + 1, "_", beginPos
								+ ref.length(), "inv"), null);
			} else {
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT), annoLoc,
						StringUtil.concatenate("g.", beginPos + 1, "_", beginPos + ref.length(), "inv"), null);

			}
		} else if (ref.length() == 0) { // SV insertion
			// if transcript is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, StringUtil.concatenate("g.", beginPos, "_",
								beginPos + 1, "ins", alt.substring(0, 2), "..", alt.substring(alt.length() - 2, alt.length())),
								null);
			} else {
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT), annoLoc,
						StringUtil.concatenate("g.", beginPos, "_", beginPos + 1, "ins", alt.substring(0, 2), "..",
								alt.substring(alt.length() - 2, alt.length())), null);
			}
		} else if (alt.length() == 0) { // SV deletion
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, StringUtil.concatenate("g.", beginPos + 1, "_", beginPos
								+ ref.length(), "del"), null);
			} else {
				return new Annotation(this.transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT),
						annoLoc,
						StringUtil.concatenate("g.", beginPos + 1, "_", beginPos + ref.length(), "del"), null);
			}
		} else { // SV substitution
			// if tm is null it is intergenic
			if (transcript == null) {
				return new Annotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, StringUtil.concatenate("g.", beginPos + 1, "_", beginPos
								+ ref.length(), "delins", alt.substring(0, 2), "..",
								alt.substring(alt.length() - 2, alt.length())), null);
			} else {
				return new Annotation(transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT), annoLoc,
						StringUtil.concatenate("g.", beginPos + 1, "_", beginPos + ref.length(), "delins",
								alt.substring(0, 2), "..", alt.substring(alt.length() - 2, alt.length())), null);
			}
		}
	}
}
