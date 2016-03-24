package de.charite.compbio.jannovar.impl.parse.gff;

/**
 * This are the currently known feature types in GFF/GFT3 files.
 *
 * @see <a href="http://www.sequenceontology.org/gff3.shtml">http://www.sequenceontology.org/gff3.shtml</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
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
	RRNA,
	/** operon */
	OPERON,
	/** promotor */
	PROMOTOR,
	/** tf binding site */
	TF_BINDING_SITE,
	/** intron */
	INTRON,
	/** three prime utr */
	THREE_PRIME_UTR,
	/** five prime utr */
	FIVE_PRIME_UTR;


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
		case OPERON:
			return "operon";
		case PROMOTOR:
			return "promoter";
		case TF_BINDING_SITE:
			return "TF_binding_site";
		case INTRON:
			return "intron";
		case THREE_PRIME_UTR:
			return "three_prime_UTR";
		case FIVE_PRIME_UTR:
			return "five_prime_UTR";
		default:
			return ".";
		}
	}
}
