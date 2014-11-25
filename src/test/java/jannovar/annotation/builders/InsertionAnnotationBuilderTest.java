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

// TODO(holtgrem): What exactly should be counted as stop gain?
// TODO(holtgrem): Convert more from UTR3AnnotationTest from the old tests, also for other variant types

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
	public void testForwardUpstream() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640062, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("dist=0", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UPSTREAM, anno.getVariantType());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649340, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("dist=0", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.DOWNSTREAM, anno.getVariantType());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeChange {
		// upstream intergenic
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6639062, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("dist=1000", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, anno.getVariantType());

		// downstream intergenic
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6650340, PositionType.ZERO_BASED), "", "A");
		Annotation anno2 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("dist=1000", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, anno2.getVariantType());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6646098, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.1044+8_1044+9insA", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTRONIC, anno.getVariantType());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640669, PositionType.ZERO_BASED), "", "C");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon2:c.-1dup", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR5, anno.getVariantType());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649272, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon11:c.2067_*1insA", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, anno.getVariantType());
	}

	@Test
	public void testForwardSplicing() throws InvalidGenomeChange {
		// TODO(holtgrem): test more cases
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6642117, PositionType.ZERO_BASED), "", "ACT");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon3:c.691-1_691insACT", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, anno.getVariantType());
	}

	@Test
	public void testForwardThreeBasesNoFrameShiftInsertion() throws InvalidGenomeChange {
		// Tests insertion of three bases (smallest no-frameshift insertion).

		// TODO(holtgrem): The WT stop codon is replaced by another one -- duplication.
		// TODO(holtgrem): The WT start codon is replaced by another one -- duplication.

		// The WT stop codon is replaced by another one.
		GenomeChange change1agc = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "",
				"AGC");
		Annotation annotation1agc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1agc);
		Assert.assertEquals("uc001anx.3:exon11:c.2066_2067insAGC:p.=", annotation1agc.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, annotation1agc.getVariantType());

		// The WT stop codon is destroyed but there is a new one downstream
		GenomeChange change1tgc = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "",
				"TGC");
		Annotation annotation1tgc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1tgc);
		Assert.assertEquals("uc001anx.3:exon11:c.2066_2067insTGC:p.*689Tyrext*24",
				annotation1tgc.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation1tgc.getVariantType());

		// Test case where the start codon is destroyed.
		GenomeChange change2agc = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "",
				"AGC");
		Annotation annotation2agc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2agc);
		Assert.assertEquals("uc001anx.3:exon2:c.1_2insAGC:p.0?", annotation2agc.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2agc.getVariantType());

		// Test cases where the start codon is not subjected to an insertion.

		// Directly insert stop codon.
		GenomeChange change3taa = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"TAA");
		Annotation annotation3taa = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3taa);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insTAA:p.Asp2*", annotation3taa.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3taa.getVariantType());

		// Directly insert some base and then a stop codon.
		GenomeChange change3tcctaa = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"TCCTAA");
		Annotation annotation3tcctaa = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3tcctaa);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insTCCTAA:p.Asp2_Gly3delinsSer",
				annotation3tcctaa.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation3tcctaa.getVariantType());

		// Insertion without a new stop codon that is no duplication.
		GenomeChange change4tcctcctcc = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED),
				"", "TCCTCCTCC");
		Annotation annotation4tcctcctcc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4tcctcctcc);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insTCCTCCTCC:p.Met1_Asp2insSerSerSer",
				annotation4tcctcctcc.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_INSERTION, annotation4tcctcctcc.getVariantType());

		// Insertion without a new stop codon that is a duplication.
		GenomeChange change5gatggc = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"GATGGC");
		Annotation annotation5gatggc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5gatggc);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insTGGCGA:p.Asp2_Gly3dup", annotation5gatggc.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation5gatggc.getVariantType());
	}

	@Test
	public void testForwardOneBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some one-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon2:c.1_2insG:p.0?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED), "", "A");
		Annotation annotation2 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon2:c.2_3insA:p.0?", annotation2.getVariantAnnotation());
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
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4c.getVariantType());

		GenomeChange change4t = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "", "T");
		Annotation annotation4t = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4t);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insT:p.Asp2Valfs*37", annotation4t.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4t.getVariantType());

		// Try to insert all non-duplicate NTs between 5 and 6.

		GenomeChange change5g = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "", "G");
		Annotation annotation5g = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5g);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insG:p.Asp2Glufs*37", annotation5g.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation5g.getVariantType());

		GenomeChange change5t = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "", "T");
		Annotation annotation5t = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5t);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insT:p.Gly3Argfs*36", annotation5t.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation5t.getVariantType());

		// It appears to be impossible to force a stop loss for this transcript.

		// Tests for stop shift.
		GenomeChange change6t = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "", "T");
		Annotation annotation6t = InsertionAnnotationBuilder.buildAnnotation(infoForward, change6t);
		Assert.assertEquals("uc001anx.3:exon11:c.2066_2067insT:p.*689Tyrext*15", annotation6t.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation6t.getVariantType());

		GenomeChange change6c = new GenomeChange(new GenomePosition('+', 1, 6649270, PositionType.ZERO_BASED), "", "C");
		Annotation annotation6c = InsertionAnnotationBuilder.buildAnnotation(infoForward, change6c);
		Assert.assertEquals("uc001anx.3:exon11:c.2065_2066insC:p.*689Serext*15", annotation6c.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation6c.getVariantType());

		// Test for no change when inserting into stop codon.
		GenomeChange change7g = new GenomeChange(new GenomePosition('+', 1, 6649270, PositionType.ZERO_BASED), "", "G");
		Annotation annotation7g = InsertionAnnotationBuilder.buildAnnotation(infoForward, change7g);
		Assert.assertEquals("uc001anx.3:exon11:c.2065_2066insG:p.=", annotation7g.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, annotation7g.getVariantType());
	}

	@Test
	public void testForwardTwoBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some two-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "", "GA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon2:c.1_2insGA:p.0?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED), "", "AG");
		Annotation annotation2 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon2:c.2_3insAG:p.0?", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2.getVariantType());

		// Try to insert some non-duplicate NT pairs between 3 and 4.

		GenomeChange change3ac = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"AC");
		Annotation annotation3ac = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3ac);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insAC:p.Asp2Thrfs*10", annotation3ac.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3ac.getVariantType());

		GenomeChange change3cg = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"CG");
		Annotation annotation3cg = InsertionAnnotationBuilder.buildAnnotation(infoForward, change3cg);
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insCG:p.Asp2Argfs*10", annotation3cg.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3cg.getVariantType());

		GenomeChange change3ta = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"TA");
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
		Assert.assertEquals("uc001anx.3:exon2:c.6_7insTAGACTAC:p.Gly3*", annotation4actagact.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation4actagact.getVariantType());

		GenomeChange change4cgtg = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "",
				"CGTG");
		Annotation annotation4cgtg = InsertionAnnotationBuilder.buildAnnotation(infoForward, change4cgtg);
		Assert.assertEquals("uc001anx.3:exon2:c.4_5insCGTG:p.Asp2Alafs*2", annotation4cgtg.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4cgtg.getVariantType());
	}

	@Test
	public void testReverseOneBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some one-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results. We perform less tests than for the forward case since the only change is the coordinate system
		// transformation to the reverse strand and the reverse-complementing of the inserted bases.

		// Insert C and G between nucleotides 1 and 2.

		GenomeChange change1c = new GenomeChange(new GenomePosition('+', 1, 23694497, PositionType.ZERO_BASED), "", "C");
		Annotation annotation1c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change1c);
		Assert.assertEquals("uc001bgu.3:exon2:c.1_2insG:p.0?", annotation1c.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1c.getVariantType());

		GenomeChange change1g = new GenomeChange(new GenomePosition('+', 1, 23694497, PositionType.ZERO_BASED), "", "G");
		Annotation annotation1g = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change1g);
		Assert.assertEquals("uc001bgu.3:exon2:c.1_2insC:p.0?", annotation1g.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1g.getVariantType());

		// Insert A and C between nucleotides 2 and 3.

		GenomeChange change2a = new GenomeChange(new GenomePosition('+', 1, 23694496, PositionType.ZERO_BASED), "", "T");
		Annotation annotation2a = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change2a);
		Assert.assertEquals("uc001bgu.3:exon2:c.2_3insA:p.0?", annotation2a.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2a.getVariantType());

		GenomeChange change2c = new GenomeChange(new GenomePosition('+', 1, 23694496, PositionType.ZERO_BASED), "", "G");
		Annotation annotation2c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change2c);
		Assert.assertEquals("uc001bgu.3:exon2:c.2_3insC:p.0?", annotation2c.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2c.getVariantType());

		// Insertions between nucleotides 3 and 4.

		GenomeChange change3a = new GenomeChange(new GenomePosition('+', 1, 23694495, PositionType.ZERO_BASED), "", "T");
		Annotation annotation3a = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change3a);
		Assert.assertEquals("uc001bgu.3:exon2:c.3_4insA:p.Ala2Serfs*16", annotation3a.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3a.getVariantType());

		GenomeChange change3c = new GenomeChange(new GenomePosition('+', 1, 23694495, PositionType.ZERO_BASED), "", "G");
		Annotation annotation3c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change3c);
		Assert.assertEquals("uc001bgu.3:exon2:c.3_4insC:p.Ala2Argfs*16", annotation3c.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3c.getVariantType());

		// Some insertions into stop codon

		GenomeChange change4g = new GenomeChange(new GenomePosition('+', 1, 23688463, PositionType.ZERO_BASED), "", "G");
		Annotation annotation4g = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4g);
		Assert.assertEquals("uc001bgu.3:exon4:c.1411_1412insC:p.*471Serext*7", annotation4g.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4g.getVariantType());

		GenomeChange change4c = new GenomeChange(new GenomePosition('+', 1, 23688463, PositionType.ZERO_BASED), "", "C");
		Annotation annotation4c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4c);
		Assert.assertEquals("uc001bgu.3:exon4:c.1411_1412insG:p.=", annotation4c.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, annotation4c.getVariantType());
	}

	@Test
	public void testReverseOnFourBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some four-nucleotide insertions in the first ten bases and in the stop codon and compared them by
		// hand to Mutalyzer results.

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeChange change4actagact = new GenomeChange(new GenomePosition('+', 1, 23694494, PositionType.ZERO_BASED),
				"", "ACTAGACT");
		Annotation annotation4actagact = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4actagact);
		Assert.assertEquals("uc001bgu.3:exon2:c.4_5insAGTCTAGT:p.Ala2Glufs*16",
				annotation4actagact.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4actagact.getVariantType());

		// This insertion will be shifted.
		GenomeChange change4cgtg = new GenomeChange(new GenomePosition('+', 1, 23694494, PositionType.ZERO_BASED), "",
				"CGTG");
		Annotation annotation4cgtg = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4cgtg);
		Assert.assertEquals("uc001bgu.3:exon2:c.6_7insCGCA:p.Ala3Argfs*16", annotation4cgtg.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4cgtg.getVariantType());

		// Insert whole stop codon.
		GenomeChange change5cgtg = new GenomeChange(new GenomePosition('+', 1, 23694492, PositionType.ZERO_BASED), "",
				"ATTA");
		Annotation annotation5cgtg = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change5cgtg);
		Assert.assertEquals("uc001bgu.3:exon2:c.6_7insTAAT:p.Ala3*", annotation5cgtg.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation5cgtg.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc010slx_2() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010slx.2	chr12	+	49208214	49219026	49208330	49218557	5	49208214,49217126,49217463,49218040,49218451,	49208336,49217249,49217586,49218156,49219026,	F8VV14	uc010slx.2");
		this.transcriptForward
				.setSequence("agaaaggggcgggaggggtactgacccgaggagcggtcccgcgactctgtactccctgctgcccagtcccggccaggacgctgcccggcttagctggggcgcccctcgagaccaggatggagggttcagccgactcctacaccagccgcccatctctggactcagacgtctccctggaggaggaccgggagagtgcccggcgtgaagtagagagccaggctcagcagcagctcgaaagggccaagcacaaacctgtggcatttgcggtgaggaccaatgtcagctactgtggcgtactggatgaggagtgcccagtccagggctctggagtcaactttgaggccaaagattttctgcacattaaagagaagtacagcaatgactggtggatcgggcggctagtgaaagagggcggggacatcgccttcatccccagcccccagcgcctggagagcatccggctcaaacaggagcagaaggccaggagatctgggaacccttccagcctgagtgacattggcaaccgacgctcccctccgccatctctaggtagcctccccccaacccacccattttgggtaggtgggtagctaagacacagggaaagaggggatcctgcccaagtgaactgtgtgggaagggtttgtggagggattggaaggggtggggagaagccagtatctcccctggggacggggataccatgcccccagggacctttcccccttcaccaactttaatcttgtatttccttttccaaaaagccaagcagaagcaaaagcaggtgagtcaaggaagggacctgggctggggggatcatggcttatggctctggggacagtgttctaggcagtcattgttggagggcagaacagagaggggagggagcccagaacctctggatctgccctgacgccaaccaggcatgagacaggcaccagggccatggtttctactgacctcatgtccattctgcaggcggaacatgttcccccatatgacgtggtgccctccatgcggcctgtggtgctggtgggaccctctctgaaaggttatgag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("CACNB3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// no RefSeq

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 12, 49218811, PositionType.ZERO_BASED), "", "T");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010slx.2:exon5:c.*255dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc010vsd_2() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010vsd.2	chr17	-	4534213	4545230	4534894	4544946	14	4534213,4535183,4535475,4536155,4536440,4536708,4539053,4540409,4541511,4541875,4542144,4542345,4542724,4544811,	4535074,4535351,4535576,4536277,4536610,4536795,4539263,4540553,4541672,4541979,4542267,4542427,4542809,4545230,	B7ZA11	uc010vsd.2");
		this.transcriptForward
				.setSequence("ctgcgtgttttcggtccaaatccttttctttttctccctcccgtcaagatagtggtttccactccctgctctcgccaggacaccgccttttggactggggctgaattctgccccttgaagctctgctccttggagctgggggccccagcggtaggcggagttgattggagacctgccacccacattccgaccccaagcgacctccgagagggcggggtctcaggctgggttatttagctcgtccacccttctccaccagaaggagcgaaacatctttgagcaagatgggtctctaccgcatccgcgtgtccactggggcctcgctctatgccggttccaacaaccaggtgcagctgtggctggtcggccagcacggggaggcggcgctcgggaagcgactgtggcccgcacggggcaagggccccggagccggggacgaggtcaggttcccttgttaccgctgggtggagggcaacggcgtcctgagcctgcctgaaggcaccggccgcactgtgggcgaggaccctcagggcctgttccagaaacaccgggaagaagagctggaagagagaaggaagttgtaccggtggggaaactggaaggacgggttaattctgaatatggctggggccaaactatatgacctccctgtggatgagcgatttctggaagacaagagagttgactttgaggtttcgctggccaaggggctggccgacctcgctatcaaagactctctaaatgttctgacttgctggaaggatctagatgacttcaaccggattttctggtgtggtcagagcaagctggctgagcgcgtgcgggactcctggaaggaagatgccttatttgggtaccagtttcttaatggcgccaaccccgtggtgctgaggcgctctgctcaccttcctgctcgcctagtgttccctccaggcatggaggaactgcaggcccagctggagaaggagctggagggaggcacactgttcgaagctgacttctccctgctggatgggatcaaggccaacgtcattctctgtagccagcagcacctggctgcccctctagtcatgctgaaattgcagcctgatgggaaactcttgcccatggtcatccagctccagctgccccgcacaggatccccaccacctccccttttcttgcctacggatcccccaatggcctggcttctggccaaatgctgggtgcgcagctctgacttccagctccatgagctgcagtctcatcttctgaggggacacttgatggctgaggtcattgttgtggccaccatgaggtgcctgccgtcgatacatcctatcttcaagcttataattccccacctgcgatacaccctggaaattaacgtccgggccaggactgggctggtctctgacatgggaattttcgaccagataatgagcactggtgggggaggccacgtgcagctgctcaagcaagctggagccttcctaacctacagctccttctgtccccctgatgacttggccgaccgggggctcctgggagtgaagtcttccttctatgcccaagatgcgctgcggctctgggaaatcatctatcggtatgtggaaggaatcgtgagtctccactataagacagacgtggctgtgaaagacgacccagagctgcagacctggtgtcgagagatcactgaaatcgggctgcaaggggcccaggaccgagggtttcctgtctctttacaggctcgggaccaggtttgccactttgtcaccatgtgtatcttcacctgcaccggccaacacgcctctgtgcacctgggccagctggactggtactcttgggtgcctaatgcaccctgcacgatgcggctgcccccgccaaccaccaaggatgcaacgctggagacagtgatggcgacactgcccaacttccaccaggcttctctccagatgtccatcacttggcagctgggcagacgccagcccgttatggtggctgtgggccagcatgaggaggagtatttttcgggccctgagcctaaggctgtgctgaagaagttcagggaggagctggctgccctggataaggaaattgagatccggaatgcaaagctggacatgccctacgagtacctgcggcccagcgtggtggaaaacagtgtggccatctaagcgtcgccaccctttggttatttcagcccccatcacccaagccacaagctgaccccttcgtggttatagccctgccctcccaagtcccaccctcttcccatgtcccaccctccctagaggggcaccttttcatggtctctgcacccagtgaacacattttactctagaggcatcacctgggaccttactcctctttccttccttcctcctttcctatcttccttcctctctctcttcctctttcttcattcagatctatatggcaaatagccacaattatataaatcatttcaagactagaatagggggatataatacatattactccacaccttttatgaatcaaatatgatttttttgttgttgttaagacagagtctcactttgacacccaggctggagtgcagtggtgccatcaccacggctcactgcagcctcagcgtcctgggctcaaatgatcctcccacctcagcctcctgagtagctgggactacaggctcatgccatcatgcccagctaatatttttttattttcgtggagacggggcctcactatgttgcctaggctggaaataggattttgaacccaaattgagtttaacaataataaaaagttgttttacgctaaagatggaaaagaactaggactgaactattttaaataaaatattggcaaaagaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("ALOX15");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 17, 4544982, PositionType.ZERO_BASED), "",
				"AAG");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010vsd.2:exon1:c.-37_-36insCTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR5, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc004aus_1() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc004aus.1	chr9	+	97317350	97330409	97317350	97317350	3	97317350,97321250,97329671,	97317592,97321434,97330409,		uc004aus.1");
		this.transcriptForward
				.setSequence("atgcgcataacctcaggatataaataatgctgaagcagagttacgttttttttgttgttgttttttttgtttttgtttttttaggtttccgtgtgtttctattgagctgctcagtgcccggcttagaagaccaggaaaaggagtcacaggtcgtatgctggaggcttgagccgcggcaccgtggcgcggctcgcctcgctgcggttggtggtggcggtggacattgcagcgcggctggaggggtgagatattcctgcacatcctctggtgaccccagaatgagggggactcgctggtgaattgcctcgggcttcacgtccagtacaggctgggtccccgtggtcgccaagcctcctgcctgctcaatgatgtaggccacgggattgcattcatacaggagccggagctgtggaggaacagaggcaggacaaattcaccaagagcctagcaacatgaagagagatgccaggaagaagagagaagccaggaaacaagccaaccgcacaatccccacatcagagcaggagaagatgggggcctgctggcagagctggggcttggctgtggtcactctgaacctgctctttggtgttttcatgagtggtgggaagaatagggaccatatggagcccacacaggaagctctagcagtaacacagcaagcaggaagacaattctaaggaagcagcccatagtcttctttcttttcctgtgcatcttccactgtcagtgaggctcctcatttatggtgaacccaactgtgtgtatctcccaagttctcacccgcagattaatgttttcaggaagataggccatcaacagtgagaggaagaagttacattgtcgtatgagggatgcattttaaccattaatttgtggtacaggctgggcgcagtggcttatgcatgtaatcccagcactttgggaggccgaggtgggtggatcacgaggtcaggagatcgagaccatcctggctaacatggtgaaaccccgtctttactaaatatacaaaaaattggccgggcgtggtggtgggcacctgtagtcccagctactcggggggctgaggcaggagaatggtgtgaacccgggaggcagagcttgcagtgagccgagatcgcgccactgcactccagcctggatgacagagcaagactccatctc"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("BC042913");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq XR_242648.1

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 9, 97329737, PositionType.ZERO_BASED), "", "GA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc004aus.1:exon3:n.492_493insGA", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.ncRNA_EXONIC, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc010arh_1() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010arh.1	chr14	-	73075741	73079803	73075741	73075741	1	73075741,	73079803,		uc010arh.1");
		this.transcriptForward
				.setSequence("tatgaatattgatgatgagaacaggaatgcccatgtgcatgcgcgtgcgtgcacgcacgcacatacacacacacacacacacacacacacacacacacacacgccctacttaatgaaccaaagggaaactttgatgttgctccaggacaagagggagagccaggggctgccaggcaaggtgcatctttcctgaagtcttctccagtcctgctcctccagctctcagcctctgcacagccaccagggttgcaaagggagccttgtgcttggaaagctactggctccgcgacctctctccttctcttcccttcgttcacctatgcttcttttcatgaaagatatttggcaatagttgggtacttcaaaagtctcaattctgctatcaaagggatgcaaagcttttatggctagaatggggtttgtggtttatacggacttggtttgcccttccatgtctactccccactgagaccctgattctcctcctgccctctcccttccctgcactgtttcctcccttccctctgccgtgatagatgctaaggagtggggtggaggtgggaagtgggaagcaacagtggcgtataggaaaaagaaaatataccccgtgcacattttcaacatgtagttgaagaagcctaaattaggtactagaaaaaaaaaaaggacagaaacactgcctgatatgtgagcaagagcatgaaaatatagaatgattatctcagagcagaggaggtggggagcaggtggctggagagaggcggggtaggtaggcagggcgtccctggctcaccaatgcgtctggtccacatggcatttgggaaagcagatgggtcatgcttttgcaatgaatggtcagatgcttagggggcatttgtgctcctctggccaggaaagggaacagagtccatccaagctgccccccactcatctgccatggttgtccaccctgggggtttttcctctgaaggaaaatgaacccatcttttgcctgccatgaatcattgcagggcaggctgttactggctaaattaaaacccttggcagggcatcgactgtgtgcgaggcaatgctctaggtcctgtaggggctggaaaataaaccgcatgctggcctagccttcaagttgcttttcagccatgaaattaagctctgccaagcaatcgtgattgtaggtcaagtatcgcgccatcacggaaggtgtaagtatatgtaggtttctctgttgaggatgcgtgttcctcagtagaagacaagcaggtggaggcatccagtgatttctaccctgtggaggctgaggggtcgggggaagaaaacagatgacctcagcctagttgctttaatctgcttttccaagcactggatgcccttggatgacagcatcctcagcattaaaactggtgaactgatgaagtcacctggcctggagtgtgttgggcagccagtgtccccagagctgcttgtgggtttctggggtggaaggcagggaggtgcaactggcagggcctgatcagaggcagaaaatgacccccacagtggtcttttccctgctagagaaagcagagagcgggactgggggggtgggggcttcaagtacagattgggcacactccaccagaccccagcaaggtcagctgccccacgcctgtatctggcactgctggtgtgtgcagggatgaaacccagcatcagagaggttttcagcaaaccttccatgctcacctttggccaggtgcttgtcagatcctagcttcgtgctggcttagactctcagttgtttgtgttgacagcattaggaataaaccgtttgtttcatctttccttttctccacacgtgtcacagccagatttcagctttgagttattccctgaagaagccacaccaaccttgctttcaagcaaaatgcctgggcttgggggaaggtgtgtatctgtccatgtgtggatgttggctcagagctatagcttctctgtggggggtggcccaagggaaggctcctctggggccctggatggcacatgactcccagtgaggagaattctggtgatctctgtggaggtagtcagggacacaaggcttggctgtgagtctggttttaaagtgcgtgacagcctgaaaagcatgcaggggtttggtccactcacctacttgaaagcctgtgggcaacgttcttttgagccaagacttctctgaatggccctgctggtggaaggggtgaggcaaaggcctctgacttggaccctttccacaccagactggcagcacttcccccaggcagccagtggtgggccctgagccctcaggtccccagctccttgagggatgaacctgggagcccaagagccagtggctgagctctgagaaggctccatctcccacctgcccttgagcgcgctctcaggctgagaacacggtctcatcaggcgccttcctggcctgatgctgtgctgtctacgtcacacggtcgattcacaaaagccagaactagacctcaaccaggtcatctccccgttgccaagtgggttcagggtgagggcaatttgtaagctcaatttctctgacagccaagacatggagcatctctgctaagaagccaaaagaaattggttttcttcttctcattgctgaagcccctctgtgtctcttctcagggacaggctggtccagtggctttggtgagggcgcctccatttgtgaacgctgggattcctctgggtgggctttggcaggtggctcctatggtaggaaggatgaccaggtccctgggaatgagctgcctacctgctgctccacgaggaagctcaggcctgcacaggctccaccaggccttggatgccctctagttgagtcagagaccctggaaacacactgagatctccaattgctgcctccattgatgtctctagacctgcagatacgaagcaaacctgggattgcttcttccaggtatgggcaccagagagggaagccactgcaacattttatccccatcattccaaaatgcttgcttgtctcttttacctcacactcacaactccttctgagactctcagtcataaaggaatgaccaagagagtgggtctccagtgagagaaatgcctatgaaagagggtttccctttttgctcttttgaacaccctccccactgatccttgggacccaacgccgcattgcctcttgcagatgaggttttgccttgggctgctttggtacttcagaccaggactgagtctgacacagctttcatgaggttacagaaaagggctacagatttgggaagctgtgtgtaatggtcttgagacaatatctccatttggcccaccctggcttctctaaaaagcaacgacagcaacagacaaacaaaaagctcccacctcccaccccgttagctgtcctcctccttcactgtgatgtggttgcggtctctgtaggtgtgtgtgccacccttgtcctctgtcctctggggatgtgcccttcccacgtgtgtcaggttcccactctttcgtggttcctaacgtgaagtgctgtgatgtttctgccctgcctaaggaacgtatcaagctctctcagtgtttcagtgttggagattgaggctgtgccacatcttctgccatcctaaggggacatgatggttctgtgattcccagagagctggcagattgtgacaatctccaggagaacctacagattggaagcagcccacacctgatgtggactcctgacccgggactcactcttcattcagaagactggtggcccacgtgccaggaccaccccacctctcttgctgccttttctcctgtcctgatggggttctgggagggagacctgtcgctgatgagatgaagaatgtggggatcgagcagccttcttctttgggacccctcgatatcccatggaatgctcgcacgttctcaaagactgagtcacaagcccctaccccttccttgctgtggttagtatcttgttctgtgattggttagcaatgttgactacccacgtagtgaatcttttgtctgcaatttagagaatgtgtaaacaaataaaaggctttaaaactc"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("AK024141");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 14, 73079293, PositionType.ZERO_BASED), "",
				"AA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010arh.1:exon1:n.511_512dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.ncRNA_EXONIC, annotation1.getVariantType());
	}

	//
	// Various Duplication Variants
	//

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 *
	 * Mutalyzer: NM_001005495(OR2T3_v001):c.769_771dup NM_001005495(OR2T3_i001):p.(Phe257dup)
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.transcriptForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OR2T3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 248637422, PositionType.ZERO_BASED), "",
				"TTC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001iel.1:exon1:c.769_771dup:p.Phe257dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * annovar: FRG1:uc003izs.3:exon6:c.439_440insA:p.M147fs, chr4:190878559->A FRG1 is on the "+" strand
	 *
	 * Jannovar says: FRG1(uc003izs.3:exon6:c.438dupA:p.M147fs) expected <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)> but
	 * was: <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)> is uc003izs.3 NM_004477.2
	 *
	 * Mutalyzer says NM_004477.2(FRG1_v001):c.439dup NM_004477.2(FRG1_i001):p.(Met147Asnfs*8)
	 *
	 * Raw variant 1: duplication from 630 to 630 GAACCAGTCTTTCAAAATGGGAAAA - TGGCTTTGTTGGCCTCAAATAGCTG
	 * GAACCAGTCTTTCAAAATGGGAAAA A TGGCTTTGTTGGCCTCAAATAGCTG
	 *
	 * Thus, 439 and not 438 is the correct number for the duplicated nucleotide. Jannovar lists refvarstart as 630.
	 * This is the last "A" of a polyA tract in the gene (see genbank L76159.1). Jannovar lists refcdsstart as 192. This
	 * is the position of the start of the start codon in FRG1 (L76159.1). <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)>
	 * but was: <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)>
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.transcriptForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FRG1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_004477.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 4, 190878559, PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003izs.3:exon6:c.439dup:p.Met147Asnfs*8", annotation1.getVariantAnnotation());
		// TODO(holtgrem): Duplication on nucleotide level but FS insertion for AAs.
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 */
	@Test
	public void testRealWorldCase_uc010naq_2_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010naq.2	chr9	+	137967088	137969237	137967582	137969065	2	137967088,137968657,	137967648,137969237,	Q6IMJ6	uc010naq.2");
		this.transcriptForward
				.setSequence("atcctgtcccagcctgcttccccgccggccgccgccctcctccccgggagagagcgaggcgcgcgggtccctctgcgccacccccgcccccgccccttccgagcaaacttttggcacccaccgcagcccagcgcgcgttcgtgctccgcagggcgcgcctctctccgccaatgccaggcgcgcgggggagccattaggaggcgaggagagaggagggcgcagctcccgcccagcccagccctgcccagccctgcccggaggcagacgcgccggaaccgggacgcgataaatatgcagagcggaggcttcgcgcagcagagcccgcgcgccgcccgctccgggtgctgaatccaggcgtggggacacgagccaggcgccgccgccggagccagcggagccggggccagagccggagcgcgtccgcgtccacgcagccgccggccggccagcacccagggccctgcatgccaggtcgttggaggtggcagcgagacatgcacccggcccggaagctcctcagcctcctcttcctcatcctgatgggcactgaactcactcaaaataaaagagaaaacaaagcagagaagatgggagggccagagagcgagaggaagaccacaggagagaagacactgaacgagcttcccttgttttgcctggaagcccacgctggctccctggctctgcccaggatgtgcagtccaaatcccaatccagcagtggggttatgtcgtcccgcttaccctcagagcccttctcctggtgctgcccagacgatcagccagtccctcctggagaggttctgcatggcctctaggagaggttttcttggccccaggaaggcctggtggagggtggtggttgtgcactgttgctggacagatgcattcattcatgtgcacacacacacacacacatgcacacacaggggagcagatacctgcagagaagagccaaccaggtcctgattagtggcaagctgccccacaaagggctatgcctgtgtcttattgagacaccttggcaaagagatggctgattctgggtggtcctggacatggccgcacccaagggccctccaagccttaatggcaccctgaagcctccatgcccaggccaaaagatgcttttcctccctaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OLFM1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 9, 137968918, PositionType.ZERO_BASED), "",
				"AGA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010naq.2:exon2:c.325_327dup:p.Arg109dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc001iel_1_second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.transcriptForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OR2T3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 248637607, PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001iel.1:exon1:c.956dup:p.=", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, annotation1.getVariantType());
	}

	/**
	 * This is the test for the in-frame duplication of six nuc.acids / two amino acids '+' strand
	 *
	 * Mutalyzer: NM_001005495.1(OR2T3_v001):c.766_771dup NM_001005495.1(OR2T3_i001):p.(Leu256_Phe257dup)
	 *
	 *
	 * <...6_771dupCTCTTC:p.L25[6_F257]dup)> but was: <...6_771dupCTCTTC:p.L25[4_F256]dup)>
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_third() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.transcriptForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OR2T3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 248637422, PositionType.ZERO_BASED), "",
				"CTCTTC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001iel.1:exon1:c.766_771dup:p.Leu256_Phe257dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * This is the test for the in-frame duplication of 12 nuc.acids / tree amino acids '+' strand
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_fourth() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.transcriptForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OR2T3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_001005495.1

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 248637422, PositionType.ZERO_BASED), "",
				"CTGCTGCTCTTC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001iel.1:exon1:c.760_771dup:p.Leu254_Phe257dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '-' strand
	 *
	 * Mutalyzer: NM_022149.4(MAGEF1_v001):c.424_426dup NM_022149.4(MAGEF1_i001):p.(Thr142dup)
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.transcriptForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MAGEF1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_022149.4

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429186, PositionType.ZERO_BASED), "",
				"AGT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.424_426dup:p.Thr142dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 *
	 * This is the test for the in-frame duplication of 6 nuc.acids / two amino acids '-' strand
	 *
	 * mutalzyer: NM_022149.4(MAGEF1_v001):c.439_444dup NM_022149.4(MAGEF1_i001):p.(Asn147_Lys148dup)
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.transcriptForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MAGEF1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_022149.4

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429171, PositionType.ZERO_BASED), "",
				"TTTGTT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.439_444dup:p.Asn147_Lys148dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * This is the test for the in-frame duplication of 12 nuc.acids / three amino acids '-' strand
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_third() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.transcriptForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MAGEF1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_022149.4

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429171, PositionType.ZERO_BASED), "",
				"TTTTAGTTTGTT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.439_450dup:p.Asn147_Lys150dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 *
	 * This is the test for the offset (+1) duplication of a single triplicate / one amino acids shifting the Stop-codon
	 * '+' strand
	 *
	 * mutalyzer: NM_001005495.1(OR2T3_v001):c.949_954dup NM_001005495.1(OR2T3_i001):p.(*319Gluext*2) I think mutalyzer
	 * is wrong here, the stop is right after the duplication.
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_fifth() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.transcriptForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OR2T3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 248637605, PositionType.ZERO_BASED), "",
				"GAAAAG");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001iel.1:exon1:c.949_954dup:p.*319Gluext*2", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation1.getVariantType());
	}

	/**
	 * annovar: MAGEF1:uc003fpa.3:exon1:c.456_457insGGA:p.L152delinsLE, chr3:184429154->TCC
	 *
	 * uc003fpa.3:exon1:c.456_458dupGGA:p.L152delinsLG
	 *
	 * Refseq: NM_022149
	 *
	 * Mutalyzer: Note that the position gets shifted downstream to bcome the most 3' position.
	 *
	 * NM_022149.4(MAGEF1_v001):c.474_476dup NM_022149.4(MAGEF1_i001):p.(Glu158dup)
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_fourth() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.transcriptForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MAGEF1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_022149.4

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429154, PositionType.ZERO_BASED), "",
				"TCC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.474_476dup:p.Glu158dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * annovar:
	 * FAM178B:uc002sxl.4:exon13:c.1579_1580insCGAT:p.L527fs,FAM178B:uc002sxk.4:exon7:c.628_629insCGAT:p.L210fs,
	 * chr2:97568428->ATCG
	 */
	@Test
	public void testRealWorldCase_uc002sxk_4() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002sxk.4	chr2	-	97541618	97617238	97542012	97617157	11	97541618,97543626,97544152,97559662,97568356,97586901,97587214,97589226,97594942,97613554,97617115,	97542045,97543779,97544230,97559788,97568444,97586999,97587391,97589320,97595057,97613639,97617238,	B3KV66	uc002sxk.4");
		this.transcriptForward
				.setSequence("agccctctgcctcccagctccccgccagcccaacagctctccttcctgcgcagtggcctcctgaacatcctctacctgcacatgcctgactgcccggtatccctgctccagtggctgttccagctgctgacatggcctccagaaacatctttgggagcctttggtcttctgtgggatctcattgtggatggaatcttccttcagcctgatgaagacaagcacctgtggtgcccctcactgcaagaagtcagggaggcattccacagcctgggtgcccacagtcctgccctgtaccctctggggcccttttggcacggtggcagggtgcttccaggcgaggctggcctgaatgagaatgaggagcaggacgctccccaagagattgccttggacatcagcctgggccacatctacaagtttctggcgctgtgtgcccaggcccagccgggggcctacactgatgagaacctcatgggactgattgagctgctgtgccgcaccagcctggacgtggggctccgcctgctgcccaaagttgacctccagcagcttctcctcttgctcctggagaacatccgggagtggccagggaagctccaggaactgtgctgcaccctgagctgggtgtctgaccaccaccacaacctgctggccctcgtgcagttcttcccagacatgacctcccggagcaggcggcttcgaagccagctcagccttgtggtcattgctcgaatgctgggccagcaggagatgctccctctctggcaagagaagacccagctgtcctcgctcagccggctcctgggcctcatgaggccatcatctctcaggcaatacctggactctgtgcccttgccaccctgccaggagcaacagccaaaggctagtgccgagctagaccacaaggcctgctacctgtgccacagcttgctgatgctggccggggtagttgttagctgccaggacatcactccagaccagtggggcgagctgcagctgctgtgcatgcagttggaccgccacatcagcacgcagatccgggagagcccccaggccatgcaccgcaccatgctcaaggacctggctacccagacctacatccgttggcaggagctgctgacccactgccagccccaggcccagtatttcagcccctggaaagacatctaaagggacagggtcagggcagcccagggctcctggcttcagcaggaagtgaacaggctcagggaactggaggaagcgaagcatcaaggccagaggaggccacatgctgaccagcctgatgaggcaagagcctgcccctgccaccgccccgacccctctcctctctgcaagagcctgcctctgccaccgccccgaccccctctcctctcagcaagggatgggcctctctgcctcgcccacccctcagccctcctcccagccatctcctcttccctaaggcctctgtctccatagctctggtttccctgggcctcagtcctccccaccctccttcctctgtctccctgtcactaatgtgaggtttctttgtgcacattaaagtcttctttcagcatca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FAM178B");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_001122646.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 2, 97568427, PositionType.ZERO_BASED), "",
				"ATCG");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002sxk.4:exon7:c.628_629insCGAT:p.Leu210Profs*61", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: RANBP2:uc002tem.4:exon16:c.2265_2266insCC:p.D755fs, chr2:109371423->CC
	 */
	@Test
	public void testRealWorldCase_uc002tem_4_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002tem.4	chr2	+	109335936	109402267	109336062	109400357	29	109335936,109345587,109347229,109347777,109351987,109352559,109356944,109363166,109365375,109367719,109367983,109368326,109369453,109369881,109370280,109371360,109371631,109374868,109378556,109379692,109388156,109388944,109389323,109392187,109393585,109397724,109398583,109398983,109400051,	109336134,109345655,109347341,109347930,109352218,109352705,109357137,109363254,109365585,109367901,109368159,109368450,109369615,109370019,109370427,109371540,109371715,109375004,109378651,109384844,109388327,109389037,109389502,109392392,109393687,109397885,109398857,109399318,109402267,	P49792	uc002tem.4");
		this.transcriptForward
				.setSequence("cacagtggtcctccgccggctacggcgctgcgtcactggtttgcaggcgctttcctcttggaagtggcgactgctgcgggcctgagcgctggtctcacgcgcctcgggagccaggttggcggcgcgatgaggcgcagcaaggctgacgtggagcggtacatcgcctcggtgcagggctccaccccgtcgcctcgacagaagtcaatgaaaggattctattttgcaaagctgtattatgaagctaaagaatatgatcttgctaaaaaatacatatgtacttacattaatgtgcaagagagggatcccaaagctcacagatttctgggtcttctttatgaattggaagaaaacacagacaaagccgttgaatgttacaggcgttcagtggaattaaacccaacacaaaaagatcttgtgttgaagattgcagaattgctttgtaaaaatgatgttactgatggaagagcaaaatactggcttgaaagagcagccaaacttttcccaggaagtcctgcaatttataaactaaaggaacagcttctagattgtgaaggtgaagatggatggaataaactttttgacttgattcagtcagaactttatgtaagacctgatgacgtccatgtgaacatccggctagtggaggtgtatcgctcaactaaaagattgaaggatgctgtggcccactgccatgaggcagagaggaacatagctttgcgttcaagtttagaatggaattcgtgtgttgtacagacccttaaggaatatctggagtctttacagtgtttggagtctgataaaagtgactggcgagcaaccaatacagacttactgctggcctatgctaatcttatgcttcttacgctttccactagagatgtgcaggaaagtagagaattactgcaaagttttgatagtgctcttcagtctgtgaaatctttgggtggaaatgatgaactgtcagctactttcttagaaatgaaaggacatttctacatgcatgctggttctctgcttttgaagatgggtcagcatagtagtaatgttcaatggcgagctctttctgagctggctgcattgtgctatctcatagcatttcaggttccaagaccaaagattaaattaataaaaggtgaagctggacaaaatctgctggaaatgatggcctgtgaccgactgagccaatcagggcacatgttgctaaacttaagtcgtggcaagcaagattttttaaaagagattgttgaaacttttgccaacaaaagcgggcagtctgcattatatgatgctctgttttctagtcagtcacctaaggatacatcttttcttggtagcgatgatattggaaacattgatgtacgagaaccagagcttgaagatttgactagatacgatgttggtgctattcgagcacataatggtagtcttcagcaccttacttggcttggcttacagtggaattcattgcctgctttacctggaatccgaaaatggctaaaacagcttttccatcatttgccccatgaaacctcaaggcttgaaacaaatgcacctgaatcaatatgtattttagatcttgaagtatttctccttggagtagtatataccagccacttacaattaaaggagaaatgtaattctcaccacagctcctatcagccgttatgcctgccccttcctgtgtgtaaacagctttgtacagaaagacaaaaatcttggtgggatgcggtttgtactctgattcacagaaaagcagtacctggaaacgtagcaaaattgagacttctagttcagcatgaaataaacactctaagagcccaggaaaaacatggccttcaacctgctctgcttgtacattgggcagaatgccttcagaaaacgggcagcggtcttaattctttttatgatcaacgagaatacatagggagaagtgttcattattggaagaaagttttgccattgttgaagataataaaaaagaagaacagtattcctgaacctattgatcctctgtttaaacattttcatagtgtagacattcaggcatcagaaattgttgaatatgaagaagacgcacacataacttttgctatattggatgcagtaaatggaaatatagaagatgctgtgactgcttttgaatctataaaaagtgttgtttcttattggaatcttgcactgatttttcacaggaaggcagaagacattgaaaatgatgccctttctcctgaagaacaagaagaatgcaaaaattatctgagaaagaccagggactacctaataaagattatagatgacagtgattcaaatctttcagtggtcaagaaattgcctgtgcccctggagtctgtaaaagagatgcttaattcagtcatgcaggaactcgaagactatagtgaaggaggtcctctctataaaaatggttctttgcgaaatgcagattcagaaataaaacattctacaccgtctcctaccagatattcactatcaccaagtaaaagttacaagtattctcccaaaacaccacctcgatgggcagaagatcagaattctttactgaaaatgatttgccaacaagtagaggccattaagaaagaaatgcaggagttgaaactaaatagcagtaactcagcatcccctcatcgttggcccacagagaattatggaccagactcagtgcctgatggatatcaggggtcacagacatttcatggggctccactaacagttgcaactactggcccttcagtatattatagtcagtcaccagcatataattcccagtatcttctcagaccagcagctaatgttactcccacaaagggcccagtctatggcatgaataggcttccaccccaacagcatatttatgcctatccgcaacagatgcacacaccgccagtgcaaagctcatctgcttgtatgttctctcaggagatgtatggtcctcctgcattgcgttttgagtctcctgcaacgggaattctatcgcccaggggtgatgattactttaattacaatgttcaacagacaagcacaaatccacctttgccagaaccaggatatttcacaaaacctccgattgcagctcatgcttcaagatctgcagaatctaagactatagaatttgggaaaactaattttgttcagcccatgccgggtgaaggattaaggccatctttgccaacacaagcacacacaacacagccaactccttttaaatttaactcaaatttcaaatcaaatgatggtgacttcacgttttcctcaccacaggttgtgacacagccccctcctgcagcttacagtaacagtgaaagccttttaggtctcctgacttcagataaacccttgcaaggagatggctatagtggagccaaaccaattcctggtggtcaaaccattgggcctcgaaatacattcaattttggaagcaaaaatgtgtctggaatttcatttacagaaaacatggggtcgagtcagcaaaagaattctggttttcggcgaagtgatgatatgtttactttccatggtccagggaaatcagtatttggaacacccactttagagacagcaaacaagaatcatgagacagatggaggaagtgcccatggggatgatgatgatgacggtcctcactttgagcctgtagtacctcttcctgataagattgaagtaaaaactggtgaggaagatgaagaagaattcttttgcaaccgcgcgaaattgtttcgtttcgatgtagaatccaaagaatggaaagaacgtgggattggcaatgtaaaaatactgaggcataaaacatctggtaaaattcgccttctaatgagacgagagcaagtattgaaaatctgtgcaaatcattacatcagtccagatatgaaattgacaccaaatgctggatcagacagatcttttgtatggcatgcccttgattatgcagatgagttgccaaaaccagaacaacttgctattaggttcaaaactcctgaggaagcagcactttttaaatgcaagtttgaagaagcccagagcattttaaaagccccaggaacaaatgtagccatggcgtcaaatcaggctgtcagaattgtaaaagaacccacaagtcatgataacaaggatatttgcaaatctgatgctggaaacctgaattttgaatttcaggttgcaaagaaagaagggtcttggtggcattgtaacagctgctcattaaagaatgcttcaactgctaagaaatgtgtatcatgccaaaatctaaacccaagcaataaagagctcgttggcccaccattagctgaaactgtttttactcctaaaaccagcccagagaatgttcaagatcgatttgcattggtgactccaaagaaagaaggtcactgggattgtagtatttgtttagtaagaaatgaacctactgtatctaggtgcattgcgtgtcagaatacaaaatctgctaacaaaagtggatcttcatttgttcatcaagcttcatttaaatttggccagggagatcttcctaaacctattaacagtgatttcagatctgttttttctacaaaggaaggacagtgggattgcagtgcatgtttggtacaaaatgaggggagctctacaaaatgtgctgcttgtcagaatccgagaaaacagagtctacctgctacttctattccaacacctgcctcttttaagtttggtacttcagagacaagtaaaactctaaaaagtggatttgaagacatgtttgctaagaaggaaggacagtgggattgcagttcatgcttagtgcgaaatgaagcaaatgctacaagatgtgttgcttgtcagaatccggataaaccaagtccatctacttctgttccagctcctgcctcttttaagtttggtacttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggaaggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagtgtccaagtaaacaaaatcaaacaactgcaatttcaacacctgcctcttcggagataagcaaggctccaaagagtggatttgaaggaatgttcatcaggaaaggacagtgggattgtagtgtttgctgtgtacaaaatgagagttcttccttaaaatgtgtggcttgtgatgcctctaaaccaactcataaacctattgcagaagctccttcagctttcacactgggctcagaaatgaagttgcatgactcttctggaagtcaggtgggaacaggatttaaaagtaatttctcagaaaaagcttctaagtttggcaatacagagcaaggattcaaatttgggcatgtggatcaagaaaattcaccttcatttatgtttcagggttcttctaatacagaatttaagtcaaccaaagaaggattttccatccctgtgtctgctgatggatttaaatttggcatttcggaaccaggaaatcaagaaaagaaaagtgaaaagcctcttgaaaatggtactggcttccaggctcaggatattagtggccagaagaatggccgtggtgtgatttttggccaaacaagtagcacttttacatttgcagatcttgcaaaatcaacttcaggagaaggatttcagtttggcaaaaaagaccccaatttcaagggattttcaggtgctggagaaaaattattctcatcacaatacggtaaaatggccaataaagcaaacacttccggtgactttgagaaagatgatgatgcctataagactgaggacagcgatgacatccattttgaaccagtagttcaaatgcccgaaaaagtagaacttgtaacaggagaagaagatgaaaaagttctgtattcacagcgggtaaaactatttagatttgatgctgaggtaagtcagtggaaagaaaggggcttggggaacttaaaaattctcaaaaacgaggtcaatggcaaactaagaatgctgatgcgaagagaacaagtactaaaagtgtgtgctaatcattggataacgactacgatgaacctgaagcctctctctggatcagatagagcatggatgtggttagccagtgatttctctgatggtgatgccaaactagagcagttggcagcaaaatttaaaacaccagagctggctgaagaattcaagcagaaatttgaggaatgccagcggcttctgttagacataccacttcaaactccccataaacttgtagatactggcagagctgccaagttaatacagagagctgaagaaatgaagagtggactgaaagatttcaaaacatttttgacaaatgatcaaacaaaagtcactgaggaagaaaataagggttcaggtacaggtgcggccggtgcctcagacacaacaataaaacccaatcctgaaaacactgggcccacattagaatgggataactatgatttaagggaagatgctttggatgatagtgtcagtagtagctcagtacatgcttctccattggcaagtagccctgtgagaaaaaatcttttccgttttggtgagtcaacaacaggatttaacttcagttttaaatctgctttgagtccatctaagtctcctgccaagttgaatcagagtgggacttcagttggcactgatgaagaatctgatgttactcaagaagaagagagagatggacagtactttgaacctgttgttcctttacctgatctagttgaagtatccagtggtgaggaaaatgaacaagttgtttttagtcacagggcaaaactctacagatatgataaagatgttggtcaatggaaagaaaggggcattggtgatataaagattttacagaattatgataataagcaagttcgtatagtgatgagaagggaccaagtattaaaactttgtgccaatcacagaataactccagacatgactttgcaaaatatgaaagggacagaaagagtatggttgtggactgcatgtgattttgcagatggagaaagaaaagtagagcatttagctgttcgttttaaactacaggatgttgcagactcgtttaagaaaatttttgatgaagcaaaaacagcccaggaaaaagattctttgataacacctcatgtttctcggtcaagcactcccagagagtcaccatgtggcaaaattgctgtagctgtattagaagaaaccacaagagagaggacagatgttattcagggtgatgatgtagcagatgcaacttcagaagttgaagtgtctagcacatctgaaacaacaccaaaagcagtggtttctcctccaaagtttgtatttggttcagagtctgttaaaagcatttttagtagtgaaaaatcaaaaccatttgcattcggcaacagttcagccactgggtctttgtttggatttagttttaatgcacctttgaaaagtaacaatagtgaaactagttcagtagcccagagtggatctgaaagcaaagtggaacctaaaaaatgtgaactgtcaaagaactctgatatcgaacagtcttcagatagcaaagtcaaaaatctctttgcttcctttccaacggaagaatcttcaatcaactacacatttaaaacaccagaaaaggcaaaagagaagaaaaaacctgaagattctccctcagatgatgatgttctcattgtatatgaactaactccaaccgctgagcagaaagcccttgcaaccaaacttaaacttcctccaactttcttctgctacaagaatagaccagattatgttagtgaagaagaggaggatgatgaagatttcgaaacagctgtcaagaaacttaatggaaaactatatttggatggctcagaaaaatgtagacccttggaagaaaatacagcagataatgagaaagaatgtattattgtttgggaaaagaaaccaacagttgaagagaaggcaaaagcagatacgttaaaacttccacctacatttttttgtggagtctgtagtgatactgatgaagacaatggaaatggggaagactttcaatcagagcttcaaaaagttcaggaagctcaaaaatctcagacagaagaaataactagcacaactgacagtgtatatacaggtgggactgaagtgatggtaccttctttctgtaaatctgaagaacctgattctattaccaaatccattagttcaccatctgtttcctctgaaactatggacaaacctgtagatttgtcaactagaaaggaaattgatacagattctacaagccaaggggaaagcaagatagtttcatttggatttggaagtagcacagggctctcatttgcagacttggcttccagtaattctggagattttgcttttggttctaaagataaaaatttccaatgggcaaatactggagcagctgtgtttggaacacagtcagtcggaacccagtcagccggtaaagttggtgaagatgaagatggtagtgatgaagaagtagttcataatgaagatatccattttgaaccaatagtgtcactaccagaggtagaagtaaaatctggagaagaagatgaagaaattttgtttaaagagagagccaaactttatagatgggatcgggatgtcagtcagtggaaggagcgcggtgttggagatataaagattctttggcatacaatgaagaattattaccggatcctaatgagaagagaccaggtttttaaagtgtgtgcaaaccacgttattactaaaacaatggaattaaagcccttaaatgtttcaaataatgctttagtttggactgcctcagattatgctgatggagaagcaaaagtagaacagcttgcagtgagatttaaaactaaagaagtagctgattgtttcaagaaaacatttgaagaatgtcagcagaatttaatgaaactccagaaaggacatgtatcactggcagcagaattatcaaaggagaccaatcctgtggtgttttttgatgtttgtgcggacggtgaacctctagggcggataactatggaattattttcaaacattgttcctcggactgctgagaacttcagagcactatgcactggagagaaaggctttggtttcaagaattccatttttcacagagtaattccagattttgtttgccaaggaggagatatcaccaaacatgatggaacaggcggacagtccatttatggagacaaatttgaagatgaaaattttgatgtgaaacatactggtcctggtttactatccatggccaatcaaggccagaataccaataattctcaatttgttataacactgaagaaagcagaacatttggactttaagcatgtagtatttgggtttgttaaggatggcatggatactgtgaaaaagattgaatcatttggttctcccaaagggtctgtttgtcgaagaataactatcacagaatgtggacagatataaaatcattgttgttcatagaaaatttcatctgtataagcagttggattgaagcttagctattacaatttgatagttatgttcagcttttgaaaatggacgtttccgatttacaaatgtaaaattgcagcttatagctgttgtcactttttaatgtgttataattgaccttgcatggtgtgaaataaaagtttaaacactggtgtatttcaggtgtacttgtgtttatgtactcctgacgtattaaaatggaataatactaatcttgttaaaagcaatagacctcaaactattgaaggaatatgatatatgcaatttaattttaattccttttaagatatttggacttcctgcatggatatacttaccatttgaataaagggaccacaacttggataatttaattttaggtttgaaatatatttggtaatcttaactattggtgtactcatttatgcatagagactcgtttatgaatgggtagagccacagaacgtatagagttaaccaaagtgctcttctctagaatctttacacctcctgtgtggttacaagttaactttgtaagtagcgtaccttccttccttaaaatatctagcttcctgtgccctttcatagatattcgattaatttttacattttaaacaagttgactatttcctttaggggttttgtttcaaacttttctgtcatctgtctctactacctcagaaactgcagcttggttctgatgatagaaattgaatttttccttgtagttattgtgataaagtatgaatatttttagaaagtctataccatgttctttcgttaaagatttgctttatacaagattgttgcagtacctttttctggtaaattttgtagcagaaataaaatgacaattcctaagagccactgacatccaaaaaattcattacttacgcttcgggttcattctaaagtaaggaagacaatttaaaggcagtaaattcaaactgctgcataatttccagagctcctagtttctcaagtttgatacacaccaaaaacgtatttggaaatggcttgtatcaaatgttaggcaaattgctaaagaaaagaggatgttcttattggcctactcaatatggaactacaaagaatccaggtagaacacaaaattttgtatattgcaattatgaatattgactgtcttccacccatctgtgttctttcgggtgaaattacctcattttatttagtgaggaaagacaggtttattccctgttacatggggatttggaaattgggtatcctaaagcaagtaactgttcaaccaccagtcaaaagaggggagggatgttgtgcgagtaatgagtgatggtataccatcaccattccactcggccacaaagccagatacttgaaataaacctactccaaatgtatcagttcagtcttgaaccatggattacatatgtttacactaaatatttcaaattggcttatttggaaatctatgtaatataaactgatgtaaagtgtgttgtaacttttcagctgagacagttgatgccttcgtcatgattttagaataaattcttaagttaatgcaagtgctttttaagagactttttacagatttgtatgctttcctaaagcactaaagttacaaattaaaaagctttaaaaactttgaccaaaaatttgacaaaatgacatgtaaactgacttttcccgtattagtattccaaagatgcttaaaagtggcttgtggcatttatgagaaagtctttgtgtcacatttcaggaaaggactttgatttctctttgttatttaatcactgatgtggtctaaacccacgataatatgtatctttccttttaaactggattttatgttgtctcattaaaatctgcttaaagatagaataaaattcattatttgtaca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("RANBP2");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 2, 109371423, PositionType.ZERO_BASED), "",
				"CC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002tem.4:exon16:c.2265_2266insCC:p.Tyr756Profs*21", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: FBXL21 chr5:135272375->A
	 *
	 * mutalzyer: NM_012159(FBXL21_v001):c.93_94insA NM_012159(FBXL21_i001):p.(Gln32Thrfs*39)
	 */
	@Test
	public void testRealWorldCase_uc031sld_1() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc031sld.1	chr5	+	135266005	135277367	135272283	135277360	9	135266005,135270043,135271008,135271311,135272282,135272451,135273113,135276177,135276715,	135266169,135270138,135271128,135271413,135272448,135272649,135273236,135276348,135277367,	Q9UKT6	uc031sld.1");
		this.transcriptForward
				.setSequence("ttgggcggctgcggtctgcgcggacccccgtgcctaggcgctcctgtgcctccagtgcgcggagccggcctcgctcgggcgtgttcctgcgccgaccggacggccggactccagcaccttggcccggcccgcgaacgctgagcacgcgcggaaaccctttaaaggaagtgaaatcaaatggaggttggtgagtaccccacggcaaccattgcttcgtttcatggttcagagtgaactctaatctggaagccttccttaggtcattcctgcttctgggatccaactctatgttgtgcttaagaaatgtgtcacatcagacagagtgaatcacaagacagagatatttacctatcacataaatattttattagatttcaagatgaccatatatacttggacgtatctggacttcaaatttgctggaacttaattgcgtcccaagctggctaatacctagagattcatcatttctgtttggaggaatgaagaggaacagtttatctgttgagaataaaattgtccagttgtcaggagcagcgaaacagccaaaagttgggttctactcttctctcaaccagactcatacacacacggttcttctagactgggggagtttgcctcaccatgtagtattacaaatttttcagtatcttcctttactagatcgggcctgtgcatcttctgtatgtaggaggtggaatgaagtttttcatatttctgacctttggagaaagtttgaatttgaactgaaccagtcagctacttcatcttttaagtccactcatcctgatctcattcagcagatcattaaaaagcattttgctcatcttcagtatgtcagctttaaggttgacagtagcgctgagtcagcagaagctgcctgtgatatactctctcagctggtaaattgttccatccagaccttgggcttgatttcaacagccaagccaagtttcatgaatgtgtcggagtctcattttgtgtcagcacttacagttgtttttatcaactcaaaatcattatcatcaatcaaaattgaagatacaccagtggatgatccttcattgaagattcttgtggccaataatagtgacactctaagactcccaaagatgagtagctgtcctcatgtttcatctgatggaattctttgtgtagctgaccgttgtcaaggccttagagaactggcgttgaattattacatcctaactgatgaacttttccttgcactctcaagcgagactcatgttaaccttgaacatcttcgaattgatgttgtgagtgaaaatcctggacagattaaatttcatgctgttaaaaaacacagttgggatgcacttattaaacattcccctagagttaatgttgttatgcacttctttctatatgaagaggaattcgagacgttcttcaaagaagaaacccctgttactcacctttattttggtcgttcagtcagcaaagtggttttaggacgggtaggtctcaactgtcctcgactgattgagttagtggtgtgtgctaatgatcttcagcctcttgataatgaacttatttgtattgctgaacactgtacaaacctaacagccttgggcctcagcaaatgtgaagttagctgcagtgccttcatcaggtttgtaagactgtgtgagagaaggttaacacagctctctgtaatggaggaagttttgatccctgatgaggattatagcctagatgaaattcacactgaagtctccaaatacctgggaagagtatggttccctgatgtgatgcctctctggtaatgggctg"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FBXL21");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 5, 135272376, PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc031sld.1:exon5:c.93_94insA:p.Gln32Thrfs*39", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: RANBP2:uc002tem.4:exon20:c.6318_6319insAGCG:p.M2106fs, chr2:109383313->AGCG
	 */
	@Test
	public void testRealWorldCase_uc002tem_4_second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002tem.4	chr2	+	109335936	109402267	109336062	109400357	29	109335936,109345587,109347229,109347777,109351987,109352559,109356944,109363166,109365375,109367719,109367983,109368326,109369453,109369881,109370280,109371360,109371631,109374868,109378556,109379692,109388156,109388944,109389323,109392187,109393585,109397724,109398583,109398983,109400051,	109336134,109345655,109347341,109347930,109352218,109352705,109357137,109363254,109365585,109367901,109368159,109368450,109369615,109370019,109370427,109371540,109371715,109375004,109378651,109384844,109388327,109389037,109389502,109392392,109393687,109397885,109398857,109399318,109402267,	P49792	uc002tem.4");
		this.transcriptForward
				.setSequence("cacagtggtcctccgccggctacggcgctgcgtcactggtttgcaggcgctttcctcttggaagtggcgactgctgcgggcctgagcgctggtctcacgcgcctcgggagccaggttggcggcgcgatgaggcgcagcaaggctgacgtggagcggtacatcgcctcggtgcagggctccaccccgtcgcctcgacagaagtcaatgaaaggattctattttgcaaagctgtattatgaagctaaagaatatgatcttgctaaaaaatacatatgtacttacattaatgtgcaagagagggatcccaaagctcacagatttctgggtcttctttatgaattggaagaaaacacagacaaagccgttgaatgttacaggcgttcagtggaattaaacccaacacaaaaagatcttgtgttgaagattgcagaattgctttgtaaaaatgatgttactgatggaagagcaaaatactggcttgaaagagcagccaaacttttcccaggaagtcctgcaatttataaactaaaggaacagcttctagattgtgaaggtgaagatggatggaataaactttttgacttgattcagtcagaactttatgtaagacctgatgacgtccatgtgaacatccggctagtggaggtgtatcgctcaactaaaagattgaaggatgctgtggcccactgccatgaggcagagaggaacatagctttgcgttcaagtttagaatggaattcgtgtgttgtacagacccttaaggaatatctggagtctttacagtgtttggagtctgataaaagtgactggcgagcaaccaatacagacttactgctggcctatgctaatcttatgcttcttacgctttccactagagatgtgcaggaaagtagagaattactgcaaagttttgatagtgctcttcagtctgtgaaatctttgggtggaaatgatgaactgtcagctactttcttagaaatgaaaggacatttctacatgcatgctggttctctgcttttgaagatgggtcagcatagtagtaatgttcaatggcgagctctttctgagctggctgcattgtgctatctcatagcatttcaggttccaagaccaaagattaaattaataaaaggtgaagctggacaaaatctgctggaaatgatggcctgtgaccgactgagccaatcagggcacatgttgctaaacttaagtcgtggcaagcaagattttttaaaagagattgttgaaacttttgccaacaaaagcgggcagtctgcattatatgatgctctgttttctagtcagtcacctaaggatacatcttttcttggtagcgatgatattggaaacattgatgtacgagaaccagagcttgaagatttgactagatacgatgttggtgctattcgagcacataatggtagtcttcagcaccttacttggcttggcttacagtggaattcattgcctgctttacctggaatccgaaaatggctaaaacagcttttccatcatttgccccatgaaacctcaaggcttgaaacaaatgcacctgaatcaatatgtattttagatcttgaagtatttctccttggagtagtatataccagccacttacaattaaaggagaaatgtaattctcaccacagctcctatcagccgttatgcctgccccttcctgtgtgtaaacagctttgtacagaaagacaaaaatcttggtgggatgcggtttgtactctgattcacagaaaagcagtacctggaaacgtagcaaaattgagacttctagttcagcatgaaataaacactctaagagcccaggaaaaacatggccttcaacctgctctgcttgtacattgggcagaatgccttcagaaaacgggcagcggtcttaattctttttatgatcaacgagaatacatagggagaagtgttcattattggaagaaagttttgccattgttgaagataataaaaaagaagaacagtattcctgaacctattgatcctctgtttaaacattttcatagtgtagacattcaggcatcagaaattgttgaatatgaagaagacgcacacataacttttgctatattggatgcagtaaatggaaatatagaagatgctgtgactgcttttgaatctataaaaagtgttgtttcttattggaatcttgcactgatttttcacaggaaggcagaagacattgaaaatgatgccctttctcctgaagaacaagaagaatgcaaaaattatctgagaaagaccagggactacctaataaagattatagatgacagtgattcaaatctttcagtggtcaagaaattgcctgtgcccctggagtctgtaaaagagatgcttaattcagtcatgcaggaactcgaagactatagtgaaggaggtcctctctataaaaatggttctttgcgaaatgcagattcagaaataaaacattctacaccgtctcctaccagatattcactatcaccaagtaaaagttacaagtattctcccaaaacaccacctcgatgggcagaagatcagaattctttactgaaaatgatttgccaacaagtagaggccattaagaaagaaatgcaggagttgaaactaaatagcagtaactcagcatcccctcatcgttggcccacagagaattatggaccagactcagtgcctgatggatatcaggggtcacagacatttcatggggctccactaacagttgcaactactggcccttcagtatattatagtcagtcaccagcatataattcccagtatcttctcagaccagcagctaatgttactcccacaaagggcccagtctatggcatgaataggcttccaccccaacagcatatttatgcctatccgcaacagatgcacacaccgccagtgcaaagctcatctgcttgtatgttctctcaggagatgtatggtcctcctgcattgcgttttgagtctcctgcaacgggaattctatcgcccaggggtgatgattactttaattacaatgttcaacagacaagcacaaatccacctttgccagaaccaggatatttcacaaaacctccgattgcagctcatgcttcaagatctgcagaatctaagactatagaatttgggaaaactaattttgttcagcccatgccgggtgaaggattaaggccatctttgccaacacaagcacacacaacacagccaactccttttaaatttaactcaaatttcaaatcaaatgatggtgacttcacgttttcctcaccacaggttgtgacacagccccctcctgcagcttacagtaacagtgaaagccttttaggtctcctgacttcagataaacccttgcaaggagatggctatagtggagccaaaccaattcctggtggtcaaaccattgggcctcgaaatacattcaattttggaagcaaaaatgtgtctggaatttcatttacagaaaacatggggtcgagtcagcaaaagaattctggttttcggcgaagtgatgatatgtttactttccatggtccagggaaatcagtatttggaacacccactttagagacagcaaacaagaatcatgagacagatggaggaagtgcccatggggatgatgatgatgacggtcctcactttgagcctgtagtacctcttcctgataagattgaagtaaaaactggtgaggaagatgaagaagaattcttttgcaaccgcgcgaaattgtttcgtttcgatgtagaatccaaagaatggaaagaacgtgggattggcaatgtaaaaatactgaggcataaaacatctggtaaaattcgccttctaatgagacgagagcaagtattgaaaatctgtgcaaatcattacatcagtccagatatgaaattgacaccaaatgctggatcagacagatcttttgtatggcatgcccttgattatgcagatgagttgccaaaaccagaacaacttgctattaggttcaaaactcctgaggaagcagcactttttaaatgcaagtttgaagaagcccagagcattttaaaagccccaggaacaaatgtagccatggcgtcaaatcaggctgtcagaattgtaaaagaacccacaagtcatgataacaaggatatttgcaaatctgatgctggaaacctgaattttgaatttcaggttgcaaagaaagaagggtcttggtggcattgtaacagctgctcattaaagaatgcttcaactgctaagaaatgtgtatcatgccaaaatctaaacccaagcaataaagagctcgttggcccaccattagctgaaactgtttttactcctaaaaccagcccagagaatgttcaagatcgatttgcattggtgactccaaagaaagaaggtcactgggattgtagtatttgtttagtaagaaatgaacctactgtatctaggtgcattgcgtgtcagaatacaaaatctgctaacaaaagtggatcttcatttgttcatcaagcttcatttaaatttggccagggagatcttcctaaacctattaacagtgatttcagatctgttttttctacaaaggaaggacagtgggattgcagtgcatgtttggtacaaaatgaggggagctctacaaaatgtgctgcttgtcagaatccgagaaaacagagtctacctgctacttctattccaacacctgcctcttttaagtttggtacttcagagacaagtaaaactctaaaaagtggatttgaagacatgtttgctaagaaggaaggacagtgggattgcagttcatgcttagtgcgaaatgaagcaaatgctacaagatgtgttgcttgtcagaatccggataaaccaagtccatctacttctgttccagctcctgcctcttttaagtttggtacttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggaaggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagtgtccaagtaaacaaaatcaaacaactgcaatttcaacacctgcctcttcggagataagcaaggctccaaagagtggatttgaaggaatgttcatcaggaaaggacagtgggattgtagtgtttgctgtgtacaaaatgagagttcttccttaaaatgtgtggcttgtgatgcctctaaaccaactcataaacctattgcagaagctccttcagctttcacactgggctcagaaatgaagttgcatgactcttctggaagtcaggtgggaacaggatttaaaagtaatttctcagaaaaagcttctaagtttggcaatacagagcaaggattcaaatttgggcatgtggatcaagaaaattcaccttcatttatgtttcagggttcttctaatacagaatttaagtcaaccaaagaaggattttccatccctgtgtctgctgatggatttaaatttggcatttcggaaccaggaaatcaagaaaagaaaagtgaaaagcctcttgaaaatggtactggcttccaggctcaggatattagtggccagaagaatggccgtggtgtgatttttggccaaacaagtagcacttttacatttgcagatcttgcaaaatcaacttcaggagaaggatttcagtttggcaaaaaagaccccaatttcaagggattttcaggtgctggagaaaaattattctcatcacaatacggtaaaatggccaataaagcaaacacttccggtgactttgagaaagatgatgatgcctataagactgaggacagcgatgacatccattttgaaccagtagttcaaatgcccgaaaaagtagaacttgtaacaggagaagaagatgaaaaagttctgtattcacagcgggtaaaactatttagatttgatgctgaggtaagtcagtggaaagaaaggggcttggggaacttaaaaattctcaaaaacgaggtcaatggcaaactaagaatgctgatgcgaagagaacaagtactaaaagtgtgtgctaatcattggataacgactacgatgaacctgaagcctctctctggatcagatagagcatggatgtggttagccagtgatttctctgatggtgatgccaaactagagcagttggcagcaaaatttaaaacaccagagctggctgaagaattcaagcagaaatttgaggaatgccagcggcttctgttagacataccacttcaaactccccataaacttgtagatactggcagagctgccaagttaatacagagagctgaagaaatgaagagtggactgaaagatttcaaaacatttttgacaaatgatcaaacaaaagtcactgaggaagaaaataagggttcaggtacaggtgcggccggtgcctcagacacaacaataaaacccaatcctgaaaacactgggcccacattagaatgggataactatgatttaagggaagatgctttggatgatagtgtcagtagtagctcagtacatgcttctccattggcaagtagccctgtgagaaaaaatcttttccgttttggtgagtcaacaacaggatttaacttcagttttaaatctgctttgagtccatctaagtctcctgccaagttgaatcagagtgggacttcagttggcactgatgaagaatctgatgttactcaagaagaagagagagatggacagtactttgaacctgttgttcctttacctgatctagttgaagtatccagtggtgaggaaaatgaacaagttgtttttagtcacagggcaaaactctacagatatgataaagatgttggtcaatggaaagaaaggggcattggtgatataaagattttacagaattatgataataagcaagttcgtatagtgatgagaagggaccaagtattaaaactttgtgccaatcacagaataactccagacatgactttgcaaaatatgaaagggacagaaagagtatggttgtggactgcatgtgattttgcagatggagaaagaaaagtagagcatttagctgttcgttttaaactacaggatgttgcagactcgtttaagaaaatttttgatgaagcaaaaacagcccaggaaaaagattctttgataacacctcatgtttctcggtcaagcactcccagagagtcaccatgtggcaaaattgctgtagctgtattagaagaaaccacaagagagaggacagatgttattcagggtgatgatgtagcagatgcaacttcagaagttgaagtgtctagcacatctgaaacaacaccaaaagcagtggtttctcctccaaagtttgtatttggttcagagtctgttaaaagcatttttagtagtgaaaaatcaaaaccatttgcattcggcaacagttcagccactgggtctttgtttggatttagttttaatgcacctttgaaaagtaacaatagtgaaactagttcagtagcccagagtggatctgaaagcaaagtggaacctaaaaaatgtgaactgtcaaagaactctgatatcgaacagtcttcagatagcaaagtcaaaaatctctttgcttcctttccaacggaagaatcttcaatcaactacacatttaaaacaccagaaaaggcaaaagagaagaaaaaacctgaagattctccctcagatgatgatgttctcattgtatatgaactaactccaaccgctgagcagaaagcccttgcaaccaaacttaaacttcctccaactttcttctgctacaagaatagaccagattatgttagtgaagaagaggaggatgatgaagatttcgaaacagctgtcaagaaacttaatggaaaactatatttggatggctcagaaaaatgtagacccttggaagaaaatacagcagataatgagaaagaatgtattattgtttgggaaaagaaaccaacagttgaagagaaggcaaaagcagatacgttaaaacttccacctacatttttttgtggagtctgtagtgatactgatgaagacaatggaaatggggaagactttcaatcagagcttcaaaaagttcaggaagctcaaaaatctcagacagaagaaataactagcacaactgacagtgtatatacaggtgggactgaagtgatggtaccttctttctgtaaatctgaagaacctgattctattaccaaatccattagttcaccatctgtttcctctgaaactatggacaaacctgtagatttgtcaactagaaaggaaattgatacagattctacaagccaaggggaaagcaagatagtttcatttggatttggaagtagcacagggctctcatttgcagacttggcttccagtaattctggagattttgcttttggttctaaagataaaaatttccaatgggcaaatactggagcagctgtgtttggaacacagtcagtcggaacccagtcagccggtaaagttggtgaagatgaagatggtagtgatgaagaagtagttcataatgaagatatccattttgaaccaatagtgtcactaccagaggtagaagtaaaatctggagaagaagatgaagaaattttgtttaaagagagagccaaactttatagatgggatcgggatgtcagtcagtggaaggagcgcggtgttggagatataaagattctttggcatacaatgaagaattattaccggatcctaatgagaagagaccaggtttttaaagtgtgtgcaaaccacgttattactaaaacaatggaattaaagcccttaaatgtttcaaataatgctttagtttggactgcctcagattatgctgatggagaagcaaaagtagaacagcttgcagtgagatttaaaactaaagaagtagctgattgtttcaagaaaacatttgaagaatgtcagcagaatttaatgaaactccagaaaggacatgtatcactggcagcagaattatcaaaggagaccaatcctgtggtgttttttgatgtttgtgcggacggtgaacctctagggcggataactatggaattattttcaaacattgttcctcggactgctgagaacttcagagcactatgcactggagagaaaggctttggtttcaagaattccatttttcacagagtaattccagattttgtttgccaaggaggagatatcaccaaacatgatggaacaggcggacagtccatttatggagacaaatttgaagatgaaaattttgatgtgaaacatactggtcctggtttactatccatggccaatcaaggccagaataccaataattctcaatttgttataacactgaagaaagcagaacatttggactttaagcatgtagtatttgggtttgttaaggatggcatggatactgtgaaaaagattgaatcatttggttctcccaaagggtctgtttgtcgaagaataactatcacagaatgtggacagatataaaatcattgttgttcatagaaaatttcatctgtataagcagttggattgaagcttagctattacaatttgatagttatgttcagcttttgaaaatggacgtttccgatttacaaatgtaaaattgcagcttatagctgttgtcactttttaatgtgttataattgaccttgcatggtgtgaaataaaagtttaaacactggtgtatttcaggtgtacttgtgtttatgtactcctgacgtattaaaatggaataatactaatcttgttaaaagcaatagacctcaaactattgaaggaatatgatatatgcaatttaattttaattccttttaagatatttggacttcctgcatggatatacttaccatttgaataaagggaccacaacttggataatttaattttaggtttgaaatatatttggtaatcttaactattggtgtactcatttatgcatagagactcgtttatgaatgggtagagccacagaacgtatagagttaaccaaagtgctcttctctagaatctttacacctcctgtgtggttacaagttaactttgtaagtagcgtaccttccttccttaaaatatctagcttcctgtgccctttcatagatattcgattaatttttacattttaaacaagttgactatttcctttaggggttttgtttcaaacttttctgtcatctgtctctactacctcagaaactgcagcttggttctgatgatagaaattgaatttttccttgtagttattgtgataaagtatgaatatttttagaaagtctataccatgttctttcgttaaagatttgctttatacaagattgttgcagtacctttttctggtaaattttgtagcagaaataaaatgacaattcctaagagccactgacatccaaaaaattcattacttacgcttcgggttcattctaaagtaaggaagacaatttaaaggcagtaaattcaaactgctgcataatttccagagctcctagtttctcaagtttgatacacaccaaaaacgtatttggaaatggcttgtatcaaatgttaggcaaattgctaaagaaaagaggatgttcttattggcctactcaatatggaactacaaagaatccaggtagaacacaaaattttgtatattgcaattatgaatattgactgtcttccacccatctgtgttctttcgggtgaaattacctcattttatttagtgaggaaagacaggtttattccctgttacatggggatttggaaattgggtatcctaaagcaagtaactgttcaaccaccagtcaaaagaggggagggatgttgtgcgagtaatgagtgatggtataccatcaccattccactcggccacaaagccagatacttgaaataaacctactccaaatgtatcagttcagtcttgaaccatggattacatatgtttacactaaatatttcaaattggcttatttggaaatctatgtaatataaactgatgtaaagtgtgttgtaacttttcagctgagacagttgatgccttcgtcatgattttagaataaattcttaagttaatgcaagtgctttttaagagactttttacagatttgtatgctttcctaaagcactaaagttacaaattaaaaagctttaaaaactttgaccaaaaatttgacaaaatgacatgtaaactgacttttcccgtattagtattccaaagatgcttaaaagtggcttgtggcatttatgagaaagtctttgtgtcacatttcaggaaaggactttgatttctctttgttatttaatcactgatgtggtctaaacccacgataatatgtatctttccttttaaactggattttatgttgtctcattaaaatctgcttaaagatagaataaaattcattatttgtaca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("RANBP2");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 2, 109383313, PositionType.ZERO_BASED), "",
				"AGCG");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002tem.4:exon20:c.6318_6319insAGCG:p.Trp2107Serfs*6", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: RANBP2:uc002tem.4:exon20:c.6882_6883insCAT:p.D2294delinsDH, chr2:109383877->CAT
	 *
	 * Mutalyzer: NM_006267.4(RANBP2_v001):c.6882_6883insCAT NM_006267.4(RANBP2_i001):p.(Asp2294_Glu2295insHis)
	 */
	@Test
	public void testRealWorldCase_uc002tem_4_third() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002tem.4	chr2	+	109335936	109402267	109336062	109400357	29	109335936,109345587,109347229,109347777,109351987,109352559,109356944,109363166,109365375,109367719,109367983,109368326,109369453,109369881,109370280,109371360,109371631,109374868,109378556,109379692,109388156,109388944,109389323,109392187,109393585,109397724,109398583,109398983,109400051,	109336134,109345655,109347341,109347930,109352218,109352705,109357137,109363254,109365585,109367901,109368159,109368450,109369615,109370019,109370427,109371540,109371715,109375004,109378651,109384844,109388327,109389037,109389502,109392392,109393687,109397885,109398857,109399318,109402267,	P49792	uc002tem.4");
		this.transcriptForward
				.setSequence("cacagtggtcctccgccggctacggcgctgcgtcactggtttgcaggcgctttcctcttggaagtggcgactgctgcgggcctgagcgctggtctcacgcgcctcgggagccaggttggcggcgcgatgaggcgcagcaaggctgacgtggagcggtacatcgcctcggtgcagggctccaccccgtcgcctcgacagaagtcaatgaaaggattctattttgcaaagctgtattatgaagctaaagaatatgatcttgctaaaaaatacatatgtacttacattaatgtgcaagagagggatcccaaagctcacagatttctgggtcttctttatgaattggaagaaaacacagacaaagccgttgaatgttacaggcgttcagtggaattaaacccaacacaaaaagatcttgtgttgaagattgcagaattgctttgtaaaaatgatgttactgatggaagagcaaaatactggcttgaaagagcagccaaacttttcccaggaagtcctgcaatttataaactaaaggaacagcttctagattgtgaaggtgaagatggatggaataaactttttgacttgattcagtcagaactttatgtaagacctgatgacgtccatgtgaacatccggctagtggaggtgtatcgctcaactaaaagattgaaggatgctgtggcccactgccatgaggcagagaggaacatagctttgcgttcaagtttagaatggaattcgtgtgttgtacagacccttaaggaatatctggagtctttacagtgtttggagtctgataaaagtgactggcgagcaaccaatacagacttactgctggcctatgctaatcttatgcttcttacgctttccactagagatgtgcaggaaagtagagaattactgcaaagttttgatagtgctcttcagtctgtgaaatctttgggtggaaatgatgaactgtcagctactttcttagaaatgaaaggacatttctacatgcatgctggttctctgcttttgaagatgggtcagcatagtagtaatgttcaatggcgagctctttctgagctggctgcattgtgctatctcatagcatttcaggttccaagaccaaagattaaattaataaaaggtgaagctggacaaaatctgctggaaatgatggcctgtgaccgactgagccaatcagggcacatgttgctaaacttaagtcgtggcaagcaagattttttaaaagagattgttgaaacttttgccaacaaaagcgggcagtctgcattatatgatgctctgttttctagtcagtcacctaaggatacatcttttcttggtagcgatgatattggaaacattgatgtacgagaaccagagcttgaagatttgactagatacgatgttggtgctattcgagcacataatggtagtcttcagcaccttacttggcttggcttacagtggaattcattgcctgctttacctggaatccgaaaatggctaaaacagcttttccatcatttgccccatgaaacctcaaggcttgaaacaaatgcacctgaatcaatatgtattttagatcttgaagtatttctccttggagtagtatataccagccacttacaattaaaggagaaatgtaattctcaccacagctcctatcagccgttatgcctgccccttcctgtgtgtaaacagctttgtacagaaagacaaaaatcttggtgggatgcggtttgtactctgattcacagaaaagcagtacctggaaacgtagcaaaattgagacttctagttcagcatgaaataaacactctaagagcccaggaaaaacatggccttcaacctgctctgcttgtacattgggcagaatgccttcagaaaacgggcagcggtcttaattctttttatgatcaacgagaatacatagggagaagtgttcattattggaagaaagttttgccattgttgaagataataaaaaagaagaacagtattcctgaacctattgatcctctgtttaaacattttcatagtgtagacattcaggcatcagaaattgttgaatatgaagaagacgcacacataacttttgctatattggatgcagtaaatggaaatatagaagatgctgtgactgcttttgaatctataaaaagtgttgtttcttattggaatcttgcactgatttttcacaggaaggcagaagacattgaaaatgatgccctttctcctgaagaacaagaagaatgcaaaaattatctgagaaagaccagggactacctaataaagattatagatgacagtgattcaaatctttcagtggtcaagaaattgcctgtgcccctggagtctgtaaaagagatgcttaattcagtcatgcaggaactcgaagactatagtgaaggaggtcctctctataaaaatggttctttgcgaaatgcagattcagaaataaaacattctacaccgtctcctaccagatattcactatcaccaagtaaaagttacaagtattctcccaaaacaccacctcgatgggcagaagatcagaattctttactgaaaatgatttgccaacaagtagaggccattaagaaagaaatgcaggagttgaaactaaatagcagtaactcagcatcccctcatcgttggcccacagagaattatggaccagactcagtgcctgatggatatcaggggtcacagacatttcatggggctccactaacagttgcaactactggcccttcagtatattatagtcagtcaccagcatataattcccagtatcttctcagaccagcagctaatgttactcccacaaagggcccagtctatggcatgaataggcttccaccccaacagcatatttatgcctatccgcaacagatgcacacaccgccagtgcaaagctcatctgcttgtatgttctctcaggagatgtatggtcctcctgcattgcgttttgagtctcctgcaacgggaattctatcgcccaggggtgatgattactttaattacaatgttcaacagacaagcacaaatccacctttgccagaaccaggatatttcacaaaacctccgattgcagctcatgcttcaagatctgcagaatctaagactatagaatttgggaaaactaattttgttcagcccatgccgggtgaaggattaaggccatctttgccaacacaagcacacacaacacagccaactccttttaaatttaactcaaatttcaaatcaaatgatggtgacttcacgttttcctcaccacaggttgtgacacagccccctcctgcagcttacagtaacagtgaaagccttttaggtctcctgacttcagataaacccttgcaaggagatggctatagtggagccaaaccaattcctggtggtcaaaccattgggcctcgaaatacattcaattttggaagcaaaaatgtgtctggaatttcatttacagaaaacatggggtcgagtcagcaaaagaattctggttttcggcgaagtgatgatatgtttactttccatggtccagggaaatcagtatttggaacacccactttagagacagcaaacaagaatcatgagacagatggaggaagtgcccatggggatgatgatgatgacggtcctcactttgagcctgtagtacctcttcctgataagattgaagtaaaaactggtgaggaagatgaagaagaattcttttgcaaccgcgcgaaattgtttcgtttcgatgtagaatccaaagaatggaaagaacgtgggattggcaatgtaaaaatactgaggcataaaacatctggtaaaattcgccttctaatgagacgagagcaagtattgaaaatctgtgcaaatcattacatcagtccagatatgaaattgacaccaaatgctggatcagacagatcttttgtatggcatgcccttgattatgcagatgagttgccaaaaccagaacaacttgctattaggttcaaaactcctgaggaagcagcactttttaaatgcaagtttgaagaagcccagagcattttaaaagccccaggaacaaatgtagccatggcgtcaaatcaggctgtcagaattgtaaaagaacccacaagtcatgataacaaggatatttgcaaatctgatgctggaaacctgaattttgaatttcaggttgcaaagaaagaagggtcttggtggcattgtaacagctgctcattaaagaatgcttcaactgctaagaaatgtgtatcatgccaaaatctaaacccaagcaataaagagctcgttggcccaccattagctgaaactgtttttactcctaaaaccagcccagagaatgttcaagatcgatttgcattggtgactccaaagaaagaaggtcactgggattgtagtatttgtttagtaagaaatgaacctactgtatctaggtgcattgcgtgtcagaatacaaaatctgctaacaaaagtggatcttcatttgttcatcaagcttcatttaaatttggccagggagatcttcctaaacctattaacagtgatttcagatctgttttttctacaaaggaaggacagtgggattgcagtgcatgtttggtacaaaatgaggggagctctacaaaatgtgctgcttgtcagaatccgagaaaacagagtctacctgctacttctattccaacacctgcctcttttaagtttggtacttcagagacaagtaaaactctaaaaagtggatttgaagacatgtttgctaagaaggaaggacagtgggattgcagttcatgcttagtgcgaaatgaagcaaatgctacaagatgtgttgcttgtcagaatccggataaaccaagtccatctacttctgttccagctcctgcctcttttaagtttggtacttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggaaggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagtgtccaagtaaacaaaatcaaacaactgcaatttcaacacctgcctcttcggagataagcaaggctccaaagagtggatttgaaggaatgttcatcaggaaaggacagtgggattgtagtgtttgctgtgtacaaaatgagagttcttccttaaaatgtgtggcttgtgatgcctctaaaccaactcataaacctattgcagaagctccttcagctttcacactgggctcagaaatgaagttgcatgactcttctggaagtcaggtgggaacaggatttaaaagtaatttctcagaaaaagcttctaagtttggcaatacagagcaaggattcaaatttgggcatgtggatcaagaaaattcaccttcatttatgtttcagggttcttctaatacagaatttaagtcaaccaaagaaggattttccatccctgtgtctgctgatggatttaaatttggcatttcggaaccaggaaatcaagaaaagaaaagtgaaaagcctcttgaaaatggtactggcttccaggctcaggatattagtggccagaagaatggccgtggtgtgatttttggccaaacaagtagcacttttacatttgcagatcttgcaaaatcaacttcaggagaaggatttcagtttggcaaaaaagaccccaatttcaagggattttcaggtgctggagaaaaattattctcatcacaatacggtaaaatggccaataaagcaaacacttccggtgactttgagaaagatgatgatgcctataagactgaggacagcgatgacatccattttgaaccagtagttcaaatgcccgaaaaagtagaacttgtaacaggagaagaagatgaaaaagttctgtattcacagcgggtaaaactatttagatttgatgctgaggtaagtcagtggaaagaaaggggcttggggaacttaaaaattctcaaaaacgaggtcaatggcaaactaagaatgctgatgcgaagagaacaagtactaaaagtgtgtgctaatcattggataacgactacgatgaacctgaagcctctctctggatcagatagagcatggatgtggttagccagtgatttctctgatggtgatgccaaactagagcagttggcagcaaaatttaaaacaccagagctggctgaagaattcaagcagaaatttgaggaatgccagcggcttctgttagacataccacttcaaactccccataaacttgtagatactggcagagctgccaagttaatacagagagctgaagaaatgaagagtggactgaaagatttcaaaacatttttgacaaatgatcaaacaaaagtcactgaggaagaaaataagggttcaggtacaggtgcggccggtgcctcagacacaacaataaaacccaatcctgaaaacactgggcccacattagaatgggataactatgatttaagggaagatgctttggatgatagtgtcagtagtagctcagtacatgcttctccattggcaagtagccctgtgagaaaaaatcttttccgttttggtgagtcaacaacaggatttaacttcagttttaaatctgctttgagtccatctaagtctcctgccaagttgaatcagagtgggacttcagttggcactgatgaagaatctgatgttactcaagaagaagagagagatggacagtactttgaacctgttgttcctttacctgatctagttgaagtatccagtggtgaggaaaatgaacaagttgtttttagtcacagggcaaaactctacagatatgataaagatgttggtcaatggaaagaaaggggcattggtgatataaagattttacagaattatgataataagcaagttcgtatagtgatgagaagggaccaagtattaaaactttgtgccaatcacagaataactccagacatgactttgcaaaatatgaaagggacagaaagagtatggttgtggactgcatgtgattttgcagatggagaaagaaaagtagagcatttagctgttcgttttaaactacaggatgttgcagactcgtttaagaaaatttttgatgaagcaaaaacagcccaggaaaaagattctttgataacacctcatgtttctcggtcaagcactcccagagagtcaccatgtggcaaaattgctgtagctgtattagaagaaaccacaagagagaggacagatgttattcagggtgatgatgtagcagatgcaacttcagaagttgaagtgtctagcacatctgaaacaacaccaaaagcagtggtttctcctccaaagtttgtatttggttcagagtctgttaaaagcatttttagtagtgaaaaatcaaaaccatttgcattcggcaacagttcagccactgggtctttgtttggatttagttttaatgcacctttgaaaagtaacaatagtgaaactagttcagtagcccagagtggatctgaaagcaaagtggaacctaaaaaatgtgaactgtcaaagaactctgatatcgaacagtcttcagatagcaaagtcaaaaatctctttgcttcctttccaacggaagaatcttcaatcaactacacatttaaaacaccagaaaaggcaaaagagaagaaaaaacctgaagattctccctcagatgatgatgttctcattgtatatgaactaactccaaccgctgagcagaaagcccttgcaaccaaacttaaacttcctccaactttcttctgctacaagaatagaccagattatgttagtgaagaagaggaggatgatgaagatttcgaaacagctgtcaagaaacttaatggaaaactatatttggatggctcagaaaaatgtagacccttggaagaaaatacagcagataatgagaaagaatgtattattgtttgggaaaagaaaccaacagttgaagagaaggcaaaagcagatacgttaaaacttccacctacatttttttgtggagtctgtagtgatactgatgaagacaatggaaatggggaagactttcaatcagagcttcaaaaagttcaggaagctcaaaaatctcagacagaagaaataactagcacaactgacagtgtatatacaggtgggactgaagtgatggtaccttctttctgtaaatctgaagaacctgattctattaccaaatccattagttcaccatctgtttcctctgaaactatggacaaacctgtagatttgtcaactagaaaggaaattgatacagattctacaagccaaggggaaagcaagatagtttcatttggatttggaagtagcacagggctctcatttgcagacttggcttccagtaattctggagattttgcttttggttctaaagataaaaatttccaatgggcaaatactggagcagctgtgtttggaacacagtcagtcggaacccagtcagccggtaaagttggtgaagatgaagatggtagtgatgaagaagtagttcataatgaagatatccattttgaaccaatagtgtcactaccagaggtagaagtaaaatctggagaagaagatgaagaaattttgtttaaagagagagccaaactttatagatgggatcgggatgtcagtcagtggaaggagcgcggtgttggagatataaagattctttggcatacaatgaagaattattaccggatcctaatgagaagagaccaggtttttaaagtgtgtgcaaaccacgttattactaaaacaatggaattaaagcccttaaatgtttcaaataatgctttagtttggactgcctcagattatgctgatggagaagcaaaagtagaacagcttgcagtgagatttaaaactaaagaagtagctgattgtttcaagaaaacatttgaagaatgtcagcagaatttaatgaaactccagaaaggacatgtatcactggcagcagaattatcaaaggagaccaatcctgtggtgttttttgatgtttgtgcggacggtgaacctctagggcggataactatggaattattttcaaacattgttcctcggactgctgagaacttcagagcactatgcactggagagaaaggctttggtttcaagaattccatttttcacagagtaattccagattttgtttgccaaggaggagatatcaccaaacatgatggaacaggcggacagtccatttatggagacaaatttgaagatgaaaattttgatgtgaaacatactggtcctggtttactatccatggccaatcaaggccagaataccaataattctcaatttgttataacactgaagaaagcagaacatttggactttaagcatgtagtatttgggtttgttaaggatggcatggatactgtgaaaaagattgaatcatttggttctcccaaagggtctgtttgtcgaagaataactatcacagaatgtggacagatataaaatcattgttgttcatagaaaatttcatctgtataagcagttggattgaagcttagctattacaatttgatagttatgttcagcttttgaaaatggacgtttccgatttacaaatgtaaaattgcagcttatagctgttgtcactttttaatgtgttataattgaccttgcatggtgtgaaataaaagtttaaacactggtgtatttcaggtgtacttgtgtttatgtactcctgacgtattaaaatggaataatactaatcttgttaaaagcaatagacctcaaactattgaaggaatatgatatatgcaatttaattttaattccttttaagatatttggacttcctgcatggatatacttaccatttgaataaagggaccacaacttggataatttaattttaggtttgaaatatatttggtaatcttaactattggtgtactcatttatgcatagagactcgtttatgaatgggtagagccacagaacgtatagagttaaccaaagtgctcttctctagaatctttacacctcctgtgtggttacaagttaactttgtaagtagcgtaccttccttccttaaaatatctagcttcctgtgccctttcatagatattcgattaatttttacattttaaacaagttgactatttcctttaggggttttgtttcaaacttttctgtcatctgtctctactacctcagaaactgcagcttggttctgatgatagaaattgaatttttccttgtagttattgtgataaagtatgaatatttttagaaagtctataccatgttctttcgttaaagatttgctttatacaagattgttgcagtacctttttctggtaaattttgtagcagaaataaaatgacaattcctaagagccactgacatccaaaaaattcattacttacgcttcgggttcattctaaagtaaggaagacaatttaaaggcagtaaattcaaactgctgcataatttccagagctcctagtttctcaagtttgatacacaccaaaaacgtatttggaaatggcttgtatcaaatgttaggcaaattgctaaagaaaagaggatgttcttattggcctactcaatatggaactacaaagaatccaggtagaacacaaaattttgtatattgcaattatgaatattgactgtcttccacccatctgtgttctttcgggtgaaattacctcattttatttagtgaggaaagacaggtttattccctgttacatggggatttggaaattgggtatcctaaagcaagtaactgttcaaccaccagtcaaaagaggggagggatgttgtgcgagtaatgagtgatggtataccatcaccattccactcggccacaaagccagatacttgaaataaacctactccaaatgtatcagttcagtcttgaaccatggattacatatgtttacactaaatatttcaaattggcttatttggaaatctatgtaatataaactgatgtaaagtgtgttgtaacttttcagctgagacagttgatgccttcgtcatgattttagaataaattcttaagttaatgcaagtgctttttaagagactttttacagatttgtatgctttcctaaagcactaaagttacaaattaaaaagctttaaaaactttgaccaaaaatttgacaaaatgacatgtaaactgacttttcccgtattagtattccaaagatgcttaaaagtggcttgtggcatttatgagaaagtctttgtgtcacatttcaggaaaggactttgatttctctttgttatttaatcactgatgtggtctaaacccacgataatatgtatctttccttttaaactggattttatgttgtctcattaaaatctgcttaaagatagaataaaattcattatttgtaca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("RANBP2");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 2, 109383877, PositionType.ZERO_BASED), "",
				"CAT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002tem.4:exon20:c.6882_6883insCAT:p.Asp2294_Glu2295insHis",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: TTN:uc002umz.1:exon112:c.21594_21595insACTT:p.K7198fs, chr2:179519685->AAGT
	 */
	@Test
	public void testRealWorldCase_uc002umz_1() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002umz.1	chr2	-	179519471	179629029	179519472	179629000	113	179519471,179519638,179527692,179534944,179535816,179536697,179537133,179537361,179538359,179539040,179539765,179540647,179542347,179542851,179543140,179543473,179544065,179544325,179544620,179544980,179545805,179546102,179546387,179547423,179547937,179548725,179549056,179549392,179549632,179549979,179550244,179552837,179553403,179553779,179554016,179554241,179554539,179556742,179557223,179558335,179558648,179559325,179559555,179560074,179560591,179561847,179563569,179565846,179566254,179566766,179566894,179567180,179568873,179569236,179569603,179569900,179571180,179571588,179572252,179574292,179575361,179575788,179576670,179577041,179577423,179577811,179578623,179579018,179579712,179580219,179581821,179582249,179582669,179583048,179583421,179583890,179584280,179584709,179585111,179585647,179586573,179586985,179587385,179587772,179588144,179588582,179588986,179590094,179590494,179591816,179592311,179592836,179593226,179593617,179594014,179594390,179594819,179595231,179595651,179596031,179596419,179596792,179597166,179597560,179597965,179598340,179599054,179599433,179600237,179602808,179617850,179623710,179628903,	179519555,179519722,179527782,179535022,179535897,179536994,179537208,179537430,179538437,179539136,179539840,179540722,179542644,179542935,179543224,179543557,179544143,179544409,179544782,179545058,179545898,179546177,179546465,179547630,179548018,179548809,179549140,179549476,179549716,179550057,179550325,179552951,179553505,179553863,179554100,179554322,179554623,179556826,179557307,179558416,179558735,179559403,179559633,179560137,179560996,179561895,179563641,179565930,179566314,179566793,179566972,179567390,179569134,179569504,179569693,179570084,179571466,179571681,179572540,179574583,179575649,179576076,179576949,179577320,179577702,179578099,179578902,179579300,179579991,179580501,179582109,179582537,179582948,179583327,179583700,179584178,179584559,179584991,179585390,179585929,179586861,179587273,179587664,179588051,179588423,179588870,179589265,179590376,179590773,179592098,179592590,179593124,179593505,179593896,179594293,179594672,179595098,179595519,179595930,179596310,179596698,179597074,179597445,179597848,179598244,179598619,179599333,179599715,179600801,179603087,179617907,179623899,179629029,		uc002umz.1");
		this.transcriptForward
				.setSequence("ttccagaagttgtgtctcctgatcaggaaatgcctgtttatccacctgccatcatcaccccgcttcaggacactgtcacttctgaagggcagccagcccgttttcaatgccgggtttctggaacagatctaaaagtgtcgtggtacagcaaagacaagaaaatcaagccatctcggttctttagaatgactcaatttgaagacacttatcaactggaaattgccgaagcttatccagaagatgaaggaacttacacgtttgttgctagtaatgctgtaggccaagtatcaagcacagccaacctgagtctggaagctcctgaatcaattttgcatgagaggattgaacaagagattgagatggaaatgaaagctgccccagtgatcaagaggaaaatcgaacccctggaagtagcactgggccacctagccaaattcacctgtgagatccaaagtgctcccaatgtccggttccagtggtttaaagctggccgagaaatttatgagagtgacaagtgttctattcgatcttcaaagtatatctccagccttgaaatcctgagaacccaggtggttgactgcggcgagtatacatgcaaagcttccaatgagtatggcagtgtcagctgtacagccacactaactgtgacagaggcatatccaccaacctttctctccagacctaagtcccttacaacttttgtgggtaaggcagccaagttcatctgcacagtgacagggactcctgtgatagaaaccatttggcagaaagatggtgctgcactctcaccttcacctaactggaggatttccgacgcagaaaacaaacacattttagaactctcaaaccttaccattcaagatagaggagtttattcttgtaaggcttccaacaagtttggagcagacatctgccaagcagagttgatcatcattgataagccacatttcattaaagaattagagcctgtgcagtccgctatcaataagaaggtccaccttgagtgccaagtagatgaagacagaaaagtcacagttacgtggagcaaagatgggcaaaaactccccccagggaaagattataagatctgttttgaagataaaatagcaacacttgagattcctttggccaaactgaaagattcaggaacctatgtctgtacagcttcaaatgaggctggaagcagctcctgctcagccacagtcactgtcagagagccaccatccttcgtgaagaaggtggatccctcttatttaatgctcccaggtgaatcagcacgcctccattgcaagctgaaaggctcacctgtgatccaggttacttggtttaaaaataacaaagaactcagtgaaagtaacacagtccgaatgtattttgtcaattctgaggctatacttgatattacggatgtaaaagttgaagacagtgggagttactcatgtgaagcagtgaatgacgtcggcagtgatagctgcagtactgaaatagttatcaaagaacctccttctttcatcaaaactcttgagcctgcagacatagtgagaggaacaaatgctctacttcagtgtgaagtctcaggcactggaccatttgaaattagctggttcaaagacaagaaacaaattcgaagtagtaaaaaatacagattgttttctcagaagtctcttgtgtgtctggagatcttttcgtttaatagtgcagatgttggtgaatatgaatgtgttgttgctaatgaagtcggcaagtgtggctgcatggcaacccacttactcaaagaacctccaacctttgtaaagaaagtagatgatttgattgcactaggaggacaaaccgttaccctgcaagctgctgtgagagggtcagagcccatttctgtcacatggatgaaaggtcaagaggtcatcagagaagacggaaaaatcaaaatgagcttttccaatggtgttgcagtcttgataatccctgatgttcagattagttttggaggcaaatacacgtgcctggctgaaaatgaagctggaagccaaacatctgttggggaattaatagttaaagaacctgccaaaatcattgagcgagcagaactgatccaggtgacagcaggagatcccgccacactggagtacacagtggcaggcacgccagaactgaagcccaaatggtacaaagatgggagacccttggtcgccagtaaaaaataccgaataagttttaaaaacaatgttgcccagctcaaattttattcagctgagctgcacgacagtggccaatacacatttgagatttccaatgaagtgggcagcagctcctgtgagactacattcactgtattagatcgagacattgctccattttttaccaaacccttgcgcaacgtggatagtgttgttaatggtacctgcagactggactgcaaaattgcaggctccctccccatgagggtgtcctggtttaaggatggcaaagaaatagctgcttctgacagatacaggatagcatttgtggaaggcacagcctctttggagatcatcagagtagacatgaatgatgcagggaatttcacttgtcgagccacaaattccgtgggaagcaaagacagcagtggagccctgattgtgcaagaaccacccagttttgtaactaaacccggctcaaaggatgttctgcctggctcagcagtctgcctgaagagcactttccaaggatctactcctctcacaatcagatggtttaagggcaacaaagagctggtttctggtggaagctgctatattaccaaagaagctttagagagttccctggaactctatttagtaaaaacctctgattcgggcacgtacacatgtaaagtcagcaatgtcgctggaggggtggaatgcagtgcaaacttgtttgtaaaagaacctgccacatttgttgaaaagttagagccatcacagctgttaaagaagggagatgccacccagctagcctgcaaagtaactggtacccctccaattaaaataacatggtttgcaaatgatagagaaattaaggagagcagcaaacacagaatgtcttttgtggagtctactgcagttctaagactgacagatgttggcatcgaagacagtggtgaatatatgtgtgaggcccaaaatgaggctggcagtgaccactgcagtagcattgtaatagtcaaagagtcaccttattttaccaaggaatttaagccaattgaagtcttaaaggagtacgatgtcatgttgctggctgaggtggcaggcactcctccctttgagatcacttggttcaaagataacacaatcctgcgaagtggtagaaagtataagactttcattcaggatcatctggttagcctgcagatcctcaagtttgtagctgcagatgctggcgaataccagtgtcgggtgaccaatgaggtgggcagcagcatctgcagtgccagggtgactttaagagaacccccatccttcataaagaagatcgagagtaccagctccctccggggaggcacagctgccttccaggccactctgaagggctccttgccaattacggtgacttggctaaaagacagcgatgaaatcactgaagacgataatataagaatgacctttgagaacaatgtggccagtttgtacctcagtggcattgaagtcaagcatgatgggaaatatgtctgtcaggccaaaaatgatgcaggaatacaaagatgttctgcattactctcagtaaaagaacctgcaacaatcaccgaggaagctgtgtctatagatgtcacccaaggagacccagccactttgcaggttaaattttcagggactaaggagattacagccaaatggtttaaagatggccaagaactgaccctgggctcaaaatataaaatcagtgtcactgatacagtctcaatactgaagattatttctacagaaaagaaagatagtggagaatatactttcgaggtccaaaatgatgttgggaggagcagctgcaaggctagaattaatgtattagatcttatcatacctccttcattcaccaaaaagctgaagaaaatggacagcattaaaggttctttcattgatttagaatgtatagtggctgggtcacatcccataagcatccagtggttcaaagatgaccaagaaatatcagctagtgaaaagtacaaattctcttttcatgacaatactgccttcttggaaatcagccagctggaaggtacagacagtgggacatacacttgttctgccacaaataaggcagggcacaaccaatgcagtgggcatctgacagtcaaagaacccccttactttgtggaaaagccacagtcacaagatgtcaatcccaacacaagggtacagttaaaggctcttgtgggtggcactgcacccatgacaataaagtggtttaaagataacaaagagttacactcaggagcagcccgctcagtttggaaggatgacacctctaccagtctagagctctttgcagccaaagctaccgattctggaacctacatttgccaactaagcaatgatgttggcacagcaaccagcaaagccactctctttgtaaaagaacctcctcaattcattaagaagcccagtccagtcttagtgctgaggaatggacagtcaacaacatttgaatgccaaataacaggcactcctaaaatccgagtgtcttggtatctagacgggaatgaaataacagccattcagaaacatggcatttccttcattgatggtttagccactttccagatttctggtgccagggtagaaaatagtgggacttatgtgtgtgaagctcgaaatgacgcaggcacggcgagctgcagcattgaactcaaagtgaaagaaccccccacctttatcagagagctgaagcctgtggaggtagtaaaatatagtgacgtggagctggagtgtgaagttacgggaacacctccgtttgaagtcacttggctgaagaataacagggaaattcgaagcagcaaaaaatacacattgaccgacagagtgtctgtgtttaacctccatataaccaagtgtgacccttcagacactggggaataccagtgcattgtatccaatgaaggcggcagctgctcatgcagtactagagttgccctgaaagaaccaccatcattcattaagaagatagaaaataccactactgttttgaaaagttctgccacctttcagagtaccgtggcaggttctcctcctatttctataacctggctaaaggatgatcagattcttgatgaagatgataatgtctatatttcatttgtggacagtgtggccacacttcaaatcagaagtgtggataatggacacagtgggagatatacctgtcaagccaagaatgagtcaggagttgagaggtgttatgctttccttttagtacaagaaccagctcaaatcgtagagaaagctaaatcagttgatgttacggagaaagatcccatgaccttggaatgtgttgtggctggaacaccagaactcaaagtgaaatggcttaaagacgggaaacaaatagttcccagtagatacttttcaatgagttttgaaaataacgtggccagttttagaattcagtcagtaatgaagcaggacagcggtcagtacactttcaaggtggaaaatgacttcggaagcagtagctgtgatgcctacttaagagtgctagatcaaaatattcctccatcattcaccaaaaaattaaccaaaatggataaagttctgggctcttctattcatatggagtgcaaggtgtctggttcacttcccattagtgctcagtggtttaaggatggaaaagaaatatctacaagtgcaaaatacagacttgtgtgccatgagagatctgtgtctctggaagttaataatctggaattagaagatactgcaaattacacatgcaaagtgtcaaatgtagcaggagatgatgcatgcagtggcatcttaactgtgaaagaaccaccaagcttcctagtgaaacctgggcgacagcaagccatacctgattctacagtggaatttaaggcaatactaaaaggaacaccaccatttaaaataaaatggtttaaggatgatgtggaacttgtctcaggtcctaaatgtttcattggcttggaagggtcgactagcttcttaaatctctactcagtggatgcttctaagactggacagtatacttgccatgttaccaatgatgttggtagcgactcttgtactacgatgttgcttgtgacagaaccaccaaagtttgtaaagaaattagaagcctccaaaattgtgaaagcaggtgactcttcacgacttgaatgcaagatagctggatccccagaaatcagagttgtgtggttccgaaatgaacatgaacttccagccagtgataagtacagaatgactttcattgactcagtggccgtcatacagatgaacaatctcagtactgaggacagtggagatttcatttgtgaggctcagaatcccgctggcagcacaagctgcagtaccaaggttatagtaaaagagccacctgtttttagcagcttccctcctatagtagaaacccttaaaaatgctgaagtcagtcttgaatgtgaactttcgggaacaccaccgtttgaggtggtatggtacaaagacaagcggcaactcagaagcagcaagaaatacaagattgcatccaaaaacttccacacaagtattcacattctcaatgtggacacttcagacatcggtgaataccactgcaaagcacagaatgaagtgggaagtgatacttgtgtttgtactgtaaaattgaaagaaccaccaagatttgtctccaaactgaacagcctcactgttgtagccggagagcctgctgaattacaagcatccatagaaggcgcccagcctatttttgtccagtggcttaaagagaaggaagaagtgattagagaaagtgaaaacatcaggattacatttgtggaaaatgttgcaactctacagtttgcaaaagcagagccagctaatgctggaaagtatatctgccaaatcaagaatgatggtggaatgagggagaacatggccacactgatggtcttagagcccgcagtcattgttgagaaggcaggaccgatgacggtgactgtaggagaaacgtgcactctggagtgtaaggtggctggcactccggaactctctgttgaatggtacaaggatggaaagctgttgaccagcagccaaaaacacaaatttagcttctacaacaaaatctcttccttaaggattctctcagttgaaaggcaagatgcaggcacatacactttccaggttcaaaataatgttgggaaaagcagctgcacagctgtggttgatgtttcagaccgagcagttcctccctctttcacacgaagactgaaaaatactggtggggtgttaggtgcttcttgcatcttggaatgcaaagtagctggatcatcacctatttcagttgcctggtttcatgagaaaaccaagattgtcagtggagcaaagtaccaaaccacattttcagataatgtctgcacattgcagttgaattctctggattcctcagatatgggcaattacacatgcgtggctgctaatgtcgctgggtctgatgaatgtcgtgcagtgctaactgtacaagaaccaccctcttttgtgaaagaacctgaacctttggaagtactaccaggcaaaaatgtaaccttcacaagcgttattagaggaacccctccattcaaagtcaactggttcagaggtgccagagaactagtgaaaggagaccggtgcaacatctattttgaagacactgtggcagaactggaattatttaatattgacatatctcagagtggggaatacacctgtgtggtttctaacaacgctggccaagcatcttgcactacccgtctctttgtgaaagaaccagctgcatttttgaagagattaagtgatcattctgtagaaccagggaagtccataattctggagagcacctacactggaacacttccaatttctgtcacttggaaaaaggatggttttaacataactacctctgaaaaatgtaacatagtcacaacagagaaaacttgtatcctggaaattctgaatagcacaaaaagagatgcagggcagtattcctgcgagattgaaaatgaggcaggaagggatgtttgtggagctctggtatctacattagaaccgccttattttgttacggaactggaacctctggaggcagcagttggagattcggtttctttacaatgccaagttgctgggacaccagaaattacagtgtcttggtacaaaggagataccaaattacggccaactcctgaatacaggacctactttacaaacaatgtggccacacttgtttttaataaagtgaacatcaatgacagtggagagtacacatgcaaagcagaaaatagtataggaactgcttcttctaagactgtgttcagaattcaagagcgccaactcccaccttcttttgcaagacaattaaaggacattgaacaaactgtggggttacctgttacactcacttgtcgattaaatggctctgcacccatccaagtgtgctggtatagagatggagtacttttaagagacgatgaaaatctacagacttcatttgtagataatgtggcaactttaaaaattttgcaaactgacttgagtcactctggccagtactcttgctcagcttccaacccacttggaacagcatcttctagtgctagactcacagcaagagaacccaagaaatctcccttctttgacatcaagcctgtatctatagatgttattgctggagaaagtgctgattttgagtgtcatgttactggtgctcaaccgatgcgaatcacttggtcaaaagataacaaggagatccgtcctggaggaaactatacaatcacatgtgtgggaaacactcctcatttgagaattcttaaagtaggcaaaggagactctggtcaatatacttgccaagcaaccaatgatgttggcaaagacatgtgctcagctcagctcagtgtaaaagaacctccaaagtttgttaagaaattagaagcttcaaaagttgcaaagcagggagaatccattcaactggaatgtaaaataagtggctccccagaaatcaaagtttcatggttccgaaatgacagcgaacttcatgaaagttggaaatacaacatgtcattcattaattctgtggcattgcttacgatcaatgaagctagtgctgaagacagcggggactacatttgtgaggctcataatggtgttggtgacgccagctgcagcacagcgttgacagtgaaagcacctcctgttttcacccagaagccttctccagtaggagctcttaaaggttctgatgtgattctccaatgtgaaatttcgggaactcccccatttgaagtagtatgggttaaagatcgaaagcaggttagaaacagcaagaaatttaaaatcacttcaaaacattttgatacaagtcttcatatccttaatcttgaagcctccgatgtcggggaatatcactgcaaagctactaatgaggtgggaagtgacacgtgttcttgctctgtcaagttcaaagaacctccacgattcgtgaaaaagctaagtgacacctcaacccttattggggatgctgttgagttacgggccatagtggagggcttccagccaatttctgttgtctggctgaaagatagaggtgaagtcatcagagagagtgaaaataccaggatttcattcattgataacattgcaaccctccagctggggagtccagaagcatctaattctggaaaatatatatgccaaatcaaaaacgatgctggaatgagagaatgctctgcagtcttgactgtactagaaccagcaagaatcatagagaagcccgaacccatgactgtcactactggaaatccttttgcattagagtgtgtagtgactggaacaccagaactctcagccaagtggttcaaagatggaagagaattgtctgcagacagcaaacatcacattacattcatcaataaagtggcttcccttaaaatcccctgtgccgaaatgagtgacaaaggattatatagctttgaagtgaaaaacagtgttggcaaaagtaactgcactgtatccgtccatgtttctgatcggattgtgcctccttccttcatccgcaagctgaaagacgtgaatgccatcctgggggcctcagttgttttggagtgccgagtctctggctcagccccgatttcagttggctggtttcaggatggaaatgagattgttagtgggcccaaatgtcagtccagcttttcggaaaacgtctgtactttgaatctgagcttgttggagccctccgacacaggcatatacacgtgtgtggctgccaatgtagctggttccgatgagtgctcagcagtcctgactgtgcaagaaccaccatcttttgaacaaacccctgattctgtggaagttttgcctggaatgagcctcacattcaccagtgtcatcagaggcacccctcctttcaaggtcaagtggtttaaaggcagcagggaactggtgcctggggagtcatgcaacatctctctggaagattttgtcacagaactggagttgtttgaggtgcagccattagaaagtggagactattcttgcctcgttacaaatgatgctggcagtgcttcctgtaccacacatctttttgtgaaagaaccagccacctttgtgaaaagattggctgatttcagtgttgagacaggaagccccatagttctcgaggccacatacactggcacacctccaatctcagtgagctggataaaggacgagtatcttatttcacaatctgagagatgcagtattactatgacagaaaaatctaccatactggaaattcttgagagcacaatagaggattatgcacagtacagctgcctgatagaaaatgaggctggtcaagatatctgtgaagctctggtgtctgtcttagaaccaccgtactttattgaacctctggaacatgtggaagcagtcattggagaacctgcaactttacagtgtaaagtggatggaactccagaaattagaatttcttggtataaagaacacacaaagctacgatcagctcctgcatataaaatgcaattcaaaaataacgttgcttccttagtaatcaacaaagtggatcacagtgatgtgggagagtattcatgcaaggcagacaacagtgtgggggcagtcgcttcttcagctgtgcttgttatcaaagcgcgcaaacttccacctttctttgcaagaaaactgaaagacgttcatgagactctaggcttcccagttgcatttgaatgccgcatcaatggctcagaacctcttcaagtgtcttggtacaaggatggggttcttttgaaagatgatgctaatttgcagacatcttttgttcataatgtagcaactcttcagattttacaaactgaccagagccacatagggcagtataattgctctgcttctaatcctcttgggactgcttcatccagtgccaagctcattctctcagagcatgaagtgcctcctttctttgatctaaagcctgtatcagtagatcttgctcttggagaaagtggtacttttaaatgtcatgtaactgggactgcaccaatcaaaatcacttgggccaaagataaccgagagattcgccctggaggcaactacaagatgactttggtagaaaatactgccactctgacagttctcaaagtaggcaaaggcgatgccgggcagtacacctgctatgcaagcaacatcgctggaaaagactcttgttctgctcagctgggtgtacaagaaccacccaggttcattaagaagctagaaccttcaagaattgtgaaacaggatgaattcacaaggtatgaatgcaaaatcggtgggtctccagaaatcaaagttttatggtataaggacgagactgaaattcaagagagcagtaaattcagaatgtcattcgttgactcggtggctgtgctggaaatgcacaatctcagtgttgaagacagtggagactacacctgtgaggcccacaatgcagcaggcagtgccagcagcagcacatccttaaaagttaaagaaccacccattttccgcaaaaagcctcatcctatagagacactgaaaggagctgatgttcaccttgaatgtgagcttcagggcactcccccatttcacgtttcttggtataaagacaagagagaacttaggagcggcaagaagtacaagataatgtctgagaacttcctaaccagtatccacatcctgaatgtcgatgctgcagacattggggaatatcagtgtaaagccacaaatgatgtgggaagcgacacttgcgttggttccatcgctctcaaagcaccaccaagatttgtgaagaagctcagtgacatatctactgtcgttggtaaagaagttcagctgcagactaccattgaaggcgctgaacccatttcagtggtgtggttcaaagataagggagaaatcgttagagaaagtgacaacatatggatttcttattcagaaaacattgcaaccttacagttttcaagagtggaaccagccaatgctggaaaatacacttgtcagatcaaaaacgatgctgggatgcaagagtgcttcgccacgctatccgttctcgagcctgcaacaattgtagaaaagccagaatccataaaagttaccacgggagacacctgtaccttggagtgtacagtagctggcacccctgaactcagtactaagtggtttaaggatggaaaagagctaacaagtgacaacaaatacaaaataagcttcttcaacaaagtatccggccttaagatcatcaatgtagcaccgagtgacagtggggtatacagttttgaggtgcagaaccctgttggcaaagacagctgcacagcttcattgcaggtttcagaccgaaccgttcctccttcattcacaagaaaattgaaagagacaaatggtctatccggctcctcagttgtaatggagtgtaaagtctatgggtcacctccaatctctgtctcctggttccatgaaggaaatgagatcagtagtggaaggaaataccagaccaccctgacagataatacttgtgctttaactgtgaacatgctggaagaatcagacagtggtgactacacatgtatagctactaatatggctggttctgatgaatgtagtgctcctttgactgtgagagaaccaccatcttttgttcagaaacctgatcccatggatgttttaactgggaccaatgtaactttcacaagtatcgtaaaaggaacacctcctttcagtgttagctggttcaaaggtagcagtgaactagtaccaggtgacagatgcaacgtgtctttggaggattcagttgctgaactggagttgttcgatgtagatacatcacaaagtggagaatatacttgcatagttagcaatgaagctggcaaggcttcttgtacaacacatctctacataaaagccccagccaaatttgtgaagaggctgaatgattacagcatagagaaaggaaaacccctgatcctagagggtacattcactggaactcctcccatttcggttacctggaagaaaaatggcataaatgtaactccttctcagaggtgtaatataactacgactgaaaaatcggcaatcctggaaattccaagtagcacagtagaggatgcaggacaatacaactgctacattgaaaatgcctctggaaaagattcctgttcagcacaaatactcatactagaaccaccgtattttgtcaagcagttggagccggttaaggtgtctgttggagattctgcctctctacaatgccagcttgctggaacccctgaaattggggtatcctggtataaaggagatacaaaattgagacccactacgacttacaaaatgcattttaggaataatgttgctacactggttttcaaccaggttgatattaatgatagtggagaatatatctgcaaagctgaaaacagcgtgggagaagtttctgcatcaactttccttaccgttcaagagcaaaaacttccaccatcattttctcgacaattgagagatgttcaagaaacagttggactgccagttgtttttgattgtgccataagtggatcagaacctatctccgtgtcttggtataaagacggcaagccattgaaagacagcccaaatgtacaaacatcatttttagacaatacagccacactcaatatttttaaaactgaccggagccttgcaggccagtattcctgcacagctacaaaccctataggctctgcttcttccagtgccaggctcattctcacagaggggaagaacccacccttctttgacatccgtcttgcccctgtggatgctgtggtgggagaaagtgctgactttgagtgccacgtcacgggcacacaaccgataaaggtcagctgggccaaagacagcagagaaatacgaagtggcggaaagtaccagattagttatctggaaaacagcgcccacctgacagtcctcaaagtagacaaaggagattctggacaatatacctgctatgctgtgaatgaagtgggaaaagactcttgcacagctcagctgaatataaaagagcggctcatcccaccaagtttcactaaaagactctcagagacagtagaagaaacagaagggaattctttcaaacttgagggacgtgtggctggttcccaacctataactgttgcctggtacaaaaataatatagagatacaaccaacttctaactgtgaaataacattcaagaacaacacgttagtgctgcaagtcaggaaagcaggcatgaacgacgctggtttgtacacatgcaaagtgtccaatgatgcaggctctgctctgtgcacgtcttcaatcgtcatcaaagaacctaagaagccacctgtatttgatcagcaccttactccagtaacagtgagtgaaggagaatacgtgcagctcagctgccatgtccagggatctgagccaattaggatccagtggttgaaggctggcagggaaataaagccttcagacagatgcagcttcagctttgctagtgggacagctgtactggaactcagagatgtggctaaagcagattcgggagattatgtgtgtaaagcttcaaatgtggctggaagtgacactaccaaatcaaaagtgaccattaaagacaaaccagctgtggccccagcaaccaagaaagctgcggtagatggaagactcttttttgtgtcagaacctcagagtatcagagtcgtagaaaaaaccactgcgaccttcattgcaaaagttggaggtgacccaatcccaaatgttaaatggacaaaagggaagtggagacagctgaaccaaggaggtcgtgttttcatccaccaaaaaggcgatgaagcaaaactggagattagggacaccacaaaaactgattctgggttataccgatgcgtggcatttaacgaacatggtgaaattgaaagtaatgttaacttacaggtggatgaaaggaagaaacaagagaaaattgaaggcgatcttagagcaatgctgaaaaagactccaatcttaaagaaaggagctggagaagaagaggaaattgatatcatggaacttctcaaaaatgttgatcctaaagaatatgaaaaatatgcccgcatgtatggaatcactgacttccgaggtcttcttcaagcatttgaactgctcaagcaaagccaagaagaagagacacatagactggaaattgaggaaatagagaggtcagagagggacgaaaaggaatttgaggaacttgtatcatttattcagcaaagactgtcacagacagagcctgtcactctgatcaaggacattgaaaatcagacagttttgaaagataatgatgctgtctttgaaattgacattaaaattaattatccagaaatcaagctttcgtggtacaaaggaactgaaaaactggaacccagtgataaatttgaaataagcattgatggtgaccgacatacactcagagtcaaaaactgtcaacttaaagaccagggcaattatcgattggtttgtggtccacacatcgctagcgctaaactaactgtaattgagcctgcatgggaacggcatcttcaggatgtgactctgaaagaaggccagacttgcaccatgacatgccaattttctgtaccaaatgtaaaatctgaatggttcagaaatggcagaatccttaaaccccaaggtagacataaaacagaagtggaacacaaagtccataaattgaccattgcagatgttcgagcagaagaccagggtcagtacacctgcaaatatgaagaccttgaaacttcagcagaactcagaatcgaagctgaaccaattcagtttacaaagcgcatacagaacatcgtggtgagtgagcatcagtctgccacctttgagtgtgaagtgtcctttgatgatgccattgtaacatggtacaaaggaccaacagaactgacagagagccagaaatacaacttcaggaatgatggccgctgccattatatgaccatccacaatgtgaccccagacgatgaaggtgtctactcggtcatcgctcggctggagccaagaggtgaagcaagaagcacggcagagctgtacctaacgacgaaagaaatcaaacttgagctgaagcctcctgatattcctgactccagagttccaatcccaaccatgcctatcagagcagtgccaccagaagaaatccctcctgtggttgctcctcctatcccccttttgctaccaacacccgaagaaaagaaaccaccaccaaaacgtattgaagttaccaaaaaggctgtgaagaaagatgccaaaaaagttgttgcaaagcccaaagagatgacaccacgtgaagagattgtcaagaagcctccacctcctactaccttaattccagcaaaagctcctgaaatcattgatgtatcctctaaagctgaagaagtaaaaataatgactataaccagaaagaaagaggttcagaaagaaaaagaagctgtgtatgagaaaaagcaagcagtccacaaggagaagagagtcttcattgaatctttcgaagaaccttatgacgaactggaggtagaaccatacacagagccatttgaacaaccttattatgaagaaccagatgaagactatgaagagattaaggtagaagctaaaaaagaagttcacgaggaatgggaagaagattttgaagaagggcaagaatactatgaaagggaagaaggctatgacgaaggggaggaagagtgggaagaggcttaccaagaaagggaagtaattcaagttcaaaaggaggtctatgaagaatcacatgagagaaaagttccagccaaagtacctgaaaagaaagcaccaccacctcctaaagttataaagaagccagtaattgaaaaaattgaaaagacttctcgaagaatggaggaagaaaaagttcaagtcaccaaagtacctgaagtttcaaagaagattgttccacaaaaaccttcccggactccagtacaggaagaagttattgaagtgaaagtaccagctgtgcatacaaagaagatggttatttcagaagaaaagatgttctttgcttctcacacagaggaggaggtgtcagtcacagtccccgaggtacaaaaggaaattgttactgaagagaaaattcacgttgccatttccaaaagggttgaaccaccacctaaagtccctgagctacctgagaaaccagctccagaagaagtggcccctgttcctatccctaaaaaagtggagcccccagcaccaaaagttcctgaggttcccaagaaacccgtgccagaggagaaaaagccagttcctgtgcctaagaaggaacctgctgctcccccaaaagtcccagaggtgccaaagaaacctgtccctgaagaaaagattcccgttcctgttgcaaagaaaaaggaagctcccccagctaaagttcctgaagtacagaagggagttgtgacagaagaaaaaataaccattgtaactcaaagagaggaatctccaccaccagcagtgccagaaataccaaagaagaaagttcctgaagaaaggaaacctgttcctcggaaggaggaagaagttccaccaccaccaaaagtgccagctctgcctaagaaacccgtcccagaggagaaagttgcagtgccagttcctgtcgctaagaaagctcctcctccccgagctgaagtctctaagaaaactgttgtagaagaaaagagatttgttgctgaagaaaaactatccttcgcagttcctcaaagagtggaagtcacgcggcacgaagtatctgcagaggaggaatggagttactcagaagaggaggaaggtgtgtccatttcagtttatagagaagaagaaagagaggaggaggaagaagcagaggttacagaatatgaagtgatggaagagcctgaggaatatgttgtggaagaaaagctgcacattatttctaagagagtggaagctgagccagctgaagtgacagagaggcaggagaagaaaattgtactgaaaccaaaaattcctgctaaaatagaggagcctccaccggctaaagttcctgaagcacctaagaaaattgtgccagaaaagaaagttcctgctccagttcctaaaaaggaaaaggtgcccccacctaaagtgccagaagagccaaagaaaccagttccagaaaaaaaggttcctccaaaagtcattaagatggaagaacctctcccagccaaagtgactgagaggcacatgcaaattacccaggaagaaaaagttcttgttgctgtaactaaaaaagaggcgcctccaaaagcaagagtgccagaggaaccgaagagagctgtcccagaagaaaaagttctgaaactcaaacctaaaagagaggaggaaccaccagctaaagtgactgaattcagaaaaagagtggttaaagaagaaaaagtatcaattgaagctccaaaaagagaacctcaacccatcaaagaagtaactataatggaagagaaagaaagggcttataccctagaagaagaagctgtttcagtacaacgggaagaagaatatgaggaatatgaagaatatgattataaagaatttgaggagtatgaaccaacagaagaatatgaccaatatgaagaatacgaggagcgggagtatgaacgatatgaagagcatgaagaatacatcacagaaccagagaagcctatccctgtaaagcctgtcccagaagaaccagttcccacaaaaccaaaggccccaccggctaaagtgctgaagaaagctgtccctgaagaaaaagtaccagtgcccattcctaagaaactcaaacctccaccacccaaagtgcctgaagaaccaaagaaagtttttgaggaaaaaatacgtatttcaattaccaaacgtgaaaaagagcaggtgactgaacccgctgctaaagtgcccatgaagcccaagagggttgtcgcagaagaaaaagtacctgtccctagaaaagaagtagcaccacctgttagagtgccagaagtgcctaaagaacttgaaccagaagaggttgcctttgaagaggaagttgtaacccatgtagaagaatatcttgtagaagaagaagaagagtacattcatgaagaagaggagttcataactgaggaagaagtggtgccagtgataccagtcaaagtgcctgaggtacccaggaaacctgttccagaagagaagaaacctgttcctgttcccaagaagaaggaagctccaccggcaaaagtgcctgaggttcctaagaagccagaggagaaagttcctgtgcttattcctaaaaaggagaagcctccgccagcaaaagttcctgaagtgcccaagaaacctgtgccagaggagaaagtaccagtaccagttcctaaaaaggtggaagctccacctgccaaagtgccagaggtacccaagaagcctgtgcctgagaagaaggtgccagttcctgctcctaagaaagtggaggctccacctgcaaaagtgccagaggtgcccaagaagctcatcccagaagaaaagaaaccaacacctgttccgaaaaaagtggaagcaccaccacccaaagtgccaaagaaacgtgaaccagttccagttcctgtagctctacctcaggaagaggaagttctatttgaagaagaaattgttcctgaagaggaagttctacctgaggaagaggaagttctacctgaggaagaggaagttctacctgaagaagaggaagttctacctgaagaagaggaaattccacctgaggaagaggaagttcctcccgaagaagaatatgtacctgaggaagaagaatttgtacctgaagaagaagtccttccagaagttaaacctaaggtgccagtacctgcaccagtgcctgaaattaagaagaaagtgacagagaagaaagtggtcattcccaagaaagaggaggctccccctgccaaagttcctgaggtgcctaagaaggtggaagaaaaacgaatcattctccctaaagaagaggaagttctaccagttgaagtgactgaggagcctgaagaagagcctatttcagaagaagaaatcccagaagaaccacctagcatagaggaagttgaagaggtggcaccacctagagtgcctgaagtgattaagaaagcagtacctgaagcacctactcctgttcctaaaaaagtggaggcaccaccagctaaagtgtcaaagaaaattcctgaggaaaaagtacctgttcctgttcagaaaaaagaggcacccccagccaaagtgcctgaagtaccaaagaaagtcccagaaaagaaagtccttgtgcctaaaaaggaagctgttcccccagctaaagggagaactgtccttgaagaaaaagtatcagttgccttccgccaagaagtagtagtaaaagaaagactagaattagaagtagtagaagcagaagtggaagaaattccggaagaagaagagttccatgaagttgaagaatattttgaagaaggcgagtttcatgaagtagaagaattcatcaaattagaacaacatagagttgaagaagaacacagagttgaaaaagttcatagggtaatagaagtttttgaggctgaagaagtggaagtatttgaaaaaccaaaagctccacctaaagggcctgagatatctgagaaaatcatccctccaaaaaaaccgcccactaaagttgttcctcgaaaagagccaccagctaaagtaccggaggtgcctaagaaaattgtggtagaagaaaaagtacgtgttcctgaagagcccagagttccaccaactaaagtgcctgaagtgctgccaccgaaggaagtggtcccagaaaagaaagtaccggtgcctcctgccaaaaagccagaagctccacctcctaaagtgcctgaggctcccaaagaagttgttcctgaaaagaaagtgccagtgcctcctcctaaaaagcctgaagtgccacccacaaaagtcccagaggtgccaaaggcagctgtcccagaaaagaaggtgcctgaagctattcctcccaaaccggaaagtcctccccctgaag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("TTN");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// no RefSeq ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 2, 179519684, PositionType.ZERO_BASED), "",
				"AAGT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002umz.1:exon112:c.21594_21595insACTT:p.Val7199Thrfs*8",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: CPS1:uc010fur.3:exon2:c.15_16insTTC:p.I5delinsIF, chr2:211421454->TTC
	 *
	 * Mutalyzer: NM_001122633.2:n.97_98insTTC NM_001122633.2(CPS1_v001):c.15_16insTTC
	 * NM_001122633.2(CPS1_i001):p.(Ile5_Lys6insPhe)
	 */
	@Test
	public void testRealWorldCase_uc010fur_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010fur.3	chr2	+	211342405	211543831	211342487	211542709	39	211342405,211421442,211438021,211441069,211442144,211444437,211447340,211452781,211454829,211455523,211456554,211457602,211459231,211460210,211464095,211465278,211466925,211469825,211471454,211473084,211476840,211481146,211502425,211503873,211504719,211507207,211512586,211513196,211515086,211518748,211521248,211523322,211525208,211527846,211532909,211539625,211540451,211541730,211542610,	211342490,211421583,211438131,211441214,211442234,211444494,211447433,211452871,211454958,211455630,211456693,211457680,211459330,211460306,211464285,211465436,211467054,211469970,211471665,211473283,211477017,211481265,211502567,211503939,211504783,211507389,211512781,211513264,211515162,211518826,211521356,211523412,211525379,211527921,211533008,211539685,211540564,211541860,211543831,	J3KQL0	uc010fur.3");
		this.transcriptForward
				.setSequence("gctcttttaaaatagttgctttcttaggaaatgtagttgctttcttaacctcatcaaattcatgaagatttagccgaggcccatgccacaaatcatcaaaatgacgaggattttgacagctttcaaagtggtgaggacactgaagactggttttggctttaccaatgtgactgcacaccaaaaatggaaattttcaagacctggcatcaggctcctttctgtcaaggcacagacagcacacattgtcctggaagatggaactaagatgaaaggttactcctttggccatccatcctctgttgctggtgaagtggtttttaatactggcctgggagggtacccagaagctattactgaccctgcctacaaaggacagattctcacaatggccaaccctattattgggaatggtggagctcctgatactactgctctggatgaactgggacttagcaaatatttggagtctaatggaatcaaggtttcaggtttgctggtgctggattatagtaaagactacaaccactggctggctaccaagagtttagggcaatggctacaggaagaaaaggttcctgcaatttatggagtggacacaagaatgctgactaaaataattcgggataagggtaccatgcttgggaagattgaatttgaaggtcagcctgtggattttgtggatccaaataaacagaatttgattgctgaggtttcaaccaaggatgtcaaagtgtacggcaaaggaaaccccacaaaagtggtagctgtagactgtgggattaaaaacaatgtaatccgcctgctagtaaagcgaggagctgaagtgcacttagttccctggaaccatgatttcaccaagatggagtatgatgggattttgatcgcgggaggaccggggaacccagctcttgcagaaccactaattcagaatgtcagaaagattttggagagtgatcgcaaggagccattgtttggaatcagtacaggaaacttaataacaggattggctgctggtgccaaaacctacaagatgtccatggccaacagagggcagaatcagcctgttttgaatatcacaaacaaacaggctttcattactgctcagaatcatggctatgccttggacaacaccctccctgctggctggaaaccactttttgtgaatgtcaacgatcaaacaaatgaggggattatgcatgagagcaaacccttcttcgctgtgcagttccacccagaggtcaccccggggccaatagacactgagtacctgtttgattcctttttctcactgataaagaaaggaaaagctaccaccattacatcagtcttaccgaagccagcactagttgcatctcgggttgaggtttccaaagtccttattctaggatcaggaggtctgtccattggtcaggctggagaatttgattactcaggatctcaagctgtaaaagccatgaaggaagaaaatgtcaaaactgttctgatgaacccaaacattgcatcagtccagaccaatgaggtgggcttaaagcaagcggatactgtctactttcttcccatcacccctcagtttgtcacagaggtcatcaaggcagaacagccagatgggttaattctgggcatgggtggccagacagctctgaactgtggagtggaactattcaagagaggtgtgctcaaggaatatggtgtgaaagtcctgggaacttcagttgagtccattatggctacggaagacaggcagctgttttcagataaactaaatgagatcaatgaaaagattgctccaagttttgcagtggaatcgattgaggatgcactgaaggcagcagacaccattggctacccagtgatgatccgttccgcctatgcactgggtgggttaggctcaggcatctgtcccaacagagagactttgatggacctcagcacaaaggcctttgctatgaccaaccaaattctggtggagaagtcagtgacaggttggaaagaaatagaatatgaagtggttcgagatgctgatgacaattgtgtcactgtctgtaacatggaaaatgttgatgccatgggtgttcacacaggtgactcagttgttgtggctcctgcccagacactctccaatgccgagtttcagatgttgagacgtacttcaatcaatgttgttcgccacttgggcattgtgggtgaatgcaacattcagtttgcccttcatcctacctcaatggaatactgcatcattgaagtgaatgccagactgtcccgaagctctgctctggcctcaaaagccactggctacccattggcattcattgctgcaaagattgccctaggaatcccacttccagaaattaagaacgtcgtatccgggaagacatcagcctgttttgaacctagcctggattacatggtcaccaagattccccgctgggatcttgaccgttttcatggaacatctagccgaattggtagctctatgaaaagtgtaggagaggtcatggctattggtcgtacctttgaggagagtttccagaaagctttacggatgtgccacccatctatagaaggtttcactccccgtctcccaatgaacaaagaatggccatctaatttagatcttagaaaagagttgtctgaaccaagcagcacgcgtatctatgccattgccaaggccattgatgacaacatgtcccttgatgagattgagaagctcacatacattgacaagtggtttttgtataagatgcgtgatattttaaacatggaaaagacactgaaaggcctcaacagtgagtccatgacagaagaaaccctgaaaagggcaaaggagattgggttctcagataagcagatttcaaaatgccttgggctcactgaggcccagacaagggagctgaggttaaagaaaaacatccacccttgggttaaacagattgatacactggctgcagaatacccatcagtaacaaactatctctatgttacctacaatggtcaggagcatgatgtcaattttgatgaccatggaatgatggtgctaggctgtggtccatatcacattggcagcagtgtggaatttgattggtgtgctgtctctagtatccgcacactgcgtcaacttggcaagaagacggtggtggtgaattgcaatcctgagactgtgagcacagactttgatgagtgtgacaaactgtactttgaagagttgtccttggagagaatcctagacatctaccatcaggaggcatgtggtggctgcatcatatcagttggaggccagattccaaacaacctggcagttcctctatacaagaatggtgtcaagatcatgggcacaagccccctgcagatcgacagggctgaggatcgctccatcttctcagctgtcttggatgagctgaaggtggctcaggcaccttggaaagctgttaatactttgaatgaagcactggaatttgcaaagtctgtggactacccctgcttgttgaggccttcctatgttttgagtgggtctgctatgaatgtggtattctctgaggatgagatgaaaaaattcctagaagaggcgactagagtttctcaggagcacccagtggtgctgacaaaatttgttgaaggggcccgagaagtagaaatggacgctgttggcaaagatggaagggttatctctcatgccatctctgaacatgttgaagatgcaggtgtccactcgggagatgccactctgatgctgcccacacaaaccatcagccaaggggccattgaaaaggtgaaggatgctacccggaagattgcaaaggcttttgccatctctggtccattcaacgtccaatttcttgtcaaaggaaatgatgtcttggtgattgagtgtaacttgagagcttctcgatccttcccctttgtttccaagactcttggggttgacttcattgatgtggccaccaaggtgatgattggagagaatgttgatgagaaacatcttccaacattggaccatcccataattcctgctgactatgttgcaattaaggctcccatgttttcctggccccggttgagggatgctgaccccattctgagatgtgagatggcttccactggagaggtggcttgctttggtgaaggtattcatacagccttcctaaaggcaatgctttccacaggatttaagataccccagaaaggcatcctgataggcatccagcaatcattccggccaagattccttggtgtggctgaacaattacacaatgaaggtttcaagctgtttgccacggaagccacatcagactggctcaacgccaacaatgtccctgccaccccagtggcatggccgtctcaagaaggacagaatcccagcctctcttccatcagaaaattgattagagatggcagcattgacctagtgattaaccttcccaacaacaacactaaatttgtccatgataattatgtgattcggaggacagctgttgatagtggaatccctctcctcactaattttcaggtgaccaaactttttgctgaagctgtgcagaaatctcgcaaggtggactccaagagtcttttccactacaggcagtacagtgctggaaaagcagcatagagatgcagacaccccagccccattattaaatcaacctgagccacatgttatctaaaggaactgattcacaactttctcagagatgaatattgataactaaacttcatttcagtttactttgttatgccttaatattctgtgtcttttgcaattaaattgtcagtcacttcttcaaaaccttacagtccttcctaagttactcttcatgagatttcatccatttactaatactgtatttttggtggactaggcttgcctatgtgcttatgtgtagctttttactttttatggtgctgattaatggtgatcaaggtaggaaaagttgctgttctattttctgaactctttctatactttaagatactctatttttaaaacactatctgcaaactcaggacactttaacagggcagaatactctaaaaacttgataaaattaaatatagatttaatttatgaaccttccatcatgatgtttgtgtattgcttctttttggatcctcattctcacccatttggctaatccaggaatattgttatcccttcccattatattgaagttgagaaatgtgacagaggcatttagagtatggacttttcttttctttttctttttctttttttctttttgagatggagtcacactctccaggctggagtgcagtggcacaatctcggctcactgcaatttccgtctcccaagttcaagcgattctcctgctttagactatggatttctttaaggaatactggtttgcagttttgttttctggactatatcagcagatggtagacagtgtttatgtagatgtgttgttgtttttatcattggattttaacttggcccgagtgaaataatcagatttttgtcattcacactctcccccagttttggaataacttggaagtaaggttcattcccttaagacgatggattctgttgaactatggggtcccacactgcactattaattccacccactgtaagggcaaggacaccattccttctacatataagaaaaaagtctctccccaagggcagcctttgttacttttaaatattttctgttattacaagtgctctaattgtgaacttttaaataaaatactattaagaggtaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("CPS1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_001122633.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 2, 211421454, PositionType.ZERO_BASED), "",
				"TTC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010fur.3:exon2:c.15_16insTTC:p.Ile5_Lys6insPhe", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: MUC4:uc021xjp.1:exon2:c.8108_8109insTG:p.T2703fs, chr3:195510343->CA
	 *
	 * Mutalyzer: NM_018406.6:n.8263_8264insTG NM_018406.6:c.8108_8109insTG NM_018406.6:p.(Ser2704Alafs*301)
	 */
	@Test
	public void testRealWorldCase_uc021xjp_1_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc021xjp.1	chr3	-	195473637	195538844	195474046	195538688	25	195473637,195475772,195477759,195478077,195479243,195479921,195481083,195484017,195485994,195487754,195488354,195488957,195489725,195490303,195490915,195491867,195492140,195493533,195495892,195497086,195498522,195501042,195505173,195505660,195538606,	195474251,195475935,195477983,195478142,195479317,195480101,195481243,195484199,195486132,195487988,195488456,195489125,195489816,195490512,195491035,195491993,195492320,195493622,195496023,195497242,195498687,195501176,195505326,195518368,195538844,	E9PDY6	uc021xjp.1");
		this.transcriptForward
				.setSequence("ctcttttgtcctcttcccaggttccctggccccttcggagaaacgcacttggttcgggccagccgcctgaggggacgggctcacgtctgctcctcacactgcagctgctgggccgtggagcttccccagggagccagggggacttttgccgcagccatgaagggggcacgctggaggagggtcccctgggtgtccctgagctgcctgtgtctctgcctccttccgcatgtggtcccaggaaccacagaggacacattaataactggaagtaaaactgctgccccagtcacctcaacaggctcaacaacagcgacactagagggacaatcaactgcagcttcttcaaggacctctaatcaggacatatcagcttcatctcagaaccaccagactaagagcacggagaccaccagcaaagctcaaaccgacaccctcacgcagatgatgacatcaactcttttttcttccccaagtgtacacaatgtgatggagacagctcctccagatgaaatgaccacatcatttccctccagtgtcaccaacacactcatgatgacatcaaagactataacaatgacaacctccacagactccactcttggaaacacagaagagacatcaacagcaggaactgaaagttctaccccagtgacctcagcagtctcaataacagctggacaggaaggacaatcacgaacaacttcctggaggacctctatccaagacacatcagcttcttctcagaaccactggactcggagcacgcagaccaccagggaatctcaaaccagcaccctaacacacagaaccacttcaactccttctttctctccaagtgtacacaatgtgacagggactgtttctcagaagacatctccttcaggtgaaacagctacctcatccctctgtagtgtcacaaacacatccatgatgacatcagagaagataacagtgacaacctccacaggctccactcttggaaacccaggggagacatcatcagtacctgttactggaagtcttatgccagtcacctcagcagccttagtaacatttgatccagaaggacaatcaccagcaactttctcaaggacttctactcaggacacaacagctttttctaagaaccaccagactcagagcgtggagaccaccagagtatctcaaatcaacaccctcaacaccctcacaccggttacaacatcaactgttttatcctcaccaagtggattcaacccaagtggaacagtttctcaggagacattcccttctggtgaaacaaccacctcatccccttccagtgtcagcaatacattcctggtaacatcaaaggtgttcagaatgccaacctccagagactctactcttggaaacacagaggagacatcactatctgtaagtggaaccatttctgcaatcacttccaaagtttcaaccatatggtggtcagacactctgtcaacagcactctcccccagttctctacctccaaaaatatccacagctttccacacccagcagagtgaaggtgcagagaccacaggacggcctcatgagaggagctcattctctccaggtgtgtctcaagaaatatttactctacatgaaacaacaacatggccttcctcattctccagcaaaggccacacaacttggtcacaaacagaactgccctcaacatcaacaggtgctgccactaggcttgtcacaggaaatccatctacagggacagctggcactattccaagggtcccctctaaggtctcagcaataggggaaccaggagagcccaccacatactcctcccacagcacaactctcccaaaaacaacaggggcaggcgcccagacacaatggacacaagaaacggggaccactggagaggctcttctcagcagcccaagctacagtgtgactcagatgataaaaacggccacatccccatcttcttcacctatgctggatagacacacatcccaacaaattacaacggcaccatcaacaaatcattcaacaatacattccacaagcacctctcctcaggaatcaccagctgtttcccaaaggggtcacactcaagccccgcagaccacacaagaatcacaaaccacgaggtccgtctcccccatgactgacaccaagacagtcaccaccccaggttcttccttcacagccagtgggcactcgccctcagaaattgttcctcaggacgcacccaccataagtgcagcaacaacctttgccccagctcccaccggggatggtcacacaacccaggccccgaccacagcactgcaggcagcacccagcagccatgatgccaccctggggccctcaggaggcacgtcactttccaaaacaggtgcccttactctggccaactctgtagtgtcaacaccagggggcccagaaggacaatggacatcagcctctgccagcacctcacctgacacagcagcagccatgacccatacccaccaggctgagagcacagaggcctctggacaaacacagaccagcgaaccggcctcctcagggtcacgaaccacctcagcgggcacagctaccccttcctcatccggggcgagtggcacaacaccttcaggaagcgaaggaatatccacctcaggagagacgacaaggttttcatcaaacccctccagggacagtcacacaacccagtcaacaaccgaattgctgtccgcctcagccagtcatggtgccatcccagtaagcacaggaatggcgtcttcgatcgtccccggcacctttcatcccaccctctctgaggcctccactgcagggagaccgacaggacagtcaagcccaacttctcccagtgcctctcctcaggagacagccgccatttcccggatggcccagactcagaggacaagaaccagcagagggtctgacactatcagcctggcgtcccaggcaaccgacaccttctcaacagtcccacccacacctccatcgatcacatccactgggcttacatctccacaaaccgagacccacactctgtcaccttcagggtctggtaaaaccttcaccacggccctcatcagcaacgccacccctcttcctgtcacctacgcttcctcggcatccacaggtcacaccacccctcttcatgtcaccgatgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcccttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagaatccacaggtcacgtcacccctcttcctgtcaccagcttttcctcagcatccacaggtgacagcacccctcttcctgtcactgacacttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcactgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccaaaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctcttcctgtcaccggcctttcctcagctaccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccagccctcttcttgtcactgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccgacacttcctcagtatccacagctcacgccaccccacttcctgtcaccggcctttcttcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccatccctcttcctgtcaccagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacctctcttcctgtcaccatcccttcctcagcatcttcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacactaactcagcatccacaggtgacaccacccctcttcatgtcaccgacgcttcctcagtatccacaggtcacgccacctctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtacccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttccggtcaccgacacttcctcagtatctacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccactggtgacaccacgccgcttcctgtcaccgatacttcctcagcatccacaggtcaggacacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactaacccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcaatatccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccaccgctcttcatgacaccgatgcttcctcattatccacaggggacaccacccctcttcctgtcaccagcccttcctcaacatccacaggtgacaccacccctcttcctgtcaccgaaacttcctcagtatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccatccctcttcttgtcaccgacacttcctcagcatccacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccagcctttcctcagtatccacaggtgacaccactcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccagctcttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccatcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccaacgtttcctcggcatccacaggtcatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctggcaccgacacttcctcagtatccacaggtcacaccacccctcttcttgtcaccgacgcttcgtcagtatccacaggtgacaccacccgtcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctctacctgtcaccgacactccctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacacgtcacgccacctctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttccgcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttactcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccaacacttcctcagtatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacgcttcgtcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgttcacaggtcatgccacctctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgctacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgacaccgacacttcctcagcatccacaggtcacgccacccttcttcctgtcaccgacacttcctcagcatccataggtcacgccacctctcttcctgtcaccgacacttcctcaatatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccaaccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccaccgctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtgacaccacccctgtgcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccgacatttcctcggcatccacaggtcaggccacccctcttcctgtcaccaacacttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctgttcctgtcaccagcacttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcatgtcaccatcccttcctcagcatccacaggtgacaccagcactcttcctgtcaccggcgcttcctcagcatccaccggtcacgccacccctcttcctgtcaccgacacttcctcagtatccaccggtcacgccacgcctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccatcccttcctcatcatcctcaggtcacaccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgtcacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcacttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcagtgtccacaggtcacgccacgcctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccatcccttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgttaccggcctttcctcagcttccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacacgtcacgccacccctcttcctgtcaccgacacttcctcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggacatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagtatccacaggtcacgccacctctcttcctgtcaccagccgttcctcagcatccacaggtcacgccaccccccttcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccacccctgttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccagaggtgacaccagcactcttcctgtcaccgatgcttcctcagcatccaccggtcacgccacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctctgcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacggcacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtcacgccacccctcttgctgtcagcagtgctacctcagcttccacagtatcctcggactcccctctgaagatggaaacaccaggaatgacaacaccgtcactgaagacagacggtgggagacgcacagccacatcaccaccccccacaacctcccagaccatcatttccaccattcccagcactgccatgcacacccgctccacagctgcccccatccccatcctgcctgagagaggagtttccctcttcccctatggggcaggcgccggggacctggagttcgtcaggaggaccgtggacttcacctccccactcttcaagccggcgactggcttcccccttggctcctctctccgtgattccctctacttcacagacaatggccagatcatcttcccagagtcagactaccagattttctcctaccccaacccactcccaacaggcttcacaggccgggaccctgtggccctggtggctccgttctgggacgatgctgacttctccactggtcgggggaccacattttatcaggaatacgagacgttctatggtgaacacagcctgctagtccagcaggccgagtcttggattagaaagatgacaaacaacgggggctacaaggccaggtgggccctaaaggtcacgtgggtcaatgcccacgcctatcctgcccagtggaccctcgggagcaacacctaccaagccatcctctccacggacgggagcaggtcctatgccctgtttctctaccagagcggtgggatgcagtgggacgtggcccagcgctcaggcaacccggtgctcatgggcttctctagtggagatggctatttcgaaaacagcccactgatgtcccagccagtgtgggagaggtatcgccctgatagattcctgaattccaactcaggcctccaagggctgcagttctacaggctacaccgggaagaaaggcccaactaccgtctcgagtgcctgcagtggctgaagagccagcctcggtggcccagctggggctggaaccaggtctcctgcccttgttcctggcagcagggacgacgggacttacgattccaacccgtcagcataggtcgctggggcctcggcagtaggcagctgtgcagcttcacctcttggcgaggaggcgtgtgctgcagctacgggccctggggagagtttcgtgaaggctggcacgtgcagcgtccttggcagttggcccaggaactggagccacagagctggtgctgccgctggaatgacaagccctacctctgtgccctgtaccagcagaggcggccccacgtgggctgtgctacatacaggcccccacagcccgcctggatgttcggggacccccacatcaccaccttggatggtgtcagttacaccttcaatgggctgggggacttcctgctggtcggggcccaagacgggaactcctccttcctgcttcagggccgcaccgcccagactggctcagcccaggccaccaacttcatcgcctttgcggctcagtaccgctccagcagcctgggccccgtcacggtccaatggctccttgagcctcacgacgcaatccgtgtcctgctggataaccagactgtgacatttcagcctgaccatgaagacggcggaggccaggagacgttcaacgccaccggagtcctcctgagccgcaacggctctgaggtctcggccagcttcgacggctgggccaccgtctcggtgatcgcgctctccaacatcctccacgcctccgccagcctcccgcccgagtaccagaaccgcacggaggggctcctgggggtctggaataacaatccagaggacgacttcaggatgcccaatggctccaccattcccccagggagccctgaggagatgcttttccactttggaatgacctggcagatcaacgggacaggcctccttggcaagaggaatgaccagctgccttccaacttcacccctgttttctactcacaactgcaaaaaaacagctcctgggctgaacatttgatctccaactgtgacggagatagctcatgcatctatgacaccctggccctgcgcaacgcaagcatcggacttcacacgagggaagtcagtaaaaactacgagcaggcgaacgccaccctcaatcagtacccgccctccatcaatggtggtcgtgtgattgaagcctacaaggggcagaccacgctgattcagtacaccagcaatgctgaggatgccaacttcacgctcagagacagctgcaccgacttggagctctttgagaatgggacgttgctgtggacacccaagtcgctggagccattcactctggagattctagcaagaagtgccaagattggcttggcatctgcactccagcccaggactgtggtctgccattgcaatgcagagagccagtgtttgtacaatcagaccagcagggtgggcaactcctccctggaggtggctggctgcaagtgtgacgggggcaccttcggccgctactgcgagggctccgaggatgcctgtgaggagccgtgcttcccgagtgtccactgcgttcctgggaagggctgcgaggcctgccctccaaacctgactggggatgggcggcactgtgcggctctggggagctctttcctgtgtcagaaccagtcctgccctgtgaattactgctacaatcaaggccactgctacatctcccagactctgggctgtcagcccatgtgcacctgccccccagccttcactgacagccgctgcttcctggctgggaacaacttcagtccaactgtcaacctagaacttcccttaagagtcatccagctcttgctcagtgaagaggaaaatgcctccatggcagaagtcaacgcctcggtggcatacagactggggaccctggacatgcgggcctttctccgcaacagccaagtggaacgaatcgattctgcagcaccggcctcgggaagccccatccaacactggatggtcatctcggagttccagtaccgccctcggggcccggtcattgacttcctgaacaaccagctgctggccgcggtggtggaggcgttcttataccacgttccacggaggagtgaggagcccaggaacgacgtggtcttccagcccatctccggggaagacgtgcgcgatgtgacagccctgaacgtgagcacgctgaaggcttacttcagatgcgatggctacaagggctacgacctggtctacagcccccagagcggcttcacctgcgtgtccccgtgcagtaggggctactgtgaccatggaggccagtgccagcacctgcccagtgggccccgctgcagctgtgtgtccttctccatctacacggcctggggcgagcactgtgagcacctgagcatgaaactcgacgcgttcttcggcatcttctttggggccctgggcggcctcttgctgctgggggtcgggacgttcgtggtcctgcgcttctggggttgctccggggccaggttctcctatttcctgaactcagctgaggccttgccttgaaggggcagctgtggcctaggctacctcaagactcacctcatccttaccgcacatttaaggcgccattgcttttgggagactggaaaagggaaggtgactgaaggctgtcaggattcttcaaggagaatgaatactgggaatcaagacaagactataccttatccataggcgcaggtgcacagggggaggccataaagatcaaacatgcatggatgggtcctcacgcagacacacccacagaaggacactagcctgtgcacgcgcgcgtgcacacacacacacacacacacgagttcataatgtggtgatggccctaagttaagcaaaatgcttctgcacacaaaactctctggtttacttcaaattaactctatttaaataaagtctctctgactttttgtgtctccaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MUC4");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 195510342, PositionType.ZERO_BASED), "",
				"CA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc021xjp.1:exon2:c.8108_8109insTG:p.Ser2704Alafs*301", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: MUC4:uc021xjp.1:exon2:c.6858_6859insCAG:p.T2286delinsTS, chr3:195511593->CTG
	 */
	@Test
	public void testRealWorldCase_uc021xjp_1_second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc021xjp.1	chr3	-	195473637	195538844	195474046	195538688	25	195473637,195475772,195477759,195478077,195479243,195479921,195481083,195484017,195485994,195487754,195488354,195488957,195489725,195490303,195490915,195491867,195492140,195493533,195495892,195497086,195498522,195501042,195505173,195505660,195538606,	195474251,195475935,195477983,195478142,195479317,195480101,195481243,195484199,195486132,195487988,195488456,195489125,195489816,195490512,195491035,195491993,195492320,195493622,195496023,195497242,195498687,195501176,195505326,195518368,195538844,	E9PDY6	uc021xjp.1");
		this.transcriptForward
				.setSequence("ctcttttgtcctcttcccaggttccctggccccttcggagaaacgcacttggttcgggccagccgcctgaggggacgggctcacgtctgctcctcacactgcagctgctgggccgtggagcttccccagggagccagggggacttttgccgcagccatgaagggggcacgctggaggagggtcccctgggtgtccctgagctgcctgtgtctctgcctccttccgcatgtggtcccaggaaccacagaggacacattaataactggaagtaaaactgctgccccagtcacctcaacaggctcaacaacagcgacactagagggacaatcaactgcagcttcttcaaggacctctaatcaggacatatcagcttcatctcagaaccaccagactaagagcacggagaccaccagcaaagctcaaaccgacaccctcacgcagatgatgacatcaactcttttttcttccccaagtgtacacaatgtgatggagacagctcctccagatgaaatgaccacatcatttccctccagtgtcaccaacacactcatgatgacatcaaagactataacaatgacaacctccacagactccactcttggaaacacagaagagacatcaacagcaggaactgaaagttctaccccagtgacctcagcagtctcaataacagctggacaggaaggacaatcacgaacaacttcctggaggacctctatccaagacacatcagcttcttctcagaaccactggactcggagcacgcagaccaccagggaatctcaaaccagcaccctaacacacagaaccacttcaactccttctttctctccaagtgtacacaatgtgacagggactgtttctcagaagacatctccttcaggtgaaacagctacctcatccctctgtagtgtcacaaacacatccatgatgacatcagagaagataacagtgacaacctccacaggctccactcttggaaacccaggggagacatcatcagtacctgttactggaagtcttatgccagtcacctcagcagccttagtaacatttgatccagaaggacaatcaccagcaactttctcaaggacttctactcaggacacaacagctttttctaagaaccaccagactcagagcgtggagaccaccagagtatctcaaatcaacaccctcaacaccctcacaccggttacaacatcaactgttttatcctcaccaagtggattcaacccaagtggaacagtttctcaggagacattcccttctggtgaaacaaccacctcatccccttccagtgtcagcaatacattcctggtaacatcaaaggtgttcagaatgccaacctccagagactctactcttggaaacacagaggagacatcactatctgtaagtggaaccatttctgcaatcacttccaaagtttcaaccatatggtggtcagacactctgtcaacagcactctcccccagttctctacctccaaaaatatccacagctttccacacccagcagagtgaaggtgcagagaccacaggacggcctcatgagaggagctcattctctccaggtgtgtctcaagaaatatttactctacatgaaacaacaacatggccttcctcattctccagcaaaggccacacaacttggtcacaaacagaactgccctcaacatcaacaggtgctgccactaggcttgtcacaggaaatccatctacagggacagctggcactattccaagggtcccctctaaggtctcagcaataggggaaccaggagagcccaccacatactcctcccacagcacaactctcccaaaaacaacaggggcaggcgcccagacacaatggacacaagaaacggggaccactggagaggctcttctcagcagcccaagctacagtgtgactcagatgataaaaacggccacatccccatcttcttcacctatgctggatagacacacatcccaacaaattacaacggcaccatcaacaaatcattcaacaatacattccacaagcacctctcctcaggaatcaccagctgtttcccaaaggggtcacactcaagccccgcagaccacacaagaatcacaaaccacgaggtccgtctcccccatgactgacaccaagacagtcaccaccccaggttcttccttcacagccagtgggcactcgccctcagaaattgttcctcaggacgcacccaccataagtgcagcaacaacctttgccccagctcccaccggggatggtcacacaacccaggccccgaccacagcactgcaggcagcacccagcagccatgatgccaccctggggccctcaggaggcacgtcactttccaaaacaggtgcccttactctggccaactctgtagtgtcaacaccagggggcccagaaggacaatggacatcagcctctgccagcacctcacctgacacagcagcagccatgacccatacccaccaggctgagagcacagaggcctctggacaaacacagaccagcgaaccggcctcctcagggtcacgaaccacctcagcgggcacagctaccccttcctcatccggggcgagtggcacaacaccttcaggaagcgaaggaatatccacctcaggagagacgacaaggttttcatcaaacccctccagggacagtcacacaacccagtcaacaaccgaattgctgtccgcctcagccagtcatggtgccatcccagtaagcacaggaatggcgtcttcgatcgtccccggcacctttcatcccaccctctctgaggcctccactgcagggagaccgacaggacagtcaagcccaacttctcccagtgcctctcctcaggagacagccgccatttcccggatggcccagactcagaggacaagaaccagcagagggtctgacactatcagcctggcgtcccaggcaaccgacaccttctcaacagtcccacccacacctccatcgatcacatccactgggcttacatctccacaaaccgagacccacactctgtcaccttcagggtctggtaaaaccttcaccacggccctcatcagcaacgccacccctcttcctgtcacctacgcttcctcggcatccacaggtcacaccacccctcttcatgtcaccgatgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcccttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagaatccacaggtcacgtcacccctcttcctgtcaccagcttttcctcagcatccacaggtgacagcacccctcttcctgtcactgacacttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcactgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccaaaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctcttcctgtcaccggcctttcctcagctaccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccagccctcttcttgtcactgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccgacacttcctcagtatccacagctcacgccaccccacttcctgtcaccggcctttcttcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccatccctcttcctgtcaccagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacctctcttcctgtcaccatcccttcctcagcatcttcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacactaactcagcatccacaggtgacaccacccctcttcatgtcaccgacgcttcctcagtatccacaggtcacgccacctctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtacccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttccggtcaccgacacttcctcagtatctacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccactggtgacaccacgccgcttcctgtcaccgatacttcctcagcatccacaggtcaggacacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactaacccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcaatatccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccaccgctcttcatgacaccgatgcttcctcattatccacaggggacaccacccctcttcctgtcaccagcccttcctcaacatccacaggtgacaccacccctcttcctgtcaccgaaacttcctcagtatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccatccctcttcttgtcaccgacacttcctcagcatccacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccagcctttcctcagtatccacaggtgacaccactcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccagctcttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccatcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccaacgtttcctcggcatccacaggtcatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctggcaccgacacttcctcagtatccacaggtcacaccacccctcttcttgtcaccgacgcttcgtcagtatccacaggtgacaccacccgtcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctctacctgtcaccgacactccctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacacgtcacgccacctctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttccgcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttactcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccaacacttcctcagtatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacgcttcgtcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgttcacaggtcatgccacctctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgctacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgacaccgacacttcctcagcatccacaggtcacgccacccttcttcctgtcaccgacacttcctcagcatccataggtcacgccacctctcttcctgtcaccgacacttcctcaatatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccaaccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccaccgctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtgacaccacccctgtgcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccgacatttcctcggcatccacaggtcaggccacccctcttcctgtcaccaacacttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctgttcctgtcaccagcacttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcatgtcaccatcccttcctcagcatccacaggtgacaccagcactcttcctgtcaccggcgcttcctcagcatccaccggtcacgccacccctcttcctgtcaccgacacttcctcagtatccaccggtcacgccacgcctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccatcccttcctcatcatcctcaggtcacaccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgtcacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcacttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcagtgtccacaggtcacgccacgcctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccatcccttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgttaccggcctttcctcagcttccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacacgtcacgccacccctcttcctgtcaccgacacttcctcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggacatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagtatccacaggtcacgccacctctcttcctgtcaccagccgttcctcagcatccacaggtcacgccaccccccttcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccacccctgttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccagaggtgacaccagcactcttcctgtcaccgatgcttcctcagcatccaccggtcacgccacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctctgcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacggcacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtcacgccacccctcttgctgtcagcagtgctacctcagcttccacagtatcctcggactcccctctgaagatggaaacaccaggaatgacaacaccgtcactgaagacagacggtgggagacgcacagccacatcaccaccccccacaacctcccagaccatcatttccaccattcccagcactgccatgcacacccgctccacagctgcccccatccccatcctgcctgagagaggagtttccctcttcccctatggggcaggcgccggggacctggagttcgtcaggaggaccgtggacttcacctccccactcttcaagccggcgactggcttcccccttggctcctctctccgtgattccctctacttcacagacaatggccagatcatcttcccagagtcagactaccagattttctcctaccccaacccactcccaacaggcttcacaggccgggaccctgtggccctggtggctccgttctgggacgatgctgacttctccactggtcgggggaccacattttatcaggaatacgagacgttctatggtgaacacagcctgctagtccagcaggccgagtcttggattagaaagatgacaaacaacgggggctacaaggccaggtgggccctaaaggtcacgtgggtcaatgcccacgcctatcctgcccagtggaccctcgggagcaacacctaccaagccatcctctccacggacgggagcaggtcctatgccctgtttctctaccagagcggtgggatgcagtgggacgtggcccagcgctcaggcaacccggtgctcatgggcttctctagtggagatggctatttcgaaaacagcccactgatgtcccagccagtgtgggagaggtatcgccctgatagattcctgaattccaactcaggcctccaagggctgcagttctacaggctacaccgggaagaaaggcccaactaccgtctcgagtgcctgcagtggctgaagagccagcctcggtggcccagctggggctggaaccaggtctcctgcccttgttcctggcagcagggacgacgggacttacgattccaacccgtcagcataggtcgctggggcctcggcagtaggcagctgtgcagcttcacctcttggcgaggaggcgtgtgctgcagctacgggccctggggagagtttcgtgaaggctggcacgtgcagcgtccttggcagttggcccaggaactggagccacagagctggtgctgccgctggaatgacaagccctacctctgtgccctgtaccagcagaggcggccccacgtgggctgtgctacatacaggcccccacagcccgcctggatgttcggggacccccacatcaccaccttggatggtgtcagttacaccttcaatgggctgggggacttcctgctggtcggggcccaagacgggaactcctccttcctgcttcagggccgcaccgcccagactggctcagcccaggccaccaacttcatcgcctttgcggctcagtaccgctccagcagcctgggccccgtcacggtccaatggctccttgagcctcacgacgcaatccgtgtcctgctggataaccagactgtgacatttcagcctgaccatgaagacggcggaggccaggagacgttcaacgccaccggagtcctcctgagccgcaacggctctgaggtctcggccagcttcgacggctgggccaccgtctcggtgatcgcgctctccaacatcctccacgcctccgccagcctcccgcccgagtaccagaaccgcacggaggggctcctgggggtctggaataacaatccagaggacgacttcaggatgcccaatggctccaccattcccccagggagccctgaggagatgcttttccactttggaatgacctggcagatcaacgggacaggcctccttggcaagaggaatgaccagctgccttccaacttcacccctgttttctactcacaactgcaaaaaaacagctcctgggctgaacatttgatctccaactgtgacggagatagctcatgcatctatgacaccctggccctgcgcaacgcaagcatcggacttcacacgagggaagtcagtaaaaactacgagcaggcgaacgccaccctcaatcagtacccgccctccatcaatggtggtcgtgtgattgaagcctacaaggggcagaccacgctgattcagtacaccagcaatgctgaggatgccaacttcacgctcagagacagctgcaccgacttggagctctttgagaatgggacgttgctgtggacacccaagtcgctggagccattcactctggagattctagcaagaagtgccaagattggcttggcatctgcactccagcccaggactgtggtctgccattgcaatgcagagagccagtgtttgtacaatcagaccagcagggtgggcaactcctccctggaggtggctggctgcaagtgtgacgggggcaccttcggccgctactgcgagggctccgaggatgcctgtgaggagccgtgcttcccgagtgtccactgcgttcctgggaagggctgcgaggcctgccctccaaacctgactggggatgggcggcactgtgcggctctggggagctctttcctgtgtcagaaccagtcctgccctgtgaattactgctacaatcaaggccactgctacatctcccagactctgggctgtcagcccatgtgcacctgccccccagccttcactgacagccgctgcttcctggctgggaacaacttcagtccaactgtcaacctagaacttcccttaagagtcatccagctcttgctcagtgaagaggaaaatgcctccatggcagaagtcaacgcctcggtggcatacagactggggaccctggacatgcgggcctttctccgcaacagccaagtggaacgaatcgattctgcagcaccggcctcgggaagccccatccaacactggatggtcatctcggagttccagtaccgccctcggggcccggtcattgacttcctgaacaaccagctgctggccgcggtggtggaggcgttcttataccacgttccacggaggagtgaggagcccaggaacgacgtggtcttccagcccatctccggggaagacgtgcgcgatgtgacagccctgaacgtgagcacgctgaaggcttacttcagatgcgatggctacaagggctacgacctggtctacagcccccagagcggcttcacctgcgtgtccccgtgcagtaggggctactgtgaccatggaggccagtgccagcacctgcccagtgggccccgctgcagctgtgtgtccttctccatctacacggcctggggcgagcactgtgagcacctgagcatgaaactcgacgcgttcttcggcatcttctttggggccctgggcggcctcttgctgctgggggtcgggacgttcgtggtcctgcgcttctggggttgctccggggccaggttctcctatttcctgaactcagctgaggccttgccttgaaggggcagctgtggcctaggctacctcaagactcacctcatccttaccgcacatttaaggcgccattgcttttgggagactggaaaagggaaggtgactgaaggctgtcaggattcttcaaggagaatgaatactgggaatcaagacaagactataccttatccataggcgcaggtgcacagggggaggccataaagatcaaacatgcatggatgggtcctcacgcagacacacccacagaaggacactagcctgtgcacgcgcgcgtgcacacacacacacacacacacgagttcataatgtggtgatggccctaagttaagcaaaatgcttctgcacacaaaactctctggtttacttcaaattaactctatttaaataaagtctctctgactttttgtgtctccaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MUC4");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_018406.6

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 195511592, PositionType.ZERO_BASED), "",
				"CTG");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc021xjp.1:exon2:c.6858_6859insCAG:p.Thr2286_Thr2287insGln",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: FRG1:uc003izs.3:exon7:c.608_609insGACT:p.K203fs, chr4:190881973->GACT
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.transcriptForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FRG1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_004477.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 4, 190881973, PositionType.ZERO_BASED), "",
				"GACT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003izs.3:exon7:c.608_609insGACT:p.Gln204Thrfs*4", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: PRDM9:uc003jgo.3:exon11:c.1147_1148insTGA:p.P383delinsLT, chr5:23526344->TGA
	 */
	@Test
	public void testRealWorldCase_uc003jgo_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003jgo.3	chr5	+	23507723	23528706	23509142	23527882	11	23507723,23509058,23509578,23510028,23517989,23521131,23522412,23522722,23523399,23524442,23526341,	23507821,23509211,23509702,23510136,23518039,23521288,23522514,23522994,23523467,23524636,23528706,	Q9NQV7	uc003jgo.3");
		this.transcriptForward
				.setSequence("ctctgagagaacgcccggccagggtgaacgccgcggcaggagagcacgggagactgtgaagagcatggggagcctttgtcgtgcagcgtgaaacccttgagcctttggcctaggagctgggagactcagggcccttctcacactcagaattggagcagggccttctagacagtcccagcaccatgagccctgaaaagtcccaagaggagagcccagaagaagacacagagagaacagagcggaagcccatggtcaaagatgccttcaaagacatttccatatacttcaccaaggaagaatgggcagagatgggagactgggagaaaactcgctataggaatgtgaaaaggaactataatgcactgattactataggtctcagagccactcgaccagctttcatgtgtcaccgaaggcaggccatcaaactccaggtggatgacacagaagattctgatgaagaatggacccctaggcagcaagtcaaacctccttggatggccttaagagtggaacagcgtaaacaccagaagggaatgcccaaggcgtcattcagtaatgaatctagtttgaaagaattgtcaagaacagcaaatttactgaatgcaagtggctcagagcaggctcagaaaccagtgtccccttctggagaagcaagtacctctggacagcactcaagactaaaactggaactcaggaagaaggagactgaaagaaagatgtatagcctgcgagaaagaaagggtcatgcatacaaagaggtcagcgagccgcaggatgatgattacctctattgtgagatgtgtcagaacttcttcattgacagctgtgctgcccatgggccccctacatttgtaaaggacagtgcagtggacaaggggcaccccaaccgttcagccctcagtctgcccccagggctgagaattgggccatcaggcatccctcaggctgggcttggagtatggaatgaggcatctgatctgccgctgggtctgcactttggcccttatgagggccgaattacagaagacgaagaggcagccaacaatggatactcctggctgatcaccaaggggagaaactgctatgagtatgtggatggaaaagataaatcctgggccaactggatgaggtatgtgaactgtgcccgggatgatgaagagcagaacctggtggccttccagtaccacaggcagatcttctatagaacctgccgagtcattaggccaggctgtgaactgctggtctggtatggggatgaatacggccaggaactgggcatcaagtggggcagcaagtggaagaaagagctcatggcagggagagaaccaaagccagagatccatccatgtccctcatgctgtctggccttttcaagtcagaaatttctcagtcaacatgtagaacgcaatcactcctctcagaacttcccaggaccatctgcaagaaaactcctccaaccagagaatccctgcccaggggatcagaatcaggagcagcaatatccagatccacacagccgtaatgacaaaaccaaaggtcaagagatcaaagaaaggtccaaactcttgaataaaaggacatggcagagggagatttcaagggccttttctagcccacccaaaggacaaatggggagctgtagagtgggaaaaagaataatggaagaagagtccagaacaggccagaaagtgaatccagggaacacaggcaaattatttgtgggggtaggaatctcaagaattgcaaaagtcaagtatggagagtgtggacaaggtttcagtgttaaatcagatgttattacacaccaaaggacacatacaggggagaagctctacgtctgcagggagtgtgggcggggctttagctggaagtcacacctcctcattcaccagaggatacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagctggcagtcagtcctcctcactcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagccggcagtcagtcctcctcactcaccagaggagacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagccggcagtcagtcctcctcactcaccagaggagacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagctggcagtcagtcctcctcactcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagctggcagtcagtcctcctcactcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagcaataagtcacacctcctcagacaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttcgcgataagtcacacctcctcagacaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagagataagtcaaacctcctcagtcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagcaataagtcacacctcctcagacaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttcgcaataagtcacacctcctcagacaccagaggacacacacaggggagaagccctacgtctgcagggagtgtgggcggggctttagcgataggtcaagcctctgctatcaccagaggacacacacaggggagaagccctacgtctgcagggaggatgagtaagtcattagtaataaaacctcatctcaatagccacaaaaagacaaatgtggtcaccacacacttgcacaccccagctgtgaggtggcttcagcggaagtctgctgaccccttatattccccgagagtataaagagatcggaaataactgattaaacaaatccgccactttcatgactagagatgaggaagaacaagggatagttctgtaagtgttcgggggacatcagcatgtgtggttctttcccgcactgatcccctccattttttgtttgtttttttgcctcctgttctaataaattttgtctccatacaaatctgaaccccaagtgtgtacctcattcttcccttatcactgaaggcaagaagagtccagaagggccacagagaactcatgtgttcagctcaagactccacaggaattcaacccccagaaagacataaacttggagtccgtctggtttaattattggagaatcgattcccaagtccaggaagagaaatgtaagattctagaaagtcgcagcaggaaagggagttccctggtctcctgggaagtgtggcttcttctcctaatggacacctctcctctgctgccatactctcccttggctccccagtctcctctcctgatctcctccaatctctgtagcccaagatgtgaaagccagacaagaacacgcgtgtgtgtatatatgtgttcgggtgtgggggtatgtgccctccgtgtaggtaactgtgtgagtgtggggggtttcaagggtgtgttaggaacaacgctcaaaatcctaaggaaactgaacactcgaacgaaggattcttagcaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("PRDM9");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_020227.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 5, 23526344, PositionType.ZERO_BASED), "",
				"TGA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003jgo.3:exon11:c.1147_1148insTGA:p.Pro383delinsLeuThr",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION, annotation1.getVariantType());
	}

	/**
	 * annovar: SCAMP1:uc003kfl.3:exon8:c.730_731insT:p.C244fs, chr5:77745856->T
	 *
	 * -- According to mutalyzer, p.(Asn244Ilefs*52), thus should be p.N244fs (this is what jannovar says, annovar finds
	 * a "C")
	 */
	@Test
	public void testRealWorldCase_uc003kfl_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003kfl.3	chr5	+	77656338	77776562	77656495	77771491	10	77656338,77684660,77711343,77712364,77714598,77717624,77745756,77745849,77755067,77771326,	77656552,77684738,77711442,77712473,77714727,77717784,77745847,77745857,77755185,77776562,	O15126	uc003kfl.3");
		this.transcriptForward
				.setSequence("gcaccgggcatgctcagtggcgcggccggctgggtggcccgcgagctgcacccggatctgctcggcggcggcgacgtgagcgcgcaggggggcggcggcctcgcctcgtctctctctctgcgcctgggtcgggtgggtgacgccgagagccagagagatgtcggatttcgacagtaacccgtttgccgacccggatctcaacaatcccttcaaggatccatcagttacacaagtgacaagaaatgttccaccaggacttgatgaatataatccattctcggattctagaacacctccaccaggcggtgtgaagatgcctaatgtacccaatacacaaccagcaataatgaaaccaacagaggaacatccagcttatacacagattgcaaaggaacatgcattggcccaagctgaacttcttaagcgccaggaagaactagaaagaaaagccgcagaattagatcgtcgggaacgagaaatgcaaaacctcagtcaacatggtagaaaaaataattggccacctcttcctagcaattttcctgtcggaccttgtttctatcaggatttttctgtagacattcctgtagaattccaaaagacagtaaagcttatgtactacttgtggatgttccatgcagtaacactgtttctaaatatcttcggatgcttggcttggttttgtgttgattctgcaagagcggttgattttggattgagtatcctgtggttcttgctttttactccttgttcatttgtctgttggtacagaccactttatggagctttcaggagtgacagttcatttagattctttgtattcttcttcgtctatatttgtcagtttgctgtacatgtactccaagctgcaggatttcataactggggcaattgtggttggatttcatcccttactggtctcaaccaaaatattcctgttggaatcatgatgataatcatagcagcacttttcacagcatcagcagtcatctcactagttatgttcaaaaaagtacatggactatatcgcacaacaggtgctagttttgagaaggcccaacaggagtttgcaacaggtgtgatgtccaacaaaactgtccagaccgcagctgcaaatgcagcttcaactgcagcatctagtgcagctcagaatgctttcaagggtaaccagatttaagaatcttcaaacaatacactgttaccttttgactgtacctttttctccagttactgtattctacaaatatttttatgttcaaaacacacagtacagacagcatggatatttcctgttcacttgtgcatgggctaaaaccaggaaaacttccttgtcttattactttacctaatagtttcttaatatttcagtgccccttgcagaaaaaatattacatgctaaataaatattctccatatttttgggggatgacattcagtgaattatttcagtggtgacccactgaaaattaataatggtacttatgattaaaaacgcatttaatactaactgcagtagttctttcaagaatctttagagataaggattgcacattggaaaagtaaaccatgtttcattcctttttccctatttatattgaaagaaataggccagcagagacttagggattttaaattggcttgctttttagctgtttcagtcaccagtgaagagcctatgtgcattttgtagtagataatgtaaaatttgtcatctttttcttttcttttttttagaatagctgatattttgataacaatctctaatttgcatgggcaccacatttcttatattaaaagaattagtgttttggcttctgtactgcttatggttgtaggattcaggggttaatggaatcacagaaatgatattctgcaagaatttcttttaaataaaaagtttgggggtgcaatataagaagtttatataatatgcagtacattatccaaaagagaaggtagttaatgcagtagaaagtagtggtaataattcctttttaaaaaaatttcggtagtcatatagtaacattttgctatatgaaaactttggtatattctgtggttacaactaagattgtgtctggcagctcttttttggggatgtgtgtgtgtgatttttaacagaggtattaaaggctagcctaactgttgtctaaaaagattgtacagtatttaagggattttccttttagcttttcatctccagtggcattaaacataaaaagaccctggcattttttcacatacttgaatccctaaatgcacctgtctttcactttttgagacagactgaatatatctaaaatttccagcaataaaaaaaaaagcatttaacttgcaccaagcaagaaaatataaatacagttaactgcattaagataatcacgttaaaattgttactatgcagcacagaacttcattcttatagtattcttgggttcaacctttgaatcaattttaccactgattaaataaatgactcaaagacatctgtaagtcatgctgctgtgttttgaaagtctttaactaaattaagattgcagaatgatagtgattattcaattagattttaagtaaggattgtgatattagaggctggaaatccttattttttaaaaaatcagataggcataaatagttaaatcactttcattctccccaaacctgtagttacagaaaaagttttatgctagaggtgggatgccaagttttcactatccatgaagcagcgctgcatgtcactaggtaacacagatccatccagatggtgtttacatttgatttatttgggatcttattgacatcaggtatacttggaagacatttcttttattcttcagcgtatgaatttaaagctattttttgtaaatatttctaatcagcgataatttctacctatgttctcaaccaacttagccagtttgtttttcagagcctgtagtcttattggaaatctattttatcagtgtgctttattgagtgtggattttgcatacattcaaaacattaaccacaaaatacagcaagtgcacctatattcaccattaacttatatcccaagtccattttttcctgtacactacaaacaaaagatatattagagacttttgaaaaatgctgaaatactttgcttcagaattggaatgtttatattatgtagaaatcttcaaaggtagcattattaaatagcaaagaataattagaacccacatatctttttttgtgtggatggggaaaatgttttaaaatccagttatttaatatgagtttgagagagaaaattgttttttaaaaatatatgtgcattgaaatgatggcaatgcttatagtatgatcaagtatgaaaggaactttaaattcttatatttacttttctctcagtaaattgttaaattttcactcagcaaaagattggcatttgttaagtgttctatatttagtactaaaatcacagtcatgaaatcatagtcataaaatggtcttcacacagcagtcatccgtgtcatttatcattttgtaatattaaattatggcaattttatttcaaactaaagtttgaacaccggaaagtcattactcagtgatttgtaatttgggacttggattatttatctagagatgtttgtatattttgtcagtaactaatactgcgctgccatcatggtgactgtcatggttctacagaaatgccctccatgtgtccctctaatgttgcatgtttcagtgggttggaagttttgtatatttattgtattaacacagagtgtcataaaataaaatgctgtttactggatgtttgtttgtataattttgaacactataatagcaattcagagacagacattgttaaaggtttgatgtatatagaaattccatgtttgattttttaaaatatgtgtataagtctgtcatgtgctaaacaaaataatatgaaagacctagttaaaaattctaaccaatgtaaaatgaccatttttctgttgcattagacctttacaggtaatggaacatgagcttcacccatattaaatattttggcccctttaaggtcaaaatacagatcatctagaagttagattcaaaatggaaaacctattcatggctcagatttttcattgtgggttaaaaatgggtgtctctgtactagtattgtatttattcaattgaacttgtattctgatttctatccttgctacctattgctgttttatgtactgatgaaagtacctatttgtgtatattggatttttcacttggttagctaaagaagatgtaaaaatatctaaaataatgttcatggtgaatcttattttgagaaatacatgttaaaaaaggaacagtattctttattttctgggtgttatatttaaaaaagcaagtttggatttttacacctaatttactaggaaaatattttattctgtaattcatgttaagattatgtatggtttgcattttaaggggatttatgttaggttaattagttgtttctgtaaatcatttgtaatagcatagtgctttttactcattgctgtatctttttctgaaaacactgttgttaacatctaattcagtatccttattggtacaaatctgtgtttggcatgactgtttatatacagaatttgttacattttgagcattttttcccctgcttatgtataccttagagttaccatggctgtcatataccatttcactatatctcctttcagtttttccttaaggaaaatgtttagaggaatttgttcatttcatgtgattaagccctttagagatgaaataagattggttaattttaaaaaaattgaggatggttaaaaaatagaaaacaccttactttgatacattttaaagtacaatagtatacatttatttagagtagactaatgtgtttaaaacatgagttgttttaaatacttttttattgagctaaaaagttttatctcacatattaagtattacagaaagtgaagtattttggctagaattttagggcatattttataaagcagcatgcctgtaatattggtgggtatttttaaactttaggactttatcacagtatgtagagagctagaaataaatctagaaactttctaagccaggtattgccactaacctgtcttatataagcagatacctcttatttgaagattgtaggaaaatagagaaagactgttctccagttttctcacccccgctgtgggttttatatttacaatttaactttggggtttgggtaagacaaacatttaatgtataggattttggccaggtgtggtggctcacgcctgtaatcccagcactttgggaggccaaggtgggcggatcacgaggtcaggagatcgaaaccatcctggctaacatggtgaaaccccatctctactaaaaatacaaaaaaaaaattagctgagcatggtggcgggcgcctgtagtcccagctacttgggaggctgaggcaggagaatgacgtgaacctgggaggcggagcttgcagtgagccgagatctctccactgcactccagcctgggcgacagagcgagactccatctcaagaaaaaaaaaaaagaattttcattagtgctggccgtgtttcaaatggcaagggaacatgggaactatcatgtggcaatgtagtgagtgttaaactttgtgtttgtccaaatcctgatttatttttcagttcatatctttctgggcttgacatggctgatggtgtagctgaaaccctcctaacactaaaagccatttaatcttttctgtaataggagcagaaaatagttaatcatccacctagtaatataagattactgtgaatattatcttctatacattaaaacagttctagtttgtagaataataccatacaagttttatttttaaattctagttattttcagtgcttacttaaatgtaattctagaattcctccacaacttttaatattttgtatgccagtgattctcaagataaatcatgattgtagtagttgttactgttggcagtttgtagtagtattcaggtattttggggatgggggaaaacaccaaaaatcagtgtcttttatctggtgatcactgtggtatctacagtattctagtctcctgcacaaaaactgaacccactgggcctatgcatccctcacactttttttctagtataaaagcaatacataatgtgttgtagaacaattaaaaattcagaaagtgatacatgagaaaataaaaataaatccttaattctgtca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("SCAMP1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 5, 77745856, PositionType.ZERO_BASED), "", "T");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003kfl.3:exon8:c.730_731insT:p.Asn244Ilefs*52", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: PCDHB10:uc003lix.3:exon1:c.1806_1807insATGC:p.L602fs, chr5:140573931->ATGC
	 */
	@Test
	public void testRealWorldCase_uc003lix_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003lix.3	chr5	+	140571951	140575213	140572125	140574528	1	140571951,	140575213,	Q9UN67	uc003lix.3");
		this.transcriptForward
				.setSequence("gaagacacggacagatgaacttaaaagagaagctttagctgccaaagattgggaaagggaaaggacaaaaaagacccctgggctacacggcgtaggtgcagggtttcctactgctgttcttttatgctgggagctgtggctgtaaccaactaggaaataacgtatgcagcagctatggctgtcagagagttgtgcttcccaagacaaaggcaagtcctgtttctttttcttttttggggagtgtccttggcaggttctgggtttggacgttattcggtgactgaggaaacagagaaaggatcctttgtggtcaatctggcaaaggatctgggactagcagagggggagctggctgcaaggggaaccagggtggtttccgatgataacaaacaatacctgctcctggattcacataccgggaatttgctcacaaatgagaaactggaccgagagaagctgtgtggccctaaagagccctgtatgctgtatttccaaattttaatggatgatccctttcagatttaccgggctgagctgagagtcagggatataaatgatcacgcgccagtatttcaggacaaagaaacagtcttaaaaatatcagaaaatacagctgaagggacagcatttagactagaaagagcacaggatccagatggaggacttaacggtatccaaaactacacgatcagccccaactcttttttccatattaacattagtggcggtgatgaaggcatgatatatccagagctagtgttggacaaagcactggatcgggaggagcagggagagctcagcttaaccctcacagcgctggatggtgggtctccatccaggtctgggacctctactgtacgcatcgttgtcttggacgtcaatgacaatgccccacagtttgcccaggctctgtatgagacccaggctccagaaaacagccccattgggttccttattgttaaggtatgggcagaagatgtagactctggagtcaacgcggaagtatcctattcattttttgatgcctcagaaaatattcgaacaacctttcaaatcaatcctttttctggggaaatctttctcagagaattgcttgattatgagttagtaaattcttacaaaataaatatacaggcaatggacggtggaggcctttctgcaagatgtagggttttagtggaagtattggacaccaatgacaatccccctgaactgatcgtatcatcattttccaactctgttgctgagaattctcctgagacgccgctggctgtttttaagattaatgacagagactctggagaaaatggaaagatggtttgctacattcaagagaatctgccattcctactaaaaccttctgtggagaatttttacatcctaattacagaaggcgcgctggacagagagatcagagccgagtacaacatcactatcaccgtcactgacttggggacacccaggctgaaaaccgagcacaacataacggtcctggtctccgacgtcaatgacaacgcccccgccttcacccaaacctcctacaccctgttcgtccgcgagaacaacagccccgccctgcacatcggcagcgtcagcgccacagacagagactcgggcaccaacgcccaggtcacctactcgctgctgccgccccaagacccgcacctgcccctcgcctccctggtctccatcaacgcggacaacggccacctgttcgccctcaggtcgctggactacgaggccctgcaggctttcgagttccgcgtgggcgccacagaccgcggctcccccgcgctgagcagagaggcgctggtgcgcgtgctggtgctggacgccaacgacaactcgcccttcgtgctgtacccgctgcagaacggctccgcgccctgcaccgagctggtgccccgggcggccgagccgggctacctggtgaccaaggtggtggcggtggacggcgactcgggccagaacgcctggctgtcgtaccagctgctcaaggccacggagcccgggctgttcggtgtgtgggcgcacaatggggaggtgcgcaccgccaggctgctgagcgagcgcgacgcagccaagcacaggctcgtggtgcttgtcaaggacaatggcgagcctcctcgctcggccaccgccacgctgcacttgctcctggtggacggcttctcccagccctacctgcctctcccggaggcggccccggcccaggcccaggccgaggccgacttgctcaccgtctacctggtggtggcgttggcctcggtgtcttcgctcttcctcctctcggtgctcctgttcgtggcggtgcggctgtgcaggaggagcagggcggcctcggtgggtcgctgctcggtgcccgagggtccttttccagggcatctggtggacgtgaggggcgctgagaccctgtcccagagctaccagtatgaggtgtgtctgacgggaggccccgggaccagtgagttcaagttcttgaaaccagttatttcggatattcaggcacagggccctgggaggaagggtgaagaaaattccaccttccgaaatagctttggatttaatattcagtaaagtctgtttttagtttcatatacttttggtgtgttacatagccatgtttctattagtttacttttaaatctcaaatttaagttattatgcaacttcaagcattattttcaagtagtatacccctgtggttttacaatgtttcatcatttttttgcattaataacaactgggtttaatttaatgagtatttttttctaaatgatagtgttaaggttttaattctttccaactgcccaaggaattaattactattatatctcattacagaaatctgaggttttgattcatttcagagcttgcatctcatgattctaatcacttctgtctatagtgtacttgctctatttaagaaggcatatctacatttccaaactcattctaacattctatatattcgtgtttgaaaaccatgtcatttatttctacatcatgtatttaaaaagaaatatttctctactactatgctcatgacaaaatgaaacaaagcatattgtgagcaatactgaacatcaataatacccttagtttatatacttattattttatctttaagcatgctacttttacttggccaatattttcttatgttaacttttgctgatgtataaaacagactatgccttataattgaaataaaattataatctgcctgaaaatgaataaaaataaaacattttgaaatgtgaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("PCDHB10");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 5, 140573931, PositionType.ZERO_BASED), "",
				"ATGC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003lix.3:exon1:c.1806_1807insATGC:p.Ser603Metfs*144", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: AK098012:uc003nrp.1:exon2:c.254_255insCAAA:p.P85fs, chr6:30782220->TTTG
	 */
	@Test
	public void testRealWorldCase_uc003nrp_1() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003nrp.1	chr6	-	30780642	30798436	30781987	30782473	2	30780642,30798291,	30782549,30798436,	uc003nrp.1");
		this.transcriptForward
				.setSequence("attccaggaagcgtttgcacatcctggccccagaagattttgcgtagcctcaccaagagaagaagttggaaagaaaaaaaaagaagttggcaaggcatccagaggagaccccgccagcacctgggttctctgccaggggagagagttatgaagaagacttgagcaggcagtccagagctgatacagctgctccaatcaccagagatgcaatctcctgctagatgtcttcacatctacctttctatgtcacatgtgcaagatggttgctctgccttcagacatcacatttacattccaggcagaaaggagaaggaaagagcaaaagatgtgttccgaacaaatccatcttaaaaacaaacaaggggaggggtagcaggagaaaatttaaaaagaagaagaagaaaaacaaaaacaggaaagccgttgtttccctaaaagccccatcaagccgacttctaccggtgactcattacccagtcatgtggccacccctggattcaaggcagtctgaggaggtgagtgtttcaaagaggcacatggatgccacagacaaaatcaagtttccatggttgaaggagaagaggacgatagagtttggggaggcagcaggctacgtctgccacagaagatataaagatatggagccacacgagaagatgggggaggctgccaaaaaagtccactgggtacaggtgaggagcaggtgacattggagttgctccatctgaggagcccgcacctgcaatggcttgtgctcctgctcctgtgggtataggcacaaggctgatgccgagagagtgagttgtttctgttggggttcagaacacaataccccaaagtatggcgatgagtacttaactaaaggacattggaaggcctcagaagcagcctcagatccaaggtccctctgacctgctcctgccctcctgtctctctcgtccctcgttctcccctgaagtgagtcatagaaaccagaatcctcttccccacggtaggtcctagaaactagaaccctttttccccgaagcaaaagccataaaaactggaattattactctaaactctccttgactttttgtccagaagatggccataaagaaattctcagctaggtgtggtggctcgtatctgtaatcccagcactttgagaggccaaggtgggaggatcacttgaggccaggagtttgagatcggcctgggcaacatagtaagacccccatctctacaaaacttttttttttaaacttacctaagcgtggtggtacacctgtagtctcagctacttaggaggctgaggtgggagggaccctgctctggctgtgactgctttatcacagggagtgcctatctgtgggctgctttctacacattaaagcattttcagaaagggccaacatcagccttggaaaaaaggaggaagagccacggagagtctccctttatcttccccttatgtgatgtgagagtgcacatgactgtgtgcacatacacacacacacacacaccacacacacacaccccacaggcacacagggtcaagaacatgctgggcagagcttaagccactgaatgtttcagtaggaatcatgatatctaaaaataggacagctgataagggggttcttactactcacagttcctggaattatatgcaattgcatcttggaaactcaacaaataaacagttagtatggaattaaggaattaagtttggtgaagttcaaagaagctgatgttactcacaagagtaaatagagaggaaaccgggcgtggtggcttcacgcctgtaattctagcactttgggaggccaagacgagcagatcacttgaggtagagagtttgagaccagcctggccaacatgatgaaaccctgttctctactaaaaatacaaaaattagctgggtgtggtggcaggcacctgcaatcccagctacatgggaggctgaggcaggagaatcgcttgaacccaggagacaggggttgcagtgagccaagatcacaccactgcactccagcctgggcgacagagcaagactccctctcaggaaag"
						.toUpperCase());
		this.transcriptForward
.setGeneSymbol("AK098012");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 6, 30782220, PositionType.ZERO_BASED), "",
				"TTTG");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003nrp.1:exon2:c.255_256insAACA:p.Val86Asnfs*13", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: PRICKLE4:uc011duf.1:exon8:c.863_864insTCT:p.L288delinsLL, chr6:41754575->TCT
	 */
	@Test
	public void testRealWorldCase_uc011duf_1() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc011duf.1	chr6	+	41748499	41755563	41751211	41754867	8	41748499,41749087,41751199,41751868,41752672,41753074,41753865,41754499,	41748564,41749258,41751343,41751976,41752810,41753278,41754070,41755563,	Q2TBC4-3	uc011duf.1");
		this.transcriptForward
				.setSequence("gaccgggtgaggggcctgacggcagagtcggtccagaactgcccggacagcgacgcacagcgaggagccaaaatggaagagggtgcaagtcccctcctggggccccagacgccccctcctttcttgctgcctggagcaggggcagtgctgttagtggctactcctggtgtgaacagcccatcctggccaccttccacaggaaacctgacctgaaggacagaagaggaaggaagcaggctttgccacaaatgtcagtgcagaactctggctggccccaccaagaagacagccccaagccccaggatccaggtccaccagccaactcagacagtgactcaggccacctgccgggggaggaccctgaggatacccatgctcagggtcctgcagttctgagcttgggttccctttgcctggacaccaaccaagcccccaactggactggacttcagaccctcctgcagcaactccctccgcaggacattgatgagcgctactgcctggcccttggggaggaggagcgggccgagctgcagctcttctgtgccaggcggaagcaggaagccctgggacagggggtagcccgcctggtacttcccaagcttgaaggacacacctgtgagaagtgtagggagctgctgaagccaggggagtacggagtgtttgcagcccgggcaggggaacagcgctgctggcaccagccttgctttgcctgccaggcctgtggccaggccctgataaacctcatctacttctaccatgatggacaactctactgcggccgtcatcatgcagagttgctgcgcccgcgctgcccggcttgtgaccagctgatcttctcctggcgctgcaccgaggcggagggacagcgctggcatgagaaccacttctgttgccaggactgcgccgggcctctgggcgggggacgttatgccctgcctgggggaagcccctgctgccccagctgcttcgagaaccgctactcggatgcaggctcgagctgggccggggcactggaagggcaggcattccttggggagactggactcgaccgaactgaaggaagggaccaaacctcggtgaactctgcaaccctctcccgaacactcctcgctgctgccggcggttccagcctgcaaactcagagggggctgcctggatccagtccccagcaggagaaccgacctggggacaaagcggaggcacccaaagggcaggagcaatgccgcctggagactattcgtgatcccaaggacacccctttctccacctgctcctcctcctctgactcggaacctgaaggatttttcttaggcgagcgccttccccagtcctggaagacccccggaagcctccaagcagaggacagcaacgcctctaagacgcactgcaccatgtgctagtggcgcagctcagagaggggatgtgagtgggaggaaaggggtctgtaaagcgggagaacaaggctagcctccccctaacaatcctagactgagacgcagtcaggcgcacgcccgcaagaggcggcgaggtgacaagtttggagtgcgcccccttcagtactgcgcgttctaagacttttggcggagactttcttggcaaaacccattccccaaagctacgcttcccctgctgagatagcccct"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("PRICKLE4");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 6, 41754575, PositionType.ZERO_BASED), "",
				"TCT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc011duf.1:exon8:c.863_864insTCT:p.Leu288dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * annovar: AEBP1:uc003tkb.3:exon1:c.118_119insAAAA:p.G40fs, chr7:44144382->AAAA
	 */
	@Test
	public void testRealWorldCase_uc003tkb_4() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003tkb.4	chr7	+	44143959	44154164	44144264	44153860	21	44143959,44146144,44147037,44147227,44147407,44147605,44148497,44148705,44148886,44149613,44149805,44150323,44150511,44150752,44151105,44151452,44151740,44152156,44152589,44152850,44153192,	44144517,44146486,44147109,44147299,44147530,44147683,44148575,44148783,44148940,44149723,44149945,44150408,44150656,44150838,44151229,44151649,44151920,44152508,44152729,44152950,44154164,	Q8IUX7	uc003tkb.4");
		this.transcriptForward
				.setSequence("cggctatccgcgcgggagtgcgccacgcggggccggagcgcctattagccgccaggacctcggagcgccccgaccacccctgagcccctctggcttcggagccccccagcaccccttcccgggtcccctcgcccaccctaatccactctccctccctttcccggattccctcgctcaccccatcctctctcccgccccttcctggattccctcacccgtctcgatcccctctccgccctttcccagagacccagagcccctgaccccccgcgccctccccggagccccccgcgcgtgccgcggccatggcggccgtgcgcggggcgcccctgctcagctgcctcctggcgttgctggccctgtgccctggagggcgcccgcagacggtgctgaccgacgacgagatcgaggagttcctcgagggcttcctgtcagagctagaacctgagccccgggaggacgacgtggaggccccgccgcctcccgagcccaccccgcgggtccgaaaagcccaggcggggggcaagccagggaagcggccagggacggccgcagaagtgcctccggaaaagaccaaagacaaagggaagaaaggcaagaaagacaaaggccccaaggtgcccaaggagtccttggaggggtcccccaggccgcccaagaaggggaaggagaagccacccaaggccaccaagaagcccaaggagaagccacctaaggccaccaagaagcccaaggagaagccacccaaggccaccaagaagcccaaagagaagccacccaaggccaccaagaagcccccgtcagggaagaggccccccattctggctccctcagaaaccctggagtggccactgcccccaccccccagccctggccccgaggagctaccccaggagggaggggcgcccctctcaaataactggcagaatccaggagaggagacccatgtggaggcacgggagcaccagcctgagccggaggaggagaccgagcaacccacactggactacaatgaccagatcgagagggaggactatgaggactttgagtacattcggcgccagaagcaacccaggccacccccaagcagaaggaggaggcccgagcgggtctggccagagccccctgaggagaaggccccggccccagccccggaggagaggattgagcctcctgtgaagcctctgctgcccccgctgccccctgactatggtgatggttacgtgatccccaactacgatgacatggactattactttgggcctcctccgccccagaagcccgatgctgagcgccagacagacgaagagaaggaggagctgaagaaacccaaaaaggaggacagcagccccaaggaggagaccgacaagtgggcagtggagaagggcaaggaccacaaagagccccgaaagggcgaggagttggaggaggagtggacgcctacggagaaagtcaagtgtccccccattgggatggagtcacaccgtattgaggacaaccagatccgagcctcctccatgctgcgccacggcctgggggcacagcgcggccggctcaacatgcagaccggtgccactgaggacgactactatgatggtgcgtggtgtgccgaggacgatgccaggacccagtggatagaggtggacaccaggaggactacccggttcacaggcgtcatcacccagggcagagactccagcatccatgacgattttgtgaccaccttcttcgtgggcttcagcaatgacagccagacatgggtgatgtacaccaacggctatgaggaaatgacctttcatgggaacgtggacaaggacacacccgtgctgagtgagctcccagagccggtggtggctcgtttcatccgcatctacccactcacctggaatggcagcctgtgcatgcgcctggaggtgctggggtgctctgtggcccctgtctacagctactacgcacagaatgaggtggtggccaccgatgacctggatttccggcaccacagctacaaggacatgcgccagctcatgaaggtggtgaacgaggagtgccccaccatcacccgcacttacagcctgggcaagagctcacgaggcctcaagatctatgccatggagatctcagacaaccctggggagcatgaactgggggagcccgagttccgctacactgctgggatccatggcaacgaggtgctgggccgagagctgttgctgctgctcatgcagtacctgtgccgagagtaccgcgatgggaacccacgtgtgcgcagcctggtgcaggacacacgcatccacctggtgccctcactgaaccctgatggctacgaggtggcagcgcagatgggctcagagtttgggaactgggcgctgggactgtggactgaggagggctttgacatctttgaagatttcccggatctcaactctgtgctctggggagctgaggagaggaaatgggtcccctaccgggtccccaacaataacttgcccatccctgaacgctacctttcgccagatgccacggtatccacggaggtccgggccatcattgcctggatggagaagaaccccttcgtgctgggagcaaatctgaacggcggcgagcggctagtatcctacccctacgatatggcccgcacgcctacccaggagcagctgctggccgcagccatggcagcagcccggggggaggatgaggacgaggtctccgaggcccaggagactccagaccacgccatcttccggtggcttgccatctccttcgcctccgcacacctcaccttgaccgagccctaccgcggaggctgccaagcccaggactacaccggcggcatgggcatcgtcaacggggccaagtggaacccccggaccgggactatcaatgacttcagttacctgcataccaactgcctggagctctccttctacctgggctgtgacaagttccctcatgagagtgagctgccccgcgagtgggagaacaacaaggaggcgctgctcaccttcatggagcaggtgcaccgcggcattaagggggtggtgacggacgagcaaggcatccccattgccaacgccaccatctctgtgagtggcattaatcacggcgtgaagacagccagtggtggtgattactggcgaatcttgaacccgggtgagtaccgcgtgacagcccacgcggagggctacaccccgagcgccaagacctgcaatgttgactatgacatcggggccactcagtgcaacttcatcctggctcgctccaactggaagcgcatccgggagatcatggccatgaacgggaaccggcctatcccacacatagacccatcgcgccctatgaccccccaacagcgacgcctgcagcagcgacgcctacaacaccgcctgcggcttcgggcacagatgcggctgcggcgcctcaacgccaccaccaccctaggcccccacactgtgcctcccacgctgccccctgcccctgccaccaccctgagcactaccatagagccctggggcctcataccgccaaccaccgctggctgggaggagtcggagactgagacctacacagaggtggtgacagagtttgggaccgaggtggagcccgagtttgggaccaaggtggagcccgagtttgagacccagttggagcctgagtttgagacccagctggaacccgagtttgaggaagaggaggaggaggagaaagaggaggagatagccactggccaggcattccccttcacaacagtagagacctacacagtgaactttggggacttctgagatcagcgtcctaccaagaccccagcccaactcaagctacagcagcagcacttcccaagcctgctgaccacagtcacatcacccatcagcacatggaaggcccctggtatggacactgaaaggaagggctggtcctgcccctttgagggggtgcaaacatgactgggacctaagagccagaggctgtgtagaggctcctgctccacctgccagtctcgtaagagatggggttgctgcagtgttggagtaggggcagagggagggagccaaggtcactccaataaaacaagctcatggcacggacaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("AEBP1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 7, 44144382, PositionType.ZERO_BASED), "",
				"AAAA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003tkb.4:exon1:c.118_119insAAAA:p.Gly40Glufs*10", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * annovar: MUC12:uc003uxo.3:exon2:c.3442_3443insGTA:p.T1148delinsST, chr7:100637286->GTA
	 */
	@Test
	public void testRealWorldCase_uc003uxo_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003uxo.3	chr7	+	100612903	100662230	100612903	100661917	12	100612903,100633911,100649751,100651914,100652375,100655576,100656099,100656260,100657193,100658895,100660855,100661875,	100612970,100648800,100649853,100652042,100652440,100655728,100656171,100656423,100657355,100658972,100660944,100662230,	F5GWV9	uc003uxo.3");
		this.transcriptForward
				.setSequence("atgctggtgatctggattctgacgctggctctccggctctgcgcgtccgttactacagtgacaccaggctcaacagtaaacaccagtattggaggtaatacaacttctgcatccacacccagttcaagcgacccttttaccacctttagtgactatggggtgtcagtcacatttatcacgggctcaactgcaacaaaacacttccttgacagctccacaaactcaggccacagtgaggaatcaacagtatcccacagcggcccaggtgcaactggaacaacactcttcccttcccactctgcaacctcagtttttgttggagaacctaaaacctcacccatcacttcagcctcaatggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccagacagaacactctcacctgcccgcacgacaagctcaggcgtcagtgaaaaatcaaccacctcccacagccgaccaggcccaacgcacacaatagcgttccctgacagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcatccccggctccacagacacaacactgtcccctggcactaccacaccatcatcccttggtccagaatctactaccttccacagcagcccaggctacactaaaacaacacgcttacctgacaacaccacaacctcaggcctccttgaagcatctacgcccgtccacagcagcactggatcgccacacacaacactgtccccttccagctctacaacccatgagggagaacctaccaccttccagagctggccaagctcaaaggacacttcgcctgcaccttctggtaccacatcagcctttgttaaactatctacaacttatcacagcagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccatagtgaggaatcgacaccagtccacagcagcccagttgcaactgcaacaacacccccacctgcccgctccgcgacctcaggccatgttgaagaatctacagcctaccacaggagcccgggctcaactcaaacaatgcacttccctgaaagctccacaacttcaggccatagtgaagaatcagcaactttccacggcagcacaacacacacaaaatcttcaactcctagcaccacagctgccctagcacatacaagctaccacagcagcctgggctcaactgaaacaacacacttccgtgatagctccacaatctcaggccgtagtgaggaatcaaaagcatcccacagcagcccagatgcaatggcaacaacagtcttacctgccggctctacaccctcagttcttgttggagactcgacgccctcacccatcagttcaggctcaatggaaaccacagcgttacccggcagtaccacaaaaccaggcctcagtgagaaatctaccactttctacagtagccccagatcaccagacacaacacacttacctgccagcatgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacacacacaacagcattccctggcagtaccaccatgccaggcctcagtcaggaatctacagcttcccacagcagcccaggccccacagacacaacattgtcccctggcagtaccacagcatcatcccttggtccagaatatactaccttccacagccgcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggactccttgaagcatctatgcccgtccacagcagcaccagatcgccacacacaacactgtcccctgccggctctacaacccgtcagggagaatctaccacattccatagctggccaagctcaaaggacactaggcctgcacctcctactaccacatcagcctttgttgagccatctacaacctcccacggcagcccgagctcaattccaacaacccacatttctgcccgctccacaacctcaggcctcgttgaagaatctacgacctaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagcgacacaacttcaggccgtggtgaagaatcaacaacttcccacagcagcacaacacacacaatatcttcagctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcgcacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagagatctaccactttccatagtagccccagatcaccagccacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccgcagccgaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggccccagtcaggaatcaacaacttcccacagcagctcaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcaacccaggctccactcacacaacactcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctactgccttccagacccacccagcctcaactcacacaacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagcatgacaagcctgggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagccgaggctcaactgaaaccacagtgttccctcacagcaccacaacctcagttcatggtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaaaactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacctggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagcgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcattccctgacagcaccaccacctcagacctcagtcaggaacctacaacttcccacagcagccaaggctcaacagaggcaacactgtcccctggcagtaccacagcctcatcccttggtcaacaatctacaaccttccacagcagcccaggcgacactgaaaccacactcttacctgacgacaccataacctcaggcctcgtggaggcatctacacccacccacagcagcactggctcgctacacacaacactgacccctgccagctccacaagcgctggccttcaggaagaatctaccactttccagagctggccaagctcaagtgacacaacaccttcacctcccggcaccacagcagcccctgttgaagtatccacaacctaccacagccgcccgagctcaactccaacaacacacttttctgccagttccacaaccttgggccgtagtgaggaatcaacaacagtccacagcagcccaggtgcaactggaacagcactcttccctacccgctctgcaacctcagttcttgttggagaacctacaacgtcacccatcagttcaggctcaacggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccggacacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctggcagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcagcccaggctccacagacacaacattgtcccctggcagtaccacagcatcatcccttggtccagaatctactactttccacagcagcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggcctccttgaagcatctacgcccgtccacagcagcactggatcgccacacacaacactgtcccctgccggctctacaacacgtcagggagaatctaccaccttccagagctggccaagctcaaaggacactatgcctgcacctcctactaccacatcagcctttgttgagctatctacaacctcccacggcagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacggcgtaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagctccacagcttcaggtcgtagtgaagaatcaagaacttcccacagcagcacaacacacacaatatcttcacctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcccacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagaaatctaccactttccacagtagccccagatcaccagccacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggccccagtcaggaatcaacaacttcccacagcagcccaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcagcccaggctccactcacacaacgctcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctaccgccttccagacccacccagcctcaactcacacgacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagtatgacaagcctaggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagcccaggctcaactgaaaccacagtgttccctcgcacccccacaacctcagttcgtggtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaacactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacccggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagtgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggccccagtcaggaatcaacaacttcccacagcagcccaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcagcccaggctccactcacacaacactcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctaccaccttccagacccacccagcctcaactcacacgacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagtatgacaagcctaggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagcccaggctcaactgaaaccacagtgttccctcgcagcaccacaacctcagttcgtggtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaacactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacccggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagtgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcgttccctgacagcaccaccacctcaggcctcagtcaggaacctacagcttcccacagcagccaaggctcaacagaggcaacactgtcccctggcagtaccacagcctcatcccttggtcaacaatctacaaccttccacagcagcccaggcgacactgaaaccacactcttacctgacgacaccataacctcaggcctcgtggaggcatctacacccacccacagcagcactggctcgctacacacaacactgacccctgccagctccacaagcgctggccttcaggaagaatctaccactttccagagctggccaagctcaagtgacacaacaccttcacctcccggcaccacagcagcccctgttgaagtatccacaacctaccacagccgcccgagctcaactccaacaacacacttttctgccagttccacaaccttgggccgtagtgaggaatcaacaacagtccacagcagcccaggtgcaactggaacagcactcttccctacccgctctgcaacctcagttcttgttggagaacctacaacgtcacccatcagttcaggctcaacggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccggacacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctggcagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcagcccaggctccacagacacaacactgtcccctggcagtaccacagcatcatcccttggtccagaatctactaccttccacagcggcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggcctccttgaagcatctacgcccgtccacagcagcactggatcgccacacacaacactgtcccctgccggctctacaacccgtcagggagaatctaccaccttccagagctggcctaactcgaaggacactacccctgcacctcctactaccacatcagcctttgttgagctatctacaacctcccacggcagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacgacctaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagcgacacaacttcaggccgtggtgaagaatcaacaacttcccacagcagcacaacacacacaatatcttcagctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcccacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagaaatctaccactttccacagtagccccagatcaccagccacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggctccagtcaggaatcaacaacttcccacagcagctcaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcagcccaggctccactcacacaacactcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctaccgccttccagacccacccagcctcaactcacacgacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagtatgacaagcctaggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagcccaggctcaactgaaaccacagtgttccctcgcagcaccacaacctcagttcgtcgtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaaaactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacccggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagcgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcgttccctgacagcaccaccacctcaggcctcagtcaggaacctacaacttcccacagcagccaaggctcaacagaggcaacactgtcccctggcagtaccacagcctcatcccttggtcaacaatctacaaccttccacagcagcccaggcgacactgaaaccacactcttacctgacgacaccataacctcaggcctcgtggaggcatctacacccacccacagcagcactggctcgctacacacaacactgacccctgccagctccacaagcactggccttcaggaagaatctaccactttccagagctggccaagctcaagtgacacaacaccttcacctcccagcaccacagcagtccctgttgaagtatccacaacctaccacagccgcccgagctcaactccaacaacacacttttctgccagttccacaaccttgggccgtagtgaggaatcaacaacagtccacagcagcccaggtgcaactggaacagcactcttccctacccgctctgcaacctcagttcttgttggagaacctacaacgtcacccatcagttcaggctcaacggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccggacacaacactttcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaatgcacacaacagcattccctagcagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcagcccaggctccacagacacaacactgtcccctggcagtaccacagcatcatcccttggtccagaatctactaccttccacagcagcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggcctccttgaagcatctacacccgtccacagcagcactggatcgccacacacaacactgtcccctgccggctctacaacccgtcagggagaatctaccaccttccagagctggccaaactcgaaggacactacccctgcacctcctactaccacatcagcctttgttgagctatctacaacctcccacggcagcccgagctcaactccaacaacccacttttctgccagctccacaacattgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacgacctaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagcaacacaacttcaggccgtggtgaagaatcaacaacttcccacagcagcacaacacacacaatatcttcagctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcccacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagaaatctaccactttccacagtagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacggcgtaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagctccacagcttcaggtcgtagtgaagaatcaagaacttcccacagcagcacaacacacacaatatcttcacctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaattgcaacaacacactttcctgagagctccacaacctccggccgtagtgaggaatcaacagcatcccacagcagcccagatacaaatggaatcacacccttacctgcccattttactacctcaggccgcattgcagaatctaccaccttctatatctctccaggctcaatggaaacaacattagccagcactgccacaacaccaggcctcagtgcaaaatctaccatcctttacagtagctccagatcaccagaccaaacactctcacctgccagcatgacaagctccagcatcagtggagaacccaccagcttgtatagccaagcagagtcaacacacacaacagcgttccctgccagcaccaccacctcaggcctcagtcaggaatcaacaactttccacagtaagccaggctcaactgagacaacactgtcccctggcagcatcacaacttcatcttttgctcaagaatttaccacccctcatagccaaccaggctcagctctgtcaacagtgtcacctgccagcaccacagtgccaggccttagtgaggaatctaccaccttctacagcagcccaggctcaactgaaaccacagcgttttctcacagcaacacaatgtccattcatagtcaacaatctacacccttccctgacagcccaggcttcactcacacagtgttacctgccaccctcacaaccacagacattggtcaggaatcaacagccttccacagcagctcagacgcaactggaacaacacccttacctgcccgctccacagcctcagaccttgttggagaacctacaactttctacatcagcccatcccctacttacacaacactctttcctgcgagttccagcacatcaggcctcactgaggaatctaccaccttccacaccagtccaagcttcacttctacaattgtgtctactgaaagcctggaaaccttagcaccagggttgtgccaggaaggacaaatttggaatggaaaacaatgcgtctgtccccaaggctacgttggttaccagtgcttgtcccctctggaatccttccctgtagaaaccccggaaaaactcaacgccactttaggtatgacagtgaaagtgacttacagaaatttcacagaaaagatgaatgacgcatcctcccaggaataccagaacttcagtaccctcttcaagaatcggatggatgtcgttttgaagggcgacaatcttcctcagtatagaggggtgaacattcggagattgctcaacggtagcatcgtggtcaagaacgatgtcatcctggaggcagactacactttagagtatgaggaactgtttgaaaacctggcagagattgtaaaggccaagattatgaatgaaactagaacaactcttcttgatcctgattcctgcagaaaggccatactgtgctatagtgaagaggacactttcgtggattcatcggtgactccgggctttgacttccaggagcaatgcacccagaaggctgccgaaggatatacccagttctactatgtggatgtcttggatgggaagctggcctgtgtgaacaagtgcaccaaaggaacgaagtcgcaaatgaactgtaacctgggcacatgtcagctgcaacgcagtggcccccgctgcctgtgcccaaatacgaacacacactggtactggggagagacctgtgaattcaacatcgccaagagcctcgtgtatgggatcgtgggggctgtgatggcggtgctgctgctcgcattgatcatcctaatcatcttattcagcctatcccagagaaaacggcacagggaacagtatgatgtgcctcaagagtggcgaaaggaaggcacccctggcatcttccagaagacggccatctgggaagaccagaatctgagggagagcagattcggccttgagaacgcctacaacaacttccggcccaccctggagactgttgactctggcacagagctccacatccagaggccggagatggtagcatccactgtgtgagccaacgggggcctcccaccctcatctagctctgttcaggagagctgcaaacacagagcccaccacaagcctccggggcgggtcaagaggagaccgaagtcaggccctgaagccggtcctgctctgagctgacagacttggccagtcccctgcctgtgctcctgctggggaaggctgggggctgtaagcctctccatccgggagcttccagactcccagaagcctcggcacccctgtctcctcctgggtggctccccactctggaatttccctaccaataaaagcaaatctgaaagctcagaatgagaagtga"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("MUC12()");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_001164462.1

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 7, 100637286, PositionType.ZERO_BASED), "",
				"GTA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003uxo.3:exon2:c.3442_3443insGTA:p.Ser1147dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * annovar: OLFM1:uc010naq.2:exon2:c.328_329insAA:p.G110fs, chr9:137968919->AA
	 */
	@Test
	public void testRealWorldCase_uc010naq_2second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010naq.2	chr9	+	137967088	137969237	137967582	137969065	2	137967088,137968657,	137967648,137969237,	Q6IMJ6	uc010naq.2");
		this.transcriptForward
				.setSequence("atcctgtcccagcctgcttccccgccggccgccgccctcctccccgggagagagcgaggcgcgcgggtccctctgcgccacccccgcccccgccccttccgagcaaacttttggcacccaccgcagcccagcgcgcgttcgtgctccgcagggcgcgcctctctccgccaatgccaggcgcgcgggggagccattaggaggcgaggagagaggagggcgcagctcccgcccagcccagccctgcccagccctgcccggaggcagacgcgccggaaccgggacgcgataaatatgcagagcggaggcttcgcgcagcagagcccgcgcgccgcccgctccgggtgctgaatccaggcgtggggacacgagccaggcgccgccgccggagccagcggagccggggccagagccggagcgcgtccgcgtccacgcagccgccggccggccagcacccagggccctgcatgccaggtcgttggaggtggcagcgagacatgcacccggcccggaagctcctcagcctcctcttcctcatcctgatgggcactgaactcactcaaaataaaagagaaaacaaagcagagaagatgggagggccagagagcgagaggaagaccacaggagagaagacactgaacgagcttcccttgttttgcctggaagcccacgctggctccctggctctgcccaggatgtgcagtccaaatcccaatccagcagtggggttatgtcgtcccgcttaccctcagagcccttctcctggtgctgcccagacgatcagccagtccctcctggagaggttctgcatggcctctaggagaggttttcttggccccaggaaggcctggtggagggtggtggttgtgcactgttgctggacagatgcattcattcatgtgcacacacacacacacacatgcacacacaggggagcagatacctgcagagaagagccaaccaggtcctgattagtggcaagctgccccacaaagggctatgcctgtgtcttattgagacaccttggcaaagagatggctgattctgggtggtcctggacatggccgcacccaagggccctccaagccttaatggcaccctgaagcctccatgcccaggccaaaagatgcttttcctccctaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("OLFM1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 9, 137968919, PositionType.ZERO_BASED), "",
				"AA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010naq.2:exon2:c.328_329insAA:p.Gly110Glufs*51", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * Mutalyzer: NM_033419.3:n.328dup NM_033419.3(PGAP3_v001):c.285dup NM_033419.3(PGAP3_i001):p.(Phe96Leufs*67)
	 */
	@Test
	public void testRealWorldCase_uc002hsk_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002hsk.3	chr17	-	37827374	37844310	37829055	37844267	7	37827374,37829303,37829766,37830245,37830869,37842174,37844086,	37829119,37829508,37829903,37830307,37830932,37842272,37844310,	Q96FM1-2	uc002hsk.3");
		this.transcriptForward
				.setSequence("atactcctaagctcctcccccggcggcgagccagggagaaaggatggccggcctggcggcgcggttggtcctgctagctggggcagcggcgctggcgagcggctcccagggcgaccgtgagccggtgtaccgcgactgcgtactgcagtgcgaagagcagaactgctctgggggcgctctgaatcacttccgctcccgccagccaatctacatgagtctagcaggctggacctgtcgggacgactgtaagtatgagtgtatgtgggtcaccgttgggctctacctccaggaaggtcacaaagtgcctcagttccatggcaaggtgtccctcaatgcatggttctggtccacagttttccacaccagggacactgacctcacagagaaaatggactacttctgtgcctccactgtcatcctacactcaatctacctgtgctgcgtcaggaccgtggggctgcagcacccagctgtggtcagtgccttccgggctctcctgctgctcatgctgaccgtgcacgtctcctacctgagcctcatccgcttcgactatggctacaacctggtggccaacgtggctattggcctggtcaacgtggtgtggtggctggcctggtgcctgtggaaccagcggcggctgcctcacgtgcgcaagtgcgtggtggtggtcttgctgctgcaggggctgtccctgctcgagctgcttgacttcccaccgctcttctgggtcctggatgcccatgccatctggcacatcagcaccatccctgtccacgtcctctttttcagctttctggaagatgacagcctgtacctgctgaaggaatcagaggacaagttcaagctggactgaagaccttggagcgagtctgccccagtggggatcctgcccccgccctgctggcctcccttctcccctcaacccttgagatgattttctcttttcaacttcttgaacttggacatgaaggatgtgggcccagaatcatgtggccagcccaccccctgttggccctcaccagccttggagtctgttctagggaaggcctcccagcatctgggactcgagagtgggcagcccctctacctcctggagctgaactggggtggaactgagtgtgctcttagctctaccgggaggacagctgcctgtttcctccccatcagcctcctccccacatccccagctgcctggctgggtcctgaagccctctgtctacctgggagaccagggaccacaggccttagggatacagggggtccccttctgttaccaccccccaccctcctccaggacaccactaggtggtgctggatgcttgttctttggccagccaaggttcacggcgattctccccatgggatcttgagggaccaagctgctgggattgggaaggagtttcaccctgaccattgccctagccaggttcccaggaggcctcaccatactccctttcagggccagggctccagcaagcccagggcaaggatcctgtgctgctgtctggttgagagcctgccaccgtgtgtcgggagtgtgggccaggctgagtgcataggtgacagggccgtgagcatgggcctgggtgtgtgtgagctcaggcctaggtgcgcagtgtggagacgggtgttgtcggggaagaggtgtggcttcaaagtgtgtgtgtgcagggggtgggtgtgttagcgtgggttaggggaacgtgtgtgcgcgtgctggtgggcatgtgagatgagtgactgccggtgaatgtgtccacagttgagaggttggagcaggatgagggaatcctgtcaccatcaataatcacttgtggagcgccagctctgcccaaggcgccacctgggcggacagccaggagctctccatggccaggctgcctgtgtgcatgttccctgtctggtgcccctttgcccgcctcctgcaaacctcacagggtccccacacaacagtgccctccagaagcagcccctcggaggcagaggaaggaaaatggggatggctggggctctctccatcctccttttctccttgccttcgcatggctggccttcccctccaaaacctccattcccctgctgccagcccctttgccatagcctgattttggggaggaggaaggggcgatttgagggagaaggggagaaagcttatggctgggtctggtttcttcccttcccagagggtcttactgttccagggtggccccagggcaggcaggggccacactatgcctgcgccctggtaaaggtgacccctgccatttaccagcagccctggcatgttcctgccccacaggaatagaatggagggagctccagaaactttccatcccaaaggcagtctccgtggttgaagcagactggatttttgctctgcccctgaccccttgtccctctttgagggaggggagctatgctaggactccaacctcagggactcgggtggcctgcgctagcttcttttgatactgaaaacttttaaggtgggagggtggcaagggatgtgcttaataaatcaattccaagcctca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("PGAP3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_033419.4

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 17, 37830926, PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002hsk.3:exon3:c.286dup:p.Leu96Profs*16", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation1.getVariantType());
	}

	/**
	 * This duplication variation should lead to the loss of the translation initiation site
	 *
	 * uc003izs.3 is NM_004477 mutalyzer: NM_004477(FRG1_v001):c.1_2insC NM_004477(FRG1_i001):p.? Note that mutalyzer
	 * says it does not know what happens to the protein but this can be classified as startloss since the start codon
	 * is destroyed.
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_third() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.transcriptForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FRG1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_004477.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 4, 190862165, PositionType.ZERO_BASED), "", "C");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003izs.3:exon1:c.1_2insC:p.0?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());
	}

	/**
	 * This duplication variation should lead to the loss of the translation initiation site
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_fourth() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.transcriptForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FRG1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_004477.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 4, 190862166, PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003izs.3:exon1:c.2_3insA:p.0?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());
	}

	//
	// Various UTR 3' Variants Below
	//

	/**
	 * annovar: THAP3 chr1:6693165->TA
	 */
	@Test
	public void testRealWorldCase_uc001aod_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001aod.3	chr1	+	6685209	6693642	6685278	6693137	5	6685209,6688558,6690350,6692450,6692855,	6685352,6688751,6690413,6692555,6693642,	Q8WTV1-3	uc001aod.3");
		this.transcriptForward
				.setSequence("gtccctcccctctccgcaggccccgccgccgccgccatctttgttgggggcagccaggcctggctcgagatgccgaagtcgtgcgcggcccggcagtgctgcaaccgctacagcagccgcaggaagcagctcaccttccaccggtttccgttcagccgcccggagctgctgaaggaatgggtgctgaacatcggccggggcaacttcaagcccaagcagcacacggtcatctgctccgagcacttccggccagagtgcttcagcgcctttggaaaccgcaagaacctaaagcacaatgccgtgcccacggtgttcgcctttcaggaccccacacaggtgagggagaacacagaccctgccagtgagagaggaaatgccagctcttctcagaaagaaaaggtcctccctgaggcgggggccggagaggacagtcctgggagaaacatggacactgcacttgaagagcttcagttgcccccaaatgccgaaggccacgtaaaacaggtctcgccacggaggccgcaagcaacagaggctgttggccggccgactggccctgcaggcctgagaaggacccccaacaagcagccatctgatcacagctatgcccttttggacttagattccctgaagaaaaaactcttcctcactctgaaggaaaatgaaaagctccggaagcgcttgcaggcccagaggctggtgatgcgaaggatgtccagccgcctccgtgcttgcaaagggcaccagggactccaggccagacttgggccagagcagcagagctgagccccacaggctccggacgcagaggtggcagtggcaccagggccggcagagctttggagctctggctgtggacatttttgtctgctgtggacactgagaaagttggccatgaggcctgcttggccggggatcgagacagtagccaagctccccggcgagagccccaatgccgtctgggggacgtttagaggcgtggcactaggagtgcacatctgtgagcatgacaagcttatcctcccatggtaacagaagtccaggctgaggctgattctggacgctgtcctttcagcacacgcagagcaaagatcgttggaagccccagtgtgggagatgctcctcagggaggaagccatgtgagggggctggctctgtggcgggtgagtggtcccctcctccatcagcctggacagccgctcggggttctaaggagtgactcctgtcccggcctggtgtgagtgggcagtgtaataaagtgtctttctatacggtgtcgctcccatcatcaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("THAP3");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6693165, PositionType.ZERO_BASED), "", "TA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001aod.3:exon5:c.*28_*29insTA", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, annotation1.getVariantType());
	}

	/**
	 * annovar: RGS21 chr1:192335275->TAAT
	 */
	@Test
	public void testRealWorldCase_uc001gsh_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc001gsh.3	chr1	+	192286121	192336414	192312166	192335254	5	192286121,192312106,192316442,192321176,192335050,	192286235,192312177,192316519,192321343,192336414,	Q2M5E4	uc001gsh.3");
		this.transcriptForward
				.setSequence("gttaccacttggaaaacaattcatctgaaagaagcacagattttctcatctatcctgtcaacaaagaaggaatcaagagagcaaggacagtgatttcccctgcattgcatttgtcttgaagatcagtcagaaagagaaactcggcatcatctgtgacagacagtggaacgaaaaatgccagtgaaatgctgtttctacaggtcaccaactgcggaaacaatgacatggtctgaaaatatggacacgcttttagccaaccaagctggtctagatgcttttcgaatatttctaaaatcagagtttagtgaagaaaatgttgagttctggcttgcctgtgaagactttaagaaaacgaaaaatgcagacaaaattgcttccaaagccaagatgatttattctgaattcattgaagctgatgcacctaaagagattaacattgacttcggtaccagagacctcatctcaaagaatattgctgaaccaacactcaaatgctttgatgaggctcagaaattaatctattgtctcatggccaaggattctttccctcgatttctgaagtcagagatttataaaaaactggtaaatagccaacaggttccaaatcataaaaaatggctcccttttttgtgaggaaggtaaaagttaactaatcactatacttcagggctacaatattttaaatatacaagcatgatgcattgtcttttgttttgtttttaggatttagaaaacattttttacccaaacagatgaataacgttttatacaacaagcctgaatttctaactcagttgtttagaatgtatttgctttaccagctatttaatctcctactgggggagtacaaagaaagtttatagagatacaatatagtcttaaaccaaaactgaatattcttattatattataatgtaaggaattatacatatcttcacgtggcagaatgaaagacttttgagcatcatatacacaattttaaataccattgctttattcaaaaaaatctcacttttgtaaaaagagaatttctgaaccaaaatacaagctttcatttaatatatttaactgttttttttctgccatttctttccaactatttctaataatgtggttatgaaaactgctacgcctctcaaattatattttttaaatcacaggaatgtatacacatttatatgtatgtcttgaatgcaccatggaccaaagtttttcaaaatatatcacttggctcaattcaatggcatcacatataaaatgtgatgagttatgtatgaaaaggcctcaagggtggggaatactgattttcttatgttaacagaaatataaaagaaagtggaagactaaggagcatagataaatccttataagatgaagtatatagcaagtcataaaatttaagaatttgcaacattatctactcaattgtggggaagtatctattcactccttcagcactgatacttgtttataaaacccaaacaatttttaaatgcatttattttgagatgttcctaaaattgtttcattctatatgtaaatatcctgtgataaatacgaataatttcatttcaatatgagaagctgtaaagattcaacagatctcccacgtttccattttctttgcacagatttatttatctgcattgatatttctgcttttagattgtttgaacattaaaaaatggaggaaaaatagcatggcttattttatgttttcacaaactactcatttgatagacaaaattttgtcttcccttcatcatgagaaataaacatttaaacatattcaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("RGS21");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 192335275, PositionType.ZERO_BASED), "",
				"TAAT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001gsh.3:exon5:c.*18_*21dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, annotation1.getVariantType());
	}

	/**
	 * <P>
	 * annovar: FRG1 chr4:190884289->GACA
	 * </P>
	 */
	@Test
	public void testRealWorldCase_uc003izs_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.transcriptForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FRG1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 4, 190884289, PositionType.ZERO_BASED), "",
				"GACA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003izs.3:exon9:c.*5_*6insGACA", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, annotation1.getVariantType());
	}

}
