package jannovar.genotype;

import jannovar.common.Genotype;
import jannovar.exception.VCFParseException;

/**
 * This class and its two subclasses provide the flexibility
 * needed to deal with the various kinds of VCF file formats.
 * For instance, VCF files may contain a single sample or multiple
 * samples, and they may have different attributes depending on the
 * actual format of the VCF file as defined in the FORMAT field, e.g.,
 * <PRE>
 * GT:AD:DP:GQ:PL
 * </PRE>
 * This class is an implementation of the abstract factory design pattern.
 * The clients of the class are concrete instances of the Genotype
 * abstract class. These in turn have various interfaces to describe
 * the possible variations in VCF format.
 * @author Peter N Robinson
 * @version 0.05 (1 November, 2013)
 */
public abstract class GenotypeFactoryA {
    /** 
     * This is the core method of the factory, and creates
     * a  {@link jannovar.genotype.GenotypeCall GenotypeCall} object.
     * @param A an array of Strings with the tab-separated fields of a VCF line.
     * @return the newly created {@link GenotypeCall} object
     * @throws jannovar.exception.VCFParseException
     */
    public abstract GenotypeCall createGenotype(String A[])  throws VCFParseException;


    /**
     * @param q The PHRED quality score represented as a String
     * @return the quality score parsed to the nearest integer.
     */
    protected int parseGenotypeQuality(String q) throws NumberFormatException {
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
     * @param DP the DEPTH of a VCF file (which is within the Genotype Field)
     * @return the depth of the read as an integer.
     */
    protected int parseGenotypeDepth(String DP) throws NumberFormatException {
	int pos = DP.indexOf(".");
	int d;
	if (pos < 0)
	    d = Integer.parseInt(DP);
	else { /* i.e., the quality is a number such as 55.16 */
	    Float fQ = Float.parseFloat(DP);
	    float f = Math.round(fQ.floatValue());
	    d = (int) f;
	}
	return d;
    }


    Genotype parseGenotypeString(String genot) {
    	Genotype call= Genotype.UNINITIALIZED;
    	if (genot.equals("0/1") || genot.equals("0|1") || genot.equals("1|0") || genot.equals("0/2"))
	    call = Genotype.HETEROZYGOUS; 
	else if (genot.equals("1/1") || genot.equals("1|1") || genot.equals("2/2") || genot.equals("1"))
	    call = Genotype.HOMOZYGOUS_ALT;
	else if (genot.equals("0/0") || genot.equals("0|0"))
	    call = Genotype.HOMOZYGOUS_REF;
	else if (genot.equals("1"))
	    call = Genotype.HOMOZYGOUS_ALT; /* hemizygous male X chromosome call */
	else if (genot.equals("./.") || genot.equals(".")) {
	    /* In this case, there is only one subfield, "./." 
	       instead of say "0/0:1,0:1:3:0,3,33" */
	    call = Genotype.NOT_OBSERVED;
	} 
	return call;
    }


}