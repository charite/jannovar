package de.charite.compbio.jannovar.htsjdk;

import de.charite.compbio.jannovar.annotation.AnnotationMessage;

/**
 * Thrown in {@link VariantContextAnnotator} in the case of invalid breakend descriptions.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class InvalidBreakendDescriptionException extends Exception {

	public InvalidBreakendDescriptionException(String msg) {
		super(msg);
	}

	public InvalidBreakendDescriptionException(String msg, Throwable other) {
		super(msg, other);
	}

	private static final long serialVersionUID = 1L;

}
