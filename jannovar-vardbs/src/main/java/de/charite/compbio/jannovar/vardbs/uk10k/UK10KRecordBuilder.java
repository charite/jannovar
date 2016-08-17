package de.charite.compbio.jannovar.vardbs.uk10k;

import java.util.ArrayList;

public class UK10KRecordBuilder {

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

	/** Chromosome count, number of chromosomes with coverage in UK10K data */
	private int chromCount;
	/** Number of observed allele, including reference */
	private ArrayList<Integer> alleleCounts;
	/** Allele frequencies, including reference */
	private ArrayList<Double> alleleFrequencies;

	public UK10KRecordBuilder() {
		this.contig = null;
		this.pos = -1;
		this.id = null;
		this.ref = null;
		this.alt = new ArrayList<String>();
		this.filter = new ArrayList<String>();

		this.chromCount = -1;
		this.alleleCounts = new ArrayList<>();
		this.alleleFrequencies = new ArrayList<>();
	}

	public UK10KRecord build() {
		return new UK10KRecord(contig, pos, id, ref, alt, filter, chromCount, alleleCounts, alleleFrequencies);
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

	public ArrayList<Integer> getAlleleCounts() {
		return alleleCounts;
	}

	public void setAlleleCounts(ArrayList<Integer> alleleCounts) {
		this.alleleCounts = alleleCounts;
	}

	public int getChromCount() {
		return chromCount;
	}

	public void setChromCount(int chromCount) {
		this.chromCount = chromCount;
	}

	public ArrayList<Double> getAlleleFrequencies() {
		return alleleFrequencies;
	}

	public void setAlleleFrequencies(ArrayList<Double> alleleFrequencies) {
		this.alleleFrequencies = alleleFrequencies;
	}

	@Override
	public String toString() {
		return "UK10KRecordBuilder [contig=" + contig + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", filter=" + filter + ", alleleCounts=" + alleleCounts + ", chromCount=" + chromCount
				+ ", alleleFrequencies=" + alleleFrequencies + "]";
	}

}
