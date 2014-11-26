package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CDSPositionTest {

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
		CDSPosition pos = new CDSPosition(this.transcriptForward, 10);
		Assert.assertEquals(pos.transcript, this.transcriptForward);
		Assert.assertEquals(pos.pos, 10);
		Assert.assertEquals(pos.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		CDSPosition pos = new CDSPosition(this.transcriptForward, 10, PositionType.ZERO_BASED);
		Assert.assertEquals(pos.transcript, this.transcriptForward);
		Assert.assertEquals(pos.pos, 10);
		Assert.assertEquals(pos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		CDSPosition onePos = new CDSPosition(this.transcriptForward, 23, PositionType.ONE_BASED);
		CDSPosition zeroPos = new CDSPosition(onePos, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroPos.transcript, this.transcriptForward);
		Assert.assertEquals(zeroPos.pos, 22);
		Assert.assertEquals(zeroPos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		CDSPosition onePos = new CDSPosition(this.transcriptForward, 23, PositionType.ZERO_BASED);
		CDSPosition zeroPos = new CDSPosition(onePos, PositionType.ONE_BASED);

		Assert.assertEquals(zeroPos.transcript, this.transcriptForward);
		Assert.assertEquals(zeroPos.pos, 24);
		Assert.assertEquals(zeroPos.positionType, PositionType.ONE_BASED);
	}

}
