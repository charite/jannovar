package de.charite.compbio.jannovar.cmd;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Thrown when the user requests help.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class HelpRequestedException extends JannovarException {

	private static final long serialVersionUID = 1L;

	public HelpRequestedException() {
		super();
	}

	public HelpRequestedException(String msg) {
		super(msg);
	}

	public HelpRequestedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
