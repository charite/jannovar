package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class TranscriptSequenceChangeHelperTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript builder for the forward strand */
	TranscriptModelBuilder builderForward;
	/** transcript info for the forward strand */
	TranscriptModel infoForward;

	/** projector helper for forward strand transcript info */
	TranscriptProjectionDecorator projectorForward;
	/** the helper under tests for forward strand */
	TranscriptSequenceChangeHelper helperForward;

	/** transcript builder for the forward strand */
	TranscriptModelBuilder builderReverse;
	/** transcript info for the forward strand */
	TranscriptModel infoReverse;

	/** projector helper for forward strand transcript info */
	TranscriptProjectionDecorator projectorReverse;
	/** the helper under tests for forward strand */
	TranscriptSequenceChangeHelper helperReverse;

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
		this.infoForward = builderForward.build();
		// RefSeq: NM_005341.3
		this.helperForward = new TranscriptSequenceChangeHelper(infoForward);
		this.projectorForward = new TranscriptProjectionDecorator(infoForward);

		this.builderReverse = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001bgu.3\tchr1\t-\t23685940\t23696357\t23688461\t23694498\t4"
						+ "\t23685940,23693534,23694465,23695858,\t23689714,23693661,23694558,"
						+ "23696357,\tQ9C0F3\tuc001bgu.3");
		this.builderReverse
				.setSequence("aataagctgctatattctttttccatcacttccctctccaaggctacagcgagctgggagctcttccccacgcagaatgcctgctttccccagtgctcgacttccattgtctaattccctcatcctggctggggaaagggagagctgcgagtcctcccgttccgaggaactccagctgaatgcagcttagttgctggtggtttctcggccagcctctgtggtctcagggatctgcctatgagcctgtggtttctgagctgcctgcgagtctgaggcctcgggaatctgagtctttaggatcagcctacgatatctgggcttcgcctgcaagtctacgaattcgagatctacctgcgggtctgagacctccgggacctgcccgtgctctctagaatcttcctgaacgccaggtctgagagaacgctgcggctctggaacccgttcgcggtctctcaggttttggagacgacgatctagtggatcttttgcgggacaggagcgctgtctgctagctgcttttcctgctctctctccctggaggcgaacccttgtgctcgagatggcagccaccctgctcatggctgggtcccaggcacctgtgacgtttgaagatatggccatgtatctcacccgggaagaatggagacctctggacgctgcacagagggacctttaccgggatgttatgcaggagaattatggaaatgttgtctcactagattttgagatcaggagtgagaacgaggtaaatcccaagcaagagattagtgaagatgtacaatttgggactacatctgaaagacctgctgagaatgctgaggaaaatcctgaaagtgaagagggctttgaaagcggagataggtcagaaagacaatggggagatttaacagcagaagagtgggtaagctatcctctccaaccagtcactgatctacttgtccacaaagaagtccacacaggcatccgctatcatatatgttctcattgtggaaaggccttcagtcagatctcagaccttaatcgacatcagaagacccacactggagacagaccctataaatgttatgaatgtggaaaaggcttcagtcgcagctcacaccttattcagcatcaaagaacacatactggggagaggccttatgactgtaacgagtgtgggaaaagttttggaagaagttctcacctgattcagcatcagacaatccacactggagagaagcctcacaaatgtaatgagtgtggaaaaagtttctgccgtctctctcacctaatccaacaccaaaggacccacagtggtgagaaaccctatgagtgtgaggagtgtgggaaaagcttcagccggagctctcacctagctcagcaccagaggacccacacgggtgagaaaccttatgaatgtaacgaatgtggccgaggcttcagtgagagatctgatctcatcaaacactatcgagtccacacaggggagaggccctacaagtgtgatgagtgtgggaagaatttcagtcagaactccgaccttgtgcgtcatcgcagagcccacacgggagagaagccataccactgtaacgaatgtggggaaaatttcagccgcatctcacacttggttcagcaccagagaactcacactggagagaagccatatgaatgcaatgcttgtgggaaaagcttcagccggagctctcatctcatcacacaccagaaaattcacactggagagaagccttatgagtgtaatgagtgttggcgaagctttggtgaaaggtcagatctaattaaacatcagagaacccacacaggggagaagccctacgagtgtgtgcagtgtgggaaaggtttcacccagagctccaacctcatcacacatcaaagagttcacacgggagagaaaccttatgaatgtaccgaatgtgagaagagtttcagcaggagctcagctcttattaaacataagagagttcatacggactaagctgtaattatgatggctgagaaatgattcatttgaagatacaattttatttgatatcaatgaacgccctcaagactgagctgcttttatcatactctcctagttgtgggccacgatttaaaccatcagagatgacaagccatttgaaattctgaccctcagctttgggaatgttatctcctccaaaatggtgatttttattcactcaatgggttacttcattaaaagcagccccacaagtaactggaaatctgaagaccaggggacaaatgctggtgaatgcttaggcctggaaatggagtaaatctttcaatgttattttctcccatccttggcccaaggaactatgctaagtgaaacgtgggactgtaatagggtggtaatggctgctttggaaaaaggcaactagagactctgcctaaattgccacacctattcacacaccatagtagttgggcacacacatcttcccttccaaagggctttttccttgagttgctcatgcatttgtatcttttccatcttcctgagggcaagattttgcacgatgaaggcaatgattgtaacttttctccttctcattgtttctaattagctcctttaaagcttgcatctttgtgaaggctaactgaagatacggttggaaaggaaaaatgagacacaggtttggggaccaaggacccatcaatgatggtgactttagcagaagatgcccacagttattactgccattaatcagatttatgaattttctttggggatcactatagggaatattgtatagaaaatatcttcaagaaaagataggaccatcagtgacagttaagtgtaaggagcaagtggaattgagtccttcagggaaggaaccacagagtcccttcccaaggaatgtaggtcgtttctgtgttctttcccttctaatctttaagatcaactcttcctatcctgctaactctaagatttgataagggccacatcccagtgtttatcttagcttgcatcagggcatgtgtatgtacagtaatgtgtattcctgtggtttttctaatagaaactgaatttacagagacttagcatgttcttgggtgatgtgagtcatgtgacagaagtacagacataactccaatgtgagaaatgtccttttttcattatggaaaataatttaaacactagtgctttagtgtgcactctcctgtaaggtctgtctttgtacagagctaagcacttgtttgtatgtgtttgtcaattgtggaagataatgaccagacaaataggtcgattgtcctattctcagaatgaattatcttctatggtaatgaagaactctttggcttagtcagaaggaattaacgaacctcggtaggaatgtatttccatcctcccaccctacagatataagaggttaaaataacagttcgcccaatttaagcccagtagtgtcagttttcctaatctcagtccaggtaggaattaagaaatatctcaagtgttgatgctatccaagcatgttggggtggaagggaattggtgcccagaaaatgggactggagtgaggaatatcttttcttttgagagtacccccagtttatttctactgtgctttattgctactgttctttattgtgaatgttgtaacattttaaaaatgttttgccatagctttttaggacttggtgttaaaggagccagtggtctctctgggtgggtactataatgagttattgtgacccacagctgtgtgggaccacatcacttgttaataacacaacctttaaagtaacccatcttccaggggggttccttcatgttgccactcctttttaaggacaaactcaggcaaggagcatgtttttttgttatttacaaaatctagcagactgtgggtatccatattttaattgtcgggtgacacatgttcttggtaactaaactcaaatatgtcttttctcatatatgttgctgatggttttaataaatgtcaaagttctcctgttgcttctgtgagccactatgggtatcagcttgggagtggccatagatgaccgcatttccatgacctaactgtatttcacccccttttccttccctactgttcttgccccaccccaaccagttcctgctgctgcttttggcttcttggaggtgaagggcttaaaacaaggcttctaagcacccagctatctccatacatgaacaatctagctgggaaacttaagggacaagggccacaccagctgtctcctctttctgccaattgttgcccgtttgctgtgttgaactttgtatagaactcatgcatcagactcccttcactaatgctttttgcatgccttctgctcccaagtccctggctgcctctgcacatcccgtgaacactttgtgcctgttttctatggttgtggagaattaatgaacaaatcaatatgtagaacagttttccttatggtattggtcacagttatcctagtgtttgtattattctaacaatattctataattaaaaatataatttttaaagtca"
						.toUpperCase());
		this.builderReverse.setGeneSymbol("Q9C0F3");
		this.infoReverse = builderReverse.build();
		// RefSeq: NM_001077195.1
		this.helperReverse = new TranscriptSequenceChangeHelper(infoReverse);
		this.projectorReverse = new TranscriptProjectionDecorator(infoReverse);
	}

	@Test
	public void testTXSNVInExonForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642119,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.getSequence());
		expectedBuilder.setCharAt(895, 'C');
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXSNVInIntronForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6648257,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.getSequence(), resultTranscript);
	}

	@Test
	public void testTXSNVOutsideTXForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.getSequence(), resultTranscript);
	}

	@Test
	public void testTXInsertionInExonForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642119,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.getSequence());
		expectedBuilder.insert(895, "CTTG");
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXInsertionInIntronForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6648257,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.getSequence(), resultTranscript);
	}

	@Test
	public void testTXInsertionOutsideTXForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.getSequence(), resultTranscript);
	}

	@Test
	public void testTXDeletionInExonForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642120,
				PositionType.ZERO_BASED), "CTT", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.getSequence());
		expectedBuilder.delete(896, 899);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXDeletionInIntronForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6647530,
				PositionType.ZERO_BASED), "CTTG", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.getSequence(), resultTranscript);
	}

	@Test
	public void testTXDeletionOutsideTXForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640058,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.getSequence(), resultTranscript);
	}

	@Test
	public void testTXDeletionSpanningIntoTXFromTheLeftForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640060,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.getSequence());
		expectedBuilder.delete(0, 2);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXDeletionSpanningIntoTXFromTheRightForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649338,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.getSequence());
		expectedBuilder.delete(infoForward.transcriptLength() - 2, infoForward.transcriptLength());
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSSNVInExonForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642119,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorForward.getTranscriptStartingAtCDS());
		expectedBuilder.setCharAt(692, 'C');
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSSNVInIntronForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6648257,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorForward.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSSNVOutsideCDSForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorForward.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSInsertionInExonForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642119,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorForward.getTranscriptStartingAtCDS());
		expectedBuilder.insert(692, "CTTG");
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSInsertionInIntronForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6648257,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorForward.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSInsertionOutsideCDSForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorForward.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSDeletionInExonForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642120,
				PositionType.ZERO_BASED), "CTT", "");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorForward.getTranscriptStartingAtCDS());
		expectedBuilder.delete(693, 696);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSDeletionInIntronForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640665,
				PositionType.ZERO_BASED), "CTTG", "");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorForward.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSDeletionOutsideCDSForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649272,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorForward.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSDeletionSpanningIntoCDSFromTheLeftForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640667,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorForward.getTranscriptStartingAtCDS());
		expectedBuilder.delete(0, 2);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSDeletionSpanningIntoCDSFromTheRightForward() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649270,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperForward.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorForward.getTranscriptStartingAtCDS());
		expectedBuilder.delete(infoForward.cdsTranscriptLength() - 2, infoForward.cdsTranscriptLength());
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXSNVInExonReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23696356,
				PositionType.ZERO_BASED), "T", "A");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoReverse.getSequence());
		expectedBuilder.setCharAt(0, 'T');
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXSNVInIntronReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23689715,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);
		Assert.assertEquals(infoReverse.getSequence(), resultTranscript);
	}

	@Test
	public void testTXSNVOutsideTXReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23685939,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);
		Assert.assertEquals(infoReverse.getSequence(), resultTranscript);
	}

	@Test
	public void testTXInsertionInExonReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23696356,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoReverse.getSequence());
		expectedBuilder.insert(1, "CAAG");
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXInsertionInIntronReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23689715,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);
		Assert.assertEquals(infoReverse.getSequence(), resultTranscript);
	}

	@Test
	public void testTXInsertionOutsideTXReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23685939,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);
		Assert.assertEquals(infoReverse.getSequence(), resultTranscript);
	}

	@Test
	public void testTXDeletionInExonReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23696354,
				PositionType.ZERO_BASED), "TAT", "");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoReverse.getSequence());
		expectedBuilder.delete(2, 5);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXDeletionInIntronReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23693663,
				PositionType.ZERO_BASED), "CTT", "");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);
		Assert.assertEquals(infoReverse.getSequence(), resultTranscript);
	}

	@Test
	public void testTXDeletionOutsideTXReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23696357,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);
		Assert.assertEquals(infoReverse.getSequence(), resultTranscript);
	}

	@Test
	public void testTXDeletionSpanningIntoTXFromTheLeftReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23685938,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoReverse.getSequence());
		expectedBuilder.delete(infoReverse.transcriptLength() - 2, infoReverse.transcriptLength());
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testTXDeletionSpanningIntoTXFromTheRightReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23696355,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperReverse.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoReverse.getSequence());
		expectedBuilder.delete(0, 2);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSSNVInExonReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694467,
				PositionType.ZERO_BASED), "C", "A");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorReverse.getTranscriptStartingAtCDS());
		expectedBuilder.setCharAt(30, 'T');
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSSNVInIntronReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694465,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorReverse.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSSNVOutsideCDSReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694498,
				PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorReverse.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSInsertionInExonReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694497,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorReverse.getTranscriptStartingAtCDS());
		expectedBuilder.insert(1, "CAAG");
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSInsertionInIntronReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23693662,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorReverse.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSInsertionOutsideCDSReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694498,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorReverse.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSDeletionInExonReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694495,
				PositionType.ZERO_BASED), "CT", "");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorReverse.getTranscriptStartingAtCDS());
		expectedBuilder.delete(1, 3);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSDeletionInIntronReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23693663,
				PositionType.ZERO_BASED), "CTTG", "");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorReverse.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSDeletionOutsideCDSReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694498,
				PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);
		Assert.assertEquals(projectorReverse.getTranscriptStartingAtCDS(), resultTranscript);
	}

	@Test
	public void testCDSDeletionSpanningIntoCDSFromTheLeftReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688459,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorReverse.getTranscriptStartingAtCDS());
		expectedBuilder.delete(infoReverse.cdsTranscriptLength() - 2, infoReverse.cdsTranscriptLength());
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testCDSDeletionSpanningIntoCDSFromTheRightReverse() {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694496,
				PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperReverse.getCDSWithGenomeVariant(change);

		StringBuilder expectedBuilder = new StringBuilder(projectorReverse.getTranscriptStartingAtCDS());
		expectedBuilder.delete(0, 2);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

}
