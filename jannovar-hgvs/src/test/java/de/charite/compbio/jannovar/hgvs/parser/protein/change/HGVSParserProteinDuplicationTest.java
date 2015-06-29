package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_duplicationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS duplication protein changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinDuplicationTest extends HGVSParserTestBase {

	@Test
	public void testPointDuplicationWithoutSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123dup", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals("(aa_change_duplication (aa_point_location (aa_char Cys) 123) dup)",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testRangeDuplicationWithoutSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125dup", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals(
				"(aa_change_duplication (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) dup)",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testPointDuplicationWithSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123dupCys", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals("(aa_change_duplication (aa_point_location (aa_char Cys) 123) dup (aa_string Cys))",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testRangeDuplicationWithSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125dupCysCysArg", Antlr4HGVSLexer.AMINO_ACID_CHANGE,
				false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals(
				"(aa_change_duplication (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) dup (aa_string Cys Cys Arg))",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testPointDuplicationWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Cys123dup1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals("(aa_change_duplication (aa_point_location (aa_char Cys) 123) dup 1)",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testRangeDuplicationWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125dup3", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals(
				"(aa_change_duplication (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) dup 3)",
				aa_change_Duplication.toStringTree(parser));
	}

}
