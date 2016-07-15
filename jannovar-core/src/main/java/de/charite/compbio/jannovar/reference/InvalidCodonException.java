package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Raised when it is attempted to access an invalid codon
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class InvalidCodonException extends JannovarException {

	private static final long serialVersionUID = 1L;

	public InvalidCodonException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public InvalidCodonException(String msg) {
		super(msg);
	}

}
