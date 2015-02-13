package de.charite.compbio.jannovar.reference;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.util.DNAUtils;

/**
 * Denote a change with a "REF" and an "ALT" string using genome coordinates.
 *
 * GenomeChange objects are immutable, the members are automatically adjusted for the longest common prefix in REF and
 * ALT.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Immutable
public final class GenomeChange {

	/** position of the change */
	public final GenomePosition pos;
	/** nucleic acid reference string */
	public final String ref;
	/** nucleic acid alternative string */
	public final String alt;

	/**
	 * Construct object given the position, reference, and alternative nucleic acid string.
	 *
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt.
	 */
	public GenomeChange(GenomePosition pos, String ref, String alt) {
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.pos);
		// TODO(holtgrem): what's the reason for placing "-" in there anyway?
		if (corr.ref.equals("-"))
			corr.ref = "";
		if (corr.alt.equals("-"))
			corr.alt = "";

		this.pos = new GenomePosition(pos.refDict, pos.strand, pos.chr, corr.position, PositionType.ZERO_BASED);
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
		// Correct variant data.
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.pos);
		// TODO(holtgrem): what's the reason for placing "-" in there anyway?
		if (corr.ref.equals("-"))
			corr.ref = "";
		if (corr.alt.equals("-"))
			corr.alt = "";

		if (strand == pos.strand) {
			this.ref = corr.ref;
			this.alt = corr.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(corr.ref);
			this.alt = DNAUtils.reverseComplement(corr.alt);
		}

		int delta = 0;
		if (strand != pos.strand && ref.length() == 0)
			delta = -1;
		else if (strand != pos.strand /* && ref.length() != 0 */)
			delta = ref.length() - 1;

		this.pos = new GenomePosition(pos.refDict, pos.strand, pos.chr, corr.position, PositionType.ZERO_BASED)
		.shifted(delta).withStrand(strand);
	}

	/**
	 * Construct object and enforce strand.
	 */
	public GenomeChange(GenomeChange other, char strand) {
		if (strand == other.pos.strand) {
			this.ref = other.ref;
			this.alt = other.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(other.ref);
			this.alt = DNAUtils.reverseComplement(other.alt);
		}

		// Get position as 0-based position.

		if (strand == other.pos.strand) {
			this.pos = other.pos;
		} else {
			this.pos = other.pos.shifted(this.ref.length() - 1).withStrand(strand);
		}
	}

	/**
	 * @return numeric ID of chromosome this change is on
	 */
	public int getChr() {
		return pos.chr;
	}

	/**
	 * @return interval of the genome change
	 */
	public GenomeInterval getGenomeInterval() {
		return new GenomeInterval(pos, ref.length());
	}

	/**
	 * @return the GenomeChange on the given strand
	 */
	public GenomeChange withStrand(char strand) {
		return new GenomeChange(this, strand);
	}

	/**
	 * @return the GenomeChange with the given position type
	 */
	public GenomeChange withPositionType(PositionType positionType) {
		return new GenomeChange(pos, ref, alt);
	}

	/**
	 * @return human-readable {@link String} describing the genome change
	 */
	@Override
	public String toString() {
		if (pos.strand != '+')
			return withStrand('+').toString();
		else
			return Joiner.on("").join(pos, ":", (ref.equals("") ? "-" : ref), ">", (alt.equals("") ? "-" : alt));
	}

	/**
	 * @return the {@link GenomeChangeType} of this GenomeChange
	 */
	public GenomeChangeType getType() {
		if (ref.length() > 0 && alt.length() == 0)
			return GenomeChangeType.DELETION;
		else if (ref.length() == 0 && alt.length() > 0)
			return GenomeChangeType.INSERTION;
		else if (ref.length() == 1 && alt.length() == 1)
			return GenomeChangeType.SNV;
		else
			return GenomeChangeType.BLOCK_SUBSTITUTION;
	}

	/**
	 * A transition is purine <-> purine or pyrimidine <-> pyrimidine. Only applies to single nucleotide subsitutions.
	 *
	 * @return true if the variant is a SNV and a transition.
	 */
	public boolean isTransition() {
		if (getType() != GenomeChangeType.SNV)
			return false;
		// purine to purine change
		if (this.ref.equals("A") && this.alt.equals("G"))
			return true;
		else if (this.ref.equals("G") && this.alt.equals("A"))
			return true;
		// pyrimidine to pyrimidine change
		if (this.ref.equals("C") && this.alt.equals("T"))
			return true;
		else if (this.ref.equals("T") && this.alt.equals("C"))
			return true;
		// If we get here, the variant must be a transversion.
		return false;
	}

	/**
	 * A transversion is purine <-> pyrimidine. Only applies to single nucleotide subsitutions.
	 *
	 * @return true if the variant is a SNV and a transversion.
	 */
	public boolean isTransversion() {
		if (getType() != GenomeChangeType.SNV)
			return false;
		// purine to purine change
		if (this.ref.equals("A") && this.alt.equals("G"))
			return false;
		else if (this.ref.equals("G") && this.alt.equals("A"))
			return false;
		// pyrimidine to pyrimidine change
		if (this.ref.equals("C") && this.alt.equals("T"))
			return false;
		else if (this.ref.equals("T") && this.alt.equals("C"))
			return false;
		// If we get here, the variant must be a SNV and a transversion.
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (pos.strand != '+')
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
		if (pos.strand != '+')
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
