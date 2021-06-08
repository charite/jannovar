package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HGVSParserDriver for parsing nucleotide duplications.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverNucleotideDuplicationTest {

	HGVSParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testPredictedOnly() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.(247_248dup)", "NM_000138.4:c.(247+1_247+3dup)",
			"NM_000138.4:c.(247-3_247-1dup)", "NM_000138.4:c.(*247_*247+3dup)", "NM_000138.4:c.(-247_-247-3dup)"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithoutSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248dup", "NM_000138.4:c.247+1_247+3dup",
			"NM_000138.4:c.247-3_247-1dup", "NM_000138.4:c.*247_*247+3dup", "NM_000138.4:c.-247_-247-3dup"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequence() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248dupAT", "NM_000138.4:c.247+1_247+3dupATA",
			"NM_000138.4:c.247-3_247-1dupATA", "NM_000138.4:c.*247_*247+3dupATAT",
			"NM_000138.4:c.-247_-247-3dupATAT"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

	@Test
	public void testWithSequenceLength() {
		String hgvsStrings[] = new String[]{"NM_000138.4:c.247_248dup2", "NM_000138.4:c.247+1_247+3dup3",
			"NM_000138.4:c.247-3_247-1dup3", "NM_000138.4:c.*247_*247+3dup4", "NM_000138.4:c.-247_-247-3dup4"};

		for (String hgvsString : hgvsStrings) {
			HGVSVariant variant = driver.parseHGVSString(hgvsString);

			Assertions.assertTrue(variant instanceof SingleAlleleNucleotideVariant);
			Assertions.assertEquals(hgvsString, variant.toHGVSString());
		}
	}

}
