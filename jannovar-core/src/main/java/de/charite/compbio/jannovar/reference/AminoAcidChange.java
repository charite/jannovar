package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.util.StringUtil;

/**
 * Representation of a change in amino acids.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class AminoAcidChange {

	/** 0-based position of the change */
	private final int pos;
	/** reference amino acid string */
	private final String ref;
	/** alternative amino acid string */
	private final String alt;

	/** Construct object with given values. */
	public AminoAcidChange(int pos, String ref, String alt) {
		super();
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
	}

	/** @return 0-based position of the change */
	public int getPos() {
		return pos;
	}

	/** @return reference amino acid string */
	public String getRef() {
		return ref;
	}

	/** @return alternative amino acid string */
	public String getAlt() {
		return alt;
	}

	/**
	 * @return 0-based position of last changed base in reference, computed from {@link #getPos} and the length of
	 *         {@link #getRef}.
	 */
	public int getLastPos() {
		return pos + ref.length() - 1;
	}

	@Override
	public String toString() {
		return StringUtil.concatenate(pos + 1, ":", ref, ">", alt);
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

	/**
	 * @return <code>true</code> if the {@link AminoAcidChange} is a non-op
	 */
	public boolean isNop() {
		return (ref.equals("") && alt.equals(""));
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
