package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

// TODO(holtgrew): Test with onlyPredicted flag ("(...)") once this works

/**
 * Tests for the HGVSParserDriver for parsing nucleotide substitutions.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideSingleAlleleMultiChangeTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser();
	}

	@Test
	public void testMultiChangeAlleleOnlyPredicted() {
		String hgvsStrings[] = new String[] { "1:r.[(123C>T),(124T>A)]", "1:r.[(123C>T,124T>A)]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsStrings[0], variant.toHGVSString());
		}
	}

	/** test with multi-change allele from single origin */
	@Test
	public void testMultiChangeAlleleSingleOrigin() {
		String hgvsStrings[] = new String[] { "1:r.[123C>T,124T>A]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test with multi-change allele with chimeric separator */
	@Test
	public void testMultiChangeAlleleChimeric() {
		String hgvsStrings[] = new String[] { "1:r.[123C>T//124T>A]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test with multi-change allele with in-cis separator */
	@Test
	public void testMultiChangeAlleleInCis() {
		String hgvsStrings[] = new String[] { "1:r.[123C>T;124T>A]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test with multi-change allele with unknown-cis-trans separator */
	@Test
	public void testMultiChangeAlleleUnknownCisTrans() {
		String hgvsStrings[] = new String[] { "1:r.[123C>T(;)124T>A]" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
