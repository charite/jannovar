package de.charite.compbio.jannovar.mendel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModeOfInheritanceTest {
	/**
	 * Including mitochondrial, there are 6 modes of inheritance
	 */
	@Test
	public void testSize() {
		Assertions.assertEquals(6, ModeOfInheritance.values().length);
	}

}
