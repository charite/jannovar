package de.charite.compbio.jannovar.mendel.filter;

import java.util.TreeSet;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * A {@link VariantContext} with an integer counter and set of compatible modes
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VariantContextCounter {

	private VariantContext variantContext;
	private int counter;
	private TreeSet<ModeOfInheritance> compatibleModes;

	public VariantContextCounter(VariantContext variantContext) {
		this(variantContext, 0);
	}

	public VariantContextCounter(VariantContext variantContext, int counter) {
		this.variantContext = variantContext;
		this.counter = counter;
		this.compatibleModes = new TreeSet<>();
	}

	public int increment() {
		return ++this.counter;
	}

	public int decrement() {
		--this.counter;
		if (this.counter < 0)
			throw new RuntimeException("Negative counter " + this.counter);
		return this.counter;
	}

	public VariantContext getVariantContext() {
		return variantContext;
	}

	public void setVariantContext(VariantContext variantContext) {
		this.variantContext = variantContext;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
		if (this.counter < 0)
			throw new RuntimeException("Negative counter " + this.counter);
	}

	public void addCompatibleMode(ModeOfInheritance mode) {
		this.compatibleModes.add(mode);
	}

	public TreeSet<ModeOfInheritance> getCompatibleModes() {
		return compatibleModes;
	}

	public void setCompatibleModes(TreeSet<ModeOfInheritance> compatibleModes) {
		this.compatibleModes = compatibleModes;
	}

	@Override
	public String toString() {
		return "VariantContextCounter [variantContext=" + variantContext + ", counter=" + counter + ", compatibleModes="
				+ compatibleModes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compatibleModes == null) ? 0 : compatibleModes.hashCode());
		result = prime * result + counter;
		result = prime * result + ((variantContext == null) ? 0 : variantContext.hashCode());
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
		VariantContextCounter other = (VariantContextCounter) obj;
		if (compatibleModes == null) {
			if (other.compatibleModes != null)
				return false;
		} else if (!compatibleModes.equals(other.compatibleModes))
			return false;
		if (counter != other.counter)
			return false;
		if (variantContext == null) {
			if (other.variantContext != null)
				return false;
		} else if (!variantContext.equals(other.variantContext))
			return false;
		return true;
	}

}
