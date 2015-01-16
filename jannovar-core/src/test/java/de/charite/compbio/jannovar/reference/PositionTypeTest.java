package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.reference.PositionType;

/**
 * We only need a few tests so we can double-check if the PositionType enum changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class PositionTypeTest {
	@Test
	public void test() {
		Assert.assertEquals(PositionType.values().length, 2);
		Assert.assertEquals(PositionType.ZERO_BASED, PositionType.values()[0]);
		Assert.assertEquals(PositionType.ONE_BASED, PositionType.values()[1]);
	}
}
