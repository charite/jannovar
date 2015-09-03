package de.charite.compbio.jannovar.pedigree;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessive;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.VariantContextCompatibilityCheckerXDominant;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.VariantContextCompatibilityCheckerXRecessive;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Base class for compatibility checkers with utility methods.
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @since 0.15
 */
public abstract class AbstractCompatibilityCheckerTest {

	protected final Genotype HET = Genotype.HETEROZYGOUS;
	protected final Genotype REF = Genotype.HOMOZYGOUS_REF;
	protected final Genotype ALT = Genotype.HOMOZYGOUS_ALT;
	protected final Genotype UKN = Genotype.NOT_OBSERVED;

	protected final Allele refAllele = Allele.create("A", true);
	protected final Allele altAllele = Allele.create("G", false);
	protected final Allele uknAllele = Allele.create(".", false);

	protected Pedigree pedigree;
	protected ImmutableList<String> names;

	String geneName = "bla";

	private List<VariantContext> getInheritanceVariantContextList(ImmutableList<Genotype> genotypes,
			boolean isXchromosomal) {

		List<VariantContext> vcs = new ArrayList<VariantContext>();
		vcs.add(getVariantContext(genotypes, (isXchromosomal ? "chrX" : "chr1"), 1));
		return vcs;
	}

	private List<VariantContext> getInheritanceVariantContextList(ImmutableList<Genotype> genotypes1,
			ImmutableList<Genotype> genotypes2, boolean isXchromosomal) {

		List<VariantContext> vcs = new ArrayList<VariantContext>();
		vcs.add(getVariantContext(genotypes1, (isXchromosomal ? "chrX" : "chr1"), 1));
		vcs.add(getVariantContext(genotypes2, (isXchromosomal ? "chrX" : "chr1"), 2));
		return vcs;
	}

	private VariantContext getVariantContext(ImmutableList<Genotype> genotypes, String chr, int pos) {
		VariantContextBuilder vcBuilder = new VariantContextBuilder().chr(chr).start(pos).stop(pos)
				.alleles(ImmutableList.of(refAllele, altAllele));
		List<htsjdk.variant.variantcontext.Genotype> vcGenotypes = new ArrayList<htsjdk.variant.variantcontext.Genotype>(
				genotypes.size());
		int i = 0;
		for (Genotype genotype : genotypes) {
			GenotypeBuilder gtBuilder = new GenotypeBuilder().name(names.get(i));

			switch (genotype) {
			case HETEROZYGOUS:
				gtBuilder = gtBuilder.alleles(ImmutableList.of(refAllele, altAllele));
				break;
			case HOMOZYGOUS_REF:
				gtBuilder = gtBuilder.alleles(ImmutableList.of(refAllele, refAllele));
				break;
			case HOMOZYGOUS_ALT:
				gtBuilder = gtBuilder.alleles(ImmutableList.of(altAllele, altAllele));
				break;
			case NOT_OBSERVED:
				gtBuilder = gtBuilder.alleles(ImmutableList.of(uknAllele, uknAllele));
				break;
			}
			vcGenotypes.add(i, gtBuilder.make());
			i++;
		}
		return vcBuilder.genotypes(vcGenotypes).make();
	}

