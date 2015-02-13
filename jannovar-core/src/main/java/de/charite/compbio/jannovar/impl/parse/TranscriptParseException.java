package de.charite.compbio.jannovar.impl.parse;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception that occurs during parsing of transcript database files.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
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