package de.charite.compbio.jannovar.mendel.filter;

import de.charite.compbio.jannovar.UncheckedJannovarException;

/**
 * Thrown on problems during the filtration.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class VariantContextFilterException extends UncheckedJannovarException {

	public VariantContextFilterException(String string) {
		super(string);
	}

	public VariantContextFilterException(String string, Throwable cause) {
		super(string, cause);
	}

	private static final long serialVersionUID = 1L;

}
