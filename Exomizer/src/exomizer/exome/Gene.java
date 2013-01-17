package exomizer.exome;

import java.util.ArrayList;
import java.util.HashMap;

import exomizer.exome.Variant;
import exomizer.common.Constants;
import exomizer.priority.IRelevanceScore;

/**
 * This class represents a Gene in which {@link exomizer.exome.Variant Variant} objects
 * have been identified by exome sequencing. Note that this class stores
 * information about observed variants and quality scores etc. In contrast,
 * the class {@link exomizer.reference.KnownGene KnownGene} stores information
 * from UCSC about all genes, irrespective of whether we see a variant in the gene
 * by exome sequencing. Therefore, the program uses information from 
 * {@link exomizer.reference.KnownGene KnownGene} object to annotate variants found
 * by exome sequencing, and stores the results of that annotation in
 * {@link exomizer.exome.Variant Variant} objects. Objects of this class have a
 * list of Variant objects, one for each variant observed in the exome. Additionally,
 * the Gene objects get prioritized for their biomedical relevance to the disease
 * in question, and each such prioritization results in an 
 * {@link exomizer.priority.IRelevanceScore IRelevanceScore} object.
 * <P>
 * There are additionally some prioritization procedures that only can be
 * performed on genes (and not on the individual variants). For instance, there
 * are certain genes such as the Mucins or the Olfactory receptor genes that are
 * often found to have variants in WES data but are known not to be the
 * relevant disease genes. Additionally, filtering for autosomal recessive or 
 * dominant patterns in the data is done with this class. This kind of
 * prioritization is done by classes that implement 
 * {@link exomizer.priority.IPriority IPriority}.
 * @author Peter Robinson
 * @version 0.08 (17 January, 2013)
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
     * @return the number of {@link exomizer.exome.Variant Variant} objects for this gene.
     */
    public int getNumberOfVariants() {
	return this.variant_list.size();
    }
    
    /**
     * @return the nth {@link exomizer.exome.Variant Variant} object for this gene.
     */
    public Variant getNthVariant(int n) {
	if (n>= this.variant_list.size())
	    return null;
	else
	    return this.variant_list.get(n);
    }
       
    

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
     * and return an UNINITIALIZED_INT flag if this gene has no {@link exomizer.exome.Variant Variant} objects.
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
     * @return the map of {@link exomizer.priority.IRelevanceScore  IRelevanceScore} 
     * objects that represent the result of filtering 
     */
    public HashMap<Integer,IRelevanceScore> getRelevanceMap() { return this.relevanceMap; }
    
    /**
     * Note that currently, the gene symbols are associated with the Variants. Probably it would
     * be more natural to associate that with a field of this Gene object. For now, leave it as be,
     * and return "-" if this gene has no  {@link exomizer.exome.Variant Variant} objects.
     * @return the symbol associated with this gene (extracted from one of the Variant objects)
     */
    public String getGeneSymbol() {
	if (this.variant_list.isEmpty())
	    return "-";
	else {
	    Variant v = this.variant_list.get(0);
	    return v.getGeneSymbol();
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
		float x = v.getFilterScore();
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
    public float getCombinedScore() {
	if (priorityScore == UNINITIALIZED_FLOAT)
	    calculatePriorityScore();
	if ( filterScore == UNINITIALIZED_FLOAT)
	    calculateFilteringScore();
	//return priorityScore * filterScore;
	return priorityScore;
    }

    /**
     * Calculate the gene (priority) and the variant 
     * (filtering) scores in preparation for sorting.
     */
    public void calculateGeneAndVariantScores() {
	calculatePriorityScore();
	calculateFilteringScore();
    }


    /**
     * Sort this gene based on priority and filter score.
     */
    public int compareTo(Gene other) {
	float me = getCombinedScore();
	float you = other.getCombinedScore();
        if( me < you )
            return 1;
        if( me > you )
            return -1;
        return 0;
    }

    
}