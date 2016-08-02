package de.charite.compbio.jannovar.mendel.bridge;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Raised in the case of problems with annotating Mendelian inheritance
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class CannotAnnotateMendelianInheritance extends JannovarException {

	private static final long serialVersionUID = 1L;

	public CannotAnnotateMendelianInheritance(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CannotAnnotateMendelianInheritance(String msg) {
		super(msg);
	}

}
