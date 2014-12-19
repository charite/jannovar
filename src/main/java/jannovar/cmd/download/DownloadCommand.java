package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.cmd.JannovarCommand;
import jannovar.datasource.DataSourceFactory;
import jannovar.exception.CommandLineParsingException;
import jannovar.exception.HelpRequestedException;
import jannovar.exception.JannovarException;

import org.apache.commons.cli.ParseException;

/**
 * Implementation of download step in Jannovar.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class DownloadCommand extends JannovarCommand {

	public DownloadCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
	}

	/**
	 * Perform the downloading.
	 */
	@Override
	public void run() throws JannovarException {
		DataSourceFactory factory = new DataSourceFactory(options.dataSourceFiles);
		for (String name : options.dataSourceNames) {
			System.err.println("Downloading/parsing for data source " + name);
			factory.getDataSource(name).getDataFactory().build(options.downloadPath);
		}
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv) throws CommandLineParsingException,
	HelpRequestedException {
		try {
			return new DownloadCommandLineParser().parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException(e.getMessage());
		}
	}
}
