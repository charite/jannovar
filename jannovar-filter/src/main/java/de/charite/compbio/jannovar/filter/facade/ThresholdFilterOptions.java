package de.charite.compbio.jannovar.filter.facade;

/**
 * Configuration for threshold-based filters
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ThresholdFilterOptions {

  /** Minimal coverage at a site for heterozygous calls */
  private final int minGtCovHet;

  /** Minimal coverage at a site for homozygous calls */
  private final int minGtCovHomAlt;

  /** Maximal coverage at a site for any call */
  private final int maxCov;

  /** Minimal genotype for calls */
  private final int minGtGq;

  /** Minimal alternative allele fraction for heterozygous calls */
  private final double minGtAafHet;

  /** Maximal alternative allele fraction for heterozygous calls */
  private final double maxGtAafHet;

  /** Minimal alternative allele fraction for homozygous alternative calls */
  private final double minGtAafHomAlt;

  /** Maximal alternative allele fraction for homozygous ref calls */
  private final double maxGtAafHomRef;

  /** Prefix of EXAC annotation */
  private final String exacPrefix;

  /** Prefix of dbSNP annotation */
  private final String dbSnpPrefix;

  /** Prefix of thousand genomes annotation */
  private final String g1kPrefix;

  /** Prefix of gnomAD exomes annotation */
  private final String gnomAdExomesPrefix;

  /** Prefix of gnomAD genomes annotation */
  private final String gnomAdGenomesPrefix;

  /** Maximal alternative allele's frequency for autosomal dominant inheritance mode */
  private final double maxAlleleFrequencyAd;

  /** Maximal alternative allele's frequency for autosomal recessive inheritance mode */
  private final double maxAlleleFrequencyAr;

  /** Maximal number of occurences in homozygous state in ExAC. */
  private final int maxExacHomState;

  /** Maximal number of occurences in homozygous state in thousand genomes. */
  private final int maxG1kHomState;

  /** @return {@link ThresholdFilterOptions} with conservative default settings */
  public static ThresholdFilterOptions buildDefaultOptions() {
    return new ThresholdFilterOptions(
        8,
        4,
        10000,
        20,
        0.2,
        0.8,
        0.7,
        0.3,
        "EXAC_",
        "DBSNP_",
        "GNOMAD_GENOMES_",
        "GNOMAD_EXOMES_",
        "G1K_",
        0.01,
        0.01,
        20,
        10);
  }

  public ThresholdFilterOptions(
      int minGtCovHet,
      int minGtCovHomAlt,
      int maxCov,
      int minGtGq,
      double minGtAafHet,
      double maxGtAafHet,
      double minGtAafHomAlt,
      double maxGtAafHomRef,
      String exacPrefix,
      String dbSnpPrefix,
      String gnomAdGenomesPrefix,
      String gnomAdExomesPrefix,
      String g1kPrefix,
      double maxAlleleFrequencyAd,
      double maxAlleleFrequencyAr,
      int maxExacHomState,
      int maxG1kHomState) {
    super();
    this.minGtCovHet = minGtCovHet;
    this.minGtCovHomAlt = minGtCovHomAlt;
    this.maxCov = maxCov;
    this.minGtGq = minGtGq;
    this.minGtAafHet = minGtAafHet;
    this.maxGtAafHet = maxGtAafHet;
    this.minGtAafHomAlt = minGtAafHomAlt;
    this.maxGtAafHomRef = maxGtAafHomRef;
    this.exacPrefix = exacPrefix;
    this.dbSnpPrefix = dbSnpPrefix;
    this.gnomAdExomesPrefix = gnomAdExomesPrefix;
    this.gnomAdGenomesPrefix = gnomAdGenomesPrefix;
    this.g1kPrefix = g1kPrefix;
    this.maxAlleleFrequencyAd = maxAlleleFrequencyAd;
    this.maxAlleleFrequencyAr = maxAlleleFrequencyAr;
    this.maxExacHomState = maxExacHomState;
    this.maxG1kHomState = maxG1kHomState;
  }

  public int getMinGtCovHet() {
    return minGtCovHet;
  }

  public int getMinGtCovHomAlt() {
    return minGtCovHomAlt;
  }

  public int getMaxCov() {
    return maxCov;
  }

  public int getMinGtGq() {
    return minGtGq;
  }

  public double getMinGtAafHet() {
    return minGtAafHet;
  }

  public double getMaxGtAafHet() {
    return maxGtAafHet;
  }

  public double getMinGtAafHomAlt() {
    return minGtAafHomAlt;
  }

  public double getMaxGtAafHomRef() {
    return maxGtAafHomRef;
  }

  public String getExacPrefix() {
    return exacPrefix;
  }

  public String getDbSnpPrefix() {
    return dbSnpPrefix;
  }

  public String getG1kPrefix() {
    return g1kPrefix;
  }

  public double getMaxAlleleFrequencyAd() {
    return maxAlleleFrequencyAd;
  }

  public double getMaxAlleleFrequencyAr() {
    return maxAlleleFrequencyAr;
  }

  public String getGnomAdExomesPrefix() {
    return gnomAdExomesPrefix;
  }

  public String getGnomAdGenomesPrefix() {
    return gnomAdGenomesPrefix;
  }

  public int getMaxExacHomState() {
    return maxExacHomState;
  }

  public int getMaxG1kHomState() {
    return maxG1kHomState;
  }

  @Override
  public String toString() {
    return "ThresholdFilterOptions [minGtCovHet="
        + minGtCovHet
        + ", minGtCovHomAlt="
        + minGtCovHomAlt
        + ", maxCov="
        + maxCov
        + ", minGtGq="
        + minGtGq
        + ", minGtAafHet="
        + minGtAafHet
        + ", maxGtAafHet="
        + maxGtAafHet
        + ", minGtAafHomAlt="
        + minGtAafHomAlt
        + ", maxGtAafHomRef="
        + maxGtAafHomRef
        + ", exacPrefix="
        + exacPrefix
        + ", dbSnpPrefix="
        + dbSnpPrefix
        + ", gnomAdExomesPrefix="
        + gnomAdExomesPrefix
        + ", gnomAdGenomesPrefix="
        + gnomAdGenomesPrefix
        + ", maxAlleleFrequencyAd="
        + maxAlleleFrequencyAd
        + ", maxAlleleFrequencyAr="
        + maxAlleleFrequencyAr
        + ", maxExacHomState="
        + maxExacHomState
        + ", maxG1kHomState="
        + maxG1kHomState
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dbSnpPrefix == null) ? 0 : dbSnpPrefix.hashCode());
    result = prime * result + ((exacPrefix == null) ? 0 : exacPrefix.hashCode());
    result = prime * result + ((gnomAdExomesPrefix == null) ? 0 : gnomAdExomesPrefix.hashCode());
    result = prime * result + ((gnomAdGenomesPrefix == null) ? 0 : gnomAdGenomesPrefix.hashCode());
    long temp;
    temp = Double.doubleToLongBits(maxAlleleFrequencyAd);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(maxAlleleFrequencyAr);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + maxCov;
    result = prime * result + maxExacHomState;
    result = prime * result + maxG1kHomState;
    temp = Double.doubleToLongBits(maxGtAafHet);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(maxGtAafHomRef);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(minGtAafHet);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(minGtAafHomAlt);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + minGtCovHet;
    result = prime * result + minGtCovHomAlt;
    result = prime * result + minGtGq;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ThresholdFilterOptions other = (ThresholdFilterOptions) obj;
    if (dbSnpPrefix == null) {
      if (other.dbSnpPrefix != null) {
        return false;
      }
    } else if (!dbSnpPrefix.equals(other.dbSnpPrefix)) {
      return false;
    }
    if (exacPrefix == null) {
      if (other.exacPrefix != null) {
        return false;
      }
    } else if (!exacPrefix.equals(other.exacPrefix)) {
      return false;
    }
    if (gnomAdExomesPrefix == null) {
      if (other.gnomAdExomesPrefix != null) {
        return false;
      }
    } else if (!gnomAdExomesPrefix.equals(other.gnomAdExomesPrefix)) {
      return false;
    }
    if (gnomAdGenomesPrefix == null) {
      if (other.gnomAdGenomesPrefix != null) {
        return false;
      }
    } else if (!gnomAdGenomesPrefix.equals(other.gnomAdGenomesPrefix)) {
      return false;
    }
    if (Double.doubleToLongBits(maxAlleleFrequencyAd)
        != Double.doubleToLongBits(other.maxAlleleFrequencyAd)) {
      return false;
    }
    if (Double.doubleToLongBits(maxAlleleFrequencyAr)
        != Double.doubleToLongBits(other.maxAlleleFrequencyAr)) {
      return false;
    }
    if (maxCov != other.maxCov) {
      return false;
    }
    if (maxExacHomState != other.maxExacHomState) {
      return false;
    }
    if (maxG1kHomState != other.maxG1kHomState) {
      return false;
    }
    if (Double.doubleToLongBits(maxGtAafHet) != Double.doubleToLongBits(other.maxGtAafHet)) {
      return false;
    }
    if (Double.doubleToLongBits(maxGtAafHomRef) != Double.doubleToLongBits(other.maxGtAafHomRef)) {
      return false;
    }
    if (Double.doubleToLongBits(minGtAafHet) != Double.doubleToLongBits(other.minGtAafHet)) {
      return false;
    }
    if (Double.doubleToLongBits(minGtAafHomAlt) != Double.doubleToLongBits(other.minGtAafHomAlt)) {
      return false;
    }
    if (minGtCovHet != other.minGtCovHet) {
      return false;
    }
    if (minGtCovHomAlt != other.minGtCovHomAlt) {
      return false;
    }
    if (minGtGq != other.minGtGq) {
      return false;
    }
    return true;
  }
}
