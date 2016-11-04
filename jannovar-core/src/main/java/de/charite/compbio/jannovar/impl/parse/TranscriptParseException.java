package de.charite.compbio.jannovar.impl.parse;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception that occurs during parsing of transcript database files.
 *
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 */
public class TranscriptParseException extends JannovarException {

	public static final long serialVersionUID = 2L;

	public TranscriptParseException() {
		super();
	}

	public TranscriptParseException(String msg) {
		super(msg);
	}

	public TranscriptParseException(String msg, Throwable cause) {
		super(msg, cause);
	}

}