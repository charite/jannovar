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

}
