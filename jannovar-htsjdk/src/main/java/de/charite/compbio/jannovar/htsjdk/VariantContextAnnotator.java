package de.charite.compbio.jannovar.htsjdk;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationList;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.io.Chromosome;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;

/**
 * Helper class for generating {@link AnnotationList} objects from {@link VariantContext}s.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class VariantContextAnnotator {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(VariantContextAnnotator.class);

	public static class Options {
		/** selection of info fields to write out (defaults to {@link InfoFields#VCF_ANN}) */
		public final InfoFields infoFields;

		/**
		 * Whether or not to trim each annotation list to the first (one with highest putative impact), defaults to
		 * <code>true</code>
		 */
		public final boolean oneAnnotationOnly;

		/** whether or not to escape values in the ANN field (defaults to <code>true</code>) */
		public final boolean escapeAnnField;

		/** whether or not to perform shifting towards the 3' end of the transcript (defaults to <code>true</code>) */
		public final boolean nt3PrimeShifting;

		public Options() {
			infoFields = InfoFields.VCF_ANN;
			oneAnnotationOnly = true;
			escapeAnnField = true;
			nt3PrimeShifting = true;
		}

		public Options(InfoFields infoFields, boolean oneAnnotationOnly, boolean escapeAnnField,
				boolean nt3PrimeShifting) {
			this.infoFields = infoFields;
			this.oneAnnotationOnly = oneAnnotationOnly;
			this.escapeAnnField = escapeAnnField;
			this.nt3PrimeShifting = nt3PrimeShifting;
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
		this.annotator = new VariantAnnotator(refDict, chromosomeMap, new AnnotationBuilderOptions(
				options.nt3PrimeShifting));
	}

	/**
	 * Build a {@link GenomeChange} from a {@link VariantContext} object.
	 *
	 * In the case of exceptions, you can use {@link #buildbuildUnknownRefAnnotationLists} to build an
	 * {@link AnnotationList} with an error message.
	 *
	 * @param vc
	 *            {@link VariantContext} describing the variatn
	 * @param alleleID
	 *            numeric identifier of the allele
	 * @return {@link GenomeChange} corresponding to <ocde>vc</code>, guaranteed to be on {@link Strand#FWD}.
	 * @throws InvalidCoordinatesException
	 *             in the case that the reference in <code>vc</code> is not known in {@link #refDict}.
	 */
	public GenomeChange buildGenomeChange(VariantContext vc, int alleleID) throws InvalidCoordinatesException {
		// Catch the case that vc.getChr() is not in ChromosomeMap.identifier2chromosom. This is the case
		// for the "random" and "alternative locus" contigs etc.
		Integer boxedInt = refDict.contigID.get(vc.getChr());
		if (boxedInt == null)
			throw new InvalidCoordinatesException("Unknown reference " + vc.getChr(),
					AnnotationMessage.ERROR_CHROMOSOME_NOT_FOUND);
		int chr = boxedInt.intValue();

		// Build the GenomeChange object.
		final String ref = vc.getReference().getBaseString();
		final Allele altAllele = vc.getAlternateAllele(alleleID);
		final String alt = altAllele.getBaseString();
		final int pos = vc.getStart();
		return new GenomeChange(new GenomePosition(refDict, Strand.FWD, chr, pos, PositionType.ONE_BASED), ref, alt);
	}

	/**
	 * Put error annotation messages to a {@link VariantContext} into the ANN field in the INFO column.
	 *
	 * Previous values are overwritten.
	 *
	 * @param vc
	 *            {@link VariantContext} to add the error message to
	 * @param messages
	 *            set of messages to write into the {@link VariantContext}
	 */
	public void putErrorAnnotation(VariantContext vc, Set<AnnotationMessage> messages) {
		// TODO(holtgrewe): Do something more elegant way than 15 * "|", needs to be kept in sync with VCFAnnotationData
		final String annotation = "|||||||||||||||" + Joiner.on('&').join(messages);
		vc.getCommonInfo().putAttribute("ANN", annotation, true); // true allows overwriting
	}

	/**
	 * Given a {@link VariantContext}, generate one {@link AnnotationList} for each alternative allele.
	 *
	 * Note that in the case of an exception being thrown, you have to add an error annotation yourself to the
	 * {@link VariantContext} yourself, e.g. by using {@link #putErrorAnnotation}.
	 *
	 * @param vc
	 *            the VCF record to annotate, remains unchanged
	 * @return {@link ImmutableList} of {@link AnnotationList}s, one for each alternative allele, in the order of the
	 *         alternative alleles in <code>vc</code>
	 * @throws InvalidCoordinatesException
	 *             in the case of problems with resolving coordinates internally, namely building the
	 *             {@link GenomeChange} object one one of the returned {@link AnnotationList}s.
	 */
	public ImmutableList<AnnotationList> buildAnnotationList(VariantContext vc) throws InvalidCoordinatesException {
		LOGGER.trace("building annotation lists for {}", new Object[] { vc });

		ImmutableList.Builder<AnnotationList> builder = new ImmutableList.Builder<AnnotationList>();
		for (int alleleID = 0; alleleID < vc.getAlternateAlleles().size(); ++alleleID) {
			GenomeChange change = buildGenomeChange(vc, alleleID);

			// Build AnnotationList object for this allele.
			try {
				final AnnotationList lst = annotator.buildAnnotationList(change);
				builder.add(lst);
				LOGGER.trace("adding annotation list {}", new Object[] { lst });
			} catch (Exception e) {
				final AnnotationList lst = buildErrorAnnotationList(change);
				builder.add(lst);
				LOGGER.trace("adding error annotation list {}", new Object[] { lst });
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
			if (!annos.get(alleleID).isEmpty()) {
				for (Annotation ann : annos.get(alleleID)) {
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
		ArrayList<VariantEffect> effectList = new ArrayList<VariantEffect>();
		ArrayList<String> hgvsList = new ArrayList<String>();

		final int altAlleleCount = vc.getAlternateAlleles().size();
		for (int alleleID = 0; alleleID < altAlleleCount; ++alleleID) {
			if (!annos.get(alleleID).isEmpty()) {
				for (Annotation ann : annos.get(alleleID)) {
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

		FluentIterable<String> effects = FluentIterable.from(effectList).transform(VariantEffect.TO_LEGACY_NAME);
		vc.getCommonInfo().putAttribute("EFFECT", Joiner.on(',').join(effects), true); // true allows overwriting
		vc.getCommonInfo().putAttribute("HGVS", Joiner.on(',').join(hgvsList), true); // true allows overwriting
	}

	/**
	 * @param change
	 *            {@link GenomeChange} to build error annotation for
	 * @return AnnotationList having the message set to {@link AnnotationMessage#ERROR_PROBLEM_DURING_ANNOTATION}.
	 */
	public AnnotationList buildErrorAnnotationList(GenomeChange change) {
		return new AnnotationList(change, ImmutableList.of(new Annotation(ImmutableList
				.of(AnnotationMessage.ERROR_PROBLEM_DURING_ANNOTATION))));
	}

}