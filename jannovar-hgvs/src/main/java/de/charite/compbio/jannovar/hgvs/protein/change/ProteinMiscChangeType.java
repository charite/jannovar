package de.charite.compbio.jannovar.hgvs.protein.change;

/**
 * Enum with for the miscellaneous protein changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum ProteinMiscChangeType {

	/** difficult to predict (<code>?</code>) */
	DIFFICULT_TO_PREDICT,
	/** no change (<code>=</code>) */
	NO_CHANGE,
	/** no protein is produced */
	NO_PROTEIN;

	/**
	 * @param onlyPredicted
	 *            whether or not the change was only predictd
	 * @return String with HGVS representation of the misc change type, together with the <code>onlyPredicted</code>
	 *         flag
	 */
	public String toHGVSString(boolean onlyPredicted) {
		switch (this) {
		case DIFFICULT_TO_PREDICT:
			return "?";
		case NO_CHANGE:
			return onlyPredicted ? "(=)" : "=";
		case NO_PROTEIN:
			return onlyPredicted ? "0?" : "0";
		default:
			throw new RuntimeException("Unknown protein misc change tyep " + this);
		}
	}

}
