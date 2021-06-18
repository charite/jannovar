package de.charite.compbio.jannovar.hgvs.nts.change;

import java.util.Objects;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;


/**
 * A repeat that is not completely sequenced (i.e. unknown bases).
 *
 * @author Mark Woon
 */
public class NucleotideNotSequencedRepeat extends NucleotideChange {
  public enum InDelType { INS, DEL}
  private final NucleotideRange range;
  private final InDelType type;
  /**
   * The lower bound on the length of the repeat, inclusive.
   */
  private final int minCount;
  /**
   * The upper bound on the length of the repeat, inclusive.
   */
  private final int maxCount;


  public NucleotideNotSequencedRepeat(boolean onlyPredicted, NucleotideRange range, InDelType type,
      int minCount, int maxCount) {
    super(onlyPredicted);
    this.range = range;
    this.type = type;
    this.minCount = minCount;
    this.maxCount = maxCount;
  }


  /**
   * @return range of repeat
   */
  public NucleotideRange getRange() {
    return range;
  }

  public boolean isInsertion() {
    return type == InDelType.INS;
  }

  public boolean isDeletion() {
    return type == InDelType.DEL;
  }

  /**
   * Gets the lower bound on the length of the repeat, inclusive.
   */
  public int getMinCount() {
    return minCount;
  }

  /**
   * Gets the upper bound on the length of the repeat, inclusive.
   */
  public int getMaxCount() {
    return maxCount;
  }


  @Override
  public NucleotideChange withOnlyPredicted(boolean flag) {
    return new NucleotideNotSequencedRepeat(flag, range, type, minCount, maxCount);
  }


  @Override
  public String toHGVSString() {
    StringBuilder builder = new StringBuilder(range.toHGVSString())
        .append(type.name().toLowerCase())
        .append("(")
        .append(minCount);
    if (minCount != maxCount) {
      builder.append("_")
          .append(maxCount);
    }
    builder.append(")");
    return wrapIfOnlyPredicted(builder.toString());
  }

  @Override
  public String toHGVSString(AminoAcidCode code) {
    return toHGVSString();
  }

  @Override
  public String toString() {
    return "NucleotideNotSequencedRepeat [range=" + range + "type=" + type.name() + ", minCount=" +
        minCount + ", maxCount=" + maxCount + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(range, type, minCount, maxCount);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final NucleotideNotSequencedRepeat other = (NucleotideNotSequencedRepeat)obj;
    return Objects.equals(range, other.range) &&
        Objects.equals(type, other.type) &&
        Objects.equals(minCount, other.getMinCount()) &&
        Objects.equals(maxCount, other.getMaxCount());
  }
}
