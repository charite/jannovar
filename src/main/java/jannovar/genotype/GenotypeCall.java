package jannovar.genotype;

import java.util.ArrayList;
import java.util.Iterator;


import jannovar.common.Genotype;


/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with a single or will multiple
 * samples. The individual calls for each sample are stored in 
 * {@link #callList}, and the corresponding qualities are stored in {@link #qualityList}.
 * <P>
 * The class also stores the values for DP (read depth at this position for this sample) and
 * GQ (genotype quality, encoded as a phred quality -10log_10p(genotype call is wrong), both
 * stored as lists of Integer values. These fields are present in all VCF files that we are interested in.
 * If there is no value for it, we return 0.
 * @author Peter Robinson
 * @version 0.11 (17 November, 2013)
 */
public class GenotypeCall  {

  
    /**  
     * List of genotype calls (See {@link jannovar.common.Genotype Genotype})
     * for one variant.
     */
    private ArrayList<Genotype> callList = null;
    /**
     * List of Phred-scaled qualities for the genotype calls in {@link #callList}.
     */
    private ArrayList<Integer> qualityList = null;

    /**
     * List of read depth values for the genotypes at hand (VCF DP field).
     */
    private ArrayList<Integer> depthList=null;
    
    /**
     * The constructor takes lists of calls and qualities that have been parsed from 
     * a single VCF line by the {@link jannovar.genotype.MultipleGenotypeFactory MultipleGenotypeFactory}.
     * By assumption, there are multiple samples, which are described elsewhere by a PED file.
     * @param calls A list of the genotype calls, one for each sample
     * @param qualities A list of the genotype Phred qualities, one for each sample.
     */
    public GenotypeCall(ArrayList<Genotype> calls,ArrayList<Integer> qualities) {
	this.callList = calls;
	this.qualityList = qualities;
    }

    /**
     * The constructor takes lists of calls and qualities that have been parsed from 
     * a single VCF line by the {@link jannovar.genotype.MultipleGenotypeFactory MultipleGenotypeFactory}.
     * By assumption, there are multiple samples, which are described elsewhere by a PED file.
     * @param calls A list of the genotype calls, one for each sample
     * @param qualities A list of the genotype Phred qualities, one for each sample.
     * @param depths A list of the genotype read depth values, one for each sample.
     */
    public GenotypeCall(ArrayList<Genotype> calls,ArrayList<Integer> qualities,ArrayList<Integer> depths) {
	this.callList = calls;
	this.qualityList = qualities;
	this.depthList = depths;
    }


    /**
     * This constructor is intended to be used for VCF files with a single
     * sample, which by assumption contains data from a patient.
     * @param gt the {@link Genotype}
     * @param qual the quality
     */
    public GenotypeCall(Genotype gt, Integer qual) {
	this.callList = new ArrayList<Genotype>();
	this.callList.add(gt);
	this.qualityList = new ArrayList<Integer>();
	this.qualityList.add(qual);
    }

     /**
     * This constructor is intended to be used for VCF files with a single
     * sample, which by assumption contains data from a patient. This constructor
     * is used to register data about the read depth of the call.
     * @param gt the {@link Genotype}
     * @param qual the quality
     * @param depth the sequencing depth
     */
    public GenotypeCall(Genotype gt, Integer qual, Integer depth) {
	this.callList = new ArrayList<Genotype>();
	this.callList.add(gt);
	this.qualityList = new ArrayList<Integer>();
	this.qualityList.add(qual);
	this.depthList = new ArrayList<Integer>();
	this.depthList.add(depth);
    }


    /**
     * @return A list of genotype calls, e.g., "0/0","0/1","1/1"
     */
    public ArrayList<String> getGenotypeList() {
	ArrayList<String> lst = new ArrayList<String>();
	Iterator<Genotype> it = callList.iterator();
	while (it.hasNext()) {
	    Genotype call = it.next();
	    switch (call) {
	    case HOMOZYGOUS_REF: lst.add("0/0"); break;
	    case HOMOZYGOUS_ALT: lst.add("1/1"); break;
	    case HETEROZYGOUS: lst.add("0/1"); break;
	    case NOT_OBSERVED: lst.add("./."); break;  
	    case ERROR: lst.add("?"); break;  
	    case UNINITIALIZED: lst.add("-");
	    }
	}
	return lst;
    }
   
    /**
     * @return A string with all genotype calls separated by ":", e.g., "0/0:0/1:1/1"
     */
    public String get_genotype_as_string() {
	StringBuffer sb = new StringBuffer();
	Iterator<Genotype> it = callList.iterator();
	int c=0;
	while (it.hasNext()) {
	    Genotype call = it.next();
	    if (c++>0) sb.append(":");
	    switch (call) {
	    case HOMOZYGOUS_REF: sb.append("0/0"); break;
	    case HOMOZYGOUS_ALT: sb.append("1/1"); break;
	    case HETEROZYGOUS: sb.append("0/1"); break;
	    case NOT_OBSERVED: sb.append("./."); break;  
	    case ERROR: sb.append("?"); break;  
	    case UNINITIALIZED: sb.append("-");
	    }
	    
	}
	return sb.toString();
    }
    
    /**
     * Note that this function expects the parameter n to be zero-based.
     * If the n is invalid, return null.
     * This method is intended mainly for debugging. May need to add exceptions.
     * @param n entry number
     * @return the Genotype of the Nth individual represented in the VCF file. (0-based)
     */
    public Genotype getGenotypeInIndividualN(int n){
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	return this.callList.get(n);
    }

    /**
     * @param n The number of the sample in the VCF file.
     * @return the read depth (DP) for the current variant as found in individual N
     */
    public int getReadDepthInIndividualN(int n){
	if (this.depthList==null) return 0;
	if (n<0 || n>=this.depthList.size() )
	    throw new IllegalArgumentException();
	return this.depthList.get(n);
    }
    
    /**
     * @param n The number of the sample in the VCF file.
     * @return the PHRED quality for the current variant as found in individual N
     */
    public int getQualityInIndividualN(int n){
	if (this.qualityList==null) return 0;
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	return this.qualityList.get(n);
    }

    /**
     * @param n Number of individual in pedigree.
     * @return True, if person N in the VCF file is hom-alt for this variant.
     */
    public boolean isHomozygousAltInIndividualN(int n) {
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	Genotype gt = this.callList.get(n);
	return gt == Genotype.HOMOZYGOUS_ALT;
    }

    /**
     * @param n Number of individual in pedigree.
     * @return True, if person N in the VCF file is het for this variant.
     */
    public boolean isHeterozygousInIndividualN(int n) {
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	Genotype gt = this.callList.get(n);
	return gt == Genotype.HETEROZYGOUS;
    }

    /**
     * This function returns true if the individual concerned has
     * a heterozygous or homozyous ALT (variant) call for the
     * current position.
     * @param n Number of individual in pedigree.
     * @return True, if person N in the VCF file is het or hom-alt for this variant.
     */
    public boolean isALTInIndividualN(int n) {
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	Genotype gt = this.callList.get(n);
	if (gt == Genotype.HETEROZYGOUS) return true;
	else if (gt == Genotype.HOMOZYGOUS_ALT) return true;
	else return false;
    }


    /**
     * @param n Number of the individual in the VCF file 
     * @return true if the genotype is called as "./." in individual n
     */
    public boolean isMissingInIndividualN(int n) {
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	Genotype gt = this.callList.get(n);
	return gt == Genotype.NOT_OBSERVED;
    }

   

    /**
     * This method gets the number of individuals included in the
     * genotype call. This must be equal to the number of samples
     * in the VCF file.
     * @return number of individuals
     */
    public int getNumberOfIndividuals() {return this.callList.size(); }

}