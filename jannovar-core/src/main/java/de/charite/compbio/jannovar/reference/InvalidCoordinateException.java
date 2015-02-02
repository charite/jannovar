package de.charite.compbio.jannovar.reference;

/**
 * Thrown if two coordinates were on different chromosomes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidCoordinateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidCoordinateException() {
	}

	public InvalidCoordinateException(String msg) {
		super(msg);
	}

}
