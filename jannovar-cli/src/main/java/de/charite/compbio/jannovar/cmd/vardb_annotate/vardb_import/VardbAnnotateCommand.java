package de.charite.compbio.jannovar.cmd.vardb_annotate.vardb_import;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.vardbs.base.AnnotateCommand;
import de.charite.compbio.jannovar.vardbs.base.AnnotateOptions;
import de.charite.compbio.jannovar.vardbs.base.ImportCommand;
import de.charite.compbio.jannovar.vardbs.base.ImportOptions;
import net.sourceforge.argparse4j.inf.Namespace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Jannovar command for importing VCF into vardb files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bih-charite.de">Manuel Holtgrewe</a>
 */
public class VardbAnnotateCommand extends JannovarAnnotationCommand {

	/**
	 * Configuration
	 */
	private JannovarVardbAnnotateOptions options;

	public VardbAnnotateCommand(String argv[], Namespace args) throws CommandLineParsingException {
		this.options = new JannovarVardbAnnotateOptions();
		this.options.setFromArgs(args);
	}

	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		System.err.println(options.toString());
		System.err.println("Starting annotation...");
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

			final AnnotateCommand cmd = new AnnotateCommand(new AnnotateOptions(
				options.getGenomeBuild(),
				options.getDbPath(),
				options.getInputVcfPath(),
				options.getOutputVcfPath(),
				options.getTableNames()
			), conn);
			cmd.run(conn);

			final long endTime = System.nanoTime();
			System.err.println(String.format("Annotating took %.2f sec.",
				(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
		} catch (SQLException e) {
			System.err.println("Problem annotating variants!");
			e.printStackTrace();
		}
	}

}
