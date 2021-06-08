package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide dellications.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideIndelTest {

	HGVSParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.(247_248delinsA)",
			"NM_000138.4:c.(247+1_247+3delinsA)", "NM_000138.4:c.(247-3_247-1delinsA)",
			"NM_000138.4:c.(*247_*247+3delinsA)", "NM_000138.4:c.(-247_-247-3delinsA)"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutDeletionSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248delinsAT",
			"NM_000138.4:c.247+1_247+3delinsAT", "NM_000138.4:c.247-3_247-1delinsAT",
			"NM_000138.4:c.*247_*247+3delinsAT", "NM_000138.4:c.-247_-247-3delinsAT"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}
	@Test
	public void testWithoutInsertionSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248delATins",
			"NM_000138.4:c.247+1_247+3delATins", "NM_000138.4:c.247-3_247-1delATins",
			"NM_000138.4:c.*247_*247+3delATins", "NM_000138.4:c.-247_-247-3delATins"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithDeletionSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248delATinsCG",
			"NM_000138.4:c.247+1_247+3delATAinsCG", "NM_000138.4:c.247-3_247-1delATAinsCG",
			"NM_000138.4:c.*247_*247+3delATATinsCG", "NM_000138.4:c.-247_-247-3delATATinsCG"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248del2ins4",
			"NM_000138.4:c.247+1_247+3del3ins4", "NM_000138.4:c.247-3_247-1del3ins4",
			"NM_000138.4:c.*247_*247+3del4ins4", "NM_000138.4:c.-247_-247-3del4ins4"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
