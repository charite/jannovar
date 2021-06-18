package de.charite.compbio.jannovar.hgvs.nts.change;

import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.charite.compbio.jannovar.hgvs.nts.change.NucleotideNotSequencedRepeat.InDelType.DEL;
import static de.charite.compbio.jannovar.hgvs.nts.change.NucleotideNotSequencedRepeat.InDelType.INS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * Unit test for {@link NucleotideNotSequencedRepeat}.
 *
 * @author Mark Woon
 */
class NucleotideNotSequencedRepeatTest {
  private NucleotideNotSequencedRepeat firstRepeatSeqA1;
  private NucleotideNotSequencedRepeat firstRepeatSeqA2;
  private NucleotideNotSequencedRepeat firstRepeatSeqB1;
  private NucleotideNotSequencedRepeat firstRepeatSeqB2;
  private NucleotideNotSequencedRepeat secondRepeatSeqA1;
  private NucleotideNotSequencedRepeat secondRepeatSeqA2;
  private NucleotideNotSequencedRepeat secondRepeatSeqB1;
  private NucleotideNotSequencedRepeat secondRepeatSeqB2;


  @BeforeEach
  public void setUp() {
    NucleotideRange range1 = new NucleotideRange(NucleotidePointLocation.build(1),
        NucleotidePointLocation.build(1));
    NucleotideRange range2 = new NucleotideRange(NucleotidePointLocation.build(1),
        NucleotidePointLocation.build(5));


    firstRepeatSeqA1 = new NucleotideNotSequencedRepeat(false, range1, INS, 2, 2);
    firstRepeatSeqA2 = new NucleotideNotSequencedRepeat(false, range2, INS, 2, 4);
    firstRepeatSeqB1 = new NucleotideNotSequencedRepeat(false, range1, DEL, 2, 2);
    firstRepeatSeqB2 = new NucleotideNotSequencedRepeat(false, range2, DEL, 2, 4);

    secondRepeatSeqA1 = new NucleotideNotSequencedRepeat(false, range1, INS, 2, 2);
    secondRepeatSeqA2 = new NucleotideNotSequencedRepeat(false, range2, INS, 2, 4);
    secondRepeatSeqB1 = new NucleotideNotSequencedRepeat(false, range1, DEL, 2, 2);
    secondRepeatSeqB2 = new NucleotideNotSequencedRepeat(false, range2, DEL, 2, 4);
  }

  @Test
  public void testEquals() {
    assertEquals(firstRepeatSeqA1, secondRepeatSeqA1);
    assertEquals(firstRepeatSeqA2, secondRepeatSeqA2);
    assertEquals(firstRepeatSeqB1, secondRepeatSeqB1);
    assertEquals(firstRepeatSeqB2, secondRepeatSeqB2);
    assertNotEquals(firstRepeatSeqA1, firstRepeatSeqA2);
    assertNotEquals(firstRepeatSeqA1, firstRepeatSeqB1);
    assertNotEquals(firstRepeatSeqA1, firstRepeatSeqB2);
    assertNotEquals(firstRepeatSeqA2, firstRepeatSeqB1);
    assertNotEquals(firstRepeatSeqA2, firstRepeatSeqB2);
    assertNotEquals(firstRepeatSeqB1, firstRepeatSeqB2);
  }

  @Test
  public void testToHGVSString() {
    assertEquals("2ins(2)", firstRepeatSeqA1.toHGVSString());
    assertEquals("2_6ins(2_4)", firstRepeatSeqA2.toHGVSString());
    assertEquals("2del(2)", firstRepeatSeqB1.toHGVSString());
    assertEquals("2_6del(2_4)", firstRepeatSeqB2.toHGVSString());
  }
}
