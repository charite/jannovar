package jannovar.exome;

import jannovar.common.ChromosomeMap;
import jannovar.common.Genotype;
import jannovar.common.VariantType;
import jannovar.genotype.GenotypeCall;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the VariantTypeCounter class
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class VariantTypeCounterTest {

	/* used in the tests below */
	ArrayList<Genotype> genotypes;
	/* qualities used in the tests below */
	ArrayList<Integer> qualities;
	/* genotype call used in the test below */
	GenotypeCall gtc;
	/* variant used in the tests below */
	Variant variant;

	@Before
	public void setUp() {
		// fill genotypes
		this.genotypes = new ArrayList<Genotype>();
		this.genotypes.add(Genotype.HETEROZYGOUS);
		this.genotypes.add(Genotype.HOMOZYGOUS_REF);
		this.genotypes.add(Genotype.HOMOZYGOUS_ALT);
		this.genotypes.add(Genotype.HETEROZYGOUS);

		// fill qualities
		this.qualities = new ArrayList<Integer>();
		this.qualities.add(10);
		this.qualities.add(20);
		this.qualities.add(30);
		this.qualities.add(40);

		// setup genotype call
		this.gtc = new GenotypeCall(genotypes, qualities);

		// setup Variant
		this.variant = new Variant(ChromosomeMap.identifier2chromosom.get("chr1"), 10, "A", "C", gtc, 40.0f,
				"<info>");
	}

	// check the variant counts
	private void checkVariantCount4(VariantTypeCounter counter, int mult) {
		Iterator<VariantType> it = counter.getVariantTypeIterator();
		while (it.hasNext()) {
			VariantType vt = it.next();
			ArrayList<Integer> expected = new ArrayList<Integer>();
			if (vt != VariantType.ERROR) {
				expected.add(0);
				expected.add(0);
				expected.add(0);
				expected.add(0);
			} else {
				expected.add(1 * mult);
				expected.add(0);
				expected.add(1 * mult);
				expected.add(1 * mult);
			}
			Assert.assertEquals(expected, counter.getTypeSpecificCounts(vt));
		}

	}

	// Test empty counter and getTypeSpecificCounts().
	@Test
	public void testEmpty_getTypeSpecificCounts() {
		VariantTypeCounter counter = new VariantTypeCounter(1);
		Iterator<VariantType> it = counter.getVariantTypeIterator();
		while (it.hasNext()) {
			VariantType vt = it.next();
			ArrayList<Integer> expected = new ArrayList<Integer>();
			expected.add(0);
			Assert.assertEquals(expected, counter.getTypeSpecificCounts(vt));
		}
	}

	// Create counter, increment counts, then consider type specific counts.
	@Test
	public void testFilled_incrementCounts_getTypeSpecificCounts() {
		VariantTypeCounter counter = new VariantTypeCounter(4);
		counter.incrementCount(variant);
		checkVariantCount4(counter, 1);
	}

	// Create counter, increment counts, then consider type specific counts.
	@Test
	public void testFilled_countVariants_getTypeSpecificCounts() {
		VariantTypeCounter counter = new VariantTypeCounter(4);
		ArrayList<Variant> varList = new ArrayList<Variant>();
		varList.add(variant);
		varList.add(variant);
		counter.countVariants(varList);
		checkVariantCount4(counter, 2);
	}

	// Create counter from variant list.
	@Test
	public void testConstructWithList() {
		ArrayList<Variant> varList = new ArrayList<Variant>();
		varList.add(variant);
		varList.add(variant);

		VariantTypeCounter counter = new VariantTypeCounter(varList);
		checkVariantCount4(counter, 2);
	}
}
