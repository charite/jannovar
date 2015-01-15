package jannovar.impl.parse;

import jannovar.JannovarException;

/**
 * Exception that occurs during parsing of transcript database files.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class TranscriptParseException extends JannovarException {

	public static final long serialVersionUID = 2L;

	public TranscriptParseException() {
		super("Unknown exception during parsing of transcript database files");
	}

	public TranscriptParseException(String msg) {
		super(msg);
	}

}