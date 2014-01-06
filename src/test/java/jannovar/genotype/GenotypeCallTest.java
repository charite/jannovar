package jannovar.genotype;

import jannovar.common.Genotype;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test adding and retireving data about the genotype.
 */
public class GenotypeCallTest {

	@Test
	public void testGetQuality() {
		ArrayList<Genotype> calls = new ArrayList<Genotype>();
		calls.add(Genotype.HETEROZYGOUS);
		ArrayList<Integer> qualities = new ArrayList<Integer>();
		qualities.add(100);
		GenotypeCall gc = new GenotypeCall(calls, qualities);
		int q = gc.getQualityInIndividualN(0);
		Assert.assertEquals(100, q);
	}

	@Test
	public void testGetGenotype() {
		ArrayList<Genotype> calls = new ArrayList<Genotype>();
		calls.add(Genotype.HETEROZYGOUS);
		ArrayList<Integer> qualities = new ArrayList<Integer>();
		qualities.add(100);
		GenotypeCall gc = new GenotypeCall(calls, qualities);
		Genotype g = gc.getGenotypeInIndividualN(0);
		Assert.assertEquals(Genotype.HETEROZYGOUS, g);
	}

}