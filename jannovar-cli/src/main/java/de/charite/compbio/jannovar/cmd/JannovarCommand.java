package de.charite.compbio.jannovar.cmd;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Super class for all commands, i.e. the classes implementing one Jannovar execution step.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public abstract class JannovarCommand {

	/**
	 * Set log level, depending on this.verbosity.
	 */
	protected void setLogLevel(int verbosity) {
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
	 * Function for the execution of the command.
	 *
	 * @throws JannovarException
	 *             on problems executing the command.
	 */
	public abstract void run() throws JannovarException;

}
