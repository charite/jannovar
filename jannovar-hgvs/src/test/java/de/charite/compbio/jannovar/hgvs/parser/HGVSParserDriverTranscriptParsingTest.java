package de.charite.compbio.jannovar.hgvs.parser;

import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HGVSParserDriver that targets parsing of the transcript properties.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverTranscriptParsingTest {

	HGVSParser driver;

	@BeforeEach
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testWithoutVersionWithoutGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
			.parseHGVSString("NM_000138:c.7339G>A");
		Assertions.assertEquals("NM_000138", variant.getRefID());
		Assertions.assertEquals("NM_000138", variant.getRefIDWithVersion());
		Assertions.assertEquals(SingleAlleleNucleotideVariant.NO_TRANSCRIPT_VERSION, variant.getTranscriptVersion());
		Assertions.assertEquals(null, variant.getProteinID());
		Assertions.assertEquals("NM_000138", variant.getSequenceNamePrefix());
	}

	@Test
	public void testWithVersionWithoutGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
			.parseHGVSString("NM_000138.4:c.7339G>A");
		Assertions.assertEquals("NM_000138", variant.getRefID());
		Assertions.assertEquals("NM_000138.4", variant.getRefIDWithVersion());
		Assertions.assertEquals(4, variant.getTranscriptVersion());
		Assertions.assertEquals(null, variant.getProteinID());
		Assertions.assertEquals("NM_000138.4", variant.getSequenceNamePrefix());
	}

	@Test
	public void testWithoutVersionGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
			.parseHGVSString("NM_000138(FBN1):c.7339G>A");
		Assertions.assertEquals("NM_000138", variant.getRefID());
		Assertions.assertEquals("NM_000138", variant.getRefIDWithVersion());
		Assertions.assertEquals(SingleAlleleNucleotideVariant.NO_TRANSCRIPT_VERSION, variant.getTranscriptVersion());
		Assertions.assertEquals("FBN1", variant.getProteinID());
		Assertions.assertEquals("NM_000138(FBN1)", variant.getSequenceNamePrefix());
	}

	@Test
	public void testWithVersionWithGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
			.parseHGVSString("NM_000138.4(FBN1):c.7339G>A");
		Assertions.assertEquals("NM_000138", variant.getRefID());
		Assertions.assertEquals("NM_000138.4", variant.getRefIDWithVersion());
		Assertions.assertEquals(4, variant.getTranscriptVersion());
		Assertions.assertEquals("FBN1", variant.getProteinID());
		Assertions.assertEquals("NM_000138.4(FBN1)", variant.getSequenceNamePrefix());
	}

}
