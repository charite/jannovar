package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_frameshiftContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS frame-shiftamino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinFrameshiftTest extends HGVSParserTestBase {

	@Test
	public void testFrameshiftOneLetterWithLength() {
		Antlr4HGVSParser parser = buildParserForString("A124Tfs*23", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_frameshiftContext aa_change_extension = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char A) 124) (aa_char T) fs * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testFrameshiftThreeLetterWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Ala124Thrfs*23", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_frameshiftContext aa_change_extension = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char Ala) 124) (aa_char Thr) fs * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testFrameshiftOneLetterWithoutLength() {
		Antlr4HGVSParser parser = buildParserForString("A124Tfs*?", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_frameshiftContext aa_change_extension = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char A) 124) (aa_char T) fs * ?)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testFrameshiftThreeLetterWithoutLength() {
		Antlr4HGVSParser parser = buildParserForString("Ala124Thrfs*?", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_frameshiftContext aa_change_frameshift = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char Ala) 124) (aa_char Thr) fs * ?)",
				aa_change_frameshift.toStringTree(parser));
	}

	@Test
	public void testFrameshiftOneLetterShort() {
		Antlr4HGVSParser parser = buildParserForString("A124fs", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_frameshiftContext aa_change_frameshift = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char A) 124) fs)",
				aa_change_frameshift.toStringTree(parser));
	}

	@Test
	public void testFrameshiftThreeLetterShort() {
		Antlr4HGVSParser parser = buildParserForString("Ala124fs", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_frameshiftContext aa_change_frameshift = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char Ala) 124) fs)",
				aa_change_frameshift.toStringTree(parser));
	}

}
