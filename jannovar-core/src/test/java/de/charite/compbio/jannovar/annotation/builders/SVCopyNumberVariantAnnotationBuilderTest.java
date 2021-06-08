package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.SVCopyNumberVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SVCopyNumberVariantAnnotationBuilderTest {

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
	@BeforeAll
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/chr1_oma1_to_jun.ser";
		ResourceUtils.copyResourceToFile("/chr1_oma1_to_jun.ser", new File(dbPath));
		jvData = new JannovarDataSerializer(dbPath).load();

		oma1 = jvData.getTmByAccession().get("NM_145243.3");
//		jun = jvData.getTmByAccession().get("NM_002228.3");
	}

	@Test
	public void testTranscriptDuplicationOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58929877),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59028960),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.58929878, genomePos2=1:g.59028961, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonDuplicationOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58968128),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58994820),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.58968129, genomePos2=1:g.58994821, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonOverlapStartDuplicatedOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59004875),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59005052),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.59004876, genomePos2=1:g.59005053, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonOverlapStopDuplicatedOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 5894659),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946671),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.5894660, genomePos2=1:g.58946672, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonOverlapDuplicationOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946844),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946946),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.58946845, genomePos2=1:g.58946947, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonOverlapNoFrameshiftOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946843),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946946),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.58946844, genomePos2=1:g.58946947, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntronicOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58957157),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58963900),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.58957158, genomePos2=1:g.58963901, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[COPY_NUMBER_CHANGE, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testUpstreamOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012449),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012899),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.59012450, genomePos2=1:g.59012900, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[UPSTREAM_GENE_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testDownstreamOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58942733),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946074),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.58942734, genomePos2=1:g.58946075, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[DOWNSTREAM_GENE_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntergenicOma1() {
		final SVCopyNumberVariant svCNV = new SVCopyNumberVariant(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59017449),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59017899),
			0, 0, 0, 0
		);
		final SVAnnotation anno = new SVCopyNumberVariantAnnotationBuilder(oma1, svCNV).build();

		Assertions.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assertions.assertEquals(
			"SVCopyNumberVariant{genomePos=1:g.59017450, genomePos2=1:g.59017900, posCILowerBound=0, " +
				"posCIUpperBound=0, pos2CILowerBound=0, pos2CIUpperBound=0}",
			anno.getVariant().toString()
		);
		Assertions.assertEquals(
			"[INTERGENIC_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

}
