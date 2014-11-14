package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomeInterval;
import jannovar.reference.TranscriptInfo;

/**
 * Helper class for the {@link BlockSubstitutionAnnotationBuilder}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class BlockSubstitutionAnnotationBuilderHelper extends AnnotationBuilderHelper {
	public BlockSubstitutionAnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		super(transcript, change);
	}

	@Override
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (so.containsExon(changeInterval)) // deletion of whole exon
			return buildFeatureAblationAnnotation();
		else if (so.overlapsWithTranslationalStartSite(changeInterval))
			return buildStartLossAnnotation();
		else if (so.overlapsWithCDSExon(changeInterval) && so.overlapsWithCDS(changeInterval))
			return buildCDSExonicAnnotation(); // can affect amino acids
		else if (so.overlapsWithCDSIntron(changeInterval) && so.overlapsWithCDS(changeInterval))
			return buildIntronicAnnotation(); // intron but no exon => intronic variant
		else if (so.overlapsWithFivePrimeUTR(changeInterval) || so.overlapsWithThreePrimeUTR(changeInterval))
			return buildUTRAnnotation();
		else if (so.overlapsWithUpstreamRegion(changeInterval) || so.overlapsWithDownstreamRegion(changeInterval))
			return buildUpOrDownstreamAnnotation();
		else
			return buildIntergenicAnnotation();
	}

	@Override
	String ncHGVS() {
		return String.format("%s:%sdelins%s", locAnno, dnaAnno, change.getAlt());
	}

	private Annotation buildFeatureAblationAnnotation() {
		return new Annotation(transcript.transcriptModel, ncHGVS(), VariantType.TRANSCRIPT_ABLATION);
	}

	private Annotation buildStartLossAnnotation() {
		return new Annotation(transcript.transcriptModel, String.format("%s:p.0?", ncHGVS()), VariantType.START_LOSS);
	}

	private Annotation buildCDSExonicAnnotation() {
		return null;
	}

}
