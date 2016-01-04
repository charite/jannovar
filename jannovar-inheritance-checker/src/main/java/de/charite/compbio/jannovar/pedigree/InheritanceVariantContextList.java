package de.charite.compbio.jannovar.pedigree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import htsjdk.variant.variantcontext.VariantContext;

/**
 *
 * This class is a wrapper for a {@link java.util.List} of {@link htsjdk.variant.variantcontext.VariantContext}. It
 * transforms every {@link htsjdk.variant.variantcontext.VariantContext} into an
 * {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContext}. At also has teh ability to find out if the
 * variants are XChromosomal or autosomal (or both).
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @version 0.15-SNAPSHOT
 */
public final class InheritanceVariantContextList {

	/** the list of individual names */
	private final ImmutableList<String> names;

	/** whether or not the variants or only some of them are on the X chromsome */
	private final boolean isXChromosomal;
	/** whether or not the variants or only some of them are on the Autosomes */
	private final boolean isAutosomal;
	/** List of the {@link InheritanceVariantContext} */
	private final List<InheritanceVariantContext> vcList;

	/**
	 * Default constructor. Sets the List, the {@link #names}, and the chromosomal locations.
	 *
	 * @param vcList
	 *            The {@link htsjdk.variant.variantcontext.VariantContext} list to store into this wrapper.
	 */
	public InheritanceVariantContextList(List<VariantContext> vcList) {
		this.names = getNames(vcList);
		boolean[] chromosomalLocation = getChromosomalLocation(vcList);
		isAutosomal = chromosomalLocation[0];
		isXChromosomal = chromosomalLocation[1];
		this.vcList = new InheritanceVariantContext.ListBuilder().variants(vcList).build();
	}

	/**
	 * Goes through the variants and checks weather they lie on the the X chromosome or on the autosomes (or both)
	 * 
	 * @param vcList
	 *            List to iterate through the contigs.
	 * @return Boolean array of size 2. First value is the autosomal, second the xchromosomal.
	 */
	private boolean[] getChromosomalLocation(List<VariantContext> vcList) {
		boolean[] output = new boolean[] { false, false };
		for (VariantContext vc : vcList) {
			output[0] = output[0] || isAutosomal(vc);
			output[1] = output[1] || isXChromosomal(vc);
		}
		return output;
	}

	/**
	 * @param vc
	 *            Variant to check
	 * @return <code>true</code> if the contig of the variant is on the chromosomeX (chrX|chr23|23|X)
	 */
	private boolean isXChromosomal(VariantContext vc) {
		return vc.getContig().toLowerCase().matches("(^chrx$)|(^x$)|(^chr23$)|(^23$)");
	}

	/**
	 * @param vc
	 *            Variant to check
	 * @return <code>true</code> if the contig of the variant is on one of the autosomes (chr1-chr22|1-22)
	 */
	private boolean isAutosomal(VariantContext vc) {
		return vc.getContig().toLowerCase().matches("(^chr(([0-9])|(1[0-9])|(2[012]))$)|(^([0-9])|(1[0-9])|(2[012])$)");
	}

	/**
	 * Extracts the sampel names out of the first variant.
	 * 
	 * @param vcList
	 *            List of {@link VariantContext} to extract the names out of the first variant.
	 * @return The sample names stored in the first variant of the list. If the list is empty the names are empty.
	 */
	private ImmutableList<String> getNames(List<VariantContext> vcList) {
		ImmutableList.Builder<String> namesBuilder = new ImmutableList.Builder<String>();
		for (VariantContext vc : vcList) {
			namesBuilder.addAll(vc.getSampleNames());
			break;
		}
		return namesBuilder.build();
	}

	/**
	 * the list of individual names
	 *
	 * @return a {@link com.google.common.collect.ImmutableList} object.
	 */
	public ImmutableList<String> getNames() {
		return names;
	}

	/**
	 * whether or not the variants are on the X chromsome
	 *
	 * @return a boolean.
	 */
	public boolean isXChromosomal() {
		return this.isXChromosomal;
	};

	/**
	 * whether or not the variants are on the Autosome
	 *
	 * @return a boolean.
	 */
	public boolean isAutosomal() {
		return this.isAutosomal;
	};

	/**
	 * <p>
	 * Getter for the field <code>vcList</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<InheritanceVariantContext> getVcList() {
		return vcList;
	}

	/**
	 * Check whether the {@link #names} of this GenotypeList are the same as the names of the members of
	 * <code>pedigree</code>.
	 *
	 * For this, the order of the names has to be the same as the number of the names. This check is important for the
	 * {@link de.charite.compbio.jannovar.pedigree.PedigreeDiseaseCompatibilityDecorator}, where the names in the
	 * pedigree must be the same as the names in the genotype list.
	 *
	 * @return <code>true</code> if the list of {@link #names} is the same as the names of the members of
	 *         <code>pedigree</code>
	 * @param pedigree
	 *            a {@link de.charite.compbio.jannovar.pedigree.Pedigree} object.
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
		return "InheritanceVariantContextList(" + Joiner.on(",").join(vcList) + ")";
	}

	/**
	 * Getter for all {@link htsjdk.variant.variantcontext.VariantContext} that matched the
	 * {@link de.charite.compbio.jannovar.pedigree.ModeOfInheritance}.
	 *
	 * @return A List of {@link htsjdk.variant.variantcontext.VariantContext} that are <code>true</code> for
	 *         {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContext#isMatchInheritance()}.
	 */
	public List<VariantContext> getMatchedVariants() {
		List<VariantContext> output = new ArrayList<VariantContext>();
		for (InheritanceVariantContext vc : getVcList()) {
			if (vc.isMatchInheritance())
				output.add(vc);
		}
		return output;

	}

	/**
	 * Getter for all possible associated {@link de.charite.compbio.jannovar.pedigree.ModeOfInheritance}.
	 *
	 * @return A Set of {@link de.charite.compbio.jannovar.pedigree.ModeOfInheritance} that are associated with the
	 *         list.
	 */
	public Set<ModeOfInheritance> getMatchedModeOfInheritances() {
		Set<ModeOfInheritance> output = new HashSet<ModeOfInheritance>();
		for (InheritanceVariantContext vc : getVcList()) {
			output.addAll(vc.getMatchedModesOfInheritance());
		}
		return output;
	}

	/**
	 * Getter for all {@link de.charite.compbio.jannovar.pedigree.ModeOfInheritance} for every variant context
	 *
	 * @return A Map with all {@link htsjdk.variant.variantcontext.VariantContext} and a set to which inheritance mode
	 *         they belong.
	 */
	public Map<VariantContext, Set<ModeOfInheritance>> getAnnotatedMap() {
		Map<VariantContext, Set<ModeOfInheritance>> output = new HashMap<VariantContext, Set<ModeOfInheritance>>();
		for (InheritanceVariantContext vc : getVcList()) {
				output.put(vc, vc.getMatchedModesOfInheritance());
		}
		return output;
	}

}
