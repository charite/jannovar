package de.charite.compbio.jannovar.hgvs.parser.nts;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_point_locationContext;

/**
 * Tests for parsing nucleotide point locations
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotidePointLocationTest extends HGVSParserTestBase {

	@Test
	public void testPositiveNumberAsPointLocation() {
		HGVSParser parser = buildParserForString("123", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location (nt_number 123)))", loc.toStringTree(parser));
	}

	@Test
	public void testUpstreamNumberAsPointLocation() {
		HGVSParser parser = buildParserForString("-123", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location - (nt_number 123)))", loc.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionAsPointLocation() {
		HGVSParser parser = buildParserForString("*123", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location * (nt_number 123)))", loc.toStringTree(parser));
	}

	@Test
	public void testPositiveNumberWithPositiveOffsetAsPointLocation() {
		HGVSParser parser = buildParserForString("123+4", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location (nt_number 123)) (nt_offset + (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testPositiveNumberWithNegativeOffsetAsPointLocation() {
		HGVSParser parser = buildParserForString("123-4", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location (nt_number 123)) (nt_offset - (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionWithPositiveOffsetAsPointLocation() {
		HGVSParser parser = buildParserForString("-123+4", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location - (nt_number 123)) (nt_offset + (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionWithNegativeOffsetAsPointLocation() {
		HGVSParser parser = buildParserForString("-123-4", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location - (nt_number 123)) (nt_offset - (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionWithPositiveOffsetAsPointLocation() {
		HGVSParser parser = buildParserForString("*123+4", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location * (nt_number 123)) (nt_offset + (nt_number 4)))",
				loc.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionWithNegativeOffsetAsPointLocation() {
		HGVSParser parser = buildParserForString("*123-4", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_point_locationContext loc = parser.nt_point_location();
		Assert.assertEquals("(nt_point_location (nt_base_location * (nt_number 123)) (nt_offset - (nt_number 4)))",
				loc.toStringTree(parser));
	}

}
