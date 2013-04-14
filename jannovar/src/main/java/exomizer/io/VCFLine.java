package exomizer.io;

import org.apache.log4j.Logger;

import exomizer.common.Constants;
import exomizer.exception.VCFParseException;
import exomizer.exome.Variant;
import exomizer.io.GenotypeFactoryA;
import exomizer.exome.GenotypeI;

import java.util.MissingFormatArgumentException;
/**
 * Parse a VCF line and provide convenient access / getter functions.
 * <P>
 * This class now provides support for both single-sample and multiple-sample
 * VCF files. Note that for a single-sample VCF file, we expect the following format (#CHROM line):
 * <PRE>
 * #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	name1
 * </PRE>
 * On the other hand, for multiple-sample files, we expect:
 * <PRE>
 * #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	name1   name2   name3  ...
 * </PRE>

 * <P>Note that for the VCF files we are interested in, there must be a FORMAT field.
 * @version 0.11 (Feb. 11, 2013)
 * @author Peter N Robinson
 */
public class VCFLine implements Constants {
    static Logger log = Logger.getLogger(VCFLine.class.getName());
    /** This field stores the original VCF file, which can be used for debugging purposes to compare the
	annotations inferred by the exomizer code with the original VCF file. */
    private String vcf_line = null;
    private String chromosome = null;
    private int position;
    /** Reference nucleotide or sequence */
    private String ref =null;
    /** Alternate nucleotide/sequence */
    private String alt=null;
    /** The quality of the variant call, the QUAL column in the VCF file. */
    private float variant_quality=0f;
   
    /** Factory object to create {@link exomizer.exome.GenotypeI GenotypeI} objects. Note that
     * this is an abstract class that will be instantiated depending on the information in the
     * #CHROM line of the VCF file (especially, whether there is a single sample or multiple
     * samples).
     */
    private static GenotypeFactoryA genofactory=null;
    /**
     * An object that represents the genotype either of a single file or
     * of each of the samples in a multiple-sample VCF file. This must be
     * instantiated with a {@link exomizer.exome.SingleGenotype SingleGenotype}
     * or a {@link exomizer.exome.MultipleGenotype MultipleGenotype}
     * object.
     */
    private GenotypeI gtype=null;
      
  
    /** getters */
    public String get_chromosome_as_string() { return this.chromosome; }
    public int get_position() { return this.position; }
    public String get_reference_sequence() { return this.ref; }
    public String get_alternate_sequence() { return this.alt; }
    public String get_variant_sequence() { return this.alt; }
    /**
     * @return A Genotype object representing the genotype of a single sample 
     * or multiple samples for this variant
     */
    public GenotypeI getGenotype() { return this.gtype; }
    public String get_genotype_as_string() {
	return this.gtype.toString();
    }
   

    /**
     * Set the factory object to create {@link exomizer.exome.GenotypeI GenotypeI} objects.
     * Note that we assume that all lines in the VCF file require the same factory
     * (single or multiple sample files), and thus make this a static class member.
     */
    public static void setGenotypeFactory(GenotypeFactoryA fac) {
	VCFLine.genofactory = fac;
    }
   
     

    /**
     * Parses a single line of a VCF file, and initialize all the values of this object. */
    public VCFLine(String line) throws VCFParseException {
	this.vcf_line = line;
	parse_line(line);
    }


    /**
     * This is a factory method that creates a {@link exomizer.exome.Variant Variant}
     * object corresponding to the current VCF file line.
     * @return A {@link exomizer.exome.Variant Variant} containing data about the variant in this VCF line.
     */
    public Variant extractVariant() throws VCFParseException {
	Variant var = null;
	var = new Variant();
	var.setChromosome(this.chromosome);
	/* Note that some nonstandard VCF files may have some bases in lower
	   case for better human readability (indel mutations, especially). */
	this.ref = ref.toUpperCase();
	this.alt = alt.toUpperCase();
	/* Note that some positions may have more than one variant base. For now,
	   we will just take the first variant, but for the future it would be better to
	   make this more flexible. */
	int idx = alt.indexOf(",");
	if (idx>0) {
	    alt = alt.substring(0,idx);
	}
	if (this.ref.length() == 1 && 
	    this.alt.length() == 1) {
	    /* i.e., single nucleotide variant */
	    var.setRef(this.ref);
	    var.setVar(this.alt);
	    var.setPosition(this.position);
	} else if (ref.length() > alt.length()) {
	    /* deletion or block substitution */
	    String head = ref.substring(0,alt.length());
	    /*System.out.println(String.format("1) ref=%s (%d nt), alt=%s (%d nt), head=%s (%d nt)",
	      ref,ref.length(),alt,alt.length(),head,head.length()));*/
	    /* For instance, if we have ref=TCG, alt=T, there is a two nt deletion, and head is "T" */
	    if (head.equals(alt)) {
		int pos = this.position + head.length(); /* this advances to position of mutation */
		var.setPosition(pos);
		String r = ref.substring(alt.length());
		var.setRef(r);
		var.setVar("-");
	    } else {
		var.setPosition(this.position);
		var.setRef(ref);
		var.setVar(alt);
	    }
	} else if(alt.length() >= ref.length ()) { 
	    /*  insertion or block substitution */
	    String head = alt.substring(0,ref.length()); /* get first L nt of ALT (where L is length of REF) */
	    /* System.out.println(String.format("2) ref=%s (%d nt), alt=%s (%d nt), head=%s (%d nt)",
	       ref,ref.length(),alt,alt.length(),head,head.length()));*/
	    if (head.equals(ref)) {
		int pos = this.position + ref.length() - 1;
		var.setPosition(pos);
		String mut = alt.substring(ref.length());
		var.setRef("-");
		var.setVar(mut);
	    } else {
		var.setPosition(this.position);
		var.setRef(ref);
		var.setVar(alt);
	    }
	}

	var.set_variant_quality(Math.round(this.variant_quality));
	var.setGenotype(this.gtype);
	var.setVCFline(this.vcf_line);
	return var;
    }



