package jannovar.genotype;

import jannovar.genotype.GenotypeI;
import jannovar.genotype.SingleGenotype;
import jannovar.common.Genotype;
import jannovar.exception.VCFParseException;

/**
 * This class is inteded to create a Genotype object for
 * VCF files that represent a single sample.
 * <P>
 * Note that for now, we deal with fields such as 0/2 that indicate
 * a second ALT nucleotide, by essentially conflating them with 0/1. This
 * should be fixed in a future version of this class, but it occurs relatively
 * rarely in VCF files that are of interest to us.
 * @author Peter N Robinson
 * @version 0.05 (5 May, 2013)
 */
public class SingleGenotypeFactory extends GenotypeFactoryA  {

    private int UNINITIALIZED_INT = -10;

    /** 
     * This is the core method of the factory, and creates
     * a Genotype object. Noting that field 9 (i.e., 8 with
     * zero-based numbering) has the FORMAT field and the following field
     * has the genotype. Note that the client code should have checked that
     * the array A has ten fields
     * @param A an array with the fields of the VCF line.
     */
    public SingleGenotype createGenotype(String A[]) throws VCFParseException {
	SingleGenotype gt = parse_genotype(A[8],A[9]);
	return gt;
    }



    /**
     * We are expecting to get two fields from the VCF file, from which we will parse the genotype.
     * @param format VCF FORMAT field, e.g., GT:PL:GQ	
     * @param sample VCF sample field, e.g., 1/1:21,9,0:17
     */
    private SingleGenotype  parse_genotype(String format, String sample) throws VCFParseException {
	
	/* one of HOMOZYGOUS_REF,HOMOZYGOUS_VAR, HETEROZYGOUS or UNKNOWN */
	Genotype call= Genotype.UNINITIALIZED;
	/* The overall genotype quality as parsed from the QUAL field. If this field was given as
	   a float, then it is rounded to the nearest integer. */
	int genotype_quality=UNINITIALIZED_INT;
	String A[] = format.split(":");
	int gt_index = -1; // index of genotype field
	int qual_idx = -1; //index of genotype quality field
	for (int i=0;i<A.length; ++i) {
	    if (A[i].equals("GT")) { gt_index = i;}
	    if (A[i].equals("GQ")) { qual_idx =i; }
	}
	if (gt_index < 0) {
	    String s = String.format("Could not find genotype field in FORMAT field: \"%s\"",format);
	    throw new VCFParseException(s);
	}
	String B[] = sample.split(":");
	String genot = B[gt_index];
	//Added code to deal with male chr X genotypes
	
	if (genot.equals("0/1") || genot.equals("0|1") || genot.equals("1|0") || genot.equals("0/2"))
	    call = Genotype.HETEROZYGOUS; 
	else if (genot.equals("1/1") || genot.equals("1|1") || genot.equals("2/2"))
	    call = Genotype.HOMOZYGOUS_ALT;
	else if (genot.equals("0/0") || genot.equals("0|0"))
	    call = Genotype.HOMOZYGOUS_REF;
	else if (genot.equals("1"))
	    call = Genotype.HOMOZYGOUS_ALT;

	if (qual_idx >= 0) {
	    try {
		genotype_quality = parseGenotypeQuality(B[qual_idx]); 
	    } catch (NumberFormatException e) {
		String err = "Could not parse genotype quality field \"" + B[qual_idx] 
		    +  "\" due to a Number Format Exception:" + e.toString();
		throw new VCFParseException(err); 
	    } catch (Exception e) {
		String err = "Could not parse format field: " + format +": Exception:\n\t" + e.toString();
		throw new VCFParseException(err); 
	    }
	}
	SingleGenotype gt = new SingleGenotype(call,genotype_quality);
	return gt;
    }

    


}
/* eof */