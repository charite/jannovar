package de.charite.compbio.jannovar.hgvs.nts.variant;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;

/**
 * Nucleotide change with one allele only.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class SingleAlleleNucleotideVariant extends NucleotideVariant {

	protected final NucleotideChangeAllele allele;

	/**
	 * @return nucleotide variant for one change only
	 */
	public static SingleAlleleNucleotideVariant makeSingleChangeVariant(SequenceType seqType, String seqID,
			NucleotideChange change) {
		return new SingleAlleleNucleotideVariant(seqType, seqID, VariantConfiguration.IN_CIS, ImmutableList.of(change));
	}

	/**
	 * @return single-change {@link SingleAlleleNucleotideVariant} with the given {@link SequenceType},
	 *         reference/transcript ID, and {@link VariantConfiguration}
	 */
	public static SingleAlleleNucleotideVariant build(SequenceType seqType, String seqID,
			VariantConfiguration varConfig, NucleotideChange... changes) {
		return new SingleAlleleNucleotideVariant(seqType, seqID, varConfig, ImmutableList.copyOf(changes));
	}

	/**
	 * Construct {@link SingleAlleleNucleotideVariant}
	 *
	 * @param seqType
	 *            type of the changed sequence
	 * @param seqID
	 *            ID of the reference/transcript that the change is on
	 * @param varConfig
	 *            {@link VariantConfiguration} of the {@link NucleotideChange}s in the allele
	 * @param changes
	 *            {@link NucleotideChange}s to use for the single allele
	 */
	public SingleAlleleNucleotideVariant(SequenceType seqType, String seqID, VariantConfiguration varConfig,
			Collection<? extends NucleotideChange> changes) {
		this(seqType, seqID, new NucleotideChangeAllele(varConfig, changes));
	}

	/**
	 * Construct {@link SingleAlleleNucleotideVariant}
	 */
	public SingleAlleleNucleotideVariant(SequenceType seqType, String refID, String proteinID, int transcriptVersion,
			NucleotideChangeAllele allele) {
		super(seqType, refID, proteinID, transcriptVersion);
		this.allele = allele;
	}

	/**
	 * Construct {@link SingleAlleleNucleotideVariant}
	 *
	 * @param seqType
	 *            type of the changed sequence
	 * @param seqID
	 *            ID of the reference/transcript that the change is on
	 * @param allele
	 *            {@link NucleotideChangeAllele} to use
	 */
	public SingleAlleleNucleotideVariant(SequenceType seqType, String seqID, NucleotideChangeAllele allele) {
		super(seqType, seqID);
		this.allele = allele;
	}

	/** @return <code>true</code> if the variant has only one {@link NucleotideChange}. */
	public boolean hasOnlyOneChange() {
		return (allele.size() == 1);
	}

	/** @return first change, convenience method for single-change variants */
	public NucleotideChange getChange() {
		return allele.get(0);
	}

	/** @return list of changes */
	public NucleotideChangeAllele getAllele() {
		return allele;
	}

	@Override
	public String toHGVSString() {
		if (hasOnlyOneChange())
			return Joiner.on("").join(getSequenceNamePrefix(), ":", seqType.getPrefix(), getChange().toHGVSString());

		final String sep = allele.getVarConfig().toHGVSSeparator();

		ArrayList<String> parts = new ArrayList<>();
		parts.add(getSequenceNamePrefix());
		parts.add(":");
		parts.add(seqType.getPrefix());
		if (hasOnlyOneChange()) {
			parts.add(getChange().toHGVSString());
		} else {
			parts.add("[");
			boolean first = true;
			for (NucleotideChange change : allele) {
				if (first)
					first = false;
				else
					parts.add(sep);
				parts.add(change.toHGVSString());
			}
			parts.add("]");
		}
		return Joiner.on("").join(parts);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((allele == null) ? 0 : allele.hashCode());
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
		SingleAlleleNucleotideVariant other = (SingleAlleleNucleotideVariant) obj;
		if (allele == null) {
			if (other.allele != null)
				return false;
		} else if (!allele.equals(other.allele))
			return false;
		return true;
	}

}
