package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.TranscriptInfo;

/**
 * Helper class that allows to remove various copy and paste code in {@link InsertionAnnotationBuilder}.
 */
class InsertionAnnotationBuilderHelper extends AnnotationBuilderHelper {

	InsertionAnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		super(transcript, change);
	}

	@Override
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		final GenomePosition changePos = change.getPos();
		if (so.liesInTranslationalStartSite(changePos))
			return buildStartLossAnnotation();
		else if (so.liesInCDSExon(changePos) && so.liesInCDS(changePos))
			return buildCDSExonicAnnotation(); // can affect amino acids
		else if (so.liesInCDSIntron(changePos) && so.liesInCDS(changePos))
			return buildIntronicAnnotation(); // intron but no exon => intronic variant
		else if (so.liesInFivePrimeUTR(changePos) || so.liesInThreePrimeUTR(changePos))
			return buildUTRAnnotation();
		else if (so.liesInUpstreamRegion(changePos) || so.liesInDownstreamRegion(changePos))
			return buildUpOrDownstreamAnnotation();
		else
			return buildIntergenicAnnotation();
	}

	@Override
	String ncHGVS() {
		return String.format("%s:%sins%s", locAnno, dnaAnno, change.getAlt());
	}

	private Annotation buildCDSExonicAnnotation() {
		return null;
	}

	private Annotation buildStartLossAnnotation() {
		return null;
	}

}