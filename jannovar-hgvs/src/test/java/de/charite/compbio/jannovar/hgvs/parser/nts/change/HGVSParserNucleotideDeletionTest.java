package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_deletionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS deletion nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideDeletionTest extends HGVSParserTestBase {

	@Test
	public void testWithPositionWithoutDeletedString() {
		Antlr4HGVSParser parser = buildParserForString("123del", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals("(nt_change_deletion (nt_point_location (nt_base_location (nt_number 123))) del)",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithDeletedLength() {
		Antlr4HGVSParser parser = buildParserForString("123del1", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_point_location (nt_base_location (nt_number 123))) del (nt_number 1))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithDeletedString() {
		Antlr4HGVSParser parser = buildParserForString("123delTA", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_point_location (nt_base_location (nt_number 123))) del (nt_string TA))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDeletedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124delAT", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del (nt_string AT))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDeletedLength() {
		Antlr4HGVSParser parser = buildParserForString("123_124del2", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del (nt_number 2))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutDeletedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124del", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_deletionContext nt_change_deletion = parser.nt_change_deletion();
		Assert.assertEquals(
				"(nt_change_deletion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) del)",
				nt_change_deletion.toStringTree(parser));
	}

}
