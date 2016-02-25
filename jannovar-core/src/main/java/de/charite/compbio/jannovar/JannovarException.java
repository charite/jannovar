package de.charite.compbio.jannovar;

/**
 * Base class for exceptions in Jannovar.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class JannovarException extends Exception {

	public static final long serialVersionUID = 2L;

	public JannovarException() {
		super();
	}

	public JannovarException(String msg) {
		super(msg);
	}

	public JannovarException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
