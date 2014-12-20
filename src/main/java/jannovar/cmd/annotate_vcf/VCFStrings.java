package jannovar.cmd.annotate_vcf;

/**
 * Constants for writing out to VCF file.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class VCFStrings {

	/**
	 * This line is added to the output of a VCF file annotated by Jannovar and describes the new field for the INFO
	 * section entitled EFFECT, which decribes the effects of variants (splicing,missense,stoploss, etc).
	 */
	public static final String INFO_EFFECT = ""
			+ "variant effect (UTR5,UTR3,intronic,splicing,missense,stoploss,stopgain,"
			+ "startloss,duplication,frameshift-insertion,frameshift-deletion,non-frameshift-deletion,"
			+ "non-frameshift-insertion,synonymous)";

	/**
	 * This line is added to the output of a VCF file annotated by Jannovar and describes the new field for the INFO
	 * section entitled HGVS, which provides the HGVS encoded variant corresponding to the chromosomal variant in the
	 * original VCF file.
	 */
	public static final String INFO_HGVS = "HGVS Nomenclature";

}
