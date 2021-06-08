package de.charite.compbio.jannovar.cmd.vardb_list;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import de.charite.compbio.jannovar.cmd.statistics.GatherStatisticsCommand;
import net.sourceforge.argparse4j.inf.*;

import java.util.function.BiFunction;

/**
 * Configuration for the <code>vardb-list</code> command
 *
 * @author <a href="mailto:manuel.holtgrewe@bih-charite.de">Manuel Holtgrewe</a>
 */
public class JannovarVardbListOptions extends JannovarBaseOptions {
	private String dbPath = null;

	/**
	 * Setup {@link ArgumentParser}
	 *
	 * @param subParsers {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, VardbListCommand> handler = (argv, args) -> {
			try {
				return new VardbListCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("vardb-list", true).help("list contents of vardb file")
			.setDefault("cmd", handler);
		subParser.description("List contents of vardb file");

		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("--database-file").help("Path to database file").required(true);

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		dbPath = args.getString("database_file");
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	@Override
	public String toString() {
		return "JannovarVardbListOptions{" +
			"dbPath='" + dbPath + '\'' +
			'}';
	}
}
