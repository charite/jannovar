package de.charite.compbio.jannovar.hgvs.nts.change;

/**
 * Represents a silent protein-level change, i.e., "p.=".
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideMiscChange extends NucleotideChange {

	/** type of the misc change */
	private final NucleotideMiscChangeType changeType;

	/**
	 * Static factory function that forwards to {@link #NucleotideMiscChange(boolean, NucleotideMiscChangeType)}.
	 * 
	 * @param onlyPredicted
	 *            whether or not to set "only predicted" flag
	 * @param changeType
	 *            the {@link NucleotideMiscChangeType} to build for
	 * @return resulting {@link NucleotideMiscChange}
	 */
	public static NucleotideMiscChange build(boolean onlyPredicted, NucleotideMiscChangeType changeType) {
		return new NucleotideMiscChange(onlyPredicted, changeType);
	}

	/**
	 * Build from input string.
	 * 
	 * @param str
	 *            <code>String</code> to build from
	 * @return resulting {@link NucleotideMiscChange}
	 */
	public static NucleotideMiscChange buildFromString(String str) {
		if (str.equals("(?)"))
			return build(false, NucleotideMiscChangeType.SAME_AS_DNA);
		else if (str.equals("?"))
			return build(false, NucleotideMiscChangeType.UNKNOWN_EFFECT);
		else if (str.equals("(spl?)"))
			return build(true, NucleotideMiscChangeType.SPLICING_AFFECTED);
		else if (str.equals("spl?"))
			return build(false, NucleotideMiscChangeType.SPLICING_AFFECTED);
		else if (str.equals("(=)"))
			return build(true, NucleotideMiscChangeType.NO_CHANGE);
		else if (str.equals("="))
			return build(false, NucleotideMiscChangeType.NO_CHANGE);
		else if (str.equals("(0)"))
			return build(true, NucleotideMiscChangeType.NO_RNA);
		else if (str.equals("0"))
			return build(false, NucleotideMiscChangeType.NO_RNA);
		else
			throw new IllegalArgumentException("Invalid nucleotide misc change string " + str);
	}

	/**
	 * Construct with given <code>changeType</code> and <code>onlyPredicted</code> flag.
	 *
	 * @param onlyPredicted
	 *            whether or not the change is only predicted
	 * @param changeType
	 *            type of the change
	 */
	public NucleotideMiscChange(boolean onlyPredicted, NucleotideMiscChangeType changeType) {
		super(onlyPredicted);
		this.changeType = changeType;
	}

	@Override
	public NucleotideMiscChange withOnlyPredicted(boolean flag) {
		return new NucleotideMiscChange(flag, changeType);
	}

	/** @return type of this {@link NucleotideMiscChangeType} */
	public NucleotideMiscChangeType getChangeType() {
		return changeType;
	}

	@Override
	public String toHGVSString() {
		return changeType.toHGVSString(isOnlyPredicted());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changeType == null) ? 0 : changeType.hashCode());
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
		NucleotideMiscChange other = (NucleotideMiscChange) obj;
		if (changeType != other.changeType)
			return false;
		return true;
	}

}
