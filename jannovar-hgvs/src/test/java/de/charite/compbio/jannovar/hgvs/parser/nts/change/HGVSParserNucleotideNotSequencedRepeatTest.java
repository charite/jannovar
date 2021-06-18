package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Parser for HGVS not-sequenced repeats.
 *
 * Examples from https://varnomen.hgvs.org/recommendations/DNA/variant/repeated/:
 *
 * NM_000333.3:c.(4_246)ins(9)
 * NC_000003.12:g.(63912602_63912844)del(15)
 * NM_002024.5:c.(-144_-16)ins(1800_2400)
 *
 * @author Mark Woon
 */
public class HGVSParserNucleotideNotSequencedRepeatTest extends HGVSParserTestBase {

  @Test
  public void testIns() {
    Antlr4HGVSParser parser = buildParserForString("4_246ins(9)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Antlr4HGVSParser.Nt_change_not_sequenced_repeatContext repeat = parser.nt_change_not_sequenced_repeat();
    Assertions.assertEquals("(nt_change_not_sequenced_repeat (nt_range (nt_point_location (nt_base_location (nt_number 4))) _ (nt_point_location (nt_base_location (nt_number 246)))) ins ( 9 ))",
        repeat.toStringTree(parser));
  }

  @Test
  public void testDel() {
    Antlr4HGVSParser parser = buildParserForString("-14_-5del(15)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Antlr4HGVSParser.Nt_change_not_sequenced_repeatContext repeat = parser.nt_change_not_sequenced_repeat();
    Assertions.assertEquals("(nt_change_not_sequenced_repeat (nt_range (nt_point_location (nt_base_location - (nt_number 14))) _ (nt_point_location (nt_base_location - (nt_number 5)))) del ( 15 ))",
        repeat.toStringTree(parser));
  }

  @Test
  public void testRange() {
    Antlr4HGVSParser parser = buildParserForString("-144_-16ins(1800_2400)", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Antlr4HGVSParser.Nt_change_not_sequenced_repeatContext repeat = parser.nt_change_not_sequenced_repeat();
    Assertions.assertEquals("(nt_change_not_sequenced_repeat (nt_range (nt_point_location (nt_base_location - (nt_number 144))) _ (nt_point_location (nt_base_location - (nt_number 16)))) ins ( 1800 _ 2400 ))",
        repeat.toStringTree(parser));
  }
}
