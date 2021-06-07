package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VardbImportTest {

	protected String h2Path;
	protected String dbGnomadExomesPath;
	protected Connection conn;

	@Before
	public void setUp() throws SQLException {
		File tmpDir = Files.createTempDir();

		h2Path = tmpDir + "/gnomad.exomes.h2.db";

		dbGnomadExomesPath = tmpDir + "/gnomad.exomes.vcf.gz";
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.1.1.sites.head.vcf.gz", new File(dbGnomadExomesPath));
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.1.1.sites.head.vcf.gz.tbi", new File(dbGnomadExomesPath + ".tbi"));

		conn = DriverManager.getConnection(
			"jdbc:h2:"
				+ h2Path
				+ ";TRACE_LEVEL_FILE=0;MV_STORE=FALSE;DB_CLOSE_ON_EXIT=FALSE",
			"sa",
			"");
	}

	@Test
	public void testVardbImport() throws JannovarVarDBException {
		new ImportCommand(new ImportOptions(
			"GRCh37",
			"gnomad.exomes",
			"2.1.1",
			h2Path.substring(0, h2Path.length() - 6),
			ImmutableList.of(dbGnomadExomesPath),
			"gnomad_exomes_r2_1_1",
			"GNOMAD_EXOMES_",
			ImmutableList.of("AC", "AN", "AF"),
			true
		)).run(conn);
	}

}
