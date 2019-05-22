package de.charite.compbio.jannovar.annotation.builders;

import static org.junit.Assert.assertEquals;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for annotating variants that lie on transcripts which align to the reference with indels.
 *
 * gene: HFM1, transcript: NM_001017975.3
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@RunWith(Parameterized.class) public class AnnotateIndelTranscriptVariantsHfm1Test {

	@Parameters(name = "{index}: {0} => {1}/{2}/{3}") public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// Cover HFM1 more or less systematically, VV uses NP_001017975.4;   //__VV prediction__
			{ "1-91809014-C-T", "g.91809014C>T", "c.2308G>A", "(Asp770Asn)" },   // p.Asp770Asn
			{ "1-91782027-G-A", "g.91782027G>A", "c.2819C>T", "(Thr940Ile)" },   // p.Thr940Ile
			{ "1-91855647-A-T", "g.91855647A>T", "c.494+4003T>A", "(=)" },       // p.?
			{ "1-91791947-A-C", "g.91791947A>C", "c.2336-1624T>G", "(=)" },      // p.
		});
	}

	@Parameter(/*0*/) public String fInput;

	@Parameter(1) public String fExpectedGenomic;
	@Parameter(2) public String fExpectedNucleotides;
	@Parameter(3) public String fExpectedProtein;

	/**
	 * Path to .ser file.
	 */
	static String dbPath;

	/**
	 * The {@link JannovarData} to load the test data into.
	 */
	static JannovarData jvData;

	/**
	 * The transcript to annotate with.
	 */
	static TranscriptModel tx;

	/**
	 * Copy out .ser file to temporary directory for tests and load.
	 */
	@BeforeClass public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/hg19_refseq_indels.ser";
		ResourceUtils.copyResourceToFile("/hg19_refseq_indels.ser", new File(dbPath));
		jvData = new JannovarDataSerializer(dbPath).load();

		tx = jvData.getTmByAccession().get("NM_001017975.3");
	}

	@Test public void test() throws InvalidGenomeVariant {
		// Build genome change
		final String[] tokens = fInput.split("-");
		final GenomePosition gPos = new GenomePosition(jvData.getRefDict(), Strand.FWD,
			jvData.getRefDict().getContigNameToID().get(tokens[0]), Integer.parseInt(tokens[1]),
			PositionType.ONE_BASED);
		final GenomeVariant change = new GenomeVariant(gPos, tokens[2], tokens[3]);

		// Run code under test.
		final AnnotationBuilderDispatcher dispatcher = new AnnotationBuilderDispatcher(tx, change,
			new AnnotationBuilderOptions());
		final Annotation result = dispatcher.build();

		// Check expectations.
		assertEquals(fExpectedGenomic, result.getGenomicNTChangeStr());
		assertEquals(fExpectedNucleotides, result.getCDSNTChangeStr());
		assertEquals(fExpectedProtein,
			result.getProteinChange().toHGVSString(AminoAcidCode.THREE_LETTER));
	}

}
