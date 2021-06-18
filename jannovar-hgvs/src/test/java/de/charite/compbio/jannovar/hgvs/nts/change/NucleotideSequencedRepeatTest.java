package de.charite.compbio.jannovar.hgvs.nts.change;

import com.google.common.collect.Lists;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * Unit test for {@link NucleotideSequencedRepeat}.
 *
 * @author Mark Woon
 */
class NucleotideSequencedRepeatTest {
  private NucleotideSequencedRepeat firstRepeatSeqA1;
  private NucleotideSequencedRepeat firstRepeatSeqA2;
  private NucleotideSequencedRepeat firstRepeatSeqB1;
  private NucleotideSequencedRepeat firstRepeatSeqB2;
  private NucleotideSequencedRepeat secondRepeatSeqA1;
  private NucleotideSequencedRepeat secondRepeatSeqA2;
  private NucleotideSequencedRepeat secondRepeatSeqB1;
  private NucleotideSequencedRepeat secondRepeatSeqB2;


  @BeforeEach
  public void setUp() {
    NucleotideRange range1 = new NucleotideRange(NucleotidePointLocation.build(1),
        NucleotidePointLocation.build(1));
    NucleotideRange range2 = new NucleotideRange(NucleotidePointLocation.build(1),
        NucleotidePointLocation.build(5));
    NucleotideRepeatSequence seqRepeat1 = new NucleotideRepeatSequence("AA", 4);
    NucleotideRepeatSequence seqRepeat2 = new NucleotideRepeatSequence("CC", 8);

    firstRepeatSeqA1 = new NucleotideSequencedRepeat(false, range1, Lists.newArrayList(seqRepeat1));
    firstRepeatSeqA2 = new NucleotideSequencedRepeat(false, range1, Lists.newArrayList(seqRepeat1, seqRepeat2));
    firstRepeatSeqB1 = new NucleotideSequencedRepeat(false, range2, Lists.newArrayList(seqRepeat1));
    firstRepeatSeqB2 = new NucleotideSequencedRepeat(false, range2, Lists.newArrayList(seqRepeat1, seqRepeat2));

    secondRepeatSeqA1 = new NucleotideSequencedRepeat(false, range1, Lists.newArrayList(seqRepeat1));
    secondRepeatSeqA2 = new NucleotideSequencedRepeat(false, range1, Lists.newArrayList(seqRepeat1, seqRepeat2));
    secondRepeatSeqB1 = new NucleotideSequencedRepeat(false, range2, Lists.newArrayList(seqRepeat1));
    secondRepeatSeqB2 = new NucleotideSequencedRepeat(false, range2, Lists.newArrayList(seqRepeat1, seqRepeat2));
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
    assertEquals("2AA[4]", firstRepeatSeqA1.toHGVSString());
    assertEquals("2AA[4]CC[8]", firstRepeatSeqA2.toHGVSString());
    assertEquals("2_6AA[4]", firstRepeatSeqB1.toHGVSString());
    assertEquals("2_6AA[4]CC[8]", firstRepeatSeqB2.toHGVSString());
  }
}
