package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HGVSPositionBuilderTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript on forward strand */
	TranscriptInfoBuilder transcriptForward;
	/** transcript on reverse strand */
	TranscriptInfoBuilder transcriptReverse;
	/** non-coding transcript on forward strand */
	TranscriptInfoBuilder ncTranscriptForward;
	/** transcript info on forward strand */
	TranscriptInfo infoForward;
	/** transcript info on reverse strand */
	TranscriptInfo infoReverse;
	/** non-coding transcript info on forward strand */
	TranscriptInfo ncInfoForward;

	@Before
	public void setUp() throws Exception {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001anx.3	chr1	+	6640062	6649340	6640669	6649272	11	6640062,6640600,6642117,6645978,6646754,6647264,6647537,6648119,6648337,6648815,6648975,	6640196,6641359,6642359,6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,	P10074	uc001anx.3");
		this.transcriptForward
				.setSequence("cgtcacgtccggcgcggagacggtggagtctccgcactgtcggcggggtacgcatagccgggcactaggttcgtgggctgtggaggcgacggagcagggggccagtggggccagctcagggaggacctgcctgggagctttctcttgcataccctcgcttaggctggccggggtgtcacttctgcctccctgccctccagaccatggacggctccttcgtccagcacagtgtgagggttctgcaggagctcaacaagcagcgggagaagggccagtactgcgacgccactctggacgtggggggcctggtgtttaaggcacactggagtgtccttgcctgctgcagtcactttttccagagcctctacggggatggctcagggggcagtgtcgtcctccctgctggcttcgctgagatctttggcctcttgttggactttttctacactggtcacctcgctctcacctcagggaaccgggatcaggtgctcctggcagccagggagttgcgagtgccagaggccgtagagctgtgccagagcttcaagcccaaaacttcagtgggacaggcagcaggtggccagagtgggctggggccccctgcctcccagaatgtgaacagccacgtcaaggagccggcaggcttggaagaagaggaagtttcgaggactctgggtctagtccccagggatcaggagcccagaggcagtcatagtcctcagaggccccagctccattccccagctcagagtgagggcccctcctccctctgtgggaaactgaagcaggccttgaagccttgtccccttgaggacaagaaacccgaggactgcaaagtgcccccaaggcccttagaggctgaaggtgcccagctgcagggcggcagtaatgagtgggaagtggtggttcaagtggaggatgatggggatggcgattacatgtctgagcctgaggctgtgctgaccaggaggaagtcaaatgtaatccgaaagccctgtgcagctgagccagccctgagcgcgggctccctagcagctgagcctgctgagaacagaaaaggtacagcggtgccggtcgaatgccccacatgtcataaaaagttcctcagcaaatattatctaaaagtccacaacaggaaacatactggggagaaaccctttgagtgtcccaaatgtgggaagtgttactttcggaaggagaacctcctggagcatgaagcccggaattgcatgaaccgctcggaacaggtcttcacgtgctctgtgtgccaggagacattccgccgaaggatggagctgcgggtgcacatggtgtctcacacaggggagatgccctacaagtgttcctcctgctcccagcagttcatgcagaagaaggacttgcagagccacatgatcaaacttcatggagcccccaagccccatgcatgccccacctgtgccaagtgcttcctgtctcggacagagctgcagctgcatgaagctttcaagcaccgtggtgagaagctgtttgtgtgtgaggagtgtgggcaccgggcctcgagccggaatggcctgcagatgcacatcaaggccaagcacaggaatgagaggccacacgtatgtgagttctgcagccacgccttcacccaaaaggccaatctcaacatgcacctgcgcacacacacgggtgagaagcccttccagtgccacctctgtggcaagaccttccgaacccaagccagcctggacaagcacaaccgcacccacaccggggaaaggcccttcagttgcgagttctgtgaacagcgcttcactgagaaggggcccctcctgaggcacgtggccagccgccatcaggagggccggccccacttctgccagatatgcggcaagaccttcaaagccgtggagcaactgcgtgtgcacgtcagacggcacaagggggtgaggaagtttgagtgcaccgagtgtggctacaagtttacccgacaggcccacctgcggaggcacatggagatccacgaccgggtagagaactacaacccgcggcagcgcaagctccgcaacctgatcatcgaggacgagaagatggtggtggtggcgctgcagccgcctgcagagctggaggtgggctcggcggaggtcattgtggagtccctggcccagggcggcctggcctcccagctccccggccagagactgtgtgcagaggagagcttcaccggcccaggtgtcctggagccctccctcatcatcacagctgctgtccccgaggactgtgacacatagcccattctggccaccagagcccacttggccccacccctcaataaaccgtgtggctttggactctcgtaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.transcriptForward.setGeneSymbol("ZBTB48");
		this.infoForward = transcriptForward.build();
		// RefSeq: NM_005341.3

		this.transcriptReverse = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bgu.3	chr1	-	23685940	23696357	23688461	23694498	4	23685940,23693534,23694465,23695858,	23689714,23693661,23694558,23696357,	Q9C0F3	uc001bgu.3");
		this.transcriptReverse
				.setSequence("aataagctgctatattctttttccatcacttccctctccaaggctacagcgagctgggagctcttccccacgcagaatgcctgctttccccagtgctcgacttccattgtctaattccctcatcctggctggggaaagggagagctgcgagtcctcccgttccgaggaactccagctgaatgcagcttagttgctggtggtttctcggccagcctctgtggtctcagggatctgcctatgagcctgtggtttctgagctgcctgcgagtctgaggcctcgggaatctgagtctttaggatcagcctacgatatctgggcttcgcctgcaagtctacgaattcgagatctacctgcgggtctgagacctccgggacctgcccgtgctctctagaatcttcctgaacgccaggtctgagagaacgctgcggctctggaacccgttcgcggtctctcaggttttggagacgacgatctagtggatcttttgcgggacaggagcgctgtctgctagctgcttttcctgctctctctccctggaggcgaacccttgtgctcgagatggcagccaccctgctcatggctgggtcccaggcacctgtgacgtttgaagatatggccatgtatctcacccgggaagaatggagacctctggacgctgcacagagggacctttaccgggatgttatgcaggagaattatggaaatgttgtctcactagattttgagatcaggagtgagaacgaggtaaatcccaagcaagagattagtgaagatgtacaatttgggactacatctgaaagacctgctgagaatgctgaggaaaatcctgaaagtgaagagggctttgaaagcggagataggtcagaaagacaatggggagatttaacagcagaagagtgggtaagctatcctctccaaccagtcactgatctacttgtccacaaagaagtccacacaggcatccgctatcatatatgttctcattgtggaaaggccttcagtcagatctcagaccttaatcgacatcagaagacccacactggagacagaccctataaatgttatgaatgtggaaaaggcttcagtcgcagctcacaccttattcagcatcaaagaacacatactggggagaggccttatgactgtaacgagtgtgggaaaagttttggaagaagttctcacctgattcagcatcagacaatccacactggagagaagcctcacaaatgtaatgagtgtggaaaaagtttctgccgtctctctcacctaatccaacaccaaaggacccacagtggtgagaaaccctatgagtgtgaggagtgtgggaaaagcttcagccggagctctcacctagctcagcaccagaggacccacacgggtgagaaaccttatgaatgtaacgaatgtggccgaggcttcagtgagagatctgatctcatcaaacactatcgagtccacacaggggagaggccctacaagtgtgatgagtgtgggaagaatttcagtcagaactccgaccttgtgcgtcatcgcagagcccacacgggagagaagccataccactgtaacgaatgtggggaaaatttcagccgcatctcacacttggttcagcaccagagaactcacactggagagaagccatatgaatgcaatgcttgtgggaaaagcttcagccggagctctcatctcatcacacaccagaaaattcacactggagagaagccttatgagtgtaatgagtgttggcgaagctttggtgaaaggtcagatctaattaaacatcagagaacccacacaggggagaagccctacgagtgtgtgcagtgtgggaaaggtttcacccagagctccaacctcatcacacatcaaagagttcacacgggagagaaaccttatgaatgtaccgaatgtgagaagagtttcagcaggagctcagctcttattaaacataagagagttcatacggactaagctgtaattatgatggctgagaaatgattcatttgaagatacaattttatttgatatcaatgaacgccctcaagactgagctgcttttatcatactctcctagttgtgggccacgatttaaaccatcagagatgacaagccatttgaaattctgaccctcagctttgggaatgttatctcctccaaaatggtgatttttattcactcaatgggttacttcattaaaagcagccccacaagtaactggaaatctgaagaccaggggacaaatgctggtgaatgcttaggcctggaaatggagtaaatctttcaatgttattttctcccatccttggcccaaggaactatgctaagtgaaacgtgggactgtaatagggtggtaatggctgctttggaaaaaggcaactagagactctgcctaaattgccacacctattcacacaccatagtagttgggcacacacatcttcccttccaaagggctttttccttgagttgctcatgcatttgtatcttttccatcttcctgagggcaagattttgcacgatgaaggcaatgattgtaacttttctccttctcattgtttctaattagctcctttaaagcttgcatctttgtgaaggctaactgaagatacggttggaaaggaaaaatgagacacaggtttggggaccaaggacccatcaatgatggtgactttagcagaagatgcccacagttattactgccattaatcagatttatgaattttctttggggatcactatagggaatattgtatagaaaatatcttcaagaaaagataggaccatcagtgacagttaagtgtaaggagcaagtggaattgagtccttcagggaaggaaccacagagtcccttcccaaggaatgtaggtcgtttctgtgttctttcccttctaatctttaagatcaactcttcctatcctgctaactctaagatttgataagggccacatcccagtgtttatcttagcttgcatcagggcatgtgtatgtacagtaatgtgtattcctgtggtttttctaatagaaactgaatttacagagacttagcatgttcttgggtgatgtgagtcatgtgacagaagtacagacataactccaatgtgagaaatgtccttttttcattatggaaaataatttaaacactagtgctttagtgtgcactctcctgtaaggtctgtctttgtacagagctaagcacttgtttgtatgtgtttgtcaattgtggaagataatgaccagacaaataggtcgattgtcctattctcagaatgaattatcttctatggtaatgaagaactctttggcttagtcagaaggaattaacgaacctcggtaggaatgtatttccatcctcccaccctacagatataagaggttaaaataacagttcgcccaatttaagcccagtagtgtcagttttcctaatctcagtccaggtaggaattaagaaatatctcaagtgttgatgctatccaagcatgttggggtggaagggaattggtgcccagaaaatgggactggagtgaggaatatcttttcttttgagagtacccccagtttatttctactgtgctttattgctactgttctttattgtgaatgttgtaacattttaaaaatgttttgccatagctttttaggacttggtgttaaaggagccagtggtctctctgggtgggtactataatgagttattgtgacccacagctgtgtgggaccacatcacttgttaataacacaacctttaaagtaacccatcttccaggggggttccttcatgttgccactcctttttaaggacaaactcaggcaaggagcatgtttttttgttatttacaaaatctagcagactgtgggtatccatattttaattgtcgggtgacacatgttcttggtaactaaactcaaatatgtcttttctcatatatgttgctgatggttttaataaatgtcaaagttctcctgttgcttctgtgagccactatgggtatcagcttgggagtggccatagatgaccgcatttccatgacctaactgtatttcacccccttttccttccctactgttcttgccccaccccaaccagttcctgctgctgcttttggcttcttggaggtgaagggcttaaaacaaggcttctaagcacccagctatctccatacatgaacaatctagctgggaaacttaagggacaagggccacaccagctgtctcctctttctgccaattgttgcccgtttgctgtgttgaactttgtatagaactcatgcatcagactcccttcactaatgctttttgcatgccttctgctcccaagtccctggctgcctctgcacatcccgtgaacactttgtgcctgttttctatggttgtggagaattaatgaacaaatcaatatgtagaacagttttccttatggtattggtcacagttatcctagtgtttgtattattctaacaatattctataattaaaaatataatttttaaagtca"
						.toUpperCase());
		this.transcriptReverse.setGeneSymbol("ZNF436");
		this.infoReverse = transcriptReverse.build();
		// RefSeq: NM_001077195.1

		this.ncTranscriptForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001aaa.3	chr1	+	11873	14409	11873	11873	3	11873,12612,13220,	12227,12721,14409,		uc001aaa.3");
		this.ncTranscriptForward
				.setSequence("cttgccgtcagccttttctttgacctcttctttctgttcatgtgtatttgctgtctcttagcccagacttcccgtgtcctttccaccgggcctttgagaggtcacagggtcttgatgctgtggtcttcatctgcaggtgtctgacttccagcaactgctggcctgtgccagggtgcaagctgagcactggagtggagttttcctgtggagaggagccatgcctagagtgggatgggccattgttcatcttctggcccctgttgtctgcatgtaacttaataccacaaccaggcataggggaaagattggaggaaagatgagtgagagcatcaacttctctcacaacctaggccagtgtgtggtgatgccaggcatgcccttccccagcatcaggtctccagagctgcagaagacgacggccgacttggatcacactcttgtgagtgtccccagtgttgcagaggcagggccatcaggcaccaaagggattctgccagcatagtgctcctggaccagtgatacacccggcaccctgtcctggacacgctgttggcctggatctgagccctggtggaggtcaaagccacctttggttctgccattgctgctgtgtggaagttcactcctgccttttcctttccctagagcctccaccaccccgagatcacatttctcactgccttttgtctgcccagtttcaccagaagtaggcctcttcctgacaggcagctgcaccactgcctggcgctgtgcccttcctttgctctgcccgctggagacggtgtttgtcatgggcctggtctgcagggatcctgctacaaaggtgaaacccaggagagtgtggagtccagagtgttgccaggacccaggcacaggcattagtgcccgttggagaaaacaggggaatcccgaagaaatggtgggtcctggccatccgtgagatcttcccagggcagctcccctctgtggaatccaatctgtcttccatcctgcgtggccgagggccaggcttctcactgggcctctgcaggaggctgccatttgtcctgcccaccttcttagaagcgagacggagcagacccatctgctactgccctttctataataactaaagttagctgccctggactattcaccccctagtctcaatttaagaagatccccatggccacagggcccctgcctgggggcttgtcacctcccccaccttcttcctgagtcattcctgcagccttgctccctaacctgccccacagccttgcctggatttctatctccctggcttggtgccagttcctccaagtcgatggcacctccctccctctcaaccacttgagcaaactccaagacatcttctaccccaacaccagcaattgtgccaagggccattaggctctcagcatgactatttttagagaccccgtgtctgtcactgaaaccttttttgtgggagactattcctcccatctgcaacagctgcccctgctgactgcccttctctcctccctctcatcccagagaaacaggtcagctgggagcttctgcccccactgcctagggaccaacaggggcaggaggcagtcactgaccccgagacgtttgcatcctgcacagctagagatcctttattaaaagcacactgttggtttctg");
		this.ncInfoForward = ncTranscriptForward.build();
	}

	@Test
	public void testForwardUpstreamOfTranscription() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("-204",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640061, PositionType.ZERO_BASED)));
		Assert.assertEquals("-225",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640040, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardDownstreamOfTranscription() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("*69",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6649340, PositionType.ZERO_BASED)));
		Assert.assertEquals("*88",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6649359, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardUpstreamOfCDS() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("-1",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640668, PositionType.ZERO_BASED)));
		Assert.assertEquals("-21",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640648, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardDownstreamOfCDS() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("*1",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6649272, PositionType.ZERO_BASED)));
		Assert.assertEquals("*8",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6649279, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardCDSExon() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("381",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6641049, PositionType.ZERO_BASED)));
		Assert.assertEquals("360",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6641028, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardCDSIntron() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("691-217",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6641900, PositionType.ZERO_BASED)));
		Assert.assertEquals("690+142",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6641500, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardNonCDSExon() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("-39",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640630, PositionType.ZERO_BASED)));
		Assert.assertEquals("-14",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640655, PositionType.ZERO_BASED)));
		Assert.assertEquals("-14",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640655, PositionType.ZERO_BASED)));
		Assert.assertEquals("-115",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640150, PositionType.ZERO_BASED)));
	}

	@Test
	public void testForwardNonCDSIntron() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoForward);
		Assert.assertEquals("-69-20",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640580, PositionType.ZERO_BASED)));
		Assert.assertEquals("-70+5",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 6640200, PositionType.ZERO_BASED)));
	}

	@Test
	public void testReverseUpstreamOfTranscription() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoReverse);
		Assert.assertEquals("-562",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 23696359, PositionType.ZERO_BASED)));
		Assert.assertEquals("-582",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 23696379, PositionType.ZERO_BASED)));
	}

	@Test
	public void testReverseDownstreamOfTranscription() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.infoReverse);
		Assert.assertEquals("*2522",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 23685939, PositionType.ZERO_BASED)));
		Assert.assertEquals("*2542",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 23685919, PositionType.ZERO_BASED)));
	}

	@Test
	public void testNonCodingForwardExon() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.ncInfoForward);
		Assert.assertEquals("1",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 11873, PositionType.ZERO_BASED)));
		Assert.assertEquals("355",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 12612, PositionType.ZERO_BASED)));
	}

	@Test
	public void testNonCodingForwardIntron() {
		HGVSPositionBuilder builderForward = new HGVSPositionBuilder(this.ncInfoForward);
		Assert.assertEquals("354+1",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 12227, PositionType.ZERO_BASED)));
		Assert.assertEquals("355-1",
				builderForward.getCDNAPosStr(new GenomePosition(refDict, '+', 1, 12611, PositionType.ZERO_BASED)));
	}
}
