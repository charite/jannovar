package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenotypeCallsBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		GenotypeCallsBuilder builder = new GenotypeCallsBuilder();
		builder.setPayload(1);
		builder.setChromType(ChromosomeType.AUTOSOMAL);
		builder.getSampleToGenotype().put("example", new Genotype(ImmutableList.of(0, 1)));

		GenotypeCalls calls = builder.build();

		Assert.assertEquals(1, calls.getPayload());
		Assert.assertEquals(1, calls.getNSamples());
		Assert.assertEquals("Genotype [alleleNumbers=[0, 1]]", calls.getGenotypeForSample("example").toString());
	}

}
