package de.charite.compbio.jannovar.vardbs.base;

/**
 * Simple variant description, for use with variant normalization
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class VariantDescription {

	/** Name of the chromosome */
	private final String chrom;
	/** 0-based position of the first base in ref */
	private final int pos;
	/** String with reference sequence */
	private final String ref;
	/** String with alternative sequence */
	private final String alt;

	public VariantDescription(String chrom, int pos, String ref, String alt) {
		super();
		this.chrom = chrom;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
	}

	public String getChrom() {
		return chrom;
	}

	public int getPos() {
		return pos;
	}
	
	/** @return 0-based end position of the variant in the reference */
	public int getEnd() {
		if (ref.length() == 0)
			return pos + 1;
		else
			return pos + ref.length();
	}

	public String getRef() {
		return ref;
	}

	public String getAlt() {
		return alt;
	}
	
	@Override
	public String toString() {
		return "VariantDescription [chrom=" + chrom + ", pos=" + pos + ", ref=" + ref + ", alt=" + alt + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
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
		VariantDescription other = (VariantDescription) obj;
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
		if (pos != other.pos)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}

	/**
	 * @return <code>true</code> on whether the two variant descriptions overlap.
	 */
	public boolean overlapsWith(VariantDescription other) {
		if (!chrom.equals(other.chrom))
			return false;
		return (other.getPos() < getEnd() && getPos() < getEnd());
	}

}
