package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide substitutions.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideSubstitutionTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser();
	}

	@Test
	public void testOnlyPredicted() {
		String hgvsStrings[] = new String[] { "1:g.(123C>T)", "chr1:g.(123C>T)", "11:g.(111959693G>T)",
				"chr11:g.(111959693G>T)", "NM_000138.4:n.(247C>T)" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test nucleotide substitutions at non-offset, non-UTR positions */
	@Test
	public void testParseNTSubstitutionNormalPos() {
		String hgvsStrings[] = new String[] { "1:g.123C>T", "chr1:g.123C>T", "11:g.111959693G>T",
				"chr11:g.111959693G>T", "NM_000138.4:n.247C>T" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test nucleotide substitutions at non-offset positions */
	@Test
	public void testParseNTSubstitutionPosWithOffset() {
		String hgvsStrings[] = new String[] { "XXX:c.123+3C>T", "XXX:c.123-3C>T" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test nucleotide substitutions at UTR positions */
	@Test
	public void testParseNTSubstitutionUTRPos() {
		String hgvsStrings[] = new String[] { "XXX:c.-123C>T", "XXX:c.*123C>T" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	/** test nucleotide substitutions at UTR positions with offset */
	@Test
	public void testParseNTSubstitutionUTRPosWithOffset() {
		String hgvsStrings[] = new String[] { "XXX:c.-123+3C>T", "XXX:c.*123+3C>T", "XXX:c.-123-3C>T",
				"XXX:c.*123-3C>T" };

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assert.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assert.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
