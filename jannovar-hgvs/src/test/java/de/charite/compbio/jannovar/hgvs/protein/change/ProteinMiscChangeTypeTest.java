package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Test;

public class ProteinMiscChangeTypeTest {

	@Test
	public void testDifficultToPredict() {
		Assert.assertEquals("?", ProteinMiscChangeType.DIFFICULT_TO_PREDICT.toHGVSString(true));
		Assert.assertEquals("?", ProteinMiscChangeType.DIFFICULT_TO_PREDICT.toHGVSString(false));
	}

	@Test
	public void testNoChange() {
		Assert.assertEquals("(=)", ProteinMiscChangeType.NO_CHANGE.toHGVSString(true));
		Assert.assertEquals("=", ProteinMiscChangeType.NO_CHANGE.toHGVSString(false));
	}

	@Test
	public void testNoProtein() {
		Assert.assertEquals("0?", ProteinMiscChangeType.NO_PROTEIN.toHGVSString(true));
		Assert.assertEquals("0", ProteinMiscChangeType.NO_PROTEIN.toHGVSString(false));
	}

}
