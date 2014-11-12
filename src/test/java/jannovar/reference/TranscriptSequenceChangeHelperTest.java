package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranscriptSequenceChangeHelperTest {

	/** transcript on forward strand */
	TranscriptModel transcriptForward;
	/** transcript info on forward strand */
	TranscriptInfo infoForward;

	/** the helper under tests for forward strand */
	TranscriptSequenceChangeHelper helperForward;

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
		this.helperForward = new TranscriptSequenceChangeHelper(infoForward);
	}


	@Test
	public void testSNVInExonForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6642119, PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.sequence);
		expectedBuilder.setCharAt(895, 'C');
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testSNVInIntronForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6648257, PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.sequence, resultTranscript);
	}

	@Test
	public void testSNVOutsideTXForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640062, PositionType.ZERO_BASED), "A", "C");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.sequence, resultTranscript);
	}

	@Test
	public void testInsertionInExonForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6642119, PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.sequence);
		expectedBuilder.insert(895, "CTTG");
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testInsertionInIntronForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6648257, PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.sequence, resultTranscript);
	}

	@Test
	public void testInsertionOutsideTXForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640062, PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.sequence, resultTranscript);
	}

	@Test
	public void testDeletionInExonForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6642120, PositionType.ZERO_BASED), "CTT", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.sequence);
		expectedBuilder.delete(896, 899);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testDeletionInIntronForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6647530, PositionType.ZERO_BASED), "CTTG", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.sequence, resultTranscript);
	}

	@Test
	public void testDeletionOutsideTXForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640058, PositionType.ZERO_BASED), "", "CTTG");
		String resultTranscript = helperForward.getTranscriptWithChange(change);
		Assert.assertEquals(infoForward.sequence, resultTranscript);
	}

	@Test
	public void testDeletionSpanningIntoTXFromTheLeftForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6640060, PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.sequence);
		expectedBuilder.delete(0, 2);
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

	@Test
	public void testDeletionSpanningIntoTXFromTheRightForward() {
		GenomeChange change = new GenomeChange(new GenomePosition('+', 1, 6649338, PositionType.ZERO_BASED), "TTTT", "");
		String resultTranscript = helperForward.getTranscriptWithChange(change);

		StringBuilder expectedBuilder = new StringBuilder(infoForward.sequence);
		expectedBuilder.delete(2336, infoForward.sequence.length());
		Assert.assertEquals(expectedBuilder.toString(), resultTranscript);
	}

}