     /**
     * Parse one line of a VCF file and decide whether this is a single nucleotide variant
     * The VCF format always has 8 fixed, mandatory columns. 
     * 0)CHROM  e.g., 1
     * 1)POS    e.g. 866511
     * 2)ID     e.g. rs60722469
     * 3)REF    e.g. C
     * 4)ALT    e.g. CCCCT
     * 5)QUAL  e.g.  258.62
     * 6)FILTER  e.g.  PASS
     * 7)INFO   e.g.   AC=2;AF=1.00;AN=2;DB;DP  (....)
     * All data lines are tab-delimited. In all cases, missing values are specified with a dot (”.”). 
     * <P>
     * The code passes the remaining fields, FORMAT and the sample fields, to the corresponding
     * genotype factory objects that "know" whether it is a single or multiple sample VCF.
     * @param line A single line of a VCF file representing one variant
     */
    private void parse_line(String line) throws VCFParseException {
	String A[] = line.split("\t");
	if (A.length < 10) throw new VCFParseException("Less than 10 fields in VCF line:" + line);
	this.ref = A[3];
	
	if (ref.equals(".")  || ref.length()<1 ) throw new VCFParseException("Could not parse ref field: \"" + ref + "\"\n" + line);
	this.alt = A[4]; 
	if (alt.equals(".")  || alt.length()<1 ) throw new VCFParseException("Could not parse alt field:\"" +
											  alt + "\"\n" + line);
	if (A[5].equals(".")){
		this.variant_quality = 0;	
	}
	else{
	    this.variant_quality = parseVariantQuality(A[5]);
	}
	this.chromosome = A[0];
	Integer pos = null;

	try {
	    pos = Integer.parseInt(A[1]);
	    this.position = pos.intValue();
	} catch (NumberFormatException e) {
	    String error = "Number format exception while parsing the position field: \"" + A[1] + "\" in VCF line "+line;
	    throw new VCFParseException(error);
	}


	/* Note that the genotype factory works for either single-sample or
	* multiple-sample VCF files! */
	try {
	    this.gtype = VCFLine.genofactory.createGenotype(A);
	} catch (VCFParseException e) {
	    String err = e.toString() + " on line: \n" + line;
	    throw new VCFParseException(err);
	}
	
    }

    
    /**
     * Parses the QUAL field of a VCF file with the PHRED score for the variant.
     * Note that the quality is mostg often given as an integer, but occasionally
     * may be a floating number. In the latter case, we will round the quality to 
     * the nearest integer.
     * @param q The PHRED quality score represented as a String
     * @return the quality score parsed to the nearest integer.
     */
    private int parseVariantQuality(String q) throws NumberFormatException {
	int pos = q.indexOf(".");
	int qual;
	if (pos < 0)
	    qual = Integer.parseInt(q);
	else { /* i.e., the quality is a number such as 55.16 */
	    Float fQ = Float.parseFloat(q);
	    float f = Math.round(fQ.floatValue());
	    qual = (int) f;
	}
	return qual;
    }
  
  
	    
	    
	  

    /**
     * This method can be used to show the state of the current VCFLine
     * object for debugging.
     */
    public void dump_VCF_line_for_debug()
    {
	System.err.println(vcf_line);
	System.err.println("chromosome: " + get_chromosome_as_string());
	System.err.println("position: " + get_position() );
	System.err.println("reference sequence:" +  get_reference_sequence() );
	System.err.println("alt sequence:" +  get_alternate_sequence() );
	System.err.println("genotype: " +  get_genotype_as_string());

    }


}
