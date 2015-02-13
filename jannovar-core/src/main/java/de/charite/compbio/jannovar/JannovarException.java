package de.charite.compbio.jannovar;

/**
 * Base class for exceptions in Jannovar.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
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
