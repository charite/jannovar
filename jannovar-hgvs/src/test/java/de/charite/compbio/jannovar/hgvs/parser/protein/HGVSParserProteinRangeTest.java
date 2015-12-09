package de.charite.compbio.jannovar.hgvs.parser.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_rangeContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Tests for parsing nucleotide ranges
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserProteinRangeTest extends HGVSParserTestBase {

	@Test
	public void testAA1CDSRange() {
		Antlr4HGVSParser parser = buildParserForString("C123_A125", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char C) 123) _ (aa_point_location (aa_char A) 125))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSRange() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Ala125", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 125))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1CDSRangeWithOffset() {
		Antlr4HGVSParser parser = buildParserForString("C123+1_A125-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char C) 123 + 1) _ (aa_point_location (aa_char A) 125 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSRangeWithOffset() {
		Antlr4HGVSParser parser = buildParserForString("Cys123+1_Ala125-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) 123 + 1) _ (aa_point_location (aa_char Ala) 125 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5Range() {
		Antlr4HGVSParser parser = buildParserForString("C-125_A-123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char C) - 125) _ (aa_point_location (aa_char A) - 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5Range() {
		Antlr4HGVSParser parser = buildParserForString("Cys-125_Ala-123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) - 125) _ (aa_point_location (aa_char Ala) - 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5RangeWithOffset() {
		Antlr4HGVSParser parser = buildParserForString("C-125+1_A-123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char C) - 125 + 1) _ (aa_point_location (aa_char A) - 123 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5RangeWithOffset() {
		Antlr4HGVSParser parser = buildParserForString("Cys-125+1_Ala-123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) - 125 + 1) _ (aa_point_location (aa_char Ala) - 123 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3Range() {
		Antlr4HGVSParser parser = buildParserForString("C*125_A*123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals("(aa_range (aa_point_location (aa_char C) * 125) _ (aa_point_location (aa_char A) * 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3Range() {
		Antlr4HGVSParser parser = buildParserForString("CysTer125_AlaTer123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) Ter 125) _ (aa_point_location (aa_char Ala) Ter 123))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3RangeWithOffset() {
		Antlr4HGVSParser parser = buildParserForString("C*125+1_A*123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char C) * 125 + 1) _ (aa_point_location (aa_char A) * 123 - 1))",
				loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3RangeWithOffset() {
		Antlr4HGVSParser parser = buildParserForString("CysTer125+1_AlaTer123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_rangeContext loc = parser.aa_range();
		Assert.assertEquals(
				"(aa_range (aa_point_location (aa_char Cys) Ter 125 + 1) _ (aa_point_location (aa_char Ala) Ter 123 - 1))",
				loc.toStringTree(parser));
	}

}
