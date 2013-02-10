package exomizer.exome;

import exomizer.common.Constants;

/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with only
 * one sample - thus, SingleGenotype as opposed to 
 * {@link exomizer.exome.MultipleGenotype MultipleGenotype} for
 * VCF files with multiple samples.
 * @author Peter Robinson
 * @version 0.01 (10 February, 2013)
 */
public class SingleGenotype extends GenotypeI implements Constants {

  
    /**  genotype (See {@link exomizer.common.Constants Constants}
     * for the integer constants used to represent the genotypes).
     */
    private byte genotype=GENOTYPE_NOT_INITIALIZED;

    private int genotype_quality;
    
    public SingleGenotype(byte gt,int quality) {
	this.genotype = gt;
	this.genotype_quality=quality;
    }
}