package de.charite.compbio.jannovar.mendel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author <a href="mailto:j.jacobsen@qmul.ac.uk">Jules Jacobsen</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class GenotypeTest {

	@Test
	public void emptyAlleles() {
		Genotype genotype = new Genotype(Collections.emptyList());
		assertEquals(0, genotype.getPloidy());
		assertFalse(genotype.isMonoploid());
		assertFalse(genotype.isDiploid());
		assertFalse(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
		assertEquals(Collections.emptyList(), genotype.getAlleleNumbers());
	}

	@Test
	public void testMonoploidRef() {
		Genotype genotype = new Genotype(Collections.singletonList(0));
		assertEquals(1, genotype.getPloidy());
		assertTrue(genotype.isMonoploid());
		assertFalse(genotype.isDiploid());
		assertFalse(genotype.isHet());
		// TODO: Should this really true? Its a monoploid.
		assertTrue(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testMonoploidAlt() {
		Genotype genotype = new Genotype(Collections.singletonList(1));
		assertEquals(1, genotype.getPloidy());
		assertTrue(genotype.isMonoploid());
		assertFalse(genotype.isDiploid());
		assertFalse(genotype.isHet());
		assertFalse(genotype.isHomRef());
		// TODO: Should this really true? Its a monoploid.
		assertTrue(genotype.isHomAlt());
	}

	@Test
	public void testMonoploidNoCall() {
		Genotype genotype = new Genotype(Collections.singletonList(-1));
		assertEquals(1, genotype.getPloidy());
		assertTrue(genotype.isNotObserved());
		// TODO: Should an unobserved monoploid really be classed as a monoploid?
		assertTrue(genotype.isMonoploid());
		assertFalse(genotype.isDiploid());
		assertFalse(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testDiploidHomRef() {
		Genotype genotype = new Genotype(Arrays.asList(0, 0));
		assertEquals(2, genotype.getPloidy());
		assertFalse(genotype.isHet());
		assertTrue(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testDiploidNoCallHetRef() {
		Genotype genotype = new Genotype(Arrays.asList(-1, 0));
		assertEquals(2, genotype.getPloidy());
		// TODO: Should a diploid with an unobserved allele really be classed as a diploid?
		assertFalse(genotype.isMonoploid());
		assertTrue(genotype.isDiploid());

		assertTrue(genotype.isHet());
		assertTrue(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testDiploidNoCallHetAlt() {
		Genotype genotype = new Genotype(Arrays.asList(0, -1));
		assertEquals(2, genotype.getPloidy());
		// TODO: Should a diploid with an unobserved allele really be classed as a diploid?
		assertFalse(genotype.isMonoploid());
		assertTrue(genotype.isDiploid());

		assertTrue(genotype.isHet());
		assertTrue(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testDiploidNoCallHom() {
		Genotype genotype = new Genotype(Arrays.asList(-1, -1));
		assertEquals(2, genotype.getPloidy());
		// TODO: Should a diploid with an unobserved allele really be classed as a diploid?
		assertFalse(genotype.isMonoploid());
		assertTrue(genotype.isDiploid());

		assertFalse(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testDiploidNoCallHetVar() {
		Genotype genotype = new Genotype(Arrays.asList(1, -1));
		assertEquals(2, genotype.getPloidy());
		assertTrue(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertTrue(genotype.isHomAlt());
	}

	@Test
	public void testDiploidNoCallHetAltRef() {
		Genotype genotype = new Genotype(Arrays.asList(-1, 1));
		assertEquals(2, genotype.getPloidy());
		assertTrue(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertTrue(genotype.isHomAlt());
	}

	@Test
	public void testDiploidHet() {
		Genotype genotype = new Genotype(Arrays.asList(0, 1));
		assertEquals(2, genotype.getPloidy());
		assertTrue(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testDiploidHomAlt() {
		Genotype genotype = new Genotype(Arrays.asList(1, 1));
		assertEquals(2, genotype.getPloidy());
		assertFalse(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertTrue(genotype.isHomAlt());
	}

	@Test
	public void testDiploidMultiSampleHetRef() {
		Genotype genotype = new Genotype(Arrays.asList(0, 2));
		assertEquals(2, genotype.getPloidy());
		assertTrue(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}
	
	@Test
	public void testDiploidMultiSampleMixed1() {
		Genotype genotype = new Genotype(Arrays.asList(-1, 2));
		assertEquals(2, genotype.getPloidy());
		assertTrue(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertTrue(genotype.isHomAlt());
	}
	
    @Test
    public void testDiploidMultiSampleMixed2() {
	Genotype genotype = new Genotype(Arrays.asList(2, -1));
	assertEquals(2, genotype.getPloidy());
	assertTrue(genotype.isHet());
	assertFalse(genotype.isHomRef());
	assertTrue(genotype.isHomAlt());
    }

	@Test
	public void testDiploidMultiSampleHomAlt() {
		Genotype genotype = new Genotype(Arrays.asList(2, 2));
		assertEquals(2, genotype.getPloidy());
		assertFalse(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertTrue(genotype.isHomAlt());
	}

	/**
	 * Previously this would fail in case of multi-sample genotypes.
	 */
	@Test
	public void testDiploidMultiSampleHetVar() {
		Genotype genotype = new Genotype(Arrays.asList(1, 2));
		assertEquals(2, genotype.getPloidy());
		assertTrue(genotype.isHet());
		assertFalse(genotype.isHomRef());
		assertFalse(genotype.isHomAlt());
	}

	@Test
	public void testHashCode() {
		Map<Genotype, String> map = new HashMap<>();
		map.put(new Genotype(Arrays.asList(1, 2)), "ONE");
		map.put(new Genotype(Arrays.asList(1, 2)), "TWO");
		map.put(new Genotype(Arrays.asList(1, 1)), "THREE");

		Map<Genotype, String> expected = new HashMap<>();
		expected.put(new Genotype(Arrays.asList(1, 2)), "TWO");
		expected.put(new Genotype(Arrays.asList(1, 1)), "THREE");
		assertEquals(expected, map);
	}

	@Test
	public void testEquals() {
		Genotype one = new Genotype(Arrays.asList(1, 2));
		Genotype two = new Genotype(Arrays.asList(1, 2));
		Genotype three = new Genotype(Arrays.asList(1, 1));
		Genotype four = new Genotype(Collections.singletonList(1));
		assertTrue(one.equals(two));
		assertFalse(one.equals(three));
		assertFalse(one.equals(four));
	}
}
