package jannovar.genotype;

import java.util.ArrayList;


import jannovar.common.Genotype;
import jannovar.exception.VCFParseException;

/**
 * This class is inteded to create a Genotype object for
 * VCF files that represent multiple samples.
 * <P>
 * Multiple-sample VCF files have a FORMAT field followed by one genotype
 * field for each sample. The following line shows an example with three samples.
 * <PRE>
 * GT:AD:DP:GQ:PL	0/1:172,36:188:99:444,0,6014	0/1:104,39:129:99:966,0,3510	0/1:111,49:144:99:688,0,2524
 * </PRE>
 * In this case, the format field {@code GT:AD:DP:GQ:PL} refers to the following bits of information
 * <UL>
 * <LI>GT="Genotype", encoded as alleles values separated by either of "/" (unphased) or "|" (phased). The allele values are 
 * 0 for the reference allele (what is in the reference sequence), 1 for the first allele listed in ALT, 2 for 
 * the second allele list in ALT and so on. For exome calls, these will be listed as 0/1 etc.
 * <LI>AD=Allelic depths for the ref and alt alleles in the order listed"
 * <LI>DP="Approximate read depth (reads with MQ=255 or with bad mates are filtered)"
 * <LI>GC="Genotype Quality", encoded as a phred quality -10log<sub>10</sub>p(genotype call is wrong)
 * <LI>PL="Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification"
 * </OL>
 * <P>
 * According to the VCF standard (see http://www.1000genomes.org/), there are several keywords in the 
 * genotype field that are standard and reserved across the community. The first sub-field must always 
 * be the genotype ({@code GT}). Another reserved keyword is {@code GQ} for genotype quality. We will therefore
 * attempt to extract {@code GT} and {@code GQ} from the individual genotype fields. Noting that it is
 * possible that the {@code GQ} field is not present, we will replace it with a default value (".") if it
 * is not present in the VCF file.
 * <P>
 * Note that while other fields such as {@code AD} provide important information for individual analysis purposes,
 * we cannot expect a VCF file that somebody uploads via the Webserver to have this information,
 * because it is optional. 
 * @see jannovar.genotype.SingleGenotypeFactory
 * @see jannovar.genotype.GenotypeCall
 * @author Peter N Robinson
 * @version 0.14 (1 November, 2013)
 */
public class MultipleGenotypeFactory extends GenotypeFactoryA {
    /**
     * A list of the calls of the current sample (needs to be reset for each VCF line).
     */
    private ArrayList<Genotype> callList=null;
    /**
     * A list of the genotype qualities of each sample (needs to be reset for each VCF line).
     */
    private ArrayList<Integer> qualityList=null;
    /**
     * A list of the genotype depths (DP) of each sample (needs to be reset for each VCF line).
     */
    private ArrayList<Integer> depthList=null;
    
    
    private final int UNINITIALIZED_INT=-10;
    
    /**
     * The index of the genotype field in the current VCF line. Note that can
     * theoretically be different in different lines, thus we parse this once for
     * each line.
     */
    private int gt_idx = UNINITIALIZED_INT;
    /** The index of the genotype quality field (GQ) in the current VCF line.*/
    private int qual_idx = UNINITIALIZED_INT; 
    /** y The index of the read depth field (DP) in the current VCF line. */
    private int depth_idx = UNINITIALIZED_INT; 

    /** 
     * This is the core method of the factory, and creates
     * a GenotypeCall object that represents a multiple-sample 
     * VCF file. 
     * <P>
     * Note that we do not check here how many fields there are for the 
     * genotypes (there should be the same number in every line). However,
     * client code probably should check that the number of individual
     * genotypes is the same for each line.
     * @return  the newly created {@link GenotypeCall} object
     * @throws jannovar.exception.VCFParseException
     */
    @Override
    public GenotypeCall createGenotype(String []A) throws VCFParseException {
	/* The following two lines have the effect of reseting the ArrayLists
	  * for each new line. */
	this.callList = new ArrayList<Genotype>();
	this.qualityList = new ArrayList<Integer>();
	this.depthList = new ArrayList<Integer>();
	String format = A[8]; /* The ninth field (i.e., 8) of a VCF line is FORMAT */
	/* iterate over all remaining fields, which are the individual genotypes for
	 * each of the samples in the file. */
	
	parseFORMATfield(format);
	for (int i=9;i<A.length;++i) {
	    //System.out.println("i="+i+": " + A[i]);
	    parseGenotypeField(A[i]);
	}
	GenotypeCall mgt = new GenotypeCall(callList,qualityList,depthList);
	return mgt;
    }

