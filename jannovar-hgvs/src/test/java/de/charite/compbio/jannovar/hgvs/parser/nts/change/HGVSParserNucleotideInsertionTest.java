package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_insertionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS inversion nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideInsertionTest extends HGVSParserTestBase {

	@Test
	public void testWithRangeWithInsertedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124insAT", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_insertionContext nt_change_insertion = parser.nt_change_insertion();
		Assert.assertEquals(
				"(nt_change_insertion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ins (nt_string AT))",
				nt_change_insertion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithInsertedLength() {
		Antlr4HGVSParser parser = buildParserForString("123_124ins2", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_insertionContext nt_change_insertion = parser.nt_change_insertion();
		Assert.assertEquals(
				"(nt_change_insertion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ins (nt_number 2))",
				nt_change_insertion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutInsertString() {
		Antlr4HGVSParser parser = buildParserForString("123_124ins", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_insertionContext nt_change_insertion = parser.nt_change_insertion();
		Assert.assertEquals(
				"(nt_change_insertion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ins)",
				nt_change_insertion.toStringTree(parser));
	}

}
