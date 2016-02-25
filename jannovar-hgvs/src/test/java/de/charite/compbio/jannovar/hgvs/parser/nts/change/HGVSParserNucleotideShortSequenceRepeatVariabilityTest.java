package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_ssrContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserNucleotideShortSequenceRepeatVariabilityTest extends HGVSParserTestBase {

	@Test
	public void testLengthOne() {
		Antlr4HGVSParser parser = buildParserForString("123(3_4)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_ssrContext nt_change_ssr = parser.nt_change_ssr();
		Assert.assertEquals("(nt_change_ssr (nt_point_location (nt_base_location (nt_number 123))) ( 3 _ 4 ))",
				nt_change_ssr.toStringTree(parser));
	}

	@Test
	public void testLengthTwo() {
		Antlr4HGVSParser parser = buildParserForString("123_124(3_4)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_ssrContext nt_change_ssr = parser.nt_change_ssr();
		Assert.assertEquals(
				"(nt_change_ssr (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ( 3 _ 4 ))",
				nt_change_ssr.toStringTree(parser));
	}

}
