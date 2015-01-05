package jannovar.annotation;

// TODO(holtgrew): Test me!

/**
 * Decorator for {@link ImmutableAnnotation} for generating annotation text.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
final public class AnnotationTextGenerator {

	/** the decorated {@link ImmutableAnnotation} */
	final public ImmutableAnnotation annotation;

	AnnotationTextGenerator(ImmutableAnnotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * Return the accession number of the transcript associated with this variant (if possible).
	 *
	 * If there is no transcript, e.g., for {@link VariantType#DOWNSTREAM} annotations, then it returns the geneSymbol
	 * (if possible). If there is no gene symbol (e.g., for {@link VariantType#INTERGENIC} annotations), it returns
	 * <code>"."</code>
	 *
	 * @return the accession number
	 */
	public String getAccessionNumber() {
		if (this.annotation.hgvsDescription == null)
			return "."; // TODO(holtgrew): Can this happen?

		int i = this.annotation.hgvsDescription.indexOf(":");
		if (i > 0)
			return this.annotation.hgvsDescription.substring(0, i);

		if (this.annotation.geneSymbol == null)
			return ".";
		else
			return this.annotation.geneSymbol;
	}

}
