package de.charite.compbio.jannovar.htsjdk;

/**
 * Thrown in {@link VariantContextAnnotator} in the case of mixing small and SV alleles.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class MixingSmallAndSVAlleles extends Exception {

	public MixingSmallAndSVAlleles(String msg) {
		super(msg);
	}

	public MixingSmallAndSVAlleles(String msg, Throwable other) {
		super(msg, other);
	}

	private static final long serialVersionUID = 1L;

}
