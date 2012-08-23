package exomizer.exome;


import exomizer.common.Constants;

/* A class that will be use to
   hold information about the individual variants as parsed from 
   the VCF file.
   
*/
public class Variant implements Comparable<Variant>, Constants {
    
    
    /** chromosome; 23=X, 24=Y */
    private byte chromosome;
    /** position along chromosome; */
    private int position; 
    /** Reference sequence (a single nucleotide for a SNV, more for some other types of variant) */
    private String ref;
    /** Variant sequence (called "ALT", alternate, in VCF files). */
    private String var;
    /**  genotype (See exomizer.common.Constants for the integer constants used to represent the genotypes). */
    private byte genotype;
    /** Quality of variant call, taken from QUAL column of VCF file. */
    private float quality;
    /** Variant type (assigned by annovar, See exomizer.common.Constants) */
    private byte variantType=VARIANT_TYPE_UNKNOWN;
    /** Number of reads associated with variant call (since we are using a short, this is max 32,767) */
    private short nReads=0;
    /** A representative String for the nucleotide mutation */
    private String nucleotideMutation=null;
    /** A representative String for the amino acid mutation if applicable */
    private String aaMutation=null;
    /** A list of results of filtering applied to this variant. */
    private ArrayList<IVariantTriage> triage=null;
    /** Original VCF line from which this mutation comes. */
    public String vcfLine=null;

   

    private int vtype=UNKNOWN;
    private String genename=null;
    private String refseq_mrna=null;
    private String exon=null;
    private String cds_mutation=null;
    private String aa_mutation=null;
    /** one of GENOTYPE_HOMOZYGOUS_REF,GENOTYPE_HOMOZYGOUS_VAR, GENOTYPE_HETEROZYGOUS or GENOTYPE_UNKNOWN */
    private int genotype= GENOTYPE_NOT_INITIALIZED;
    private int genotype_quality = GENOTYPE_UNKNOWN;
   


    public void set_genename(String n) { this.genename=n; }
    public void set_refseq_mrna(String m) { this.refseq_mrna=m; }
    public void set_cds_mutation(String m) { this.cds_mutation=m; }
    public void set_aa_mutation(String m) { this.aa_mutation=m; }
    public void set_esp_minor(int minor) { this.ESP_minor_count = minor; }
    public void set_esp_major(int major)  { this.ESP_major_count = major; }
    public void set_esp_frequency(float freq) { this.ESP_freq = freq; }


   

    private boolean is_non_SNV_patho = false;
    public boolean is_non_SNV_pathogenic() { return is_non_SNV_patho; }
     
    /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param var variant (alt) nucleotide
    */
    public SNV(String c, int p, String r, String var) {
	this.chrom = make_chrom_int(c);
	this.pos=p;
	this.ref = r;
	this.var = var;
    }
    /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param var variant (alt) nucleotide
     * @param annot arbitrary annotation data
    */
    public SNV(String c, int p, String r, String var,String vcf_line) {
	this(c,p,r,var);
	this.vcf_line = vcf_line;
    }

    public Integer getChromosomeAsInteger()
    {
	return new Integer(this.chrom);
    }

    public int get_position() { return pos; }
    public String get_ref_nucleotide() { return ref; }
    public String get_alt_nucleotide() { return var; }
    public String get_genename() { return genename; }
    public boolean is_homozygous_alt() { return this.genotype == GENOTYPE_HOMOZYGOUS_ALT; }
    public boolean is_homozygous_ref() { return this.genotype == GENOTYPE_HOMOZYGOUS_REF; }
    public boolean is_heterozygous() { return this.genotype == GENOTYPE_HETEROZYGOUS; }
    public boolean is_unknown_genotype() { return this.genotype ==GENOTYPE_UNKNOWN; }
    public boolean genotype_not_initialized() { return this.genotype == GENOTYPE_NOT_INITIALIZED; }
   
    public char ref_as_char() { return ref.charAt(0); }
    public char var_as_char() { return var.charAt(0); }

    public boolean is_single_nucleotide_variant () { return (this.ref.length()==1 && this.var.length()==1); }

    public void set_homozygous_alt() { this.genotype = GENOTYPE_HOMOZYGOUS_ALT; }
    public void set_heterozygous() { this.genotype = GENOTYPE_HETEROZYGOUS; }
    public void set_homozygous_ref() { this.genotype = GENOTYPE_HOMOZYGOUS_REF; }
    
    public void set_predicted_non_missense_path() { this.is_non_SNV_patho = true; }
    public void set_variant_type(int t) { this.vtype=t; }
    public void set_genotype_quality(int q) { this.genotype_quality = q; }
    public boolean passes_genotype_quality_threshold(int threshold) { return this.genotype_quality >= threshold; }
    public int get_genotype_quality() { return this.genotype_quality; }
    public String get_genotype_as_string() {
	switch(this.genotype) {
	case GENOTYPE_NOT_INITIALIZED: return "genotype not initialized";
	case GENOTYPE_HOMOZYGOUS_REF: return "homozygous ref";
	case GENOTYPE_HOMOZYGOUS_ALT: return "homozygous alt";
	case GENOTYPE_HETEROZYGOUS: return "heterozygous";
	case GENOTYPE_UNKNOWN: return  "genotype unknown";
	}
	return "?";
    }

