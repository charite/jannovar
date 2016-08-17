package de.charite.compbio.jannovar.annotation.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Dispatches annotation building to the specific classes, depending on their {@link GenomeVariant#getType}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class AnnotationBuilderDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationBuilderDispatcher.class);

	/** transcript to build annotation for */
	private final TranscriptModel transcript;
	/** genomic change to build annotation for */
	private final GenomeVariant change;
	/** configuration to use */
	private final AnnotationBuilderOptions options;

	public AnnotationBuilderDispatcher(TranscriptModel transcript, GenomeVariant change,
			AnnotationBuilderOptions options) {
		this.transcript = transcript;
		this.change = change;
		this.options = options;
	}

	/**
	 * @return {@link Annotation} for {@link #transcript} and {@link #change}
	 *
	 * @throws InvalidGenomeVariant
	 *             if there is a problem with {@link #change}
	 */
	public Annotation build() throws InvalidGenomeVariant {
		if (transcript == null)
			return new Annotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT), null,
					new GenomicNucleotideChangeBuilder(change).build(), null, null);

		switch (change.getType()) {
		case SNV:
			LOGGER.debug("Annotating SNV {}", new Object[] { change });
			return new SNVAnnotationBuilder(transcript, change, options).build();
		case DELETION:
			LOGGER.debug("Annotating deletion {}", new Object[] { change });
			return new DeletionAnnotationBuilder(transcript, change, options).build();
		case INSERTION:
			LOGGER.debug("Annotating insertion {}", new Object[] { change });
			return new InsertionAnnotationBuilder(transcript, change, options).build();
		case BLOCK_SUBSTITUTION:
		default:
			LOGGER.debug("Annotating block substitution {}", new Object[] { change });
			return new BlockSubstitutionAnnotationBuilder(transcript, change, options).build();
		}
	}

}
