package de.charite.compbio.jannovar.reference;

/**
 * Helper class for checking whether an insertion in a string is a duplication.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class DuplicationChecker {

	/**
	 * @param ref
	 *            reference string for insertion
	 * @param insertion
	 *            the string to be inserted at <code>pos</code>
	 * @param pos
	 *            the 0-based position in <code>ref</code> that <code>insertion</code> is to be inserted
	 *
	 * @return <code>true</code> if the described insertion is a duplication
	 */
	public static boolean isDuplication(String ref, String insertion, int pos) {
		if (pos + insertion.length() <= ref.length()) {
			// can be duplication with string after pos
			if (ref.substring(pos, pos + insertion.length()).equals(insertion))
				return true;
		}
		if (pos >= insertion.length()) {
			// can be duplication with string before pos
			if (ref.substring(pos - insertion.length(), pos).equals(insertion))
				return true;
		}
		return false;
	}
}
