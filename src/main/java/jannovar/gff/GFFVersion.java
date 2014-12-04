package jannovar.gff;

import jannovar.common.Immutable;

/**
 * Wraps information about the version of a GFF/GTF file and allows to query format specific values.
 */
@Immutable
public class GFFVersion {
	/** the version */
	public final int version;
	/** the string to use for separating attributes */
	public final String valueSeparator;

	public GFFVersion(int version) {
		this.version = version;
		this.valueSeparator = (version == 3) ? "=" : " ";
	}

	@Override
	public String toString() {
		return String.format("%d", version);
	}
}