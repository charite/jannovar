package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.reference.VariantDataCorrector;

/**
 * Test for the VariantCorrectorData class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class VariantDataCorrectorTest {

	/** Test with SNV data. */
	@Test
	public void testSingleNucleotide() {
		VariantDataCorrector corr = new VariantDataCorrector("C", "T", 100);
		Assert.assertEquals(corr.position, 100);
		Assert.assertEquals(corr.ref, "C");
		Assert.assertEquals(corr.alt, "T");
	}

	/** Test with insertion data. */
	@Test
	public void testInsertion() {
		VariantDataCorrector corr = new VariantDataCorrector("CGAT", "C", 100);
		Assert.assertEquals(corr.position, 101);
		Assert.assertEquals(corr.ref, "GAT");
		Assert.assertEquals(corr.alt, "");
	}

	/** Test with substitution data. */
	@Test
	public void testSubstitution() {
		VariantDataCorrector corr = new VariantDataCorrector("CCGA", "CGAT", 100);
		Assert.assertEquals(corr.position, 101);
		Assert.assertEquals(corr.ref, "CGA");
		Assert.assertEquals(corr.alt, "GAT");
	}

	/** Test with deletion data. */
	@Test
	public void testDeletion() {
		VariantDataCorrector corr = new VariantDataCorrector("C", "CGAT", 100);
		Assert.assertEquals(corr.position, 101);
		Assert.assertEquals(corr.ref, "");
		Assert.assertEquals(corr.alt, "GAT");
	}

}
