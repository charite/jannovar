package jannovar.io;

import jannovar.exception.ChromosomeScaffoldException;
import jannovar.exception.VCFParseException;
import jannovar.exome.Variant;
import jannovar.genotype.GenotypeFactoryA;
import jannovar.genotype.GenotypeCall;

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
 * @version 0.14 (29 December, 2013)
 * @author Peter N Robinson
 */
public class VCFLine {
    /** This field stores the original VCF file for debugging purposes (or leave null). */
    private String vcf_line = null;
    /** A byte representation of the chromosome, e.g., 2 is chr 2, 23 is X, 24 is Y, 25 is Mt.*/
    private byte chromosome;
    /** Start position of the variant on the chromosome. */
    private int position;
    /** Reference nucleotide or sequence */
    private String ref =null;
    /** Alternate nucleotide/sequence */
    private String alt=null;
    /** The quality of the variant call, the QUAL column in the VCF file. */
    private float phredScore;
   
    /** Factory object to create {@link jannovar.genotype.GenotypeCall GenotypeCall}
     * objects. Note that this is an abstract class that will be instantiated
     * depending on the information in the #CHROM line of the VCF file
     * (especially, whether there is a single sample or multiple
     * samples).
     */
    private static GenotypeFactoryA genofactory=null;
    /**
     * An object that represents the genotype either of a single file or
     * of each of the samples in a multiple-sample VCF file. 
     */
    private GenotypeCall gtype=null;
    /**
     * A flag that if true causes the original VCF lines to be stored by the
     * individual {@link jannovar.io.VCFLine VCFLine} objects.
     */
    private static boolean storeVCFlines=false;
      
  
    /** getters */
    /** @return a byte representationof the chromosome, 1-22, 23=X, 24=Y, 25=MT */
    public byte get_chromosome() { return this.chromosome; }
    /** @return position of the variant on the chromosome. */
    public int get_position() { return this.position; }
    /** @return reference sequence that is changed by the variant. */
    public String get_reference_sequence() { return this.ref; }
    /** @return The alternate (variant) sequence. */
    public String get_alternate_sequence() { return this.alt; }
    /**
     * @return A Genotype object representing the genotype of a single sample 
     * or multiple samples for this variant
     */
    public GenotypeCall getGenotype() { return this.gtype; }
    /**
     * @return the Phred quality score for the variant call (cast to an integer)
     */
    public float getVariantPhredScore() { return this.phredScore; }
    
    /**
     * Calling this method causes the static variable {@link #storeVCFlines}
     * to be set to true, which causes the original VCF lines to be stored by the
     * individual {@link jannovar.io.VCFLine VCFLine} objects.
     */
    public static void setStoreVCFLines() {
	VCFLine.storeVCFlines = true;
    }
    /**
     * Calling this method causes the static variable {@link #storeVCFlines}
     * to be set to false, such that the individual VCF lines are not stored.
     */
    public static void unsetStoreVCFLines() {
	VCFLine.storeVCFlines = false;
    }
   

    /**
     * Set the factory object to create {@link jannovar.genotype.GenotypeCall GenotypeCall} objects.
     * Note that we assume that all lines in the VCF file require the same factory
     * (single or multiple sample files), and thus make this a static class member.
     * <p>
     * The intended use is for the VCFReader to call this with either a
     * Single- or Multiple-Sample Genotype Factory after it has parsed the header
     * (the #CHROM line contains the names of the sample or samples for which the
     * genotypes are recorded in each variant line of the VCF file).
     * @param fac genotype factory
     */
    public static void setGenotypeFactory(GenotypeFactoryA fac) {
	VCFLine.genofactory = fac;
    }
   
     

    /**
     * Parses a single line of a VCF file, and initialize all the values of this object.
     * @param line single line from VCF file
     * @throws jannovar.exception.VCFParseException 
     */
    public VCFLine(String line) throws VCFParseException {
	if (VCFLine.storeVCFlines) {
	    this.vcf_line = line;
	}
	parse_line(line);
    }



