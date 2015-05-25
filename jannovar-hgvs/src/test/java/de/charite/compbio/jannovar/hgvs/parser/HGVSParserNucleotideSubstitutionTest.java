package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_miscContext;

/**
 * Parser for HGVS substitution nucleotide changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideSubstitutionTest extends HGVSParserTestBase {

	@Test
	public void testSameAsDNA() {
		HGVSParser parser = buildParserForString("(?)", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( ? ))", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testUnknownEffect() {
		HGVSParser parser = buildParserForString("?", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ?)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testSplicingAffected() {
		HGVSParser parser = buildParserForString("spl?", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc spl ?)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testSplicingAffectedOnlyPredicted() {
		HGVSParser parser = buildParserForString("(spl?)", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( spl ? ))", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChange() {
		HGVSParser parser = buildParserForString("spl?", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc spl ?)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChangeOnlyPredicted() {
		HGVSParser parser = buildParserForString("(spl?)", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( spl ? ))", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoRNA() {
		HGVSParser parser = buildParserForString("0", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc 0)", nt_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoRNAOnlyPredicted() {
		HGVSParser parser = buildParserForString("(0)", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_miscContext nt_change_misc = parser.nt_change_misc();
		Assert.assertEquals("(nt_change_misc ( 0 ))", nt_change_misc.toStringTree(parser));
	}

}
