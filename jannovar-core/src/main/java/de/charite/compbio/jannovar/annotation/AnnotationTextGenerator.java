package de.charite.compbio.jannovar.annotation;

//TODO(holtgrewe): Remove

/**
 * Decorator for {@link Annotation} for generating annotation text.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 */
@Deprecated
final public class AnnotationTextGenerator {

	/** the decorated {@link Annotation} */
	private final Annotation annotation;

	AnnotationTextGenerator(Annotation annotation) {
		this.annotation = annotation;
	}

	/** @return the decorated {@link Annotation} */
	public Annotation getAnnotation() {
		return annotation;
	}

	/**
	 * Return the accession number of the transcript associated with this variant (if possible).
	 *
	 * If there is no transcript, e.g., for {@link VariantEffect#DOWNSTREAM_GENE_VARIANT} annotations, then it returns
	 * the geneSymbol (if possible). If there is no gene symbol (e.g., for {@link VariantEffect#INTERGENIC_VARIANT}
	 * annotations), it returns <code>"."</code>
	 *
	 * @return the accession number
	 */
	public String getAccessionNumber() {
		if (this.annotation.getTranscript() == null)
			return ".";
		else
			return this.annotation.getTranscript().getAccession();
	}

}
