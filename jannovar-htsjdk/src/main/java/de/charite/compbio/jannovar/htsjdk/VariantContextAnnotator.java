package de.charite.compbio.jannovar.htsjdk;

import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationList;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.VariantType;
import de.charite.compbio.jannovar.io.Chromosome;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.PositionType;

/**
 * Helper class for generating {@link AnnotationList} objects from {@link VariantContext}s.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class VariantContextAnnotator {

	public static class Options {
		/** selection of info fields to write out (defaults to {@link InfoFields#VCF_ANN}) */
		public final InfoFields infoFields;

		/**
		 * Whether or not to trim each annotation list to the first (one with highest putative impact), defaults to
		 * <code>true</code>
		 */
		public final boolean oneAnnotationOnly;

		public Options() {
			infoFields = InfoFields.VCF_ANN;
			oneAnnotationOnly = true;
		}

		public Options(InfoFields infoFields, boolean oneAnnotationOnly) {
			this.infoFields = infoFields;
			this.oneAnnotationOnly = oneAnnotationOnly;
		}
	}

	/** the {@link ReferenceDictionary} to use */
	public final ReferenceDictionary refDict;
	/** {@link Chromsome} map with the {@link TranscriptModel}s, probably from {@link JannovarData} */
	public final ImmutableMap<Integer, Chromosome> chromosomeMap;
	/** configuration */
	public final Options options;

	/** implementation of the actual variant annotation */
	private final VariantAnnotator annotator;

	/**
	 * Construct annotator with default options.
	 */
	public VariantContextAnnotator(ReferenceDictionary refDict, ImmutableMap<Integer, Chromosome> chromosomeMap) {
		this(refDict, chromosomeMap, new Options());
	}

	/**
	 * Construct Annotator.
	 *
	 * @param refDict
	 *            {@link ReferenceDictionary} to use, probably from {@link JannovarData}
	 * @param chromosomeMap
	 *            {@link Chromosome} map to use, probably from {@link JannovarData}
	 * @param options
	 *            configuration of the Annotator, for {@link #applyAnnotations}
	 */
	public VariantContextAnnotator(ReferenceDictionary refDict, ImmutableMap<Integer, Chromosome> chromosomeMap,
			Options options) {
		this.refDict = refDict;
		this.chromosomeMap = chromosomeMap;
		this.options = options;
		this.annotator = new VariantAnnotator(refDict, chromosomeMap);
	}

	/**
	 * Given a {@link VariantContext}, generate one {@link AnnotationList} for each alternative allele.
	 *
	 * @param vc
	 *            the VCF record to annotate, remains unchanged
	 * @return {@link ImmutableList} of {@link AnnotationList}s, one for each alternative allele, in the order of the
	 *         alternative alleles in <code>vc</code>
	 */
	public ImmutableList<AnnotationList> buildAnnotationList(VariantContext vc) {
		// Catch the case that vc.getChr() is not in ChromosomeMap.identifier2chromosom. This is the case
		// for the "random" and "alternative locus" contigs etc.
		Integer boxedInt = refDict.contigID.get(vc.getChr());
		if (boxedInt == null)
			return buildUnknownRefAnnotationLists(vc);
		int chr = boxedInt.intValue();

		ImmutableList.Builder<AnnotationList> builder = new ImmutableList.Builder<AnnotationList>();
		for (int alleleID = 0; alleleID < vc.getAlternateAlleles().size(); ++alleleID) {
			// Get shortcuts to REF, ALT, and POS and build a GenomeChange with stripped common prefixes.
			final String ref = vc.getReference().getBaseString();
			final String alt = vc.getAlternateAllele(alleleID).getBaseString();
			final int pos = vc.getStart();
			final GenomeChange change = new GenomeChange(new GenomePosition(refDict, '+', chr, pos,
					PositionType.ONE_BASED), ref, alt);

			// Build AnnotationList object for this allele.
			if (alt.contains("[") || alt.contains("]") || alt.equals(".")) {
				builder.add(AnnotationList.EMPTY);
			} else {
				try {
					builder.add(annotator.buildAnnotationList(change));
				} catch (Exception e) {
					builder.add(buildErrorAnnotationList(vc));
				}
			}
		}

		return builder.build();
	}

	/**
	 * Write annotations from <code>annos</code> to <code>vc</code>
	 *
	 * @param vc
	 *            {@link VariantContext} to write the annotations to (to INFO column)
	 * @param annos
	 *            annotations to apply (one for each alternative allele in <code>vc</code>)
	 * @return modified <code>vc</code>
	 */
	public VariantContext applyAnnotations(VariantContext vc, List<AnnotationList> annos) {
		if (options.infoFields == InfoFields.VCF_ANN || options.infoFields == InfoFields.BOTH)
			applyStandardAnnotations(vc, annos);
		if (options.infoFields == InfoFields.EFFECT_HGVS || options.infoFields == InfoFields.BOTH)
			applyOldJannovarAnnotations(vc, annos);
		return vc;
	}

	private void applyStandardAnnotations(VariantContext vc, List<AnnotationList> annos) {
		ArrayList<String> annotations = new ArrayList<String>();
		for (int alleleID = 0; alleleID < vc.getAlternateAlleles().size(); ++alleleID) {
			if (!annos.get(alleleID).entries.isEmpty()) {
				for (Annotation ann : annos.get(alleleID).entries) {
					final String alt = vc.getAlternateAllele(alleleID).getBaseString();
					annotations.add(ann.toVCFAnnoString(alt));
					if (options.oneAnnotationOnly)
						break;
				}
			}
		}
		vc.getCommonInfo().putAttribute("ANN", Joiner.on(',').join(annotations), true); // true allows overwriting
	}

	private void applyOldJannovarAnnotations(VariantContext vc, List<AnnotationList> annos) {
		ArrayList<VariantType> effectList = new ArrayList<VariantType>();
		ArrayList<String> hgvsList = new ArrayList<String>();

		final int altAlleleCount = vc.getAlternateAlleles().size();
		for (int alleleID = 0; alleleID < altAlleleCount; ++alleleID) {
			if (!annos.get(alleleID).entries.isEmpty()) {
				for (Annotation ann : annos.get(alleleID).entries) {
					final String alt = vc.getAlternateAllele(alleleID).getBaseString();
					effectList.add(ann.getMostPathogenicVarType());
					if (altAlleleCount == 1)
						hgvsList.add(ann.getSymbolAndAnnotation());
					else
						hgvsList.add(Joiner.on("").join("alt", alt, ":", ann.getSymbolAndAnnotation()));

					if (options.oneAnnotationOnly)
						break;
				}
			}
		}

		vc.getCommonInfo().putAttribute("EFFECT", Joiner.on(',').join(effectList), true); // true allows overwriting
		vc.getCommonInfo().putAttribute("HGVS", Joiner.on(',').join(hgvsList), true); // true allows overwriting
	}

	private AnnotationList buildErrorAnnotationList(VariantContext vc) {
		return new AnnotationList(ImmutableList.of(new Annotation(ImmutableList
				.of(AnnotationMessage.ERROR_PROBLEM_DURING_ANNOTATION))));
	}

	public ImmutableList<AnnotationList> buildUnknownRefAnnotationLists(VariantContext vc) {
		return ImmutableList.of(new AnnotationList(ImmutableList.of(new Annotation(ImmutableList
				.of(AnnotationMessage.ERROR_CHROMOSOME_NOT_FOUND)))));
	}

}