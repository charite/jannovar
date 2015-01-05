package jannovar.annotation;

import jannovar.Immutable;

// TODO(holtgrem): Remove Immutable prefix
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
public final class ImmutableAnnotation implements Comparable<ImmutableAnnotation> {

	/** type of the variant */
	public final VariantType varType;

	/** position of the variant on the transcript, used for sorting only */
	public final int txVarPos;

	/** HGVS variant annotation */
	public final String hgvsDescription;

	/** the gene symbol */
	public final String geneSymbol;

	/** the NCBI Entrez gene ID, set to <tt>-1</tt> if not available */
	public final int entrezID;

	/**
	 * Initialize the {@link ImmutableAnnotation} with the given values.
	 *
	 * @param varType
	 *            type of the variant
	 * @param txVarPos
	 *            transcript start position of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclauture
	 * @param geneSymbol
	 *            gene symbol of this annotation
	 * @param entrezID
	 *            numeric entrez ID
	 */
	public ImmutableAnnotation(VariantType varType, int txVarPos, String hgvsDescription, String geneSymbol,
			int entrezID) {
		this.varType = varType;
		this.txVarPos = txVarPos;
		this.hgvsDescription = hgvsDescription;
		this.geneSymbol = geneSymbol;
		this.entrezID = entrezID;
	}

	/**
	 * Initialize the {@link ImmutableAnnotation} with the given value, <code>entrezID</code> is set to <code>-1</code>.
	 *
	 * @param varType
	 *            type of the variant
	 * @param txVarPos
	 *            transcript start position of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclature
	 * @param geneSymbol
	 *            gene symbol of this annotation
	 */
	public ImmutableAnnotation(VariantType varType, int txVarPos, String hgvsDescription, String geneSymbol) {
		this(varType, txVarPos, hgvsDescription, geneSymbol, -1);
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
		if (geneSymbol == null && hgvsDescription != null)
			return hgvsDescription;
		return String.format("%s:%s", geneSymbol, hgvsDescription);
	}

	@Override
	public int compareTo(ImmutableAnnotation other) {
		int myPriority = VariantType.priorityLevel(this.varType);
		int otherPriority = VariantType.priorityLevel(other.varType);
		if (myPriority < otherPriority)
			return -1;
		else if (myPriority > otherPriority)
			return 1;
		if (txVarPos < other.txVarPos)
			return -1;
		else if (txVarPos > other.txVarPos)
			return 1;
		else
			return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entrezID;
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((hgvsDescription == null) ? 0 : hgvsDescription.hashCode());
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
		ImmutableAnnotation other = (ImmutableAnnotation) obj;
		if (entrezID != other.entrezID)
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (hgvsDescription == null) {
			if (other.hgvsDescription != null)
				return false;
		} else if (!hgvsDescription.equals(other.hgvsDescription))
			return false;
		if (txVarPos != other.txVarPos)
			return false;
		if (varType != other.varType)
			return false;
		return true;
	}

}
