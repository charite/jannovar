package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_deletionContext;

/**
 * Parser for HGVS deletion amino acid changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinDeletionTest extends HGVSParserTestBase {

	@Test
	public void testPointDeletionWithoutSequence() {
		HGVSParser parser = buildParserForString("Cys123del", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals("(aa_change_deletion (aa_point_location (aa_char Cys) 123) del)",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testRangeDeletionWithoutSequence() {
		HGVSParser parser = buildParserForString("Cys123_Arg125del", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals(
				"(aa_change_deletion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del)",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testPointDeletionWithSequence() {
		HGVSParser parser = buildParserForString("Cys123delCys", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals("(aa_change_deletion (aa_point_location (aa_char Cys) 123) del (aa_string Cys))",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testRangeDeletionWithSequence() {
		HGVSParser parser = buildParserForString("Cys123_Arg125delCysCysArg", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals(
				"(aa_change_deletion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del (aa_string Cys Cys Arg))",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testPointDeletionWithLength() {
		HGVSParser parser = buildParserForString("Cys123del1", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals("(aa_change_deletion (aa_point_location (aa_char Cys) 123) del 1)",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testRangeDeletionWithLength() {
		HGVSParser parser = buildParserForString("Cys123_Arg125del3", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals(
				"(aa_change_deletion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del 3)",
				aa_change_deletion.toStringTree(parser));
	}

}
