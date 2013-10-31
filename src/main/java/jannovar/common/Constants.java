package jannovar.common;



/**
 * This interface has some numerical contants that are used
 * by various other classes in the Exomizer to refer to 
 * various entities. 
 * Chromosomes 1-22 are refered to by the corresponding ints, and chromosomes
 * X, Y, and M are referred to as below.
 * @author Peter N Robinson
 * @version 0.19 (11 July, 2013)
 */
public interface Constants {
    /* 1) Chromosomes */
    public static final byte X_CHROMOSOME = 23;
    public static final byte Y_CHROMOSOME = 24;
    public static final byte M_CHROMOSOME = 25;

    /* 4) Index of fields of the DP4 (depth) from the VCF file:
       ref-forward bases, ref-reverse, alt-forward and alt-reverse bases
    */
    
    public static final int N_REF_FORWARD_BASES=0;
    public static final int N_REF_REVERSE_BASES=1;
    public static final int N_ALT_FORWARD_BASES=2;
    public static final int N_ALT_REVERSE_BASES=3;
      
    /* Source of transcript files */
    
    public static final int UCSC	= 0;
    public static final int ENSEMBL	= 1;
    public static final int REFSEQ	= 2; 
    
    /* FTP base names and paths */
    
    public static final String UCSC_FTP_BASE	= "http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/";
    public static final String ENSEMBL_FTP_BASE	= "ftp://ftp.ensembl.org/pub/release-72/";
    public static final String ENSEMBL_GTF_BASE	= "gtf/homo_sapiens/";
    public static final String ENSEMBL_FASTA_BASE	= "fasta/homo_sapiens/";
    public static final String REFSEQ_FTP_BASE		= "ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/";
//    public static final String REFSEQ_GFF_BASE		= "GFF_interim/";
    public static final String REFSEQ_GFF_BASE		= "GFF/";
    public static final String REFSEQ_FASTA_BASE	= "RNA/";

    public static final String ensembl_gtf		= "Homo_sapiens.GRCh37.72.gtf.gz";
    public static final String ensembl_cdna		= "Homo_sapiens.GRCh37.72.cdna.all.fa.gz";
    public static final String ensembl_ncrna	= "Homo_sapiens.GRCh37.72.ncrna.fa.gz";
    
//    public static final String refseq_gff		= "interim_GRCh37.p13_top_level_2013-07-05.gff3.gz";
    public static final String refseq_gff		= "ref_GRCh37.p13_top_level.gff3.gz";
    public static final String refseq_rna		= "rna.fa.gz";
    
    

    /** Flag for an integer value that has not been initialized. */
    public static final int UNINITIALIZED_INT = -10;
    /** Flag for an float value that has not been initialized. */
    public static final float UNINITIALIZED_FLOAT = -10;
    /** Flag for an integer field that could not be parsed correctly */
    public static final int NOPARSE = -5; 
    /** Flag for a float field that could not be parsed correctly */
    public static final float NOPARSE_FLOAT = -5f;
    /** Flag for no rsID for variant */
    public static final int NO_RSID = -1;


     /** Name of the UCSC knownGenes file. */
    public static final String knownGene = "knownGene.txt";
    /** Name of the UCSC knownGenes mRNA file. */
    public static final String knownGeneMrna = "knownGeneMrna.txt";
    /** Name of the UCSC knownGenes Xref file. */
    public static final String kgXref = "kgXref.txt";
    /** Name of the UCSC knownGenes Xref file. */
    public static final String known2locus = "knownToLocusLink.txt";
    /** Name of refFlat.txt file */
    public static final String refFlat = "refFlat.txt";
   


}
