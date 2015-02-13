package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Annotation exceptions are thrown when the information provided is not well formed or not sufficient to create a
 * correct annotation.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class AnnotationException extends JannovarException {

	public static final long serialVersionUID = 2L;

	public AnnotationException() {
		super();
	}

	public AnnotationException(String msg) {
		super(msg);
	}

	public AnnotationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}