package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.SVInsertion;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class SVInsertionAnnotationBuilderTest {

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
	 * Copy out .ser file to temporary directory for tests and load.
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/chr1_oma1_to_jun.ser";
		ResourceUtils.copyResourceToFile("/chr1_oma1_to_jun.ser", new File(dbPath));
		jvData = new JannovarDataSerializer(dbPath).load();

		oma1 = jvData.getTmByAccession().get("NM_145243.3");
//		jun = jvData.getTmByAccession().get("NM_002228.3");
	}

	@Test
	public void testExonicOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946660),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.58946661, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, STRUCTURAL_VARIANT, CODING_SEQUENCE_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntronicFivePrimeOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58957157),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.58957158, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, STRUCTURAL_VARIANT, INTRON_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonicThreerimeOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946502),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.58946503, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, THREE_PRIME_UTR_EXON_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonicFivePrimeOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012403),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.59012404, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, FIVE_PRIME_UTR_EXON_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntronicThreePrimeOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012355),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.59012356, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, FIVE_PRIME_UTR_INTRON_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testUpstreamOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012449),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.59012450, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, UPSTREAM_GENE_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testDownstreamOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58942733),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.58942734, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INSERTION, DOWNSTREAM_GENE_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntergenicOma1() {
		final SVInsertion svIns = new SVInsertion(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59017449),
			0, 0
		);
		final SVAnnotation anno = new SVInsertionAnnotationBuilder(oma1, svIns).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVInsertion{genomePos=1:g.59017450, posCILowerBound=0, posCIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[INTERGENIC_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

}
