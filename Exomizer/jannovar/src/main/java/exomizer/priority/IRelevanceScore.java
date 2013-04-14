package exomizer.priority;



/**
 * Prioritization of Genes results in a relevance score for each tested
 * {@link exomizer.exome.Gene Gene} object. The methods may also annotate
 * the genes with data (e.g., a link to OMIM or a link to Phenodigm or
 * uberpheno data. Each prioritization is expected to result on an object
 * of a class that implements IRelevanceScore
 * @author Peter N Robinson
 * @version 0.02 (January 11,2012)
 * @see exomizer.filter.ITriage
 */
public interface IRelevanceScore {

    /** @return a numerical value representing the relevance of the gene. Should be between
     * zero (no relevance) and an arbitrary real number (not necessarily 1.0f).
     */
    public float getRelevanceScore();
    /**
     * Some of the prioritizers need to renormalize the score after they have gotten
     * a score for all genes, and can use this method to do so,
     * @param newscore The renormalized score 
     */
    public void resetRelevanceScore(float newscore);


    /**
     * @return HTML code representing this prioritization/relevance score 
     */
    public String getHTMLCode();

}