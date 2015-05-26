package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Aa_change_extensionContext;

/**
 * Parser for HGVS extension amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinExtensionTest extends HGVSParserTestBase {

	@Test
	public void testExtensionOneLetterWithLength() {
		HGVSParser parser = buildParserForString("A124Text*23", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char A) 124) (aa_char T) ext * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testExtensionThreeLetterWithLength() {
		HGVSParser parser = buildParserForString("Ala124Thrext*23", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char Ala) 124) (aa_char Thr) ext * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testExtensionOneLetterWithoutLength() {
		HGVSParser parser = buildParserForString("A124Text*?", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char A) 124) (aa_char T) ext * ?)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testExtensionThreeLetterWithoutLength() {
		HGVSParser parser = buildParserForString("Ala124Thrext*?", HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char Ala) 124) (aa_char Thr) ext * ?)",
				aa_change_extension.toStringTree(parser));
	}

}
