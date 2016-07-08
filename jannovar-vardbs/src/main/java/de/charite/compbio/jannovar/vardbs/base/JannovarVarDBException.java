package de.charite.compbio.jannovar.vardbs.base;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Raised on problems with variant database annotation
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class JannovarVarDBException extends JannovarException {

	private static final long serialVersionUID = 1L;

	public JannovarVarDBException(String message, Throwable cause) {
		super(message, cause);
	}

	public JannovarVarDBException(String message) {
		super(message);
	}
	
}
