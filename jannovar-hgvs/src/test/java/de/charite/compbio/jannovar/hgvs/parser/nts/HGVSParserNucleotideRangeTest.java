package de.charite.compbio.jannovar.hgvs.parser.nts;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_rangeContext;

/**
 * Tests for parsing nucleotide ranges
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideRangeTest extends HGVSParserTestBase {

	@Test
	public void testCDSPositionsWithoutOffsets() {
		HGVSParser parser = buildParserForString("123_456", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 456))))",
				range.toStringTree(parser));
	}

	@Test
	public void testCDSPositionsWithOffsets() {
		HGVSParser parser = buildParserForString("123+987_456+765", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location (nt_number 123)) (nt_offset + (nt_number 987))) _ (nt_point_location (nt_base_location (nt_number 456)) (nt_offset + (nt_number 765))))",
				range.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionsWithoutOffsets() {
		HGVSParser parser = buildParserForString("*123_*456", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location * (nt_number 123))) _ (nt_point_location (nt_base_location * (nt_number 456))))",
				range.toStringTree(parser));
	}

	@Test
	public void testDownstreamPositionsWithOffsets() {
		HGVSParser parser = buildParserForString("*123+987_*456+765", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location * (nt_number 123)) (nt_offset + (nt_number 987))) _ (nt_point_location (nt_base_location * (nt_number 456)) (nt_offset + (nt_number 765))))",
				range.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionsWithoutOffsets() {
		HGVSParser parser = buildParserForString("-456_-123", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location - (nt_number 456))) _ (nt_point_location (nt_base_location - (nt_number 123))))",
				range.toStringTree(parser));
	}

	@Test
	public void testUpstreamPositionsWithOffsets() {
		HGVSParser parser = buildParserForString("-456+987_-123+765", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_rangeContext range = parser.nt_range();
		Assert.assertEquals(
				"(nt_range (nt_point_location (nt_base_location - (nt_number 456)) (nt_offset + (nt_number 987))) _ (nt_point_location (nt_base_location - (nt_number 123)) (nt_offset + (nt_number 765))))",
				range.toStringTree(parser));
	}

}
