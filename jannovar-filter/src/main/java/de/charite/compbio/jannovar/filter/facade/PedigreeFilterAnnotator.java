package de.charite.compbio.jannovar.filter.facade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeQueryDecorator;
import de.charite.compbio.jannovar.pedigree.Person;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Further pedigree-based filters (beyond mode of inheritance).
 *
 * <p>
 * Useful for obtaining reliable de novo variant calls.
 * </p>
 *
 * <p>
 * Note that this filter has to be applied <b>after</b> {@link GenotypeThresholdFilterAnnotator}
 * because the de novo filtration settings would otherwise conflict with the "all affected
 * individuals filtered" variant filter.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PedigreeFilterAnnotator {

	/** Filter configuration */
	private final PedigreeFilterOptions options;

	/** Mapping from individual to {@link Pedigree} */
	private final Pedigree pedigree;

	public PedigreeFilterAnnotator(PedigreeFilterOptions options, Pedigree pedigree) {
		super();
		this.options = options;
		this.pedigree = pedigree;
	}

	/**
	 * Annotate VariantContext with the pedigree-based filters
	 *
	 * @param vc the {@link VariantContext} to annotate
	 * @return copy of <code>vc</code> with applied annotations
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);
		List<Genotype> gts = annotateGenotypes(builder, vc);
		builder.genotypes(gts);
		return builder.make();
	}

	private List<Genotype> annotateGenotypes(VariantContextBuilder builder, VariantContext vc) {
		ArrayList<Genotype> gts = new ArrayList<>();

		// First, collect extra FT entries without "is parent filtered"
		Map<String, List<String>> extraFts = new HashMap<>();
		for (Genotype gt : vc.getGenotypes()) {
			// Enforce registration of samples in extraFts.
			extraFts.put(gt.getSampleName(), new ArrayList<>());

			GenotypeBuilder gtBuilder = new GenotypeBuilder(gt);

			// Get de novo allele, null if not de novo
			final Allele deNovoAllele = getDeNovoAllele(vc, gt.getSampleName());
			if (deNovoAllele != null) {
				final List<String> sampleFts = extraFts.get(gt.getSampleName());

				// Add flag for support in parent above threshold
				if (deNovoGtSharedWithSibling(vc, gt.getSampleName(), deNovoAllele)) {
					sampleFts.add(PedigreeFilterHeaderExtender.FILTER_GT_DE_NOVO_IN_SIBLING);
				}

				// Add flag with support in parents
				final int maxCountInParent =
						getMaxCountInParents(vc, gt.getSampleName(), deNovoAllele);
				if (maxCountInParent > options.getDeNovoMaxParentAd2()) {
					sampleFts.add(PedigreeFilterHeaderExtender.FILTER_GT_DE_NOVO_PARENT_AD2);
				}
			}

		}

		// Then, add "is parent filtered" if enabled.
		if (options.isApplyParentGtFilteredFilters()) {
			for (Genotype gt : vc.getGenotypes()) {
				final int count = filteredParentGtCount(vc, extraFts, gt.getSampleName());
				final List<String> sampleFts = extraFts.get(gt.getSampleName());
				if (count == 1) {
					sampleFts.add(PedigreeFilterHeaderExtender.FILTER_GT_ONE_PARENT_FILTERED);
				} else if (count == 2) {
					sampleFts.add(PedigreeFilterHeaderExtender.FILTER_GT_BOTH_PARENTS_FILTERED);
				}
			}
		}

		// Build final Genotype objects, also incorporating existing genotype filters.
		for (Genotype gt : vc.getGenotypes()) {
			GenotypeBuilder gtBuilder = new GenotypeBuilder(gt);
			if (!extraFts.get(gt.getSampleName()).isEmpty()) {
				if (gt.isFiltered()) {
					extraFts.get(gt.getSampleName()).add(0, gt.getFilters());
				}
				gtBuilder.filters(extraFts.get(gt.getSampleName()));
			}

			final Allele deNovoAllele = getDeNovoAllele(vc, gt.getSampleName());
			gtBuilder.attribute(PedigreeFilterHeaderExtender.FORMAT_GT_DE_NOVO,
					(deNovoAllele != null) ? "Y" : "N");

			if (areParentsRef(vc, gt.getSampleName())) {
				gtBuilder.attribute(PedigreeFilterHeaderExtender.FORMAT_PARENTS_REF, "Y");
			}

			gts.add(gtBuilder.make());
		}

		return gts;
	}

	/**
	 * Query whether parents show reference allele.
	 *
	 * @param vc {@link VariantContext} to check.
	 * @param sampleName Name of the sample to check.
	 * @return {@code true} if the parents are reference homozygous, {@code false} otherwise.
	 */
	private boolean areParentsRef(VariantContext vc, String sampleName) {
		final Person person = this.pedigree.getNameToMember().get(sampleName).getPerson();

		if (person.getFather() == null) {
			return false;
		}
		final Genotype gtFather = vc.getGenotype(person.getFather().getName());
		if (gtFather == null || !gtFather.isHomRef()) {
			return false;
		}

		if (person.getMother() == null) {
			return false;
		}
		final Genotype gtMother = vc.getGenotype(person.getMother().getName());
		if (gtMother == null || !gtMother.isHomRef()) {
			return false;
		}

		return true;
	}

	/**
	 * Return number of filtered genotypes in parents of {@code sampleName}.
	 *
	 * @param vc {@link VariantContext} with the variant.
	 * @param extraFts Additional filters to add for each sample name.
	 * @param sampleName The name of the child to consider.
	 * @return The number of parents with filtered VC.
	 */
	private int filteredParentGtCount(VariantContext vc, Map<String, List<String>> extraFts,
			String sampleName) {
		final Person person = this.pedigree.getNameToMember().get(sampleName).getPerson();

		final int valFather;
		if (person.getFather() != null) {
			final String fatherName = person.getFather().getName();
			final Genotype gtFather = vc.getGenotype(fatherName);
			valFather = (gtFather != null
					&& (gtFather.isFiltered() || !extraFts.get(fatherName).isEmpty())) ? 1 : 0;
		} else {
			valFather = 0;
		}

		final int valMother;
		if (person.getMother() != null) {
			final String motherName = person.getMother().getName();
			final Genotype gtMother = vc.getGenotype(motherName);
			valMother = (gtMother != null
					&& (gtMother.isFiltered() || !extraFts.get(motherName).isEmpty())) ? 1 : 0;
		} else {
			valMother = 0;
		}

		return valFather + valMother;
	}

	// TODO(holtgrewe): right now only works for GATK
	private int getMaxCountInParents(VariantContext vc, String sampleName, Allele deNovoAllele) {
		final int alleleIdx = vc.getAlleleIndex(deNovoAllele);

		final Person person = this.pedigree.getNameToMember().get(sampleName).getPerson();
		final Genotype gtFather = vc.getGenotype(person.getFather().getName());
		final Genotype gtMother = vc.getGenotype(person.getMother().getName());

		final int[] adFather = gtFather.getAD();
		final int[] adMother = gtMother.getAD();

		int result = 0;

		for (int[] ad : new int[][] {adFather, adMother}) {
			if (ad != null && ad.length > alleleIdx) {
				result = Math.max(result, ad[alleleIdx]);
			}
		}

		return result;
	}

	private boolean deNovoGtSharedWithSibling(VariantContext vc, String sampleName,
			Allele deNovoAllele) {
		// This is only called when de novo, thus genotypes of parents exit and fit
		final Person index = this.pedigree.getNameToMember().get(sampleName).getPerson();

		final PedigreeQueryDecorator pedigreeDecorator = new PedigreeQueryDecorator(pedigree);
		final ImmutableMap<Person, ImmutableList<Person>> siblings =
				pedigreeDecorator.buildSiblings();
		for (Person sibling : siblings.get(index)) {
			final Genotype gtSibling = vc.getGenotype(sibling.getName());
			if (gtSibling.countAllele(deNovoAllele) != 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get de novo allele in <code>sampleName</code> or <code>null</code> if there is none
	 *
	 * @param vc {@link VarianContext} to query
	 * @param sampleName Name of the sample
	 * @return De novo allele
	 */
	private Allele getDeNovoAllele(VariantContext vc, String sampleName) {
		final Person person = this.pedigree.getNameToMember().get(sampleName).getPerson();
		if (person.getFather() == null || person.getMother() == null) return null; // cannot make
																					// any judgement
		final Genotype gtPerson = vc.getGenotype(sampleName);
		final Genotype gtFather = vc.getGenotype(person.getFather().getName());
		final Genotype gtMother = vc.getGenotype(person.getMother().getName());
		if (gtPerson.isNoCall() || gtFather.isNoCall() || gtMother.isNoCall()) return null; // cannot
																							// make
																							// any
																							// judgement
		if (!gtPerson.isHet()) return null; // impossible or too unlikely
		// Count non-reference alleles not yet seen in parents. Should be exactly one.
		final Set<Allele> personAlleles = new HashSet<>(gtPerson.getAlleles());
		personAlleles.remove(vc.getReference());
		personAlleles.removeAll(gtFather.getAlleles());
		personAlleles.removeAll(gtMother.getAlleles());
		if (personAlleles.size() == 1) {
			return personAlleles.iterator().next();
		} else {
			return null;
		}
	}

}
