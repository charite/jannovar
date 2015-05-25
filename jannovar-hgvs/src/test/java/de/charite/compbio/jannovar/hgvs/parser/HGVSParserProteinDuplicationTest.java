package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_duplicationContext;

/**
 * Parser for HGVS duplication protein changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinDuplicationTest extends HGVSParserTestBase {

	@Test
	public void testPointDuplicationWithoutSequence() {
		HGVSParser parser = buildParserForString("Cys123dup", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals("(aa_change_duplication (aa_point_location (aa_char Cys) 123) dup)",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testRangeDuplicationWithoutSequence() {
		HGVSParser parser = buildParserForString("Cys123_Arg125dup", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals(
				"(aa_change_duplication (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) dup)",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testPointDuplicationWithSequence() {
		HGVSParser parser = buildParserForString("Cys123dupCys", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals("(aa_change_duplication (aa_point_location (aa_char Cys) 123) dup (aa_string Cys))",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testRangeDuplicationWithSequence() {
		HGVSParser parser = buildParserForString("Cys123_Arg125dupCysCysArg", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals(
				"(aa_change_duplication (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) dup (aa_string Cys Cys Arg))",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testPointDuplicationWithLength() {
		HGVSParser parser = buildParserForString("Cys123dup1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals("(aa_change_duplication (aa_point_location (aa_char Cys) 123) dup 1)",
				aa_change_Duplication.toStringTree(parser));
	}

	@Test
	public void testRangeDuplicationWithLength() {
		HGVSParser parser = buildParserForString("Cys123_Arg125dup3", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_duplicationContext aa_change_Duplication = parser.aa_change_duplication();
		Assert.assertEquals(
				"(aa_change_duplication (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) dup 3)",
				aa_change_Duplication.toStringTree(parser));
	}

}