    /**
     * The constructor is initialized once, but the state is initialized once
     * for each VCF line.
     */
    public MultipleGenotypeFactory() {
	/* no-op */
    }


   
    /**
     * Parse the FORMAT field of the current line to extract the
     * genotype subfield index and the genotype-quality subfield
     * index. Note that the quality field (GQ) is optional according
     * to the VCF specification, but the GT field is mandatory.
     * @param format The FORMAT field, e.g., {@code GT:AD:DP:GQ:PL}.
     */
    private void parseFORMATfield(String format) throws VCFParseException {
	String A[] = format.split(":");
	this.gt_idx = UNINITIALIZED_INT;
	this.qual_idx = UNINITIALIZED_INT;
	this.depth_idx = UNINITIALIZED_INT; // index of the depth field.
	for (int i=0;i<A.length; ++i) {
	    if (A[i].equals("GT")) { gt_idx = i;}
	    if (A[i].equals("GQ")) { qual_idx =i; }
	    if (A[i].equals("DP")) { depth_idx =i; }
	}
	if (gt_idx < 0) {
	    String s = String.format("Could not find genotype field in FORMAT field: \"%s\"",format);
	    throw new VCFParseException(s);
	}
    }


    /**
     * We are expecting to get two fields from the VCF file, from which we will parse the genotype.
     * The method should be call once for each of the samples in the VCF file.
     * @param sample VCF sample field, e.g., 1/1:21,9,0:17
     */
    private void parseGenotypeField(String sample) throws VCFParseException {
	//System.out.println("Parse gt sample="+sample);

    /* one of HOMOZYGOUS_REF,HOMOZYGOUS_VAR, HETEROZYGOUS or NOT_OBSERVED ("./.") */
	Genotype call;
	/* The overall genotype quality as parsed from the QUAL field. If this field was given as
	   a float, then it is rounded to the nearest integer. */
	int genotype_quality=UNINITIALIZED_INT;
	int genotype_depth =UNINITIALIZED_INT;
	
	String B[] = sample.split(":");
	String genot = B[this.gt_idx];

	call = parseGenotypeString(genot);

	if (call == Genotype.NOT_OBSERVED) {
	    qual_idx = UNINITIALIZED_INT; 
	    depth_idx = UNINITIALIZED_INT;
	    /* Even though the FORMAT field is OK, there is only ./. for the genotype field, 
	       and there is no quality/depth subfield. Resetting qual_idx to -1 causes the following
	       if clause to be skipped. */
	} 
	if (this.qual_idx != UNINITIALIZED_INT) {
	    try {
		genotype_quality = parseGenotypeQuality(B[qual_idx]); 
	    } catch (NumberFormatException e) {
		String err = "Could not parse genotype quality field \"" + B[qual_idx] 
		    +  "\" due to a Number Format Exception:" + e.toString();
		throw new VCFParseException(err); 
//	    } catch (NumberFormatException e) {
//		String err = "Could not parse genotype quality field for sample \"" + sample 
//		    +  "\" due to:" + e.toString();
//		throw new VCFParseException(err); 
	    }
	}
	if (this.depth_idx != UNINITIALIZED_INT) {
	    try {
		genotype_depth = parseGenotypeDepth(B[depth_idx]); 
	    } catch (NumberFormatException e) {
		String err = "Could not parse genotype depth field \"" + B[depth_idx] 
		    +  "\" due to a Number Format Exception:" + e.toString();
		throw new VCFParseException(err); 
//	    } catch (Exception e) {
//		String err = "Could not parse genotype depth field in sample: " 
//		    + sample +": Exception:\n\t" + e.toString();
//		throw new VCFParseException(err); 
	    }
	}


	/* when we get here, we have successfully parsed the GT field. If there was a QC field,
	   we have successfully parsed it, otherwise, genotype_quality is still UNINITIALIZED_INT.
	   Similary, if there was a DP field, genotype_depth has a value, otherwise it is still
	   UNINITIALIZED_INT.
	*/
	this.callList.add(call);
	this.qualityList.add(genotype_quality);
	this.depthList.add(genotype_depth);

    }


}