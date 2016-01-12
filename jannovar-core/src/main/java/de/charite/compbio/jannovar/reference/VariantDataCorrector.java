package de.charite.compbio.jannovar.reference;

/**
 * Converts a triple (ref string, alt string, position) into the internal Jannovar represetation.
 *
 * The longest prefix of ref and alt is removed and position is incremented by the number of removed characters.
 *
 * This is used in the constructor {@link GenomeVariant} so there is no need for using this class directly when the
 * change is converted into a {@link GenomeVariant} before processing. This class has package-visibility only because its
 * members are visible to the outside. Rather construct a {@link GenomeVariant} and use this immutable class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
final class VariantDataCorrector {

	/** The reference characters after correction. */
	String ref;
	/** The alt bases after correction. */
	String alt;
	/** The position after correction. */
	int position;

	/** Initialize from triple and immediately correct. */
	public VariantDataCorrector(String ref, String alt, int position) {
		this.ref = ref;
		this.alt = alt;
		this.position = position;

		correct();
	}

	private void correct() {
		int idx = 0;
		// beginning
		while (idx < ref.length() && idx < alt.length() && ref.charAt(idx) == alt.charAt(idx)) {
			idx++;
		}
		position += idx;
		ref = ref.substring(idx);
		alt = alt.substring(idx);

		// end
		int xdi = ref.length();
		int diff = ref.length() - alt.length();
		while (xdi > 0 && xdi - diff > 0 && ref.charAt(xdi - 1) == alt.charAt(xdi - 1 - diff)) {
			xdi--;
		}
		ref = xdi == 0 ? "" : ref.substring(0, xdi);
		alt = xdi - diff == 0 ? "" : alt.substring(0, xdi - diff);
	}
}