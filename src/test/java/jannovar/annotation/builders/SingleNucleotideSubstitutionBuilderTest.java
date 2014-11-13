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

// TODO(holtgrem): Extend tests for reverse transcript?

public class SingleNucleotideSubstitutionBuilderTest {

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
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640061, PositionType.ZERO_BASED), "T", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.-204T>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UPSTREAM, anno.getVariantType());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649340, PositionType.ZERO_BASED), "T", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.*69T>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.DOWNSTREAM, anno.getVariantType());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeChange {
		// upstream intergenic
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6639061, PositionType.ZERO_BASED), "T", "A");
		Annotation anno1 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:c.-1204T>A", anno1.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, anno1.getVariantType());

		// downstream intergenic
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6650340, PositionType.ZERO_BASED), "T", "A");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:c.*1069T>A", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTERGENIC, anno2.getVariantType());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeChange {
		// position towards right side of intron
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6642106, PositionType.ZERO_BASED), "T", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.691-11T>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTRONIC, anno.getVariantType());

		// position towards left side of intron
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6646100, PositionType.ZERO_BASED), "T", "A");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:c.1044+11T>A", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.INTRONIC, anno2.getVariantType());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649272, PositionType.ZERO_BASED), "T", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon11:c.*1T>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR3, anno.getVariantType());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640668, PositionType.ZERO_BASED), "T", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon2:c.-1T>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.UTR5, anno.getVariantType());
	}

	@Test
	public void testForwardStartLoss() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640669, PositionType.ZERO_BASED), "A", "T");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon2:c.1A>T:p.0?", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno.getVariantType());
	}

	@Test
	public void testForwardStopLoss() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "G", "C");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon11:c.2067G>C:p.*689Tyrext*23", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, anno.getVariantType());
	}

	@Test
	public void testForwardStopGained() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649262, PositionType.ZERO_BASED), "T", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon11:c.2058T>A:p.Cys686*", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPGAIN, anno.getVariantType());
	}

	@Test
	public void testForwardStopRetained() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649271, PositionType.ZERO_BASED), "G", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon11:c.2067G>A:p.=", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno.getVariantType());
	}

	@Test
	public void testForwardSplicingDonor() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640196, PositionType.ZERO_BASED), "G", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.-70+1G>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, anno.getVariantType());
	}

	@Test
	public void testForwardSplicingAcceptor() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640599, PositionType.ZERO_BASED), "G", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:c.-69-1G>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, anno.getVariantType());
	}

	@Test
	public void testForwardSplicingRegion() throws InvalidGenomeChange {
		// in UTR
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640602, PositionType.ZERO_BASED), "G", "A");
		Annotation anno = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
		Assert.assertEquals("uc001anx.3:exon2:c.-67G>A", anno.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, anno.getVariantType());
		// in CDS
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6647537, PositionType.ZERO_BASED), "T", "G");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon7:c.1225T>G:p.Cys409Gly", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.SPLICING, anno2.getVariantType());
	}

	@Test
	public void testForwardFirstCDSBases() throws InvalidGenomeChange {
		// We check the first 10 CDS bases and compared them by hand to Mutalyzer results.

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640669, PositionType.ZERO_BASED), "A", "T");
		Annotation anno1 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon2:c.1A>T:p.0?", anno1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "T", "C");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon2:c.2T>C:p.0?", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno2.getVariantType());

		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED), "G", "A");
		Annotation anno3 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change3);
		Assert.assertEquals("uc001anx.3:exon2:c.3G>A:p.0?", anno3.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno3.getVariantType());

		GenomeChange change4 = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "G", "T");
		Annotation anno4 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change4);
		Assert.assertEquals("uc001anx.3:exon2:c.4G>T:p.Asp2Tyr", anno4.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno4.getVariantType());

		GenomeChange change5 = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "A", "T");
		Annotation anno5 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change5);
		Assert.assertEquals("uc001anx.3:exon2:c.5A>T:p.Asp2Val", anno5.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno5.getVariantType());

		GenomeChange change6 = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "C", "T");
		Annotation anno6 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change6);
		Assert.assertEquals("uc001anx.3:exon2:c.6C>T:p.=", anno6.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno6.getVariantType());

		GenomeChange change7 = new GenomeChange(new GenomePosition('+', 1, 6640675, PositionType.ZERO_BASED), "G", "T");
		Annotation anno7 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change7);
		Assert.assertEquals("uc001anx.3:exon2:c.7G>T:p.Gly3Cys", anno7.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno7.getVariantType());

		GenomeChange change8 = new GenomeChange(new GenomePosition('+', 1, 6640676, PositionType.ZERO_BASED), "G", "T");
		Annotation anno8 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change8);
		Assert.assertEquals("uc001anx.3:exon2:c.8G>T:p.Gly3Val", anno8.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno8.getVariantType());

		GenomeChange change9 = new GenomeChange(new GenomePosition('+', 1, 6640677, PositionType.ZERO_BASED), "C", "G");
		Annotation anno9 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change9);
		Assert.assertEquals("uc001anx.3:exon2:c.9C>G:p.=", anno9.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno9.getVariantType());

		GenomeChange change10 = new GenomeChange(new GenomePosition('+', 1, 6640678, PositionType.ZERO_BASED), "T", "A");
		Annotation anno10 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change10);
		Assert.assertEquals("uc001anx.3:exon2:c.10T>A:p.Ser4Thr", anno10.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno10.getVariantType());
	}

	@Test
	public void testForwardLastCDSBases() throws InvalidGenomeChange {
		// Here, we start off 3 positions before the end (2 positions before the inclusive end).

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6649270, PositionType.ZERO_BASED), "A", "G");
		Annotation anno1 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change1);
		Assert.assertEquals("uc001anx.3:exon11:c.2066A>G:p.*689Trpext*23", anno1.getVariantAnnotation());
		Assert.assertEquals(anno1.getVariantType(), VariantType.STOPLOSS);

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6649269, PositionType.ZERO_BASED), "T", "C");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change2);
		Assert.assertEquals("uc001anx.3:exon11:c.2065T>C:p.*689Glnext*23", anno2.getVariantAnnotation());
		Assert.assertEquals(anno2.getVariantType(), VariantType.STOPLOSS);

		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6649268, PositionType.ZERO_BASED), "A", "T");
		Annotation anno3 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change3);
		Assert.assertEquals("uc001anx.3:exon11:c.2064A>T:p.=", anno3.getVariantAnnotation());
		Assert.assertEquals(anno3.getVariantType(), VariantType.SYNONYMOUS);

		GenomeChange change4 = new GenomeChange(new GenomePosition('+', 1, 6649267, PositionType.ZERO_BASED), "C", "G");
		Annotation anno4 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change4);
		Assert.assertEquals("uc001anx.3:exon11:c.2063C>G:p.Thr688Arg", anno4.getVariantAnnotation());
		Assert.assertEquals(anno4.getVariantType(), VariantType.MISSENSE);

		GenomeChange change5 = new GenomeChange(new GenomePosition('+', 1, 6649266, PositionType.ZERO_BASED), "A", "G");
		Annotation anno5 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change5);
		Assert.assertEquals("uc001anx.3:exon11:c.2062A>G:p.Thr688Ala", anno5.getVariantAnnotation());
		Assert.assertEquals(anno5.getVariantType(), VariantType.MISSENSE);

		GenomeChange change6 = new GenomeChange(new GenomePosition('+', 1, 6649265, PositionType.ZERO_BASED), "C", "T");
		Annotation anno6 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change6);
		Assert.assertEquals("uc001anx.3:exon11:c.2061C>T:p.=", anno6.getVariantAnnotation());
		Assert.assertEquals(anno6.getVariantType(), VariantType.SYNONYMOUS);

		GenomeChange change7 = new GenomeChange(new GenomePosition('+', 1, 6649264, PositionType.ZERO_BASED), "A", "G");
		Annotation anno7 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change7);
		Assert.assertEquals("uc001anx.3:exon11:c.2060A>G:p.Asp687Gly", anno7.getVariantAnnotation());
		Assert.assertEquals(anno7.getVariantType(), VariantType.MISSENSE);

		GenomeChange change8 = new GenomeChange(new GenomePosition('+', 1, 6649263, PositionType.ZERO_BASED), "G", "A");
		Annotation anno8 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change8);
		Assert.assertEquals("uc001anx.3:exon11:c.2059G>A:p.Asp687Asn", anno8.getVariantAnnotation());
		Assert.assertEquals(anno8.getVariantType(), VariantType.MISSENSE);

		GenomeChange change9 = new GenomeChange(new GenomePosition('+', 1, 6649262, PositionType.ZERO_BASED), "T", "G");
		Annotation anno9 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change9);
		Assert.assertEquals("uc001anx.3:exon11:c.2058T>G:p.Cys686Trp", anno9.getVariantAnnotation());
		Assert.assertEquals(anno9.getVariantType(), VariantType.MISSENSE);

		GenomeChange change10 = new GenomeChange(new GenomePosition('+', 1, 6649261, PositionType.ZERO_BASED), "G", "C");
		Annotation anno10 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change10);
		Assert.assertEquals("uc001anx.3:exon11:c.2057G>C:p.Cys686Ser", anno10.getVariantAnnotation());
		Assert.assertEquals(anno10.getVariantType(), VariantType.MISSENSE);
	}

	@Test
	public void testReverseFirstCDSBases() throws InvalidGenomeChange {
		// We check the first 10 CDS bases and compared them by hand to Mutalyzer results.

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 23694497, PositionType.ZERO_BASED), "T", "A");
		Annotation anno1 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change1);
		Assert.assertEquals("uc001bgu.3:exon2:c.1A>T:p.0?", anno1.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 23694496, PositionType.ZERO_BASED), "A", "G");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change2);
		Assert.assertEquals("uc001bgu.3:exon2:c.2T>C:p.0?", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno2.getVariantType());

		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 23694495, PositionType.ZERO_BASED), "C", "T");
		Annotation anno3 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change3);
		Assert.assertEquals("uc001bgu.3:exon2:c.3G>A:p.0?", anno3.getVariantAnnotation());
		Assert.assertEquals(VariantType.START_LOSS, anno3.getVariantType());

		GenomeChange change4 = new GenomeChange(new GenomePosition('+', 1, 23694494, PositionType.ZERO_BASED), "C", "A");
		Annotation anno4 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change4);
		Assert.assertEquals("uc001bgu.3:exon2:c.4G>T:p.Ala2Ser", anno4.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno4.getVariantType());

		GenomeChange change5 = new GenomeChange(new GenomePosition('+', 1, 23694493, PositionType.ZERO_BASED), "G", "A");
		Annotation anno5 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change5);
		Assert.assertEquals("uc001bgu.3:exon2:c.5C>T:p.Ala2Val", anno5.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno5.getVariantType());

		GenomeChange change6 = new GenomeChange(new GenomePosition('+', 1, 23694492, PositionType.ZERO_BASED), "T", "G");
		Annotation anno6 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change6);
		Assert.assertEquals("uc001bgu.3:exon2:c.6A>C:p.=", anno6.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno6.getVariantType());

		GenomeChange change7 = new GenomeChange(new GenomePosition('+', 1, 23694491, PositionType.ZERO_BASED), "C", "T");
		Annotation anno7 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change7);
		Assert.assertEquals("uc001bgu.3:exon2:c.7G>A:p.Ala3Thr", anno7.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno7.getVariantType());

		GenomeChange change8 = new GenomeChange(new GenomePosition('+', 1, 23694490, PositionType.ZERO_BASED), "G", "A");
		Annotation anno8 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change8);
		Assert.assertEquals("uc001bgu.3:exon2:c.8C>T:p.Ala3Val", anno8.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno8.getVariantType());

		GenomeChange change9 = new GenomeChange(new GenomePosition('+', 1, 23694489, PositionType.ZERO_BASED), "G", "C");
		Annotation anno9 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change9);
		Assert.assertEquals("uc001bgu.3:exon2:c.9C>G:p.=", anno9.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno9.getVariantType());

		GenomeChange change10 = new GenomeChange(new GenomePosition('+', 1, 23694488, PositionType.ZERO_BASED), "T",
				"C");
		Annotation anno10 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change10);
		Assert.assertEquals("uc001bgu.3:exon2:c.10A>G:p.Thr4Ala", anno10.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno10.getVariantType());
	}

	@Test
	public void testReverseLastCDSBases() throws InvalidGenomeChange {
		// Here, we start off 3 positions before the end (2 positions before the inclusive end).

		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 23688461, PositionType.ZERO_BASED), "T", "C");
		Annotation anno1 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change1);
		Assert.assertEquals("uc001bgu.3:exon4:c.1413A>G:p.=", anno1.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno1.getVariantType());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 23688462, PositionType.ZERO_BASED), "T", "G");
		Annotation anno2 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change2);
		Assert.assertEquals("uc001bgu.3:exon4:c.1412A>C:p.*471Serext*9", anno2.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, anno2.getVariantType());

		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 23688463, PositionType.ZERO_BASED), "A", "T");
		Annotation anno3 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change3);
		Assert.assertEquals("uc001bgu.3:exon4:c.1411T>A:p.*471Lysext*9", anno3.getVariantAnnotation());
		Assert.assertEquals(VariantType.STOPLOSS, anno3.getVariantType());

		GenomeChange change4 = new GenomeChange(new GenomePosition('+', 1, 23688464, PositionType.ZERO_BASED), "G", "C");
		Annotation anno4 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change4);
		Assert.assertEquals("uc001bgu.3:exon4:c.1410C>G:p.Asp470Glu", anno4.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno4.getVariantType());

		GenomeChange change5 = new GenomeChange(new GenomePosition('+', 1, 23688465, PositionType.ZERO_BASED), "T", "C");
		Annotation anno5 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change5);
		Assert.assertEquals("uc001bgu.3:exon4:c.1409A>G:p.Asp470Gly", anno5.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno5.getVariantType());

		GenomeChange change6 = new GenomeChange(new GenomePosition('+', 1, 23688466, PositionType.ZERO_BASED), "C", "A");
		Annotation anno6 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change6);
		Assert.assertEquals("uc001bgu.3:exon4:c.1408G>T:p.Asp470Tyr", anno6.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno6.getVariantType());

		GenomeChange change7 = new GenomeChange(new GenomePosition('+', 1, 23688467, PositionType.ZERO_BASED), "C", "G");
		Annotation anno7 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change7);
		Assert.assertEquals("uc001bgu.3:exon4:c.1407G>C:p.=", anno7.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno7.getVariantType());

		GenomeChange change8 = new GenomeChange(new GenomePosition('+', 1, 23688468, PositionType.ZERO_BASED), "G", "T");
		Annotation anno8 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change8);
		Assert.assertEquals("uc001bgu.3:exon4:c.1406C>A:p.Thr469Lys", anno8.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno8.getVariantType());

		GenomeChange change9 = new GenomeChange(new GenomePosition('+', 1, 23688469, PositionType.ZERO_BASED), "T", "C");
		Annotation anno9 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change9);
		Assert.assertEquals("uc001bgu.3:exon4:c.1405A>G:p.Thr469Ala", anno9.getVariantAnnotation());
		Assert.assertEquals(VariantType.MISSENSE, anno9.getVariantType());

		GenomeChange change10 = new GenomeChange(new GenomePosition('+', 1, 23688470, PositionType.ZERO_BASED), "A",
				"G");
		Annotation anno10 = SingleNucleotideSubstitutionBuilder.buildAnnotation(infoReverse, change10);
		Assert.assertEquals("uc001bgu.3:exon4:c.1404T>C:p.=", anno10.getVariantAnnotation());
		Assert.assertEquals(VariantType.SYNONYMOUS, anno10.getVariantType());
	}

}
