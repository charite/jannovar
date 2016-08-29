package de.charite.compbio.jannovar.cmd.download;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;
import de.charite.compbio.jannovar.datasource.DatasourceOptions;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Implementation of download step in Jannovar.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class DownloadCommand extends JannovarCommand {

	private JannovarDownloadOptions options;

	public DownloadCommand(String argv[], Namespace args) throws CommandLineParsingException {
		this.options = new JannovarDownloadOptions();
		this.options.setFromArgs(args);
	}

	/**
	 * Perform the downloading.
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		System.err.println(options.toString());

		DatasourceOptions dsOptions = new DatasourceOptions(options.getHttpProxy(), options.getHttpsProxy(),
				options.getFtpProxy(), options.isReportProgress());

		DataSourceFactory factory = new DataSourceFactory(dsOptions, options.dataSourceFiles);
		for (String name : options.getDatabaseNames()) {
			System.err.println("Downloading/parsing for data source \"" + name + "\"");
			JannovarData data = factory.getDataSource(name).getDataFactory().build(options.getDownloadDir(),
					options.isReportProgress());
			String filename = PathUtil.join(options.getDownloadDir(),
					name.replace('/', '_').replace('\\', '_') + ".ser");
			JannovarDataSerializer serializer = new JannovarDataSerializer(filename);
			serializer.save(data);
		}
	}

}
