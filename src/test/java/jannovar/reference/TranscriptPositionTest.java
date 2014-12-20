package jannovar.reference;

import jannovar.io.ReferenceDictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranscriptPositionTest {

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
		this.infoReverse = builderForward.build();
	}

	@Test
	public void testConstructorDefaultPositionType() {
		TranscriptPosition pos = new TranscriptPosition(this.infoForward, 10);
		Assert.assertEquals(pos.transcript, this.infoForward);
		Assert.assertEquals(pos.pos, 10);
		Assert.assertEquals(pos.positionType, PositionType.ONE_BASED);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		TranscriptPosition pos = new TranscriptPosition(this.infoForward, 10, PositionType.ZERO_BASED);
		Assert.assertEquals(pos.transcript, this.infoForward);
		Assert.assertEquals(pos.pos, 10);
		Assert.assertEquals(pos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		TranscriptPosition onePos = new TranscriptPosition(this.infoForward, 23, PositionType.ONE_BASED);
		TranscriptPosition zeroPos = new TranscriptPosition(onePos, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroPos.transcript, this.infoForward);
		Assert.assertEquals(zeroPos.pos, 22);
		Assert.assertEquals(zeroPos.positionType, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		TranscriptPosition onePos = new TranscriptPosition(this.infoForward, 23, PositionType.ZERO_BASED);
		TranscriptPosition zeroPos = new TranscriptPosition(onePos, PositionType.ONE_BASED);

		Assert.assertEquals(zeroPos.transcript, this.infoForward);
		Assert.assertEquals(zeroPos.pos, 24);
		Assert.assertEquals(zeroPos.positionType, PositionType.ONE_BASED);
	}

}
