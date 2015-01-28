package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.TranscriptPosition;

/**
 * Annotation to be used in the VCF output.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class VCFVariantAnnotation {

	/** The annotated GenomeChange */
	public final GenomeChange change;

	/** ALT field, incase of multiple ALT fields, this helps to identify which ALT we are referring to. */
	public final String alt;

	/** List of {@link VariantType}s of this annotation. */
	public final ImmutableSortedSet<VariantType> effects;

	/** putative impact of the variant */
	public final PutativeImpact putativeImpact;

	// TODO(holtgrem): replace by AnnotationLocation
	/** identifier of affected gene, or closest gene if intergenic */
	public final String geneID;

	// TODO(holtgrem): replace by AnnotationLocation
	/** type of the affected feature */
	public final String featureType;

	// TODO(holtgrem): replace by AnnotationLocation
	/** affected feature, can include cell type / tissue information */
	public final String featureID;

	// TODO(holtgrem): replace by AnnotationLocation
	/** transcript biotype, e.g. <code>"Coding"</code>, <code>"Noncoding"</code> */
	public final String featureBiotype;

	/** HGVS nucleotide annotation (either HGVS.c or HGVS.n) */
	public final String hgvsNT;

	/** HGVS amino acid notation, null if non-coding sequence */
	public final String hgvsAA;

	// TODO(holtgrem): replace by AnnotationLocation
	/** Position on the transcript, from this exon/intron rank, CDS and protein position can be obtained. */
	public final TranscriptPosition transcriptPos;

	/** List of error messages */
	public final ImmutableList<AnnotationMessage> messages;

	public VCFVariantAnnotation(GenomeChange change, String alt, Collection<VariantType> effects,
			PutativeImpact putativeImpact, String geneID, String featureType, String featureID, String featureBiotype,
			String hgvsNT, String hgvsAA, TranscriptPosition transcriptPos, ImmutableList<AnnotationMessage> messages) {
		this.change = change;
		this.alt = alt;
		this.effects = ImmutableSortedSet.copyOf(effects);
		this.putativeImpact = putativeImpact;
		this.geneID = geneID;
		this.featureType = featureType;
		this.featureID = featureID;
		this.featureBiotype = featureBiotype;
		this.hgvsNT = hgvsNT;
		this.hgvsAA = hgvsAA;
		this.transcriptPos = transcriptPos;
		this.messages = messages;
	}

	public VCFVariantAnnotation(GenomeChange change, String alt, Collection<VariantType> effects,
			PutativeImpact putativeImpact, String geneID, String featureType, String featureID, String featureBiotype,
			String hgvsNT, String hgvsAA, TranscriptPosition transcriptPos) {
		this(change, alt, effects, putativeImpact, geneID, featureType, featureID, featureBiotype, hgvsNT, hgvsAA,
				transcriptPos, ImmutableList.<AnnotationMessage> of());
	}

	/**
	 * @return String with the VCF annotation for the ANN field.
	 */
	public String toVCFString() {
		String rankStr = "";
		String txPosStr = "";
		String cdsPosStr = "";
		String aaPosStr = "";
		return Joiner.on("|").join(
				ImmutableList.of(alt, Joiner.on("&").join(effects), putativeImpact, geneID, featureType, featureID,
						featureBiotype, rankStr, hgvsNT, hgvsAA, txPosStr, cdsPosStr, aaPosStr,
						Joiner.on("&").join(messages)));
	}

}