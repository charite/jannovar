package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.AnnotationLocation.RankType;
import de.charite.compbio.jannovar.reference.GenomeChange;
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
		StringBuilder builder = new StringBuilder();
		// Allele
		builder.append(alt);
		// Annotation
		builder.append('|').append(Joiner.on('&').join(effects));
		// Annotation_impact
		builder.append('|').append(getPutativeImpact());
		// Gene_Name
		builder.append('|').append(transcript.geneSymbol);
		// Gene_ID
		builder.append('|'); // TODO(holtgrem): gene ID
		// Feature_Type
		// Feature_ID
		if (transcript != null)
			builder.append("|transcript|").append(transcript.accession);
		else
			builder.append("||");

		// Transcript_BioType
		if (annoLoc != null && annoLoc.transcript != null)
			builder.append('|').append(annoLoc.transcript.isCoding() ? "Coding" : "Noncoding");
		else
			builder.append('|');

		// Rank / Total Rank
		if (annoLoc != null && annoLoc.rankType != RankType.UNDEFINED)
			builder.append('|').append(annoLoc.rank).append("/").append(annoLoc.totalRank);
		else
			builder.append('|');
		// HGVS.c
		builder.append('|').append(ntHGVSDescription);

		// HGVS.p
		if (transcript.isCoding())
			builder.append('|').append(aaHGVSDescription);
		else
			builder.append('|');
		if (annoLoc != null && annoLoc.txLocation != null)
			builder.append('|').append(annoLoc.txLocation.beginPos + 1);
		else
			builder.append('|');

		// cDNS.pos / cDNA.length
		if (annoLoc != null) {
			final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
			final TranscriptPosition txPos;
			if (annoLoc != null && annoLoc.txLocation.length() == 0)
				txPos = annoLoc.txLocation.getBeginPos().shifted(-1); // change length == 0, insertion
			else
				txPos = annoLoc.txLocation.getBeginPos(); // all other variants
			// System.err.println("TX REGION\t" + transcript.txRegion + "\tLEN=" + transcript.transcriptLength());
			// System.err.println("CHANGE TX REGION\t" + annoLoc.txLocation);
			// System.err.println("CHANGE TX POS\t" + txPos);
			int cdsPos = -1;
			try {
				cdsPos = projector.projectGenomeToCDSPosition(projector.transcriptToGenomePos(txPos)).pos;
			} catch (ProjectionException e) {
				// e.printStackTrace();
				throw new Error("Bug: problem with projection!: " + e.getMessage());
			}

			// CDS.pos / CDS.length
			// AA.pos / AA.length
			if (annoLoc != null && annoLoc.txLocation != null && transcript.isCoding()) {
				// CDS position / length
				builder.append('|').append(cdsPos + 1).append('/').append(transcript.cdsTranscriptLength());
				// AA position / length (excluding stop codon)
				builder.append('|').append(cdsPos / 3 + 1).append('/')
				.append(transcript.cdsTranscriptLength() / 3 - 1);
			} else {
				builder.append("||");
			}
		} else {
			builder.append("||");
		}

		// Distance
		if (transcript != null
				&& (effects.contains(VariantType.INTERGENIC) || effects.contains(VariantType.UPSTREAM) || effects
						.contains(VariantType.DOWNSTREAM))) {
			if (change.getGenomeInterval().isLeftOf(transcript.txRegion.getGenomeBeginPos()))
				builder.append(transcript.txRegion.getGenomeBeginPos().differenceTo(
						change.getGenomeInterval().getGenomeEndPos()));
			else
				builder.append(transcript.txRegion.getGenomeEndPos().differenceTo(
						change.getGenomeInterval().getGenomeBeginPos()));
		} else {
			builder.append('|');
		}

		// ERRORS / WARNING / INFOS
		builder.append('|').append(Joiner.on("&").join(messages));

		// Build value of the ANN string and escape invalid characters. Commas, semicolons and whitespaces are replace by underscores. We then escape stuff occuring in HGVS strings by URL encoding.
		String result = CharMatcher.anyOf(",;").or(CharMatcher.WHITESPACE).replaceFrom(builder.toString(), "_");
		result = CharMatcher.is('=').replaceFrom(result, "%3D");
		result = CharMatcher.is('(').replaceFrom(result, "%28");
		result = CharMatcher.is(')').replaceFrom(result, "%29");
		return result;
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
