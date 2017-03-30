package de.charite.compbio.jannovar.vardbs.gnomad;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

// TODO: somehow share code bewteen exac and gnomad?

/**
 * Represents on entry in the gnomAD VCF database file
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GnomadRecord {

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
	/**
	 * Filters, RF: failed random forest, AC0: Allele count is zero, InbreedingCoeff: InbreedingCoeff < threshold, LCR:
	 * in a low-complexity region, SEGDUP: in a segmental duplication region
	 */
	final private ImmutableList<String> filter;
	/** POPMAX for each alternative allele */
	final private ImmutableList<String> popmax;

	// Entries of the INFO column

	/** Observed alternative allele counts for each population */
	final private ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> alleleCounts;
	/** Observed alternative allele het counts for each population */
	final private ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> alleleHetCounts;
	/** Observed alternative allele hom counts for each population */
	final private ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> alleleHomCounts;
	/** Observed alternative allele hemi counts for each population */
	final private ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> alleleHemiCounts;
	/** Chromsome counts for each population, for all but POPMAX, one-element list, otherwise multiple */
	final private ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> chromCounts;
	/** Observed alternative allele frequencies for each population */
	final private ImmutableSortedMap<GnomadPopulation, ImmutableList<Double>> alleleFrequencies;

	public GnomadRecord(String chrom, int pos, String id, String ref, List<String> alt, Collection<String> filter,
			List<String> popmax, Map<GnomadPopulation, List<Integer>> alleleCounts,
			Map<GnomadPopulation, List<Integer>> alleleHetCounts, Map<GnomadPopulation, List<Integer>> alleleHomCounts,
			Map<GnomadPopulation, List<Integer>> alleleHemiCounts,
			Map<GnomadPopulation, ImmutableList<Integer>> chromCounts) {
		this.chrom = chrom;
		this.pos = pos;
		this.id = id;
		this.ref = ref;
		this.alt = ImmutableList.copyOf(alt);
		this.filter = ImmutableList.copyOf(filter);
		this.popmax = ImmutableList.copyOf(popmax);

		ImmutableSortedMap.Builder<GnomadPopulation, ImmutableList<Integer>> acBuilder = ImmutableSortedMap
				.naturalOrder();
		for (Entry<GnomadPopulation, List<Integer>> e : alleleCounts.entrySet())
			acBuilder.put(e.getKey(), ImmutableList.copyOf(e.getValue()));
		this.alleleCounts = acBuilder.build();
		ImmutableSortedMap.Builder<GnomadPopulation, ImmutableList<Integer>> acHetBuilder = ImmutableSortedMap
				.naturalOrder();
		for (Entry<GnomadPopulation, List<Integer>> e : alleleHetCounts.entrySet())
			acHetBuilder.put(e.getKey(), ImmutableList.copyOf(e.getValue()));
		this.alleleHetCounts = acHetBuilder.build();
		ImmutableSortedMap.Builder<GnomadPopulation, ImmutableList<Integer>> acHomBuilder = ImmutableSortedMap
				.naturalOrder();
		for (Entry<GnomadPopulation, List<Integer>> e : alleleHomCounts.entrySet())
			acHomBuilder.put(e.getKey(), ImmutableList.copyOf(e.getValue()));
		this.alleleHomCounts = acHomBuilder.build();
		ImmutableSortedMap.Builder<GnomadPopulation, ImmutableList<Integer>> acHemiBuilder = ImmutableSortedMap
				.naturalOrder();
		for (Entry<GnomadPopulation, List<Integer>> e : alleleHemiCounts.entrySet())
			acHemiBuilder.put(e.getKey(), ImmutableList.copyOf(e.getValue()));
		this.alleleHemiCounts = acHemiBuilder.build();

		this.chromCounts = ImmutableSortedMap.copyOf(chromCounts.entrySet());

		ImmutableSortedMap.Builder<GnomadPopulation, ImmutableList<Double>> afBuilder = ImmutableSortedMap
				.naturalOrder();
		for (Entry<GnomadPopulation, ImmutableList<Integer>> e : this.alleleCounts.entrySet()) {
			final GnomadPopulation pop = e.getKey();
			final ImmutableList<Integer> counts = chromCounts.get(pop);
			ImmutableList.Builder<Double> afs = new ImmutableList.Builder<Double>();
			int i = 0;
			for (int x : e.getValue()) {
				int count;
				if (pop == GnomadPopulation.POPMAX)
					count = counts.get(i);
				else
					count = counts.get(0);
				++i;

				if (count > 0)
					afs.add((1.0 * x) / count);
				else
					afs.add(0.0);
			}
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

	public ImmutableList<String> getPopmax() {
		return popmax;
	}

	public ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> getAlleleHetCounts() {
		return alleleHetCounts;
	}

	public ImmutableSortedMap<GnomadPopulation, ImmutableList<Integer>> getAlleleHomCounts() {
		return alleleHomCounts;
	}

	public ImmutableMap<GnomadPopulation, ImmutableList<Integer>> getAlleleCounts() {
		return alleleCounts;
	}

	public ImmutableMap<GnomadPopulation, ImmutableList<Integer>> getAlleleHemiCounts() {
		return alleleHemiCounts;
	}

	public ImmutableMap<GnomadPopulation, ImmutableList<Integer>> getChromCounts() {
		return chromCounts;
	}

	public ImmutableMap<GnomadPopulation, ImmutableList<Double>> getAlleleFrequencies() {
		return alleleFrequencies;
	}

	/** @return Allele count for the given population <code>pop</code>, for each allele, including reference one */
	public ImmutableList<Integer> getAlleleCounts(GnomadPopulation pop) {
		return alleleCounts.get(pop);
	}

	/** @return Allele het count for the given population <code>pop</code>, for each allele, including reference one */
	public ImmutableList<Integer> getAlleleHetCounts(GnomadPopulation pop) {
		return alleleHetCounts.get(pop);
	}

	/** @return Allele hom count for the given population <code>pop</code>, for each allele, including reference one */
	public ImmutableList<Integer> getAlleleHomCounts(GnomadPopulation pop) {
		return alleleHomCounts.get(pop);
	}

	/** @return Allele hemi count for the given population <code>pop</code>, for each allele, including reference one */
	public ImmutableList<Integer> getAlleleHemiCounts(GnomadPopulation pop) {
		return alleleHemiCounts.get(pop);
	}

	/**
	 * @return Chromsome count for the given population <code>pop</code>, for each allele, including the reference one
	 */
	public ImmutableList<Integer> getChromCount(GnomadPopulation pop) {
		return chromCounts.get(pop);
	}

	/** @return Alternative allele frequency for the given population, for each allele, including the reference one */
	public ImmutableList<Double> getAlleleFrequencies(GnomadPopulation pop) {
		return alleleFrequencies.get(pop);
	}

	/**
	 * @return {@link GnomadPopulation} with highest allele frequency for the given allele index (0 is first alternative
	 *         allele)
	 */
	public GnomadPopulation popWithHighestAlleleFreq(int alleleNo) {
		double bestFreq = -1;
		GnomadPopulation bestPop = GnomadPopulation.ALL;
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			if (alleleFrequencies.get(pop) != null && alleleNo < alleleFrequencies.get(pop).size()
					&& alleleFrequencies.get(pop).get(alleleNo) > bestFreq) {
				bestFreq = alleleFrequencies.get(pop).get(alleleNo);
				bestPop = pop;
			}
		}
		return bestPop;
	}

	/** @return Highest frequency of the given allele, 0 is first alternative allele */
	public double highestAlleleFreq(int alleleNo) {
		return getAlleleFrequencies(popWithHighestAlleleFreq(alleleNo)).get(alleleNo);
	}

	/** @return Highest frequency of any allele in any population */
	public double highestAlleleFreqOverall() {
		double maxAlleleFreq = 0;
		for (int alleleNo = 0; alleleNo < alt.size(); ++alleleNo)
			maxAlleleFreq = Math.max(maxAlleleFreq,
					getAlleleFrequencies(popWithHighestAlleleFreq(alleleNo)).get(alleleNo));
		return maxAlleleFreq;
	}

	@Override
	public String toString() {
		return "GnomadRecord [chrom=" + chrom + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", filter=" + filter + ", popmax=" + popmax + ", alleleCounts=" + alleleCounts + ", alleleHetCounts="
				+ alleleHetCounts + ", alleleHomCounts=" + alleleHomCounts + ", alleleHemiCounts=" + alleleHemiCounts
				+ ", chromCounts=" + chromCounts + ", alleleFrequencies=" + alleleFrequencies + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleCounts == null) ? 0 : alleleCounts.hashCode());
		result = prime * result + ((alleleFrequencies == null) ? 0 : alleleFrequencies.hashCode());
		result = prime * result + ((alleleHemiCounts == null) ? 0 : alleleHemiCounts.hashCode());
		result = prime * result + ((alleleHetCounts == null) ? 0 : alleleHetCounts.hashCode());
		result = prime * result + ((alleleHomCounts == null) ? 0 : alleleHomCounts.hashCode());
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
		result = prime * result + ((chromCounts == null) ? 0 : chromCounts.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((popmax == null) ? 0 : popmax.hashCode());
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
		GnomadRecord other = (GnomadRecord) obj;
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
		if (alleleHemiCounts == null) {
			if (other.alleleHemiCounts != null)
				return false;
		} else if (!alleleHemiCounts.equals(other.alleleHemiCounts))
			return false;
		if (alleleHetCounts == null) {
			if (other.alleleHetCounts != null)
				return false;
		} else if (!alleleHetCounts.equals(other.alleleHetCounts))
			return false;
		if (alleleHomCounts == null) {
			if (other.alleleHomCounts != null)
				return false;
		} else if (!alleleHomCounts.equals(other.alleleHomCounts))
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
		if (popmax == null) {
			if (other.popmax != null)
				return false;
		} else if (!popmax.equals(other.popmax))
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
