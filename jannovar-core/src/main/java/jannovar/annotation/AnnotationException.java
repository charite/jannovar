package jannovar.annotation;

import jannovar.JannovarException;

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

}