	/**
	 * <p>buildCheckerAD.</p>
	 *
	 * @param gt a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalDominant buildCheckerAD(Genotype gt) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt), false);
		return new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAD.</p>
	 *
	 * @param gt1 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @param gt2 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalDominant buildCheckerAD(Genotype gt1, Genotype gt2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt1), ImmutableList.of(gt2),
				false);
		return new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAD.</p>
	 *
	 * @param list a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalDominant buildCheckerAD(ImmutableList<Genotype> list) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list, false);
		return new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAD.</p>
	 *
	 * @param list1 a {@link com.google.common.collect.ImmutableList} object.
	 * @param list2 a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ad.VariantContextCompatibilityCheckerAutosomalDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalDominant buildCheckerAD(ImmutableList<Genotype> list1,
			ImmutableList<Genotype> list2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list1, list2, false);
		return new VariantContextCompatibilityCheckerAutosomalDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAR.</p>
	 *
	 * @param gt a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalRecessive buildCheckerAR(Genotype gt) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt), false);
		return new VariantContextCompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAR.</p>
	 *
	 * @param gt1 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @param gt2 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalRecessive buildCheckerAR(Genotype gt1, Genotype gt2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt1), ImmutableList.of(gt2),
				false);
		return new VariantContextCompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAR.</p>
	 *
	 * @param list a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalRecessive buildCheckerAR(ImmutableList<Genotype> list) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list, false);
		return new VariantContextCompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerAR.</p>
	 *
	 * @param list1 a {@link com.google.common.collect.ImmutableList} object.
	 * @param list2 a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.ar.VariantContextCompatibilityCheckerAutosomalRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerAutosomalRecessive buildCheckerAR(ImmutableList<Genotype> list1,
			ImmutableList<Genotype> list2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list1, list2, false);
		return new VariantContextCompatibilityCheckerAutosomalRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXR.</p>
	 *
	 * @param gt a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.VariantContextCompatibilityCheckerXRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXRecessive buildCheckerXR(Genotype gt) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt), true);
		return new VariantContextCompatibilityCheckerXRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXR.</p>
	 *
	 * @param gt1 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @param gt2 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.VariantContextCompatibilityCheckerXRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXRecessive buildCheckerXR(Genotype gt1, Genotype gt2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt1), ImmutableList.of(gt2), true);
		return new VariantContextCompatibilityCheckerXRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXR.</p>
	 *
	 * @param list a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.VariantContextCompatibilityCheckerXRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXRecessive buildCheckerXR(ImmutableList<Genotype> list) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list, true);
		return new VariantContextCompatibilityCheckerXRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXR.</p>
	 *
	 * @param list1 a {@link com.google.common.collect.ImmutableList} object.
	 * @param list2 a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xr.VariantContextCompatibilityCheckerXRecessive} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXRecessive buildCheckerXR(ImmutableList<Genotype> list1,
			ImmutableList<Genotype> list2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list1, list2, true);
		return new VariantContextCompatibilityCheckerXRecessive(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXD.</p>
	 *
	 * @param gt a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.VariantContextCompatibilityCheckerXDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXDominant buildCheckerXD(Genotype gt) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt), true);
		return new VariantContextCompatibilityCheckerXDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXD.</p>
	 *
	 * @param list a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.VariantContextCompatibilityCheckerXDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXDominant buildCheckerXD(ImmutableList<Genotype> list) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list, true);
		return new VariantContextCompatibilityCheckerXDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXD.</p>
	 *
	 * @param list1 a {@link com.google.common.collect.ImmutableList} object.
	 * @param list2 a {@link com.google.common.collect.ImmutableList} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.VariantContextCompatibilityCheckerXDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXDominant buildCheckerXD(ImmutableList<Genotype> list1,
			ImmutableList<Genotype> list2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(list1, list2, true);
		return new VariantContextCompatibilityCheckerXDominant(pedigree, lst);
	}

	/**
	 * <p>buildCheckerXD.</p>
	 *
	 * @param gt1 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @param gt2 a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link de.charite.compbio.jannovar.pedigree.compatibilitychecker.xd.VariantContextCompatibilityCheckerXDominant} object.
	 * @throws InheritanceCompatibilityCheckerException 
	 */
	protected VariantContextCompatibilityCheckerXDominant buildCheckerXD(Genotype gt1, Genotype gt2) throws InheritanceCompatibilityCheckerException {
		List<VariantContext> lst = getInheritanceVariantContextList(ImmutableList.of(gt1), ImmutableList.of(gt2), true);
		return new VariantContextCompatibilityCheckerXDominant(pedigree, lst);
	}

	/**
	 * <p>lst.</p>
	 *
	 * @param gts a {@link de.charite.compbio.jannovar.pedigree.Genotype} object.
	 * @return a {@link com.google.common.collect.ImmutableList} object.
	 */
	protected ImmutableList<Genotype> lst(Genotype... gts) {
		ImmutableList.Builder<Genotype> builder = new ImmutableList.Builder<Genotype>();
		for (int i = 0; i < gts.length; ++i)
			builder.add(gts[i]);
		return builder.build();
	}

}
