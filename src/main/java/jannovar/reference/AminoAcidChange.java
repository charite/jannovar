package jannovar.reference;

/**
 * Representation of a change in amino acids.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AminoAcidChange {
	/** 0-based position of the change */
	public final int pos;
	/** reference amino acid string */
	public final String ref;
	/** alternative amino acid string */
	public final String alt;

	/** Construct object with given values. */
	public AminoAcidChange(int pos, String ref, String alt) {
		super();
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
	}

	/**
	 * @return 0-based position of last changed base in reference, computed from {@link #pos} and the length of
	 *         {@link #ref}.
	 */
	public int getLastPos() {
		return pos + ref.length() - 1;
	}

	@Override
	public String toString() {
		return String.format("%d:%s>%s", pos + 1, ref, alt);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + pos;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		return result;
	}

	/**
	 * @return new {@link AminoAcidChange} that is shifted one to the right
	 */
	public AminoAcidChange shiftRight() {
		if (alt.length() > 0)
			return new AminoAcidChange(pos + 1, ref.substring(1, ref.length()), alt.substring(1, alt.length()));
		else
			return new AminoAcidChange(pos + 1, ref.substring(1, ref.length()), "");
	}

	/**
	 * @return new {@link AminoAcidChange} that is shifted one to the left
	 */
	public AminoAcidChange shiftLeft() {
		if (alt.length() > 0)
			return new AminoAcidChange(pos, ref.substring(0, ref.length() - 1), alt.substring(1, alt.length()));
		else
			return new AminoAcidChange(pos, ref.substring(0, ref.length() - 1), "");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AminoAcidChange other = (AminoAcidChange) obj;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
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
