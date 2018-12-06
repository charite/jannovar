package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatches annotation building to the specific classes, depending on their {@link GenomeVariant#getType}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class AnnotationBuilderDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationBuilderDispatcher.class);
	private static final ImmutableSet<VariantEffect> INTERGENIC_VARIANT = Sets.immutableEnumSet(EnumSet.of(VariantEffect.INTERGENIC_VARIANT));

	/** transcript to build annotation for */
	private final TranscriptModel transcript;
	/** genomic change to build annotation for */
	private final GenomeVariant change;
	/** configuration to use */
	private final AnnotationBuilderOptions options;

	public AnnotationBuilderDispatcher(TranscriptModel transcript, GenomeVariant change, AnnotationBuilderOptions options) {
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
			return new Annotation(null, change, INTERGENIC_VARIANT, null,
					new GenomicNucleotideChangeBuilder(change).build(), null, null);

		switch (change.getType()) {
		case SNV:
			LOGGER.debug("Annotating SNV {}", change);
			return new SNVAnnotationBuilder(transcript, change, options).build();
		case DELETION:
			LOGGER.debug("Annotating deletion {}", change);
			return new DeletionAnnotationBuilder(transcript, change, options).build();
		case INSERTION:
			LOGGER.debug("Annotating insertion {}", change);
			return new InsertionAnnotationBuilder(transcript, change, options).build();
		case BLOCK_SUBSTITUTION:
		default:
			LOGGER.debug("Annotating block substitution {}", change);
			return new BlockSubstitutionAnnotationBuilder(transcript, change, options).build();
		}
	}

}
