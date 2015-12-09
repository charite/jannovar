package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

/**
 * Base class for protein changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public abstract class ProteinChange implements ConvertibleToHGVSString {

	/** change is only predicted and will be kept in parantheses */
	private final boolean onlyPredicted;

	/**
	 * @param onlyPredicted
	 */
	public ProteinChange(boolean onlyPredicted) {
		this.onlyPredicted = onlyPredicted;
	}

	/** @return <code>true</code> if the protein change is only predicted */
	public boolean isOnlyPredicted() {
		return onlyPredicted;
	}

	/** @return <code>ProteinChange</code> object with prediction state set to the one given by the parameter */
	abstract public ProteinChange withOnlyPredicted(boolean onlyPredicted);

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	/** @return <code>s</code> wrapped in parantheses if not {@link #onlyPredicted} and plain <code>s</code> otherwise. */
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
		ProteinChange other = (ProteinChange) obj;
		if (onlyPredicted != other.onlyPredicted)
			return false;
		return true;
	}

}