    public String get_position_as_string() { return Integer.toString(pos); }
    /**
     * @return an integer representation of the chromosome  (note: X=23, Y=24).
     */
    public int get_chromosome() { return chrom; }

  

    /**
     * @return an String representation of the chromosome (e.g., chr3, chrX).
     */
    public String get_chrom_string() {
	StringBuilder sb = new StringBuilder();
	sb.append("chr");
	if (chrom == 23) sb.append("X");
	else if (chrom == 24) sb.append("Y");
	else if (chrom == 25) sb.append("M");
	else sb.append(chrom);
	return sb.toString();
    }


    public String get_mutation()
    {
	if (aa_mutation == null || aa_mutation.equals("-"))
	    return cds_mutation;
	else return cds_mutation + " (" + aa_mutation + ")";
    }


    /**
     * @param c a String representation of a chromosome (e.g., chr3, chrX).
     * @return corresponding integer (e.g., 3, 23).
     */
    public int make_chrom_int(String c) {
	//System.out.println(" make_chrom_int c = " + c);
	if (c.startsWith("chr")) c = c.substring(3);
	if (c.equals("X") ) return 23;
	if (c.equals("Y") ) return 24;
	if (c.equals("M") ) return 25;
	Integer i = null;
	try {
	    i = Integer.parseInt(c);
	} catch (NumberFormatException e) {
	    System.err.println("[SNV.java] Could not parse Chromosome string \"" + c + "\"");
	    throw e;
	}
	return i;
    }
    





    public String toString() {
	StringBuilder sb = new StringBuilder();
	String chr = get_chrom_string();
	sb.append("SNV: " + genename +"\n");
	sb.append("\t"+ chr + ":" + ref + pos + var);
	if (cds_mutation != null)
	    sb.append("\tmutation: "+cds_mutation + "\n");
	else
	    sb.append("\tcds mutation not initialized\n");
	if (genotype == GENOTYPE_HOMOZYGOUS_REF)
	    sb.append("\tGenotype: homzygous ref\n");
	else if (genotype == GENOTYPE_HOMOZYGOUS_ALT)
	    sb.append("\tGenotype: homzygous var\n");
	else if (genotype == GENOTYPE_HETEROZYGOUS)
	    sb.append("\tGenotype: heterozygous\n");
	else 
	    sb.append("\tGenotype not known or not initialized");
	sb.append("\tType: " + get_variant_type_as_string() + "\n");
	if (anno != null)
	    sb.append(" [" + anno + "]");
	return sb.toString();

    }

     public String get_variant_type_as_string()
    {
	String s = "-";
	switch(this.vtype) {
	case INTERGENIC: s = "intergenic"; break;
	case ncRNA_INTRONIC: s = "ncRNA_intronic"; break;
	case INTRONIC: s = "intronic"; break;
	case MISSENSE: s = "missense"; break;
	case NONSENSE: s = "nonsense"; break;
	case SYNONYMOUS: s = "synonymous"; break;
	case DOWNSTREAM: s =  "downstream"; break;
	case ncRNA_EXONIC: s = "ncRNA_exonic"; break;
	case UNKNOWN: s = "unknown"; break;
	case UTR5: s = "UTR5"; break;
	case UTR3: s= "UTR3"; break;
	case STOPLOSS: s = "stoploss"; break;
	case NON_FS_INSERTION: s = "nonframeshift-insertion"; break;
	case ncRNA_UTR3: s = "ncRNA_UTR5"; break;
	case STOPGAIN: s = "stopgain"; break;
	case FS_INSERTION: s = "frameshift-insertion"; break;
	case FS_DELETION: s = "frameshift-deletion"; break;
	case FS_SUBSTITUTION: s = "frameshift-substitution"; break;
	case NON_FS_DELETION: s = "nonframeshift-deletion"; break;
	case UPSTREAM: s = "upstream"; break;
	case SPLICING: s = "splicing"; break;
	case NON_FS_SUBSTITUTION: s = "nonframeshift-substitution"; break;
	case ncRNA_SPLICING: s = "ncRNA_splicing"; break;
	case EXONIC: s = "exonic"; break;
	default: s = "unparsed";
	}
	return s;
    }



    
    /**
     * Sort based on chromosome and position.
     */
    @Override
    public int compareTo(SNV other) {
	if (other.chrom>this.chrom) return -1;
	else if (other.chrom<this.chrom) return 1;
	else if (other.pos > this.pos) return -1;
	else if (other.pos < this.pos) return 1;
	else return 0;
    }
}