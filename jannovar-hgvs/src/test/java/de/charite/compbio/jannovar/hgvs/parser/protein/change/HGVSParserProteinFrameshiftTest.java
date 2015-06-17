package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_frameshiftContext;

/**
 * Parser for HGVS frame-shiftamino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinFrameshiftTest extends HGVSParserTestBase {

	@Test
	public void testFrameshiftOneLetterWithLength() {
		HGVSParser parser = buildParserForString("A124Tfs*23", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_frameshiftContext aa_change_extension = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char A) 124) (aa_char T) fs * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testFrameshiftThreeLetterWithLength() {
		HGVSParser parser = buildParserForString("Ala124Thrfs*23", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_frameshiftContext aa_change_extension = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char Ala) 124) (aa_char Thr) fs * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testFrameshiftOneLetterWithoutLength() {
		HGVSParser parser = buildParserForString("A124Tfs*?", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_frameshiftContext aa_change_extension = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char A) 124) (aa_char T) fs * ?)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testFrameshiftThreeLetterWithoutLength() {
		HGVSParser parser = buildParserForString("Ala124Thrfs*?", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_frameshiftContext aa_change_frameshift = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char Ala) 124) (aa_char Thr) fs * ?)",
				aa_change_frameshift.toStringTree(parser));
	}

	@Test
	public void testFrameshiftOneLetterShort() {
		HGVSParser parser = buildParserForString("A124fs", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_frameshiftContext aa_change_frameshift = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char A) 124) fs)",
				aa_change_frameshift.toStringTree(parser));
	}

	@Test
	public void testFrameshiftThreeLetterShort() {
		HGVSParser parser = buildParserForString("Ala124fs", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_frameshiftContext aa_change_frameshift = parser.aa_change_frameshift();
		Assert.assertEquals("(aa_change_frameshift (aa_point_location (aa_char Ala) 124) fs)",
				aa_change_frameshift.toStringTree(parser));
	}

}
