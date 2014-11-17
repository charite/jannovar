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

// TODO(holtgrem): Mutalyzer uses parantheses () to indicate non-validated prediction.
// TODO(holtgrem): What exactly should be counted as stop gain?

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
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640061, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.-205_-204insA", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UPSTREAM, anno.getVariantType());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649340, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.*68_*69insA", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.DOWNSTREAM, anno.getVariantType());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeChange {
		// upstream intergenic
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6639061, PositionType.ZERO_BASED), "", "A");
		Annotation anno = InsertionAnnotationBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.-1205_-1204insA", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, anno.getVariantType());

		// downstream intergenic
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6650340, PositionType.ZERO_BASED), "", "A");
		Annotation anno2 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:c.*1068_*1069insA", anno2.getVariantAnnotation());
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
		Assert.assertEquals("uc001anx.3:exon2:c.-1_1insC", anno.getVariantAnnotation());
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
		Assert.assertEquals(VariantType.NON_FS_INSERTION, annotation1tgc.getVariantType());

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
		Assert.assertEquals("uc001anx.3:exon2:c.3_4insTCCTCCTCC:p.Asp2_Gly3insSerSerSer",
				annotation4tcctcctcc.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_INSERTION, annotation4tcctcctcc.getVariantType());

		// Insertion without a new stop codon that is a duplication.
		GenomeChange change5gatggc = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "",
				"GATGGC");
		Annotation annotation5gatggc = InsertionAnnotationBuilder.buildAnnotation(infoForward, change5gatggc);
		Assert.assertEquals("uc001anx.3:exon2:c.5_6insTGGCGA:p.Asp2_Gly3dup",
				annotation5gatggc.getVariantAnnotation());
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

		GenomeChange change1c = new GenomeChange(new GenomePosition('+', 1, 23694496, PositionType.ZERO_BASED), "", "C");
		Annotation annotation1c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change1c);
		Assert.assertEquals("uc001bgu.3:exon2:c.1_2insG:p.0?", annotation1c.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1c.getVariantType());

		GenomeChange change1g = new GenomeChange(new GenomePosition('+', 1, 23694496, PositionType.ZERO_BASED), "", "G");
		Annotation annotation1g = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change1g);
		Assert.assertEquals("uc001bgu.3:exon2:c.1_2insC:p.0?", annotation1g.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1g.getVariantType());

		// Insert A and C between nucleotides 2 and 3.

		GenomeChange change2a = new GenomeChange(new GenomePosition('+', 1, 23694495, PositionType.ZERO_BASED), "", "T");
		Annotation annotation2a = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change2a);
		Assert.assertEquals("uc001bgu.3:exon2:c.2_3insA:p.0?", annotation2a.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2a.getVariantType());

		GenomeChange change2c = new GenomeChange(new GenomePosition('+', 1, 23694495, PositionType.ZERO_BASED), "", "G");
		Annotation annotation2c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change2c);
		Assert.assertEquals("uc001bgu.3:exon2:c.2_3insC:p.0?", annotation2c.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2c.getVariantType());

		// Insertions between nucleotides 3 and 4.

		GenomeChange change3a = new GenomeChange(new GenomePosition('+', 1, 23694494, PositionType.ZERO_BASED), "", "T");
		Annotation annotation3a = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change3a);
		Assert.assertEquals("uc001bgu.3:exon2:c.3_4insA:p.Ala2Serfs*16", annotation3a.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3a.getVariantType());

		GenomeChange change3c = new GenomeChange(new GenomePosition('+', 1, 23694494, PositionType.ZERO_BASED), "", "G");
		Annotation annotation3c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change3c);
		Assert.assertEquals("uc001bgu.3:exon2:c.3_4insC:p.Ala2Argfs*16", annotation3c.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation3c.getVariantType());

		// Some insertions into stop codon

		GenomeChange change4g = new GenomeChange(new GenomePosition('+', 1, 23688462, PositionType.ZERO_BASED), "", "G");
		Annotation annotation4g = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4g);
		Assert.assertEquals("uc001bgu.3:exon4:c.1411_1412insC:p.*471Serext*7", annotation4g.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4g.getVariantType());

		GenomeChange change4c = new GenomeChange(new GenomePosition('+', 1, 23688462, PositionType.ZERO_BASED), "", "C");
		Annotation annotation4c = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4c);
		Assert.assertEquals("uc001bgu.3:exon4:c.1411_1412insG:p.=", annotation4c.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, annotation4c.getVariantType());
	}

	@Test
	public void testReverseOnFourBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some four-nucleotide insertions in the first ten bases and in the stop codon and compared them by
		// hand to Mutalyzer results.

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeChange change4actagact = new GenomeChange(new GenomePosition('+', 1, 23694493, PositionType.ZERO_BASED),
				"", "ACTAGACT");
		Annotation annotation4actagact = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4actagact);
		Assert.assertEquals("uc001bgu.3:exon2:c.4_5insAGTCTAGT:p.Ala2Glufs*16",
				annotation4actagact.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4actagact.getVariantType());

		// This insertion will be shifted.
		GenomeChange change4cgtg = new GenomeChange(new GenomePosition('+', 1, 23694493, PositionType.ZERO_BASED), "",
				"CGTG");
		Annotation annotation4cgtg = InsertionAnnotationBuilder.buildAnnotation(infoReverse, change4cgtg);
		Assert.assertEquals("uc001bgu.3:exon2:c.6_7insCGCA:p.Ala3Argfs*16", annotation4cgtg.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_INSERTION, annotation4cgtg.getVariantType());

		// Insert whole stop codon.
		GenomeChange change5cgtg = new GenomeChange(new GenomePosition('+', 1, 23694491, PositionType.ZERO_BASED), "",
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 12, 49218811, PositionType.ZERO_BASED), "", "T");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010slx.2:exon5:c.*255_*256insT", annotation1.getVariantAnnotation());
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
		Assert.assertEquals("uc010vsd.2:c.-37_-36insCTT", annotation1.getVariantAnnotation());
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 9, 97329738, PositionType.ZERO_BASED), "", "GA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc004aus.1:exon3:n.494_495insGA", annotation1.getVariantAnnotation());
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

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 13, 73079293, PositionType.ZERO_BASED), "",
				"AA");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010arh.1:exon1:n.512_513insTT", annotation1.getVariantAnnotation());
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
		Assert.assertEquals("uc001iel.1:exon1:c.769_771dup:p.F257dup", annotation1.getVariantAnnotation());
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
	public void testRealWorldCase_uc003izs_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.transcriptForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("FRG1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 4, 190878559, PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003izs.3:exon6:c.439dup:p.Met147fs", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_DUPLICATION, annotation1.getVariantType());
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 */
	@Test
	public void testRealWorldCase_uc010naq_2() throws InvalidGenomeChange {
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
		Assert.assertEquals(VariantType.UTR5, annotation1.getVariantType());
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
		Assert.assertEquals("uc001iel.1:exon1:c.766_771dup:p.L256_F257dup", annotation1.getVariantAnnotation());
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 248637422, PositionType.ZERO_BASED), "",
				"CTGCTGCTCTTC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001iel.1:exon1:c.760_771dup:p.L254_F257dup", annotation1.getVariantAnnotation());
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429186, PositionType.ZERO_BASED), "",
				"AGT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.424_426dup:p.T142dup", annotation1.getVariantAnnotation());
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429171, PositionType.ZERO_BASED), "",
				"TTTGTT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.439_444dup:p.N147_K148du", annotation1.getVariantAnnotation());
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429171, PositionType.ZERO_BASED), "",
				"TTTTAGTTTGTT");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.439_450dup:p.N147_K150dup", annotation1.getVariantAnnotation());
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
		Assert.assertEquals("uc001iel.1:exon1:c.949_954dup:p.*319ext*2", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
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
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 3, 184429154, PositionType.ZERO_BASED), "",
				"TCC");
		Annotation annotation1 = InsertionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc003fpa.3:exon1:c.474_476dup:p.E158dup", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, annotation1.getVariantType());
	}

}
