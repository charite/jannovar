package de.charite.compbio.jannovar.mendel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenotypeBuilderTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		GenotypeBuilder builder = new GenotypeBuilder();
		builder.getAlleleNumbers().add(0);
		builder.getAlleleNumbers().add(1);

		Genotype gt = builder.build();
		Assertions.assertEquals(2, gt.getAlleleNumbers().size());

		Assertions.assertEquals(0, gt.getAlleleNumbers().get(0).intValue());
		Assertions.assertEquals(1, gt.getAlleleNumbers().get(1).intValue());
	}

}
