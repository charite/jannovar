package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

/**
 * Represents a silent protein-level change, i.e., "p.=".
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinMiscChange extends ProteinChange {

	/** type of the misc change */
	private final ProteinMiscChangeType changeType;

	/**
	 * Static factory function that forwards to {@link #ProteinMiscChange(boolean, ProteinMiscChangeType)}.
	 */
	public static ProteinMiscChange build(boolean onlyPredicted, ProteinMiscChangeType changeType) {
		return new ProteinMiscChange(onlyPredicted, changeType);
	}

	/**
	 * Construct with given <code>changeType</code> and <code>onlyPredicted</code> flag.
	 *
	 * @param onlyPredicted
	 *            whether or not the change is only predicted
	 * @param changeType
	 *            type of the change
	 */
	public ProteinMiscChange(boolean onlyPredicted, ProteinMiscChangeType changeType) {
		super(onlyPredicted);
		this.changeType = changeType;
	}

	/** @return type of this {@link ProteinMiscChangeType} */
	public ProteinMiscChangeType getChangeType() {
		return changeType;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
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
		ProteinMiscChange other = (ProteinMiscChange) obj;
		if (changeType != other.changeType)
			return false;
		return true;
	}

	@Override
	public ProteinChange withOnlyPredicted(boolean onlyPredicted) {
		return new ProteinMiscChange(onlyPredicted, this.changeType);
	}

}
