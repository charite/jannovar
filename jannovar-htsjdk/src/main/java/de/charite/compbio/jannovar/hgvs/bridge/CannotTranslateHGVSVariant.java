package de.charite.compbio.jannovar.hgvs.bridge;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.reference.GenomeVariant;

/**
 * Helper class thrown on problems with translating {@link HGVSVariant} to {@link GenomeVariant}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class CannotTranslateHGVSVariant extends Exception {

	private static final long serialVersionUID = 1L;

	public CannotTranslateHGVSVariant() {
		super();
	}

	public CannotTranslateHGVSVariant(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotTranslateHGVSVariant(String message) {
		super(message);
	}

}
