package exomizer.exome;

import exomizer.common.Constants;

/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with multiple
 * samples - thus, MultipleGenotype as opposed to 
 * {@link exomizer.exome.SingleGenotype SingleGenotype} for
 * VCF files with single samples.
 * @author Peter Robinson
 * @version 0.02 (10 February, 2013)
 */
public class MultipleGenotype extends GenotypeI implements Constants {

  
    /**  genotype (See {@link exomizer.common.Constants Constants}
     * for the integer constants used to represent the genotypes).
     */
    private GenotypeCall call =GenotypeCall.UNINITIALIZED;

    private int genotype_quality;
    
    public MultipleGenotype(byte gt,int quality) {
	System.out.println("Warning: MultipleGenotype not implemented");
	this.genotype_quality=quality;
    }


    public boolean is_homozygous_alt() { return false; }
    public boolean is_homozygous_ref() {return false; }
    public boolean is_heterozygous() { return false; }
    public boolean is_unknown_genotype() { return false; }
    public boolean genotype_not_initialized() { return false; }

    public String get_genotype_as_string() {
	return "todo";
    }

}