package de.charite.compbio.jannovar.hgvs.protein.variant;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

/**
 * Protein variant having multiple alleles.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class MultiAlleleProteinVariant extends ProteinVariant {

	/** alleles */
	protected final ImmutableList<ProteinChangeAllele> alleles;

	/**
	 * @return single-allele protein variant for the given changes
	 */
	public static MultiAlleleProteinVariant build(String proteinID, ProteinChangeAllele... alleles) {
		return new MultiAlleleProteinVariant(proteinID, ImmutableList.copyOf(alleles));
	}

	/** Construct with the given protein ID and collection of {@link ProteinChangeAllele}s. */
	public MultiAlleleProteinVariant(String proteinID, Collection<ProteinChangeAllele> alleles) {
		super(proteinID);
		this.alleles = ImmutableList.copyOf(alleles);
	}

	/** @return the alleles */
	public ImmutableList<ProteinChangeAllele> getAlleles() {
		return alleles;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		ArrayList<String> parts = new ArrayList<>();
		parts.add(getSequenceNamePrefix());
		parts.add(":p.");
		boolean first = true;
		for (ProteinChangeAllele allele : alleles) {
			if (first)
				first = false;
			else
				parts.add(";");
			parts.add(allele.toHGVSString(code));
		}
		return Joiner.on("").join(parts);
	}

	@Override
	public String toString() {
		return "MultiAlleleProteinVariant [alleles=" + alleles + "]";
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
		MultiAlleleProteinVariant other = (MultiAlleleProteinVariant) obj;
		if (alleles == null) {
			if (other.alleles != null)
				return false;
		} else if (!alleles.equals(other.alleles))
			return false;
		return true;
	}

}
