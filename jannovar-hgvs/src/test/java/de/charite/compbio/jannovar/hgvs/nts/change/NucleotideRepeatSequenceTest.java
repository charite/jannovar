package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit test for {@link NucleotideRepeatSequence}.
 *
 * @author Mark Woon
 */
class NucleotideRepeatSequenceTest {
  private NucleotideRepeatSequence firstSeq;
  private NucleotideRepeatSequence secondSeq;
  private NucleotideRepeatSequence thirdSeq;
  private NucleotideRepeatSequence fourthSeq;

  @BeforeEach
  public void setUp() {
    firstSeq = new NucleotideRepeatSequence("TT", 2);
    secondSeq = new NucleotideRepeatSequence("TT", 2);
    thirdSeq = new NucleotideRepeatSequence("AT", 2);
    fourthSeq = new NucleotideRepeatSequence("TT", 4);
  }

  @Test
  public void testEquals() {
    assertEquals(firstSeq, secondSeq);
    assertNotEquals(firstSeq, thirdSeq);
    assertNotEquals(firstSeq, fourthSeq);
  }

  @Test
  public void testToHGVSString() {
    assertEquals("TT[2]", firstSeq.toHGVSString());
    assertEquals("AT[2]", thirdSeq.toHGVSString());
    assertEquals("TT[4]", fourthSeq.toHGVSString());
  }
}
