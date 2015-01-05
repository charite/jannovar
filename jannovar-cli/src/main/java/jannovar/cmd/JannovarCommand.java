package jannovar.cmd;

import jannovar.JannovarException;
import jannovar.JannovarOptions;

/**
 * Super class for all commands, i.e. the classes implementing one Jannovar execution step.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarCommand {

	/** Configuration to use for the command execution. */
	protected JannovarOptions options;

	/**
	 * Initialize the JannovarCommand.
	 *
	 * @param options
	 *            configuration to use
	 * @throws CommandLineParsingException
	 *             on problems with the command line
	 * @throws HelpRequestedException
	 */
	public JannovarCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		this.options = parseCommandLine(argv);
	}

	/**
	 * Function for parsing the command line.
	 *
	 * @param args
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
