package de.charite.compbio.jannovar.cmd.annotate_vcf;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import de.charite.compbio.jannovar.Jannovar;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

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
	static File dbPath;

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
	@TempDir
	static File tmpFolder;

	/**
	 * Create a new VCF path using the header from the given file name and return the path to the output VCF file.
	 */
	public static String createTempVcfWithLine(String pathHeaderVcf, String vcfLine) throws Exception {
		final String tmpPath = new File(tmpFolder, "test_file.vcf").toString();
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
	@BeforeAll
	public static void setUpClass() throws Exception {
		dbPath = new File(tmpFolder,  "chr1_oma1_to_jun.ser");
		ResourceUtils.copyResourceToFile("/chr1_oma1_to_jun.ser", dbPath);
		jvData = new JannovarDataSerializer(dbPath.toString()).load();

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

		final String outPath = new File(tmpFolder, "output.jv.vcf").toString();
		String[] argv = new String[]{"annotate-vcf", "-o", outPath, "-d", dbPath.toString(), "-i", tmpVcfFile};
		System.err.println(Joiner.on(" ").join(argv));

		Jannovar.main(argv);
		return outPath;
	}

}
