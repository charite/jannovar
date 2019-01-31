package de.charite.compbio.jannovar.vardbs.clinvar;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/**
 * Enum for annotating origin from ClinVar
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ClinVarOrigin {
  /** unknown origin */
  UNKNOWN,
  /** germline variant */
  GERMLINE,
  /** somatic variant */
  SOMATIC,
  /** inherited */
  INHERITED,
  /** paternal */
  PATERNAL,
  /** maternal */
  MATERNAL,
  /** de novo */
  DE_NOVO,
  /** biparental */
  BIPARENTAL,
  /** uniparental */
  UNIPARENTAL,
  /** not-tested */
  NOT_TESTED,
  /** tested-inconclusive */
  TESTED_INCONCLUSIVE,
  /** other */
  OTHER;

  /** @return Label to be used for printing */
  public String getLabel() {
    switch (this) {
      case UNKNOWN:
        return "unknown";
      case GERMLINE:
        return "germline";
      case SOMATIC:
        return "somatic";
      case INHERITED:
        return "inherited";
      case PATERNAL:
        return "paternal";
      case MATERNAL:
        return "maternal";
      case DE_NOVO:
        return "de novo";
      case BIPARENTAL:
        return "biparental";
      case UNIPARENTAL:
        return "uniparental";
      case NOT_TESTED:
        return "not tested";
      case TESTED_INCONCLUSIVE:
        return "tested_inconclusive";
      default:
        return "other";
    }
  }

  public static List<ClinVarOrigin> fromInteger(int i) {
    if (i == 0) {
      return Lists.newArrayList(UNKNOWN);
    }
    List<ClinVarOrigin> result = new ArrayList<>();
    if ((i & 1) != 0) {
      result.add(GERMLINE);
    }
    if ((i & 2) != 0) {
      result.add(SOMATIC);
    }
    if ((i & 4) != 0) {
      result.add(INHERITED);
    }
    if ((i & 8) != 0) {
      result.add(PATERNAL);
    }
    if ((i & 16) != 0) {
      result.add(MATERNAL);
    }
    if ((i & 32) != 0) {
      result.add(DE_NOVO);
    }
    if ((i & 64) != 0) {
      result.add(BIPARENTAL);
    }
    if ((i & 128) != 0) {
      result.add(UNIPARENTAL);
    }
    if ((i & 256) != 0) {
      result.add(NOT_TESTED);
    }
    if ((i & 512) != 0) {
      result.add(TESTED_INCONCLUSIVE);
    }
    if ((i & 1073741824) != 0) {
      result.add(OTHER);
    }
    return result;
  }
}
