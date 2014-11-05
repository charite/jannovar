package jannovar.annotation;

import jannovar.annotation.builders.SingleNucleotideSubstitutionBuilder;
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
	}

	@Test(expected = InvalidGenomeChange.class)
	public void testForwardOneBeforeFirstCDSBase() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640668, PositionType.ZERO_BASED), "T", "A");
		SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
	}

	@Test
	public void testForwardFirstCDSBases() throws InvalidGenomeChange {
		// We check the first 10 CDS bases and compared them by hand to Mutalyzer results.
		//
		// TODO(holtgrem): the first three changes should be in the start codon but are not
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6640669, PositionType.ZERO_BASED), "A", "T");
		Assert.assertEquals("uc001anx.3:exon2:c.1A>T:p.Met1Leu",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change1).getVariantAnnotation());

		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6640670, PositionType.ZERO_BASED), "T", "C");
		Assert.assertEquals("uc001anx.3:exon2:c.2T>C:p.Met1Thr",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change2).getVariantAnnotation());

		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6640671, PositionType.ZERO_BASED), "G", "A");
		Assert.assertEquals("uc001anx.3:exon2:c.3G>A:p.Met1Ile",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change3).getVariantAnnotation());

		GenomeChange change4 = new GenomeChange(new GenomePosition('+', 1, 6640672, PositionType.ZERO_BASED), "G", "T");
		Assert.assertEquals("uc001anx.3:exon2:c.4G>T:p.Asp2Tyr",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change4).getVariantAnnotation());

		GenomeChange change5 = new GenomeChange(new GenomePosition('+', 1, 6640673, PositionType.ZERO_BASED), "A", "T");
		Assert.assertEquals("uc001anx.3:exon2:c.5A>T:p.Asp2Val",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change5).getVariantAnnotation());

		GenomeChange change6 = new GenomeChange(new GenomePosition('+', 1, 6640674, PositionType.ZERO_BASED), "C", "T");
		Assert.assertEquals("uc001anx.3:exon2:c.6C>T:p.(=)",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change6).getVariantAnnotation());

		GenomeChange change7 = new GenomeChange(new GenomePosition('+', 1, 6640675, PositionType.ZERO_BASED), "G", "T");
		Assert.assertEquals("uc001anx.3:exon2:c.7G>T:p.Gly3Cys",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change7).getVariantAnnotation());

		GenomeChange change8 = new GenomeChange(new GenomePosition('+', 1, 6640676, PositionType.ZERO_BASED), "G", "T");
		Assert.assertEquals("uc001anx.3:exon2:c.8G>T:p.Gly3Val",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change8).getVariantAnnotation());

		GenomeChange change9 = new GenomeChange(new GenomePosition('+', 1, 6640677, PositionType.ZERO_BASED), "C", "G");
		Assert.assertEquals("uc001anx.3:exon2:c.9C>G:p.(=)",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change9).getVariantAnnotation());

		GenomeChange change10 = new GenomeChange(new GenomePosition('+', 1, 6640678, PositionType.ZERO_BASED), "T", "A");
		Assert.assertEquals("uc001anx.3:exon2:c.10T>A:p.Ser4Thr",
				SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change10).getVariantAnnotation());

	}

	@Test
	public void testForwardLastCDSBases() throws InvalidGenomeChange {
		// Here, we start off 3 positions before the end (2 positions before the inclusive end).
		// TODO(holtgrem): Here, Mutalizer converted to "p.(*689Trpext*23)"
		GenomeChange change1 = new GenomeChange(new GenomePosition('+', 1, 6649270, PositionType.ZERO_BASED), "A", "G");
		Assert.assertEquals("uc001anx.3:exon11:c.2066A>G:p.*689Trp", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change1).getVariantAnnotation());
		// TODO(holtgrem): Mutalizer converts to p.(*689Glnext*23)
		GenomeChange change2 = new GenomeChange(new GenomePosition('+', 1, 6649269, PositionType.ZERO_BASED), "T", "C");
		Assert.assertEquals("uc001anx.3:exon11:c.2065T>C:p.*689Gln", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change2).getVariantAnnotation());

		GenomeChange change3 = new GenomeChange(new GenomePosition('+', 1, 6649268, PositionType.ZERO_BASED), "A", "T");
		Assert.assertEquals("uc001anx.3:exon11:c.2064A>T:p.(=)",
				SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change3).getVariantAnnotation());

		GenomeChange change4 = new GenomeChange(new GenomePosition('+', 1, 6649267, PositionType.ZERO_BASED), "C", "G");
		Assert.assertEquals("uc001anx.3:exon11:c.2063C>G:p.Thr688Arg", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change4).getVariantAnnotation());

		GenomeChange change5 = new GenomeChange(new GenomePosition('+', 1, 6649266, PositionType.ZERO_BASED), "A", "G");
		Assert.assertEquals("uc001anx.3:exon11:c.2062A>G:p.Thr688Ala", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change5).getVariantAnnotation());

		GenomeChange change6 = new GenomeChange(new GenomePosition('+', 1, 6649265, PositionType.ZERO_BASED), "C", "T");
		Assert.assertEquals("uc001anx.3:exon11:c.2061C>T:p.(=)",
				SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change6).getVariantAnnotation());

		GenomeChange change7 = new GenomeChange(new GenomePosition('+', 1, 6649264, PositionType.ZERO_BASED), "A", "G");
		Assert.assertEquals("uc001anx.3:exon11:c.2060A>G:p.Asp687Gly", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change7).getVariantAnnotation());

		GenomeChange change8 = new GenomeChange(new GenomePosition('+', 1, 6649263, PositionType.ZERO_BASED), "G", "A");
		Assert.assertEquals("uc001anx.3:exon11:c.2059G>A:p.Asp687Asn", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change8).getVariantAnnotation());

		GenomeChange change9 = new GenomeChange(new GenomePosition('+', 1, 6649262, PositionType.ZERO_BASED), "T", "G");
		Assert.assertEquals("uc001anx.3:exon11:c.2058T>G:p.Cys686Trp", SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change9).getVariantAnnotation());

		GenomeChange change10 = new GenomeChange(new GenomePosition('+', 1, 6649261, PositionType.ZERO_BASED), "G", "C");
		Assert.assertEquals("uc001anx.3:exon11:c.2057G>C:p.Cys686Ser",
				SingleNucleotideSubstitutionBuilder
				.buildAnnotation(infoForward, change10).getVariantAnnotation());
	}

	@Test(expected = InvalidGenomeChange.class)
	public void testForwardOneAfterLastCDSBase() throws InvalidGenomeChange {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649272, PositionType.ZERO_BASED), "T", "A");
		SingleNucleotideSubstitutionBuilder.buildAnnotation(infoForward, change);
	}

}
