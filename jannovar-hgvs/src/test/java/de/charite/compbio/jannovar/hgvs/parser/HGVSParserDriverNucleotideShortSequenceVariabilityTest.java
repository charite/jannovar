package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide short sequence variabilities.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideShortSequenceVariabilityTest {

	HGVSParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testOnlyPredicted() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.(247(1_6))", "NM_000138.4:c.(247+1(1_6))",
			"NM_000138.4:c.(247-1(1_6))", "NM_000138.4:c.(*247(1_6))", "NM_000138.4:c.(-247(1_6))"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithPointLocations() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247(1_6)", "NM_000138.4:c.247+1(1_6)",
			"NM_000138.4:c.247-1(1_6)", "NM_000138.4:c.*247(1_6)", "NM_000138.4:c.-247(1_6)"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithRanges() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248(1_6)", "NM_000138.4:c.247+1_247+3(1_6)",
			"NM_000138.4:c.247-3_247-1(1_6)", "NM_000138.4:c.*247_*247+3(1_6)", "NM_000138.4:c.-247_-247-3(1_6)"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
