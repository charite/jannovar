package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenotypeCallsBuilderTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		GenotypeCallsBuilder builder = new GenotypeCallsBuilder();
		builder.setPayload(1);
		builder.setChromType(ChromosomeType.AUTOSOMAL);
		builder.getSampleToGenotype().put("example", new Genotype(ImmutableList.of(0, 1)));

		GenotypeCalls calls = builder.build();

		Assertions.assertEquals(1, calls.getPayload());
		Assertions.assertEquals(1, calls.getNSamples());
		Assertions.assertEquals("Genotype [alleleNumbers=[0, 1]]", calls.getGenotypeForSample("example").toString());
	}

}
