package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_substitutionContext;

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

}
