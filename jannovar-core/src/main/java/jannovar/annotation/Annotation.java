package jannovar.annotation;

import jannovar.Immutable;
import jannovar.impl.util.StringUtil;
import jannovar.reference.TranscriptModel;

//TODO(holtgrem): Test me!

/**
 * Collect the information for one variant's annotation
 *
 * @see AnnotationVariantTypeDecorator
 * @see AnnotationTextGenerator
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class Annotation implements Comparable<Annotation> {

	/** type of the variant */
	public final VariantType varType;

	/** position of the variant on the transcript, used for sorting only */
	public final int txVarPos;

	/** HGVS variant annotation */
	public final String hgvsDescription;

	/** the transcript, <code>null</code> for {@link VariantType#INTERGENIC} annotations */
	public final TranscriptModel transcript;

	// TODO(holtgrem): Change parameter order, transcript should be first
	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * @param varType
	 *            type of the variant
	 * @param txVarPos
	 *            transcript start position of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclauture
	 * @param transcript
	 *            transcript for this annotation
	 */
	public Annotation(VariantType varType, int txVarPos, String hgvsDescription, TranscriptModel transcript) {
		this.varType = varType;
		this.txVarPos = txVarPos;
		this.hgvsDescription = hgvsDescription;
		this.transcript = transcript;
	}

	/**
	 * Return the full annotation with the gene symbol.
	 *
	 * If this annotation does not have a symbol (e.g., for an intergenic annotation) then just return the annotation
	 * string, e.g., <code>"KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q"</code>.
	 *
	 * @return full annotation string
	 */
	public String getSymbolAndAnnotation() {
		if (transcript.geneSymbol == null && hgvsDescription != null)
			return hgvsDescription;
		return StringUtil.concatenate(transcript.geneSymbol, ":", hgvsDescription);
	}

	@Override
	public int compareTo(Annotation other) {
		int result = VariantType.priorityLevel(this.varType) - VariantType.priorityLevel(other.varType);
		if (result != 0)
			return result;

		result = txVarPos - other.txVarPos;
		if (result != 0)
			return result;

		return transcript.compareTo(other.transcript);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hgvsDescription == null) ? 0 : hgvsDescription.hashCode());
		result = prime * result + ((transcript == null) ? 0 : transcript.hashCode());
		result = prime * result + txVarPos;
		result = prime * result + ((varType == null) ? 0 : varType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotation other = (Annotation) obj;
		if (hgvsDescription == null) {
			if (other.hgvsDescription != null)
				return false;
		} else if (!hgvsDescription.equals(other.hgvsDescription))
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		if (txVarPos != other.txVarPos)
			return false;
		if (varType != other.varType)
			return false;
		return true;
	}

}
