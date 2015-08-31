package de.charite.compbio.jannovar.pedigree;

import java.util.ArrayList;
import java.util.List;

import htsjdk.variant.variantcontext.VariantContext;

public class InheritanceVariantContext extends VariantContext {

	static final class ListBuilder {

		List<InheritanceVariantContext> list = new ArrayList<InheritanceVariantContext>();

		public ListBuilder variants(List<VariantContext> vcList) {
			for (VariantContext vc : vcList) {
				list.add(new InheritanceVariantContext(vc));
			}

			return this;
		}

		public List<InheritanceVariantContext> build() {
			return list;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6381923752159274326L;

	private boolean matchInheritance;

	protected InheritanceVariantContext(VariantContext other) {
		super(other);
		this.matchInheritance = false;
	}

	public void setMatchInheritance(boolean matchInheritance) {
		this.matchInheritance = matchInheritance;
	}
	
	public Genotype getSingleSampleGenotype() {
		return getGenotype(getGenotype(0));
	}
	
	public boolean isMatchInheritance() {
		return matchInheritance;
	}

	private Genotype getGenotype(htsjdk.variant.variantcontext.Genotype g) {
		if (g.isHet() || g.isHetNonRef())
			return Genotype.HETEROZYGOUS;
		else if (g.isHomRef())
			return Genotype.HOMOZYGOUS_REF;
		else if (g.isHomVar())
			return Genotype.HOMOZYGOUS_ALT;
		else
			return Genotype.NOT_OBSERVED;
	}

	public Genotype getGenotype(Person p) {
		return getGenotype(getGenotype(p.getName()));
	}

}
