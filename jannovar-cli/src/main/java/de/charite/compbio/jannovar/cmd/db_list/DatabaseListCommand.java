package de.charite.compbio.jannovar.cmd.db_list;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;
import de.charite.compbio.jannovar.datasource.DatasourceOptions;

public class DatabaseListCommand extends JannovarCommand {

	public DatabaseListCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
	}

	/**
	 * Perform the downloading.
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		options.print(System.err);

		DatasourceOptions dsOptions = new DatasourceOptions(options.httpProxy, options.httpsProxy, options.ftpProxy,
				options.printProgressBars);

		DataSourceFactory factory = new DataSourceFactory(dsOptions, options.dataSourceFiles);
		System.err.println("Available data sources:\n");
		for (String name : factory.getNames())
			System.err.println(String.format("    %s", name));
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv) throws CommandLineParsingException,
			HelpRequestedException {
		try {
			return new DatabaseListCommandLineParser().parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException("Could not parse command line", e);
		}
	}

}
