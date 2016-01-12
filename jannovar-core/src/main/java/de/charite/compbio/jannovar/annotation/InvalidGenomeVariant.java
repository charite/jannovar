package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.reference.GenomeVariant;

/**
 * Thrown when the the given {@link GenomeVariant} does not fit the used annotation builder class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class InvalidGenomeVariant extends AnnotationException {

	private static final long serialVersionUID = -6983204936815945929L;

	public InvalidGenomeVariant() {
	}

	public InvalidGenomeVariant(String msg) {
		super(msg);
	}

}
