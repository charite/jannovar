package de.charite.compbio.jannovar.hgvs.nts.change;

import java.util.List;
import java.util.Objects;
import com.google.common.base.Joiner;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;


/**
 * A repeat that has been sequenced.
 *
 * @author Mark Woon
 */
public class NucleotideSequencedRepeat extends NucleotideChange {
  private final NucleotideRange range;
  public List<NucleotideRepeatSequence> sequencedRepeats;
  public NucleotideNotSequencedRepeat notSequencedRepeat;


  public NucleotideSequencedRepeat(boolean onlyPredicted, NucleotideRange range,
      List<NucleotideRepeatSequence> sequencedRepeats) {
    super(onlyPredicted);
    this.range = range;
    this.sequencedRepeats = sequencedRepeats;
  }


  /**
   * @return range of repeat
   */
  public NucleotideRange getRange() {
    return range;
  }

  /**
   * Gets the sequenced repeats.  Null if this repeat is not sequenced.
   */
  public List<NucleotideRepeatSequence> getSequencedRepeats() {
    return sequencedRepeats;
  }

  /**
   * Gets the repeat if it was not sequenced.  Null if this repeat is sequenced.
   */
  public NucleotideNotSequencedRepeat getNotSequencedRepeat() {
    return notSequencedRepeat;
  }


  @Override
  public NucleotideChange withOnlyPredicted(boolean flag) {
    return new NucleotideSequencedRepeat(flag, range, sequencedRepeats);
  }


  @Override
  public String toHGVSString() {
    StringBuilder builder = new StringBuilder(range.toHGVSString());
    sequencedRepeats.stream()
        .map(NucleotideRepeatSequence::toHGVSString)
        .forEach(builder::append);
    return wrapIfOnlyPredicted(builder.toString());
  }

  @Override
  public String toString() {
    return "NucleotideRepeat [range=" + range + ", sequences=(" +
        Joiner.on(", ").join(sequencedRepeats) + ")]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), range, sequencedRepeats);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final NucleotideSequencedRepeat other = (NucleotideSequencedRepeat)obj;
    return super.equals(obj) &&
        Objects.equals(range, other.range) &&
        Objects.equals(sequencedRepeats, other.sequencedRepeats);
  }
}
