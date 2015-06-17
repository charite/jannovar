package de.charite.compbio.jannovar.hgvs.nts.variant;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.SequenceType;

/**
 * Nucleotide variant having multiple alleles.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class MultiAlleleNucleotideVariant extends NucleotideVariant {

	/** alleles */
	protected final ImmutableList<NucleotideChangeAllele> alleles;

	/**
	 * @return single-allele nucleotide variant for the given changes
	 */
	public static MultiAlleleNucleotideVariant build(SequenceType seqType, String seqID,
			NucleotideChangeAllele... alleles) {
		return new MultiAlleleNucleotideVariant(seqType, seqID, ImmutableList.copyOf(alleles));
	}

	/**
	 * Construct with the given {@link SequenceType}, reference/transcript ID, protein ID, transcript version, and
	 * collection of {@link NucleotideChangeAllele}s.
	 */
	public MultiAlleleNucleotideVariant(SequenceType seqType, String refID, String proteinID, int transcriptVersion,
			Collection<NucleotideChangeAllele> alleles) {
		super(seqType, refID, proteinID, transcriptVersion);
		this.alleles = ImmutableList.copyOf(alleles);
	}

	/**
	 * Construct with the given {@link SequenceType}, reference/transcript ID and collection of
	 * {@link NucleotideChangeAllele}s.
	 */
	public MultiAlleleNucleotideVariant(SequenceType seqType, String seqID, Collection<NucleotideChangeAllele> alleles) {
		super(seqType, seqID);
		this.alleles = ImmutableList.copyOf(alleles);
	}

	/** @return the alleles */
	public ImmutableList<NucleotideChangeAllele> getAlleles() {
		return alleles;
	}

	@Override
	public String toHGVSString() {
		ArrayList<String> parts = new ArrayList<>();
		parts.add(getRefIDWithVersion());
		parts.add(":");
		parts.add(seqType.getPrefix());
		boolean first = true;
		for (NucleotideChangeAllele allele : alleles) {
			if (first)
				first = false;
			else
				parts.add(";");
			parts.add(allele.toHGVSString());
		}
		return Joiner.on("").join(parts);
	}

	@Override
	public String toString() {
		return "MultiAlleleNucleotideVariant [alleles=" + alleles + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alleles == null) ? 0 : alleles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiAlleleNucleotideVariant other = (MultiAlleleNucleotideVariant) obj;
		if (alleles == null) {
			if (other.alleles != null)
				return false;
		} else if (!alleles.equals(other.alleles))
			return false;
		return true;
	}

}
