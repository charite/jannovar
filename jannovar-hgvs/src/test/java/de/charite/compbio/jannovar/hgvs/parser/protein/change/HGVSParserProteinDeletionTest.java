package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_deletionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinDeletionTest extends HGVSParserTestBase {

	@Test
	public void testPointDeletionWithoutSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123del", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals("(aa_change_deletion (aa_point_location (aa_char Cys) 123) del)",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testRangeDeletionWithoutSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125del", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals(
				"(aa_change_deletion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del)",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testPointDeletionWithSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123delCys", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals("(aa_change_deletion (aa_point_location (aa_char Cys) 123) del (aa_string Cys))",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testRangeDeletionWithSequence() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125delCysCysArg", Antlr4HGVSLexer.AMINO_ACID_CHANGE,
				false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals(
				"(aa_change_deletion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del (aa_string Cys Cys Arg))",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testPointDeletionWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Cys123del1", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals("(aa_change_deletion (aa_point_location (aa_char Cys) 123) del 1)",
				aa_change_deletion.toStringTree(parser));
	}

	@Test
	public void testRangeDeletionWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Arg125del3", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_deletionContext aa_change_deletion = parser.aa_change_deletion();
		Assert.assertEquals(
				"(aa_change_deletion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Arg) 125)) del 3)",
				aa_change_deletion.toStringTree(parser));
	}

}
