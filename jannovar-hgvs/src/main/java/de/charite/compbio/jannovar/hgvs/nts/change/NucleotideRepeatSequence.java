package de.charite.compbio.jannovar.hgvs.nts.change;

import java.util.Objects;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;


/**
 * A repeated sequence.
 *
 * @author Mark Woon
 */
public class NucleotideRepeatSequence implements ConvertibleToHGVSString {
  final String sequence;
  final int copyNumber;


  public NucleotideRepeatSequence(String sequence, int copyNumber) {
    this.sequence = sequence;
    this.copyNumber = copyNumber;
  }


  public String getSequence() {
    return sequence;
  }

  public int getCopyNumber() {
    return copyNumber;
  }


  @Override
  public String toHGVSString() {
    return sequence + "[" + copyNumber + "]";
  }

  @Override
  public String toHGVSString(AminoAcidCode code) {
    return toHGVSString();
  }

  @Override
  public String toString() {
    return "NucleotideRepeatSequence [sequence=" + sequence + ", copyNumber=" + copyNumber + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(sequence, copyNumber);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final NucleotideRepeatSequence other = (NucleotideRepeatSequence)obj;
    return Objects.equals(sequence, other.sequence) &&
        Objects.equals(copyNumber, other.copyNumber);
  }
}
