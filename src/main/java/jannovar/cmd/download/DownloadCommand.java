package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.cmd.JannovarCommand;
import jannovar.exception.JannovarException;

/**
 * Implementation of download step in Jannovar.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class DownloadCommand extends JannovarCommand {

	public DownloadCommand(JannovarOptions options) {
		super(options);
	}

	/**
	 * Perform the downloading.
	 */
	@Override
	public void run() throws JannovarException {
		DownloadManager manager = DownloadManagerFactory.build(options);
		if (manager != null)
			manager.run();
	}
}
