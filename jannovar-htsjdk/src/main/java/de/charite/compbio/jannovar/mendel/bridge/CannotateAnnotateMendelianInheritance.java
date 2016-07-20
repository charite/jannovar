package de.charite.compbio.jannovar.mendel.bridge;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Raised in the case of problems with annotating Mendelian inheritance
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class CannotateAnnotateMendelianInheritance extends JannovarException {

	private static final long serialVersionUID = 1L;

	public CannotateAnnotateMendelianInheritance(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CannotateAnnotateMendelianInheritance(String msg) {
		super(msg);
	}

}
