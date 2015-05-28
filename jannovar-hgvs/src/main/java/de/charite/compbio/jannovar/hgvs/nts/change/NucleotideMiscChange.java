package de.charite.compbio.jannovar.hgvs.nts.change;

/**
 * Represents a silent protein-level change, i.e., "p.=".
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideMiscChange extends NucleotideChange {

	/** type of the misc change */
	private final NucleotideMiscChangeType changeType;

	/**
	 * Static factory function that forwards to {@link #NucleotideMiscChange(boolean, NucleotideMiscChangeType)}.
	 */
	public static NucleotideMiscChange build(boolean onlyPredicted, NucleotideMiscChangeType changeType) {
		return new NucleotideMiscChange(onlyPredicted, changeType);
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
