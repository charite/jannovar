package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.VariantDescription;

// TODO(holtgrem): Test me!

/**
 * Collect the information for one variant's annotation.
 *
 * Implements the {@link VariantDescription} interface for quicker access to the variant description information.
 *
 * @see AnnotationVariantTypeDecorator
 * @see AnnotationTextGenerator
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class Annotation implements VariantDescription, Comparable<Annotation> {

	/**
	 * This line is added to the output of a VCF file annotated by Jannovar and describes the new field for the INFO
	 * section entitled EFFECT, which decribes the effects of variants (splicing,missense,stoploss, etc).
	 */
	public static final String INFO_EFFECT = ""
			+ "variant effect (UTR5,UTR3,intronic,splicing,missense,stoploss,stopgain,"
			+ "startloss,duplication,frameshift-insertion,frameshift-deletion,non-frameshift-deletion,"
			+ "non-frameshift-insertion,synonymous)";

	/**
	 * This line is added to the output of a VCF file annotated by Jannovar and describes the new field for the INFO
	 * section entitled HGVS, which provides the HGVS encoded variant corresponding to the chromosomal variant in the
	 * original VCF file.
	 */
	public static final String INFO_HGVS = "HGVS Nomenclature";

	/** The DESCRIPTION string to use in the VCF header for VCFVariantAnnotation objects */
	public static final String VCF_ANN_DESCRIPTION_STRING = "Functional annotations:'Allele|Annotation|"
			+ "Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|"
			+ "cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'";

	/** the annotated {@link GenomeVariant} */
	private final GenomeVariant change;

	/** variant types, sorted by internal pathogenicity score */
	private final ImmutableSortedSet<VariantEffect> effects;

	/** errors and warnings */
	private final ImmutableSortedSet<AnnotationMessage> messages;

	/** location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	private final AnnotationLocation annoLoc;

	/** HGVS nucleotide variant annotation */
	private final String ntHGVSDescription;

	/** amino acid variant annotation */
	private final String aaHGVSDescription;

	/** the transcript, <code>null</code> for {@link VariantEffect#INTERGENIC} annotations */
	private final TranscriptModel transcript;

	/**
	 * Initialize object with messages only.
	 *
	 * @param messages
	 *            {@link AnnotationMessage}s to use in this annotation
	 */
	public Annotation(Collection<AnnotationMessage> messages) {
		this(null, null, null, null, null, null, messages);
	}

	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param change
	 *            the annotated {@link GenomeVariant}
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
	public Annotation(TranscriptModel transcript, GenomeVariant change, Collection<VariantEffect> varTypes,
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
	 *            the annotated {@link GenomeVariant}
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
	public Annotation(TranscriptModel transcript, GenomeVariant change, Collection<VariantEffect> varTypes,
			AnnotationLocation annoLoc, String ntHGVSDescription, String aaHGVSDescription,
			Collection<AnnotationMessage> messages) {
		if (change != null)
			change = change.withStrand(Strand.FWD); // enforce forward strand
		this.change = change;
		if (varTypes == null)
			this.effects = ImmutableSortedSet.<VariantEffect> of();
		else
			this.effects = ImmutableSortedSet.copyOf(varTypes);
		this.annoLoc = annoLoc;
		this.ntHGVSDescription = ntHGVSDescription;
		this.aaHGVSDescription = aaHGVSDescription;
		this.transcript = transcript;
		this.messages = ImmutableSortedSet.copyOf(messages);
	}

	/** @return the annotated {@link GenomeVariant} */
	public GenomeVariant getGenomeVariant() {
		return change;
	}

	/** @return variant types, sorted by internal pathogenicity score */
	public ImmutableSortedSet<VariantEffect> getEffects() {
		return effects;
	}

	/** @return errors and warnings */
	public ImmutableSortedSet<AnnotationMessage> getMessages() {
		return messages;
	}

	/** @return location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	public AnnotationLocation getAnnoLoc() {
		return annoLoc;
	}

	/** @return HGVS nucleotide variant annotation */
	public String getNucleotideHGVSDescription() {
		return ntHGVSDescription;
	}

	/** @return amino acid variant annotation */
	public String getAminoAcidHGVSDescription() {
		return aaHGVSDescription;
	}

	/** @return the transcript, <code>null</code> for {@link VariantEffect#INTERGENIC} annotations */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/**
	 * @return highest {@link PutativeImpact} of all {@link #effects}.
	 */
	public PutativeImpact getPutativeImpact() {
		if (effects.isEmpty())
			return null;
		VariantEffect worst = effects.first();
		for (VariantEffect vt : effects)
			if (worst.getImpact().compareTo(vt.getImpact()) > 0)
				worst = vt;
		return worst.getImpact();
	}

	/**
	 * Return the standardized VCF variant string for the given <code>ALT</code> allele.
	 *
	 * The <code>ALT</code> allele has to be given to this function since we trim away at least the first base of
	 * <code>REF</code>/<code>ALT</code>.
	 *
	 * @param escape
	 *            whether or not to escape the invalid VCF characters, e.g. <code>'='</code>.
	 */
	public String toVCFAnnoString(String alt, boolean escape) {
		VCFAnnotationData data = new VCFAnnotationData();
		data.effects = effects;
		data.impact = getPutativeImpact();
		data.setTranscriptAndChange(transcript, change);
		data.setAnnoLoc(annoLoc);
		data.ntHGVSDescription = ntHGVSDescription;
		data.aaHGVSDescription = aaHGVSDescription;
		data.messages = messages;
		if (escape)
			return data.toString(alt);
		else
			return data.toUnescapedString(alt);
	}

	/**
	 * Forward to {@link toVCFAnnoString}<code>(alt, true)</code>.
	 */
	public String toVCFAnnoString(String alt) {
		return toVCFAnnoString(alt, true);
	}

	/**
	 * Return the gene annotation or <code>"."</code> if it has no transcript.
	 *
	 * @return gene symbol or <code>"."</code>
	 */
	public String getGeneSymbol() {
		if (transcript == null || transcript.getGeneSymbol() == null)
			return ".";
		else
			return transcript.getGeneSymbol();
	}

	/**
	 * Return the full annotation with the gene symbol.
	 *
	 * If this annotation does not have a symbol (e.g., for an intergenic annotation) then just return the annotation
	 * string, e.g., <code>"KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q"</code>.
	 *
	 * @return full annotation string or <code>null</code> if {@link #transcript} is <code>null</code>
	 */
	public String getSymbolAndAnnotation() {
		if (transcript == null)
			return null;
		return Joiner.on(":").skipNulls()
				.join(transcript.getGeneSymbol(), transcript.getAccession(), ntHGVSDescription, aaHGVSDescription);
	}

	/**
	 * @return most pathogenic {@link VariantEffect} link {@link #effects}, <code>null</code> if none.
	 */
	public VariantEffect getMostPathogenicVarType() {
		if (effects.isEmpty())
			return null;
		return effects.first();
	}

	public String getChrName() {
		return change.getChrName();
	}

	public int getChr() {
		return change.getChr();
	}

	public int getPos() {
		return change.getPos();
	}

	public String getRef() {
		return change.getRef();
	}

	public String getAlt() {
		return change.getAlt();
	}

	public int compareTo(Annotation other) {
		if (getMostPathogenicVarType() == null && getMostPathogenicVarType() == other.getMostPathogenicVarType())
			return 0;
		else if (other.getMostPathogenicVarType() == null)
			return -1;
		else if (getMostPathogenicVarType() == null)
			return 1;

		int result = getMostPathogenicVarType().ordinal() - other.getMostPathogenicVarType().ordinal();
		if (result != 0)
			return result;

		if (transcript == null && other.transcript == null)
			return 0;
		else if (other.transcript == null)
			return -1;
		else if (transcript == null)
			return 1;

		return transcript.compareTo(other.transcript);
	}

	@Override
	public String toString() {
		return "Annotation [change=" + change + ", effects=" + effects + ", ntHGVSDescription=" + ntHGVSDescription
				+ ", aaHGVSDescription=" + aaHGVSDescription + ", transcript.getAccession()=" + transcript.getAccession() + "]";
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
