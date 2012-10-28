package exomizer.reference;

import exomizer.common.Constants;
import exomizer.reference.KnownGene;

/**
 * This class encapsulates a single annotation and includes basically two pieces of information:
 * <OL>
 * <LI>The variant type: frameshift, synonymous substitution, etc
 * <LI>A string representing the actualy variant
 * </OL>
 * This class is meant to be used in place of the hash {@code %function} in Annovar. Typically,
 * variants are entered into that has with lines such as
 * <P>
 * {@code  $function->\{$index\}\{ssnv\} .= "$geneidmap->\{$seqid\}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";}
 * <P>
 * This has then stores the potentially multiple variants for any one chromosomal variant, and these are then
 * printed to the variant and exonic variant files.
 * @author Peter N Robinson
 * @version 0.01 (6 October 2012)
 */
public class Annotation implements Constants {
    /** The type of the variant being annotated, using the constants in {@link exomizer.common.Constants Constants},
	e.g., MISSENSE, 5UTR, etc. */
    private byte varType;

    private String variantType=null;
    private String variantAnnotation=null;

    public String getType() { return this.variantType; }


    public Annotation(String type, String anno) {
	this.variantType=type;
	this.variantAnnotation=anno;
    }

    /** This constructor is intended to be used only by static factory methods. */
    private Annotation() {
    }


    /**
     * This factory method creates an annotation object for an intergenic variant that is located
     * between two genes and is not nearby (threshold default of 1000 nt) to either of them. For
     * example the annotation should look like
     * <PRE>HGVS=LOC100288069(dist=39337),LINC00115(dist=8181)</PRE>
     * @param leftGene Gene that is 5' to the variant
     * @param rightGene Gene that is 3' to the variant
     * @param pos position of the variant (should be the start position)
     * @return Annotation object for internenic variant
     */
    public static Annotation createIntergenicAnnotation(KnownGene leftGene, KnownGene rightGene, int pos) {
	Annotation ann = new Annotation();
	ann.varType=INTERGENIC;
	/* Note that either the leftGene or the rightGene can be null, if the variant is located
	   5' (3') to all variants on a chromosome. */
	if (leftGene == null) {
	    int distR =  rightGene.getTXStart() - pos;
	    ann.variantAnnotation = String.format("HGVS=NONE(dist=NONE),%s(dist=%d)",
						  rightGene.getName2(),distR);
	} else if (rightGene == null) {
	    int distL =  pos - leftGene.getTXEnd();
	    ann.variantAnnotation = String.format("HGVS=%s(dist=%d),NONE(dist=NONE)",
						  leftGene.getName2(),distL);

	} else {
	    int distR =  rightGene.getTXStart() - pos;
	    int distL =  pos - leftGene.getTXEnd();
	    ann.variantAnnotation = String.format("HGVS=%s(dist=%d),%s(dist=%d)",
						  leftGene.getName2(),distL,rightGene.getName2(),distR);
	}
	return ann;
    }

    /**
     * @return type of this Annotation (one of the constants such as INTERGENIC from {@link exomizer.common.Constants Constants}).
     */
    public byte getVarType() {
	return this.varType;
    }


    public String getVariantTypeAsString() { 
	String s="";
	switch(this.varType) {
	case INTERGENIC: s="INTERGENIC";break;
	default: s="NOT IMPLEMENTED YET, CHECK Annotation.java";
	}
	return s;
    }
    public String getVariantAnnotation() { return this.variantAnnotation; }






}