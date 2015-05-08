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

}
