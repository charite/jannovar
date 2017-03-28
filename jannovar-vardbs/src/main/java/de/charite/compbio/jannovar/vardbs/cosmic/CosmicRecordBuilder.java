package de.charite.compbio.jannovar.vardbs.cosmic;

import java.util.ArrayList;

public class CosmicRecordBuilder {

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

	/** Number of samples showing the mutation */
	private int cnt;
	/** Classified as SNP in COSMIC */
	private boolean snp;

	public CosmicRecordBuilder() {
		this.contig = null;
		this.pos = -1;
		this.id = null;
		this.ref = null;
		this.alt = new ArrayList<String>();

		this.cnt = 0;
		this.snp = false;
	}

	public CosmicRecord build() {
		return new CosmicRecord(contig, pos, id, ref, alt, cnt, snp);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public boolean isSnp() {
		return snp;
	}

	public void setSnp(boolean snp) {
		this.snp = snp;
	}

	@Override
	public String toString() {
		return "CosmicRecordBuilder [contig=" + contig + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", cnt=" + cnt + ", snp=" + snp + "]";
	}

}
