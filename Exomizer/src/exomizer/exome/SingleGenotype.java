package exomizer.exome;

import exomizer.common.Constants;

/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with only
 * one sample - thus, SingleGenotype as opposed to 
 * {@link exomizer.exome.MultipleGenotype MultipleGenotype} for
 * VCF files with multiple samples.
 * <P>
 * TODO: Note that in some files, we may have DP or DP4 in the INFO field,
 * but actually this should be in the FORMAT/Genotype fields.
 * @author Peter Robinson
 * @version 0.03 (11 February, 2013)
 */
public class SingleGenotype extends GenotypeI implements Constants {

  
    /**  genotype (See {@link exomizer.common.Constants.GenotypeCall GenotypeCall}
     * for the enumeration used to represent the genotypes).
     */
    private GenotypeCall genotype= GenotypeCall.UNINITIALIZED;

    private int genotype_quality;
    
    public SingleGenotype(GenotypeCall call, int quality) {
	this.genotype = call;
	this.genotype_quality=quality;
    }



    public boolean is_homozygous_alt() {return this.genotype == GenotypeCall.HOMOZYGOUS_ALT; }
    public boolean is_homozygous_ref() {return this.genotype == GenotypeCall.HOMOZYGOUS_REF; }
    public boolean is_heterozygous() { return this.genotype == GenotypeCall.HETEROZYGOUS; }
    public boolean is_unknown_genotype() { return this.genotype == GenotypeCall.UNKNOWN; }
    public boolean genotype_not_initialized() { return this.genotype == GenotypeCall.UNINITIALIZED; }




      /**
     * Get the following fields from the VCF INFO fields
     ##INFO=&lt;ID=DP,Number=1,Type=Integer,Description="Raw read depth"&gt;</BR>
     ##INFO=&lt;ID=DP4,Number=4,Type=Integer,Description="# high-quality ref-forward bases, ref-reverse, alt-forward and alt-reverse bases"&gt;
     <P>
     e.g., ;DP=8;VDB=0.0353;AF1=0.1837;AC1=2;DP4=3,2,1,0;
     This may be adapted in the future for VCF files with DP4
    private void extractReadDepth(String info) throws VCFParseException  {
	String A[] = info.split(";");
	for (String item : A) {
	    if (item.startsWith("DP=")){
		item = item.substring(3);
		try {
		    this.depth = Integer.parseInt(item);
		} catch (NumberFormatException e) {
		    String err = String.format("Could not parse DP field (INFO:%s): %s",info,e.toString());
		    throw new VCFParseException(err);
		}
	    } else if (item.startsWith("DP4=")) {
		item = item.substring(4);
		String B[] = item.split(",");
		try {
		    this.DP4[N_REF_FORWARD_BASES] = Integer.parseInt(B[N_REF_FORWARD_BASES]); // 0
		    this.DP4[N_REF_REVERSE_BASES] = Integer.parseInt(B[N_REF_REVERSE_BASES]); // 1
		    this.DP4[N_ALT_FORWARD_BASES] = Integer.parseInt(B[N_ALT_FORWARD_BASES]); // 2
		    this.DP4[N_ALT_REVERSE_BASES] = Integer.parseInt(B[N_REF_REVERSE_BASES]); // 3
		} catch(NumberFormatException e) {
		    String err = String.format("Could not parse DP4 field (INFO:%s): %s",info,e.toString());
		    throw new VCFParseException(err);
		}
	    }
	}
    }
 */
}