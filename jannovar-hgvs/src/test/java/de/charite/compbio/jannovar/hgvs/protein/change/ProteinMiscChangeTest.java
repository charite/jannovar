package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

public class ProteinMiscChangeTest {

	@Test
	public void testDifficultToPredictTest() {
		Assert.assertEquals("?", ProteinMiscChange.build(false, ProteinMiscChangeType.DIFFICULT_TO_PREDICT)
				.toHGVSString());
		Assert.assertEquals("?", ProteinMiscChange.build(true, ProteinMiscChangeType.DIFFICULT_TO_PREDICT)
				.toHGVSString());
	}

	@Test
	public void testNoChangeTest() {
		Assert.assertEquals("=", ProteinMiscChange.build(false, ProteinMiscChangeType.NO_CHANGE).toHGVSString());
		Assert.assertEquals("(=)", ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE).toHGVSString());
	}

	@Test
	public void testNoProteinTest() {
		Assert.assertEquals("0", ProteinMiscChange.build(false, ProteinMiscChangeType.NO_PROTEIN).toHGVSString());
		Assert.assertEquals("0?", ProteinMiscChange.build(true, ProteinMiscChangeType.NO_PROTEIN).toHGVSString());
	}

}
