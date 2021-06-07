package de.charite.compbio.jannovar.cmd.vardb_list;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.cmd.vardb_import.JannovarVardbImportOptions;
import de.charite.compbio.jannovar.vardbs.base.ListCommand;
import de.charite.compbio.jannovar.vardbs.base.ListOptions;
import de.charite.compbio.jannovar.vardbs.base.Table;
import de.charite.compbio.jannovar.vardbs.base.TableDao;
import net.sourceforge.argparse4j.inf.Namespace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Jannovar command listing contents of vardb files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bih-charite.de">Manuel Holtgrewe</a>
 */
public class VardbListCommand extends JannovarAnnotationCommand {

	/**
	 * Configuration
	 */
	private JannovarVardbListOptions options;

	public VardbListCommand(String argv[], Namespace args) throws CommandLineParsingException {
		this.options = new JannovarVardbListOptions();
		this.options.setFromArgs(args);
	}

	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		System.err.println(options.toString());
		System.err.println("Starting listing...");
		final long startTime = System.nanoTime();

		String dbPath = this.options.getDbPath();
		if (!dbPath.startsWith("/") && !dbPath.startsWith(".")) {
			dbPath = "./" + dbPath;  // H2 does not like implicit relative paths
		}
		if (dbPath.endsWith(".h2.db")) {
			dbPath = dbPath.substring(0, dbPath.length() - 6);
		}

		try (final Connection conn = DriverManager.getConnection(
			"jdbc:h2:"
				+ dbPath
				+ ";TRACE_LEVEL_FILE=0;MV_STORE=FALSE;DB_CLOSE_ON_EXIT=FALSE;ACCESS_MODE_DATA=r",
			"sa",
			"");) {
			new ListCommand(new ListOptions(options.getDbPath())).run(conn);
		} catch (SQLException e) {
			System.err.println("Problem accessing database!");
			e.printStackTrace();
		}
	}

}
