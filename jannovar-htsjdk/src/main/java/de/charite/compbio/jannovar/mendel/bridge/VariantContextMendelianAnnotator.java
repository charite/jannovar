package de.charite.compbio.jannovar.mendel.bridge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.mendel.ChromosomeType;
import de.charite.compbio.jannovar.mendel.GenotypeBuilder;
import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.GenotypeCallsBuilder;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
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

	/** Pedigree to use for checking for Mendelian compatibility */
	private final Pedigree pedigree;
	/** Implementation class to usee */
	private final MendelianInheritanceChecker mendelChecker;

	public VariantContextMendelianAnnotator(Pedigree pedigree) {
		this.pedigree = pedigree;
		this.mendelChecker = new MendelianInheritanceChecker(this.pedigree);
	}

	/**
	 * Annotate {@link VariantContext} with compatibility for Mendelian inheritance
	 * 
	 * @param vc
	 *            {@link VariantContext} to check for compatibility and to annotate
	 * @return Copy of <code>vc</code> with mendelian inheritance compatibility annotation
	 * @throws CannotateAnnotateMendelianInheritance
	 *             on problems with annotating the {@link VariantContext}
	 */
	public VariantContext annotateRecord(VariantContext vc) throws CannotateAnnotateMendelianInheritance {
		return annotateRecords(ImmutableList.of(vc)).get(0);
	}

	/**
	 * Annotate {@link List} of {@link VariantContext} objects
	 * 
	 * @param vcs
	 *            {@link VariantContext} objects to annotate
	 * @return An {@link ImmutableList} of {@link VariantContext} copies of <code>vcs</code>
	 * @throws CannotateAnnotateMendelianInheritance
	 *             on problems with annotating the {@link VariantContext}s
	 */
	public ImmutableList<VariantContext> annotateRecords(List<VariantContext> vcs)
			throws CannotateAnnotateMendelianInheritance {
		// Convert VariantContext to GenotypeCalls objects
		List<GenotypeCalls> gcs = buildGenotypeCalls(vcs);
		ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> checkResult;
		try {
			checkResult = mendelChecker.checkMendelianInheritance(gcs);
		} catch (IncompatiblePedigreeException e) {
			throw new CannotateAnnotateMendelianInheritance(
					"Problem with annotating VariantContext for Mendelian inheritance.", e);
		}

		// Build map of compatible Mendelian inheritance modes for each record
		HashMap<Integer, ArrayList<String>> map = new HashMap<>();
		for (Entry<ModeOfInheritance, ImmutableList<GenotypeCalls>> e : checkResult.entrySet()) {
			final ModeOfInheritance mode = e.getKey();
			final ImmutableList<GenotypeCalls> calls = e.getValue();
			for (GenotypeCalls gc : calls) {
				Integer key = (Integer) gc.getPayload();
				map.putIfAbsent(key, Lists.newArrayList());
				switch (mode) {
				case AUTOSOMAL_DOMINANT:
					map.get(key).add("AD");
					break;
				case AUTOSOMAL_RECESSIVE:
					map.get(key).add("AR");
					break;
				case X_DOMINANT:
					map.get(key).add("XD");
					break;
				case X_RECESSIVE:
					map.get(key).add("XR");
					break;
				default:
					break; // ignore
				}
			}
		}

		// Construct extended VariantContext objects with INHERITED attribute
		ArrayList<VariantContextBuilder> vcBuilders = new ArrayList<>();
		for (int i = 0; i < vcs.size(); ++i)
			vcBuilders.add(new VariantContextBuilder(vcs.get(i)));
		for (Entry<Integer, ArrayList<String>> e : map.entrySet()) {
			VariantContextBuilder vcBuilder = vcBuilders.get(e.getKey());
			vcBuilder.attribute(MendelVCFHeaderExtender.key(), map.values());
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
	 * @throws CannotateAnnotateMendelianInheritance
	 *             on problems with annotating mendelian inheritance
	 */
	public ImmutableMap<ModeOfInheritance, ImmutableList<VariantContext>> computeCompatibleInheritanceModes(
			List<VariantContext> vcs) throws CannotateAnnotateMendelianInheritance {
		// Perform annotation, preceded by building GenotypeCalls list
		List<GenotypeCalls> gcs = buildGenotypeCalls(vcs);
		ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> checkResult;
		try {
			checkResult = mendelChecker.checkMendelianInheritance(gcs);
		} catch (IncompatiblePedigreeException e) {
			throw new CannotateAnnotateMendelianInheritance(
					"Problem with annotating VariantContext for Mendelian inheritance.", e);
		}

		// Build final result
		ImmutableMap.Builder<ModeOfInheritance, ImmutableList<VariantContext>> builder = new ImmutableMap.Builder<>();
		for (Entry<ModeOfInheritance, ImmutableList<GenotypeCalls>> e : checkResult.entrySet()) {
			ImmutableList.Builder<VariantContext> listBuilder = new ImmutableList.Builder<>();
			for (GenotypeCalls gc : e.getValue())
				listBuilder.add((VariantContext) gc.getPayload());
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

		final ImmutableList<String> xNames = ImmutableList.of("x", "X", "23", "chrx", "chrX", "chr23");
		final ImmutableList<String> mtNames = ImmutableList.of("m", "M", "mt", "MT", "chrm", "chrM", "chrmt", "chrMT");

		for (VariantContext vc : vcs) {
			GenotypeCallsBuilder builder = new GenotypeCallsBuilder();
			builder.setPayload(vc);

			if (xNames.contains(vc.getContig()))
				builder.setChromType(ChromosomeType.X_CHROMOSOMAL);
			else if (mtNames.contains(vc.getContig()))
				builder.setChromType(ChromosomeType.MITOCHONDRIAL);
			else
				builder.setChromType(ChromosomeType.AUTOSOMAL);

			for (Genotype gt : vc.getGenotypes()) {
				GenotypeBuilder gtBuilder = new GenotypeBuilder();
				for (Allele allele : gt.getAlleles()) {
					final int aIDX = vc.getAlleleIndex(allele);
					gtBuilder.getAlleleNumbers().add(aIDX);
				}
				builder.getSampleToGenotype().put(gt.getSampleName(), gtBuilder.build());
			}

			result.add(builder.build());
		}

		return result;
	}

}
