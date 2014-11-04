package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranscriptPositionTest {

	/** transcript on forward strand */
	TranscriptModel transcriptForward;
	/** transcript on reverse strand */
	TranscriptModel transcriptReverse;

	@Before
	public void setUp() {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc009vmz.1\tchr1\t+\t11539294\t11541938\t11539294\t11539294\t2\t"
				+ "11539294,11541314,\t11539429,11541938,\tuc009vmz.1");
		this.transcriptReverse = TranscriptModelFactory
				.parseKnownGenesLine("uc009vjr.2\tchr1\t-\t893648\t894679\t894010\t894620\t2\t"
				+ "893648,894594,\t894461,894679,\tuc009vjr.2");
	}


	@Test
	public void testConstructorDefaultPositionType() {
		TranscriptPosition pos = new TranscriptPosition(this.transcriptForward, 10);
		Assert.assertEquals(pos.getTranscript(), this.transcriptForward);
		Assert.assertEquals(pos.getPos(), 10);
		Assert.assertEquals(pos.getPositionType(), PositionType.ONE_BASED);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		TranscriptPosition pos = new TranscriptPosition(this.transcriptForward, 10, PositionType.ZERO_BASED);
		Assert.assertEquals(pos.getTranscript(), this.transcriptForward);
		Assert.assertEquals(pos.getPos(), 10);
		Assert.assertEquals(pos.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		TranscriptPosition onePos = new TranscriptPosition(this.transcriptForward, 23, PositionType.ONE_BASED);
		TranscriptPosition zeroPos = new TranscriptPosition(onePos, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroPos.getTranscript(), this.transcriptForward);
		Assert.assertEquals(zeroPos.getPos(), 22);
		Assert.assertEquals(zeroPos.getPositionType(), PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		TranscriptPosition onePos = new TranscriptPosition(this.transcriptForward, 23, PositionType.ZERO_BASED);
		TranscriptPosition zeroPos = new TranscriptPosition(onePos, PositionType.ONE_BASED);

		Assert.assertEquals(zeroPos.getTranscript(), this.transcriptForward);
		Assert.assertEquals(zeroPos.getPos(), 24);
		Assert.assertEquals(zeroPos.getPositionType(), PositionType.ONE_BASED);
	}

}
