package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_inversionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS insertion nucleotide changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserNucleotideInversionTest extends HGVSParserTestBase {

	@Test
	public void testWithRangeWithDeletedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124invAT", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_inversionContext nt_change_inversion = parser.nt_change_inversion();
		Assert.assertEquals(
				"(nt_change_inversion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) inv (nt_string AT))",
				nt_change_inversion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDeletedLength() {
		Antlr4HGVSParser parser = buildParserForString("123_124inv2", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_inversionContext nt_change_inversion = parser.nt_change_inversion();
		Assert.assertEquals(
				"(nt_change_inversion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) inv (nt_number 2))",
				nt_change_inversion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutDeletedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124inv", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_inversionContext nt_change_inversion = parser.nt_change_inversion();
		Assert.assertEquals(
				"(nt_change_inversion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) inv)",
				nt_change_inversion.toStringTree(parser));
	}

}
