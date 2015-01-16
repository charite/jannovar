package de.charite.compbio.jannovar.impl.util;

public class StringUtil {

	/**
	 * Concatenate values.toString() using a StringBuilder.
	 *
	 * Java's {@link String#format} is very slow and internally uses regular expressions.
	 */
	public static String concatenate(Object... values) {
		if (values.length == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; ++i)
			builder.append(values[i]);
		return builder.toString();
	}

}
