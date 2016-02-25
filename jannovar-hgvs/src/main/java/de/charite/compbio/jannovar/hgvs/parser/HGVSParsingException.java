package de.charite.compbio.jannovar.hgvs.parser;

/**
 * Thrown in case of problems in {@link HGVSParser}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParsingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HGVSParsingException() {
		super();
	}

	public HGVSParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public HGVSParsingException(String message) {
		super(message);
	}

	public HGVSParsingException(Throwable cause) {
		super(cause);
	}

}
