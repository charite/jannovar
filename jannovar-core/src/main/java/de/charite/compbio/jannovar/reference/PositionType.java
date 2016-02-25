package de.charite.compbio.jannovar.reference;

/**
 * Enum for differentiating between one- and zero-based positions.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public enum PositionType {
	/** positions start at zero and intervals are half-opened <tt>[begin, end)</tt>. */
	ZERO_BASED,
	/** positions start at zero and intervals are closed <tt>[begin, end]</tt>. */
	ONE_BASED
}
