package de.charite.compbio.jannovar.cmd;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;

/**
 * Super class for all commands, i.e. the classes implementing one Jannovar execution step.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarCommand {

	/** Configuration to use for the command execution. */
	protected JannovarOptions options;
	/** Verbosity level: (0) quiet, (1) normal, (2) verbose, (3) very verbose */
	protected int verbosity = 1;

	/**
	 * Initialize the JannovarCommand.
	 *
	 * @param argv
	 *            command line arguments to use
	 * @throws CommandLineParsingException
	 *             on problems with the command line
	 * @throws HelpRequestedException
	 *             if the user requested help through the command line parameters
	 */
	public JannovarCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		this.options = parseCommandLine(argv);
		this.verbosity = options.verbosity;
		setLogLevel();
	}

	/**
	 * Set log level, depending on this.verbosity.
	 */
	private void setLogLevel() {
		switch (verbosity) {
		case 0:
		case 1:
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
			break;
		case 2:
		default:
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
		}
	}

	/**
	 * Function for parsing the command line.
	 *
	 * @param argv
	 *            command line to parse, as in the program's main function
	 * @return {@link JannovarOptions} with the programs' configuration
	 * @throws CommandLineParsingException
	 *             on problems with the command line
	 * @throws HelpRequestedException
	 *             when the user requested the help page
	 */
	protected abstract JannovarOptions parseCommandLine(String[] argv) throws CommandLineParsingException,
	HelpRequestedException;

	/**
	 * Function for the execution of the command.
	 *
	 * @throws JannovarException
	 *             on problems executing the command.
	 */
	public abstract void run() throws JannovarException;

}
