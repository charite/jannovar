package de.charite.compbio.jannovar.hgvs.parser.nts;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_rangeContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Tests for parsing nucleotide ranges
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserNucleotideRangeTest extends HGVSParserTestBase {

	@Test
	public void testCDSPositionsWithoutOffsets() {
		Antlr4HGVSParser parser = buildParserForString("123_456", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 456))))",
				range.toStringTree(parser));
	}

	@Test
	public void testCDSPositionsWithOffsets() {
		Antlr4HGVSParser parser = buildParserForString("123+987_456+765", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location (nt_number 123)) (nt_offset + (nt_number 987))) _ (nt_point_location (nt_base_location (nt_number 456)) (nt_offset + (nt_number 765))))",
				range.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionsWithoutOffsets() {
		Antlr4HGVSParser parser = buildParserForString("*123_*456", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location * (nt_number 123))) _ (nt_point_location (nt_base_location * (nt_number 456))))",
				range.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionsWithOffsets() {
		Antlr4HGVSParser parser = buildParserForString("*123+987_*456+765", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location * (nt_number 123)) (nt_offset + (nt_number 987))) _ (nt_point_location (nt_base_location * (nt_number 456)) (nt_offset + (nt_number 765))))",
				range.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionsWithoutOffsets() {
		Antlr4HGVSParser parser = buildParserForString("-456_-123", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location - (nt_number 456))) _ (nt_point_location (nt_base_location - (nt_number 123))))",
				range.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionsWithOffsets() {
		Antlr4HGVSParser parser = buildParserForString("-456+987_-123+765", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location - (nt_number 456)) (nt_offset + (nt_number 987))) _ (nt_point_location (nt_base_location - (nt_number 123)) (nt_offset + (nt_number 765))))",
				range.toStringTree(parser));
	}

}
