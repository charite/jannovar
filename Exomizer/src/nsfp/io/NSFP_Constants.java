package nsfp.io;


public interface NSFP_Constants {

  /** The following are used to make the indices of the fields of this object public. */
    public static final int CHR = 0;
    public static final int POS = 1;
    public static final int REF = 2;   
    public static final int ALT = 3;
    public static final int AAREF = 4;   
    public static final int AAALT = 5;   
    public static final int HG18_POS = 6;        
    public static final int GENENAME=7;        
    public static final int UNIPROT_ACC = 8;// Uniprot_acc     
    public static final int UNIPROT_ID = 9; //Uniprot_id      
    public static final int UNIPROT_AAPOS = 10;// Uniprot_aapos   
    public static final int INTERPRO_DOMAIN = 11;// Interpro_domain 
    public static final int CDS_STRAND = 12;// cds_strand      
    public static final int REFCODON = 13; // refcodon        
    public static final int SLR_TEST_STATISTIC = 14; // SLR_test_statistic      
    public static final int CODONPOS = 15; // codonpos        
    public static final int FOLD_DEGENERATE = 16; // fold-degenerate 
    public static final int ANCESTRAL_ALLELE = 17;// Ancestral_allele       
    public static final int ENSEMBL_GENE_ID = 18; // Ensembl_geneid  
    public static final int ENSEMBL_TRANSCRIPT_ID = 19; // Ensembl_transcriptid    
    public static final int AAPOS = 20; // aapos   
    public static final int SIFT_SCORE = 21; //score      
    public static final int Polyphen2_HDIV_score = 22;  
    public static final int POLYPHEN2_HDIV_SCORE = 22;  
    public static final int Polyphen2_HDIV_pred = 23;      
    public static final int Polyphen2_HVAR_score = 24;    
    public static final int POLYPHEN2_HVAR_SCORE = 24;
    public static final int Polyphen2_HVAR_pred = 25;     
    public static final int LRT_score = 26;       
    public static final int LRT_pred = 27;        
    public static final int MUTATION_TASTER_SCORE = 28;      
    public static final int MUTATION_TASTER_PRED = 29;    
    public static final int GERP_NR = 30;       
    public static final int GERP_RS = 31;        
    public static final int PHYLO_P = 32;  
    public static final int UCSC_29way_pi = 33;        
    public static final int UCSC_29way_logOdds = 34;   
    public static final int LRT_Omega = 35;       
    public static final int UniSNP_ids = 36;      
    public static final int TG_1000Gp1_AC = 37;      
    public static final int TG_1000Gp1_AF = 38;      
    public static final int TG_1000Gp1_AFR_AC = 39;  
    public static final int TG_1000Gp1_AFR_AF = 40; 
    public static final int TG_1000Gp1_EUR_AC = 41; 
    public static final int TG_1000Gp1_EUR_AF  =42;
    public static final int TG_1000Gp1_AMR_AC = 43; 
    public static final int TG_1000Gp1_AMR_AF  = 44;
    public static final int TG_1000Gp1_ASN_AC = 45; 
    public static final int TG_1000Gp1_ASN_AF = 46; 
    public static final int TG_ESP5400_AA_AF  = 47;
    public static final int ESP5400_EA_AF = 48;

    /** The following constants are not dbNSFP fields, but flags that
	cause a specially formated field to be displayed. */
    public static final int GENOMIC_VAR = 101;
    public static final int POLYPHEN_WITH_PRED = 200;
    public static final int SIFT_WITH_PRED = 201;
    public static final int MUT_TASTER_WITH_PRED = 202;
    public static final int VARTYPE_IDX = 203;
    public static final int GENOTYPE_QUALITY = 204;

    public static final int THOUSAND_GENOMES_AF_AC = 1000;


    public static final int NOPARSE = -1; /* Flag for bad parse */
    public static final float NOPARSE_FLOAT = -1f;
    public static final float DATA_NOT_APPLICABLE = -2f;
    public static final int THOUSAND_GENOMES_NOT_AVAILABLE = -3;
    public static final int INT_NOT_AVAILABLE = -1;



    /* Mutation types (from annovar) */
    public static final int DOWNSTREAM = 1001;
    public static final int EXONIC = 1002;
    public static final int FS_DELETION = 1003;
    public static final int FS_INSERTION = 1004;
    public static final int NON_FS_SUBSTITUTION = 1005;
    public static final int FS_SUBSTITUTION = 1006;
    public static final int INTERGENIC = 1007;
    public static final int INTRONIC = 1008;
    public static final int MISSENSE = 1009;
    public static final int ncRNA_EXONIC = 1010;
    public static final int ncRNA_INTRONIC = 1011;
    public static final int ncRNA_SPLICING = 1012;
    public static final int ncRNA_UTR3 = 1013;
    public static final int ncRNA_UTR5 = 1014;
    public static final int NON_FS_DELETION = 1015;
    public static final int NON_FS_INSERTION = 1016;
    public static final int NONSENSE = 1017;
    public static final int SPLICING= 1018;
    public static final int STOPGAIN = 1019;
    public static final int STOPLOSS = 1020;
    public static final int SYNONYMOUS = 1021;
    public static final int UNKNOWN = 1022;
    public static final int UPSTREAM = 1023;
    public static final int UTR3 = 1024;
    public static final int UTR5 = 1025;

    
    


    public static final int GENOTYPE_HOMOZYGOUS_REF = 2001;
    public static final int GENOTYPE_HOMOZYGOUS_ALT = 2002;
  
    public static final int GENOTYPE_HETEROZYGOUS = 2003;
    public static final int GENOTYPE_UNKNOWN =2004;
    public static final int GENOTYPE_NOT_INITIALIZED =2005;

  
    public static final int X_CHROMOSOME = 23;
    public static final int Y_CHROMOSOME = 24;
    public static final int M_CHROMOSOME = 25;
    

}