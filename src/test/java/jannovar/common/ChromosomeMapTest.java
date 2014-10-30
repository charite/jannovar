package jannovar.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the ChromosomeMap interface.
 */
public class ChromosomeMapTest {

	@Test
	public void testIdentifier2Chromosom() {
		Assert.assertEquals(ChromosomeMap.identifier2chromosom.get("chr1").intValue(), 1);
		Assert.assertEquals(ChromosomeMap.identifier2chromosom.get("chrM").intValue(), ChromosomeMap.M_CHROMOSOME);
		Assert.assertEquals(ChromosomeMap.identifier2chromosom.get("NC_000020.11").intValue(), 20);
		Assert.assertEquals(ChromosomeMap.identifier2chromosom.get("NC_000082.5").intValue(), 16);
	}
}
