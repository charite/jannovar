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

// TODO(holtgrem): Extend tests to also use reverse transcript?

public class BlockSubstitutionAnnotationBuilderTest {

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
	public void testForwardUstream() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640059, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.-206_-204delinsCGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UPSTREAM, annotation1.getVariantType());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6649340, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.*69_*71delinsCGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.DOWNSTREAM, annotation1.getVariantType());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeChange {
		// intergenic upstream
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6639059, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.-1206_-1204delinsCGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, annotation1.getVariantType());
		// intergenic downstream
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6650340, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:c.*1069_*1071delinsCGTT", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, annotation2.getVariantType());
	}

	@Test
	public void testForwardTranscriptAblation() throws InvalidGenomeChange {
		StringBuilder chars200 = new StringBuilder();
		for (int i = 0; i < 200; ++i)
			chars200.append("A");
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640061, PositionType.ZERO_BASED),
				chars200.toString(), "CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.-204_-70+65delinsCGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.TRANSCRIPT_ABLATION, annotation1.getVariantType());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6642106, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.691-11_691-9delinsCGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTRONIC, annotation1.getVariantType());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640070, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon1:c.-195_-193delinsCGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR5, annotation1.getVariantType());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6649329, PositionType.ZERO_BASED), "ACG",
				"CGGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon11:c.*58_*60delinsCGGTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, annotation1.getVariantType());
	}

	@Test
	public void testForwardStartLoss() throws InvalidGenomeChange {
		// Testing with some START_LOSS scenarios.

		// Delete one base of start codon.
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640669, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon2:c.1_3delinsCGTT:p.0?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation1.getVariantType());

		// Delete chunk out of first exon, spanning start codon from the left.
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640660, PositionType.ZERO_BASED),
				"CCCTCCAGACC", "GTTG");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon2:c.-9_2delinsGTTG:p.0?", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation2.getVariantType());

		// Delete chunk out of first exon, spanning start codon from the right.
		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED),
				"GGACGGCTCCT", "CTTG");
		Annotation annotation3 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change3);
		Assert.assertEquals("uc001anx.3:exon2:c.3_13delinsCTTG:p.0?", annotation3.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation3.getVariantType());

		// Deletion from before transcript, reaching into the start codon.
		GenomeChange change4 = new GenomeChange(
				new GenomePosition('+', 1, 6640399, PositionType.ZERO_BASED),
				"TCTCACCAGGCCCTTCTTCACGACCCTGGCCCCCCATCCAGCATCCCCCCTGGCCAATCCAATATGGCCCCCGGCCCCCGGGAGGCTGTCAGTGTGTTCCAGCCCTCCGCGTGCACCCCTCACCCTGACCCAAGCCCTCGTGCTGATAAATATGATTATTTGAGTAGAGGCCAACTTCCCGTTTCTCTCTCTTGACTCCAGGAGCTTTCTCTTGCATACCCTCGCTTAGGCTGGCCGGGGTGTCACTTCTGCCTCCCTGCCCTCCAGACCA",
				"ACCT");
		Annotation annotation4 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change4);
		Assert.assertEquals("uc001anx.3:c.-69-201_1delinsACCT:p.0?", annotation4.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, annotation4.getVariantType());
	}

	@Test
	public void testForwardStopLoss() throws InvalidGenomeChange {
		// Delete last base of stop codon, leads to complete loss.
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon11:c.2067del:p.0?", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation1.getVariantType());

		// Delete middle base of stop codon, leads to complete loss.
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6649270, PositionType.ZERO_BASED), "ACG",
				"CGGT");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon11:c.2066del:p.0?", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation2.getVariantType());

		// Delete first base of stop codon, leads to complete loss.
		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6649269, PositionType.ZERO_BASED), "ACG",
				"CGGT");
		Annotation annotation3 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change3);
		Assert.assertEquals("uc001anx.3:exon11:c.2065del:p.0?", annotation3.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation3.getVariantType());
	}

	@Test
	public void testForwardSplicing() throws InvalidGenomeChange {
		// intronic splicing
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6642116, PositionType.ZERO_BASED), "G", "TT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.691-1delinsTT", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, annotation1.getVariantType());

		// exonic splicing
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6642117, PositionType.ZERO_BASED), "TGG",
				"AA");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon3:c.691_693del:p.Trp231del", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, annotation2.getVariantType());
	}

	@Test
	public void testForwardFrameShiftBlockSubstitution() throws InvalidGenomeChange {
		// The following case contains a shift in the nucleotide sequence.
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6645978, PositionType.ZERO_BASED),
				"GAAACATACT", "TAA");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon4:c.934_943del:p.Lys312Glyfs*29", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_DELETION, annotation1.getVariantType());
	}

	@Test
	public void testForwardNonFrameBlockSubstitution() throws InvalidGenomeChange {
		// deletion of two codons, insertion of one
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6642114, PositionType.ZERO_BASED), "GAAACA",
				"GTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.691-3_693del:p.Trp231del", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, annotation1.getVariantType());

		// deletion of three codons, insertion of one
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6642126, PositionType.ZERO_BASED),
				"GTGGTTCAA", "ACC");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon3:c.704_712del:p.Val235_Val237del", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DELETION, annotation2.getVariantType());

		// deletion of three codons, insertion of one
		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6642134, PositionType.ZERO_BASED),
				"AGTGGAGGA", "CTT");
		Annotation annotation3 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change3);
		Assert.assertEquals("uc001anx.3:exon3:c.708_716del:p.Gln236_Asp239delinsHis",
				annotation3.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_DELETION, annotation3.getVariantType());
	}
}
