package de.charite.compbio.jannovar.vardbs.exac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExacRecordBuilder {

	/** Name of the chromosome */
	private String contig;
	/** Position of the variant, 0-based */
	private int pos;
	/** ID of the variant */
	private String id;
	/** Reference sequence */
	private String ref;
	/** Alternative alleles in cluster */
	private ArrayList<String> alt;
	/** Filters, NC: inconsistent genotype submission for at least one sample */
	private ArrayList<String> filter;

	/** Allele counts for each population */
	private HashMap<ExacPopulation, List<Integer>> alleleCounts;
	/** Allele het counts for each population */
	private HashMap<ExacPopulation, List<Integer>> alleleHetCounts;
	/** Allele hom counts for each population */
	private HashMap<ExacPopulation, List<Integer>> alleleHomCounts;
	/** Allele hemicounts for each population */
	private HashMap<ExacPopulation, List<Integer>> alleleHemiCounts;
	/** Chromsome counts for each population */
	private HashMap<ExacPopulation, Integer> chromCounts;

	ExacRecordBuilder() {
		contig = null;
		pos = -1;
		id = null;
		ref = null;
		alt = new ArrayList<>();
		filter = new ArrayList<>();

		alleleCounts = new HashMap<>();
		alleleHetCounts = new HashMap<>();
		alleleHomCounts = new HashMap<>();
		alleleHemiCounts = new HashMap<>();
		chromCounts = new HashMap<>();
	}

	public ExacRecord build() {
		return new ExacRecord(contig, pos, id, ref, alt, filter, alleleCounts, alleleHetCounts, alleleHomCounts,
				alleleHemiCounts, chromCounts);
	}

	public String getContig() {
		return contig;
	}

	public void setContig(String contig) {
		this.contig = contig;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public ArrayList<String> getAlt() {
		return alt;
	}

	public void setAlt(ArrayList<String> alt) {
		this.alt = alt;
	}

	public ArrayList<String> getFilter() {
		return filter;
	}

	public void setFilter(ArrayList<String> filter) {
		this.filter = filter;
	}

	public HashMap<ExacPopulation, List<Integer>> getAlleleCounts() {
		return alleleCounts;
	}
	
	public HashMap<ExacPopulation, List<Integer>> getAlleleHetCounts() {
		return alleleHetCounts;
	}
	
	public HashMap<ExacPopulation, List<Integer>> getAlleleHomCounts() {
		return alleleHomCounts;
	}
	
	public HashMap<ExacPopulation, List<Integer>> getAlleleHemiCounts() {
		return alleleHemiCounts;
	}

	public void setAlleleCounts(HashMap<ExacPopulation, List<Integer>> alleleCounts) {
		this.alleleCounts = alleleCounts;
	}
	
	public void setAlleleHetCounts(HashMap<ExacPopulation, List<Integer>> alleleHetCounts) {
		this.alleleHetCounts = alleleHetCounts;
	}
	
	public void setAlleleHomCounts(HashMap<ExacPopulation, List<Integer>> alleleHomCounts) {
		this.alleleHomCounts = alleleHomCounts;
	}
	
	public void setAlleleHemiCounts(HashMap<ExacPopulation, List<Integer>> alleleHemiCounts) {
		this.alleleHemiCounts = alleleHemiCounts;
	}

	public HashMap<ExacPopulation, Integer> getChromCounts() {
		return chromCounts;
	}

	public void setChromCounts(HashMap<ExacPopulation, Integer> chromCounts) {
		this.chromCounts = chromCounts;
	}

	@Override
	public String toString() {
		return "ExacRecordBuilder [chrom=" + contig + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", filter=" + filter + ", alleleCounts=" + alleleCounts + ", alleleHetCounts=" + alleleHetCounts
				+ ", alleleHomCounts=" + alleleHomCounts + ", alleleHemiCounts=" + alleleHemiCounts + ", chromCounts="
				+ chromCounts + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleCounts == null) ? 0 : alleleCounts.hashCode());
		result = prime * result + ((alleleHetCounts == null) ? 0 : alleleHetCounts.hashCode());
		result = prime * result + ((alleleHomCounts == null) ? 0 : alleleHomCounts.hashCode());
		result = prime * result + ((alleleHemiCounts == null) ? 0 : alleleHemiCounts.hashCode());
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((contig == null) ? 0 : contig.hashCode());
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
		ExacRecordBuilder other = (ExacRecordBuilder) obj;
		if (alleleCounts == null) {
			if (other.alleleCounts != null)
				return false;
		} else if (alleleHetCounts == null) {
			if (other.alleleHetCounts != null)
				return false;
		} else if (alleleHomCounts == null) {
			if (other.alleleHomCounts != null)
				return false;
		} else if (alleleHemiCounts == null) {
			if (other.alleleHemiCounts != null)
				return false;
		} else if (!alleleCounts.equals(other.alleleCounts))
			return false;
		else if (!alleleHetCounts.equals(other.alleleHetCounts))
			return false;
		else if (!alleleHomCounts.equals(other.alleleHomCounts))
			return false;
		else if (!alleleHemiCounts.equals(other.alleleHemiCounts))
			return false;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (contig == null) {
			if (other.contig != null)
				return false;
		} else if (!contig.equals(other.contig))
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
