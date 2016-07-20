package de.charite.compbio.jannovar.vardbs.exac;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

// TODO: add more values, e.g. homozygous/heterozygous/hemizygous counts?

/**
 * Represents on entry in the ExAC VCF database file
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ExacRecord {

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

	/** Observed alternative allele counts for each population */
	final private ImmutableSortedMap<ExacPopulation, ImmutableList<Integer>> alleleCounts;
	/** Chromsome counts for each population */
	final private ImmutableSortedMap<ExacPopulation, Integer> chromCounts;
	/** Observed alternative allele frequencies for each population */
	final private ImmutableSortedMap<ExacPopulation, ImmutableList<Double>> alleleFrequencies;

	public ExacRecord(String chrom, int pos, String id, String ref, List<String> alt, Collection<String> filter,
			Map<ExacPopulation, List<Integer>> alleleCounts, Map<ExacPopulation, Integer> chromCounts) {
		this.chrom = chrom;
		this.pos = pos;
		this.id = id;
		this.ref = ref;
		this.alt = ImmutableList.copyOf(alt);
		this.filter = ImmutableList.copyOf(filter);

		ImmutableSortedMap.Builder<ExacPopulation, ImmutableList<Integer>> acBuilder = ImmutableSortedMap
				.naturalOrder();
		for (Entry<ExacPopulation, List<Integer>> e : alleleCounts.entrySet())
			acBuilder.put(e.getKey(), ImmutableList.copyOf(e.getValue()));
		this.alleleCounts = acBuilder.build();

		this.chromCounts = ImmutableSortedMap.copyOf(chromCounts.entrySet());

		ImmutableSortedMap.Builder<ExacPopulation, ImmutableList<Double>> afBuilder = ImmutableSortedMap.naturalOrder();
		for (Entry<ExacPopulation, ImmutableList<Integer>> e : this.alleleCounts.entrySet()) {
			final ExacPopulation pop = e.getKey();
			final int count = chromCounts.get(pop);
			ImmutableList.Builder<Double> afs = new ImmutableList.Builder<Double>();
			for (int x : e.getValue())
				afs.add((1.0 * x) / count);
			afBuilder.put(pop, afs.build());
		}
		this.alleleFrequencies = afBuilder.build();
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

	public ImmutableMap<ExacPopulation, ImmutableList<Integer>> getAlleleCounts() {
		return alleleCounts;
	}

	public ImmutableMap<ExacPopulation, Integer> getChromCounts() {
		return chromCounts;
	}

	public ImmutableMap<ExacPopulation, ImmutableList<Double>> getAlleleFrequencies() {
		return alleleFrequencies;
	}

	/** @return Allele count for the given population <code>pop</code>, for each allele, including reference one */
	public ImmutableList<Integer> getAlleleCounts(ExacPopulation pop) {
		return alleleCounts.get(pop);
	}

	/**
	 * @return Chromsome count for the given population <code>pop</code>, for each allele, including the reference one
	 */
	public int getChromCount(ExacPopulation pop) {
		return chromCounts.get(pop);
	}

	/** @return Alternative allele frequency for the given population, for each allele, including the reference one */
	public ImmutableList<Double> getAlleleFrequencies(ExacPopulation pop) {
		return alleleFrequencies.get(pop);
	}

	/**
	 * @return {@link ExacPopulation} with highest allele frequency for the given allele index (0 is first alternative
	 *         allele)
	 */
	public ExacPopulation popWithHighestAlleleFreq(int alleleNo) {
		double bestFreq = -1;
		ExacPopulation bestPop = ExacPopulation.ALL;
		for (ExacPopulation pop : ExacPopulation.values()) {
			if (alleleFrequencies.get(pop).get(alleleNo - 1) > bestFreq) {
				bestFreq = alleleFrequencies.get(pop).get(alleleNo - 1);
				bestPop = pop;
			}
		}
		return bestPop;
	}

	/** @return Highest frequency of the given allele */
	public double highestAlleleFreq(int alleleNo) {
		return getAlleleFrequencies(popWithHighestAlleleFreq(alleleNo)).get(alleleNo);
	}

	@Override
	public String toString() {
		return "ExacRecord [chrom=" + chrom + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", filter=" + filter + ", alleleCounts=" + alleleCounts + ", chromCounts=" + chromCounts
				+ ", alleleFrequencies=" + alleleFrequencies + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleCounts == null) ? 0 : alleleCounts.hashCode());
		result = prime * result + ((alleleFrequencies == null) ? 0 : alleleFrequencies.hashCode());
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
		result = prime * result + ((chromCounts == null) ? 0 : chromCounts.hashCode());
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
		ExacRecord other = (ExacRecord) obj;
		if (alleleCounts == null) {
			if (other.alleleCounts != null)
				return false;
		} else if (!alleleCounts.equals(other.alleleCounts))
			return false;
		if (alleleFrequencies == null) {
			if (other.alleleFrequencies != null)
				return false;
		} else if (!alleleFrequencies.equals(other.alleleFrequencies))
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
		if (chromCounts == null) {
			if (other.chromCounts != null)
				return false;
		} else if (!chromCounts.equals(other.chromCounts))
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
