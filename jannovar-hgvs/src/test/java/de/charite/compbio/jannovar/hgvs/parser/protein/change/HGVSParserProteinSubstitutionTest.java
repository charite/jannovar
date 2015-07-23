package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_substitutionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

// TODO(holtgrew): "p.Met1?" cannot be represented by our classes yet, same with p.Met1=

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinSubstitutionTest extends HGVSParserTestBase {

	@Test
	public void testOneLetter() {
		Antlr4HGVSParser parser = buildParserForString("C123A", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char C) 123) (aa_char A))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testThreeLetters() {
		Antlr4HGVSParser parser = buildParserForString("Cys123Arg", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char Cys) 123) (aa_char Arg))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMetSubstitutionOneLetter() {
		Antlr4HGVSParser parser = buildParserForString("M123A", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char M) 123) (aa_char A))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMetSubstitutionThreeLetter() {
		Antlr4HGVSParser parser = buildParserForString("Met123Ala", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char Met) 123) (aa_char Ala))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMet1SubstitutionOneLetter() {
		Antlr4HGVSParser parser = buildParserForString("M1A", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char M) 1) (aa_char A))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMet1SubstitutionThreeLetter() {
		Antlr4HGVSParser parser = buildParserForString("Met1Ala", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char Met) 1) (aa_char Ala))",
				aa_change_substitution.toStringTree(parser));
	}

}
