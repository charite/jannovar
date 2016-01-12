package de.charite.compbio.jannovar.reference;

/**
 * Representation for forward/backward strand.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public enum Strand {

	/** forward strand */
	FWD,
	/** reverse strand */
	REV;

	/** @return <code>true</code> if this is the forward strand */
	public boolean isForward() {
		return (this == FWD);
	}

	/** @return <code>true</code> if this is the reverse strand */
	public boolean isReverse() {
		return (this == REV);
	}

	@Override
	public String toString() {
		return (this == FWD) ? "+" : "-";
	}

	/**
	 * @return {@link #REV} if the char is <code>'-'</code>, otherwise returns {@link #FWD}.
	 */
	public Strand valueOf(char strand) {
		return (strand == '-') ? REV : FWD;
	}
}
