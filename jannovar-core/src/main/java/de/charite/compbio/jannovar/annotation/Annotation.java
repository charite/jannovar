package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.TranscriptModel;

// TODO(holtgrem): Test me!
// TODO(holtgrem): Sorting of annotations
// TODO(holtgrem): collection of warnings

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
	public final static String VCF_ANN_DESCRIPTION_STRING = "Functional annotations:'Allele|Annotation|"
			+ "Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|"
			+ "cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'";

	/** the annotated {@link GenomeChange} */
	public final GenomeChange change;

	/** variant types, sorted by internal pathogenicity score */
	public final ImmutableSortedSet<VariantType> effects;

	/** errors and warnings */
	public final ImmutableSortedSet<AnnotationMessage> messages;

	/**
	 * @return highest {@link PutativeImpact} of all {@link #effects}.
	 */
	public final PutativeImpact getPutativeImpact() {
		if (effects.isEmpty())
			return null;
		VariantType worst = effects.first();
		for (VariantType vt : effects)
			if (worst.getPutativeImpact().compareTo(vt.getPutativeImpact()) > 0)
				worst = vt;
		return worst.getPutativeImpact();
	}

	/** location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	public final AnnotationLocation annoLoc;

	/** HGVS nucleotide variant annotation */
	public final String ntHGVSDescription;

	/** amino acid variant annotation */
	public final String aaHGVSDescription;

	/** the transcript, <code>null</code> for {@link VariantType#INTERGENIC} annotations */
	public final TranscriptModel transcript;

	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param change
	 *            the annotated {@link GenomeChange}
	 * @param transcript
	 *            transcript for this annotation
	 * @param effects
	 *            type of the variants
	 * @param annoLoc
	 *            location of the variant
	 * @param ntHGVSDescription
	 *            nucleotide variant description following the HGVS nomenclauture
	 * @param aaHGVSDescription
	 *            amino acid variant description following the HGVS nomenclauture
	 */
	public Annotation(TranscriptModel transcript, GenomeChange change, Collection<VariantType> varTypes,
			AnnotationLocation annoLoc, String ntHGVSDescription, String aaHGVSDescription) {
		this(transcript, change, varTypes, annoLoc, ntHGVSDescription, aaHGVSDescription, ImmutableSortedSet
				.<AnnotationMessage> of());
	}

	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param change
	 *            the annotated {@link GenomeChange}
	 * @param transcript
	 *            transcript for this annotation
	 * @param effects
	 *            type of the variants
	 * @param annoLoc
	 *            location of the variant
	 * @param ntHGVSDescription
	 *            nucleotide variant description following the HGVS nomenclauture
	 * @param aaHGVSDescription
	 *            amino acid variant description following the HGVS nomenclauture
	 * @param messages
	 *            {@link Collection} of {@link AnnotatioMessage} objects
	 */
	public Annotation(TranscriptModel transcript, GenomeChange change, Collection<VariantType> varTypes,
			AnnotationLocation annoLoc, String ntHGVSDescription, String aaHGVSDescription,
			Collection<AnnotationMessage> messages) {
		this.change = change;
		this.effects = ImmutableSortedSet.copyOf(varTypes);
		this.annoLoc = annoLoc;
		this.ntHGVSDescription = ntHGVSDescription;
		this.aaHGVSDescription = aaHGVSDescription;
		this.transcript = transcript;
		this.messages = ImmutableSortedSet.copyOf(messages);
	}

	/**
	 * Return the standardized VCF variant string for the given <code>ALT</code> allele.
	 *
	 * The <code>ALT</code> allele has to be given to this function since we trim away at least the first base of
	 * <code>REF</code>/<code>ALT</code>.
	 */
	public String toVCFAnnoString(String alt) {
		VCFAnnotationData data = new VCFAnnotationData();
		data.effects = effects;
		data.impact = getPutativeImpact();
		data.setTranscriptAndChange(transcript, change);
		data.setAnnoLoc(annoLoc);
		data.ntHGVSDescription = ntHGVSDescription;
		data.aaHGVSDescription = aaHGVSDescription;
		data.messages = messages;
		return data.toString(alt);
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
		return Joiner.on(":").skipNulls()
				.join(transcript.geneSymbol, transcript.accession, ntHGVSDescription, aaHGVSDescription);
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
		result = prime * result + ((aaHGVSDescription == null) ? 0 : aaHGVSDescription.hashCode());
		result = prime * result + ((annoLoc == null) ? 0 : annoLoc.hashCode());
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
		result = prime * result + ((ntHGVSDescription == null) ? 0 : ntHGVSDescription.hashCode());
		result = prime * result + ((transcript == null) ? 0 : transcript.hashCode());
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
		if (aaHGVSDescription == null) {
			if (other.aaHGVSDescription != null)
				return false;
		} else if (!aaHGVSDescription.equals(other.aaHGVSDescription))
			return false;
		if (annoLoc == null) {
			if (other.annoLoc != null)
				return false;
		} else if (!annoLoc.equals(other.annoLoc))
			return false;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		if (ntHGVSDescription == null) {
			if (other.ntHGVSDescription != null)
				return false;
		} else if (!ntHGVSDescription.equals(other.ntHGVSDescription))
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		return true;
	}

}
