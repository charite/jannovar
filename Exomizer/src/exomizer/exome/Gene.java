package exomizer.exome;

import java.util.ArrayList;
import java.util.HashMap;

import exomizer.exome.Variant;
import exomizer.common.Constants;
import exomizer.filter.IRelevanceScore;

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
 * dominant patterns in the data is done with this class. This kind of
 * prioritization is done by classes that implement 
 * {@link exomizer.filter.IPriority IPriority}.
 * @author Peter Robinson
 * @version 0.03 (10 January, 2013)
 */
public class Gene implements Comparable<Gene>, Constants  {
    /** A list of all of the variants that affect this gene. */
    private ArrayList<Variant> variant_list=null;

    /** A priority score between 0 (irrelevant) and an arbitrary
     * number (highest prediction for a disease gene) reflecting the predicted relevance
     * of this gene for the disease under study by exome sequencing.
     */
    private float priorityScore = UNINITIALIZED_FLOAT;

    /**
     * A score representing the combined pathogenicity predictions for the
     * {@link exomizer.exome.Variant Variant} objects associated with this gene.
     */
    private float filterScore = UNINITIALIZED_FLOAT;

   
     /** A map of the results of prioritization. The key to the map is an 
	integer constant as defined in {@link exomizer.common.Constants Constants}. */
    private HashMap<Integer,IRelevanceScore> relevanceMap=null;
       
    

    /**
     * Construct the gene by adding the first variant that affects the gene. If the current gene
     * has additional variants, they will be added using the function add_variant.
     * @param var A variant located in this gene.
     */
    public Gene(Variant var) {
	variant_list = new ArrayList<Variant>();
	variant_list.add(var);
	this.relevanceMap = new HashMap<Integer,IRelevanceScore>();
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
     * @param rel Result of a prioritization algorithm
     */
    public void addRelevanceScore(IRelevanceScore rel, int type) {
	this.relevanceMap.put(type,rel);
    }


    /**
     * Note that currently, the EntrezGene IDs are associated with the Variants. Probably it would
     * be more natural to associate that with a field of this Gene object. For now, leave it as be,
     * and return an UNINITIALIZED_INT flag if this gene has not variants.
     * @return the NCBI Entrez Gene ID associated with this gene (extracted from one of the Variant objects)
     */
    public int getEntrezGeneID() {
	if (this.variant_list.isEmpty())
	    return UNINITIALIZED_INT;
	else {
	    Variant v = this.variant_list.get(0);
	    return v.getEntrezGeneID();
	}
    }


    /**
     * Calculate the total priority score for this
     * gene based on data stored in its associated
     * {@link exomizer.exome.Variant Variant} objects.
     */
    public void calculateFilteringScore() {
	if (variant_list.size()==0)
	    this.filterScore = 0f;
	else {
	    this.filterScore = 1f;
	    for (Variant v : this.variant_list) {
		float x = v.getPriorityScore();
		this.filterScore *= x;
	    }
	}
    }

    /**
     * Calculate the combined priority score for this gene (the result
     * is stored in the class variable 
     * {@link exomizer.exome.Gene#priorityScore}, which is used to help sort
     * the gene.
     */
     public void calculatePriorityScore() {
	 this.priorityScore  = 1f;
	 for (Integer i : this.relevanceMap.keySet()) {
	    IRelevanceScore r = this.relevanceMap.get(i);
	    float x = r.getRelevanceScore();
	    priorityScore *= x;
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

    /**
     * Calculate the combined score of this gene based on the relevance of the
     * gene (priorityScore) and the predicted effects of the variants
     * (filterScore).
     * @return a combined score that will be used to rank the gene.
     */
    private float getCombinedScore() {
	if (priorityScore == UNINITIALIZED_FLOAT)
	    calculatePriorityScore();
	if ( filterScore == UNINITIALIZED_FLOAT)
	    calculateFilteringScore();
	return priorityScore * filterScore;
    }


    /**
     * Sort this gene based on priority and filter score.
     */
    public int compareTo(Gene other) {
	float me = getCombinedScore();
	float you = other.getCombinedScore();
        if( me < you )
            return -1;
        if( me > you )
            return 1;
        return 0;
    }

    
}