package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_deletionContext;

/**
 * Parser for HGVS deletion nucleotide changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideDeletionTest extends HGVSParserTestBase {

	@Test
	public void testWithPositionWithoutDeletedString() {
		HGVSParser parser = buildParserForString("123del", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals("(nt_change_deletion (nt_point_location (nt_base_location (nt_number 123))) del)",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithDeletedLength() {
		HGVSParser parser = buildParserForString("123del1", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_point_location (nt_base_location (nt_number 123))) del (nt_number 1))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithDeletedString() {
		HGVSParser parser = buildParserForString("123delTA", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_point_location (nt_base_location (nt_number 123))) del (nt_string TA))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDeletedString() {
		HGVSParser parser = buildParserForString("123_124delAT", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del (nt_string AT))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDeletedLength() {
		HGVSParser parser = buildParserForString("123_124del2", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del (nt_number 2))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutDeletedString() {
		HGVSParser parser = buildParserForString("123_124del", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del)",
				nt_change_deletion.toStringTree(parser));
	}

}
