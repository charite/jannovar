package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_ssrContext;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinShortSequenceRepeatVariabilityTest extends HGVSParserTestBase {

	@Test
	public void testLengthOneOneLetter() {
		HGVSParser parser = buildParserForString("C123(3_4)", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_ssrContext aa_change_ssr = parser.aa_change_ssr();
		Assert.assertEquals("(aa_change_ssr (aa_point_location (aa_char C) 123) ( 3 _ 4 ))",
				aa_change_ssr.toStringTree(parser));
	}

	@Test
	public void testLengthOneThreeLetter() {
		HGVSParser parser = buildParserForString("Cys123(3_4)", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_ssrContext aa_change_ssr = parser.aa_change_ssr();
		Assert.assertEquals("(aa_change_ssr (aa_point_location (aa_char Cys) 123) ( 3 _ 4 ))",
				aa_change_ssr.toStringTree(parser));
	}

	@Test
	public void testLengthTwoOneLetter() {
		HGVSParser parser = buildParserForString("C123_A124(3_4)", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_ssrContext aa_change_ssr = parser.aa_change_ssr();
		Assert.assertEquals(
				"(aa_change_ssr (aa_range (aa_point_location (aa_char C) 123) _ (aa_point_location (aa_char A) 124)) ( 3 _ 4 ))",
				aa_change_ssr.toStringTree(parser));
	}

	@Test
	public void testLengthTwoThreeLetter() {
		HGVSParser parser = buildParserForString("Cys123_Arg124(3_4)", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_ssrContext aa_change_ssr = parser.aa_change_ssr();
		Assert.assertEquals(
				"(aa_change_ssr (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 124)) ( 3 _ 4 ))",
				aa_change_ssr.toStringTree(parser));
	}

}
