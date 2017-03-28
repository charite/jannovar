package de.charite.compbio.jannovar.vardbs.cosmic;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class CosmicRecord {

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

	// Entries of the INFO column

	/** Number of samples in COSMIC showing the mutation */
	private final int cnt;
	/** Whether or not the sample is flagged as SNP in COSMIC */
	private final boolean snp;

	public CosmicRecord(String chrom, int pos, String id, String ref, List<String> alt, int cnt, boolean snp) {
		this.chrom = chrom;
		this.pos = pos;
		this.id = id;
		this.ref = ref;
		this.alt = ImmutableList.copyOf(alt);
		this.cnt = cnt;
		this.snp = snp;
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

	public int getCnt() {
		return cnt;
	}

	public boolean isSnp() {
		return snp;
	}

	@Override
	public String toString() {
		return "CosmicRecord [chrom=" + chrom + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", cnt=" + cnt + ", snp=" + snp + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
		result = prime * result + cnt;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + pos;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + (snp ? 1231 : 1237);
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
		CosmicRecord other = (CosmicRecord) obj;
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
		if (cnt != other.cnt)
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
		if (snp != other.snp)
			return false;
		return true;
	}

}
