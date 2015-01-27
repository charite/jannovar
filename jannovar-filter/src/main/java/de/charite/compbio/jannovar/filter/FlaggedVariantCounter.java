package de.charite.compbio.jannovar.filter;


/**
 * Store {@link FlaggedVariant} and a counter.
 */
class FlaggedVariantCounter {
	public final FlaggedVariant var;
	public int count;

	FlaggedVariantCounter(FlaggedVariant var, int count) {
		this.var = var;
		this.count = count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((var == null) ? 0 : var.hashCode());
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
		FlaggedVariantCounter other = (FlaggedVariantCounter) obj;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}

}