     /**
     * Parse one line of a VCF file and decide whether this is a single nucleotide variant
     * The VCF format always has 8 fixed, mandatory columns.
     * <ul>
     * <li> 0) CHROM  e.g., 1
     * <li> 1) POS    e.g. 866511
     * <li> 2) ID     e.g. rs60722469
     * <li> 3) REF    e.g. C
     * <li> 4) ALT    e.g. CCCCT
     * <li> 5) QUAL  e.g.  258.62
     * <li> 6) FILTER  e.g.  PASS
     * <li> 7) INFO   e.g.   AC=2;AF=1.00;AN=2;DB;DP  (....)
     * </ul>
     * All data lines are tab-delimited. In all cases, missing values are specified with a dot (”.”). 
     * <P>
     * The code passes the remaining fields, FORMAT (the 8th field) and
     * the sample fields (the 9th... field(s)), to the corresponding
     * genotype factory objects that know whether it is a single or multiple sample VCF.
     * Thus, A valid exome VCF line must have at least 10 fields (for a single sample),
     * and has an additional field for every additional sample.
     * @param line A single line of a VCF file representing one variant
     */
    private void parse_line(String line) throws ChromosomeScaffoldException,VCFParseException {
	String A[] = line.split("\t");
	if (A.length < 10) {
	    throw new VCFParseException("Less than 10 fields in VCF line:" + line);
	}
	this.ref = A[3];
	if (ref.equals(".")  || ref.length()<1 ) {
	    throw new VCFParseException("Could not parse ref field: \"" + ref + "\"\n" + line);
	}
	this.alt = A[4]; 
//	if (alt.equals(".")  || alt.length()<1 ) {
	if (alt.length()<1 ) {
	    throw new VCFParseException("Could not parse alt field:\"" + alt + "\"\n" + line);
	}
	
	this.phredScore = parseVariantQuality(A[5]);
	this.chromosome = convertChromosomeStringToByteValue(A[0]);
	try {
	    Integer pos = Integer.parseInt(A[1]);
	    this.position = pos.intValue();
	} catch (NumberFormatException e) {
	    String error = "Number format exception while parsing the position field: \""
		+ A[1] + "\" in VCF line "+line;
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
	convertToAnnovar(); /* compensate for odd numbering of VCF file */
	//dump_VCF_line_for_debug();
    }


    /**
     * @return the variant represented by this VCFline.
     */
    public Variant toVariant() {
	Variant v = new Variant(this.get_chromosome(),
				this.get_position(),
				this.get_reference_sequence(),
				this.get_alternate_sequence(),
				this.getGenotype(),
				this.getVariantPhredScore());
	return v;
    }

    
    /**
     * Parses the QUAL field of a VCF file with the PHRED score for the variant.
     * Note that the quality is most often given as an integer, but occasionally
     * may be a floating number. In the latter case, we will round the quality to 
     * the nearest integer.
     * <p>
     * If no quality score is provided, then the VCF format specifies that
     * a period (".") be shown. In this case, the function returns 0.
     * @param q The PHRED quality score represented as a String
     * @return the PHRED quality score .
     */
    private float parseVariantQuality(String q)  {
	if (q.equals("."))
	    return 0f;
	try {
	    Float fQ = Float.parseFloat(q);
	    return fQ.floatValue();
	} catch (NumberFormatException e) {
	    System.err.println("[VCFLine] Warning: Unable to parse Phred score: \""+ q + "\"");
	    return 0f;
	}
    }
  
  
	    
    
    /**
     * The VCF format stores some normal sequence together with some indels, and
     * so the positions for indels reported in the VCF file are not necessarily
     * the same as the position of the variants themselves. This function converts
     * the positions reported in VCF files to the precise position of the variant as
     * would be used by Annovar or reported in HGVS mutation nomenclature. The
     * major topics are
     * <ul>
     * <li> All sequences are converted to upper case
     * <li> SNVs are identical in VCF/Annovar and are unchanged.
     * <li> Note that some positions may have more than one variant base. For now,
     *     we will just take the first variant
     * <li> For deletions/block subsitutions, ref is longer than alt in the
     * original VCF file.
     * </ul>
     * Note that this method should be called once after everything else has been
     * parsed. 
     * @throws jannovar.exception.VCFParseException
     */
    public void convertToAnnovar() throws VCFParseException {
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
            /* i.e., single nucleotide variant . THere is
            * no need to correct anything. */
            return; 
        } else if (ref.length() > alt.length()) {
            /* deletion or block substitution */
            String head = ref.substring(0,alt.length());
            /*System.out.println(String.format("1) ref=%s (%d nt), alt=%s (%d nt), head=%s (%d nt)",
              ref,ref.length(),alt,alt.length(),head,head.length()));*/
            /* For instance, if we have ref=TCG, alt=T, there is a two nt deletion,
		and head is "T". Therefore, we have to move past the "T" to get to the position of
		the deletion. */
            if (head.equals(alt)) {
                int pos2 = this.position + head.length(); /* this advances to position of mutation */
                this.position = pos2;
                String r = ref.substring(alt.length());
                this.ref = r;
                this.alt = "-"; /* symbol for a deletion */
		return;
            } else {
		return; /* block substition, nothing to be done. */
            }
        } else if (alt.length() >= ref.length ()) { 
            /*  insertion or block substitution */
            String head = alt.substring(0,ref.length()); /* get first L nt of ALT (where L is length of REF) */
            /* System.out.println(String.format("2) ref=%s (%d nt), alt=%s (%d nt), head=%s (%d nt)",
               ref,ref.length(),alt,alt.length(),head,head.length())); */
            if (head.equals(ref)) {
                int pos2 = this.position + ref.length() - 1;
                this.position  = pos2;//var.setPosition(pos);
                String mut = alt.substring(ref.length());
                this.ref = "-";
                this.alt = mut;
            } else {
                return; /* block substition, nothing to be done. */
            }
        }
    }


    /**
     * @param c a String representation of a chromosome (e.g., chr3, chrX).
     * @return corresponding integer (e.g., 3, 23).
     * @throws jannovar.exception.ChromosomeScaffoldException
     */
    public byte convertChromosomeStringToByteValue(String c) throws ChromosomeScaffoldException {
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
	   ChromosomeScaffoldException cpe = new ChromosomeScaffoldException(c);
	    throw cpe;
	}
	return i;
    }

    /**
     * @return a copy of the original line in the VCF file.
     */
    public String getOriginalVCFLine() {
	return this.vcf_line;
    }
    
	  

    /**
     * This method can be used to show the state of the current VCFLine
     * object for debugging.
     */
    private void dump_VCF_line_for_debug()
    {
	System.err.println(vcf_line);
	System.err.println("chromosome: " + get_chromosome());
	System.err.println("position: " + get_position() );
	System.err.println("reference sequence:" +  get_reference_sequence() );
	System.err.println("alt sequence:" +  get_alternate_sequence() );
    }


}
