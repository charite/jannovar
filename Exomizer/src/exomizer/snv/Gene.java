package nsfp.snv;

import java.util.ArrayList;

import nsfp.NSFP;
/**
 * This class is meant to allow filtering for autosomal recessive or dominant patterns in the data.
 */
public class Gene {

    private ArrayList<NSFP> nsfp_list=null;

    public Gene(NSFP nsfp) {
	nsfp_list = new ArrayList<NSFP>();
	nsfp_list.add(nsfp);
    }

    public void add_nsfp(NSFP nsfp) {
	nsfp_list.add(nsfp);
    }

    public ArrayList<NSFP> get_NFSP_list() { return nsfp_list; }

    /** Return true if the variants for this gene are consistent with AR
	inheritance. */
    public boolean is_consistent_with_recessive() {
	if (nsfp_list.size()>1) return true; /* compound heterozygous */
	for (NSFP n : nsfp_list) {
	    if (n.is_homozygous_alt()) return true; 
	}
	return false;
    }

    public boolean is_consistent_with_dominant() {
	for (NSFP n : nsfp_list) {
	    if (n.is_heterozygous()) return true; 
	}
	return false;
    }

    public boolean is_consistent_with_X() {
	if (nsfp_list.size()==0) return false;
	NSFP nsfp = nsfp_list.get(0);
	return nsfp.is_X_chromosomal();

    }

    
}