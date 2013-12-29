package jannovar.genotype;


import jannovar.genotype.GenotypeCall;
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
 * @version 0.08 (29 December, 2013)
 */
public class SingleGenotypeFactory extends GenotypeFactoryA  {
    /** A flag that a value has not ben initialized. */
    private static int UNINITIALIZED_INT = -10;

    /** 
     * This is the core method of the factory, and creates
     * a Genotype object. Noting that field 9 (i.e., 8 with
     * zero-based numbering) has the FORMAT field and the following field
     * has the genotype. Note that the client code should have checked that
     * the array A has ten fields
     * @param A an array with the fields of the VCF line.
     */
    public GenotypeCall createGenotype(String A[]) throws VCFParseException {
	GenotypeCall gt = parse_genotype(A[7],A[8],A[9]);
	return gt;
    }



    /**
     * We are expecting to get two fields from the VCF file, from which we will parse the genotype.
     * Note that the genotype quality field GQ is not required, and if it is not present, the
     * value will remain UNINITIALIZED_INT.
     * @param format VCF FORMAT field, e.g., GT:PL:GQ	
     * @param sample VCF sample field, e.g., 1/1:21,9,0:17
     */
    private GenotypeCall  parse_genotype(String info, String format, String sample) throws VCFParseException {
	
	/* one of HOMOZYGOUS_REF,HOMOZYGOUS_VAR, HETEROZYGOUS or UNKNOWN */
	Genotype call= Genotype.UNINITIALIZED;
	/* The overall genotype quality as parsed from the QUAL field. If this field was given as
	   a float, then it is rounded to the nearest integer. */
	int genotype_quality=UNINITIALIZED_INT;
	int genotype_depth=UNINITIALIZED_INT;
	String A[] = format.split(":");
	int gt_idx = UNINITIALIZED_INT; // index of genotype field
	int qual_idx = UNINITIALIZED_INT; //index of genotype quality field
	int depth_idx = UNINITIALIZED_INT; // index of the depth field.
	for (int i=0;i<A.length; ++i) {
	    if (A[i].equals("GT")) { gt_idx = i;}
	    if (A[i].equals("GQ")) { qual_idx =i; }
	    if (A[i].equals("DP")) { depth_idx =i; }
	}
	if (gt_idx == UNINITIALIZED_INT) {
	    String s = String.format("Could not find genotype field in FORMAT field: \"%s\"",format);
	    throw new VCFParseException(s);
	}
	String B[] = sample.split(":");
	String genot = B[gt_idx];
	
	call = parseGenotypeString(genot);
	if (call == Genotype.NOT_OBSERVED) {
	    qual_idx = UNINITIALIZED_INT; 
	    depth_idx = UNINITIALIZED_INT;
	    /* Even though the FORMAT field is OK, there is only ./. for the genotype field, 
	       and there is no quality/depth subfield. Resetting qual_idx to -1 causes the following
	       if clause to be skipped. */
	} 
	if (qual_idx != UNINITIALIZED_INT) {
	    try {
		genotype_quality = parseGenotypeQuality(B[qual_idx]); 
	    } catch (NumberFormatException e) {
		String err = "Could not parse genotype quality field \"" + B[qual_idx] 
		    +  "\" due to a Number Format Exception:" + e.toString();
		throw new VCFParseException(err); 
	    } catch (Exception e) {
		String err = "Could not parse genotype quality field: " + format +": Exception:\n\t" + e.toString();
		throw new VCFParseException(err); 
	    }
	}
	if (depth_idx != UNINITIALIZED_INT) {
	    try {
		genotype_depth = parseGenotypeDepth(B[depth_idx]); 
	    } catch (NumberFormatException e) {
		String err = "Could not parse genotype depth field \"" + B[depth_idx] 
		    +  "\" due to a Number Format Exception:" + e.toString();
		throw new VCFParseException(err); 
	    } catch (Exception e) {
		String err = "Could not parse genotype depth field: " + format +": Exception:\n\t" + e.toString();
		throw new VCFParseException(err); 
	    }
	} else {
	    genotype_depth = parseDepthFromInfo(info);
	}

	if (genotype_depth != UNINITIALIZED_INT) {
	    return new GenotypeCall(call,genotype_quality,genotype_depth);
	} else {
	    return new GenotypeCall(call,genotype_quality);
	}
    }

    /**
     * In single-sample VCF files, the Read depth (DP) may be stored in the INFO field.
     * We try to parse that here.  INFO fields are encoded as a semicolon-separated series of short keys.
     * A typical INFO string might be "DP=20;VDB=0.0141;AF1=1;AC1=12;DP4=0"
     */
    //GENE=FGFR2;INHERITANCE=AD;MIM=101600
    private int parseDepthFromInfo(String info) {
	int i = info.indexOf("DP="); 
	if (i<0)
	    return UNINITIALIZED_INT; // Could not find it.
	i += 3; // advance to after the "DP="
	int j = info.indexOf(";",i);
	Integer dp=null;
	try {
	    if (j<0) { // the DP could be the last subfield, then it is not terminated by semicolon
		dp = Integer.parseInt(info.substring(i));
	    } else {
		dp = Integer.parseInt(info.substring(i,j));
	    }
	} catch (NumberFormatException e) {
	    //e.printStackTrace();
	    System.err.println("INOF=" + info);
	    System.err.println("i=" + i);
	    return UNINITIALIZED_INT;
	}
	return dp.intValue();
    }
    
}
/* eof */