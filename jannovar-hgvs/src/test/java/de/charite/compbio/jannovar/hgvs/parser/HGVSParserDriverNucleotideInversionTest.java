package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide inversions.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideInversionTest {

	HGVSParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.(247_248inv)", "NM_000138.4:c.(247+1_247+3inv)",
			"NM_000138.4:c.(247-3_247-1inv)", "NM_000138.4:c.(*247_*247+3inv)", "NM_000138.4:c.(-247_-247-3inv)"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248inv", "NM_000138.4:c.247+1_247+3inv",
			"NM_000138.4:c.247-3_247-1inv", "NM_000138.4:c.*247_*247+3inv", "NM_000138.4:c.-247_-247-3inv"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248invAT", "NM_000138.4:c.247+1_247+3invATA",
			"NM_000138.4:c.247-3_247-1invATA", "NM_000138.4:c.*247_*247+3invATAT",
			"NM_000138.4:c.-247_-247-3invATAT"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248inv2", "NM_000138.4:c.247+1_247+3inv3",
			"NM_000138.4:c.247-3_247-1inv3", "NM_000138.4:c.*247_*247+3inv4", "NM_000138.4:c.-247_-247-3inv4"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
