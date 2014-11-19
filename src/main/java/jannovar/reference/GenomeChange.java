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
		// Normalize position type to zero-based.
		pos = pos.withPositionType(PositionType.ZERO_BASED);

		// Correct variant data.
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
		// TODO(holtgrem): what's the reason for placing "-" in there anyway?
		if (corr.ref.equals("-"))
			corr.ref = "";
		if (corr.alt.equals("-"))
			corr.alt = "";

		if (strand == pos.getStrand()) {
			this.ref = corr.ref;
			this.alt = corr.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(corr.ref);
			this.alt = DNAUtils.reverseComplement(corr.alt);
		}

		int delta = 0;
		if (strand != pos.getStrand() && ref.length() == 0)
			delta = -1;
		else if (strand != pos.getStrand() /* && ref.length() != 0 */)
			delta = ref.length() - 1;

		this.pos = new GenomePosition(pos.getStrand(), pos.getChr(), corr.position, PositionType.ZERO_BASED).shifted(
				delta).withStrand(strand);
	}

	/**
	 * Construct object and enforce strand.
	 */
	public GenomeChange(GenomeChange other, char strand) {
		if (strand == other.pos.getStrand()) {
			this.ref = other.ref;
			this.alt = other.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(other.ref);
			this.alt = DNAUtils.reverseComplement(other.alt);
		}

		// Get position as 0-based position.

		if (strand == other.pos.getStrand()) {
			this.pos = other.pos;
		} else {
			GenomePosition pos = other.pos.withPositionType(PositionType.ZERO_BASED);
			this.pos = pos.shifted(this.ref.length() - 1).withStrand(strand)
					.withPositionType(other.pos.getPositionType());
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
		return new GenomeInterval(pos, this.ref.length()).withPositionType(this.pos.getPositionType());
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
		return new GenomeChange(this, strand);
	}

	/**
	 * @return human-readable {@link String} describing the genome change
	 */
	@Override
	public String toString() {
		return pos.toString() + ":" + ref + ">" + alt;
	}

	/**
	 * @return the {@link GenomeChangeType} of this GenomeChange
	 */
	public GenomeChangeType getType() {
		if (getRef().length() > 0 && getAlt().length() == 0)
			return GenomeChangeType.DELETION;
		else if (getRef().length() == 0 && getAlt().length() > 0)
			return GenomeChangeType.INSERTION;
		else if (getRef().length() == 1 && getAlt().length() == 1)
			return GenomeChangeType.SNV;
		else
			return GenomeChangeType.BLOCK_SUBSTITUTION;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (strand != '+')
			return withStrand('+').hashCode();
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
		if (pos.getStrand() != '+')
			return withStrand('+').equals(obj);
		other = other.withStrand('+');

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
