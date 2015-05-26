package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_insertionContext;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinInsertionTest extends HGVSParserTestBase {

	@Test
	public void testInsertionWithoutSequenceTest() {
		HGVSParser parser = buildParserForString("Cys123_Ala124ins", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_insertionContext aa_change_insertion = parser.aa_change_insertion();
		Assert.assertEquals(
				"(aa_change_insertion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 124)) ins)",
				aa_change_insertion.toStringTree(parser));
	}

	@Test
	public void testInsertionWithSequenceTest() {
		HGVSParser parser = buildParserForString("Cys123_Ala124insThrGlu", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_insertionContext aa_change_insertion = parser.aa_change_insertion();
		Assert.assertEquals(
				"(aa_change_insertion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 124)) ins (aa_string Thr Glu))",
				aa_change_insertion.toStringTree(parser));
	}

	@Test
	public void testInsertionWithLengthTest() {
		HGVSParser parser = buildParserForString("Cys123_Ala124ins2", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_insertionContext aa_change_insertion = parser.aa_change_insertion();
		Assert.assertEquals(
				"(aa_change_insertion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 124)) ins 2)",
				aa_change_insertion.toStringTree(parser));
	}

}
