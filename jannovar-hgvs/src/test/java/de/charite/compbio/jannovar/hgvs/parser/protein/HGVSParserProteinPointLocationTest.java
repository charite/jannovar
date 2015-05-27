package de.charite.compbio.jannovar.hgvs.parser.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_point_locationContext;

/**
 * Tests for parsing nucleotide point locations
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinPointLocationTest extends HGVSParserTestBase {

	@Test
	public void testAA1CDSPointLocation() {
		HGVSParser parser = buildParserForString("C123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSPointLocation() {
		HGVSParser parser = buildParserForString("Cys123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1CDSPointLocationWithPositiveOffset() {
		HGVSParser parser = buildParserForString("C123+1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) 123 + 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3CDSPointLocationWithNegativeOffset() {
		HGVSParser parser = buildParserForString("Cys123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) 123 - 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5PointLocation() {
		HGVSParser parser = buildParserForString("C-123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) - 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5PointLocation() {
		HGVSParser parser = buildParserForString("Cys-123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) - 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR5PointLocationWithPositiveOffset() {
		HGVSParser parser = buildParserForString("C-23+1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) - 23 + 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR5PointLocationWithNegativeOffset() {
		HGVSParser parser = buildParserForString("Cys-123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) - 123 - 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3PointLocation() {
		HGVSParser parser = buildParserForString("C*123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) * 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3PointLocation() {
		HGVSParser parser = buildParserForString("CysTer123", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) Ter 123)", loc.toStringTree(parser));
	}

	@Test
	public void testAA1UTR3PointLocationWithPositiveOffset() {
		HGVSParser parser = buildParserForString("C*23+1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char C) * 23 + 1)", loc.toStringTree(parser));
	}

	@Test
	public void testAA3UTR3PointLocationWithNegativeOffset() {
		HGVSParser parser = buildParserForString("CysTer123-1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_point_locationContext loc = parser.aa_point_location();
		Assert.assertEquals("(aa_point_location (aa_char Cys) Ter 123 - 1)", loc.toStringTree(parser));
	}

}
