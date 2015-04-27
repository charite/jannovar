package de.charite.compbio.jannovar.filter;


/**
 * Store {@link FlaggedVariant} and a counter.
 */
class FlaggedVariantCounter {
	private final FlaggedVariant var;
	private int count;

	FlaggedVariantCounter(FlaggedVariant var, int count) {
		this.var = var;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public FlaggedVariant getVar() {
		return var;
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