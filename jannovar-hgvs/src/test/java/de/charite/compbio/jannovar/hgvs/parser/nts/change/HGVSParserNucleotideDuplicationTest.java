package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_duplicationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS duplication nucleotide changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserNucleotideDuplicationTest extends HGVSParserTestBase {

	@Test
	public void testWithPositionWithDuplicatedString() {
		Antlr4HGVSParser parser = buildParserForString("123dup", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals("(nt_change_duplication (nt_point_location (nt_base_location (nt_number 123))) dup)",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithDuplicatedLength() {
		Antlr4HGVSParser parser = buildParserForString("123dup1", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_point_location (nt_base_location (nt_number 123))) dup (nt_number 1))",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithoutDuplicatedString() {
		Antlr4HGVSParser parser = buildParserForString("123dupTA", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_deletion = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_point_location (nt_base_location (nt_number 123))) dup (nt_string TA))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDuplicatedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124dupAT", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) dup (nt_string AT))",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDuplicatedLength() {
		Antlr4HGVSParser parser = buildParserForString("123_124dup2", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) dup (nt_number 2))",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutDuplicatedString() {
		Antlr4HGVSParser parser = buildParserForString("123_124dup", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) dup)",
				nt_change_duplication.toStringTree(parser));
	}

}
