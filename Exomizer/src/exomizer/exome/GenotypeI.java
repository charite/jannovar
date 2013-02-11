package exomizer.exome;



public abstract class GenotypeI {


    public abstract boolean is_homozygous_alt();
    public abstract boolean is_homozygous_ref();
    public abstract boolean is_heterozygous();
    public abstract boolean is_unknown_genotype();
    public abstract boolean genotype_not_initialized();

    public abstract String get_genotype_as_string();
  
}