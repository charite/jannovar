package jannovar.annotation;

// TODO(holtgrem): Test me!

/**
 * Generate annotation text (effect and HGVS description) from {@link ImmutableAnnotationList} object.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
abstract class AnnotationListTextGenerator {

	/** the decorated {@link ImmutableAnnotationList} */
	public final ImmutableAnnotationList annotations;

	/**
	 * Initialize the decorator.
	 *
	 * @param annotations
	 *            {@link ImmutableAnnotationList} of {@link ImmutableAnnotation} objects
	 */
	public AnnotationListTextGenerator(ImmutableAnnotationList annotations) {
		this.annotations = annotations;
	}

	/**
	 * @return String with the effect text, comma separated if {@link #getAnnotations} returns more than one element
	 */
	public String buildEffectText() {
		StringBuilder builder = new StringBuilder();
		for (ImmutableAnnotation anno : getAnnotations().entries) {
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
		for (ImmutableAnnotation anno : getAnnotations().entries) {
			if (builder.length() != 0)
				builder.append(',');
			builder.append(anno.getSymbolAndAnnotation());
		}
		return builder.toString();
	}

	/**
	 * @return {@link ImmutableAnnotationList} of annotations to generate the annotation text for
	 */
	protected abstract ImmutableAnnotationList getAnnotations();

}
