package jannovar.cmd;

import jannovar.JannovarOptions;
import jannovar.exception.JannovarException;

/**
 * Super class for all commands, i.e. the classes implementing one Jannovar execucion step.
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
	 */
	public JannovarCommand(JannovarOptions options) {
		this.options = options;
	}

	/**
	 * Function for the execution of the command.
	 *
	 * @throws JannovarException
	 *             on problems executing the command.
	 */
	public abstract void run() throws JannovarException;
}
