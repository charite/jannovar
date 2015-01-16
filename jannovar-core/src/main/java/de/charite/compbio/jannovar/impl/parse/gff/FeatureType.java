package de.charite.compbio.jannovar.impl.parse.gff;

/**
 * This are the currently known feature types in GFF/GFT3 files.
 *
 * @see <a href="http://www.sequenceontology.org/gff3.shtml">http://www.sequenceontology.org/gff3.shtml</a>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public enum FeatureType {

	/** unknown feature type */
	UNKNOWN,
	/** coding sequence */
	CDS,
	/** exon */
	EXON,
	/** messenger RNA */
	MRNA,
	/** transcript */
	TRANSCRIPT,
	/** region */
	REGION,
	/** gene */
	GENE,
	/** start codon */
	START_CODON,
	/** stop codon */
	STOP_CODON,
	/** non-coding RNA */
	NCRNA,
	/** transfer RNA */
	TRNA,
	/** ribosomal RNA */
	RRNA;

	public static String toString(FeatureType type) {
		switch (type) {
		case STOP_CODON:
			return "stop_codon";
		case START_CODON:
			return "start_codon";
		case GENE:
			return "gene";
		case MRNA:
			return "mRNA";
		case REGION:
			return "region";
		case TRANSCRIPT:
			return "transcript";
		case EXON:
			return "exon";
		case CDS:
			return "CDS";
		case NCRNA:
			return "ncRNA";
		case TRNA:
			return "tRNA";
		case RRNA:
			return "rRNA";
		default:
			return ".";
		}
	}
}
