package de.charite.compbio.jannovar.impl.util;

/**
 * Utility class for DNA string manipulation.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class DNAUtils {

	/**
	 * Return the reverse complement version of a DNA string in upper case. Note that no checking is done in this code
	 * since the parse code checks for valid DNA and upper-cases the input. This code will break if these assumptions
	 * are not valid.
	 *
	 * @param sq
	 *            original, upper-case cDNA string
	 * @return reverse complement version of the input string sq.
	 */
	public static String reverseComplement(String sq) {
		if (sq.isEmpty())
			return sq; // deletion, insertion do not need rc

		StringBuffer sb = new StringBuffer();
		for (int i = sq.length() - 1; i >= 0; i--) {
			char c = sq.charAt(i);
			char match = 0;
			switch (c) {
			case 'A':
				match = 'T';
				break;
			case 'C':
				match = 'G';
				break;
			case 'G':
				match = 'C';
				break;
			case 'T':
				match = 'A';
				break;
			case 'N':
				match = 'N';
				break;
			}
			if (match > 0)
				sb.append(match);
		}
		return sb.toString();
	}

}
