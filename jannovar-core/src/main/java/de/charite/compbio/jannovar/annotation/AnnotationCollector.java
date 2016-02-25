package de.charite.compbio.jannovar.annotation;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.reference.GenomeVariant;

/**
 * This class collects all the information about a variant and its annotations and calculates the final annotations for
 * a given variant. The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} objects each use an instance of
 * this class to assemble a list of {@link Annotation} objects for each variant. Each variant should receive at least
 * one {@link Annotation}, but variants that affect multiple transcripts will have multiple annotations.
 *
 * This class creates one {@link de.charite.compbio.jannovar.annotation.VariantAnnotations AnnotationList} object for
 * each variant (with one or more {@link Annotation} objects), that can return both an ArrayList of all annotations, a
 * list of all annotations of the highest priority level for the variant, and a single representative Annotation.
 *
 * The default preference for annotations is thus
 *
 * <OL>
 * <LI><B>exonic</B>: variant overlaps a coding exon (does not include 5' or 3' UTR, and also does not include
 * synonymous).
 * <LI><B>splicing</B>: variant is within 2-bp of a splicing junction (same precedence as exonic).
 * <LI><B>ncRNA</B>: variant overlaps a transcript without coding annotation in the gene definition
 * <LI><B>UTR5</B>: variant overlaps a 5' untranslated region
 * <LI><B>UTR3</B>: variant overlaps a 3' untranslated region
 * <LI><B>synonymous</B> synonymous substitution
 * <LI><B>intronic</B>: variant overlaps an intron
 * <LI><B>upstream</B>: variant overlaps 1-kb region upstream of transcription start site
 * <LI><B>downstream</B>: variant overlaps 1-kb region downtream of transcription end site (use -neargene to change
 * this)
 * <LI><B>intergenic</B>: variant is in intergenic region
 * </OL>
 *
 * Note that the class of <B>exonic</B> and <B>splicing</B> mutations as defined here comprises the class of
 * "obvious candidates" for pathogenic mutations, i.e., NS/SS/I, nonsynonymous, splice site, indel.
 *
 * One object of this class is created for each variant we want to annotate. The
 * {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class goes through a list of genes in the vicinity of
 * the variant and adds one {@link Annotation} object for each gene. These are essentially candidates for the actual
 * correct annotation of the variant, but we can only decide what the correct annotation is once we have seen enough
 * candidates. Therefore, once we have gone through the candidates, this class decides what the best annotation is and
 * returns the corresponding {@link Annotation} object (in some cases, this class may modify the {@link Annotation}
 * object before returning it).
 *
 * For each class of Variant, there is a function that returns a single
 * {@link de.charite.compbio.jannovar.annotation.Annotation Annotation} object. These functions are called
 * summarizeABC(), where ABC is Intronic, Exonic, etc., representing the precedence classes.
 *
 * Used for the implementation of VariantAnnotator.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 */
