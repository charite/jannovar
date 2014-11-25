package jannovar.reference;

/**
 * Helper for normalizing changes in amino acid sequences.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AminoAcidChangeNormalizer {

	/**
	 * Search for stop codon in <code>change.alt</code> and truncate afterwards.
	 *
	 * @param change
	 *            the {@link AminoAcidChange} to normalize
	 * @return normalized {@link AminoAcidChange}
	 */
	public static AminoAcidChange truncateAltAfterStopCodon(AminoAcidChange change) {
		int stopCodonPos = change.alt.indexOf('*');
		if (stopCodonPos == -1)
			return change; // no stop codon found in change.alt
		return new AminoAcidChange(change.pos, change.ref, change.alt.substring(0, stopCodonPos + 1));
	}

	/**
	 * Normalize deletion {@link AminoAcidChange} for amino acid string
	 *
	 * @param ref
	 *            reference amino acid string to change
	 * @param change
	 *            the {@link AminoAcidChange} to normalize
	 * @return normalized AminoAcidChange
	 */
	public static AminoAcidChange normalizeDeletion(String ref, AminoAcidChange change) {
		if (change.ref.length() == 0 || change.alt.length() > 0)
			throw new Error("Invalid AminoAcidChange: " + change);

		// Compute shift of deletion.
		int shift = 0;
		final int LEN = change.ref.length();
		while (change.pos + LEN + shift < ref.length()
				&& ref.charAt(change.pos) == ref.charAt(change.pos + LEN + shift))
			shift += 1;
		if (shift == 0)
			return change;

		// Build new AminoAcidChange.
		StringBuilder changeRefBuilder = new StringBuilder();
		changeRefBuilder.append(change.ref.substring(shift, change.ref.length()));
		changeRefBuilder.append(ref.substring(change.pos + change.ref.length(), change.pos + change.ref.length()
				+ shift));
		return new AminoAcidChange(change.pos + shift, changeRefBuilder.toString(), "");
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
		final int REFLEN = aaChange.ref.length() - 1;
		final int ALTLEN = aaChange.alt.length() - 1;
		int truncSuffix = 0;
		while (truncSuffix < aaChange.ref.length() && truncSuffix < aaChange.alt.length()
				&& aaChange.ref.charAt(REFLEN - truncSuffix) == aaChange.alt.charAt(ALTLEN - truncSuffix))
			++truncSuffix;
		if (truncSuffix != 0)
			aaChange = new AminoAcidChange(aaChange.pos,
					aaChange.ref.substring(0, aaChange.ref.length() - truncSuffix), aaChange.alt.substring(0,
							aaChange.alt.length() - truncSuffix));

		// Truncate prefixes / from the left.
		int truncPrefix = 0;
		while (truncPrefix < aaChange.ref.length() && truncPrefix < aaChange.alt.length()
				&& aaChange.ref.charAt(truncPrefix) == aaChange.alt.charAt(truncPrefix))
			++truncPrefix;
		if (truncPrefix != 0)
			aaChange = new AminoAcidChange(aaChange.pos + truncPrefix, aaChange.ref.substring(truncPrefix),
					aaChange.alt.substring(truncPrefix));

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
		if (aaChange.ref.length() != 0)
			return aaChange;

		// Insert the alternative bases at the position indicated by txPos.
		StringBuilder builder = new StringBuilder(wtAASeq);
		builder.insert(aaChange.pos, aaChange.alt);

		// Execute algorithm and compute the shift.
		int pos = aaChange.pos;
		int shift = 0;
		final int LEN = aaChange.alt.length();
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
