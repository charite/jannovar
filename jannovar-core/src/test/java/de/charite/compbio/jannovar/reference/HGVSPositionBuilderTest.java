package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class HGVSPositionBuilderTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript on forward strand */
	TranscriptModelBuilder transcriptForward;
	/** transcript on reverse strand */
	TranscriptModelBuilder transcriptReverse;
	/** non-coding transcript on forward strand */
	TranscriptModelBuilder ncTranscriptForward;
	/** transcript info on forward strand */
	TranscriptModel infoForward;
	/** transcript info on reverse strand */
	TranscriptModel infoReverse;
	/** non-coding transcript info on forward strand */
	TranscriptModel ncInfoForward;

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
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"-204",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640061, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-225",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640040, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardDownstreamOfTranscription() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"*69",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6649340, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"*88",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6649359, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardUpstreamOfCDS() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"-1",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640668, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-21",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640648, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardDownstreamOfCDS() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"*1",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6649272, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"*8",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6649279, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardCDSExon() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"381",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6641049, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"360",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6641028, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardCDSIntron() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"691-217",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6641900, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"690+142",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6641500, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardNonCDSExon() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"-39",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640630, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-14",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640655, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-14",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640655, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-115",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640150, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testForwardNonCDSIntron() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoForward);
		Assert.assertEquals(
				"-69-20",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640580, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-70+5",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 6640200, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testReverseUpstreamOfTranscription() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoReverse);
		Assert.assertEquals(
				"-562",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 23696359, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"-582",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 23696379, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testReverseDownstreamOfTranscription() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.infoReverse);
		Assert.assertEquals(
				"*2522",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 23685939, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"*2542",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 23685919, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testNonCodingForwardExon() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.ncInfoForward);
		Assert.assertEquals(
				"1",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 11873, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"355",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 12612, PositionType.ZERO_BASED)).toHGVSString());
	}

	@Test
	public void testNonCodingForwardIntron() {
		NucleotidePointLocationBuilder builderForward = new NucleotidePointLocationBuilder(this.ncInfoForward);
		Assert.assertEquals(
				"354+1",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 12227, PositionType.ZERO_BASED)).toHGVSString());
		Assert.assertEquals(
				"355-1",
				builderForward.getNucleotidePointLocation(
						new GenomePosition(refDict, Strand.FWD, 1, 12611, PositionType.ZERO_BASED)).toHGVSString());
	}
}
