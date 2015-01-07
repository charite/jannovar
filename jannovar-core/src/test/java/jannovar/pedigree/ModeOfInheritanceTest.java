package jannovar.pedigree;

import org.junit.Assert;
import org.junit.Test;

public class ModeOfInheritanceTest {

	@Test
	public void testSize() {
		Assert.assertEquals(5, ModeOfInheritance.values().length);
	}

}
