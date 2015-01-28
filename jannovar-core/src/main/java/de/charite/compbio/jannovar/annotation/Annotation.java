package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.AnnotationLocation.RankType;
import de.charite.compbio.jannovar.impl.util.StringUtil;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;

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
			+ "cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|ERRORS / WARNINGS / INFO'";

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

	/** HGVS variant annotation */
	public final String hgvsDescription;

	/** the transcript, <code>null</code> for {@link VariantType#INTERGENIC} annotations */
	public final TranscriptModel transcript;

	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param transcript
	 *            transcript for this annotation
	 * @param effects
	 *            type of the variants
	 * @param annoLoc
	 *            location of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclauture
	 */
	public Annotation(TranscriptModel transcript, Collection<VariantType> varTypes, AnnotationLocation annoLoc,
			String hgvsDescription) {
		this(transcript, varTypes, annoLoc, hgvsDescription, ImmutableSortedSet.<AnnotationMessage> of());
	}

	/**
	 * Initialize the {@link Annotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param transcript
	 *            transcript for this annotation
	 * @param effects
	 *            type of the variants
	 * @param annoLoc
	 *            location of the variant
	 * @param hgvsDescription
	 *            variant description following the HGVS nomenclauture
	 * @param messages
	 *            {@link Collection} of {@link AnnotatioMessage} objects
	 */
	public Annotation(TranscriptModel transcript, Collection<VariantType> varTypes, AnnotationLocation annoLoc,
			String hgvsDescription, Collection<AnnotationMessage> messages) {
		this.effects = ImmutableSortedSet.copyOf(varTypes);
		this.annoLoc = annoLoc;
		this.hgvsDescription = hgvsDescription;
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
		StringBuilder builder = new StringBuilder();
		// Allele
		builder.append(alt);
		// Annotation
		builder.append('|').append(Joiner.on('&').join(effects));
		// Annotation_impact
		builder.append('|').append(getPutativeImpact());
		// Gene_Name
		builder.append('|').append(annoLoc.transcript.accession);
		// Gene_ID
		builder.append('|'); // TODO(holtgrem): gene ID
		// Feature_Type
		builder.append('|'); // TODO(holtgrem): feature type
		// Feature_ID
		builder.append('|'); // TODO(holtgrem): feature ID

		// Transcript_BioType
		if (annoLoc.transcript != null)
			builder.append('|').append(annoLoc.transcript.isCoding() ? "Coding" : "Noncoding");
		else
			builder.append('|');

		// Rank / Total Rank
		if (annoLoc.rankType != RankType.UNDEFINED)
			builder.append('|').append(annoLoc.rank).append("/").append(annoLoc.totalRank);
		else
			builder.append('|');
		// HGVS.c
		builder.append('|').append(hgvsDescription); // TODO(holtgrem): HGVS.c

		// HGVS.p
		if (transcript.isCoding())
			builder.append('|').append(hgvsDescription); // TODO(holtgrem): HGVS.p
		else
			builder.append('|');
		if (annoLoc.txLocation != null)
			builder.append('|').append(annoLoc.txLocation.beginPos + 1);
		else
			builder.append('|');

		// cDNS.pos / cDNA.length
		final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		final TranscriptPosition txPos;
		if (annoLoc.txLocation.length() == 0)
			txPos = annoLoc.txLocation.getBeginPos().shifted(-1);
		else
			txPos = annoLoc.txLocation.getBeginPos();
		int cdsPos = -1;
		try {
			cdsPos = projector.projectGenomeToCDSPosition(projector.transcriptToGenomePos(txPos)).pos;
		} catch (ProjectionException e) {
			throw new Error("Bug: problem with projection!");
		}

		// CDS.pos / CDS.length
		// AA.pos / AA.length
		if (annoLoc.txLocation != null && transcript.isCoding()) {
			// CDS position / length
			builder.append('|').append(cdsPos + 1).append(" / ").append(transcript.cdsTranscriptLength());
			// AA position / length (excluding stop codon)
			builder.append('|').append(cdsPos / 3 + 1).append(" / ").append(transcript.cdsTranscriptLength() / 3 - 1);
		} else {
			builder.append("||");
		}

		// ERRORS / WARNING / INFOS
		builder.append('|').append(Joiner.on("&").join(messages));
		return builder.toString();
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
