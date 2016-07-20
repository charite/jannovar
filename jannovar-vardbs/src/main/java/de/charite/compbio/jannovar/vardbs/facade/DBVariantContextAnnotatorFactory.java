package de.charite.compbio.jannovar.vardbs.facade;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.dbsnp.DBSNPAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.exac.ExacAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.uk10k.UK10KAnnotationDriver;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Factory for generating {@link DBVariantContextAnnotator} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
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

	/**
	 * Construct ExAC {@link VariantContext} annotator factory.
	 * 
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed ExAC file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotaiton
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructExac(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new ExacAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

	/**
	 * Construct UK10K {@link VariantContext} annotator factory.
	 * 
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed ExAC file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotaiton
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructUK10K(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new UK10KAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

}
