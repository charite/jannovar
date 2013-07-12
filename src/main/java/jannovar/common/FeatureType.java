/**
 * 
 */
package jannovar.common;

/**
 * This are the up to now known Feature types in GFF/GFT3 files
 * @author mjaeger
 * @version 0.1
 */
public enum FeatureType {
	
	UNKNOWN,
	CDS,
	EXON,
	MRNA,
	TRANSCRIPT,
	REGION,
	GENE,
	START_CODON,
	STOP_CODON,
	NCRNA,
	TRNA,
	RRNA;
	
	public static String toString(FeatureType type){
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
