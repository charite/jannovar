package de.charite.compbio.jannovar.hgvs.nts.change;

/**
 * Enumeration of the miscellaneous nucleotide change types (for RNA).
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum NucleotideMiscChangeType {

	/** same RNA change as on DNA level (<code>(?)</code>) */
	SAME_AS_DNA,
	/** unknown effect (<code>?</code>) */
	UNKNOWN_EFFECT,
	/** splicing is expected to be affected (<code>spl?</code>) */
	SPLICING_AFFECTED,
	/** no change (<code>=</code>) */
	NO_CHANGE,
	/** no RNA is produced (<code>0</code>) */
	NO_RNA;

	/**
	 * @return String with HGVS representation of the misc change type
	 */
	public String toHGVSString(boolean onlyPredicted) {
		switch (this) {
		case SAME_AS_DNA:
			return "(?)";
		case UNKNOWN_EFFECT:
			return "?";
		case SPLICING_AFFECTED:
			return onlyPredicted ? "(spl?)" : "spl?";
		case NO_CHANGE:
			return onlyPredicted ? "(=)" : "=";
		case NO_RNA:
			return onlyPredicted ? "(0)" : "0";
		default:
			throw new RuntimeException("Unknown protein misc change tyep " + this);
		}
	}

}
