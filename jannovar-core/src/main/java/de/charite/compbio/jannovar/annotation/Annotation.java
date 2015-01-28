package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.impl.util.StringUtil;
import de.charite.compbio.jannovar.reference.TranscriptModel;

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

	/** The DESCRIPTION string to use in the VCF header for VCFVariantAnnotation objects */
	public final static String VCF_HEADER_DESCRIPTION_STRING = "Functional annotations:'Allele |Annotation|"
			+ "Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|"
			+ "cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|ERRORS / WARNINGS / INFO'";

	/** variant types, sorted by internal pathogenicity score */
	public final ImmutableSortedSet<VariantType> effects;

	/** location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	public final AnnotationLocation annoLoc;

	/** HGVS variant annotation */
	public final String hgvsDescription;

	/** the transcript, <code>null</code> for {@link VariantType#INTERGENIC} annotations */
	public final TranscriptModel transcript;

	// TODO(holtgrem): Change parameter order, transcript should be first
	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * @param varType
	 *            one type of the variant
	 * @param annoLoc
	 *            location of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclauture
	 * @param transcript
	 *            transcript for this annotation
	 */
	public Annotation(VariantType varType, AnnotationLocation annoLoc, String hgvsDescription,
			TranscriptModel transcript) {
		this(ImmutableSortedSet.of(varType), annoLoc, hgvsDescription, transcript);
	}

	// TODO(holtgrem): Change parameter order, transcript should be first
	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param effects
	 *            type of the variants
	 * @param annoLoc
	 *            location of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclauture
	 * @param transcript
	 *            transcript for this annotation
	 */
	public Annotation(Collection<VariantType> varTypes, AnnotationLocation annoLoc, String hgvsDescription,
			TranscriptModel transcript) {
		this.effects = ImmutableSortedSet.copyOf(varTypes);
		this.annoLoc = annoLoc;
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

	/**
	 * @return most pathogenic {@link VariantType} link {@link #effects}.
	 */
	public VariantType getMostPathogenicVarType() {
		return effects.first();
	}

	@Override
	public int compareTo(Annotation other) {
		int result = getMostPathogenicVarType().priorityLevel() - other.getMostPathogenicVarType().priorityLevel();
		if (result != 0)
			return result;

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
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
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
		if (effects != other.effects)
			return false;
		return true;
	}

}
