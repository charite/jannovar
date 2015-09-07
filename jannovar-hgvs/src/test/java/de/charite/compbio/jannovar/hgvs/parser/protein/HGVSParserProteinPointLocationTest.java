package de.charite.compbio.jannovar.hgvs.parser.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_point_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Tests for parsing nucleotide point locations
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinPointLocationTest extends HGVSParserTestBase {

	@Test
	public void testAA1CDSPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("C123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("Cys123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1CDSPointLocationWithPositiveOffset() {
		Antlr4HGVSParser parser = buildParserForString("C123+1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) 123 + 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSPointLocationWithNegativeOffset() {
		Antlr4HGVSParser parser = buildParserForString("Cys123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) 123 - 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5PointLocation() {
		Antlr4HGVSParser parser = buildParserForString("C-123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) - 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5PointLocation() {
		Antlr4HGVSParser parser = buildParserForString("Cys-123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) - 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5PointLocationWithPositiveOffset() {
		Antlr4HGVSParser parser = buildParserForString("C-23+1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) - 23 + 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5PointLocationWithNegativeOffset() {
		Antlr4HGVSParser parser = buildParserForString("Cys-123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) - 123 - 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3PointLocation() {
		Antlr4HGVSParser parser = buildParserForString("C*123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) * 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3PointLocation() {
		Antlr4HGVSParser parser = buildParserForString("CysTer123", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) Ter 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3PointLocationWithPositiveOffset() {
		Antlr4HGVSParser parser = buildParserForString("C*23+1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) * 23 + 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3PointLocationWithNegativeOffset() {
		Antlr4HGVSParser parser = buildParserForString("CysTer123-1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) Ter 123 - 1)", loc.toStringTree(parser));
	}

}
