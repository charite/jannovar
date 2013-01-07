package exomizer.exome;

import java.util.ArrayList;


import exomizer.exome.Variant;

/**
 * (Jan 8, 2013): This class will now be extended to be the object that gets 
 * prioritized in the Exomizer. It will receive methods that take the
 * scores from the {@link exomizer.exome.Variant Variant} objects it
 * contains and calculates a numerical value that can be used for sorting.
 * <P>
 * There are additionally some prioritization procedures that only can be
 * performed on genes (and not on the individual variants). For instance, there
 * are certain genes such as the Mucins or the Olfactory receptor genes that are
 * often found to have variants in WES data but are known not to be the
 * relevant disease genes. Additionally, filtering for autosomal recessive or 
 * dominant patterns in the data is done with this class.
 * @author Peter Robinson
 * @version 0.02 (7 January, 2013)
 */
public class Gene {
    /** A list of all of the variants that affect this gene. */
    private ArrayList<Variant> variant_list=null;

    /** A priority score between 0 (irrelevant) and 1 (highest prediction 
     * for a disease gene) reflecting the predicted relevance
     * of this gene for the disease under study by exome sequencing.
     */
    private float priorityScore;
       
    

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
     * Calculate the total priority score for this
     * gene based on data stored in its associated
     * {@link exomizer.exome.Variant Variant} objects.
     */
    public void calculatePriorityScore() {
	if (variant_list.size()==0)
	    this.priorityScore = 0f;
	else {
	    this.priorityScore = 1f;
	    for (Variant v : this.variant_list) {
		float x = v.getPriorityScore();
		this.priorityScore *= x;
	    }
	}
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