package exomizer.reference;


import java.util.TreeMap;

import exomizer.io.KGLine;


/**
 * This class encapsulates a chromosome and all of the genes its contains.
 * It is intended to be used together with the KGLine input class to make 
 * a list of gene models that will be used to annotate chromosomal variants.
 * The genes are stored in a TreeMap (A Java implementation of the red-black
 * tree), allowing them to be found quickly on the basis of the position of 
 * the chromosomal variant. Also, we can find the neighbors (5' and 3') of 
 * the closest gene in order to find the right and left genes of intergenic
 * variants and to find the correct gene in the cases of complex regions of the
 * chromosome with one gene located in the intron of the next or with overlapping
 * genes. 
 * @author Peter N Robinson
 * @version 0.01 (Sept. 19, 2012)
 */
public class Chromosome {
    /** Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. 
     TODO. Add more flexible way of dealing with scaffolds etc.*/
    private byte chromosome;
    /** Alternative String for Chromosome. USe for scaffolds and "random" chromosomes. TODO: Refactor */
    private String chromosomeString=null;
    /** TreeMap with all of the genes ({@link exomizer.io.KGLine KGLine} objects) of this chromosome. */
    private TreeMap<Integer,KGLine> geneTreeMap=null;
    

    public Chromosome(byte c) {
	this.chromosome = c;
	this.geneTreeMap = new TreeMap<Integer,KGLine>();
    }

    /**
     * Add a gene model to this chromosome. 
     */
    public void addGene(KGLine kgl) {
	int pos = kgl.getTXStart();
	this.geneTreeMap.put(pos,kgl);
    }

    /**
     * @return Number of genes contained in this chromosome.
     */
    public int getNumberOfGenes() { return this.geneTreeMap.size(); }

}