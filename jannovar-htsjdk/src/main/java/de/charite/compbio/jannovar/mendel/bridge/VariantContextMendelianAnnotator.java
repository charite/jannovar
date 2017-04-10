package de.charite.compbio.jannovar.mendel.bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.mendel.ChromosomeType;
import de.charite.compbio.jannovar.mendel.GenotypeBuilder;
import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.GenotypeCallsBuilder;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import de.charite.compbio.jannovar.mendel.SubModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Helper class for annotating one {@link VariantContext} or a {@link Collection} thereof for compatibility with
 * Mendelian inheritance
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VariantContextMendelianAnnotator {

	// Known variant-wise filters
	private final static ImmutableSet<String> VAR_FILTERS = ImmutableSet.of("AllAffGtFiltered", "MaxFreqAd",
			"MaxFreqAr", "OffExome");
	private final static ImmutableSet<String> VAR_FILTERS_AD = ImmutableSet.of("MaxFreqAd");
	private final static ImmutableSet<String> VAR_FILTERS_AR = ImmutableSet.of("MaxFreqAr");
	// Known genotype-wise filters
	private final static ImmutableSet<String> GT_FILTERS = ImmutableSet.of("MaxCov", "MinGq");
	private final static ImmutableSet<String> GT_FILTERS_HOM_REF = ImmutableSet.of("MinAafHomRef");
	private final static ImmutableSet<String> GT_FILTERS_HOM_ALT = ImmutableSet.of("MinCovHomAlt", "MinAafHomAlt");
	private final static ImmutableSet<String> GT_FILTERS_HET = ImmutableSet.of("MinCovHet", "MinAafHet", "MaxAafHet");

	/** Pedigree to use for checking for Mendelian compatibility */
	private final Pedigree pedigree;
	/** Implementation class to usee */
	private final MendelianInheritanceChecker mendelChecker;
	/** Whether or not to interpret genotype-wise filters */
	boolean interpretGenotypeFilters;
	/** Whether or not to interpret variant-wise filters */
	boolean interpretVariantFilters;

	public VariantContextMendelianAnnotator(Pedigree pedigree, boolean interpretGenotypeFilters,
			boolean interpretVariantFilters) {
		this.pedigree = pedigree;
		this.mendelChecker = new MendelianInheritanceChecker(this.pedigree);
		this.interpretGenotypeFilters = interpretGenotypeFilters;
		this.interpretVariantFilters = interpretVariantFilters;
	}

	/**
	 * Annotate {@link VariantContext} with compatibility for Mendelian inheritance
	 * 
	 * @param vc
	 *            {@link VariantContext} to check for compatibility and to annotate
	 * @return Copy of <code>vc</code> with mendelian inheritance compatibility annotation
	 * @throws CannotAnnotateMendelianInheritance
	 *             on problems with annotating the {@link VariantContext}
	 */
	public VariantContext annotateRecord(VariantContext vc) throws CannotAnnotateMendelianInheritance {
		return annotateRecords(ImmutableList.of(vc)).get(0);
	}

	/**
	 * Annotate {@link List} of {@link VariantContext} objects
	 * 
	 * If <code>self.interpretVariantFilters</code> then the variant contexts to be considered for compound heterozygous
	 * will be prefiltered to those with appropriately high frequency and not being annotated as synonymous variant.
	 * 
	 * @param vcs
	 *            {@link VariantContext} objects to annotate
	 * @return An {@link ImmutableList} of {@link VariantContext} copies of <code>vcs</code>
	 * @throws CannotAnnotateMendelianInheritance
	 *             on problems with annotating the {@link VariantContext}s
	 */
	public ImmutableList<VariantContext> annotateRecords(List<VariantContext> vcs)
			throws CannotAnnotateMendelianInheritance {
		// TODO: filter for synonymous variant currently broken if annotating with all variants... :(
		final String synonymous = VariantEffect.SYNONYMOUS_VARIANT.getSequenceOntologyTerm();

		// Filter functor for recessive filtration
		Predicate<VariantContext> keepFreqRecessive;
		if (interpretVariantFilters) {
			keepFreqRecessive = vc -> {
				return !isFiltered(vc.getFilters(), VAR_FILTERS, VAR_FILTERS_AR)
						&& !vc.getAttributeAsString("ANN", "").contains(synonymous);
			};
		} else {
			keepFreqRecessive = vc -> true;
		}

		// Create mapping from MOH to genotype calls and pre-filter if configured to do so
		HashMap<SubModeOfInheritance, List<GenotypeCalls>> origCalls = new HashMap<>();
		final List<GenotypeCalls> allCalls = buildGenotypeCalls(vcs);
		final List<GenotypeCalls> recessiveCalls = buildGenotypeCalls(
				vcs.stream().filter(keepFreqRecessive).collect(Collectors.toList()));
		origCalls.put(SubModeOfInheritance.AUTOSOMAL_DOMINANT, allCalls);
		origCalls.put(SubModeOfInheritance.X_DOMINANT, allCalls);
		origCalls.put(SubModeOfInheritance.AUTOSOMAL_RECESSIVE_COMP_HET, recessiveCalls);
		origCalls.put(SubModeOfInheritance.AUTOSOMAL_RECESSIVE_HOM_ALT, allCalls);
		origCalls.put(SubModeOfInheritance.X_RECESSIVE_COMP_HET, recessiveCalls);
		origCalls.put(SubModeOfInheritance.X_RECESSIVE_HOM_ALT, allCalls);

		// Filter to compatible records
		HashMap<SubModeOfInheritance, List<GenotypeCalls>> filteredGenotypeCalls = new HashMap<>();
		try {
			for (Entry<SubModeOfInheritance, List<GenotypeCalls>> e : origCalls.entrySet())
				filteredGenotypeCalls.put(e.getKey(),
						mendelChecker.filterCompatibleRecordsSub(e.getValue(), e.getKey()));
		} catch (IncompatiblePedigreeException e) {
			throw new CannotAnnotateMendelianInheritance(
					"Problem with annotating VariantContext for Mendelian inheritance.", e);
		}

		// Build map of compatible Mendelian inheritance modes for each record
		HashMap<Integer, Set<String>> map = new HashMap<>();
		HashMap<Integer, Set<String>> subMap = new HashMap<>();
		for (Entry<SubModeOfInheritance, List<GenotypeCalls>> e : filteredGenotypeCalls.entrySet()) {
			final SubModeOfInheritance mode = e.getKey();
			final List<GenotypeCalls> calls = e.getValue();
			for (GenotypeCalls gc : calls) {
				Integer key = (Integer) gc.getPayload();
				map.putIfAbsent(key, new TreeSet<String>());
				subMap.putIfAbsent(key, new TreeSet<String>());
				switch (mode) {
				case AUTOSOMAL_DOMINANT:
					map.get(key).add("AD");
					break;

				case AUTOSOMAL_RECESSIVE_COMP_HET:
					subMap.get(key).add(MendelVCFHeaderExtender.AR_COMP_HET);
				case AUTOSOMAL_RECESSIVE_HOM_ALT:
					subMap.get(key).add(MendelVCFHeaderExtender.AR_HOM_ALT);
					map.get(key).add("AR");
					break;

				case X_DOMINANT:
					map.get(key).add("XD");
					break;

				case X_RECESSIVE_COMP_HET:
					subMap.get(key).add(MendelVCFHeaderExtender.XR_COMP_HET);
				case X_RECESSIVE_HOM_ALT:
					subMap.get(key).add(MendelVCFHeaderExtender.XR_HOM_ALT);
					map.get(key).add("XR");
					break;
				default:
					break; // ignore
				}
			}
		}

		// Construct extended VariantContext objects with INHERITED and INHERITANCE_RECESSIVE_DETAIL attributes
		ArrayList<VariantContextBuilder> vcBuilders = new ArrayList<>();
		for (int i = 0; i < vcs.size(); ++i)
			vcBuilders.add(new VariantContextBuilder(vcs.get(i)));
		for (Entry<Integer, Set<String>> e : map.entrySet()) {
			VariantContextBuilder vcBuilder = vcBuilders.get(e.getKey());
			vcBuilder.attribute(MendelVCFHeaderExtender.key(), e.getValue());
			vcBuilder.attribute(MendelVCFHeaderExtender.keySub(), subMap.get(e.getKey()));
		}

		// Build final result list
		ImmutableList.Builder<VariantContext> resultBuilder = new ImmutableList.Builder<>();
		for (int i = 0; i < vcs.size(); ++i)
			resultBuilder.add(vcBuilders.get(i).make());
		return resultBuilder.build();
	}

	/**
	 * Compute compatible modes of inheritance for a list of {@link VariantContext} objects
	 * 
	 * @param vcs
	 *            {@link VariantContext} objects to check for compatibility
	 * @return A {@link Map} from {@link ModeOfInheritance} to the list of {@link VariantContext} in <code>vcs</code>
	 *         that is compatible with each mode
	 * @throws CannotAnnotateMendelianInheritance
	 *             on problems with annotating mendelian inheritance
	 */
	public ImmutableMap<ModeOfInheritance, ImmutableList<VariantContext>> computeCompatibleInheritanceModes(
			List<VariantContext> vcs) throws CannotAnnotateMendelianInheritance {
		// Perform annotation, preceded by building GenotypeCalls list
		List<GenotypeCalls> gcs = buildGenotypeCalls(vcs);
		ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> checkResult;
		try {
			checkResult = mendelChecker.checkMendelianInheritance(gcs);
		} catch (IncompatiblePedigreeException e) {
			throw new CannotAnnotateMendelianInheritance(
					"Problem with annotating VariantContext for Mendelian inheritance.", e);
		}

		// Build final result
		ImmutableMap.Builder<ModeOfInheritance, ImmutableList<VariantContext>> builder = new ImmutableMap.Builder<>();
		for (Entry<ModeOfInheritance, ImmutableList<GenotypeCalls>> e : checkResult.entrySet()) {
			ImmutableList.Builder<VariantContext> listBuilder = new ImmutableList.Builder<>();
			for (GenotypeCalls gc : e.getValue())
				listBuilder.add(vcs.get((Integer) gc.getPayload()));
			builder.put(e.getKey(), listBuilder.build());
		}
		return builder.build();
	}

	/**
	 * Compute compatible modes of inheritance for a list of {@link VariantContext} objects
	 * 
	 * @param vcs
	 *            {@link VariantContext} objects to check for compatibility
	 * @return A {@link Map} from {@link ModeOfInheritance} to the list of {@link VariantContext} in <code>vcs</code>
	 *         that is compatible with each mode
	 * @throws CannotAnnotateMendelianInheritance
	 *             on problems with annotating mendelian inheritance
	 */
	public ImmutableMap<SubModeOfInheritance, ImmutableList<VariantContext>> computeCompatibleInheritanceSubModes(
			List<VariantContext> vcs) throws CannotAnnotateMendelianInheritance {
		// Perform annotation, preceded by building GenotypeCalls list
		List<GenotypeCalls> gcs = buildGenotypeCalls(vcs);
		ImmutableMap<SubModeOfInheritance, ImmutableList<GenotypeCalls>> checkResult;
		try {
			checkResult = mendelChecker.checkMendelianInheritanceSub(gcs);
		} catch (IncompatiblePedigreeException e) {
			throw new CannotAnnotateMendelianInheritance(
					"Problem with annotating VariantContext for Mendelian inheritance.", e);
		}

		// Build final result
		ImmutableMap.Builder<SubModeOfInheritance, ImmutableList<VariantContext>> builder = new ImmutableMap.Builder<>();
		for (Entry<SubModeOfInheritance, ImmutableList<GenotypeCalls>> e : checkResult.entrySet()) {
			ImmutableList.Builder<VariantContext> listBuilder = new ImmutableList.Builder<>();
			for (GenotypeCalls gc : e.getValue())
				listBuilder.add(vcs.get((Integer) gc.getPayload()));
			builder.put(e.getKey(), listBuilder.build());
		}
		return builder.build();
	}

	/**
	 * Convert a {@link List} of {@link VariantContext} objects into a list of {@link GenotypeCalls} objects
	 * 
	 * @param vcs
	 *            input {@link Collection} of {@link VariantContext} objects
	 * @return {@link List} of corresponding {@link GenotypeCalls} objects
	 */
	private List<GenotypeCalls> buildGenotypeCalls(Collection<VariantContext> vcs) {
		ArrayList<GenotypeCalls> result = new ArrayList<>();

		// Somewhat hacky but working inclusion of X and mitochondrial genomes
		final ImmutableList<String> xNames = ImmutableList.of("x", "X", "23", "chrx", "chrX", "chr23");
		final ImmutableList<String> mtNames = ImmutableList.of("m", "M", "mt", "MT", "chrm", "chrM", "chrmt", "chrMT");

		int i = 0;
		for (VariantContext vc : vcs) {
			GenotypeCallsBuilder builder = new GenotypeCallsBuilder();
			builder.setPayload(i++);

			if (xNames.contains(vc.getContig()))
				builder.setChromType(ChromosomeType.X_CHROMOSOMAL);
			else if (mtNames.contains(vc.getContig()))
				builder.setChromType(ChromosomeType.MITOCHONDRIAL);
			else
				builder.setChromType(ChromosomeType.AUTOSOMAL);

			for (Genotype gt : vc.getGenotypes()) {
				List<String> gtFilters = new ArrayList<String>();
				if (gt.getFilters() != null)
					gtFilters.addAll(Arrays.asList(gt.getFilters().split(";")));

				boolean isFiltered = false;
				if (gt.isHet()) {
					if (interpretGenotypeFilters && isFiltered(gtFilters, GT_FILTERS, GT_FILTERS_HET))
						isFiltered = true;
				} else if (gt.isHomRef()) {
					if (interpretGenotypeFilters && isFiltered(gtFilters, GT_FILTERS, GT_FILTERS_HOM_REF))
						isFiltered = true;
				} else { // hom-alt or two overlapping hets, treated the same for filtration
					if (interpretGenotypeFilters && isFiltered(gtFilters, GT_FILTERS, GT_FILTERS_HOM_ALT))
						isFiltered = true;
				}

				GenotypeBuilder gtBuilder = new GenotypeBuilder();
				for (Allele allele : gt.getAlleles()) {
					if (isFiltered) {
						gtBuilder.getAlleleNumbers().add(de.charite.compbio.jannovar.mendel.Genotype.NO_CALL);
					} else {
						final int aIDX = vc.getAlleleIndex(allele);
						gtBuilder.getAlleleNumbers().add(aIDX);
					}
				}
				builder.getSampleToGenotype().put(gt.getSampleName(), gtBuilder.build());
			}

			result.add(builder.build());
		}

		return result;
	}

	/**
	 * Helper function for filtered variants/genotypes
	 * 
	 * @return whether or not variant is filtered based on filters
	 */
	private boolean isFiltered(Collection<String> vcFilters, Collection<String> filtersA, Collection<String> filtersB) {
		Set<String> filterIntersection = new HashSet<>(filtersA);
		filterIntersection.addAll(filtersB);
		filterIntersection.retainAll(vcFilters);
		return !filterIntersection.isEmpty();
	}

	/**
	 * Helper function for filtered variants/genotypes
	 * 
	 * @return whether or not variant is filtered based on filters
	 */
	private boolean isFiltered(Collection<String> vcFilters, Collection<String> filtersA) {
		return isFiltered(vcFilters, filtersA, ImmutableList.<String> of());
	}

}
