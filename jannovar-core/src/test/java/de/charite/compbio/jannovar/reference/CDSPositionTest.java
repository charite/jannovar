package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CDSPositionTest {

	/**
	 * this test uses this static hg19 reference dictionary
	 */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/**
	 * transcript on forward strand
	 */
	TranscriptModelBuilder builderForward;
	/**
	 * transcript on reverse strand
	 */
	TranscriptModelBuilder builderReverse;
	/**
	 * transcript info for the forward strand
	 */
	TranscriptModel infoForward;
	/**
	 * transcript info for the reverse strand
	 */
	TranscriptModel infoReverse;

	@BeforeEach
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
	public void testConstructorDefaultPositionType() {
		CDSPosition pos = new CDSPosition(this.infoForward, 10);
		Assertions.assertEquals(pos.getTranscript(), this.infoForward);
		Assertions.assertEquals(pos.getPos(), 10);
	}

	@Test
	public void testConstructorExplicitPositionType() {
		CDSPosition pos = new CDSPosition(this.infoForward, 10, PositionType.ZERO_BASED);
		Assertions.assertEquals(pos.getTranscript(), this.infoForward);
		Assertions.assertEquals(pos.getPos(), 10);
	}

}
