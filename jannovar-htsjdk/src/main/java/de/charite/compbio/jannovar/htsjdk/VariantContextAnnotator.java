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
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Helper class for generating {@link VariantAnnotations} objects from {@link VariantContext}s.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class VariantContextAnnotator {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(VariantContextAnnotator.class);

	public static class Options {
		/** selection of info fields to write out (defaults to {@link InfoFields#VCF_ANN}) */
		private final InfoFields infoFields;

		/**
		 * Whether or not to trim each annotation list to the first (one with highest putative impact), defaults to
		 * <code>true</code>
		 */
		private final boolean oneAnnotationOnly;

		/** whether or not to escape values in the ANN field (defaults to <code>true</code>) */
		private final boolean escapeAnnField;

		/** whether or not to perform shifting towards the 3' end of the transcript (defaults to <code>true</code>) */
		private final boolean nt3PrimeShifting;

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

		public InfoFields getInfoFields() {
			return infoFields;
		}

		public boolean isOneAnnotationOnly() {
			return oneAnnotationOnly;
		}

		public boolean isEscapeAnnField() {
			return escapeAnnField;
		}

		public boolean isNt3PrimeShifting() {
			return nt3PrimeShifting;
		}

	}

	/** the {@link ReferenceDictionary} to use */
	private final ReferenceDictionary refDict;
	/** {@link Chromosome} map with the {@link TranscriptModel}s, probably from {@link JannovarData} */
	private final ImmutableMap<Integer, Chromosome> chromosomeMap;
	/** configuration */
	private final Options options;

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

	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	public ImmutableMap<Integer, Chromosome> getChromosomeMap() {
		return chromosomeMap;
	}

	public Options getOptions() {
		return options;
	}

	public VariantAnnotator getAnnotator() {
		return annotator;
	}

	/**
	 * Build a {@link GenomeVariant} from a {@link VariantContext} object.
	 *
	 * In the case of exceptions, you can use {@link #buildErrorAnnotations} to build an {@link VariantAnnotations} with
	 * an error message.
	 *
	 * @param vc
	 *            {@link VariantContext} describing the variant
	 * @param alleleID
	 *            numeric identifier of the allele
	 * @return {@link GenomeVariant} corresponding to <ocde>vc</code>, guaranteed to be on {@link Strand#FWD}.
	 * @throws InvalidCoordinatesException
	 *             in the case that the reference in <code>vc</code> is not known in {@link #refDict}.
	 */
	public GenomeVariant buildGenomeVariant(VariantContext vc, int alleleID) throws InvalidCoordinatesException {
		// Catch the case that vc.getChr() is not in ChromosomeMap.identifier2chromosom. This is the case
		// for the "random" and "alternative locus" contigs etc.
		Integer boxedInt = refDict.getContigNameToID().get(vc.getContig());
		if (boxedInt == null)
			throw new InvalidCoordinatesException("Unknown reference " + vc.getContig(),
					AnnotationMessage.ERROR_CHROMOSOME_NOT_FOUND);
		int chr = boxedInt.intValue();

		// Build the GenomeChange object.
		final String ref = vc.getReference().getBaseString();
		final Allele altAllele = vc.getAlternateAllele(alleleID);
		final String alt = altAllele.getBaseString();
		final int pos = vc.getStart();
		return new GenomeVariant(new GenomePosition(refDict, Strand.FWD, chr, pos, PositionType.ONE_BASED), ref, alt);
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
	 * Given a {@link VariantContext}, generate one {@link VariantAnnotations} for each alternative allele.
	 *
	 * Note that in the case of an exception being thrown, you have to add an error annotation yourself to the
	 * {@link VariantContext} yourself, e.g. by using {@link #putErrorAnnotation}.
	 *
	 * @param vc
	 *            the VCF record to annotate, remains unchanged
	 * @return {@link ImmutableList} of {@link VariantAnnotations}s, one for each alternative allele, in the order of
	 *         the alternative alleles in <code>vc</code>
	 * @throws InvalidCoordinatesException
	 *             in the case of problems with resolving coordinates internally, namely building the
	 *             {@link GenomeVariant} object one one of the returned {@link VariantAnnotations}s.
	 */
	public ImmutableList<VariantAnnotations> buildAnnotations(VariantContext vc) throws InvalidCoordinatesException {
		LOGGER.trace("building annotation lists for {}", new Object[] { vc });

		ImmutableList.Builder<VariantAnnotations> builder = new ImmutableList.Builder<VariantAnnotations>();
		for (int alleleID = 0; alleleID < vc.getAlternateAlleles().size(); ++alleleID) {
			GenomeVariant change = buildGenomeVariant(vc, alleleID);

			// Build AnnotationList object for this allele.
			try {
				final VariantAnnotations lst = annotator.buildAnnotations(change);
				builder.add(lst);
				LOGGER.trace("adding annotation list {}", new Object[] { lst });
			} catch (Exception e) {
				final VariantAnnotations lst = buildErrorAnnotations(change);
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
	public VariantContext applyAnnotations(VariantContext vc, List<VariantAnnotations> annos) {
		if (options.infoFields == InfoFields.VCF_ANN || options.infoFields == InfoFields.BOTH)
			applyStandardAnnotations(vc, annos);
		if (options.infoFields == InfoFields.EFFECT_HGVS || options.infoFields == InfoFields.BOTH)
			applyOldJannovarAnnotations(vc, annos);
		return vc;
	}

	private void applyStandardAnnotations(VariantContext vc, List<VariantAnnotations> annos) {
		ArrayList<String> annotations = new ArrayList<String>();
		for (int alleleID = 0; alleleID < vc.getAlternateAlleles().size(); ++alleleID) {
			if (!annos.get(alleleID).getAnnotations().isEmpty()) {
				for (Annotation ann : annos.get(alleleID).getAnnotations()) {
					final String alt = vc.getAlternateAllele(alleleID).getBaseString();
					annotations.add(ann.toVCFAnnoString(alt));
					if (options.oneAnnotationOnly)
						break;
				}
			}
		}
		vc.getCommonInfo().putAttribute("ANN", Joiner.on(',').join(annotations), true); // true allows overwriting
	}

	private void applyOldJannovarAnnotations(VariantContext vc, List<VariantAnnotations> annos) {
		ArrayList<VariantEffect> effectList = new ArrayList<VariantEffect>();
		ArrayList<String> hgvsList = new ArrayList<String>();

		final int altAlleleCount = vc.getAlternateAlleles().size();
		for (int alleleID = 0; alleleID < altAlleleCount; ++alleleID) {
			if (!annos.get(alleleID).getAnnotations().isEmpty()) {
				for (Annotation ann : annos.get(alleleID).getAnnotations()) {
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
	 *            {@link GenomeVariant} to build error annotation for
	 * @return VariantAnnotations having the message set to {@link AnnotationMessage#ERROR_PROBLEM_DURING_ANNOTATION}.
	 */
	public VariantAnnotations buildErrorAnnotations(GenomeVariant change) {
		return new VariantAnnotations(change, ImmutableList.of(new Annotation(ImmutableList
				.of(AnnotationMessage.ERROR_PROBLEM_DURING_ANNOTATION))));
	}

}