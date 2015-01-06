package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the TranscriptInfo class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptInfoTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript builder for the forward strand */
	TranscriptInfoBuilder builderForward;
	/** transcript builder for the reverse strand */
	TranscriptInfoBuilder builderReverse;
	/** transcript info for the forward strand */
	TranscriptInfo infoForward;
	/** transcript info for the reverse strand */
	TranscriptInfo infoReverse;

	@Before
	public void setUp() {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc009vmz.1\tchr1\t+\t11539294\t11541938\t11539294\t11539294\t2\t"
						+ "11539294,11541314,\t11539429,11541938,\tuc009vmz.1");
		this.infoForward = builderForward.build();
		this.builderReverse = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc009vjr.2\tchr1\t-\t893648\t894679\t894010\t894620\t2\t"
						+ "893648,894594,\t894461,894679,\tuc009vjr.2");
		this.infoReverse = builderReverse.build();
	}

	@Test
	public void testForwardTranscript() {
		TranscriptInfo info = builderForward.build();
		Assert.assertEquals(info, infoForward);
	}

	@Test
	public void testReverseTranscript() {
		TranscriptInfo info = builderReverse.build();
		Assert.assertEquals(info, infoReverse);
	}

}
