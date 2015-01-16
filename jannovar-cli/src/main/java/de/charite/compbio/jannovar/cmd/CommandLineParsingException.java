package de.charite.compbio.jannovar.cmd;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception thrown on problems with the command line.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class CommandLineParsingException extends JannovarException {

	public CommandLineParsingException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
