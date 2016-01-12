package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_miscContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS substitution nucleotide changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserNucleotideSubstitutionTest extends HGVSParserTestBase {

	@Test
	public void testSameAsDNA() {
		Antlr4HGVSParser parser = buildParserForString("(?)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( ? ))", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testUnknownEffect() {
		Antlr4HGVSParser parser = buildParserForString("?", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ?)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testSplicingAffected() {
		Antlr4HGVSParser parser = buildParserForString("spl?", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc spl ?)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testSplicingAffectedOnlyPredicted() {
		Antlr4HGVSParser parser = buildParserForString("(spl?)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( spl ? ))", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChange() {
		Antlr4HGVSParser parser = buildParserForString("spl?", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc spl ?)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChangeOnlyPredicted() {
		Antlr4HGVSParser parser = buildParserForString("(spl?)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( spl ? ))", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoRNA() {
		Antlr4HGVSParser parser = buildParserForString("0", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc 0)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoRNAOnlyPredicted() {
		Antlr4HGVSParser parser = buildParserForString("(0)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( 0 ))", nt_change_misc.toStringTree(parser));
	}

}
