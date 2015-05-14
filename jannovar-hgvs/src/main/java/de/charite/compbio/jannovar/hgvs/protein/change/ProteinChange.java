package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;

// TODO(holtgrewe): add support for frameshift changes, short sequence repeats, and extension

/**
 * Base class for protein changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
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

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	/** @return <code>s</code> wrapped in parantheses if not {@link #onlyPredicted} and plain <code>s</code> otherwise. */
	protected String wrapIfPredicted(String s) {
		if (onlyPredicted)
			return "(" + s + ")";
		else
			return s;
	}

}
