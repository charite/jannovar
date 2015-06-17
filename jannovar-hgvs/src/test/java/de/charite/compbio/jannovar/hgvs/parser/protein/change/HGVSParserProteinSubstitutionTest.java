package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_substitutionContext;

// TODO(holtgrew): "p.Met1?" cannot be represented by our classes yet, same with p.Met1=

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinSubstitutionTest extends HGVSParserTestBase {

	@Test
	public void testOneLetter() {
		HGVSParser parser = buildParserForString("C123A", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char C) 123) (aa_char A))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testThreeLetters() {
		HGVSParser parser = buildParserForString("Cys123Arg", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char Cys) 123) (aa_char Arg))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMetSubstitutionOneLetter() {
		HGVSParser parser = buildParserForString("M123A", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char M) 123) (aa_char A))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMetSubstitutionThreeLetter() {
		HGVSParser parser = buildParserForString("Met123Ala", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char Met) 123) (aa_char Ala))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMet1SubstitutionOneLetter() {
		HGVSParser parser = buildParserForString("M1A", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char M) 1) (aa_char A))",
				aa_change_substitution.toStringTree(parser));
	}

	@Test
	public void testMet1SubstitutionThreeLetter() {
		HGVSParser parser = buildParserForString("Met1Ala", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_substitutionContext aa_change_substitution = parser.aa_change_substitution();
		Assert.assertEquals("(aa_change_substitution (aa_point_location (aa_char Met) 1) (aa_char Ala))",
				aa_change_substitution.toStringTree(parser));
	}

}
