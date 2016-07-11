package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Raised when it is attempted to access an invalid codon
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
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
