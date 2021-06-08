package de.charite.compbio.jannovar.reference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for the VariantCorrectorData class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class VariantDataCorrectorTest {

	/**
	 * Test with SNV data.
	 */
	@Test
	public void testSingleNucleotide() {
		VariantDataCorrector corr = new VariantDataCorrector("C", "T", 100);
		Assertions.assertEquals(corr.position, 100);
		Assertions.assertEquals(corr.ref, "C");
		Assertions.assertEquals(corr.alt, "T");
	}

	/**
	 * Test with insertion data.
	 */
	@Test
	public void testInsertion() {
		VariantDataCorrector corr = new VariantDataCorrector("CGAT", "C", 100);
		Assertions.assertEquals(corr.position, 101);
		Assertions.assertEquals(corr.ref, "GAT");
		Assertions.assertEquals(corr.alt, "");
	}

	/**
	 * Test with substitution data.
	 */
	@Test
	public void testSubstitution() {
		VariantDataCorrector corr = new VariantDataCorrector("CCGA", "CGAT", 100);
		Assertions.assertEquals(corr.position, 101);
		Assertions.assertEquals(corr.ref, "CGA");
		Assertions.assertEquals(corr.alt, "GAT");
	}

	/**
	 * Test with deletion data.
	 */
	@Test
	public void testDeletion() {
		VariantDataCorrector corr = new VariantDataCorrector("C", "CGAT", 100);
		Assertions.assertEquals(corr.position, 101);
		Assertions.assertEquals(corr.ref, "");
		Assertions.assertEquals(corr.alt, "GAT");
	}

}
