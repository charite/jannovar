package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenomeChangeNormalizerTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript on forward strand */
	TranscriptInfoBuilder builderForward;
	/** transcript on reverse strand */
	TranscriptInfoBuilder builderReverse;
	/** transcript info on forward strand */
	TranscriptInfo infoForward;
	/** transcript info on reverse strand */
	TranscriptInfo infoReverse;

	@Before
	public void setUp() {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001anx.3	chr1	+	6640062	6649340	6640669	6649272	11	6640062,6640600,6642117,6645978,6646754,6647264,6647537,6648119,6648337,6648815,6648975,	6640196,6641359,6642359,6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,	P10074	uc001anx.3");
		this.builderForward
				.setSequence("cgtcacgtccggcgcggagacggtggagtctccgcactgtcggcggggtacgcatagccgggcactaggttcgtgggctgtggaggcgacggagcagggggccagtggggccagctcagggaggacctgcctgggagctttctcttgcataccctcgcttaggctggccggggtgtcacttctgcctccctgccctccagaccatggacggctccttcgtccagcacagtgtgagggttctgcaggagctcaacaagcagcgggagaagggccagtactgcgacgccactctggacgtggggggcctggtgtttaaggcacactggagtgtccttgcctgctgcagtcactttttccagagcctctacggggatggctcagggggcagtgtcgtcctccctgctggcttcgctgagatctttggcctcttgttggactttttctacactggtcacctcgctctcacctcagggaaccgggatcaggtgctcctggcagccagggagttgcgagtgccagaggccgtagagctgtgccagagcttcaagcccaaaacttcagtgggacaggcagcaggtggccagagtgggctggggccccctgcctcccagaatgtgaacagccacgtcaaggagccggcaggcttggaagaagaggaagtttcgaggactctgggtctagtccccagggatcaggagcccagaggcagtcatagtcctcagaggccccagctccattccccagctcagagtgagggcccctcctccctctgtgggaaactgaagcaggccttgaagccttgtccccttgaggacaagaaacccgaggactgcaaagtgcccccaaggcccttagaggctgaaggtgcccagctgcagggcggcagtaatgagtgggaagtggtggttcaagtggaggatgatggggatggcgattacatgtctgagcctgaggctgtgctgaccaggaggaagtcaaatgtaatccgaaagccctgtgcagctgagccagccctgagcgcgggctccctagcagctgagcctgctgagaacagaaaaggtacagcggtgccggtcgaatgccccacatgtcataaaaagttcctcagcaaatattatctaaaagtccacaacaggaaacatactggggagaaaccctttgagtgtcccaaatgtgggaagtgttactttcggaaggagaacctcctggagcatgaagcccggaattgcatgaaccgctcggaacaggtcttcacgtgctctgtgtgccaggagacattccgccgaaggatggagctgcgggtgcacatggtgtctcacacaggggagatgccctacaagtgttcctcctgctcccagcagttcatgcagaagaaggacttgcagagccacatgatcaaacttcatggagcccccaagccccatgcatgccccacctgtgccaagtgcttcctgtctcggacagagctgcagctgcatgaagctttcaagcaccgtggtgagaagctgtttgtgtgtgaggagtgtgggcaccgggcctcgagccggaatggcctgcagatgcacatcaaggccaagcacaggaatgagaggccacacgtatgtgagttctgcagccacgccttcacccaaaaggccaatctcaacatgcacctgcgcacacacacgggtgagaagcccttccagtgccacctctgtggcaagaccttccgaacccaagccagcctggacaagcacaaccgcacccacaccggggaaaggcccttcagttgcgagttctgtgaacagcgcttcactgagaaggggcccctcctgaggcacgtggccagccgccatcaggagggccggccccacttctgccagatatgcggcaagaccttcaaagccgtggagcaactgcgtgtgcacgtcagacggcacaagggggtgaggaagtttgagtgcaccgagtgtggctacaagtttacccgacaggcccacctgcggaggcacatggagatccacgaccgggtagagaactacaacccgcggcagcgcaagctccgcaacctgatcatcgaggacgagaagatggtggtggtggcgctgcagccgcctgcagagctggaggtgggctcggcggaggtcattgtggagtccctggcccagggcggcctggcctcccagctccccggccagagactgtgtgcagaggagagcttcaccggcccaggtgtcctggagccctccctcatcatcacagctgctgtccccgaggactgtgacacatagcccattctggccaccagagcccacttggccccacccctcaataaaccgtgtggctttggactctcgtaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZBTB48");
		this.infoForward = builderForward.make();
		// RefSeq: NM_005341.3

		this.builderReverse = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bgu.3	chr1	-	23685940	23696357	23688461	23694498	4	23685940,23693534,23694465,23695858,	23689714,23693661,23694558,23696357,	Q9C0F3	uc001bgu.3");
		this.builderReverse
				.setSequence("aataagctgctatattctttttccatcacttccctctccaaggctacagcgagctgggagctcttccccacgcagaatgcctgctttccccagtgctcgacttccattgtctaattccctcatcctggctggggaaagggagagctgcgagtcctcccgttccgaggaactccagctgaatgcagcttagttgctggtggtttctcggccagcctctgtggtctcagggatctgcctatgagcctgtggtttctgagctgcctgcgagtctgaggcctcgggaatctgagtctttaggatcagcctacgatatctgggcttcgcctgcaagtctacgaattcgagatctacctgcgggtctgagacctccgggacctgcccgtgctctctagaatcttcctgaacgccaggtctgagagaacgctgcggctctggaacccgttcgcggtctctcaggttttggagacgacgatctagtggatcttttgcgggacaggagcgctgtctgctagctgcttttcctgctctctctccctggaggcgaacccttgtgctcgagatggcagccaccctgctcatggctgggtcccaggcacctgtgacgtttgaagatatggccatgtatctcacccgggaagaatggagacctctggacgctgcacagagggacctttaccgggatgttatgcaggagaattatggaaatgttgtctcactagattttgagatcaggagtgagaacgaggtaaatcccaagcaagagattagtgaagatgtacaatttgggactacatctgaaagacctgctgagaatgctgaggaaaatcctgaaagtgaagagggctttgaaagcggagataggtcagaaagacaatggggagatttaacagcagaagagtgggtaagctatcctctccaaccagtcactgatctacttgtccacaaagaagtccacacaggcatccgctatcatatatgttctcattgtggaaaggccttcagtcagatctcagaccttaatcgacatcagaagacccacactggagacagaccctataaatgttatgaatgtggaaaaggcttcagtcgcagctcacaccttattcagcatcaaagaacacatactggggagaggccttatgactgtaacgagtgtgggaaaagttttggaagaagttctcacctgattcagcatcagacaatccacactggagagaagcctcacaaatgtaatgagtgtggaaaaagtttctgccgtctctctcacctaatccaacaccaaaggacccacagtggtgagaaaccctatgagtgtgaggagtgtgggaaaagcttcagccggagctctcacctagctcagcaccagaggacccacacgggtgagaaaccttatgaatgtaacgaatgtggccgaggcttcagtgagagatctgatctcatcaaacactatcgagtccacacaggggagaggccctacaagtgtgatgagtgtgggaagaatttcagtcagaactccgaccttgtgcgtcatcgcagagcccacacgggagagaagccataccactgtaacgaatgtggggaaaatttcagccgcatctcacacttggttcagcaccagagaactcacactggagagaagccatatgaatgcaatgcttgtgggaaaagcttcagccggagctctcatctcatcacacaccagaaaattcacactggagagaagccttatgagtgtaatgagtgttggcgaagctttggtgaaaggtcagatctaattaaacatcagagaacccacacaggggagaagccctacgagtgtgtgcagtgtgggaaaggtttcacccagagctccaacctcatcacacatcaaagagttcacacgggagagaaaccttatgaatgtaccgaatgtgagaagagtttcagcaggagctcagctcttattaaacataagagagttcatacggactaagctgtaattatgatggctgagaaatgattcatttgaagatacaattttatttgatatcaatgaacgccctcaagactgagctgcttttatcatactctcctagttgtgggccacgatttaaaccatcagagatgacaagccatttgaaattctgaccctcagctttgggaatgttatctcctccaaaatggtgatttttattcactcaatgggttacttcattaaaagcagccccacaagtaactggaaatctgaagaccaggggacaaatgctggtgaatgcttaggcctggaaatggagtaaatctttcaatgttattttctcccatccttggcccaaggaactatgctaagtgaaacgtgggactgtaatagggtggtaatggctgctttggaaaaaggcaactagagactctgcctaaattgccacacctattcacacaccatagtagttgggcacacacatcttcccttccaaagggctttttccttgagttgctcatgcatttgtatcttttccatcttcctgagggcaagattttgcacgatgaaggcaatgattgtaacttttctccttctcattgtttctaattagctcctttaaagcttgcatctttgtgaaggctaactgaagatacggttggaaaggaaaaatgagacacaggtttggggaccaaggacccatcaatgatggtgactttagcagaagatgcccacagttattactgccattaatcagatttatgaattttctttggggatcactatagggaatattgtatagaaaatatcttcaagaaaagataggaccatcagtgacagttaagtgtaaggagcaagtggaattgagtccttcagggaaggaaccacagagtcccttcccaaggaatgtaggtcgtttctgtgttctttcccttctaatctttaagatcaactcttcctatcctgctaactctaagatttgataagggccacatcccagtgtttatcttagcttgcatcagggcatgtgtatgtacagtaatgtgtattcctgtggtttttctaatagaaactgaatttacagagacttagcatgttcttgggtgatgtgagtcatgtgacagaagtacagacataactccaatgtgagaaatgtccttttttcattatggaaaataatttaaacactagtgctttagtgtgcactctcctgtaaggtctgtctttgtacagagctaagcacttgtttgtatgtgtttgtcaattgtggaagataatgaccagacaaataggtcgattgtcctattctcagaatgaattatcttctatggtaatgaagaactctttggcttagtcagaaggaattaacgaacctcggtaggaatgtatttccatcctcccaccctacagatataagaggttaaaataacagttcgcccaatttaagcccagtagtgtcagttttcctaatctcagtccaggtaggaattaagaaatatctcaagtgttgatgctatccaagcatgttggggtggaagggaattggtgcccagaaaatgggactggagtgaggaatatcttttcttttgagagtacccccagtttatttctactgtgctttattgctactgttctttattgtgaatgttgtaacattttaaaaatgttttgccatagctttttaggacttggtgttaaaggagccagtggtctctctgggtgggtactataatgagttattgtgacccacagctgtgtgggaccacatcacttgttaataacacaacctttaaagtaacccatcttccaggggggttccttcatgttgccactcctttttaaggacaaactcaggcaaggagcatgtttttttgttatttacaaaatctagcagactgtgggtatccatattttaattgtcgggtgacacatgttcttggtaactaaactcaaatatgtcttttctcatatatgttgctgatggttttaataaatgtcaaagttctcctgttgcttctgtgagccactatgggtatcagcttgggagtggccatagatgaccgcatttccatgacctaactgtatttcacccccttttccttccctactgttcttgccccaccccaaccagttcctgctgctgcttttggcttcttggaggtgaagggcttaaaacaaggcttctaagcacccagctatctccatacatgaacaatctagctgggaaacttaagggacaagggccacaccagctgtctcctctttctgccaattgttgcccgtttgctgtgttgaactttgtatagaactcatgcatcagactcccttcactaatgctttttgcatgccttctgctcccaagtccctggctgcctctgcacatcccgtgaacactttgtgcctgttttctatggttgtggagaattaatgaacaaatcaatatgtagaacagttttccttatggtattggtcacagttatcctagtgtttgtattattctaacaatattctataattaaaaatataatttttaaagtca"
						.toUpperCase());
		this.builderReverse.setGeneSymbol("ZNF436");
		this.infoReverse = builderForward.make();
		// RefSeq: NM_001077195.1
	}

	@Test
	public void testForwardInsertNoNormalizationNecessaryOneBaseFirst() {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640669, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "G", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testForwardInsertNoNormalizationNecessaryOneBaseSecond() {
		// one base at second CDS position
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640670, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "A", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testForwardInsertNoNormalizationNecessaryMoreBasesSecond() {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640670, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "AAA", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testForwardInsertNormalizationNecessaryOneBaseFirst() {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640669, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "C", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(1), "", "C", '+');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testForwardInsertNormalizationNecessaryOneBaseSecond() {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640670, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "G", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(1), "", "G", '+');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testForwardInsertNormalizationNecessaryMoreBasesSecond() {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640670, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "GTGC", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(2), "", "GCGT", '+');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseInsertNoNormalizationNecessaryOneBaseFirst() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23694498, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "G", '-');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testReverseInsertNoNormalizationNecessaryOneBaseSecond() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23694498, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "A", '-');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testReverseInsertNoNormalizationNecessaryMoreBasesSecond() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23694497, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "AAA", '-');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testReverseInsertNormalizationNecessaryOneBaseFirst() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23694497, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "T", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(-2), "", "T", '-');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseInsertNormalizationNecessaryOneBaseSecond() {
		// one base at second CDS position
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23694497, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "T", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(-1), "", "T", '-');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseInsertNormalizationNecessaryMoreBasesSecond() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23694497, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "", "GGGTTAT", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(-4), "", "TTATGGG", '-');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testForwardDeletionNormalization() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 24, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 6640062 + 24, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "GGAGTCTCCGCACT", "", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeDeletion(this.infoForward, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(1), "GAGTCTCCGCACTG", "", '+');
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseDeletionNormalization() {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = new GenomePosition(refDict, '+', 1, 23696357 - 15, PositionType.ZERO_BASED);
		GenomeChange change = new GenomeChange(gPos, "ATAGCAGCTTATT", "", '+');
		GenomeChange updatedChange = GenomeChangeNormalizer.normalizeDeletion(this.infoReverse, change, txPos);
		GenomeChange expectedChange = new GenomeChange(gPos.shifted(-1), "TATAGCAGCTTAT", "", '+');
		Assert.assertEquals(expectedChange, updatedChange);
	}
}
