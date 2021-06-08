package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide delitions.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideDeletionTest {

	HGVSParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		HGVSParsingTestBase.setLogLevel(Level.DEBUG);

		String hgvsStrings[] = new String[]{"NM_000138.4:c.(247_248del)", "NM_000138.4:c.(247+1_247+3del)",
			"NM_000138.4:c.(247-3_247-1del)", "NM_000138.4:c.(*247_*247+3del)",
			"NM_000138.4:c.(-247_-247-3del)"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248del", "NM_000138.4:c.247+1_247+3del",
			"NM_000138.4:c.247-3_247-1del", "NM_000138.4:c.*247_*247+3del", "NM_000138.4:c.-247_-247-3del"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248delAT",
			"NM_000138.4:c.247+1_247+3delATA", "NM_000138.4:c.247-3_247-1delATA",
			"NM_000138.4:c.*247_*247+3delATAT", "NM_000138.4:c.-247_-247-3delATAT"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248del2", "NM_000138.4:c.247+1_247+3del3",
			"NM_000138.4:c.247-3_247-1del3", "NM_000138.4:c.*247_*247+3del4",
			"NM_000138.4:c.-247_-247-3del4"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
