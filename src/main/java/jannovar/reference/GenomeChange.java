package jannovar.reference;

import jannovar.annotation.VariantDataCorrector;
import jannovar.util.DNAUtils;

// TODO(holtgrew): enforce immutability of pos
/**
 * Denote a change with a "REF" and an "ALT" string using genome coordinates.
 *
 * GenomeChange objects are immutable, the members are automatically adjusted for the longest common prefix in REF and
 * ALT.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class GenomeChange {
	/** position of the change */
	private final GenomePosition pos;
	/** nucleic acid reference string */
	private final String ref;
	/** nucleic acid alternative string */
	private final String alt;

	/**
	 * Construct object given the position, reference, and alternative nucleic acid string.
	 *
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt.
	 */
	public GenomeChange(GenomePosition pos, String ref, String alt) {
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
		// TODO(holtgrem): what's the reason for placing "-" in there anyway?
		if (corr.ref.equals("-"))
			corr.ref = "";
		if (corr.alt.equals("-"))
			corr.alt = "";

		this.pos = new GenomePosition(pos.getStrand(), pos.getChr(), corr.position, pos.getPositionType());
		this.ref = corr.ref;
		this.alt = corr.alt;
	}

	/**
	 * Construct object given the position, reference, alternative nucleic acid string, and strand.
	 *
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt. Further, the position is adjusted to the given strand.
	 */
	public GenomeChange(GenomePosition pos, String ref, String alt, char strand) {
		// TODO(holtgrem): test this function
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
		// TODO(holtgrem): what's the reason for placing "-" in there anyway?
		if (corr.ref.equals("-"))
			corr.ref = "";
		if (corr.alt.equals("-"))
			corr.alt = "";

		int shift = ref.length() + (pos.getPositionType() == PositionType.ONE_BASED ? -1 : 0);

		this.pos = new GenomePosition(pos.getStrand(), pos.getChr(), corr.position, pos.getPositionType()).shifted(
				shift).withStrand(strand);
		if (strand == '+') {
			this.ref = corr.ref;
			this.alt = corr.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(corr.ref);
			this.alt = DNAUtils.reverseComplement(corr.alt);
		}
	}

	/**
	 * Construct object and enforce strand.
	 */
	public GenomeChange(GenomeChange other, char strand) {
		// TODO(holtgrem): test this function
		VariantDataCorrector corr = new VariantDataCorrector(other.ref, other.alt, other.pos.getPos());
		// TODO(holtgrem): what's the reason for placing "-" in there anyway?
		if (corr.ref.equals("-"))
			corr.ref = "";
		if (corr.alt.equals("-"))
			corr.alt = "";

		this.pos = new GenomePosition(other.pos.getStrand(), other.pos.getChr(), corr.position,
				other.pos.getPositionType()).withStrand(strand);
		if (strand == '+') {
			this.ref = corr.ref;
			this.alt = corr.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(corr.ref);
			this.alt = DNAUtils.reverseComplement(corr.alt);
		}
	}

	/**
	 * @return the position of the genome change
	 */
	public GenomePosition getPos() {
		return pos;
	}

	/**
	 * @return interval of the genome change
	 */
	public GenomeInterval getGenomeInterval() {
		GenomePosition pos = this.pos.withPositionType(PositionType.ZERO_BASED);
		return new GenomeInterval(pos.getStrand(), pos.getChr(), pos.getPos(), pos.getPos() + this.ref.length(),
				PositionType.ZERO_BASED).withPositionType(this.pos.getPositionType());
	}

	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @return the alt
	 */
	public String getAlt() {
		return alt;
	}

	/**
	 * @return the GenomeChange on the given strand
	 */
	public GenomeChange withStrand(char strand) {
		// TODO(holtgrem): test this function
		return new GenomeChange(this, strand);
	}

	/**
	 * @return human-readable {@link String} describing the genome change
	 */
	@Override
	public String toString() {
		return pos.toString() + ":" + ref + ">" + alt;
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
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
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
		GenomeChange other = (GenomeChange) obj;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}

}
