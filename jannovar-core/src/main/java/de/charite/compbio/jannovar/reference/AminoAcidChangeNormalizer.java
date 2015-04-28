package de.charite.compbio.jannovar.reference;

/**
 * Helper for normalizing changes in amino acid sequences.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class AminoAcidChangeNormalizer {

	/**
	 * Search for stop codon in <code>change.alt</code> and truncate afterwards.
	 *
	 * @param change
	 *            the {@link AminoAcidChange} to normalize
	 * @return normalized {@link AminoAcidChange}
	 */
	public static AminoAcidChange truncateAltAfterStopCodon(AminoAcidChange change) {
		int stopCodonPos = change.getAlt().indexOf('*');
		if (stopCodonPos == -1)
			return change; // no stop codon found in change.alt
		return new AminoAcidChange(change.getPos(), change.getRef(), change.getAlt().substring(0, stopCodonPos + 1));
	}

	/**
	 * Normalize deletion {@link AminoAcidChange} for amino acid string
	 *
	 * Return <code>change</code> if it is not a clean deletion.
	 *
	 * @param ref
	 *            reference amino acid string to change
	 * @param change
	 *            the {@link AminoAcidChange} to normalize
	 * @return normalized AminoAcidChange
	 */
	public static AminoAcidChange normalizeDeletion(String ref, AminoAcidChange change) {
		if (change.getRef().length() == 0 || change.getAlt().length() != 0)
			return change;

		// Compute shift of deletion.
		int shift = 0;
		final int LEN = change.getRef().length();
		while (change.getPos() + LEN + shift < ref.length()
				&& ref.charAt(change.getPos()) == ref.charAt(change.getPos() + LEN + shift))
			shift += 1;
		if (shift == 0)
			return change;

		// Build new AminoAcidChange.
		StringBuilder changeRefBuilder = new StringBuilder();
		changeRefBuilder.append(ref.substring(change.getPos() + shift, change.getPos() + shift + change.getRef().length()));
		return new AminoAcidChange(change.getPos() + shift, changeRefBuilder.toString(), "");
	}

	/**
	 * Truncate {@link AminoAcidChange} from both sides for matching ref/alt prefixes/suffixes.
	 *
	 * Truncating of the prefixes is given higher priority to conform with the HGVS notation (you have to call
	 * {@link #shiftInsertion}) afterwards.
	 *
	 * @param aaChange
	 *            {@link AminoAcidChange} to truncate
	 * @return updated {@link AminoAcidChange}
	 */
	public static AminoAcidChange truncateBothSides(AminoAcidChange aaChange) {
		// TODO(holtgrem): Test me!

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

	/**
	 * Shift insertion {@link AminoAcidChange} to the right in WT AA sequence.
	 *
	 * Returns <code>aaChange</code> if <code>aaChange.ref</code> is not the empty string.
	 *
	 * @param aaChange
	 *            {@link AminoAcidChange} to normalize
	 * @param wtAASeq
	 *            WT AA sequence to use for shifting
	 * @return updated {@link AminoAcidChange}
	 */
	public static AminoAcidChange shiftInsertion(AminoAcidChange aaChange, String wtAASeq) {
		// TODO(holtgrem): Test me!
		if (aaChange.getRef().length() != 0)
			return aaChange;

		// Insert the alternative bases at the position indicated by txPos.
		StringBuilder builder = new StringBuilder(wtAASeq);
		builder.insert(aaChange.getPos(), aaChange.getAlt());

		// Execute algorithm and compute the shift.
		int pos = aaChange.getPos();
		int shift = 0;
		final int LEN = aaChange.getAlt().length();
		final String seq = builder.toString();
		while ((pos + LEN < seq.length()) && (seq.charAt(pos) == seq.charAt(pos + LEN))) {
			++shift;
			++pos;
		}

		if (shift == 0) // only rebuild if shift > 0
			return aaChange;
		else
			return new AminoAcidChange(pos, "", seq.substring(pos, pos + LEN));
	}

}
