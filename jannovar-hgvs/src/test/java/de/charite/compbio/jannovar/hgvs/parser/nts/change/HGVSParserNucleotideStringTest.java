package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_stringContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test parsing of nucleotide strings.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserNucleotideStringTest extends HGVSParserTestBase {

  @Test
  public void testWithDNA() {
    Antlr4HGVSParser parser =
        buildParserForString("CGAT", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Nt_stringContext nt_string = parser.nt_string();
    Assert.assertEquals("(nt_string CGAT)", nt_string.toStringTree(parser));
  }

  @Test
  public void testWithRNA() {
    Antlr4HGVSParser parser =
        buildParserForString("CGAU", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Nt_stringContext nt_string = parser.nt_string();
    Assert.assertEquals("(nt_string CGAU)", nt_string.toStringTree(parser));
  }
}
