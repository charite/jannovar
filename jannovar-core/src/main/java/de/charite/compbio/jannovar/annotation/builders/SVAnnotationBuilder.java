package de.charite.compbio.jannovar.annotation.builders;

import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.reference.SVGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptSequenceOntologyDecorator;

/**
 * Base class for building annotations for structural variants.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
abstract class SVAnnotationBuilder {

	/**
	 * The {@link TranscriptModel} to build the annotation for.
	 */
	protected final TranscriptModel transcript;

	/**
	 * The {@link SVGenomeVariant} to build the annotation for.  The constructor will force the it to be on the same
	 * strand as {@link #transcript}.
	 */
	protected final SVGenomeVariant variant;

	/**
	 * helper for sequence ontology terms
	 */
	protected final TranscriptSequenceOntologyDecorator so;

	/**
	 * Initialize all fields.
	 *
	 * @param transcript The {@link TranscriptModel} to create the annotation for.
	 * @param variant    The {@link SVGenomeVariant} to build the annotation for.
	 */
	public SVAnnotationBuilder(TranscriptModel transcript, SVGenomeVariant variant) {
		this.transcript = transcript;
		this.variant = variant.withStrand(transcript.getStrand());
		this.so = new TranscriptSequenceOntologyDecorator(transcript);
	}

	/**
	 * Perform the annotation process.
	 *
	 * @return The {@link SVAnnotation} with the annotation result.
	 */
	public abstract SVAnnotation build();

}
