package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_rangeContext;

/**
 * Tests for parsing nucleotide ranges
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinRangeTest extends HGVSParserTestBase {

	@Test
	public void testAA1CDSRange() {
		HGVSParser parser = buildParserForString("C123_A125", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char C) 123) _ (aa_point_location (aa_char A) 125))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSRange() {
		HGVSParser parser = buildParserForString("Cys123_Ala125", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 125))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1CDSRangeWithOffset() {
		HGVSParser parser = buildParserForString("C123+1_A125-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char C) 123 + 1) _ (aa_point_location (aa_char A) 125 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSRangeWithOffset() {
		HGVSParser parser = buildParserForString("Cys123+1_Ala125-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) 123 + 1) _ (aa_point_location (aa_char Ala) 125 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5Range() {
		HGVSParser parser = buildParserForString("C-125_A-123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char C) - 125) _ (aa_point_location (aa_char A) - 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5Range() {
		HGVSParser parser = buildParserForString("Cys-125_Ala-123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) - 125) _ (aa_point_location (aa_char Ala) - 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5RangeWithOffset() {
		HGVSParser parser = buildParserForString("C-125+1_A-123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char C) - 125 + 1) _ (aa_point_location (aa_char A) - 123 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5RangeWithOffset() {
		HGVSParser parser = buildParserForString("Cys-125+1_Ala-123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) - 125 + 1) _ (aa_point_location (aa_char Ala) - 123 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3Range() {
		HGVSParser parser = buildParserForString("C*125_A*123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char C) * 125) _ (aa_point_location (aa_char A) * 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3Range() {
		HGVSParser parser = buildParserForString("CysTer125_AlaTer123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) Ter 125) _ (aa_point_location (aa_char Ala) Ter 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3RangeWithOffset() {
		HGVSParser parser = buildParserForString("C*125+1_A*123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char C) * 125 + 1) _ (aa_point_location (aa_char A) * 123 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3RangeWithOffset() {
		HGVSParser parser = buildParserForString("CysTer125+1_AlaTer123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) Ter 125 + 1) _ (aa_point_location (aa_char Ala) Ter 123 - 1))",
				loc.toStringTree(parser));
	}

}
