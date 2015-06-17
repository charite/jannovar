package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_miscContext;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinMiscChangeTest extends HGVSParserTestBase {

	@Test
	public void testDifficultToPredict() {
		HGVSParser parser = buildParserForString("?", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc ?)",
				aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChange() {
		HGVSParser parser = buildParserForString("=", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc =)",
				aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoChangePredicted() {
		HGVSParser parser = buildParserForString("(=)", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc ( = ))",
				aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoProtein() {
		HGVSParser parser = buildParserForString("0", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc 0)",
				aa_change_misc.toStringTree(parser));
	}

	@Test
	public void testNoProteinPredicted() {
		HGVSParser parser = buildParserForString("0?", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_miscContext aa_change_misc = parser.aa_change_misc();
		Assert.assertEquals("(aa_change_misc 0 ?)",
				aa_change_misc.toStringTree(parser));
	}

}
