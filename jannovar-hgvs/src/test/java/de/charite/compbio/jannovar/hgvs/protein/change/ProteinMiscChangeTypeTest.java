package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProteinMiscChangeTypeTest {

	@Test
	public void testDifficultToPredict() {
		Assertions.assertEquals("?", ProteinMiscChangeType.DIFFICULT_TO_PREDICT.toHGVSString(true));
		Assertions.assertEquals("?", ProteinMiscChangeType.DIFFICULT_TO_PREDICT.toHGVSString(false));
	}

	@Test
	public void testNoChange() {
		Assertions.assertEquals("(=)", ProteinMiscChangeType.NO_CHANGE.toHGVSString(true));
		Assertions.assertEquals("=", ProteinMiscChangeType.NO_CHANGE.toHGVSString(false));
	}

	@Test
	public void testNoProtein() {
		Assertions.assertEquals("0?", ProteinMiscChangeType.NO_PROTEIN.toHGVSString(true));
		Assertions.assertEquals("0", ProteinMiscChangeType.NO_PROTEIN.toHGVSString(false));
	}

}
