package jannovar.exome;

import java.util.ArrayList;
import java.util.HashMap;

import exomizer.common.Constants;
import exomizer.filter.ITriage;
import exomizer.reference.Annotation;
import exomizer.exome.GenotypeI;
import exomizer.exception.VCFParseException;

/* A class that is used to hold information about the individual variants 
 *  as parsed from the VCF file.
 * @author Peter Robinson
 * @version 0.13 (6 April, 2013)
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
    /**  The genotype of this variant (note that {@link exomizer.exome.GenotypeI GenotypeI}
     * must be instantiated by either {@link exomizer.exome.SingleGenotype SingleGenotype}
     * for VCF files with a single sample or 
     * {@link exomizer.exome.MultipleGenotype MultipleGenotype}
     * for VCF files with multiple samples.
     */
    private GenotypeI genotype=null;
    /** The PHRED score for the variant call. */
    private int variant_quality;
   
    
    /** A map of the results of filtering and prioritization. The key to the map is an 
	integer constant as defined in {@link exomizer.common.Constants Constants}. */
    private HashMap<Integer,ITriage> triageMap=null;
    /** Original VCF line from which this mutation comes. */
    public String vcfLine=null;
    /** Annotation object resulting from Jannovar-type annotation of this variant. */
    private Annotation annot=null;

   

 
   
     /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param var variant (alt) nucleotide
    */
    public Variant(String c, int p, String r, String var) throws VCFParseException {
	this.chromosome = convertChromosomeStringToByteValue(c);
	this.position=p;
	this.ref = r;
	this.var = var;
	this.triageMap = new HashMap<Integer,ITriage> ();
    }

    /**
     * This constructor is intended to be used by the factory method
     * {@link exomizer.io.VCFLine#extractVariant extractVariant} in the
     * class {@link exomizer.io.VCFLine VCFLine}. The constructor merely
     * initializes the ArrayList of ITriage objects and expects that everything
     * else will be initialized by {@link exomizer.io.VCFLine#extractVariant extractVariant}.
     */
    public Variant() {
	this.triageMap = new HashMap<Integer,ITriage> ();
    }
   

    // ###########   SETTERS ######################### //
    /** Initialize the {@link #chromosome} field
     * @param chr A string representation of the chromosome such as chr3 or chrX
     */
    public void setChromosome(String chr) throws VCFParseException {
	this.chromosome = convertChromosomeStringToByteValue(chr);
    }
     /** Initialize the {@link #position} field
     * @param p Position of the variant on the chromosome
     */
    public void setPosition(int p) {
	this.position = p;
    }
    /**
     * Initialize the {@link #ref} field
     * @param s sequence of reference
     */
    public void setRef(String s) {
	this.ref = s;
    }

    public void setGenotype(GenotypeI gtype) {
	this.genotype = gtype;
    }

     /**
     * Initialize the {@link #var} field
     * @param s sequence of variant
     */
    public void setVar(String s) {
	this.var = s;
    }
    
    public void set_variant_quality(int q) { this.variant_quality = q; }
    /**
     * This method is used to add an {@link exomizer.filter.ITriage ITriage} object to
     * this variant. Such objects represent the results of evaluation of this variant
     * and may be used for filtering or prioritization. The Integer is a constant from 
     * {@link exomizer.common.Constants Constants} that identifies the type of 
     * {@link exomizer.filter.ITriage ITriage} object being added (e.g., pathogenicity,
     * frequency, etc). */
    public void addFilterTriage(ITriage t, int type){ 
	this.triageMap.put(type,t); 
    }
    public void setVCFline(String line) { this.vcfLine = line; }

    /**
     * Set the annotation object for this variant. This method is intended to be
     * used by our annovar-style annotation code in order to provide transcript-
     * level annotation for the variants, for example, to annotate the chromosomal
     * variant {@code chr7:34889222:T>C} to {@code NPSR1(uc003teh.1:exon10:c.1171T>C:p.*391R)" (Type:STOPLOSS)}.
     */
    public void setAnnotation(Annotation a) {
	this.annot = a;
    }


   
    // ###########   GETTERS ######################### //
    /**
     * @return the 5' position of this variant on its chromosome.
     */
    public int get_position() { return position; }
    /**
     * @return The reference base or bases of this variant
     */
    public String get_ref() { return ref; }
    /**
     * The alternate base or bases of this variant.
     */
    public String get_alt() { return var; }
    /**
     * Get the genesymbol of the gene associated with this variant, if possible 
     */
    public String getGeneSymbol() { 
	if (this.annot != null)  {
	    return annot.getGeneSymbol();
	} else {
	    return ".";
	}    
    }

    /**
     * @return the NCBI Entrez Gene ID 
     */
    public int getEntrezGeneID() {
	return annot.getEntrezGeneID();
    }



    public boolean is_homozygous_alt() { return this.genotype.is_homozygous_alt(); }
    public boolean is_homozygous_ref() { return this.genotype.is_homozygous_ref(); }
    public boolean is_heterozygous() { return this.genotype.is_heterozygous(); }
    public boolean is_unknown_genotype() { return this.genotype.is_unknown_genotype(); }
    public boolean genotype_not_initialized() { return this.genotype.genotype_not_initialized(); }
    /** 
     * @return true if this variant is a nonsynonymous substitution (missense).
     */
    public boolean is_missense_variant() { 
	if (annot == null)
	    return false;
	else return (annot.getVariantType() == VariantType.MISSENSE);
    }
    public String getVCFline() { return this.vcfLine; }
   
    public char ref_as_char() { return ref.charAt(0); }
    public char var_as_char() { return var.charAt(0); }

    public boolean is_single_nucleotide_variant () { return (this.ref.length()==1 && this.var.length()==1); }
    /** 
     * @return the map of "ITriage objects that represent the result of filtering 
     */
    public HashMap<Integer,ITriage> getTriageMap() { return this.triageMap; }
     /**
     * @return an integer representation of the chromosome  (note: X=23, Y=24).
     */
    public int get_chromosome() { return chromosome; }
    /**
     * @return an byte representation of the chromosome, e.g., 1,2,...,22 (note: X=23, Y=24, MT=25).
     */
    public byte getChromosomeAsByte() { return chromosome; }
   

    public boolean is_X_chromosomal() { return this.chromosome == X_CHROMOSOME;  }
    
   
    public boolean passes_variant_quality_threshold(int threshold) { return this.variant_quality >= threshold; }
    public float get_variant_quality() { return this.variant_quality; }
    public String get_genotype_as_string() {
	return this.genotype.get_genotype_as_string();
    }

    

    public String get_position_as_string() { return Integer.toString(this.position); }
   

    public String get_chromosomal_mutation() {
	StringBuilder sb = new StringBuilder();
	sb.append( get_chrom_string() );
	sb.append(":g.");
	sb.append(this.position + ref + ">" + var);
	return sb.toString();
    }

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
     * Get representation of current variant as a string. This method
     * retrieves the annotation of the variant stored in the
     * {@link exomizer.reference.Annotation Annotation} object. If this
     * is not initialized (which should never happen), it returns ".".
     *<p>
     * Note: This function should not be used anymore, we will refactor the
     * code to handle variants with multiple transcripts better in the 
     * future.
     * @return The annotation of the current variant.
     */
    public String getAnnotation()
    {
	if (this.annot != null)
	    return this.annot.getVariantAnnotation();
	else return ".";
    }

    /**
     * This is intended to decouple the rest of the 
     * program from the way that annnotations are currently
     * stored (annotations for multiple-transcript mutations
     * are stored as a single string, e.g., 
     * LTF(uc003cpr.3:exon5:c.30_31insAAG:p.R10delinsRR,
     * uc003cpq.3:exon2:c.69_70insAAG:p.R23delinsRR,
     * uc010hjh.3:exon2:c.69_70insAAG:p.R23delinsRR)
     * <P>
     * We will be creating a separate object for each annotation, and future
     * implementations will just return that, or a list of strings from 
     * each of them. For now, since everything else is working, just split
     * the String and send back a list.
     */
    public String[] getAnnotationList() {
	ArrayList<String> annotList = new ArrayList<String>();
	if (this.annot == null) {
	    String A[] = new String[1];
	    A[0] = ".";
	    return A;
	}
	String a = this.annot.getVariantAnnotation();
	int x,y;
	x = a.indexOf('(');
	y = a.indexOf(')');
	String b = a.substring(x+1,y); /* Get the stuff inside the "(", ")" */
	String A[] = b.split(","); /* transcript mutations are separated by "'".*/

	return A;
    }


    public String getVariantType() {
	if (this.annot != null)
	    return this.annot.getVariantTypeAsString();
	else return "?";
    }

    /**
     * @see exomizer.common.Constants
     * @return a enum constant representing the variant class (NONSENSE, INTRONIC etc)
     */
    public VariantType getVariantTypeConstant() {
	if (this.annot != null)
	    return this.annot.getVariantType();
	else
	    return VariantType.UNKNOWN;
    }

    /**
     * This method calculates a filter
     * score (prediction of the pathogenicity
     * and relevance of the Variant) by using data from
     * the {@link exomizer.filter.ITriage ITriage} objects
     * associated with this Variant.
     * <P>
     * Note that we use results of filtering to remove Variants
     * that are predicted to be simply non-pathogenic. However, amongst
     * variants predicted to be potentially pathogenic, there are different
     * strengths of prediction, which is what this score tries to reflect.
     * @return a priority score between 0 and 1
     */
    public float getFilterScore() {
	float score = 1f;
	for (Integer i : this.triageMap.keySet()) {
	    ITriage itria = this.triageMap.get(i);
	    float x = itria.filterResult();
	    score *= x;
	}
	return score;
    }
    
    /**
     * This method calculates a priority
     * score (prediction of the pathogenicity
     * and relevance of the Variant) by using data from
     * the {@link exomizer.filter.ITriage ITriage} objects
     * associated with this Variant.
     * @return a priority score between 0 and 1
     */
    public float getPathogenicityPriorityScore() {
    	ITriage path = triageMap.get(PATHOGENICITY_FILTER);
    	float x = path.filterResult();
    	return x;
    }

    // ##########   UTILITY FUNCTIONS ##################### //

    /**
     * @param c a String representation of a chromosome (e.g., chr3, chrX).
     * @return corresponding integer (e.g., 3, 23).
     */
    public byte convertChromosomeStringToByteValue(String c) throws VCFParseException {
	if (c.startsWith("chr")) c = c.substring(3);
	if (c.equals("X") ) return 23;
	if (c.equals("23")) return 23;
	if (c.equals("Y") ) return 24;
	if (c.equals("24")) return 24;
	if (c.equals("M") ) return 25;
	if (c.equals("MT") ) return 25;
	if (c.equals("25") ) return 25;
	Byte i = null;
	try {
	    i = Byte.parseByte(c);
	} catch (NumberFormatException e) {
	    VCFParseException ve = new VCFParseException("[Variant.java] Could not parse Chromosome string \"" + c + "\"");
	    ve.setBadChromosome(c);
	    throw ve;
	}
	return i;
    }
    
    /**
     * @return A String representing the variant in the chromosomal sequence, e.g., chr17:c.73221527G>A
     */
    public String getChromosomalVariant() {
	return String.format("%s:c.%d%s>%s",get_chrom_string(), position, ref, var);
    }



    public String toString() {
	StringBuilder sb = new StringBuilder();
	String chr = get_chrom_string();
	sb.append("\t"+ chr + ":c." + position + ref +">" + var);
	if (annot != null)
	    sb.append("\t: "+ getAnnotation() + "\n");
	else
	    sb.append("\tcds mutation not initialized\n");

	sb.append("\tGenotype: " + this.genotype.toString());

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
	if (this.annot == null)
	    return "uninitialized";
	else
	    return this.annot.getVariantTypeAsString();
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