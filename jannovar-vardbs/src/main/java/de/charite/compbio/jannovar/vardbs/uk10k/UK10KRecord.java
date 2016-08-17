package de.charite.compbio.jannovar.vardbs.uk10k;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class UK10KRecord {

	// Fields up to the INFO column

	/** Name of the chromosome */
	final private String chrom;
	/** Position of the variant, 0-based */
	final private int pos;
	/** ID of the variant */
	final private String id;
	/** Reference sequence */
	final private String ref;
	/** Alternative alleles in cluster */
	final private ImmutableList<String> alt;
	/** Filters, NC: inconsistent genotype submission for at least one sample */
	final private ImmutableList<String> filter;

	// Entries of the INFO column

	/** Number of observed chromosomes for each alternative allele */
	final private ImmutableList<Integer> altAlleleCounts;
	/** Chromosome count, number of chromosomes with coverage in UK10K data */
	final private int chromCount;
	/** Allele frequencies for alternative alleles */
	final private ImmutableList<Double> altAlleleFrequencies;

	public UK10KRecord(String chrom, int pos, String id, String ref, List<String> alt, List<String> filter,
			int chromCount, List<Integer> alleleCounts, List<Double> alleleFrequencies) {
		this.chrom = chrom;
		this.pos = pos;
		this.id = id;
		this.ref = ref;
		this.alt = ImmutableList.copyOf(alt);
		this.filter = ImmutableList.copyOf(filter);
		this.chromCount = chromCount;
		this.altAlleleCounts = ImmutableList.copyOf(alleleCounts);
		this.altAlleleFrequencies = ImmutableList.copyOf(alleleFrequencies);
	}

	public String getChrom() {
		return chrom;
	}

	public int getPos() {
		return pos;
	}

	public String getId() {
		return id;
	}

	public String getRef() {
		return ref;
	}

	public ImmutableList<String> getAlt() {
		return alt;
	}

	public ImmutableList<String> getFilter() {
		return filter;
	}

	public ImmutableList<Integer> getAltAlleleCounts() {
		return altAlleleCounts;
	}

	public int getChromCount() {
		return chromCount;
	}

	public ImmutableList<Double> getAltAlleleFrequencies() {
		return altAlleleFrequencies;
	}

	@Override
	public String toString() {
		return "UK10KRecord [chrom=" + chrom + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", filter=" + filter + ", altAlleleCounts=" + altAlleleCounts + ", chromCount=" + chromCount
				+ ", altAlleleFrequencies=" + altAlleleFrequencies + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((altAlleleCounts == null) ? 0 : altAlleleCounts.hashCode());
		result = prime * result + ((altAlleleFrequencies == null) ? 0 : altAlleleFrequencies.hashCode());
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
		result = prime * result + chromCount;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + pos;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
		UK10KRecord other = (UK10KRecord) obj;
		if (altAlleleCounts == null) {
			if (other.altAlleleCounts != null)
				return false;
		} else if (!altAlleleCounts.equals(other.altAlleleCounts))
			return false;
		if (altAlleleFrequencies == null) {
			if (other.altAlleleFrequencies != null)
				return false;
		} else if (!altAlleleFrequencies.equals(other.altAlleleFrequencies))
			return false;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (chrom == null) {
			if (other.chrom != null)
				return false;
		} else if (!chrom.equals(other.chrom))
			return false;
		if (chromCount != other.chromCount)
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pos != other.pos)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}

}