// TODO(holtgrem): expose the hasNcRna etc. fields?
@Deprecated
class AnnotationCollector {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationCollector.class);

	/** List of all {@link Annotation} objects found for exonic variation. */
	private List<Annotation> annotations;

	/**
	 * Set of all gene symbols used for the current annotation (usually one, but if the size of this set is greater than
	 * one, then there qare annotations to multiple genes and we will need to use special treatment).
	 */
	private Set<String> geneSymbols;

	/** Flag to state that we have at least one exonic variant. */
	private boolean hasExonic;
	/** Flag to state we have at least one splicing variant */
	private boolean hasSplicing;
	/** Flag to state that we have at least one noncoding RNA variant. */
	@SuppressWarnings("unused")
	private boolean hasNcRna;
	/** Flag to state that we have at least one UTR5 variant. */
	@SuppressWarnings("unused")
	private boolean hasUTR5;
	/** Flag to state that we have at least one UTR3 variant. */
	@SuppressWarnings("unused")
	private boolean hasUTR3;
	/** Flag to state that we have at least one nonsynonymous exonic variant. */
	@SuppressWarnings("unused")
	private boolean hasSynonymous;
	/** Flag to state that we have at least one intronic variant. */
	@SuppressWarnings("unused")
	private boolean hasIntronic;
	/** Flag to state that we have at least one noncoding RNA intronic variant. */
	@SuppressWarnings("unused")
	private boolean hasNcrnaIntronic;
	/** Flag to state that we have at least one upstream variant. */
	@SuppressWarnings("unused")
	private boolean hasUpstream;
	/** Flag to state that we have at least one downstream variant. */
	@SuppressWarnings("unused")
	private boolean hasDownstream;
	/** Flag to state that we have at least one intergenic variant. */
	@SuppressWarnings("unused")
	private boolean hasIntergenic;
	/** Flag to state that we have at least one error annotation. */
	@SuppressWarnings("unused")
	private boolean hasError;
	/** Flag to state we have a structural variation */
	@SuppressWarnings("unused")
	private boolean hasStructural;
	/**
	 * True if we have at least one annotation for the classes ncRNA_EXONIC SPLICING, UTR5, UTR3, EXONIC, INTRONIC
	 */
	private boolean hasGenicMutation;

	/** The current number of annotations for the variant being annotated */
	private int annotationCount;

	public AnnotationCollector() {
		annotations = new ArrayList<>();
		geneSymbols = new HashSet<>();
	}

	/**
	 * The constructor initializes an ArrayList of {@link Annotation} objects as well as a HashSet of Gene symbols
	 * (Strings).
	 *
	 * @param initialCapacity The initial capacity of the {@link AnnotationCollector}.
	 */
	public AnnotationCollector(int initialCapacity) {
		annotations = new ArrayList<>(initialCapacity);
		geneSymbols = new HashSet<>(initialCapacity);
	}

	/**
	 * @return The number of {@link Annotation} objects for the current variant.
	 */
	public int getAnnotationCount() {
		return annotationCount;
	}

	/**
	 * Note that this function is used by {@link Chromosome} during the construction of an {@link VariantAnnotations}
	 * for a given variant.
	 *
	 * @return true if there are currently no annotations.
	 */
	public boolean isEmpty() {
		return annotations.isEmpty();
	}

    /**
     * After the {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} object has added annotations for all of
     * the transcripts that intersect with the current variant (or a DOWNSTREAM, UPSTREAM, or INTERGENIC annotation if
     * the variant does not intersect with any transcript), it calls this function to return the list of annotations in
     * form of an {@link de.charite.compbio.jannovar.annotation.VariantAnnotations AnnotationList} object.
     * <P>
     * The strategy is to return all variants that affect coding exons (and only these) if such variants exist, as they
     * are the best candidates. Otherwise, return all variants that affect other exonic sequences (UTRs, ncRNA).
     * Otherwise, return UPSTREAM and DOWNSTREAM annotations if they exist. Otherwise, return an intergenic Annotation.
     *
     * @return returns the {@link Annotation}s collected
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

	/**
	 * @return true if there is a nonsynonymous, splice site, or insertion/deletion variant
	 */
	public boolean isNS_SS_I() {
		return hasExonic || hasSplicing;
	}

	/**
	 * @return True if we have at least one annotation for the classes ncRNA_EXONIC SPLICING, UTR5, UTR3, EXONIC,
	 *         INTRONIC
	 */
	public boolean hasGenic() {
		return hasGenicMutation;
	}

	/**
	 * This function goes through all of the Annotations that have been entered for the current variant and enters the
	 * type of variant that is deemed to be the most pathogenic. The function follows the priority as set out by
	 * annovar.
	 * <P>
	 * The strategy of the function is to start out with the least pathogenic type (INTERGENIC), and to workthrough all
	 * types towards the most pathogenic. After this is finished, the variant type with the most pathogenic annotation
	 * is returned.
	 * <P>
	 * There should always be at least one annotation type. If not return ERROR (should never happen).
	 *
	 * @return most pathogenic variant type for current variant.
	 */
	@SuppressWarnings("unused")
	private VariantEffect getMostPathogenicVariantType() {
		VariantEffect vt;
		Collections.sort(annotations);
		Annotation a = annotations.get(0);
		return a.getMostPathogenicVarType();
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to add a non-coding
	 * RNA exon variant. From the program logic, only one such Annotation should be added per variant.
	 *
	 * @param ann
	 *            A noncoding RNA exonic annotation object.
	 */
	public void addNonCodingRNAExonicAnnotation(Annotation ann) {
		annotations.add(ann);
		hasNcRna = true;
		annotationCount++;
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to add a 5' UTR
	 * variant.
	 *
	 * @param ann
	 *            A 5' UTR annotation object.
	 */
	public void addUTR5Annotation(Annotation ann) {
		annotations.add(ann);
		hasUTR5 = true;
		hasGenicMutation = true;
		annotationCount++;
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to add a 3' UTR
	 * variant.
	 *
	 * @param ann
	 *            A 3' UTR annotation object.
	 */
	public void addUTR3Annotation(Annotation ann) {
		annotations.add(ann);
		hasUTR3 = true;
		hasGenicMutation = true;
		annotationCount++;
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to register an
	 * Annotation for a variant that is located between two genes. From the program logic, only one such Annotation
	 * should be added per variant.
	 *
	 * @param ann
	 *            An Annotation with type INTERGENIC
	 */
	public void addIntergenicAnnotation(Annotation ann) {
		annotations.add(ann);
		hasIntergenic = true;
		annotationCount++;
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to register an
	 * Annotation for a variant that affects the coding sequence of an exon. Many different variant types are summarized
	 * (NONSYNONYMOUS, DELETION etc.).
	 *
	 * @param ann
	 *            An Annotation to be added.
	 */
	public void addExonicAnnotation(Annotation ann) {
		annotations.add(ann);
		if (FluentIterable.from(ann.getEffects()).anyMatch(Predicates.equalTo(VariantEffect.SYNONYMOUS_VARIANT)))
			hasSynonymous = true;
		else if (FluentIterable.from(ann.getEffects()).anyMatch(VariantEffect.IS_SPLICING))
			hasSplicing = true;
		else
			hasExonic = true;

		geneSymbols.add(ann.getTranscript().getGeneSymbol());
		hasGenicMutation = true;
		annotationCount++;
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to register an
	 * annotation for a noncoding RNA transcript that is affected by a splice mutation.
	 *
	 * @param ann
	 *            {@link Annotation} to be registered
	 */
	public void addNcRNASplicing(Annotation ann) {
		// String s = String.format("%s", ann.hgvsDescription);
		hasNcRna = true;
		// ann.setVariantAnnotation(s); // TODO(holtgrew): necessary?
		annotations.add(ann);
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to add an annotation
	 * for an intronic variant. Note that if the same intronic annotation already exists, nothing is done, i.e., this
	 * method avoids duplicate annotations.
	 *
	 * @param ann
	 *            the Intronic annotation to be added.
	 */
	public void addIntronicAnnotation(Annotation ann) {
		geneSymbols.add(ann.getTranscript().getGeneSymbol());
		if (FluentIterable.from(ann.getEffects()).anyMatch(VariantEffect.IS_INTRONIC)) {
			for (Annotation a : annotations) {
				if (a.equals(ann))
					return; /* already have identical annotation */
			}
			annotations.add(ann);
		}
		if (ann.getMostPathogenicVarType() == VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT) {
			hasIntronic = true;
		} else if (ann.getMostPathogenicVarType() == VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT) {
			hasNcrnaIntronic = true;
		}
		hasGenicMutation = true;
		annotationCount++;
	}

	/**
	 * The {@link de.charite.compbio.jannovar.data.Chromosome Chromosome} class calls this function to register an
	 * annotation for a transcript inside a structural variant.
	 *
	 * @param ann
	 *            the Structual annotation to be added
	 */
	public void addStructuralAnnotation(Annotation ann) {
        if (ann.getTranscript() != null) {
            annotations.add(ann);
            geneSymbols.add(ann.getTranscript().getGeneSymbol());
            hasStructural = true;
            annotationCount++;
        }
	}

	/**
	 * An error annotation is created in a few cases where there data seem to be inconsistent.
	 *
	 * @param ann
	 *            An Annotation object that contains a String representing the error.
	 */
	public void addErrorAnnotation(Annotation ann) {
		annotations.add(ann);
		hasError = true;
		annotationCount++;
	}

	/**
	 * Adds an annotation for an upstream or downstream variant. Note that currently, we add only one such annotation
	 * for each gene, that is, we do not add a separate annotation for each isoform of a gene. This method avaoid such
	 * duplicate annotations.
	 *
	 * @param ann
	 *            The annotation that is to be added to the list of annotations for the current sequence variant.
	 */
	public void addUpDownstreamAnnotation(Annotation ann) {
		for (Annotation a : annotations) {
			if (a.equals(ann))
				return;
		}
		annotations.add(ann);
		VariantEffect type = ann.getMostPathogenicVarType();
		if (type == VariantEffect.DOWNSTREAM_GENE_VARIANT) {
			hasDownstream = true;
		} else if (type == VariantEffect.UPSTREAM_GENE_VARIANT) {
			hasUpstream = true;
		} else {
			LOGGER.error("Expecting UPSTREAM or DOWNSTREAM variant but got {}", type);
		}
		annotationCount++;
	}

}
