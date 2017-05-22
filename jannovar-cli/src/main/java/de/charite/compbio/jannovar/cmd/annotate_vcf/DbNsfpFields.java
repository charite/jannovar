package de.charite.compbio.jannovar.cmd.annotate_vcf;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.vardbs.generic_tsv.GenericTSVAccumulationStrategy;
import de.charite.compbio.jannovar.vardbs.generic_tsv.GenericTSVValueColumnDescription;
import htsjdk.variant.vcf.VCFHeaderLineType;

/**
 * Configuration for annotation with dbNSFP (v3.4).
 *
 * <p>
 * Defines preconfigured {@link GenericTSVValueColumnDescription}
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DbNsfpFields {

	// TODO: implement accumulation in list instead of choose first/min/max only or special
	// handling?

	public static final ImmutableMap<String, GenericTSVValueColumnDescription> DBNSFP_FIELDS;

	static {
		ImmutableMap.Builder<String,
				GenericTSVValueColumnDescription> builder = ImmutableMap.builder();

		builder.put("hg38_chr",
				new GenericTSVValueColumnDescription(1, VCFHeaderLineType.String, "hg38_chr",
						"Value of dbNSFP column 'chr'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("hg38_pos",
				new GenericTSVValueColumnDescription(2, VCFHeaderLineType.Integer, "hg38_pos",
						"Value of dbNSFP column 'pos'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("ref",
				new GenericTSVValueColumnDescription(3, VCFHeaderLineType.Character, "ref",
						"Value of dbNSFP column 'ref'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("alt",
				new GenericTSVValueColumnDescription(4, VCFHeaderLineType.Character, "alt",
						"Value of dbNSFP column 'alt'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("aaref",
				new GenericTSVValueColumnDescription(5, VCFHeaderLineType.Character, "aaref",
						"Value of dbNSFP column 'aaref'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("aaalt",
				new GenericTSVValueColumnDescription(6, VCFHeaderLineType.Character, "aaalt",
						"Value of dbNSFP column 'aaalt'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("rs_dbSNP147",
				new GenericTSVValueColumnDescription(7, VCFHeaderLineType.String, "rs_dbSNP147",
						"Value of dbNSFP column 'rs_dbSNP147'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("hg19_chr",
				new GenericTSVValueColumnDescription(8, VCFHeaderLineType.String, "hg19_chr",
						"Value of dbNSFP column 'chr'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("hg19_pos",
				new GenericTSVValueColumnDescription(9, VCFHeaderLineType.Integer, "hg19_pos",
						"Value of dbNSFP column 'hg19_pos'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("hg18_chr",
				new GenericTSVValueColumnDescription(10, VCFHeaderLineType.String, "hg18_chr",
						"Value of dbNSFP column 'hg18_chr'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("hg18_pos",
				new GenericTSVValueColumnDescription(11, VCFHeaderLineType.Integer, "hg18_pos",
						"Value of dbNSFP column 'hg18_pos'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("genename",
				new GenericTSVValueColumnDescription(12, VCFHeaderLineType.String, "genename",
						"Value of dbNSFP column 'genename'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("cds_strand",
				new GenericTSVValueColumnDescription(13, VCFHeaderLineType.Character, "cds_strand",
						"Value of dbNSFP column 'cds_strand'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("refcodon",
				new GenericTSVValueColumnDescription(14, VCFHeaderLineType.String, "refcodon",
						"Value of dbNSFP column 'refcodon'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("codonpos",
				new GenericTSVValueColumnDescription(15, VCFHeaderLineType.Integer, "codonpos",
						"Value of dbNSFP column 'codonpos'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("codon_degeneracy",
				new GenericTSVValueColumnDescription(16, VCFHeaderLineType.Integer,
						"codon_degeneracy", "Value of dbNSFP column 'codon_degeneracy'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("Ancestral_allele",
				new GenericTSVValueColumnDescription(17, VCFHeaderLineType.String,
						"Ancestral_allele", "Value of dbNSFP column 'Ancestral_allele'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("AltaiNeandertal",
				new GenericTSVValueColumnDescription(18, VCFHeaderLineType.String,
						"AltaiNeandertal", "Value of dbNSFP column 'AltaiNeandertal'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("Denisova",
				new GenericTSVValueColumnDescription(19, VCFHeaderLineType.String, "Denisova",
						"Value of dbNSFP column 'Denisova'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("Ensembl_geneid",
				new GenericTSVValueColumnDescription(20, VCFHeaderLineType.String, "Ensembl_geneid",
						"Value of dbNSFP column 'Ensembl_geneid'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("Ensembl_transcriptid",
				new GenericTSVValueColumnDescription(21, VCFHeaderLineType.String,
						"Ensembl_transcriptid", "Value of dbNSFP column 'Ensembl_transcriptid'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("Ensembl_proteinid",
				new GenericTSVValueColumnDescription(22, VCFHeaderLineType.String,
						"Ensembl_proteinid", "Value of dbNSFP column 'Ensembl_proteinid'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		builder.put("aapos",
				new GenericTSVValueColumnDescription(23, VCFHeaderLineType.Integer, "aapos",
						"Value of dbNSFP column 'aapos'",
						GenericTSVAccumulationStrategy.CHOOSE_FIRST));

		builder.put("SIFT_score",
				new GenericTSVValueColumnDescription(24, VCFHeaderLineType.Float, "SIFT_score",
						"Value of dbNSFP column 'SIFT_score', If a score is smaller than 0.05 the "
								+ "corresponding NS is predicted as \"D(amaging)\" therwise it is "
								+ "predicted as \"T(olerated)\".",
						GenericTSVAccumulationStrategy.CHOOSE_MIN, "SIFT_score"));
		builder.put("SIFT_converted_rankscore",
				new GenericTSVValueColumnDescription(25, VCFHeaderLineType.Float,
						"SIFT_converted_rankscore",
						"Value of dbNSFP column 'SIFT_converted_rankscore', SIFTnew=1-SIFTori. "
								+ "The larger the more damaging.",
						GenericTSVAccumulationStrategy.CHOOSE_MIN, "SIFT_score"));
		builder.put("SIFT_pred",
				new GenericTSVValueColumnDescription(26, VCFHeaderLineType.Character, "SIFT_pred",
						"Value of dbNSFP column 'SIFT_pred' (Damaging/Tolerated). If SIFTori is smaller "
								+ "than 0.05 (SIFTnew>0.95) the corresponding NS is predicted as "
								+ "\"D(amaging)\"; otherwise it is predicted as \"T(olerated)\".",
						GenericTSVAccumulationStrategy.CHOOSE_MIN, "SIFT_score"));

		builder.put("Uniprot_acc_Polyphen2",
				new GenericTSVValueColumnDescription(27, VCFHeaderLineType.String,
						"Uniprot_acc_Polyphen2", "Value of dbNSFP column 'Uniprot_acc_Polyphen2'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Uniprot_id_Polyphen2",
				new GenericTSVValueColumnDescription(28, VCFHeaderLineType.String,
						"Uniprot_id_Polyphen2", "Value of dbNSFP column 'Uniprot_id_Polyphen2'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Uniprot_aapos_Polyphen2",
				new GenericTSVValueColumnDescription(29, VCFHeaderLineType.Integer,
						"Uniprot_aapos_Polyphen2",
						"Value of dbNSFP column 'Uniprot_aapos_Polyphen2'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Polyphen2_HDIV_score",
				new GenericTSVValueColumnDescription(30, VCFHeaderLineType.Integer,
						"Polyphen2_HDIV_score", "Value of dbNSFP column 'Polyphen2_HDIV_score'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Polyphen2_HDIV_rankscore",
				new GenericTSVValueColumnDescription(31, VCFHeaderLineType.Integer,
						"Polyphen2_HDIV_rankscore",
						"Value of dbNSFP column 'Polyphen2_HDIV_rankscore'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Polyphen2_HDIV_pred",
				new GenericTSVValueColumnDescription(32, VCFHeaderLineType.Integer,
						"Polyphen2_HDIV_pred", "Value of dbNSFP column 'Polyphen2_HDIV_pred'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Polyphen2_HVAR_score",
				new GenericTSVValueColumnDescription(33, VCFHeaderLineType.Integer,
						"Polyphen2_HVAR_score", "Value of dbNSFP column 'Polyphen2_HVAR_score'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Polyphen2_HVAR_rankscore",
				new GenericTSVValueColumnDescription(34, VCFHeaderLineType.Integer,
						"Polyphen2_HVAR_rankscore",
						"Value of dbNSFP column 'Polyphen2_HVAR_rankscore'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));
		builder.put("Polyphen2_HVAR_pred",
				new GenericTSVValueColumnDescription(35, VCFHeaderLineType.Integer,
						"Polyphen2_HVAR_pred", "Value of dbNSFP column 'Polyphen2_HVAR_pred'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "Polyphen2_HDIV_score"));

		builder.put("LRT_score",
				new GenericTSVValueColumnDescription(36, VCFHeaderLineType.Integer, "LRT_score",
						"Value of dbNSFP column 'LRT_score', first in case of multiple",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "LRT_score"));
		builder.put("LRT_converted_rankscore",
				new GenericTSVValueColumnDescription(37, VCFHeaderLineType.Integer,
						"LRT_converted_rankscore",
						"Value of dbNSFP column 'LRT_converted_rankscore', first in case of multiple",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "LRT_score"));
		builder.put("LRT_pred",
				new GenericTSVValueColumnDescription(38, VCFHeaderLineType.Integer, "LRT_pred",
						"Value of dbNSFP column 'LRT_pred', first in case of multiple",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "LRT_score"));
		builder.put("LRT_Omega",
				new GenericTSVValueColumnDescription(39, VCFHeaderLineType.Integer, "LRT_Omega",
						"Value of dbNSFP column 'LRT_Omega', first in case of multiple",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "LRT_score"));

		builder.put("MutationTaster_score",
				new GenericTSVValueColumnDescription(40, VCFHeaderLineType.Float,
						"MutationTaster_score",
						"Value of dbNSFP column 'MutationTaster_score', lower is more pathogenic",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationTaster_score"));
		builder.put("MutationTaster_converted_rankscore",
				new GenericTSVValueColumnDescription(41, VCFHeaderLineType.Float,
						"MutationTaster_converted_rankscore",
						"Value of dbNSFP column 'MutationTaster_converted_rankscore', lower is more pathogenic",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationTaster_score"));
		builder.put("MutationTaster_pred",
				new GenericTSVValueColumnDescription(42, VCFHeaderLineType.Character,
						"MutationTaster_pred", "Value of dbNSFP column 'MutationTaster_pred'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationTaster_score"));
		builder.put("MutationTaster_model",
				new GenericTSVValueColumnDescription(43, VCFHeaderLineType.String,
						"MutationTaster_model", "Value of dbNSFP column 'MutationTaster_model'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationTaster_score"));
		builder.put("MutationTaster_AAE",
				new GenericTSVValueColumnDescription(44, VCFHeaderLineType.String,
						"MutationTaster_AAE", "Value of dbNSFP column 'MutationTaster_AAE'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationTaster_score"));

		builder.put("MutationAssessor_UniprotID",
				new GenericTSVValueColumnDescription(45, VCFHeaderLineType.String,
						"MutationAssessor_UniprotID",
						"Value of dbNSFP column 'MutationAssessor_UniprotID'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationAssessor_score"));
		builder.put("MutationAssessor_variant",
				new GenericTSVValueColumnDescription(46, VCFHeaderLineType.String,
						"MutationAssessor_variant",
						"Value of dbNSFP column 'MutationAssessor_variant'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationAssessor_score"));
		builder.put("MutationAssessor_score",
				new GenericTSVValueColumnDescription(47, VCFHeaderLineType.Float,
						"MutationAssessor_score", "Value of dbNSFP column 'MutationAssessor_score'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationAssessor_score"));
		builder.put("MutationAssessor_score_rankscore",
				new GenericTSVValueColumnDescription(48, VCFHeaderLineType.Float,
						"MutationAssessor_score_rankscore",
						"Value of dbNSFP column 'MutationAssessor_score_rankscore'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationAssessor_score"));
		builder.put("MutationAssessor_pred",
				new GenericTSVValueColumnDescription(49, VCFHeaderLineType.String,
						"MutationAssessor_pred", "Value of dbNSFP column 'MutationAssessor_pred'",
						GenericTSVAccumulationStrategy.CHOOSE_MAX, "MutationAssessor_score"));

		// TODO: add remaining ~150 columns

		DBNSFP_FIELDS = builder.build();
	}
	public static String hg38_chr = "chr";
	public static String hg38_pos = "pos(1-based)";
	public static String ref = "ref";
	public static String alt = "alt";
	public static String aaref = "aaref";
	public static String aaalt = "aaalt";
	public static String rs_dbSNP147 = "rs_dbSNP147";
	public static String hg19_chr = "hg19_chr";
	public static String hg19_pos = "hg19_pos(1-based)";
	public static String hg18_chr = "hg18_chr";
	public static String hg18_pos = "hg18_pos(1-based)";
	public static String genename = "genename";
	public static String cds_strand = "cds_strand";
	public static String refcodon = "refcodon";
	public static String codonpos = "codonpos";
	public static String codon_degeneracy = "codon_degeneracy";
	public static String Ancestral_allele = "Ancestral_allele";
	public static String AltaiNeandertal = "AltaiNeandertal";
	public static String Denisova = "Denisova";
	public static String Ensembl_geneid = "Ensembl_geneid";
	public static String Ensembl_transcriptid = "Ensembl_transcriptid";
	public static String Ensembl_proteinid = "Ensembl_proteinid";
	public static String aapos = "aapos";
	public static String SIFT_score = "SIFT_score";
	public static String SIFT_converted_rankscore = "SIFT_converted_rankscore";
	public static String SIFT_pred = "SIFT_pred";
	public static String Uniprot_acc_Polyphen2 = "Uniprot_acc_Polyphen2";
	public static String Uniprot_id_Polyphen2 = "Uniprot_id_Polyphen2";
	public static String Uniprot_aapos_Polyphen2 = "Uniprot_aapos_Polyphen2";
	public static String Polyphen2_HDIV_score = "Polyphen2_HDIV_score";
	public static String Polyphen2_HDIV_rankscore = "Polyphen2_HDIV_rankscore";
	public static String Polyphen2_HDIV_pred = "Polyphen2_HDIV_pred";
	public static String Polyphen2_HVAR_score = "Polyphen2_HVAR_score";
	public static String Polyphen2_HVAR_rankscore = "Polyphen2_HVAR_rankscore";
	public static String Polyphen2_HVAR_pred = "Polyphen2_HVAR_pred";
	public static String LRT_score = "LRT_score";
	public static String LRT_converted_rankscore = "LRT_converted_rankscore";
	public static String LRT_pred = "LRT_pred";
	public static String LRT_Omega = "LRT_Omega";
	public static String MutationTaster_score = "MutationTaster_score";
	public static String MutationTaster_converted_rankscore = "MutationTaster_converted_rankscore";
	public static String MutationTaster_pred = "MutationTaster_pred";
	public static String MutationTaster_model = "MutationTaster_model";
	public static String MutationTaster_AAE = "MutationTaster_AAE";
	public static String MutationAssessor_UniprotID = "MutationAssessor_UniprotID";
	public static String MutationAssessor_variant = "MutationAssessor_variant";
	public static String MutationAssessor_score = "MutationAssessor_score";
	public static String MutationAssessor_score_rankscore = "MutationAssessor_score_rankscore";
	public static String MutationAssessor_pred = "MutationAssessor_pred";
	public static String FATHMM_score = "FATHMM_score";
	public static String FATHMM_converted_rankscore = "FATHMM_converted_rankscore";
	public static String FATHMM_pred = "FATHMM_pred";
	public static String PROVEAN_score = "PROVEAN_score";
	public static String PROVEAN_converted_rankscore = "PROVEAN_converted_rankscore";
	public static String PROVEAN_pred = "PROVEAN_pred";
	public static String Transcript_id_VEST3 = "Transcript_id_VEST3";
	public static String Transcript_var_VEST3 = "Transcript_var_VEST3";
	public static String VEST3_score = "VEST3_score";
	public static String VEST3_rankscore = "VEST3_rankscore";
	public static String MetaSVM_score = "MetaSVM_score";
	public static String MetaSVM_rankscore = "MetaSVM_rankscore";
	public static String MetaSVM_pred = "MetaSVM_pred";
	public static String MetaLR_score = "MetaLR_score";
	public static String MetaLR_rankscore = "MetaLR_rankscore";
	public static String MetaLR_pred = "MetaLR_pred";
	public static String Reliability_index = "Reliability_index";
	public static String M_CAP_score = "M-CAP_score";
	public static String M_CAP_rankscore = "M-CAP_rankscore";
	public static String M_CAP_pred = "M-CAP_pred";
	public static String REVEL_score = "REVEL_score";
	public static String REVEL_rankscore = "REVEL_rankscore";
	public static String MutPred_score = "MutPred_score";
	public static String MutPred_rankscore = "MutPred_rankscore";
	public static String MutPred_protID = "MutPred_protID";
	public static String MutPred_AAchange = "MutPred_AAchange";
	public static String MutPred_Top5features = "MutPred_Top5features";
	public static String CADD_raw = "CADD_raw";
	public static String CADD_raw_rankscore = "CADD_raw_rankscore";
	public static String CADD_phred = "CADD_phred";
	public static String DANN_score = "DANN_score";
	public static String DANN_rankscore = "DANN_rankscore";
	public static String fathmm_MKL_coding_score = "fathmm-MKL_coding_score";
	public static String fathmm_MKL_coding_rankscore = "fathmm-MKL_coding_rankscore";
	public static String fathmm_MKL_coding_pred = "fathmm-MKL_coding_pred";
	public static String fathmm_MKL_coding_group = "fathmm-MKL_coding_group";
	public static String Eigen_coding_or_noncoding = "Eigen_coding_or_noncoding";
	public static String Eigen_raw = "Eigen-raw";
	public static String Eigen_phred = "Eigen-phred";
	public static String Eigen_PC_raw = "Eigen-PC-raw";
	public static String Eigen_PC_phred = "Eigen-PC-phred";
	public static String Eigen_PC_raw_rankscore = "Eigen-PC-raw_rankscore";
	public static String GenoCanyon_score = "GenoCanyon_score";
	public static String GenoCanyon_score_rankscore = "GenoCanyon_score_rankscore";
	public static String integrated_fitCons_score = "integrated_fitCons_score";
	public static String integrated_fitCons_score_rankscore = "integrated_fitCons_score_rankscore";
	public static String integrated_confidence_value = "integrated_confidence_value";
	public static String GM12878_fitCons_score = "GM12878_fitCons_score";
	public static String GM12878_fitCons_score_rankscore = "GM12878_fitCons_score_rankscore";
	public static String GM12878_confidence_value = "GM12878_confidence_value";
	public static String H1_hESC_fitCons_score = "H1-hESC_fitCons_score";
	public static String H1_hESC_fitCons_score_rankscore = "H1-hESC_fitCons_score_rankscore";
	public static String H1_hESC_confidence_value = "H1-hESC_confidence_value";
	public static String HUVEC_fitCons_score = "HUVEC_fitCons_score";
	public static String HUVEC_fitCons_score_rankscore = "HUVEC_fitCons_score_rankscore";
	public static String HUVEC_confidence_value = "HUVEC_confidence_value";
	public static String GERPpp_NR = "GERP++_NR";
	public static String GERPpp_RS = "GERP++_RS";
	public static String GERPpp_RS_rankscore = "GERP++_RS_rankscore";
	public static String phyloP100way_vertebrate = "phyloP100way_vertebrate";
	public static String phyloP100way_vertebrate_rankscore = "phyloP100way_vertebrate_rankscore";
	public static String phyloP20way_mammalian = "phyloP20way_mammalian";
	public static String phyloP20way_mammalian_rankscore = "phyloP20way_mammalian_rankscore";
	public static String phastCons100way_vertebrate = "phastCons100way_vertebrate";
	public static String phastCons100way_vertebrate_rankscore = "phastCons100way_vertebrate_rankscore";
	public static String phastCons20way_mammalian = "phastCons20way_mammalian";
	public static String phastCons20way_mammalian_rankscore = "phastCons20way_mammalian_rankscore";
	public static String SiPhy_29way_pi = "SiPhy_29way_pi";
	public static String SiPhy_29way_logOdds = "SiPhy_29way_logOdds";
	public static String SiPhy_29way_logOdds_rankscore = "SiPhy_29way_logOdds_rankscore";
	public static String G1000_Gp3_AC = "1000Gp3_AC";
	public static String G1000_Gp3_AF = "1000Gp3_AF";
	public static String G1000_Gp3_AFR_AC = "1000Gp3_AFR_AC";
	public static String G1000_Gp3_AFR_AF = "1000Gp3_AFR_AF";
	public static String G1000_Gp3_EUR_AC = "1000Gp3_EUR_AC";
	public static String G1000_Gp3_EUR_AF = "1000Gp3_EUR_AF";
	public static String G1000_Gp3_AMR_AC = "1000Gp3_AMR_AC";
	public static String G1000_Gp3_AMR_AF = "1000Gp3_AMR_AF";
	public static String G1000_Gp3_EAS_AC = "1000Gp3_EAS_AC";
	public static String G1000_Gp3_EAS_AF = "1000Gp3_EAS_AF";
	public static String G1000_Gp3_SAS_AC = "1000Gp3_SAS_AC";
	public static String G1000_Gp3_SAS_AF = "1000Gp3_SAS_AF";
	public static String TWINSUK_AC = "TWINSUK_AC";
	public static String TWINSUK_AF = "TWINSUK_AF";
	public static String ALSPAC_AC = "ALSPAC_AC";
	public static String ALSPAC_AF = "ALSPAC_AF";
	public static String ESP6500_AA_AC = "ESP6500_AA_AC";
	public static String ESP6500_AA_AF = "ESP6500_AA_AF";
	public static String ESP6500_EA_AC = "ESP6500_EA_AC";
	public static String ESP6500_EA_AF = "ESP6500_EA_AF";
	public static String ExAC_AC = "ExAC_AC";
	public static String ExAC_AF = "ExAC_AF";
	public static String ExAC_Adj_AC = "ExAC_Adj_AC";
	public static String ExAC_Adj_AF = "ExAC_Adj_AF";
	public static String ExAC_AFR_AC = "ExAC_AFR_AC";
	public static String ExAC_AFR_AF = "ExAC_AFR_AF";
	public static String ExAC_AMR_AC = "ExAC_AMR_AC";
	public static String ExAC_AMR_AF = "ExAC_AMR_AF";
	public static String ExAC_EAS_AC = "ExAC_EAS_AC";
	public static String ExAC_EAS_AF = "ExAC_EAS_AF";
	public static String ExAC_FIN_AC = "ExAC_FIN_AC";
	public static String ExAC_FIN_AF = "ExAC_FIN_AF";
	public static String ExAC_NFE_AC = "ExAC_NFE_AC";
	public static String ExAC_NFE_AF = "ExAC_NFE_AF";
	public static String ExAC_SAS_AC = "ExAC_SAS_AC";
	public static String ExAC_SAS_AF = "ExAC_SAS_AF";
	public static String ExAC_nonTCGA_AC = "ExAC_nonTCGA_AC";
	public static String ExAC_nonTCGA_AF = "ExAC_nonTCGA_AF";
	public static String ExAC_nonTCGA_Adj_AC = "ExAC_nonTCGA_Adj_AC";
	public static String ExAC_nonTCGA_Adj_AF = "ExAC_nonTCGA_Adj_AF";
	public static String ExAC_nonTCGA_AFR_AC = "ExAC_nonTCGA_AFR_AC";
	public static String ExAC_nonTCGA_AFR_AF = "ExAC_nonTCGA_AFR_AF";
	public static String ExAC_nonTCGA_AMR_AC = "ExAC_nonTCGA_AMR_AC";
	public static String ExAC_nonTCGA_AMR_AF = "ExAC_nonTCGA_AMR_AF";
	public static String ExAC_nonTCGA_EAS_AC = "ExAC_nonTCGA_EAS_AC";
	public static String ExAC_nonTCGA_EAS_AF = "ExAC_nonTCGA_EAS_AF";
	public static String ExAC_nonTCGA_FIN_AC = "ExAC_nonTCGA_FIN_AC";
	public static String ExAC_nonTCGA_FIN_AF = "ExAC_nonTCGA_FIN_AF";
	public static String ExAC_nonTCGA_NFE_AC = "ExAC_nonTCGA_NFE_AC";
	public static String ExAC_nonTCGA_NFE_AF = "ExAC_nonTCGA_NFE_AF";
	public static String ExAC_nonTCGA_SAS_AC = "ExAC_nonTCGA_SAS_AC";
	public static String ExAC_nonTCGA_SAS_AF = "ExAC_nonTCGA_SAS_AF";
	public static String ExAC_nonpsych_AC = "ExAC_nonpsych_AC";
	public static String ExAC_nonpsych_AF = "ExAC_nonpsych_AF";
	public static String ExAC_nonpsych_Adj_AC = "ExAC_nonpsych_Adj_AC";
	public static String ExAC_nonpsych_Adj_AF = "ExAC_nonpsych_Adj_AF";
	public static String ExAC_nonpsych_AFR_AC = "ExAC_nonpsych_AFR_AC";
	public static String ExAC_nonpsych_AFR_AF = "ExAC_nonpsych_AFR_AF";
	public static String ExAC_nonpsych_AMR_AC = "ExAC_nonpsych_AMR_AC";
	public static String ExAC_nonpsych_AMR_AF = "ExAC_nonpsych_AMR_AF";
	public static String ExAC_nonpsych_EAS_ACExAC_nonpsych_EAS_AF = "ExAC_nonpsych_EAS_ACExAC_nonpsych_EAS_AF";
	public static String ExAC_nonpsych_FIN_AC = "ExAC_nonpsych_FIN_AC";
	public static String ExAC_nonpsych_FIN_AF = "ExAC_nonpsych_FIN_AF";
	public static String ExAC_nonpsych_NFE_AC = "ExAC_nonpsych_NFE_AC";
	public static String ExAC_nonpsych_NFE_AF = "ExAC_nonpsych_NFE_AF";
	public static String ExAC_nonpsych_SAS_AC = "ExAC_nonpsych_SAS_AC";
	public static String ExAC_nonpsych_SAS_AF = "ExAC_nonpsych_SAS_AF";
	public static String clinvar_rs = "clinvar_rs";
	public static String clinvar_clnsig = "clinvar_clnsig";
	public static String clinvar_trait = "clinvar_trait";
	public static String clinvar_golden_starsInterpro_domain = "clinvar_golden_starsInterpro_domain";
	public static String GTEx_V6_gene = "GTEx_V6_gene";
	public static String GTEx_V6_tissue = "GTEx_V6_tissue ";

}
