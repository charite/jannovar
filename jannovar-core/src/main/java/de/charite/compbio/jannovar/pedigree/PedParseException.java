package de.charite.compbio.jannovar.pedigree;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception that occurs during parsing of PEDfiles.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
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