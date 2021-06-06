package de.charite.compbio.jannovar.reference;

/**
 * Helper for normalizing changes in amino acid sequences.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class AminoAcidChangeNormalizer {

	/** Utility class, should not be instantiated.*/
	private AminoAcidChangeNormalizer() {}

	/**
	 * Search for stop codon in <code>change.alt</code> and truncate afterwards.
	 *
	 * @param change the {@link AminoAcidChange} to normalize
	 * @return normalized {@link AminoAcidChange}
	 */
	public static AminoAcidChange truncateAltAfterStopCodon(AminoAcidChange change) {
		int stopCodonPos = change.getAlt().indexOf('*');
		if (stopCodonPos == -1)
			return change; // no stop codon found in change.alt
		return new AminoAcidChange(change.getPos(), change.getRef(), change.getAlt().substring(0, stopCodonPos + 1));
	}

	/**
	 * This shifts the amino acid and its position to be reported in the <code>proteinChange</code> HGVS annotation to the
	 * first position where the amino acids actually differ. This is necessary because
	 * {@link AminoAcidChangeNormalizer#truncateBothSides(AminoAcidChange)} does not suffice in all situations.
	 *
	 * @param change the original amino acid change to be shifted
	 * @param wtAASeq the wildtype amino acid sequence (i.e. the translated reference sequence)
	 * @param varAASeq the predicted amino acid sequence induced by the variant
	 * @return the amino acid change shifted to the first difference in the sequence, or the end of the ref/alt CDS
	 */
	public static AminoAcidChange shiftSynonymousChange(AminoAcidChange change, String wtAASeq, String varAASeq) {
	  AminoAcidChange aminoAcidChange = truncateBothSides(change);
		int position = aminoAcidChange.getPos();
		int originalPosition = position;
		int maxPosition = Math.min(
				wtAASeq.length() - aminoAcidChange.getRef().length(),
				varAASeq.length() - aminoAcidChange.getAlt().length());
		while (position < maxPosition && wtAASeq.charAt(position) != '*'
				&& wtAASeq.charAt(position) == varAASeq.charAt(position)) {
			position++;
		}
		if (position == originalPosition) {
			return aminoAcidChange;
		}
		return new AminoAcidChange(position,
				wtAASeq.substring(position, position + aminoAcidChange.getRef().length()),
				varAASeq.substring(position, position + aminoAcidChange.getAlt().length()));
	}

	/**
	 * Truncate {@link AminoAcidChange} from both sides for matching ref/alt prefixes/suffixes.
	 * <p>
	 * Truncating of the prefixes is given higher priority to conform with the HGVS notation (you have to call
	 * {@link #shiftSynonymousChange}) afterwards.
	 *
	 * @param aaChange {@link AminoAcidChange} to truncate
	 * @return updated {@link AminoAcidChange}
	 */
	private static AminoAcidChange truncateBothSides(AminoAcidChange aaChange) {

		// Truncate suffixes / from the right.
		final int REFLEN = aaChange.getRef().length() - 1;
		final int ALTLEN = aaChange.getAlt().length() - 1;
		int truncSuffix = 0;
		while (truncSuffix < aaChange.getRef().length() && truncSuffix < aaChange.getAlt().length()
			&& aaChange.getRef().charAt(REFLEN - truncSuffix) == aaChange.getAlt().charAt(ALTLEN - truncSuffix))
			++truncSuffix;
		if (truncSuffix != 0)
			aaChange = new AminoAcidChange(aaChange.getPos(),
				aaChange.getRef().substring(0, aaChange.getRef().length() - truncSuffix), aaChange.getAlt().substring(0,
				aaChange.getAlt().length() - truncSuffix));

		// Truncate prefixes / from the left.
		int truncPrefix = 0;
		while (truncPrefix < aaChange.getRef().length() && truncPrefix < aaChange.getAlt().length()
			&& aaChange.getRef().charAt(truncPrefix) == aaChange.getAlt().charAt(truncPrefix))
			++truncPrefix;
		if (truncPrefix != 0)
			aaChange = new AminoAcidChange(aaChange.getPos() + truncPrefix, aaChange.getRef().substring(truncPrefix),
				aaChange.getAlt().substring(truncPrefix));

		return aaChange;
	}

}
