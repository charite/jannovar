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
		// Replace bases of stop codon by 4 nucleotides, frameshift case.
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "ACG",
				"CGTT");
		// Note that the transcript here differs to the one Mutalyzer uses after the CDS.
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon11:c.2067_*2delinsCGTT:p.*689Tyrext*25", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation1.getVariantType());

		// Replace stop codon by 6 nucleotides, non-frameshift case.
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6649270, PositionType.ZERO_BASED), "ACT",
				"CGGTCG");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		// Note that the transcript here differs to the one Mutalyzer uses after the CDS.
		Assert.assertEquals("uc001anx.3:exon11:c.2066_*1delinsCGGTCG:p.*689Serext*17",
				annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, annotation2.getVariantType());

		// Delete first base of stop codon, leads to complete loss.
		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6649269, PositionType.ZERO_BASED), "ACG",
				"CGGT");
		Annotation annotation3 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change3);
		// Note that the transcript here differs to the one Mutalyzer uses after the CDS.
		Assert.assertEquals("uc001anx.3:exon11:c.2065_2067delinsCGGT:p.*689Argext*16",
				annotation3.getVariantAnnotation());
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
		Assert.assertEquals("uc001anx.3:exon3:c.691_693delinsAA:p.Trp231Lysfs*23", annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, annotation2.getVariantType());
	}

	@Test
	public void testForwardFrameShiftBlockSubstitution() throws InvalidGenomeChange {
		// The following case contains a shift in the nucleotide sequence.
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6647537, PositionType.ZERO_BASED),
				"TGCCCCACCT", "CCC");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon7:c.1225_1234delinsCCC:p.Cys409Profs*127",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, annotation1.getVariantType());
	}

	@Test
	public void testForwardNonFrameBlockSubstitution() throws InvalidGenomeChange {
		// deletion of two codons, insertion of one
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6642114, PositionType.ZERO_BASED), "TAAACA",
				"GTT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.691-3_693delinsGTT:p.Trp231Val", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, annotation1.getVariantType());

		// deletion of three codons, insertion of one
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6642126, PositionType.ZERO_BASED),
				"GTGGTTCAA", "ACC");
		Annotation annotation2 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon3:c.700_708delinsACC:p.Val234_Gln236delinsThr",
				annotation2.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION, annotation2.getVariantType());

		// deletion of three codons, insertion of one, includes truncation of replacement ref from the right
		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6642134, PositionType.ZERO_BASED),
				"AGTGGAGGAT", "CTT");
		Annotation annotation3 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change3);
		Assert.assertEquals("uc001anx.3:exon3:c.708_716delinsCT:p.Gln236Hisfs*16", annotation3.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_SUBSTITUTION, annotation3.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc002djq_3() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002axo.4	chr15	+	74528629	74628482	74529060	74628394	19	74528629,74536325,74554780,74559018,74560682,74564043,74565111,74572303,74573008,74574118,74588094,74622529,74623003,74623321,74623543,74625019,74626221,74627315,74628265,	74529081,74536489,74554914,74559128,74560799,74564135,74565232,74572433,74573142,74574190,74588289,74622695,74623092,74623453,74623637,74625186,74626308,74627429,74628482,	Q8N5R6-6	uc002axo.4");
		this.transcriptForward
				.setSequence("agtgttgcccaggtgacccagctaggtggaagagcttattgttttccaggcctgggcagagacagggcccccctgccccatctctccaccgtcctaggtgtgccaagagtcaattgcctcattgctgaccctgtccagctggccatggccctcaacccccaaggcccttccacccacagaccatctcgctgctgagagatccaggacctgctcccacctggccaccctccccctccccccacatccaggccccagggctggtgtgtggcacccctgagaccacattgacctccatactgtctactacccataaggactccaagacgcccaggccagctgtctgggcaggactgattcctgatcacccactgataccaagtactcatccccaagattgttaaacaaggccagacactcctggcctcaagaggatgggactgaaaaacaaaaagaacactgaagacccagaggagcccctgatcgcctcccagagcacggaacctgagatcggtcacctgtctccctctaagaaggagaccatcatggtcaccctccatggggctaccaacctgcctgcctgcaaggatggctccgagccgtggccctatgtggtggtgaaaagcacatctgaggaaaagaacaatcagagctccaaggcagtcacatctgtgacctcagagcccaccagagcccctatctggggggacacggtgaatgtggagatccaagctgaggatgcagggcaagaagatgtgatcctcaaggtggtggacaacagaaagaaacaggagttgttgtcctacaaaatccccatcaagtacctgcgtgtcttccacccctaccactttgagctggtgaagcccactgagtctgggaaagccgatgaagccactgccaagacccagttgtacgcaacagtcgttcggaagagcagcttcataccccgctacatcggctgcaaccacatggctctggagatctttctccggggagtcaacgagcccctggccaacaaccccaaccccatagtggtgattgcccgggtcgttcccaactacaaggaatttaaggtcagccaggctaacagggacctggcctctgtggggctgcccatcaccccactgtccttccctatcccgtccatgatgaactttgacgtgcctcgcgtcagccagaacggatgccctcagctgtccaagcctgggggacccccagagcagcccctgtggaatcagtccttcctcttccaaggccgagatggagctaccagcttctcagaagacacagccctggtgctggagtactactcctcaacttcaatgaaaggcagccagccgtggaccctcaaccagcccctgggcatctctgtgttgccgctaaagagccgtttgtaccagaagatgctgacagggaaaggcttggacgggcttcacgtggagcggctccccatcatggacaccagcctgaaaactatcaatgatgaggcccccacagtggctctctccttccagctgctttcctctgagagaccagaaaacttcttgacaccaaacaacagcaaggctcttcctaccttggaccccaagatcctggataagaagctgagaaccatccaagagtcctggtccaaggacacagtgagctccacaatggacttgagcacgtccactccacgagaagcagaggaggaacctctggtgcctgagatgtcccatgacacagagatgaacaactaccggcgggccatgcagaagatggcagaggacatcctgtctctgcggagacaggccagcatcctggaaggagagaaccgcatactgaggagccgcctggcccagcaggaggaggaagaggggcagggcaaagccagtgaggcccagaacacggtgtccatgaagcagaaactgctgctgagtgagctggatatgaagaaactgagggacagggtgcagcatttgcagaatgagctgattcgaaagaatgatcgagagaaggagctgctccttctgtatcaggcccagcagccacaggccgctctgctgaagcagtaccagggcaagctgcagaagatgaaggcgctggaggagactgtgcggcaccaagagaaggtgatcgagaagatggagcgggtgctggaggacaggctgcaggacaggagcaagccccctcctctgaacaggcagcagggaaagccctacacgggcttccctatgctctcagcctctggccttcccttgggttctatgggagagaacctgccggttgaactttactcggtgctgctggcagaaaacgcgaagctgcggacggagctggataagaaccgccaccagcaggcccccatcattctgcagcaacaggccctgccggatctcctctctggtacttcagacaagttcaacctcctggccaagctggaacacgctcagagccggatcctgtccctggaaagccagttagaggactcagctcgacgctggggacgagagaagcaggatctggccacacggctgcaggagcaagaaaaaggtttcaggcacccctcgaactccatcatcatagaacagcctagtgccctcacccactccatggacctcaagcagccctcagagctggagcccctgctgcccagctcagactctaagctcaacaagcccttgagcccccagaaggagaccgctaactctcagcagacctgagccccagagcaggcctccttccctgtgtgctggggagtctcatcaccgccccctaaaaatgacgttattaaatgttgtagctctgtgaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("CCDC33");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_025055.4

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 15, 74536399, PositionType.ZERO_BASED),
				"TAAGAAGGAGACCATCA", "ACTACCAGAGGAAT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002axo.4:exon2:c.96_112delinsACTACCAGAGGAAT:p.Lys33_Met38delinsLeuProGluGluLeu",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc010qzf_2() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc010qzf.2	chr11	+	5474637	5475707	5474718	5475657	1	5474637,	5475707,	Q9H344	uc010qzf.2");
		this.transcriptForward
				.setSequence("ttgtcctccagcaagtgcaactgttagaattctccaagtcagaagatctgactctgaaaagtaccctaagtttgttttgctatggggttgttcaatgtcactcaccctgcattcttcctcctgactggtatccctggtctggagagctctcactcctggctgtcagggcccctctgcgtgatgtatgctgtggcccttgggggaaatacagtgatcctgcaggctgtgcgagtggagcccagcctccatgagcccatgtactacttcctgtccatgttgtccttcagtgatgtggccatatccatggccacactgcccactgtactccgaaccttctgcctcaatgcccgcaacatcacttttgatgcctgtctaattcagatgtttcttattcacttcttctccatgatggaatcaggtattctgctggccatgagttttgaccgctatgtggccatttgtgaccccttgcgctatgcaactgtgctcaccactgaagtcattgctgcaatgggtttaggtgcagctgctcgaagcttcatcacccttttccctcttccctttcttattaagaggctgcctatctgcagatccaatgttctttctcactcctactgcctgcacccagacatgatgaggcttgcctgtgctgatatcagtatcaacagcatctatggactctttgttcttgtatccacctttggcatggacctgttttttatcttcctctcctatgtgctcattctgcgttctgtcatggccactgcttcccgtgaggaacgcctcaaagctctcaacacatgtgtgtcacatatcctggctgtacttgcattttatgtgccaatgattggggtctccacagtgcaccgctttgggaagcatgtcccatgctacatacatgtcctcatgtcaaatgtgtacctatttgtgcctcctgtgctcaaccctctcatttatagcgccaagacaaaggaaatccgccgagccattttccgcatgtttcaccacatcaaaatatgactttcacacttggctttagaatctgttattttggccataggctctcatca"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("LOC100132247");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_001004754.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 11, 5475430, PositionType.ZERO_BASED),
				"TCAACA", "ACAACACT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc010qzf.2:exon1:c.713_718delinsACAACACT:p.Leu238Hisfs*19",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.FS_SUBSTITUTION, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc011ddm_2_first() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc011ddm.2	chr5	-	156479371	156485970	156479372	156484954	4	156479371,156482211,156484908,156485931,	156479665,156482544,156485087,156485970,	E9PFX0	uc011ddm.2");
		this.transcriptForward
				.setSequence("gttacccagcattgtgagtgacagagcctggatctgaacagcaggctcatatgaatcaaccaactgggtgaaaagataagttgcaatctgagatttaagacttgatcagataccatctggtggagggtaccaaccagcctgtctgctcattttccttcaggctgatcccataatgcatcctcaagtggtcatcttaagcctcatcctacatctggcagattctgtagctggttctgtaaaggttggtggagaggcaggtccatctgtcacactaccctgccactacagtggagctgtcacatccatgtgctggaatagaggctcatgttctctattcacatgccaaaatggcattgtctggaccaatggaacccacgtcacctatcggaaggacacacgctataagctattgggggacctttcaagaagggatgtctctttgaccatagaaaatacagctgtgtctgacagtggcgtatattgttgccgtgttgagcaccgtgggtggttcaatgacatgaaaatcaccgtatcattggagattgtgccacccaaggtcacgactactccaattgtcacaactgttccaaccgtcacgactgttcgaacgagcaccactgttccaacgacaacgactgttccaatgacgactgttccaacgacaactgttccaacaacaatgagcattccaacgacaacgactgttctgacgacaatgactgtttcaacgacaacgagcgttccaacgacaacgagcattccaacaacaacaagtgttccagtgacaacaactgtctctacctttgttcctccaatgcctttgcccaggcagaaccatgaaccag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("HAVCR1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_012206.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 5, 156479564, PositionType.ZERO_BASED),
				"AGTCGT", "AGTGAG");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc011ddm.2:exon4:c.475_477delinsCTC:p.Thr159Leu",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc002axo_4() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc002axo.4	chr15	+	74528629	74628482	74529060	74628394	19	74528629,74536325,74554780,74559018,74560682,74564043,74565111,74572303,74573008,74574118,74588094,74622529,74623003,74623321,74623543,74625019,74626221,74627315,74628265,	74529081,74536489,74554914,74559128,74560799,74564135,74565232,74572433,74573142,74574190,74588289,74622695,74623092,74623453,74623637,74625186,74626308,74627429,74628482,	Q8N5R6-6	uc002axo.4");
		this.transcriptForward
				.setSequence("agtgttgcccaggtgacccagctaggtggaagagcttattgttttccaggcctgggcagagacagggcccccctgccccatctctccaccgtcctaggtgtgccaagagtcaattgcctcattgctgaccctgtccagctggccatggccctcaacccccaaggcccttccacccacagaccatctcgctgctgagagatccaggacctgctcccacctggccaccctccccctccccccacatccaggccccagggctggtgtgtggcacccctgagaccacattgacctccatactgtctactacccataaggactccaagacgcccaggccagctgtctgggcaggactgattcctgatcacccactgataccaagtactcatccccaagattgttaaacaaggccagacactcctggcctcaagaggatgggactgaaaaacaaaaagaacactgaagacccagaggagcccctgatcgcctcccagagcacggaacctgagatcggtcacctgtctccctctaagaaggagaccatcatggtcaccctccatggggctaccaacctgcctgcctgcaaggatggctccgagccgtggccctatgtggtggtgaaaagcacatctgaggaaaagaacaatcagagctccaaggcagtcacatctgtgacctcagagcccaccagagcccctatctggggggacacggtgaatgtggagatccaagctgaggatgcagggcaagaagatgtgatcctcaaggtggtggacaacagaaagaaacaggagttgttgtcctacaaaatccccatcaagtacctgcgtgtcttccacccctaccactttgagctggtgaagcccactgagtctgggaaagccgatgaagccactgccaagacccagttgtacgcaacagtcgttcggaagagcagcttcataccccgctacatcggctgcaaccacatggctctggagatctttctccggggagtcaacgagcccctggccaacaaccccaaccccatagtggtgattgcccgggtcgttcccaactacaaggaatttaaggtcagccaggctaacagggacctggcctctgtggggctgcccatcaccccactgtccttccctatcccgtccatgatgaactttgacgtgcctcgcgtcagccagaacggatgccctcagctgtccaagcctgggggacccccagagcagcccctgtggaatcagtccttcctcttccaaggccgagatggagctaccagcttctcagaagacacagccctggtgctggagtactactcctcaacttcaatgaaaggcagccagccgtggaccctcaaccagcccctgggcatctctgtgttgccgctaaagagccgtttgtaccagaagatgctgacagggaaaggcttggacgggcttcacgtggagcggctccccatcatggacaccagcctgaaaactatcaatgatgaggcccccacagtggctctctccttccagctgctttcctctgagagaccagaaaacttcttgacaccaaacaacagcaaggctcttcctaccttggaccccaagatcctggataagaagctgagaaccatccaagagtcctggtccaaggacacagtgagctccacaatggacttgagcacgtccactccacgagaagcagaggaggaacctctggtgcctgagatgtcccatgacacagagatgaacaactaccggcgggccatgcagaagatggcagaggacatcctgtctctgcggagacaggccagcatcctggaaggagagaaccgcatactgaggagccgcctggcccagcaggaggaggaagaggggcagggcaaagccagtgaggcccagaacacggtgtccatgaagcagaaactgctgctgagtgagctggatatgaagaaactgagggacagggtgcagcatttgcagaatgagctgattcgaaagaatgatcgagagaaggagctgctccttctgtatcaggcccagcagccacaggccgctctgctgaagcagtaccagggcaagctgcagaagatgaaggcgctggaggagactgtgcggcaccaagagaaggtgatcgagaagatggagcgggtgctggaggacaggctgcaggacaggagcaagccccctcctctgaacaggcagcagggaaagccctacacgggcttccctatgctctcagcctctggccttcccttgggttctatgggagagaacctgccggttgaactttactcggtgctgctggcagaaaacgcgaagctgcggacggagctggataagaaccgccaccagcaggcccccatcattctgcagcaacaggccctgccggatctcctctctggtacttcagacaagttcaacctcctggccaagctggaacacgctcagagccggatcctgtccctggaaagccagttagaggactcagctcgacgctggggacgagagaagcaggatctggccacacggctgcaggagcaagaaaaaggtttcaggcacccctcgaactccatcatcatagaacagcctagtgccctcacccactccatggacctcaagcagccctcagagctggagcccctgctgcccagctcagactctaagctcaacaagcccttgagcccccagaaggagaccgctaactctcagcagacctgagccccagagcaggcctccttccctgtgtgctggggagtctcatcaccgccccctaaaaatgacgttattaaatgttgtagctctgtgaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("CCDC33");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 15, 74536399, PositionType.ZERO_BASED),
				"TAAGAAGGAGACCATCA", "ACTACCAGAGGAAT");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc002axo.4:exon2:c.96_112delinsACTACCAGAGGAAT:p.Lys33_Met38delinsLeuProGluGluLeu",
				annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.NON_FS_SUBSTITUTION, annotation1.getVariantType());
	}

	@Test
	public void testRealWorldCase_uc011ddm_2_second() throws InvalidGenomeChange {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc011ddm.2	chr5	-	156479371	156485970	156479372	156484954	4	156479371,156482211,156484908,156485931,	156479665,156482544,156485087,156485970,	E9PFX0	uc011ddm.2");
		this.transcriptForward
				.setSequence("gttacccagcattgtgagtgacagagcctggatctgaacagcaggctcatatgaatcaaccaactgggtgaaaagataagttgcaatctgagatttaagacttgatcagataccatctggtggagggtaccaaccagcctgtctgctcattttccttcaggctgatcccataatgcatcctcaagtggtcatcttaagcctcatcctacatctggcagattctgtagctggttctgtaaaggttggtggagaggcaggtccatctgtcacactaccctgccactacagtggagctgtcacatccatgtgctggaatagaggctcatgttctctattcacatgccaaaatggcattgtctggaccaatggaacccacgtcacctatcggaaggacacacgctataagctattgggggacctttcaagaagggatgtctctttgaccatagaaaatacagctgtgtctgacagtggcgtatattgttgccgtgttgagcaccgtgggtggttcaatgacatgaaaatcaccgtatcattggagattgtgccacccaaggtcacgactactccaattgtcacaactgttccaaccgtcacgactgttcgaacgagcaccactgttccaacgacaacgactgttccaatgacgactgttccaacgacaactgttccaacaacaatgagcattccaacgacaacgactgttctgacgacaatgactgtttcaacgacaacgagcgttccaacgacaacgagcattccaacaacaacaagtgttccagtgacaacaactgtctctacctttgttcctccaatgcctttgcccaggcagaaccatgaaccag"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("HAVCR1");
		this.infoForward = new TranscriptInfo(this.transcriptForward);
		// RefSeq NM_012206.2

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 5, 156479564, PositionType.ZERO_BASED),
				"AGTCGT", "GAGCTA");
		Annotation annotation1 = BlockSubstitutionAnnotationBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc011ddm.2:exon4:c.475_480delinsTAGCTC:p.Thr159*", annotation1.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, annotation1.getVariantType());
	}

}
