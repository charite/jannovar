package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptModel;
import jannovar.reference.TranscriptModelFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InsertionAnnotationBuilderTest {

	/** transcript on forward strand */
	TranscriptModel transcriptForward;
	/** transcript on reverse strand */
	TranscriptModel transcriptReverse;
	/** transcript info on forward strand */
	TranscriptInfo infoForward;
	/** transcript info on reverse strand */
	TranscriptInfo infoReverse;

	@Before
	public void setUp() {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001anx.3	chr1	+	6640062	6649340	6640669	6649272	11	6640062,6640600,6642117,6645978,6646754,6647264,6647537,6648119,6648337,6648815,6648975,	6640196,6641359,6642359,6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,	P10074	uc001anx.3");
		this.transcriptForward
				.setSequence("cgtcacgtccggcgcggagacggtggagtctccgcactgtcggcggggtacgcatagccgggcactaggttcgtgggctgtggaggcgacggagcagggggccagtggggccagctcagggaggacctgcctgggagctttctcttgcataccctcgcttaggctggccggggtgtcacttctgcctccctgccctccagaccatggacggctccttcgtccagcacagtgtgagggttctgcaggagctcaacaagcagcgggagaagggccagtactgcgacgccactctggacgtggggggcctggtgtttaaggcacactggagtgtccttgcctgctgcagtcactttttccagagcctctacggggatggctcagggggcagtgtcgtcctccctgctggcttcgctgagatctttggcctcttgttggactttttctacactggtcacctcgctctcacctcagggaaccgggatcaggtgctcctggcagccagggagttgcgagtgccagaggccgtagagctgtgccagagcttcaagcccaaaacttcagtgggacaggcagcaggtggccagagtgggctggggccccctgcctcccagaatgtgaacagccacgtcaaggagccggcaggcttggaagaagaggaagtttcgaggactctgggtctagtccccagggatcaggagcccagaggcagtcatagtcctcagaggccccagctccattccccagctcagagtgagggcccctcctccctctgtgggaaactgaagcaggccttgaagccttgtccccttgaggacaagaaacccgaggactgcaaagtgcccccaaggcccttagaggctgaaggtgcccagctgcagggcggcagtaatgagtgggaagtggtggttcaagtggaggatgatggggatggcgattacatgtctgagcctgaggctgtgctgaccaggaggaagtcaaatgtaatccgaaagccctgtgcagctgagccagccctgagcgcgggctccctagcagctgagcctgctgagaacagaaaaggtacagcggtgccggtcgaatgccccacatgtcataaaaagttcctcagcaaatattatctaaaagtccacaacaggaaacatactggggagaaaccctttgagtgtcccaaatgtgggaagtgttactttcggaaggagaacctcctggagcatgaagcccggaattgcatgaaccgctcggaacaggtcttcacgtgctctgtgtgccaggagacattccgccgaaggatggagctgcgggtgcacatggtgtctcacacaggggagatgccctacaagtgttcctcctgctcccagcagttcatgcagaagaaggacttgcagagccacatgatcaaacttcatggagcccccaagccccatgcatgccccacctgtgccaagtgcttcctgtctcggacagagctgcagctgcatgaagctttcaagcaccgtggtgagaagctgtttgtgtgtgaggagtgtgggcaccgggcctcgagccggaatggcctgcagatgcacatcaaggccaagcacaggaatgagaggccacacgtatgtgagttctgcagccacgccttcacccaaaaggccaatctcaacatgcacctgcgcacacacacgggtgagaagcccttccagtgccacctctgtggcaagaccttccgaacccaagccagcctggacaagcacaaccgcacccacaccggggaaaggcccttcagttgcgagttctgtgaacagcgcttcactgagaaggggcccctcctgaggcacgtggccagccgccatcaggagggccggccccacttctgccagatatgcggcaagaccttcaaagccgtggagcaactgcgtgtgcacgtcagacggcacaagggggtgaggaagtttgagtgcaccgagtgtggctacaagtttacccgacaggcccacctgcggaggcacatggagatccacgaccgggtagagaactacaacccgcggcagcgcaagctccgcaacctgatcatcgaggacgagaagatggtggtggtggcgctgcagccgcctgcagagctggaggtgggctcggcggaggtcattgtggagtccctggcccagggcggcctggcctcccagctccccggccagagactgtgtgcagaggagagcttcaccggcccaggtgtcctggagccctccctcatcatcacagctgctgtccccgaggactgtgacacatagcccattctggccaccagagcccacttggccccacccctcaataaaccgtgtggctttggactctcgtaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("ZBTB48");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq: NM_005341.3

		this.transcriptReverse = TranscriptModelFactory
				.parseKnownGenesLine("uc001bgu.3	chr1	-	23685940	23696357	23688461	23694498	4	23685940,23693534,23694465,23695858,	23689714,23693661,23694558,23696357,	Q9C0F3	uc001bgu.3");
		this.transcriptReverse
				.setSequence("aataagctgctatattctttttccatcacttccctctccaaggctacagcgagctgggagctcttccccacgcagaatgcctgctttccccagtgctcgacttccattgtctaattccctcatcctggctggggaaagggagagctgcgagtcctcccgttccgaggaactccagctgaatgcagcttagttgctggtggtttctcggccagcctctgtggtctcagggatctgcctatgagcctgtggtttctgagctgcctgcgagtctgaggcctcgggaatctgagtctttaggatcagcctacgatatctgggcttcgcctgcaagtctacgaattcgagatctacctgcgggtctgagacctccgggacctgcccgtgctctctagaatcttcctgaacgccaggtctgagagaacgctgcggctctggaacccgttcgcggtctctcaggttttggagacgacgatctagtggatcttttgcgggacaggagcgctgtctgctagctgcttttcctgctctctctccctggaggcgaacccttgtgctcgagatggcagccaccctgctcatggctgggtcccaggcacctgtgacgtttgaagatatggccatgtatctcacccgggaagaatggagacctctggacgctgcacagagggacctttaccgggatgttatgcaggagaattatggaaatgttgtctcactagattttgagatcaggagtgagaacgaggtaaatcccaagcaagagattagtgaagatgtacaatttgggactacatctgaaagacctgctgagaatgctgaggaaaatcctgaaagtgaagagggctttgaaagcggagataggtcagaaagacaatggggagatttaacagcagaagagtgggtaagctatcctctccaaccagtcactgatctacttgtccacaaagaagtccacacaggcatccgctatcatatatgttctcattgtggaaaggccttcagtcagatctcagaccttaatcgacatcagaagacccacactggagacagaccctataaatgttatgaatgtggaaaaggcttcagtcgcagctcacaccttattcagcatcaaagaacacatactggggagaggccttatgactgtaacgagtgtgggaaaagttttggaagaagttctcacctgattcagcatcagacaatccacactggagagaagcctcacaaatgtaatgagtgtggaaaaagtttctgccgtctctctcacctaatccaacaccaaaggacccacagtggtgagaaaccctatgagtgtgaggagtgtgggaaaagcttcagccggagctctcacctagctcagcaccagaggacccacacgggtgagaaaccttatgaatgtaacgaatgtggccgaggcttcagtgagagatctgatctcatcaaacactatcgagtccacacaggggagaggccctacaagtgtgatgagtgtgggaagaatttcagtcagaactccgaccttgtgcgtcatcgcagagcccacacgggagagaagccataccactgtaacgaatgtggggaaaatttcagccgcatctcacacttggttcagcaccagagaactcacactggagagaagccatatgaatgcaatgcttgtgggaaaagcttcagccggagctctcatctcatcacacaccagaaaattcacactggagagaagccttatgagtgtaatgagtgttggcgaagctttggtgaaaggtcagatctaattaaacatcagagaacccacacaggggagaagccctacgagtgtgtgcagtgtgggaaaggtttcacccagagctccaacctcatcacacatcaaagagttcacacgggagagaaaccttatgaatgtaccgaatgtgagaagagtttcagcaggagctcagctcttattaaacataagagagttcatacggactaagctgtaattatgatggctgagaaatgattcatttgaagatacaattttatttgatatcaatgaacgccctcaagactgagctgcttttatcatactctcctagttgtgggccacgatttaaaccatcagagatgacaagccatttgaaattctgaccctcagctttgggaatgttatctcctccaaaatggtgatttttattcactcaatgggttacttcattaaaagcagccccacaagtaactggaaatctgaagaccaggggacaaatgctggtgaatgcttaggcctggaaatggagtaaatctttcaatgttattttctcccatccttggcccaaggaactatgctaagtgaaacgtgggactgtaatagggtggtaatggctgctttggaaaaaggcaactagagactctgcctaaattgccacacctattcacacaccatagtagttgggcacacacatcttcccttccaaagggctttttccttgagttgctcatgcatttgtatcttttccatcttcctgagggcaagattttgcacgatgaaggcaatgattgtaacttttctccttctcattgtttctaattagctcctttaaagcttgcatctttgtgaaggctaactgaagatacggttggaaaggaaaaatgagacacaggtttggggaccaaggacccatcaatgatggtgactttagcagaagatgcccacagttattactgccattaatcagatttatgaattttctttggggatcactatagggaatattgtatagaaaatatcttcaagaaaagataggaccatcagtgacagttaagtgtaaggagcaagtggaattgagtccttcagggaaggaaccacagagtcccttcccaaggaatgtaggtcgtttctgtgttctttcccttctaatctttaagatcaactcttcctatcctgctaactctaagatttgataagggccacatcccagtgtttatcttagcttgcatcagggcatgtgtatgtacagtaatgtgtattcctgtggtttttctaatagaaactgaatttacagagacttagcatgttcttgggtgatgtgagtcatgtgacagaagtacagacataactccaatgtgagaaatgtccttttttcattatggaaaataatttaaacactagtgctttagtgtgcactctcctgtaaggtctgtctttgtacagagctaagcacttgtttgtatgtgtttgtcaattgtggaagataatgaccagacaaataggtcgattgtcctattctcagaatgaattatcttctatggtaatgaagaactctttggcttagtcagaaggaattaacgaacctcggtaggaatgtatttccatcctcccaccctacagatataagaggttaaaataacagttcgcccaatttaagcccagtagtgtcagttttcctaatctcagtccaggtaggaattaagaaatatctcaagtgttgatgctatccaagcatgttggggtggaagggaattggtgcccagaaaatgggactggagtgaggaatatcttttcttttgagagtacccccagtttatttctactgtgctttattgctactgttctttattgtgaatgttgtaacattttaaaaatgttttgccatagctttttaggacttggtgttaaaggagccagtggtctctctgggtgggtactataatgagttattgtgacccacagctgtgtgggaccacatcacttgttaataacacaacctttaaagtaacccatcttccaggggggttccttcatgttgccactcctttttaaggacaaactcaggcaaggagcatgtttttttgttatttacaaaatctagcagactgtgggtatccatattttaattgtcgggtgacacatgttcttggtaactaaactcaaatatgtcttttctcatatatgttgctgatggttttaataaatgtcaaagttctcctgttgcttctgtgagccactatgggtatcagcttgggagtggccatagatgaccgcatttccatgacctaactgtatttcacccccttttccttccctactgttcttgccccaccccaaccagttcctgctgctgcttttggcttcttggaggtgaagggcttaaaacaaggcttctaagcacccagctatctccatacatgaacaatctagctgggaaacttaagggacaagggccacaccagctgtctcctctttctgccaattgttgcccgtttgctgtgttgaactttgtatagaactcatgcatcagactcccttcactaatgctttttgcatgccttctgctcccaagtccctggctgcctctgcacatcccgtgaacactttgtgcctgttttctatggttgtggagaattaatgaacaaatcaatatgtagaacagttttccttatggtattggtcacagttatcctagtgtttgtattattctaacaatattctataattaaaaatataatttttaaagtca"
						.toUpperCase());
		this.transcriptReverse.setGeneSymbol("ZNF436");
		this.infoReverse = new TranscriptInfo(this.transcriptReverse);
		// RefSeq: NM_001077195.1
	}

	@Test
	public void testForwardOneBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some one-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		// TODO(holtgrem): Mutalyzer does not have the Met1
		Assert.assertEquals("uc001anx.3:exon2:c.1_2insG:p.Met1?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED), "", "A");
		Annotation annotation2 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2);
		// TODO(holtgrem): Mutalyzer does not have the Met1
		Assert.assertEquals("uc001anx.3:exon2:c.2_3insA:p.Met1?", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2.getVariantType());

		// Try to insert all non-duplicate NTs between 3 and 4.

		GenomeChange change3a = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "", "A");
		Annotation annotation3a = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3a);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insA:p.Asp2Argfs*37", annotation3a.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3a.getVariantType());

		GenomeChange change3c = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "", "C");
		Annotation annotation3c = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3c);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insC:p.Asp2Argfs*37", annotation3c.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3c.getVariantType());

		GenomeChange change3t = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "", "T");
		Annotation annotation3t = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3t);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insT:p.Asp2*", annotation3t.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3t.getVariantType());

		// Try to insert all non-duplicate NTs between 4 and 5.

		GenomeChange change4c = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "", "C");
		Annotation annotation4c = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4c);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insC:p.Asp2Alafs*37", annotation4c.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3c.getVariantType());

		GenomeChange change4t = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "", "T");
		Annotation annotation4t = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4t);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insT:p.Asp2Valfs*37", annotation4t.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3t.getVariantType());

		// Try to insert all non-duplicate NTs between 5 and 6.

		GenomeChange change5g = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "", "G");
		Annotation annotation5g = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5g);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insG:p.Asp2Glufs*37", annotation5g.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3t.getVariantType());

		GenomeChange change5t = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "", "T");
		Annotation annotation5t = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5t);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insT:p.Gly3Argfs*36", annotation5t.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3c.getVariantType());
	}

	@Test
	public void testForwardTwoBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some two-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "", "GA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		// TODO(holtgrem): Mutalyzer does not have the Met1
		Assert.assertEquals("uc001anx.3:exon2:c.1_2insGA:p.Met1?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED), "", "AG");
		Annotation annotation2 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2);
		// TODO(holtgrem): Mutalyzer does not have the Met1
		Assert.assertEquals("uc001anx.3:exon2:c.2_3insAG:p.Met1?", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2.getVariantType());

		// Try to insert some non-duplicate NT pairs between 3 and 4.

		GenomeChange change3ac = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "", "AC");
		Annotation annotation3ac = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3ac);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insAC:p.Asp2Thrfs*10", annotation3ac.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3ac.getVariantType());

		GenomeChange change3cg = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "", "CG");
		Annotation annotation3cg = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3cg);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insCG:p.Asp2Argfs*10", annotation3cg.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3cg.getVariantType());

		GenomeChange change3ta = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "", "TA");
		Annotation annotation3ta = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3ta);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insTA:p.Asp2*", annotation3ta.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3ta.getVariantType());

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeChange change4ct = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "",
				"CT");
		Annotation annotation4ct = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4ct);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insCT:p.Asp2Alafs*10", annotation4ct.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3cg.getVariantType());

		GenomeChange change4tg = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "",
				"TG");
		Annotation annotation4tg = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4tg);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insTG:p.Asp2Valfs*10", annotation4tg.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3ta.getVariantType());

		// Try to insert some non-duplicate NT pairs between 5 and 6.

		GenomeChange change5gc = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "",
				"GC");
		Annotation annotation5gc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5gc);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insGC:p.Asp2Glufs*10", annotation5gc.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3ta.getVariantType());

		GenomeChange change5ta = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "",
				"TA");
		Annotation annotation5ta = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5ta);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insTA:p.Gly3Thrfs*9", annotation5ta.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3cg.getVariantType());
	}

	@Test
	public void testForwardOnFourBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some four-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeChange change4actagact = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED),
				"", "ACTAGACT");
		Annotation annotation4actagact = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4actagact);
		Assert.assertEquals("uc001anx.3:exon2:c.6_7insTAGACTAC:p.Gly3*",
				annotation4actagact.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation4actagact.getVariantType());

		GenomeChange change4cgtg = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "",
				"CGTG");
		Annotation annotation4cgtg = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4cgtg);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insCGTG:p.Asp2Alafs*2", annotation4cgtg.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4cgtg.getVariantType());

	}
}
