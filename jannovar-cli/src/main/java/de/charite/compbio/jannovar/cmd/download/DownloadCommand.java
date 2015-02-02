package de.charite.compbio.jannovar.cmd.download;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.JannovarDataSerializer;

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
		options.print(System.err);

		DataSourceFactory factory = new DataSourceFactory(options, options.dataSourceFiles);
		for (String name : options.dataSourceNames) {
			System.err.println("Downloading/parsing for data source \"" + name + "\"");
			JannovarData data = factory.getDataSource(name).getDataFactory()
					.build(options.downloadPath, options.printProgressBars);
			String filename = PathUtil.join(options.downloadPath, name.replace('/', '_').replace('\\', '_') + ".ser");
			JannovarDataSerializer serializer = new JannovarDataSerializer(filename);
			serializer.save(data);
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
