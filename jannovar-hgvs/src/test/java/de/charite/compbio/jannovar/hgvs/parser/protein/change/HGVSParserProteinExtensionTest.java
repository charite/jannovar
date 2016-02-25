package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_extensionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

// TODO(holtgrewe): p.Met1ext is not representable using the classes yet

/**
 * Parser for HGVS extension amino acid changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserProteinExtensionTest extends HGVSParserTestBase {

	@Test
	public void testExtensionOneLetterWithLength() {
		Antlr4HGVSParser parser = buildParserForString("A124Text*23", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char A) 124) (aa_char T) ext * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testExtensionThreeLetterWithLength() {
		Antlr4HGVSParser parser = buildParserForString("Ala124Thrext*23", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char Ala) 124) (aa_char Thr) ext * 23)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testExtensionOneLetterWithoutLength() {
		Antlr4HGVSParser parser = buildParserForString("A124Text*?", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char A) 124) (aa_char T) ext * ?)",
				aa_change_extension.toStringTree(parser));
	}

	@Test
	public void testExtensionThreeLetterWithoutLength() {
		Antlr4HGVSParser parser = buildParserForString("Ala124Thrext*?", Antlr4HGVSLexer.AMINO_ACID_CHANGE, false);
		Aa_change_extensionContext aa_change_extension = parser.aa_change_extension();
		Assert.assertEquals("(aa_change_extension (aa_point_location (aa_char Ala) 124) (aa_char Thr) ext * ?)",
				aa_change_extension.toStringTree(parser));
	}

}
