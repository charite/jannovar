package exomizer.io;

import exomizer.exome.GenotypeI;
import exomizer.exception.VCFParseException;

/**
 * This interface is intended to provide the flexibility
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
 * @version 0.03 (13 February, 2013)
 */
public abstract class GenotypeFactoryA {


    /** 
     * This is the core method of the factory, and creates
     * a Genotype object. Currently, the Exomizer has
     * two concrete implementations of the {@link exomizer.exome.GenotypeI GenotypeI}
     * interface, for single and multiple-sample VCF files. The interface
     * could be extended to allow for cool stuff such as DP4 analysis in
     * the future.
     * @param A an array of Strings with the tab-separated fields of a VCF line.
     */
    public abstract GenotypeI createGenotype(String A[])  throws VCFParseException;


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


}