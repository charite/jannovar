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
		chromCounts = new HashMap<>();
	}

	public ExacRecord build() {
		return new ExacRecord(contig, pos, id, ref, alt, filter, alleleCounts, chromCounts);
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

	public void setAlleleCounts(HashMap<ExacPopulation, List<Integer>> alleleCounts) {
		this.alleleCounts = alleleCounts;
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
				+ ", filter=" + filter + ", alleleCounts=" + alleleCounts + ", chromCounts=" + chromCounts + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleCounts == null) ? 0 : alleleCounts.hashCode());
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
		} else if (!alleleCounts.equals(other.alleleCounts))
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
