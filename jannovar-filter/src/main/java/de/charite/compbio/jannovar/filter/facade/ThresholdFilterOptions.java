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

	/** @return {@link ThresholdFilterOptions} with conservative default settings */
	public static ThresholdFilterOptions buildDefaultOptions() {
		return new ThresholdFilterOptions(8, 4, 10000, 20, 0.2, 0.8, 0.7, 0.3);
	}

	public ThresholdFilterOptions(int minGtCovHet, int minGtCovHomAlt, int maxCov, int minGtGq, double minGtAafHet,
			double maxGtAafHet, double minGtAafHomAlt, double maxGtAafHomRef) {
		super();
		this.minGtCovHet = minGtCovHet;
		this.minGtCovHomAlt = minGtCovHomAlt;
		this.maxCov = maxCov;
		this.minGtGq = minGtGq;
		this.minGtAafHet = minGtAafHet;
		this.maxGtAafHet = maxGtAafHet;
		this.minGtAafHomAlt = minGtAafHomAlt;
		this.maxGtAafHomRef = maxGtAafHomRef;
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

	@Override
	public String toString() {
		return "ThresholdFilterOptions [minGtCovHet=" + minGtCovHet + ", minGtCovHomAlt=" + minGtCovHomAlt + ", maxCov="
				+ maxCov + ", minGtGq=" + minGtGq + ", minGtAafHet=" + minGtAafHet + ", maxGtAafHet=" + maxGtAafHet
				+ ", minGtAafHomAlt=" + minGtAafHomAlt + ", maxGtAafHomRef=" + maxGtAafHomRef + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxCov;
		long temp;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThresholdFilterOptions other = (ThresholdFilterOptions) obj;
		if (maxCov != other.maxCov)
			return false;
		if (Double.doubleToLongBits(maxGtAafHet) != Double.doubleToLongBits(other.maxGtAafHet))
			return false;
		if (Double.doubleToLongBits(maxGtAafHomRef) != Double.doubleToLongBits(other.maxGtAafHomRef))
			return false;
		if (Double.doubleToLongBits(minGtAafHet) != Double.doubleToLongBits(other.minGtAafHet))
			return false;
		if (Double.doubleToLongBits(minGtAafHomAlt) != Double.doubleToLongBits(other.minGtAafHomAlt))
			return false;
		if (minGtCovHet != other.minGtCovHet)
			return false;
		if (minGtCovHomAlt != other.minGtCovHomAlt)
			return false;
		if (minGtGq != other.minGtGq)
			return false;
		return true;
	}

}
