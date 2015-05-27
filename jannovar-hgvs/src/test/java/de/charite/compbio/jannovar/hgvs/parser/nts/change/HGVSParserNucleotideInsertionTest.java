package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_insertionContext;

/**
 * Parser for HGVS inversion nucleotide changes.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideInsertionTest extends HGVSParserTestBase {

	@Test
	public void testWithRangeWithInsertedString() {
		HGVSParser parser = buildParserForString("123_124insAT", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_insertionContext nt_change_insertion = parser.nt_change_insertion();
		Assert.assertEquals(
				"(nt_change_insertion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ins (nt_string AT))",
				nt_change_insertion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithInsertedLength() {
		HGVSParser parser = buildParserForString("123_124ins2", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_insertionContext nt_change_insertion = parser.nt_change_insertion();
		Assert.assertEquals(
				"(nt_change_insertion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ins (nt_number 2))",
				nt_change_insertion.toStringTree(parser));
	}

	@Test
	public void testWithRangeWithoutInsertString() {
		HGVSParser parser = buildParserForString("123_124ins", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_change_insertionContext nt_change_insertion = parser.nt_change_insertion();
		Assert.assertEquals(
				"(nt_change_insertion (nt_range (nt_point_location (nt_base_location (nt_number 123))) _ (nt_point_location (nt_base_location (nt_number 124)))) ins)",
				nt_change_insertion.toStringTree(parser));
	}

}
