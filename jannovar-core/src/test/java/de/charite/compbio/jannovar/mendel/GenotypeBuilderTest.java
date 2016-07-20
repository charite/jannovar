package de.charite.compbio.jannovar.mendel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenotypeBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		GenotypeBuilder builder = new GenotypeBuilder();
		builder.getAlleleNumbers().add(0);
		builder.getAlleleNumbers().add(1);

		Genotype gt = builder.build();
		Assert.assertEquals(2, gt.getAlleleNumbers().size());

		Assert.assertEquals(0, gt.getAlleleNumbers().get(0).intValue());
		Assert.assertEquals(1, gt.getAlleleNumbers().get(1).intValue());
	}

}
