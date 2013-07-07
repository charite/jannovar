package jannovar.exome;

import java.util.ArrayList;
import java.util.HashMap;

import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.genotype.GenotypeCall;

/** A class that is used to hold information about the individual variants 
 *  as parsed from the VCF file.
 * @author Peter Robinson
 * @version 0.19 (7 July, 2013)
 */
public class Variant implements Comparable<Variant>, Constants {
    
    
    /** chromosome; 23=X, 24=Y */
    private byte chromosome;
    /** position along chromosome; */
    private int position; 
    /** Reference sequence (a single nucleotide for a SNV, more for some other types of variant) */
    private String ref;
    /** Variant sequence (called "ALT", alternate, in VCF files). */
    private String alt;
    /**  The genotype of this variant (note that {@link jannovar.genotype.GenotypeCall GenotypeCall}
     * objects can hold a single genotype for single-sample VCF files or multiple
     * genotypes for for VCF files with multiple samples.
     */
    private GenotypeCall genotype=null;
    /** The PHRED score for the variant call. */
    private int variant_quality;
    /** {@link jannovar.annotation.AnnotationList AnnotationList} object resulting from 
	Jannovar-type annotation of this variant. */
    private AnnotationList annot=null;

   
     /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param alternate variant (alt) nucleotide
    */
    public Variant(byte c, int p, String r, String alternate, GenotypeCall gtype, int qual) {
	this.chromosome = c;
	this.position=p;
	this.ref = r;
	this.alt = alternate;
	this.genotype = gtype;
	this.variant_quality = qual;
    }

    
   

    // ###########   SETTERS ######################### //
    /** Initialize the {@link #chromosome} field
     * @param chr A string representation of the chromosome such as chr3 or chrX
     */
    public void setChromosome(byte chr)  {
	this.chromosome = chr;
    }
     /** Initialize the {@link #position} field
     * @param p Position of the alternate sequence on the chromosome
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

    public void setGenotype(GenotypeCall gtype) {
	this.genotype = gtype;
    }

     /**
     * Initialize the {@link #alt} field
     * @param s sequence of variant
     */
    public void setVar(String s) {
	this.alt = s;
    }
    
    public void set_variant_quality(int q) { this.variant_quality = q; }
    

