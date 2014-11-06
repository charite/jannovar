package jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CDSIntervalTest {

	/** transcript on forward strand */
	TranscriptModel transcriptForward;
	/** transcript on reverse strand */
	TranscriptModel transcriptReverse;

	@Before
	public void setUp() {
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc009vmz.1\tchr1\t+\t11539294\t11541938\t11539294\t11539294\t2\t"
						+ "11539294,11541314,\t11539429,11541938,\tuc009vmz.1");
		this.transcriptForward = TranscriptModelFactory
				.parseKnownGenesLine("uc009vjr.2\tchr1\t-\t893648\t894679\t894010\t894620\t2\t"
						+ "893648,894594,\t894461,894679,\tuc009vjr.2");
	}

	@Test
	public void testConstructorDefaultPositionType() {
		CDSInterval interval = new CDSInterval(this.transcriptForward, 23, 45);
		Assert.assertEquals(interval.getTranscript(), this.transcriptForward);
		Assert.assertEquals(interval.getBeginPos(), 23);
		Assert.assertEquals(interval.getEndPos(), 45);
		Assert.assertEquals(interval.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(interval.length(), 23);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		CDSInterval interval = new CDSInterval(this.transcriptForward, 23, 45, PositionType.ZERO_BASED);
		Assert.assertEquals(interval.getTranscript(), this.transcriptForward);
		Assert.assertEquals(interval.getBeginPos(), 23);
		Assert.assertEquals(interval.getEndPos(), 45);
		Assert.assertEquals(interval.getPositionType(), PositionType.ZERO_BASED);
		Assert.assertEquals(interval.length(), 22);
	}

	@Test
	public void testConstructorOneToZeroPositionType() {
		CDSInterval oneInterval = new CDSInterval(this.transcriptForward, 23, 45, PositionType.ONE_BASED);
		CDSInterval zeroInterval = new CDSInterval(oneInterval, PositionType.ZERO_BASED);

		Assert.assertEquals(zeroInterval.getTranscript(), this.transcriptForward);
		Assert.assertEquals(zeroInterval.getBeginPos(), 22);
		Assert.assertEquals(zeroInterval.getEndPos(), 45);
		Assert.assertEquals(zeroInterval.getPositionType(), PositionType.ZERO_BASED);
		Assert.assertEquals(zeroInterval.length(), 23);
	}

	@Test
	public void testConstructorZeroToOnePositionType() {
		CDSInterval zeroInterval = new CDSInterval(this.transcriptForward, 23, 45, PositionType.ZERO_BASED);
		CDSInterval oneInterval = new CDSInterval(zeroInterval, PositionType.ONE_BASED);

		Assert.assertEquals(oneInterval.getTranscript(), this.transcriptForward);
		Assert.assertEquals(oneInterval.getBeginPos(), 24);
		Assert.assertEquals(oneInterval.getEndPos(), 45);
		Assert.assertEquals(oneInterval.getPositionType(), PositionType.ONE_BASED);
		Assert.assertEquals(oneInterval.length(), 22);
	}
}
