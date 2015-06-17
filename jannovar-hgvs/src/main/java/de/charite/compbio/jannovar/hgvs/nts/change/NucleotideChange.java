package de.charite.compbio.jannovar.hgvs.nts.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

// TODO(holtgrewe): NucleotideTranslocation, GeneConversion are currently still missing.

/**
 * Base class for nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class NucleotideChange implements ConvertibleToHGVSString {

	/** change is only predicted and will be kept in parantheses */
	private final boolean onlyPredicted;

	/**
	 * @param onlyPredicted
	 */
	public NucleotideChange(boolean onlyPredicted) {
		this.onlyPredicted = onlyPredicted;
	}

	/** @return {@link NucleotideChange} with given <code>onlyPredicted</code> value. */
	public abstract NucleotideChange withOnlyPredicted(boolean flag);

	/** @return <code>true</code> if the protein change is only predicted */
	public boolean isOnlyPredicted() {
		return onlyPredicted;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return toHGVSString();
	}

	/**
	 * @return <code>s</code> wrapped in parantheses if not {@link #onlyPredicted} and plain <code>s</code> otherwise.
	 */
	protected String wrapIfOnlyPredicted(String s) {
		if (onlyPredicted)
			return "(" + s + ")";
		else
			return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (onlyPredicted ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NucleotideChange other = (NucleotideChange) obj;
		if (onlyPredicted != other.onlyPredicted)
			return false;
		return true;
	}

}
