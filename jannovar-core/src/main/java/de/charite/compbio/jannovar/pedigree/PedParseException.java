package de.charite.compbio.jannovar.pedigree;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception that occurs during parsing of PEDfiles.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
public class PedParseException extends JannovarException {

	public static final long serialVersionUID = 2L;

	public PedParseException() {
		super();
	}

	public PedParseException(String msg) {
		super(msg);
	}

	public PedParseException(String msg, Throwable cause) {
		super(msg, cause);
	}

}