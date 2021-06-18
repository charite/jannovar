package de.charite.compbio.jannovar.hgvs.parser.nts.change;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test parser for sequenced repeats.
 *
 * Examples from https://varnomen.hgvs.org/recommendations/DNA/variant/repeated/:
 *
 * NC_000003.12:c.89AGC[13]
 * NC_000003.12:g.63912687AGC[13]
 * NM_002024.5:c.-129CGG[79]
 * LRG_763t1:c.53AGC[23]
 * NM_000492.3:c.1210-12T[7]
 * NC_000012.11:g.112036755_112036823CTG[9]TTG[1]CTG[13]
 * NM_000492.3:c.1210-33_1210-6GT[11]T[6]
 * NM_021080.3:c.-136-75952ATTTT[15]
 * NM_002024.5:c.-128_-69GGC[10]GGA[1]GGC[9]GGA[1]GGC[10]
 *
 * NM_023035.2(CACNA1A):c.6955CAG[26]
 *
 * @author Mark Woon
 */
public class HGVSParserNucleotideSequencedRepeatTest extends HGVSParserTestBase {

  @Test
  public void testLengthOne() {
    Antlr4HGVSParser parser = buildParserForString("123T[3]", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Antlr4HGVSParser.Nt_change_sequenced_repeatContext nt_change_repeat = parser.nt_change_sequenced_repeat();
    Assertions.assertEquals("(nt_change_sequenced_repeat (nt_point_location (nt_base_location (nt_number 123))) (nt_change_repeat_sequence T [ 3 ]))",
        nt_change_repeat.toStringTree(parser));
  }

  @Test
  public void testLengthTwo() {
    Antlr4HGVSParser parser = buildParserForString("-123TA[13]", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Antlr4HGVSParser.Nt_change_sequenced_repeatContext nt_change_repeat = parser.nt_change_sequenced_repeat();
    Assertions.assertEquals("(nt_change_sequenced_repeat (nt_point_location (nt_base_location - (nt_number 123))) (nt_change_repeat_sequence TA [ 13 ]))",
        nt_change_repeat.toStringTree(parser));
  }

  @Test
  public void testRange() {
    Antlr4HGVSParser parser = buildParserForString("755_823CTG[9]TTG[1]CTG[13]", Antlr4HGVSLexer.NUCLEOTIDE_CHANGE, false);
    Antlr4HGVSParser.Nt_change_sequenced_repeatContext nt_change_repeat = parser.nt_change_sequenced_repeat();
    Assertions.assertEquals("(nt_change_sequenced_repeat (nt_range (nt_point_location (nt_base_location (nt_number 755))) _ (nt_point_location (nt_base_location (nt_number 823)))) (nt_change_repeat_sequence CTG [ 9 ]) (nt_change_repeat_sequence TTG [ 1 ]) (nt_change_repeat_sequence CTG [ 13 ]))",
        nt_change_repeat.toStringTree(parser));
  }
}
