package de.charite.compbio.jannovar.filter;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Adds a boolean flag to a {@link VariantContext} that shows whether the variant has been included.
 *
 * By default, variants are not included.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite>
 */
public class FlaggedVariant {

	private boolean included = false;

	private final VariantContext vc;

	/** Initialize with the given {@link VariantContext}. */
	public FlaggedVariant(VariantContext vc) {
		this.vc = vc;
	}

	public VariantContext getVC() {
		return vc;
	}

	/** @return <code>true</code> if flagged as included */
	public boolean isIncluded() {
		return included;
	}

	/** Sets inclusion flag. */
	public void setIncluded(boolean included) {
		this.included = included;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vc == null) ? 0 : vc.getContig().hashCode());
		result = prime * result + ((vc == null) ? 0 : vc.getStart());
		result = prime * result + ((vc == null) ? 0 : vc.getReference().hashCode());
		result = prime * result + ((vc == null) ? 0 : vc.getAlleles().hashCode());
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
		FlaggedVariant other = (FlaggedVariant) obj;
		if (included != other.included)
			return false;
		if (vc == null) {
			if (other.vc != null)
				return false;
		} else if (!vc.equals(other.vc))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Included:" + isIncluded() + " -> " + getVC().toString();
	}

}
