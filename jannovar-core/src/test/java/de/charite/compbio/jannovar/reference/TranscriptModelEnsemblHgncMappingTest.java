package de.charite.compbio.jannovar.reference;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Test that the mapping from Ensembl {@code ENSG} to HGNC identifier works for {@code WHAMMP}.
 * <p>
 * The mapping file is created with:
 *
 * <pre>
 * java -jar jannovar-cli.jar \
 *     download -d hg19/ensembl -o jannovar-core/src/test/resources/chr15_whammp3.ser --gene-ids WHAMMP3
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class TranscriptModelEnsemblHgncMappingTest {

	/**
	 * Path to .ser file.
	 */
	static String dbPath;

	/**
	 * The {@link JannovarData} to load the test data into.
	 */
	static JannovarData jvData;

	/**
	 * The transcript of OMA1 lies on the reverse strand.
	 */
	static TranscriptModel whammp3;

	/**
	 * Copy out .ser file to temporary directory for tests and load.
	 */
	@BeforeAll
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/chr15_whammp3.ser";
		ResourceUtils.copyResourceToFile("/chr15_whammp3.ser", new File(dbPath));
		jvData = new JannovarDataSerializer(dbPath).load();

		whammp3 = jvData.getTmByAccession().get("ENST00000400153.2");
//		jun = jvData.getTmByAccession().get("NM_002228.3");
	}

	@Test
	public void testHgncId() throws Exception {
		Assertions.assertEquals("HGNC:27892", whammp3.getAltGeneIDs().get("HGNC_ID"));
	}

}
