package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeChange;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Dispatches annotation building to the specific classes, depending on their {@link GenomeChange#getType}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class AnnotationBuilderDispatcher {

	/** transcript to build annotation for */
	public final TranscriptModel transcript;
	/** genomic change to build annotation for */
	public final GenomeChange change;
	/** configuration to use */
	public final AnnotationBuilderOptions options;

	public AnnotationBuilderDispatcher(TranscriptModel transcript, GenomeChange change,
			AnnotationBuilderOptions options) {
		this.transcript = transcript;
		this.change = change.withPositionType(PositionType.ZERO_BASED);
		this.options = options;
	}

	/**
	 * @return {@link Annotation} for {@link #transcript} and {@link #change}
	 *
	 * @throws InvalidGenomeChange
	 *             if there is a problem with {@link #change}
	 */
	public Annotation build() throws InvalidGenomeChange {
		if (transcript == null)
			return new Annotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT), null, null, null);

		switch (change.getType()) {
		case SNV:
			return new SNVAnnotationBuilder(transcript, change, options).build();
		case DELETION:
			return new DeletionAnnotationBuilder(transcript, change, options).build();
		case INSERTION:
			return new InsertionAnnotationBuilder(transcript, change, options).build();
		case BLOCK_SUBSTITUTION:
		default:
			return new BlockSubstitutionAnnotationBuilder(transcript, change, options).build();
		}
	}

}
