package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.SVBreakend;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.testutils.ResourceUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class SVBreakendAnnotationBuilderTest {

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
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946660),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.58946661, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, STRUCTURAL_VARIANT, CODING_SEQUENCE_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntronicFivePrimeOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58957157),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.58957158, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, STRUCTURAL_VARIANT, INTRON_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonicFivePrimeOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58946502),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.58946503, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, THREE_PRIME_UTR_EXON_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testExonicThreePrimeOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012403),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.59012404, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, FIVE_PRIME_UTR_EXON_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntronicThreePrimeOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012355),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.59012356, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, FIVE_PRIME_UTR_INTRON_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testUpstreamOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59012449),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.59012450, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, UPSTREAM_GENE_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testDownstreamOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 58941393),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.58941394, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, DOWNSTREAM_GENE_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

	@Test
	public void testIntergenicOma1() {
		final SVBreakend svBnd = new SVBreakend(
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 59017449),
			new GenomePosition(jvData.getRefDict(), Strand.FWD, oma1.getChr(), 42),
			0, 0, 0, 0, "A", "T", SVBreakend.Side.LEFT_END
		);
		final SVAnnotation anno = new SVBreakendAnnotationBuilder(oma1, svBnd).build();

		Assert.assertEquals(anno.getTranscript().toString(), "NM_145243.3(1:g.58946391_59012446)");
		Assert.assertEquals(
			"SVBreakend{genomePos=1:g.59017450, genomePos2=1:g.43, posCILowerBound=0, posCIUpperBound=0" +
				", pos2CILowerBound=0, pos2CIUpperBound=0, posRefBases=A, pos2RefBases=T, side=LEFT_END}",
			anno.getVariant().toString()
		);
		Assert.assertEquals(
			"[TRANSLOCATION, INTERGENIC_VARIANT, STRUCTURAL_VARIANT, CODING_TRANSCRIPT_VARIANT]",
			anno.getEffects().toString()
		);
	}

}