    /**
     * Set the {@link jannovar.annotation.AnnotationList AnnotationList} object for this variant. 
     * This method is intended to be
     * used by our annovar-style annotation code in order to provide transcript-
     * level annotation for the variants, for example, to annotate the chromosomal
     * variant {@code chr7:34889222:T>C} to {@code NPSR1(uc003teh.1:exon10:c.1171T>C:p.*391R) (Type:STOPLOSS)}.
     * @param a An annotationList object representing annotations of all affected transcripts.
     */
    public void setAnnotation(AnnotationList a) {
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
    public String get_alt() { return alt; }
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



   
    /** 
     * @return true if this variant is a nonsynonymous substitution (missense).
     */
    public boolean is_nonsynonymous_variant() { 
	if (annot == null)
	    return false;
	else return (annot.getVariantType() == VariantType.NONSYNONYMOUS);
    }
  
  
    public boolean is_single_nucleotide_variant () { return (this.ref.length()==1 && this.alt.length()==1); }
    
    /**
     * @return an integer representation of the chromosome  (note: X=23, Y=24).
     */
    public int get_chromosome() { return chromosome; }
    /**
     * @return an byte representation of the chromosome, e.g., 1,2,...,22 (note: X=23, Y=24, MT=25).
     */
    public byte getChromosomeAsByte() { return chromosome; }

    public String get_chromosome_as_string() {
	if (this.chromosome == (byte)23)
	    return "chrX";
	else if (this.chromosome == (byte)24)
	    return "chrY";
	else if (this.chromosome == (byte)25)
	    return "chrM";
	else
	    return String.format("chr%d",this.chromosome);

    }
   

    public boolean is_X_chromosomal() { return this.chromosome == X_CHROMOSOME;  }
    
   
    /**
     * @return The PHRED quality of this variant call.
     */
    public float get_variant_quality() { return this.variant_quality; }

    /**
     * @return the {@link jannovar.genotype.GenotypeCall GenotypeCall} object corresponding to this variant.
     */
    public GenotypeCall getGenotype() { return this.genotype; }

    /**
     * @return A string representing the genotype of this variant.
     */
    public String getGenotypeAsString() {
	return this.genotype.get_genotype_as_string();
    }

    public String get_genotype_as_string() {
	return this.genotype.get_genotype_as_string();
    }

    /**
     * This function uses the function of the same name of the 
     * the {@link jannovar.genotype.GenotypeCall GenotypeCall} object
     * corresponding to this variant.
     * @return A list of genotype calls, e.g., "0/0","0/1","1/1"
     */
    public ArrayList<String> getGenotypeList() {
	return this.genotype.getGenotypeList();
    }

    

    public String get_position_as_string() { return Integer.toString(this.position); }
   
    /**
     * @return variant expressed in chromosomal coordinates 
     */
    public String get_chromosomal_variant() {
	StringBuilder sb = new StringBuilder();
	sb.append( get_chrom_string() );
	sb.append(":g.");
	sb.append(this.position + ref + ">" + alt);
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
     * {@link jannovar.annotation.Annotation Annotation} object. If this
     * is not initialized (which should never happen), it returns ".".
     *<p>
     * Note: This function returns a String that summarizes all of the annotations
     * of the current variant, e.g., 
     * <p>
     * LTF(uc003cpr.3:exon5:c.30_31insAAG:p.R10delinsRR,uc003cpq.3:exon2:c.69_70insAAG:p.R23delinsRR,
     * uc010hjh.3:exon2:c.69_70insAAG:p.R23delinsRR)
     * </P>
     * If client code wants to get a list of each individual annotation, it should instead call 
     * the function {@link #getAnnotationList}.
     * @return The annotation of the current variant.
     */
    public String getAnnotation()
    {
	try { 
	    if (this.annot != null)
		return this.annot.getVariantAnnotation();
	     else return ".";
	} catch (AnnotationException e) {
	    return ".";
	}
	   
    }

    /**
     * This function returns a list of all of the 
     * {@link jannovar.annotation.Annotation Annotation} objects
     * that have been associated with the current variant. This function
     * can be called if client code wants to display one line for each
     * affected transcript, e.g., 
     * <ul>
     * <li>LTF(uc003cpr.3:exon5:c.30_31insAAG:p.R10delinsRR)
     * <li>LTF(uc003cpq.3:exon2:c.69_70insAAG:p.R23delinsRR)
     * <li>LTF(uc010hjh.3:exon2:c.69_70insAAG:p.R23delinsRR)
     * </ul>
     * <P>
     * If client code wants instead to display just
     * a single string that summarizes all of the annotations, it should
     * call the function {@link #getAnnotation}.
     */
    public ArrayList<String> getAnnotationList() {
	if (this.annot == null) {
	    ArrayList<String> A = new ArrayList<String>();
	    A.add(".");
	    return A;
	}
	ArrayList<Annotation> alist = this.annot.getAnnotationList();
	ArrayList<String> A = new ArrayList<String>();
	for (Annotation ann : alist) {
	    String s = ann.getVariantAnnotation();
	    A.add(s);
	}
	return A;
    }


    public String getVariantType() {
	if (this.annot != null)
	    return this.annot.getVariantType().toString();
	else return "?";
    }

    /**
     * @see jannovar.common.Constants
     * @return a enum constant representing the variant class (NONSENSE, INTRONIC etc)
     */
    public VariantType getVariantTypeConstant() {
	if (this.annot != null)
	    return this.annot.getVariantType();
	else
	    return VariantType.ERROR;
    }

   
   
   
    
    /**
     * @return A String representing the variant in the chromosomal sequence, e.g., chr17:c.73221527G>A
     */
    public String getChromosomalVariant() {
	return String.format("%s:c.%d%s>%s",get_chrom_string(), position, ref, alt);
    }



    public String toString() {
	StringBuilder sb = new StringBuilder();
	String chr = getChromosomalVariant();
	sb.append("\t"+ chr);
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
	    return this.annot.getVariantType().toString();
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