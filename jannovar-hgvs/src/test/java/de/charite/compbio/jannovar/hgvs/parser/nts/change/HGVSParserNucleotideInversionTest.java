package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_inversionContext;

/**
 * Parser for HGVS insertion nucleotide changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideInversionTest extends HGVSParserTestBase {

	@Test
	public void testWithRangeWithDeletedString() {
		HGVSParser parser = buildParserForString("123_124invAT", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_inversionContext nt_change_inversion = parser.nt_change_inversion();
		Assert.assertEquals(
				"(nt_change_inversion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) inv (nt_string AT))",
				nt_change_inversion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDeletedLength() {
		HGVSParser parser = buildParserForString("123_124inv2", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_inversionContext nt_change_inversion = parser.nt_change_inversion();
		Assert.assertEquals(
				"(nt_change_inversion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) inv (nt_number 2))",
				nt_change_inversion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutDeletedString() {
		HGVSParser parser = buildParserForString("123_124inv", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_inversionContext nt_change_inversion = parser.nt_change_inversion();
		Assert.assertEquals(
				"(nt_change_inversion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) inv)",
				nt_change_inversion.toStringTree(parser));
	}

}
