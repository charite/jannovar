package de.charite.compbio.jannovar.cmd.annotate_vcf;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import de.charite.compbio.jannovar.Jannovar;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import org.junit.BeforeClass;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Base class for tests that test annotation of structural variants.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarAnnotateVCFWithSVTestBase {

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
	static TranscriptModel oma1;

	/**
	 * Temporary directory to use
	 */
	static TemporaryFolder tmpFolder;

	/**
	 * Create a new VCF path using the header from the given file name and return the path to the output VCF file.
	 */
	public static String createTempVcfWithLine(String pathHeaderVcf, String vcfLine) throws Exception {
		final String tmpPath = tmpFolder.newFolder() + "/test_file.vcf";
		ResourceUtils.copyResourceToFile(pathHeaderVcf, new File(tmpPath));
		try (FileWriter fw = new FileWriter(new File(tmpPath), true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter pw = new PrintWriter(bw)) {
			pw.println(vcfLine);
		}
		return tmpPath;
	}

	/**
	 * Copy out .ser file to temporary directory for tests and load.
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		tmpFolder = new TemporaryFolder();
		tmpFolder.create();
		dbPath = tmpFolder.newFolder() + "/chr1_oma1_to_jun.ser";
		ResourceUtils.copyResourceToFile("/chr1_oma1_to_jun.ser", new File(dbPath));
		jvData = new JannovarDataSerializer(dbPath).load();

		oma1 = jvData.getTmByAccession().get("NM_145243.3");
//		jun = jvData.getTmByAccession().get("NM_002228.3");
	}

	/**
	 * Load load body from the VCF file at {@code outPath}.
	 */
	protected String loadVcfBody(String outPath) throws IOException {
		return Arrays.asList(
			Files
				.asCharSource(new File(outPath), Charsets.UTF_8)
				.read()
				.split("\r?\n"))
			.stream()
			.filter(line -> !line.startsWith("#"))
			.collect(Collectors.joining());
	}

	/**
	 * Run Jannovar on VCF file with the given line and return path to temporary output file.
	 */
	protected String runJannovarOnVCFLine(String pathHeader, String vcfLine) throws Exception {
		final String tmpVcfFile = createTempVcfWithLine(pathHeader, vcfLine);

		final File outFolder = tmpFolder.newFolder();
		final String outPath = outFolder.toString() + "/output.jv.vcf";
		String[] argv = new String[]{"annotate-vcf", "-o", outPath, "-d", dbPath, "-i", tmpVcfFile};
		System.err.println(Joiner.on(" ").join(argv));

		Jannovar.main(argv);
		return outPath;
	}

}
