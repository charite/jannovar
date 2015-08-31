package de.charite.compbio.jannovar.pedigree;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Wrapper for a {@link List} of {@link VariantContext} for one {@link TranscriptInfo}.
 *
 * This name list is used for ensuring that the same order and number of individuals is used in the VCF file as in the
 * pedigree file.
 *
 * Max Schubach <max.schubach@charite.de>
 */
public final class InheritanceVariantContextList {

	/** the list of individual names */
	private final ImmutableList<String> names;

	/** whether or not the variants or only some of them are on the X chromsome */
	private final boolean isXChromosomal;
	/** whether or not the variants or only some of them are on the Autosomes */
	private final boolean isAutosomal;

	private final List<InheritanceVariantContext> vcList;
	

	/** the lists of genotype calls, each contains one entry for each individual */
	// private final ImmutableList<ImmutableList<Genotype>> calls;

	public InheritanceVariantContextList(List<VariantContext> vcList) {
		this.names = getNames(vcList);
		boolean[] chromosomalLocation = getChromosomalLocation(vcList);
		isAutosomal = chromosomalLocation[0];
		isXChromosomal = chromosomalLocation[1];
		this.vcList = new InheritanceVariantContext.ListBuilder().variants(vcList).build();
	}

	private boolean[] getChromosomalLocation(List<VariantContext> vcList) {
		boolean[] output = new boolean[] { false, false };
		for (VariantContext vc : vcList) {
			output[0] = output[0] || isAutosomal(vc);
			output[1] = output[1] || isXChromosomal(vc);
		}
		return output;

	}

	private boolean isXChromosomal(VariantContext vc) {
		return vc.getContig().toLowerCase().matches("(^chrX$)|(^X$)|(^chr23$)|(^23$)");
	}

	private boolean isAutosomal(VariantContext vc) {
		return vc.getContig().toLowerCase()
				.matches("(^chr([0-9])|(1[0-9])|(2[012])$)|(^([0-9])|(1[0-9])|(2[012])$)");
	}

	private ImmutableList<String> getNames(List<VariantContext> vcList) {
		ImmutableList.Builder<String> namesBuilder = new ImmutableList.Builder<String>();
		for (VariantContext vc : vcList) {
			namesBuilder.addAll(vc.getSampleNames());
			break;
		}
		return namesBuilder.build();
	}

	/** the list of individual names */
	public ImmutableList<String> getNames() {
		return names;
	}

	/** whether or not the variants are on the X chromsome */
	public boolean isXChromosomal() {
		return this.isXChromosomal;
	};
	
	/** whether or not the variants are on the Autosome */
	public boolean isAutosomal() {
		return this.isAutosomal;
	};

	public List<InheritanceVariantContext> getVcList() {
		return vcList;
	}
	/**
	 * Check whether the {@link #names} of this GenotypeList are the same as the names of the members of
	 * <code>pedigree</code>.
	 *
	 * For this, the order of the names has to be the same as the number of the names. This check is important for the
	 * {@link PedigreeDiseaseCompatibilityDecorator}, where the names in the pedigree must be the same as the names in
	 * the genotype list.
	 *
	 * @return <code>true</code> if the list of {@link #names} is the same as the names of the members of
	 *         <code>pedigree</code>
	 */
	public boolean namesEqual(Pedigree pedigree) {
		for (String name : names) {
			if (!pedigree.getNames().contains(name))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GenotypeList(" + Joiner.on(",").join(vcList) + ")";
	}

}
