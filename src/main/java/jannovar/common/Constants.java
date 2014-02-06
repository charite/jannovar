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

    public static final String UCSC_FTP_BASE_MM9	= "http://hgdownload.soe.ucsc.edu/goldenPath/mm9/database/";
    public static final String UCSC_FTP_BASE_MM10	= "http://hgdownload.soe.ucsc.edu/goldenPath/mm10/database/";
    public static final String UCSC_FTP_BASE_HG18	= "http://hgdownload.soe.ucsc.edu/goldenPath/hg18/database/";
    public static final String UCSC_FTP_BASE_HG19	= "http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/";

    public static final String ENSEMBL_FTP_BASE_HG19	= "ftp://ftp.ensembl.org/pub/release-74/";
    public static final String ENSEMBL_FTP_BASE_HG18	= "ftp://ftp.ensembl.org/pub/release-54/";
    public static final String ENSEMBL_FTP_BASE_MM9		= "ftp://ftp.ensembl.org/pub/release-67/";
    public static final String ENSEMBL_FTP_BASE_MM10	= "ftp://ftp.ensembl.org/pub/release-74/";
    public static final String ENSEMBL_HUMAN_BASE	= "homo_sapiens/";
    public static final String ENSEMBL_MOUSE_BASE	= "mus_musculus/";
    public static final String ENSEMBL_GTF_BASE		= "gtf/";
    public static final String ENSEMBL_FASTA_BASE	= "fasta/";
    
    public static final String REFSEQ_FTP_BASE		= "ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/";
    public static final String REFSEQ_FTP_BASE_MOUSE		= "ftp://ftp.ncbi.nlm.nih.gov/genomes/Mus_musculus/";
    public static final String REFSEQ_GFF_BASE		= "GFF/";
    public static final String REFSEQ_FASTA_BASE	= "RNA/";
    public static final String REFSEQ_HG18			= "ARCHIVE/BUILD.36.3/";
    public static final String REFSEQ_HG19			= "ARCHIVE/ANNOTATION_RELEASE.105/";
    public static final String REFSEQ_HG38			= "";
    public static final String REFSEQ_MM9			= "ARCHIVE/BUILD.37.2/";
    public static final String REFSEQ_MM10			= "";

    public static final String ensembl_hg19			= "Homo_sapiens.GRCh37.74";
    public static final String ensembl_hg18			= "Homo_sapiens.NCBI36.54";
    public static final String ensembl_mm9			= "Mus_musculus.NCBIM37.67";
    public static final String ensembl_mm10			= "Mus_musculus.GRCm38.74";
    public static final String ensembl_gtf			= ".gtf.gz";
    public static final String ensembl_cdna			= ".cdna.all.fa.gz";
    public static final String ensembl_ncrna		= ".ncrna.fa.gz";

    public static final String refseq_gff_hg38		= "ref_GRCh38_top_level.gff3.gz";
    public static final String refseq_gff_hg19		= "ref_GRCh37.p13_top_level.gff3.gz";
    public static final String refseq_gff_hg18		= "ref_NCBI36_top_level.gff3.gz";
    public static final String refseq_gff_mm9		= "ref_MGSCv37_top_level.gff3.gz";
    public static final String refseq_gff_mm10		= "ref_GRCm38.p2_top_level.gff3.gz";
    public static final String refseq_rna			= "rna.fa.gz";
    
    

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

    /* realeses */
    
    public enum Release{
    	HG18,
    	HG19,
	HG38,
    	MM9,
    	MM10;
       	
    	/**
    	 * Returns the UCSC genome release name for the given {@link Release}.
    	 * @param r The {@link Release}
    	 * @return the {@link Release} as String representation.
    	 */
    	public String getUCSCString(Release r){
    		switch (r) {
			case HG18: return "hg18";
			case HG19: return "hg19";
			case HG38: return "hg38";
			case MM9: return "mm9";
			case MM10: return "mm10";
			default: return "error";
			}
    	}   	
    	
    	/**
    	 * Returns the NCBI genome release name for the given {@link Release}.
    	 * @param r The {@link Release}
    	 * @return the {@link Release} as String representation.
    	 */
    	public String getNCBIString(Release r){
    		switch (r) {
			case HG18: return "NCBI36.3";
			case HG19: return "GRCh37.p13";
			case HG38: return "GRCh38";
			case MM9: return "MGSCv37.2";
			case MM10: return "GRCm38.p1";
			default: return "error";
			}
    	}   	
    	
    	/**
    	 * Returns the Ensembl genome release name for the given {@link Release}.
    	 * @param r The {@link Release}
    	 * @return the {@link Release} as String representation.
    	 */
    	public String getEnsemblString(Release r){
    		switch (r) {
			case HG18: return "NCBI36.54";
			case HG19: return "GRCh37.74";
			case MM9: return "NCBIM37.67";
			case MM10: return "GRCm38.74";
			default: return "error";
			}
    	}
    }
    
    /* DEFAUL */
    public static final String DEFAULT_DATA	= "data/";

    /* TESTING */
    
    /**  Name of the UCSC serialized data file that will be used by Jannovar-testing. */
    public static final String UCSCserializationTestFileName="/ucsc_test.ser";
    

}
