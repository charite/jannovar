package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_duplicationContext;

/**
 * Parser for HGVS duplication nucleotide changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideDuplicationTest extends HGVSParserTestBase {

	@Test
	public void testWithPositionWithDuplicatedString() {
		HGVSParser parser = buildParserForString("123dup", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals("(nt_change_duplication (nt_point_location (nt_base_location (nt_number 123))) dup)",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithDuplicatedLength() {
		HGVSParser parser = buildParserForString("123dup1", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_point_location (nt_base_location (nt_number 123))) dup (nt_number 1))",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithPositionWithoutDuplicatedString() {
		HGVSParser parser = buildParserForString("123dupTA", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_deletion = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_point_location (nt_base_location (nt_number 123))) dup (nt_string TA))",
				nt_change_deletion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDuplicatedString() {
		HGVSParser parser = buildParserForString("123_124dupAT", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) dup (nt_string AT))",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithDuplicatedLength() {
		HGVSParser parser = buildParserForString("123_124dup2", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) dup (nt_number 2))",
				nt_change_duplication.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutDuplicatedString() {
		HGVSParser parser = buildParserForString("123_124dup", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_duplicationContext nt_change_duplication = parser.nt_change_duplication();
		Assert.assertEquals(
				"(nt_change_duplication (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) dup)",
				nt_change_duplication.toStringTree(parser));
	}

}
