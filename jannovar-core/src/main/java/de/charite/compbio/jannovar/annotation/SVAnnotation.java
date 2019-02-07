package de.charite.compbio.jannovar.annotation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.SVGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Annotation information of a {@link SVGenomeVariant}
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public class SVAnnotation implements Comparable<SVAnnotation> {

	/**
	 * The DESCRIPTION string to use in the VCF header for VCFVariantAnnotation objects
	 */
	public static final String VCF_SVANN_DESCRIPTION_STRING = "Functional SV Annotation:'Annotation|" +
		"Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|ERRORS / WARNINGS / INFO'";

	private static final ImmutableSet<AnnotationMessage> EMPTY_ANNOTATION_MESSAGES = Sets.immutableEnumSet(
		EnumSet.noneOf(AnnotationMessage.class));

	/**
	 * Annotated {@link SVGenomeVariant}.
	 */
	private final SVGenomeVariant variant;

	/**
	 * Annotate transcript.
	 */
	private final TranscriptModel transcript;

	/**
	 * The effects the variant is annotated with for the transcript.
	 */
	private final ImmutableSet<VariantEffect> effects;

	/**
	 * errors and warnings
	 */
	private final ImmutableSet<AnnotationMessage> messages;

	/**
	 * Initialize object with messages only.
	 *
	 * @param messages {@link AnnotationMessage}s to use in this annotation
	 */
	public SVAnnotation(Collection<AnnotationMessage> messages) {
		this.variant = null;
		this.transcript = null;
		this.effects = Sets.immutableEnumSet(EnumSet.noneOf(VariantEffect.class));
		this.messages = Sets.immutableEnumSet(messages);
	}

	/**
	 * Initialize all fields.
	 *
	 * @param variant The {@link SVGenomeVariant} that was annotated.
	 * @param effects The effects to store in the SV annotation.
	 */
	public SVAnnotation(SVGenomeVariant variant, TranscriptModel transcriptModel, Collection<VariantEffect> effects) {
		this.variant = variant;
		this.transcript = transcriptModel;
		this.effects = Sets.immutableEnumSet(effects);
		this.messages = EMPTY_ANNOTATION_MESSAGES;
	}

	/**
	 * Return the annotated variant.
	 */
	public SVGenomeVariant getVariant() {
		return variant;
	}

	/**
	 * Return the annotated transcript model.
	 */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/**
	 * Return the effects the variant is annotated with for the transcript.
	 */
	public ImmutableSet<VariantEffect> getEffects() {
		return effects;
	}

	/**
	 * @return highest {@link PutativeImpact} of all {@link #getEffects}.
	 */
	public PutativeImpact getPutativeImpact() {
		if (effects.isEmpty())
			return null;
		VariantEffect worst = effects.iterator().next();
		for (VariantEffect vt : effects)
			if (worst.getImpact().compareTo(vt.getImpact()) > 0)
				worst = vt;
		return worst.getImpact();
	}

	/**
	 * Return string for {@code SVANNO} entry of the {@code INFO} VCF field.
	 */
	public String toVCFSVAnnoString(boolean escape) {
		VCFSVAnnotationData data = new VCFSVAnnotationData();
		data.effects = effects;
		data.impact = getPutativeImpact();
		data.setTranscriptAndVariant(transcript, variant);
		data.isCoding = (transcript == null) ? false : transcript.isCoding();
		data.messages = messages;
		if (escape)
			return data.toString();
		else
			return data.toUnescapedString();
	}

	@Override
	public String toString() {
		return "SVAnnotation{" +
			"variant=" + variant +
			", transcript=" + transcript +
			", effects=" + effects +
			'}';
	}

	/**
	 * @return most pathogenic {@link VariantEffect}, {@code null} if none.
	 */
	public VariantEffect getMostPathogenicVariantEffect() {
		if (effects.isEmpty()) {
			return null;
		}
		return effects.iterator().next();
	}

	@Override
	public int compareTo(SVAnnotation other) {
		if (getMostPathogenicVariantEffect() == null && getMostPathogenicVariantEffect() == other.getMostPathogenicVariantEffect())
			return 0;
		else if (other.getMostPathogenicVariantEffect() == null)
			return -1;
		else if (getMostPathogenicVariantEffect() == null)
			return 1;

		int result = getMostPathogenicVariantEffect().ordinal() - other.getMostPathogenicVariantEffect().ordinal();
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

}
