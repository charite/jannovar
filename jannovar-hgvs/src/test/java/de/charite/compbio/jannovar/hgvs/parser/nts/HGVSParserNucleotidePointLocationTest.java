package de.charite.compbio.jannovar.hgvs.parser.nts;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_point_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Tests for parsing nucleotide point locations
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotidePointLocationTest extends HGVSParserTestBase {

	@Test
	public void testPositiveNumberAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("123", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location (nt_number 123)))", loc.toStringTree(parser));
	}

	@Test
	public void testUpstreamNumberAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("-123", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location - (nt_number 123)))", loc.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("*123", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location * (nt_number 123)))", loc.toStringTree(parser));
	}

	@Test
	public void testPositiveNumberWithPositiveOffsetAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("123+4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location (nt_number 123)) (nt_offset + (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testPositiveNumberWithNegativeOffsetAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("123-4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location (nt_number 123)) (nt_offset - (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionWithPositiveOffsetAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("-123+4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location - (nt_number 123)) (nt_offset + (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionWithNegativeOffsetAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("-123-4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location - (nt_number 123)) (nt_offset - (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionWithPositiveOffsetAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("*123+4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location * (nt_number 123)) (nt_offset + (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionWithNegativeOffsetAsPointLocation() {
		Antlr4HGVSParser parser = buildParserForString("*123-4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location * (nt_number 123)) (nt_offset - (nt_number 4)))",
				loc.toStringTree(parser));
	}

}
