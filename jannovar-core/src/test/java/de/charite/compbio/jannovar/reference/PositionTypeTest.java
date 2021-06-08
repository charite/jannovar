package de.charite.compbio.jannovar.reference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * We only need a few tests so we can double-check if the PositionType enum changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class PositionTypeTest {
	@Test
	public void test() {
		Assertions.assertEquals(PositionType.values().length, 2);
		Assertions.assertEquals(PositionType.ZERO_BASED, PositionType.values()[0]);
		Assertions.assertEquals(PositionType.ONE_BASED, PositionType.values()[1]);
	}
}
