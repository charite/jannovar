package exomizer.exome;

import java.util.ArrayList;


import exomizer.exome.Variant;

/**
 * This class is meant to allow filtering for autosomal recessive or dominant patterns in the data.
 */
public class Gene {
    /** A list of all of the variants that affect this gene. */
    private ArrayList<Variant> variant_list=null;

    /**
     * Construct the gene by adding the first variant that affects the gene. If the current gene
     * has additional variants, they will be added using the function add_variant.
     * @param var A variant located in this gene.
     */
    public Gene(Variant var) {
	variant_list = new ArrayList<Variant>();
	variant_list.add(var);
    }

    /**
     * This function adds additional variants to the current gene. The variants have been identified by parsing
     * the VCF file.
     * @param var A Variant affecting the current gene.
     */
    public void addVariant(Variant var) {
	this.variant_list.add(var);
    }

    /**
     * @return A list of all variants in the VCF file that affect this gene.
     */
    public ArrayList<Variant> get_variant_list() { return variant_list; }

    /** @return true if the variants for this gene are consistent with autosomal recessive
	inheritance, otherwise false. */
    public boolean is_consistent_with_recessive() {
	if (variant_list.size()>1) return true; /* compound heterozygous */
	for (Variant v : variant_list) {
	    if (v.is_homozygous_alt()) return true; 
	}
	return false;
    }
    /** @return true if the variants for this gene are consistent with autosomal dominant
	inheritance, otherwise false. */
    public boolean is_consistent_with_dominant() {
	for (Variant v : variant_list) {
	    if (v.is_heterozygous()) return true; 
	}
	return false;
    }
    /** @return true if the variants for this gene are consistent with X chromosomal
	inheritance, otherwise false. */
    public boolean is_consistent_with_X() {
	if (variant_list.size()==0) return false;
	Variant v = variant_list.get(0); 
	return v.is_X_chromosomal();// is true if the gene is X chromosomal.
    }

    
}