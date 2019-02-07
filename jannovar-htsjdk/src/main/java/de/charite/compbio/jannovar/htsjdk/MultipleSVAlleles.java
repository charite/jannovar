package de.charite.compbio.jannovar.htsjdk;

/**
 * Thrown in {@link VariantContextAnnotator} in the case of having more than one SV allele in a variant.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class MultipleSVAlleles extends Exception {

	public MultipleSVAlleles(String msg) {
		super(msg);
	}

	public MultipleSVAlleles(String msg, Throwable other) {
		super(msg, other);
	}

	private static final long serialVersionUID = 1L;

}
