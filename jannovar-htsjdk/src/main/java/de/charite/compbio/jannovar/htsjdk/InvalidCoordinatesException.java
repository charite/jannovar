package de.charite.compbio.jannovar.htsjdk;

import de.charite.compbio.jannovar.annotation.AnnotationMessage;

/**
 * Thrown in {@link VariantContextAnnotator} in the case of invalid positions (unknown chromsomes).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidCoordinatesException extends Exception {

	final private AnnotationMessage annotationMessage;

	public InvalidCoordinatesException(AnnotationMessage annotationMessage) {
		super();
		this.annotationMessage = annotationMessage;
	}

	public InvalidCoordinatesException(String msg, AnnotationMessage annotationMessage) {
		super(msg);
		this.annotationMessage = annotationMessage;
	}

	public InvalidCoordinatesException(String msg, Throwable other, AnnotationMessage annotationMessage) {
		super(msg, other);
		this.annotationMessage = annotationMessage;
	}

	public AnnotationMessage getAnnotationMessage() {
		return annotationMessage;
	}

	private static final long serialVersionUID = 1L;

}
