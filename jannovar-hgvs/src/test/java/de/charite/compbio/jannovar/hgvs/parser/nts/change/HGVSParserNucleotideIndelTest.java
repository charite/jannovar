package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_indelContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS deletion nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideIndelTest extends HGVSParserTestBase {

	@Test
	public void testWithPositionWithStrings() {
		Antlr4HGVSParser parser = buildParserForString("123delCinsTCG", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_indelContext nt_change_indel = parser.nt_change_indel();
		Assert.assertEquals(
				"(nt_change_indel (nt_point_location (nt_base_location (nt_number 123))) del (nt_string C) ins (nt_string TCG))",
				nt_change_indel.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithLengths() {
		Antlr4HGVSParser parser = buildParserForString("123del1ins23", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_indelContext nt_change_indel = parser.nt_change_indel();
		Assert.assertEquals(
				"(nt_change_indel (nt_point_location (nt_base_location (nt_number 123))) del (nt_number 1) ins (nt_number 23))",
				nt_change_indel.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithoutStrings() {
		Antlr4HGVSParser parser = buildParserForString("123delins", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_indelContext nt_change_indel = parser.nt_change_indel();
		Assert.assertEquals("(nt_change_indel (nt_point_location (nt_base_location (nt_number 123))) del ins)",
				nt_change_indel.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithStrings() {
		Antlr4HGVSParser parser = buildParserForString("123_124delATinsGGTAT", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_indelContext nt_change_indel = parser.nt_change_indel();
		Assert.assertEquals(
				"(nt_change_indel (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del (nt_string AT) ins (nt_string GGTAT))",
				nt_change_indel.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithLengths() {
		Antlr4HGVSParser parser = buildParserForString("123_124del2ins4", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_indelContext nt_change_indel = parser.nt_change_indel();
		Assert.assertEquals(
				"(nt_change_indel (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del (nt_number 2) ins (nt_number 4))",
				nt_change_indel.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutStrings() {
		Antlr4HGVSParser parser = buildParserForString("123_124delins", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_indelContext nt_change_indel = parser.nt_change_indel();
		Assert.assertEquals(
				"(nt_change_indel (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del ins)",
				nt_change_indel.toStringTree(parser));
	}

}
