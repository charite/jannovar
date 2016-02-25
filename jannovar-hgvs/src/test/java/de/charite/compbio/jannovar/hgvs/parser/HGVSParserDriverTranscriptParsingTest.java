package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;

/**
 * Tests for the HGVSParserDriver that targets parsing of the transcript properties.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserDriverTranscriptParsingTest {

	HGVSParser driver;

	@Before
	public void setUp() throws Exception {
		driver = new HGVSParser(false);
	}

	@Test
	public void testWithoutVersionWithoutGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
				.parseHGVSString("NM_000138:c.7339G>A");
		Assert.assertEquals("NM_000138", variant.getRefID());
		Assert.assertEquals("NM_000138", variant.getRefIDWithVersion());
		Assert.assertEquals(SingleAlleleNucleotideVariant.NO_TRANSCRIPT_VERSION, variant.getTranscriptVersion());
		Assert.assertEquals(null, variant.getProteinID());
		Assert.assertEquals("NM_000138", variant.getSequenceNamePrefix());
	}

	@Test
	public void testWithVersionWithoutGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
				.parseHGVSString("NM_000138.4:c.7339G>A");
		Assert.assertEquals("NM_000138", variant.getRefID());
		Assert.assertEquals("NM_000138.4", variant.getRefIDWithVersion());
		Assert.assertEquals(4, variant.getTranscriptVersion());
		Assert.assertEquals(null, variant.getProteinID());
		Assert.assertEquals("NM_000138.4", variant.getSequenceNamePrefix());
	}

	@Test
	public void testWithoutVersionGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
				.parseHGVSString("NM_000138(FBN1):c.7339G>A");
		Assert.assertEquals("NM_000138", variant.getRefID());
		Assert.assertEquals("NM_000138", variant.getRefIDWithVersion());
		Assert.assertEquals(SingleAlleleNucleotideVariant.NO_TRANSCRIPT_VERSION, variant.getTranscriptVersion());
		Assert.assertEquals("FBN1", variant.getProteinID());
		Assert.assertEquals("NM_000138(FBN1)", variant.getSequenceNamePrefix());
	}

	@Test
	public void testWithVersionWithGeneSymbol() {
		SingleAlleleNucleotideVariant variant = (SingleAlleleNucleotideVariant) driver
				.parseHGVSString("NM_000138.4(FBN1):c.7339G>A");
		Assert.assertEquals("NM_000138", variant.getRefID());
		Assert.assertEquals("NM_000138.4", variant.getRefIDWithVersion());
		Assert.assertEquals(4, variant.getTranscriptVersion());
		Assert.assertEquals("FBN1", variant.getProteinID());
		Assert.assertEquals("NM_000138.4(FBN1)", variant.getSequenceNamePrefix());
	}

}
