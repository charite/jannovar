package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Thrown when a {@link GenomeVariant} could not be created, e.g., when an allele would be symbolic.
 *
 * @author Manuel Holtgrewe <a href="mailto:manuel.holtgrewe@bihealth.de">manuel.holtgrewe@bihealth.de</a>
 */
public class InvalidGenomeVariant extends JannovarException {

	/**
	 * Default constructor.
	 */
	public InvalidGenomeVariant() {
	}

	/**
	 * Construct with message;
	 *
	 * @param msg   message of the exception.
	 */
	public InvalidGenomeVariant(String msg) {
		super(msg);
	}

	/**
	 * Construct with message and nested exception.
	 *
	 * @param msg   message of the exception.
	 * @param cause nested throwable.
	 */
	public InvalidGenomeVariant(String msg, Throwable cause) {
		super(msg, cause);
	}

}
