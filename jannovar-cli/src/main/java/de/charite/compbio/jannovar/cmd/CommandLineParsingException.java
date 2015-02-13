package de.charite.compbio.jannovar.cmd;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception thrown on problems with the command line.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class CommandLineParsingException extends JannovarException {

	public CommandLineParsingException() {
		super();
	}

	public CommandLineParsingException(String msg) {
		super(msg);
	}

	public CommandLineParsingException(String msg, Throwable cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = 1L;

}
