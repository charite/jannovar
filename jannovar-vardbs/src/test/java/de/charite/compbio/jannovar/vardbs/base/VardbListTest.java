package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VardbListTest {

	protected String h2Path;
	protected Connection conn;

	@BeforeEach
	public void setUp() throws SQLException {
		File tmpDir = Files.createTempDir();

		h2Path = tmpDir + "/gnomad.exomes.h2.db";
		ResourceUtils.copyResourceToFile("/gnomad.exomes.h2.db", new File(h2Path));

		conn = DriverManager.getConnection(
			"jdbc:h2:"
				+ h2Path.substring(0, h2Path.length() - 6)
				+ ";TRACE_LEVEL_FILE=0;MV_STORE=FALSE;DB_CLOSE_ON_EXIT=FALSE;ACCESS_MODE_DATA=r",
			"sa",
			"");
	}

	@Test
	public void testVardbList() throws JannovarVarDBException {
		ListOptions options = new ListOptions(h2Path);
		ListCommand cmd = new ListCommand(options);
		cmd.run(conn);
	}

}
