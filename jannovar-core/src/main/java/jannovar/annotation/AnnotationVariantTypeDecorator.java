package jannovar.annotation;

// TODO(holtgrem): Test me!

/**
 * Decorator for {@link Annotation} that allows querying for variant types.
 *
 * @author holtgrem
 */
final public class AnnotationVariantTypeDecorator {

	/** the decorated {@link Annotation} */
	final public Annotation annotation;

	AnnotationVariantTypeDecorator(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * This function checks if we have a variant that affects the sequence of a coding exon
	 *
	 * @return <code>true</code> if we have a missense, PTC, splicing, indel variant, or a synonymous change.
	 */
	public boolean isCodingExonic() {
		// TODO(holtgrem): Are the first three ones correct?
		switch (this.annotation.varType) {
		case SPLICE_DONOR:
		case SPLICE_ACCEPTOR:
		case SPLICE_REGION:
		case STOPLOSS:
		case STOPGAIN:
		case SYNONYMOUS:
		case MISSENSE:
		case NON_FS_SUBSTITUTION:
		case NON_FS_INSERTION:
		case FS_SUBSTITUTION:
		case FS_DELETION:
		case FS_INSERTION:
		case NON_FS_DELETION:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return <code>true</code> if this annotation is for a 3' or 5' UTR
	 */
	public boolean isUTRVariant() {
		switch (this.annotation.varType) {
		case UTR3:
		case UTR5:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return <code>true</code> if the variant affects an exon of an ncRNA
	 */
	public boolean isNonCodingRNA() {
		switch (this.annotation.varType) {
		case ncRNA_EXONIC:
		case ncRNA_INTRONIC:
		case ncRNA_SPLICE_DONOR:
		case ncRNA_SPLICE_ACCEPTOR:
		case ncRNA_SPLICE_REGION:
			return true;
		default:
			return false;
		}
	}
}
