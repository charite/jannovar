package de.charite.compbio.jannovar.cmd;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;

/**
 * Super class for all commands, i.e. the classes implementing one Jannovar execution step.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public abstract class JannovarCommand {

	/** Configuration to use for the command execution. */
	protected JannovarOptions options;
	/** Verbosity level: (0) quiet, (1) normal, (2) verbose, (3) very verbose */
	protected int verbosity = 1;
	/** command line arguments */
	protected final ImmutableList<String> args;

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
		this.args = ImmutableList.copyOf(argv);
		setLogLevel();
	}

	/**
	 * Set log level, depending on this.verbosity.
	 */
	private void setLogLevel() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration conf = ctx.getConfiguration();

		if (verbosity <= 1)
			conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
		else if (verbosity <= 2)
			conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
		else
			conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.TRACE);

		ctx.updateLoggers(conf);
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
