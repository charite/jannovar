package jannovar.genotype;

import jannovar.common.Genotype;

/**
 * THis is the superclass of both SingleGenotype and MultipleGenotype. It
 * has methods that are designed to be used to allow client code to check if
 * a particular variant is compatible with a certain type of inheritance.
 * <P>
 * Note that for now we will simplify the situation with X chromosomal variants
 * by filtering for them simply on the basis of chromosome (i.e., not on the
 * basis of X chromosomal recessive vs dominant, which is difficult in exome
 * data). Thus, we are assuming that the genotype is on an autosome or behaves
 * as such.
 * <p>
 * Need to implement new features.
 * @author Peter Robinson
 * @version 0.03 (10 May, 2013)
 */
public abstract class GenotypeI {


    public abstract boolean is_homozygous_alt();
    public abstract boolean is_homozygous_ref();
    public abstract boolean is_heterozygous();
    public abstract boolean is_error();
    public abstract boolean genotype_not_initialized();

    public abstract String get_genotype_as_string();
    /**
     * @return a constant from {@link jpedfilter.common.Genotype Genotype}
     * that represents the genotype of the individual in sample N
     * @param n the number of the individual in the VCF file's list of
     * samples (note: 0-based!)
     */
    public abstract Genotype getGenotypeInIndividualN(int n);
    
    public abstract int getNumberOfIndividuals();
  
}