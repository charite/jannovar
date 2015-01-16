/**
 *
 */
package de.charite.compbio.jannovar.impl.parse.gff;

import java.io.IOException;

/**
 * Thrown on invalid feature formats.
 *
 * @author mjaeger
 */
public class FeatureFormatException extends IOException {

	public FeatureFormatException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
