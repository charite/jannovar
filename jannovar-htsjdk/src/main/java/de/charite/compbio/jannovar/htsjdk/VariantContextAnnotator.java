package de.charite.compbio.jannovar.htsjdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Helper class for generating {@link VariantAnnotations} objects from {@link VariantContext}s.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class VariantContextAnnotator {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(VariantContextAnnotator.class);

	/**
	 * Options class for {@link VariantContextAnnotator}
	 * 
	 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
	 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
	 *
	 */
	public static class Options {
		/**
		 * Whether or not to trim each annotation list to the first (one with highest putative impact), defaults to
		 * <code>true</code>
		 */
		private final boolean oneAnnotationOnly;
		
		/**
		 * HGVS protein output in three or one letter
		 */
		private final AminoAcidCode aminoAcidCode;

		/** whether or not to escape values in the ANN field (defaults to <code>true</code>) */
		private final boolean escapeAnnField;

		/** whether or not to perform shifting towards the 3' end of the transcript (defaults to <code>true</code>) */
		private final boolean nt3PrimeShifting;

		/** Whether or not off-target filter is enabled */
		private boolean offTargetFilterEnabled;

		/** Whether or not UTR counts as off-target */
		private boolean offTargetFilterUtrIsOffTarget;

		/** Whether or not non-consensus splice region counts as off-target */
		private boolean offTargetFilterIntronicSpliceIsOffTarget;

		/**
		 * Constructor
		 */
		public Options() {
			oneAnnotationOnly = true;
			aminoAcidCode = AminoAcidCode.ONE_LETTER;
			escapeAnnField = true;
			nt3PrimeShifting = true;
			offTargetFilterEnabled = false;
			offTargetFilterUtrIsOffTarget = false;
			offTargetFilterIntronicSpliceIsOffTarget = false;
		}

		/**
		 * 
		 * constructor using fields
		 * 
		 * @param oneAnnotationOnly
		 *            Whether or not to trim each annotation list to the first (one with
		 *            highest putative impact), defaults to <code>true</code>
		 * @param code
		 *            HGVS protein output in three or one letter
		 * @param escapeAnnField
		 *            whether or not to escape values in the ANN field (defaults to
		 *            <code>true</code>)
		 * @param nt3PrimeShifting
		 *            whether or not to perform shifting towards the 3' end of the
		 *            transcript (defaults to <code>true</code>)
		 * @param offTargetFilterEnabled
		 *            whether or not off target filter application is abled
		 * @param offTargetFilterUtrIsOffTarget
		 *            whether or not to count UTR as off-target
		 * @param offTargetFilterIntronicSpliceIsOffTarget
		 *            whether or not to to count non-consensus intronic splicing as
		 *            off-target
		 */
		public Options(boolean oneAnnotationOnly, AminoAcidCode code, boolean escapeAnnField, boolean nt3PrimeShifting,
				boolean offTargetFilterEnabled, boolean offTargetFilterUtrIsOffTarget,
				boolean offTargetFilterIntronicSpliceIsOffTarget) {
			this.oneAnnotationOnly = oneAnnotationOnly;
			this.aminoAcidCode = code;
			this.escapeAnnField = escapeAnnField;
			this.nt3PrimeShifting = nt3PrimeShifting;
			this.offTargetFilterEnabled = offTargetFilterEnabled;
			this.offTargetFilterUtrIsOffTarget = offTargetFilterUtrIsOffTarget;
			this.offTargetFilterIntronicSpliceIsOffTarget = offTargetFilterIntronicSpliceIsOffTarget;
		}

		/**
		 * @return if annotation list is trimmed to first.
		 */
		public boolean isOneAnnotationOnly() {
			return oneAnnotationOnly;
		}

		/**
		 * @return Escape values in ANN field
		 */
		public boolean isEscapeAnnField() {
			return escapeAnnField;
		}

		/**
		 * @return perform shifting towards 3' end of the transcript
		 */
		public boolean isNt3PrimeShifting() {
			return nt3PrimeShifting;
		}

		public boolean isOffTargetFilterEnabled() {
			return offTargetFilterEnabled;
		}

		public boolean isOffTargetFilterUtrIsOffTarget() {
			return offTargetFilterUtrIsOffTarget;
		}

		public boolean isOffTargetFilterIntronicSpliceIsOffTarget() {
			return offTargetFilterIntronicSpliceIsOffTarget;
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
	 * 
	 * @param refDict
	 *            Referencedictionary
	 * @param chromosomeMap
	 *            the chomosomal map
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
		this.annotator = new VariantAnnotator(refDict, chromosomeMap,
				new AnnotationBuilderOptions(options.nt3PrimeShifting, false));
	}

	/**
	 * @return The refDict
	 */
	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	/**
	 * @return the chromosomal map
	 */
	public ImmutableMap<Integer, Chromosome> getChromosomeMap() {
		return chromosomeMap;
	}

	/**
	 * @return get the options of the VCAnnotator
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * @return get the annotator
	 */
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
	 * @return {@link GenomeVariant} corresponding to <code>vc</code>, guaranteed to be on {@link Strand#FWD}.
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

		// If a VC builder is used before the attributes can be unmodifiable.
		Map<String, Object> attributes = new HashMap<>(vc.getAttributes());
		attributes.put("ANN", annotation);
		vc.getCommonInfo().setAttributes(attributes);
	}

	/**
	 * Annotate variant <code>vc</code> and return annoated variant
	 * 
	 * @param vc
	 *            {@link VariantContext} to annotate
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		try {
			vc = applyAnnotations(vc, buildAnnotations(vc));
		} catch (InvalidCoordinatesException e) {
			putErrorAnnotation(vc, ImmutableSet.of(e.getAnnotationMessage()));
		}
		vc.getCommonInfo().removeAttribute(""); // remove leading/trailing comma
		return vc;
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
	 * Write annotations from <code>annos</code> to <code>vc</code> l
	 * 
	 * @param vc
	 *            {@link VariantContext} to write the annotations to (to INFO column)
	 * @param annos
	 *            annotations to apply (one for each alternative allele in <code>vc</code>)
	 * @return modified <code>vc</code>
	 */
	public VariantContext applyAnnotations(VariantContext vc, List<VariantAnnotations> annos) {
		// Whether or not variant is off-target in all annotations
		boolean offTargetInAll = true;

		ArrayList<String> annotations = new ArrayList<String>();
		for (int alleleID = 0; alleleID < vc.getAlternateAlleles().size(); ++alleleID) {
			if (!annos.get(alleleID).getAnnotations().isEmpty()) {
				for (Annotation ann : annos.get(alleleID).getAnnotations()) {
					boolean offTargetInThis = ann.getEffects().stream()
							.allMatch(e -> e.isOffExome(options.offTargetFilterUtrIsOffTarget,
									options.offTargetFilterIntronicSpliceIsOffTarget));
					offTargetInAll = offTargetInAll && offTargetInThis;

					if (!options.oneAnnotationOnly || annotations.isEmpty()) {
						final String alt = vc.getAlternateAllele(alleleID).getBaseString();
						annotations.add(ann.toVCFAnnoString(alt, options.escapeAnnField, options.aminoAcidCode));
					}
				}
			}
		}

		if (options.isOffTargetFilterEnabled() && (offTargetInAll && !annotations.isEmpty())) {
			Set<String> filters = new HashSet<>(vc.getFilters());
			filters.add(VariantEffectHeaderExtender.FILTER_EFFECT_OFF_EXOME);
			vc = new VariantContextBuilder(vc).filters(filters).make();
		}

		// If a VC builder is used before the attributes can be unmodifiable.
		Map<String, Object> attributes = new HashMap<>(vc.getAttributes());
		if (!annotations.isEmpty())
			attributes.put("ANN", Joiner.on(',').join(annotations));
		vc.getCommonInfo().setAttributes(attributes);

		return vc;
	}

	/**
	 * @param change
	 *            {@link GenomeVariant} to build error annotation for
	 * @return VariantAnnotations having the message set to {@link AnnotationMessage#ERROR_PROBLEM_DURING_ANNOTATION}.
	 */
	public VariantAnnotations buildErrorAnnotations(GenomeVariant change) {
		return new VariantAnnotations(change,
				ImmutableList.of(new Annotation(ImmutableList.of(AnnotationMessage.ERROR_PROBLEM_DURING_ANNOTATION))));
	}

}