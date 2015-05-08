package de.charite.compbio.jannovar.annotation;

//TODO(holtgrewe): Remove

/**
 * Decorator for {@link Annotation} for generating annotation text.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
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
	 * If there is no transcript, e.g., for {@link VariantEffect#DOWNSTREAM} annotations, then it returns the geneSymbol
	 * (if possible). If there is no gene symbol (e.g., for {@link VariantEffect#INTERGENIC} annotations), it returns
	 * <code>"."</code>
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
