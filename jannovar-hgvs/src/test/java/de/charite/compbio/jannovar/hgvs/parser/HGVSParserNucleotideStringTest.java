package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_stringContext;

/**
 * Test parsing of nucleotide strings.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserNucleotideStringTest extends HGVSParserTestBase {

	@Test
	public void testWithDNA() {
		HGVSParser parser = buildParserForString("CGAT", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_stringContext nt_string = parser.nt_string();
		Assert.assertEquals("(nt_string CGAT)", nt_string.toStringTree(parser));
	}

	@Test
	public void testWithRNA() {
		HGVSParser parser = buildParserForString("CGAU", HGVSLexer.NUCLEOTIDE_CHANGE, false);
		Nt_stringContext nt_string = parser.nt_string();
		Assert.assertEquals("(nt_string CGAU)", nt_string.toStringTree(parser));
	}

}
