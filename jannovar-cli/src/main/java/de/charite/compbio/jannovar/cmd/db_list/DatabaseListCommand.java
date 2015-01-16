package de.charite.compbio.jannovar.cmd.db_list;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarCommand;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;

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
		options.print();

		DataSourceFactory factory = new DataSourceFactory(options, options.dataSourceFiles);
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
			throw new CommandLineParsingException(e.getMessage());
		}
	}

}
