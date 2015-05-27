package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_indelContext;

/**
 * Parser for HGVS indel amino acid changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinIndelTest extends HGVSParserTestBase {

	@Test
	public void testPointIndelWithoutSequence() {
		HGVSParser parser = buildParserForString("Cys123delins", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals("(aa_change_indel (aa_point_location (aa_char Cys) 123) del ins)",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testRangeIndellWithoutSequence() {
		HGVSParser parser = buildParserForString("Cys123_Arg125delins", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del ins)",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testPointIndelWithSequence() {
		HGVSParser parser = buildParserForString("Cys123delCysinsArg", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_point_location (aa_char Cys) 123) del (aa_string Cys) ins (aa_string Arg))",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testRangeIndellWithSequence() {
		HGVSParser parser = buildParserForString("Cys123_Arg125delCysCysArginsAla", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del (aa_string Cys Cys Arg) ins (aa_string Ala))",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testPointIndelWithLength() {
		HGVSParser parser = buildParserForString("Cys123del1ins3", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals("(aa_change_indel (aa_point_location (aa_char Cys) 123) del 1 ins 3)",
				aa_change_indel.toStringTree(parser));
	}

	@Test
	public void testRangeIndellWithLength() {
		HGVSParser parser = buildParserForString("Cys123_Arg125del3ins1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_indelContext aa_change_indel = parser.aa_change_indel();
		Assert.assertEquals(
				"(aa_change_indel (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del 3 ins 1)",
				aa_change_indel.toStringTree(parser));
	}

}
