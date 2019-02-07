package de.charite.compbio.jannovar.htsjdk;

/**
 * Thrown in {@link VariantContextAnnotator} in the case missing {@code SVTYPE} in the {@code INFO} field.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class MissingSVTypeInfoField extends Exception {

	public MissingSVTypeInfoField(String msg) {
		super(msg);
	}

	public MissingSVTypeInfoField(String msg, Throwable other) {
		super(msg, other);
	}

	private static final long serialVersionUID = 1L;

}
