package de.charite.compbio.jannovar.hgvs.protein.variant;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;

/**
 * Protein change with one allele only.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class SingleAlleleProteinVariant extends ProteinVariant {

	protected final ProteinChangeAllele allele;

	/**
	 * @return protein variant for one change only
	 */
	public static SingleAlleleProteinVariant makeSingleChangeVariant(String proteinID, ProteinChange change) {
		return new SingleAlleleProteinVariant(proteinID, VariantConfiguration.IN_CIS, ImmutableList.of(change));
	}

	/**
	 * @return single-change {@link SingleAlleleProteinVariant} with the given protein ID and
	 *         {@link VariantConfiguration}
	 */
	public static SingleAlleleProteinVariant build(String proteinID, VariantConfiguration varConfig,
			ProteinChange... changes) {
		return new SingleAlleleProteinVariant(proteinID, varConfig, ImmutableList.copyOf(changes));
	}

	/**
	 * Construct {@link SingleAlleleProteinVariant}
	 *
	 * @param proteinID
	 *            ID of the protein that the change is on
	 * @param varConfig
	 *            {@link VariantConfiguration} of the {@link ProteinChange}s in the allele
	 * @param changes
	 *            {@link ProteinChange}s to use for the single allele
	 */
	public SingleAlleleProteinVariant(String proteinID, VariantConfiguration varConfig,
			Collection<? extends ProteinChange> changes) {
		super(proteinID);
		this.allele = new ProteinChangeAllele(varConfig, changes);
	}

	/** @return <code>true</code> if the variant has only one {@link ProteinChange}. */
	public boolean hasOnlyOneChange() {
		return (allele.size() == 1);
	}

	/** @return first change, convenience method for single-change variants */
	public ProteinChange getChange() {
		return allele.get(0);
	}

	/** @return list of changes */
	public ProteinChangeAllele getAllele() {
		return allele;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		if (hasOnlyOneChange())
			return Joiner.on("").join(getSequenceNamePrefix(), ":p.", getChange().toHGVSString(code));

		final String sep = allele.getVarConfig().toHGVSSeparator();

		ArrayList<String> parts = new ArrayList<>();
		parts.add(getSequenceNamePrefix());
		parts.add(":p.");
		if (hasOnlyOneChange()) {
			parts.add(getChange().toHGVSString(code));
		} else {
			parts.add("[");
			boolean first = true;
			for (ProteinChange change : allele) {
				if (first)
					first = false;
				else
					parts.add(sep);
				parts.add(change.toHGVSString(code));
			}
			parts.add("]");
		}
		return Joiner.on("").join(parts);
	}

	@Override
	public String toString() {
		return "SingleAlleleProteinVariant [allele=" + allele + ", toHGVSString()=" + toHGVSString() + "]";
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
		SingleAlleleProteinVariant other = (SingleAlleleProteinVariant) obj;
		if (allele == null) {
			if (other.allele != null)
				return false;
		} else if (!allele.equals(other.allele))
			return false;
		return true;
	}

}
