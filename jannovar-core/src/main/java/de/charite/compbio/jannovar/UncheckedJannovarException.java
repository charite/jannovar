package de.charite.compbio.jannovar;

/**
 * Base class for unchecked exceptions in Jannovar
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class UncheckedJannovarException extends RuntimeException {

	public static final long serialVersionUID = 2L;

	public UncheckedJannovarException() {
		super();
	}

	public UncheckedJannovarException(String msg) {
		super(msg);
	}

	public UncheckedJannovarException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
