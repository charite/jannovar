package jannovar.genotype;

import java.util.ArrayList;
import java.util.Iterator;


import jannovar.common.Genotype;


/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with multiple
 * samples - thus, MultipleGenotype as opposed to 
 * {@link jannovar.genotype.SingleGenotype SingleGenotype} for
 * VCF files with single samples.
 * @author Peter Robinson
 * @version 0.05 (5 May, 2013)
 */
public class MultipleGenotype extends GenotypeI {

  
    /**  List of genotype calls (See {@link jannovar.common.Genotype Genotype})
     * for one variant.
     */
    private ArrayList<Genotype> callList = null;
    /**
     * List of Phred-scaled qualities for the genotype calls in {@link #callList}.
     */
    private ArrayList<Integer> qualityList = null;
    
    /**
     * The constructor takes lists of calls and qualities that have been parsed from 
     * a single VCF line by the {@link jannovar.genotype.MultipleGenotypeFactory MultipleGenotypeFactory}.
     * @param calls A list of the genotype calls, one for each sample
     * @param qualities A list of the genotype Phred qualities, one for each sample.
     */
    public MultipleGenotype(ArrayList<Genotype> calls,ArrayList<Integer> qualities) {
	this.callList = calls;
	this.qualityList = qualities;

	//System.out.println("Warning: MultipleGenotype not fully implemented");

    }


    public boolean is_homozygous_alt() { return false; }
    public boolean is_homozygous_ref() {return false; }
    public boolean is_heterozygous() { return false; }
    public boolean is_error() { return false; }
    public boolean genotype_not_initialized() { return false; }

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
     * Note that this function expects the parameter n to be one-based.
     * If the n is invalid, return null.
     * This method is intended mainly for debugging. May need to add exceptions.
     * @return the Genotype of the Nth individual represented in the VCF file. (0-based)
     */
    public Genotype getGenotypeInIndividualN(int n){
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	return this.callList.get(n);
    }

      @Override public int getNumberOfIndividuals() {return this.callList.size(); }

}