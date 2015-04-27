package de.charite.compbio.jannovar.impl.parse.gff;

import de.charite.compbio.jannovar.Immutable;

/**
 * Wraps information about the version of a GFF/GTF file and allows to query format specific values.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class GFFVersion {

	/** the version */
	private final int version;
	/** the string to use for separating attributes */
	private final String valueSeparator;

	public GFFVersion(int version) {
		this.version = version;
		this.valueSeparator = (version == 3) ? "=" : " ";
	}

	/** @return GFF version */
	public int getVersion() {
		return version;
	}

	/** @return string used for separating keys and values */
	public String getValueSeparator() {
		return valueSeparator;
	}

	@Override
	public String toString() {
		return Integer.toString(version);
	}

}