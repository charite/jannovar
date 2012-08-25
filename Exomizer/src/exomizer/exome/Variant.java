package exomizer.exome;

import java.util.ArrayList;

import exomizer.common.Constants;
import exomizer.filter.ITriage;

/* A class that is used to hold information about the individual variants 
 *  as parsed from the VCF file.
 * @author peter.robinson@charite.de
 * @date 22.08.2012
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
    private byte genotype=GENOTYPE_NOT_INITIALIZED;
    /** Quality of variant call, taken from QUAL column of VCF file. */
    private float variant_quality;
    /** Variant type (assigned by annovar)
     *
     *	@see  exomizer.common.Constants */
    private byte variantType=VARIANT_TYPE_UNKNOWN;
    /** Number of reads associated with variant call (since we are using a short, this value has a maximum of 32,767). */
    private short nReads=0;
    /** HGVS gene symbol */
    private String genename=null;
    /** Refseq id, if available */
    private String refseq_mrna=null;
    /** A representative String for the nucleotide mutation */
    private String nucleotideMutation=null;
    /** A representative String for the amino acid mutation if applicable */
    private String aaMutation=null;
    /** A list of results of filtering applied to this variant. */
    private ArrayList<ITriage> triage_list=null;
    /** Original VCF line from which this mutation comes. */
    public String vcfLine=null;

   

 
   
     /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param var variant (alt) nucleotide
    */
    public Variant(String c, int p, String r, String var) {
	this.chromosome = convertChromosomeStringToByteValue(c);
	this.position=p;
	this.ref = r;
	this.var = var;
	this.triage_list = new ArrayList<ITriage> ();
    }
    /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param var variant (alt) nucleotide
     * @param annot arbitrary annotation data
    */
    public Variant(String c, int p, String r, String var,String vcf_line) {
	this(c,p,r,var);
	this.vcfLine = vcf_line;
    }
   

    // ###########   SETTERS ######################### //

    public void set_genename(String n) { this.genename=n; }
    public void set_refseq_mrna(String m) { this.refseq_mrna=m; }
    public void set_cds_mutation(String m) { this.nucleotideMutation=m; }
    public void set_aa_mutation(String m) { this.aaMutation=m; }
    public void set_homozygous_alt() { this.genotype = GENOTYPE_HOMOZYGOUS_ALT; }
    public void set_heterozygous() { this.genotype = GENOTYPE_HETEROZYGOUS; }
    public void set_homozygous_ref() { this.genotype = GENOTYPE_HOMOZYGOUS_REF; }
    public void set_predicted_non_missense_path() { this.is_non_SNV_patho = true; }
    /** Set the type of the variant to one of the 25 types defined by the annovar annotations. */
    public void set_variant_type(byte t) { this.variantType=t; }
    public void set_variant_quality(int q) { this.variant_quality = q; }

    public void addFilterTriage(ITriage t){ this.triage_list.add(t); }
   
    // ###########   GETTERS ######################### //
    private boolean is_non_SNV_patho = false;
    public boolean is_non_SNV_pathogenic() { return is_non_SNV_patho; }
    public int get_position() { return position; }
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

   

    public boolean is_X_chromosomal() { return this.chromosome == X_CHROMOSOME;  }
    
   
    public boolean passes_variant_quality_threshold(int threshold) { return this.variant_quality >= threshold; }
    public float get_variant_quality() { return this.variant_quality; }
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

    public String get_position_as_string() { return Integer.toString(this.position); }
    /**
     * @return an integer representation of the chromosome  (note: X=23, Y=24).
     */
    public int get_chromosome() { return chromosome; }

  

    /**
     * @return an String representation of the chromosome (e.g., chr3, chrX).
     */
    public String get_chrom_string() {
	StringBuilder sb = new StringBuilder();
	sb.append("chr");
	if (chromosome ==  X_CHROMOSOME ) sb.append("X");
	else if (chromosome ==  Y_CHROMOSOME ) sb.append("Y");
	else if (chromosome ==  M_CHROMOSOME ) sb.append("M");
	else sb.append(chromosome);
	return sb.toString();
    }


    /**
     * Get representation of current mutation as a string. For mutations with
     * an amino acid code, return this in parentheses. 
     *<p>
     * For instance, return c.1234-2A>T for a splice mutation but
     * c.1236C>G (p.C412Y) for a missence mutation.
     */
    public String get_mutation()
    {
	if (aaMutation == null || aaMutation.equals("-"))
	    return nucleotideMutation;
	else return nucleotideMutation + " (" + aaMutation + ")";
    }



    // ##########   UTILITY FUNCTIONS ##################### //

    /**
     * @param c a String representation of a chromosome (e.g., chr3, chrX).
     * @return corresponding integer (e.g., 3, 23).
     */
    public byte convertChromosomeStringToByteValue(String c) {
	//System.out.println(" make_chrom_int c = " + c);
	if (c.startsWith("chr")) c = c.substring(3);
	if (c.equals("X") ) return 23;
	if (c.equals("Y") ) return 24;
	if (c.equals("M") ) return 25;
	Byte i = null;
	try {
	    i = Byte.parseByte(c);
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
	sb.append("\t"+ chr + ":" + ref + position + var);
	if (nucleotideMutation != null)
	    sb.append("\tmutation: "+ get_mutation() + "\n");
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
	
	return sb.toString();

    }
    /**
     * The variant types (e.e., MISSENSE, NONSENSE) are stored internally as byte values.
     * This function converts these byte values into strings.
     * @return A string representing the type of the current variant.
     */
     public String get_variant_type_as_string()
    {
	String s = "-";
	switch(this.variantType) {
	case INTERGENIC: s = "intergenic"; break;
	case ncRNA_INTRONIC: s = "ncRNA_intronic"; break;
	case INTRONIC: s = "intronic"; break;
	case MISSENSE: s = "missense"; break;
	case NONSENSE: s = "nonsense"; break;
	case SYNONYMOUS: s = "synonymous"; break;
	case DOWNSTREAM: s =  "downstream"; break;
	case ncRNA_EXONIC: s = "ncRNA_exonic"; break;
	case VARIANT_TYPE_UNKNOWN: s = "unknown"; break;
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
    public int compareTo(Variant other) {
	if (other.chromosome > this.chromosome) return -1;
	else if (other.chromosome < this.chromosome) return 1;
	else if (other.position > this.position) return -1;
	else if (other.position < this.position) return 1;
	else return 0;
    }
}