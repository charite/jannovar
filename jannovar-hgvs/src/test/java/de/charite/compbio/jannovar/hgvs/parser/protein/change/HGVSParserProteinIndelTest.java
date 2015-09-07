package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_indelContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS indel amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinIndelTest extends HGVSParserTestBase {

	@Test
	public void testPointIndelWithoutSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123delins", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals("(aa_change_indel (aa_point_location (aa_char Cys) 123) del ins)",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testRangeIndellWithoutSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125delins", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del ins)",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testPointIndelWithSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123delCysinsArg", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_point_location (aa_char Cys) 123) del (aa_string Cys) ins (aa_string Arg))",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testRangeIndellWithSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125delCysCysArginsAla",
				Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del (aa_string Cys Cys Arg) ins (aa_string Ala))",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testPointIndelWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Cys123del1ins3", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals("(aa_change_indel (aa_point_location (aa_char Cys) 123) del 1 ins 3)",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testRangeIndellWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125del3ins1", Antlr4HGVSLexer.AMINO_ACID_CHANGE,
				false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del 3 ins 1)",
				aa_change_indel.toStringTree(parser));
	}

}
