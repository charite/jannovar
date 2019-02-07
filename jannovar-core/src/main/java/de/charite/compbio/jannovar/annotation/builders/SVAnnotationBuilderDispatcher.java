package de.charite.compbio.jannovar.annotation.builders;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

/**
 * Dispatches annotation building for SVs to the specific classes, depending on their {@link SVGenomeVariant#getType}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class SVAnnotationBuilderDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(SVAnnotationBuilderDispatcher.class);

	/**
	 * transcript to build annotation for.
	 */
	private final TranscriptModel transcript;

	/**
	 * Genomic SV to build annotation for.
	 */
	private final SVGenomeVariant sv;

	public SVAnnotationBuilderDispatcher(TranscriptModel transcript, SVGenomeVariant sv) {
		this.transcript = transcript;
		this.sv = sv;
	}

	/**
	 * @return {@link Annotation} for {@link #transcript} and {@link #sv}
	 */
	public SVAnnotation build() {
		if (transcript == null)
			return new SVAnnotation(sv, null, EnumSet.noneOf(VariantEffect.class));

		switch (sv.getType()) {
			case DEL:
				LOGGER.debug("Annotating SV deletion");
				return new SVDeletionAnnotationBuilder(transcript, (SVDeletion) sv).build();
			case DEL_ME:
				LOGGER.debug("Annotating SV mobile element deletion");
				return new SVMobileElementDeletionAnnotationBuilder(transcript, (SVMobileElementDeletion) sv).build();
			case INS:
				LOGGER.debug("Annotating SV insertion");
				return new SVInsertionAnnotationBuilder(transcript, (SVInsertion) sv).build();
			case INS_ME:
				LOGGER.debug("Annotating SV mobile element insertion");
				return new SVMobileElementInsertionAnnotationBuilder(transcript, (SVMobileElementInsertion) sv).build();
			case DUP:
				LOGGER.debug("Annotating SV duplication");
				return new SVDuplicationAnnotationBuilder(transcript, (SVDuplication) sv).build();
			case DUP_TANDEM:
				LOGGER.debug("Annotating SV tandem duplication");
				return new SVTandemDuplicationAnnotationBuilder(transcript, (SVTandemDuplication) sv).build();
			case INV:
				LOGGER.debug("Annotating SV inversion");
				return new SVInversionAnnotationBuilder(transcript, (SVInversion) sv).build();
			case CNV:
				LOGGER.debug("Annotating SV copy number variation");
				return new SVCopyNumberVariantAnnotationBuilder(transcript, (SVCopyNumberVariant) sv).build();
			case BND:
				LOGGER.debug("Annotating SV breakend / translocation");
				return new SVBreakendAnnotationBuilder(transcript, (SVBreakend) sv).build();
			case UNKNOWN:
			default:
				LOGGER.debug("Returning dummy annotation for unknown SV");
				return new SVAnnotation(sv, transcript, EnumSet.noneOf(VariantEffect.class));
		}
	}

}
