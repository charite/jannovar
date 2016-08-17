package de.charite.compbio.jannovar.vardbs.base;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Raised on problems with variant database annotation
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
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
