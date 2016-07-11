package de.charite.compbio.jannovar.vardbs.facade;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.dbsnp.DBSNPAnnotationDriver;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Factory for generating {@link DBVariantContextAnnotator} objects
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class DBVariantContextAnnotatorFactory {

	/**
	 * Construct dbSNP {@link VariantContext} annotator factory.
	 * 
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed dbSNP file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotaiton
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructDBSNP(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new DBSNPAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

}
