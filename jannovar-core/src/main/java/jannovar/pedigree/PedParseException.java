package jannovar.pedigree;

import jannovar.JannovarException;

/**
 * Exception that occurs during parsing of PEDfiles.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class PedParseException extends JannovarException {

	public static final long serialVersionUID = 2L;

	public PedParseException() {
		super("Unknown exception during parsing of Ped File");
	}

	public PedParseException(String msg) {
		super(msg);
	}

}