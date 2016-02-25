package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_miscContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserProteinMiscChangeTest extends HGVSParserTestBase {

	@Test
	public void testDifficultToPredict() {
		Antlr4HGVSParser parser = buildParserForString("?", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc ?)", aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChange() {
		Antlr4HGVSParser parser = buildParserForString("=", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc =)", aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChangePredicted() {
		Antlr4HGVSParser parser = buildParserForString("(=)", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc ( = ))", aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoProtein() {
		Antlr4HGVSParser parser = buildParserForString("0", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc 0)", aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoProteinPredicted() {
		Antlr4HGVSParser parser = buildParserForString("0?", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc 0 ?)", aa_change_misc.toStringTree(parser));
	}

}
