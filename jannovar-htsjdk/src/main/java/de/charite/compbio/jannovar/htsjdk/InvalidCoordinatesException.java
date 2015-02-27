package de.charite.compbio.jannovar.htsjdk;

/**
 * Thrown in {@link VariantContextAnnotator} in the case of invalid positions (unknown chromsomes).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidCoordinatesException extends Exception {

	public InvalidCoordinatesException() {
		super();
	}

	public InvalidCoordinatesException(String msg) {
		super(msg);
	}

	public InvalidCoordinatesException(String msg, Throwable other) {
		super(msg, other);
	}

	private static final long serialVersionUID = 1L;

}
