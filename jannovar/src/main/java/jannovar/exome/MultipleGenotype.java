package jannovar.exome;

import java.util.ArrayList;
import java.util.Iterator;

import jannovar.common.Constants;

/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with multiple
 * samples - thus, MultipleGenotype as opposed to 
 * {@link jannovar.exome.SingleGenotype SingleGenotype} for
 * VCF files with single samples.
 * @author Peter Robinson
 * @version 0.03 (13 February, 2013)
 */
public class MultipleGenotype extends GenotypeI implements Constants {

  
    /**  List of genotype calls (See {@link jannovar.common.Constants Constants})
     * for one variant.
     */
    private ArrayList<GenotypeCall> callList = null;
    /**
     * List of Phred-scaled qualities for the genotype calls in {@link #callList}.
     */
    private ArrayList<Integer> qualityList = null;
    
    /**
     * The constructor takes lists of calls and qualities that have been parsed from 
     * a single VCF line by the {@link jannovar.io.MultipleGenotypeFactory MultipleGenotypeFactory}.
     * @param calls A list of the genotype calls, one for each sample
     * @param qualities A list of the genotype Phred qualities, one for each sample.
     */
    public MultipleGenotype(ArrayList<GenotypeCall> calls,ArrayList<Integer> qualities) {
	this.callList = calls;
	this.qualityList = qualities;

	//System.out.println("Warning: MultipleGenotype not fully implemented");

    }


    public boolean is_homozygous_alt() { return false; }
    public boolean is_homozygous_ref() {return false; }
    public boolean is_heterozygous() { return false; }
    public boolean is_unknown_genotype() { return false; }
    public boolean genotype_not_initialized() { return false; }

    public String get_genotype_as_string() {
	StringBuffer sb = new StringBuffer();
	Iterator<GenotypeCall> it = callList.iterator();
	int c=0;
	while (it.hasNext()) {
	    GenotypeCall call = it.next();
	    if (c++>0) sb.append(":");
	    switch (call) {
	    case HOMOZYGOUS_REF: sb.append("0/0"); break;
	    case HOMOZYGOUS_ALT: sb.append("1/1"); break;
	    case HETEROZYGOUS: sb.append("0/1"); break;
	    case NOT_OBSERVED: sb.append("./."); break;  
	    case UNKNOWN: sb.append("?"); break;  
	    case UNINITIALIZED: sb.append("-");
	    }
	    
	}
	return sb.toString();
    }

}