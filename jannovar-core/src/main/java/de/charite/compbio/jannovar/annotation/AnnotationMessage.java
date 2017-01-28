package de.charite.compbio.jannovar.annotation;

/**
 * Error messages for encoding problems during the annotation.
 */
public enum AnnotationMessage {
	/**
	 * (E1) Chromosome does not exists in reference genome database. Typically indicates a mismatch between the
	 * chromosome names in the input file and the chromosome names used in the reference genome.
	 */
	ERROR_CHROMOSOME_NOT_FOUND,
	/** (E2) The variant's genomic coordinate is greater than chromosome's length. */
	ERROR_OUT_OF_CHROMOSOME_RANGE,
	/**
	 * (Additional warning) This means that the "REF" field in the input VCF file does not match the reference genome. This warning
	 * may indicate a conflict between input data and data from reference genome (for instance is the input VCF was
	 * aligned to a different reference genome).
	 */
	WARNING_REF_DOES_NOT_MATCH_TRANSCRIPT,
	/**
	 * (W1) This means that the "REF" field in the input VCF file does not match the reference genome. This warning
	 * may indicate a conflict between input data and data from reference genome (for instance is the input VCF was
	 * aligned to a different reference genome).
	 */
	WARNING_REF_DOES_NOT_MATCH_GENOME,
	/**
	 * (W2) Reference sequence is not available, thus no inference could be performed.
	 */
	WARNING_SEQUENCE_NOT_AVAILABLE,
	/**
	 * (W3) A protein coding transcript having a non­multiple of 3 length. It indicates that the reference genome
	 * has missing information about this particular transcript.
	 */
	WARNING_TRANSCRIPT_INCOMPLETE,
	/**
	 * (W4) A protein coding transcript has two or more STOP codons in the middle of the coding sequence (CDS). This
	 * should not happen and it usually means the reference genome may have an error in this transcript.
	 */
	WARNING_TRANSCRIPT_MULTIPLE_STOP_CODONS,
	/**
	 * (W5) A protein coding transcript does not have a proper START codon. It is rare that a real transcript does
	 * not have a START codon, so this probably indicates an error or missing information in the reference genome.
	 */
	WARNING_TRANSCRIPT_NO_START_CODON,
	/**
	 * (W6) Variant has been realigned to the most 3­prime position within the transcript. This is usually done to
	 * to comply with HGVS specification to always report the most 3­prime annotation.
	 */
	INFO_REALIGN_3_PRIME,
	/**
	 * (W7) This effect is a result of combining more than one variants (e.g. two consecutive SNPs that conform an
	 * MNP, or two consecutive frame_shift variants that compensate frame).
	 */
	INFO_COMPOUND_ANNOTATION,
	/**
	 * (W8) An alternative reference sequence was used to calculate this annotation (e.g. cancer sample comparing
	 * somatic vs. germline).
	 */
	INFO_NON_REFERENCE_ANNOTATION,
	/**
	 * (non-standard) There was a problem with the annotation problem, if you see this in the output of the program then
	 * this indicates a bug.
	 */
	ERROR_PROBLEM_DURING_ANNOTATION,
	/** AnnotationMessage not encoded in enum. */
	OTHER_MESSAGE
}