package de.charite.compbio.jannovar.annotation;

/**
 * Thrown when the the given {@link GenomeVariant} does not fit the used annotation builder class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidGenomeVariant extends AnnotationException {

	private static final long serialVersionUID = -6983204936815945929L;

	public InvalidGenomeVariant() {
	}

	public InvalidGenomeVariant(String msg) {
		super(msg);
	}

}
