package de.charite.compbio.jannovar.annotation.builders;

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
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for annotating variants that lie on transcripts which align to the reference with indels.
 *
 * gene: LTBP4, transcript: NM_003573.2
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class AnnotateIndelTranscriptVariantsLtbp4Test {

	private static Stream<Arguments> data() {
		return Stream.of(
			// Cover LTBP4 more or less systematically, VV uses NP_000248;       //__VV prediction__
			Arguments.of("19-41128880-C-T", "g.41128880C>T", "c.3600C>T", "(=)"),          // p.(His1200=)
			Arguments.of("19-41128880-C-A", "g.41128880C>A", "c.3600C>A", "(His1200Gln)"), // p.(His1200Gln)
			Arguments.of("19-41128880-C-G", "g.41128880C>G", "c.3600C>G", "(His1200Gln)"), // p.(His1200Gln)
			Arguments.of("19-41128881-G-A", "g.41128881G>A", "c.3601G>A", "(Gly1201Ser)"), // p.(Gly1201Ser)
			Arguments.of("19-41128881-G-C", "g.41128881G>C", "c.3601G>C", "(Gly1201Arg)"), // p.(Gly1201Arg)
			Arguments.of("19-41128881-G-T", "g.41128881G>T", "c.3601G>T", "(Gly1201Cys)"), // p.(Gly1201Cys)
			Arguments.of("19-41128882-G-A", "g.41128882G>A", "c.3602G>A", "(Gly1201Asp)"), // p.(Gly1201Asp)
			Arguments.of("19-41128882-G-C", "g.41128882G>C", "c.3602G>C", "(Gly1201Ala)"), // p.(Gly1201Ala)
			Arguments.of("19-41128882-G-T", "g.41128882G>T", "c.3602G>T", "(Gly1201Val)"), // p.(Gly1201Val)
			Arguments.of("19-41128883-C-A", "g.41128883C>A", "c.3603C>A", "(=)"), // p.(Gly1201=)
			Arguments.of("19-41128883-C-G", "g.41128883C>G", "c.3603C>G", "(=)"), // p.(Gly1201=)
			Arguments.of("19-41128883-C-T", "g.41128883C>T", "c.3603C>T", "(=)"), // p.(Gly1201=)
			Arguments.of("19-41128884-C-A", "g.41128884C>A", "c.3604C>A", "(=)"), // p.(Arg1202=)
			Arguments.of("19-41128884-C-G", "g.41128884C>G", "c.3604C>G", "(Arg1202Gly)"), // p.(Arg1202Gly)
			Arguments.of("19-41128884-C-T", "g.41128884C>T", "c.3604C>T", "(Arg1202Trp)"), // p.(Arg1202Trp)
			Arguments.of("19-41128885-G-A", "g.41128885G>A", "c.3605G>A", "(Arg1202Gln)"), // p.(Arg1202Gln)
			Arguments.of("19-41128885-G-C", "g.41128885G>C", "c.3605G>C", "(Arg1202Pro)"), // p.(Arg1202Pro)
			Arguments.of("19-41128885-G-T", "g.41128885G>T", "c.3605G>T", "(Arg1202Leu)"), // p.(Arg1202Leu)
			Arguments.of("19-41128880-C-CG", "g.41128882_41128883insG",  // TODO: g.41128882dup
				"c.3602dup",  // c.3602dup
				"(Arg1202Profs*60)")
		);
	}

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
	@BeforeAll
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/hg19_refseq_indels.ser";
		ResourceUtils.copyResourceToFile("/hg19_refseq_indels.ser", new File(dbPath));
		jvData = new JannovarDataSerializer(dbPath).load();

		tx = jvData.getTmByAccession().get("NM_003573.2");
	}

	@ParameterizedTest
	@MethodSource("data")
	public void test(String fInput, String fExpectedGenomic, String fExpectedNucleotides, String fExpectedProtein) throws InvalidGenomeVariant {
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
		Assertions.assertEquals(fExpectedGenomic, result.getGenomicNTChangeStr());
		Assertions.assertEquals(fExpectedNucleotides, result.getCDSNTChangeStr());
		Assertions.assertEquals(fExpectedProtein,
			result.getProteinChange().toHGVSString(AminoAcidCode.THREE_LETTER));
	}

}
