package de.charite.compbio.jannovar.reference;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.impl.util.DNAUtils;

// TODO(holtgrewe): We only want genome changes on the forward strand, make sure this does not lead to problems downstream.

/**
 * Denote a change with a "REF" and an "ALT" string using genome coordinates.
 * <p>
 * GenomeChange objects are immutable, the members are automatically adjusted for the longest common suffix and prefix
 * in REF and ALT.
 * <p>
 * Symbolic-ness of alleles can be checked with {@link #wouldBeSymbolicAllele(String)} but symbolic alleles are not
 * supported by {@code GenomeVariant}.  Rather, use {@link SVGenomeVariant} for this.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 */
@Immutable
public final class GenomeVariant implements VariantDescription {

	/**
	 * position of the change
	 */
	private final GenomePosition pos;
	/**
	 * nucleic acid reference string
	 */
	private final String ref;
	/**
	 * nucleic acid alternative string
	 */
	private final String alt;

	/**
	 * Construct object given the position, reference, and alternative nucleic acid string.
	 * <p>
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt.
	 */
	public GenomeVariant(GenomePosition pos, String ref, String alt) {
		if (wouldBeSymbolicAllele(ref) || wouldBeSymbolicAllele(alt)) {
			this.pos = pos;
			this.ref = ref;
			this.alt = alt;
			return;
		}

		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
		this.pos = new GenomePosition(pos.getContig(), pos.getStrand(), corr.position, PositionType.ZERO_BASED);
		this.ref = corr.ref;
		this.alt = corr.alt;
	}

	/**
	 * Construct object given the position, reference, alternative nucleic acid string, and strand.
	 * <p>
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt. Further, the position is adjusted to the given strand.
	 *
	 * @throws InvalidGenomeVariant if {@code ref} and {@code alt} describe symbolic or non-ACGT alleles.
	 */
	public GenomeVariant(GenomePosition pos, String ref, String alt, Strand strand) throws InvalidGenomeVariant {
		if (wouldBeSymbolicAllele(ref) || wouldBeSymbolicAllele(alt)) {
			throw new InvalidGenomeVariant("Symbolic alleles are not supported by GenomeVariant");
		}

		// Correct variant data.
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
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

		this.pos = new GenomePosition(pos.getContig(), pos.getStrand(), corr.position, PositionType.ZERO_BASED)
			.shifted(delta)
			.withStrand(strand);
	}

	/**
	 * Check whether the given allele string would be interpreted as a symbolic one.
	 *
	 * @return <code>true</code> if the given <code>allele</code> string describes a symbolic allele (events not
	 * described by replacement of bases, e.g. break-ends or duplications that are described in one line).
	 */
	public static boolean wouldBeSymbolicAllele(String allele) {
		if (allele.length() <= 1)
			return false;
		// The outer terms are: (i) symbolic or large insertion, (ii) single breakend, (iii) mated breakend
		return (allele.charAt(0) == '<' || allele.charAt(allele.length() - 1) == '>') ||
			(allele.charAt(0) == '.' || allele.charAt(allele.length() - 1) == '.') || // single breakend
			(allele.contains("[") || allele.contains("]"));
	}

	@Override
	public String getChrName() {
		return pos.getChrName();
	}

	public GenomePosition getGenomePos() {
		return this.pos;
	}

	@Override
	public int getPos() {
		return this.pos.getPos();
	}

	@Override
	public String getRef() {
		return this.ref;
	}

	@Override
	public String getAlt() {
		return this.alt;
	}

	/**
	 * @return numeric ID of chromosome this change is on
	 */
	@Override
	public int getChr() {
		return pos.getChr();
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
	public GenomeVariant withStrand(Strand strand) {
		if (pos.getStrand() == strand) {
			return this;
		}

		String oppositeRef = DNAUtils.reverseComplement(ref);
		String oppositeAlt = DNAUtils.reverseComplement(alt);

		// Get position as 0-based position.
		GenomePosition oppositePos = pos.shifted(this.ref.length() - 1).withStrand(strand);
		try {
			return new GenomeVariant(oppositePos, oppositeRef, oppositeAlt, strand);
		} catch (InvalidGenomeVariant invalidGenomeVariant) {
			throw new UncheckedJannovarException("This cannot happen as check is done on construction.");
		}
	}

	/**
	 * @return human-readable {@link String} describing the genome change
	 */
	@Override
	public String toString() {
		if (pos.getStrand() != Strand.FWD)
			return withStrand(Strand.FWD).toString();
		else if (ref.equals("")) // handle insertion as special case
			return Joiner.on("").join(getChrName(), ":g.", getPos(), "_", getPos() + 1, "ins", alt);
		else if (alt.equals("") && ref.length() == 1) // single-base deletion
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "del", ref);
		else if (alt.equals("") && ref.length() > 1) // multi-base deletion
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "_", getPos() + ref.length(), "del", ref);
		else if (ref.length() == 1 && alt.length() > 1)
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "del", ref, "ins", alt);
		else if (ref.length() > 1 && alt.length() != 0)
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "_", getPos() + ref.length(), "del", ref,
				"ins", alt);
		else
			return Joiner.on("").join(pos, (ref.equals("") ? "-" : ref), ">", (alt.equals("") ? "-" : alt));
	}

	/**
	 * @return the {@link GenomeVariantType} of this GenomeChange
	 */
	public GenomeVariantType getType() {
		if (ref.length() > 0 && alt.length() == 0)
			return GenomeVariantType.DELETION;
		else if (ref.length() == 0 && alt.length() > 0)
			return GenomeVariantType.INSERTION;
		else if (ref.length() == 1 && alt.length() == 1)
			return GenomeVariantType.SNV;
		else
			return GenomeVariantType.BLOCK_SUBSTITUTION;
	}

	/**
	 * A transition is between purine and purine or between pyrimidine and pyrimidine. Only applies to single nucleotide
	 * subsitutions.
	 *
	 * @return true if the variant is a SNV and a transition.
	 */
	public boolean isTransition() {
		if (getType() != GenomeVariantType.SNV)
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
		if (getType() != GenomeVariantType.SNV)
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
		if (pos != null && pos.getStrand() != null && pos.getStrand().isReverse())
			return withStrand(Strand.FWD).hashCode();
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

		GenomeVariant other = (GenomeVariant) obj;
		if (pos != null && pos.getStrand() != Strand.FWD)
			return withStrand(Strand.FWD).equals(obj);
		other = other.withStrand(Strand.FWD);

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

	@Override
	public int compareTo(Annotation other) {
		return ComparisonChain.start().compare(pos, other.getPos()).compare(ref, other.getRef())
			.compare(alt, other.getAlt()).result();
	}

}
