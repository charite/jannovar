package jannovar.annotation;

// TODO(holtgrem): Test me!

/**
 * Generate annotation text (effect and HGVS description) from {@link AnnotationList} object.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class AnnotationListTextGenerator {

	/** the decorated {@link AnnotationList} */
	public final AnnotationList annotations;

	/**
	 * Initialize the decorator.
	 *
	 * @param annotations
	 *            {@link AnnotationList} of {@link Annotation} objects
	 */
	public AnnotationListTextGenerator(AnnotationList annotations) {
		this.annotations = annotations;
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildEffectText() {
		StringBuilder builder = new StringBuilder();
		for (Annotation anno : getAnnotations().entries) {
			if (builder.length() != 0)
				builder.append(',');
			builder.append(anno.varType);
		}
		return builder.toString();
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildHGVSText() {
		StringBuilder builder = new StringBuilder();
		for (Annotation anno : getAnnotations().entries) {
			if (builder.length() != 0)
				builder.append(',');
			builder.append(anno.getSymbolAndAnnotation());
		}
		return builder.toString();
	}

	/**
	 * @return {@link AnnotationList} of annotations to generate the annotation text for
	 */
	protected abstract AnnotationList getAnnotations();

}
