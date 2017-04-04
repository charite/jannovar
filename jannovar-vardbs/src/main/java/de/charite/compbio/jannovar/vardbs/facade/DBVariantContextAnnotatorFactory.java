package de.charite.compbio.jannovar.vardbs.facade;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.clinvar.ClinVarAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.cosmic.CosmicAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.dbsnp.DBSNPAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.exac.ExacAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.gnomad.GnomadAnnotationDriver;
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
	 *            Configuration for the variant context annotation
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructExac(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new ExacAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

	/**
	 * Construct gnomAD {@link VariantContext} annotator factory.
	 * 
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed gnomAD file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotation
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructGnomad(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new GnomadAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

	/**
	 * Construct UK10K {@link VariantContext} annotator factory.
	 * 
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed UK10K file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotation
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructUK10K(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new CosmicAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

	/**
	 * Construct ClinVar {@link VariantContext} annotator factory.
	 *
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed ClinVar file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotation
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructClinVar(String vcfDBPath, String fastaRefPath,
			DBAnnotationOptions options) throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new ClinVarAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

	/**
	 * Construct Cosmic {@link VariantContext} annotator factory.
	 *
	 * @param vcfDBPath
	 *            Path to gzip-compressed, normalized and tbi-indexed Cosmic file to use for the annotation
	 * @param fastaRefPath
	 *            Path to reference FASTA file
	 * @param options
	 *            Configuration for the variant context annotation
	 * @return Preconfigured {@link DBVariantContextAnnotator} object
	 * @throws JannovarVarDBException
	 *             on problems loading the resources
	 */
	public DBVariantContextAnnotator constructCosmic(String vcfDBPath, String fastaRefPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		return new DBVariantContextAnnotator(new CosmicAnnotationDriver(vcfDBPath, fastaRefPath, options), options);
	}

}
