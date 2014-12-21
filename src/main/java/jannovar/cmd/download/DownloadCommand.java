package jannovar.cmd.download;

import jannovar.JannovarOptions;
import jannovar.cmd.JannovarCommand;
import jannovar.datasource.DataSourceFactory;
import jannovar.exception.CommandLineParsingException;
import jannovar.exception.HelpRequestedException;
import jannovar.exception.JannovarException;
import jannovar.io.JannovarData;
import jannovar.io.JannovarDataSerializer;
import jannovar.util.PathUtil;

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
		System.err.println("Options");
		options.print();
		DataSourceFactory factory = new DataSourceFactory(options, options.dataSourceFiles);
		for (String name : options.dataSourceNames) {
			System.err.println("Downloading/parsing for data source \"" + name + "\"");
			JannovarData data = factory.getDataSource(name).getDataFactory().build(options.downloadPath);
			String filename = PathUtil.join(options.downloadPath, name.replace('/', '_').replace('\\', '_') + ".ser");
			System.err.println("Serializing to " + filename);
			JannovarDataSerializer serializer = new JannovarDataSerializer(filename);
			serializer.serializeKnownGeneList(data);
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
