package de.charite.compbio.jannovar.htsjdk;

/**
 * Thrown in {@link VariantContextAnnotator} in the case missing {@code END} in the {@code INFO} field.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class MissingEndInfoField extends Exception {

	public MissingEndInfoField(String msg) {
		super(msg);
	}

	public MissingEndInfoField(String msg, Throwable other) {
		super(msg, other);
	}

	private static final long serialVersionUID = 1L;

}
