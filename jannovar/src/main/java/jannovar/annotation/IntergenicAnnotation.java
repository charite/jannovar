package jannovar.annotation;


import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;
import jannovar.exception.AnnotationException;

/**
 * This class is intended to provide a static method to generate annotations for INTERGENIC,
 * DOWNSTREAM and UPSTREAM variants.
 * @version 0.02 (5 July, 2013)
 * @author Peter N Robinson
 */

public class IntergenicAnnotation {

    /**
     * This factory method creates an annotation object for an intergenic variant that is located
     * between two genes and is not nearby (threshold default of 1000 nt) to either of them. For
     * example the annotation should look like
     * <PRE>HGVS=LOC100288069(dist=39337),LINC00115(dist=8181)</PRE>
     * @param leftGene Gene that is 5' to the variant
     * @param rightGene Gene that is 3' to the variant
     * @param startpos 5' position of the variant (should be the start position)
     * @param endpos 3' position of the variant (should be the end position)
     * @return Annotation object for internenic variant
     */
    public static Annotation createIntergenicAnnotation(TranscriptModel leftGene, TranscriptModel rightGene, int startpos, int endpos) {
	//Annotation ann = new Annotation();
	//System.out.println(String.format("Left:%s, right:%s, start %d end %d",leftGene.getName2(),rightGene.getName2(),startpos,endpos));
	String annot = null;
	/* Note that either the leftGene or the rightGene can be null, if the variant is located
	   5' (3') to all variants on a chromosome. */
	if (leftGene == null) {
	    int distR =  rightGene.getTXStart() - endpos;
	    annot = String.format("NONE(dist=NONE),%s(dist=%d)",
						  rightGene.getGeneSymbol(),distR);
	} else if (rightGene == null) {
	    int distL =  startpos - leftGene.getTXEnd();
	    annot = String.format("%s(dist=%d),NONE(dist=NONE)",
						  leftGene.getGeneSymbol(),distL);

	} else {
	    int distR =  rightGene.getTXStart() - endpos;
	    int distL =  startpos - leftGene.getTXEnd();
	    annot = String.format("%s(dist=%d),%s(dist=%d)",
				  leftGene.getGeneSymbol(),distL,rightGene.getGeneSymbol(),distR);
	}
	return Annotation.createIntergenicAnnotation(annot,VariantType.INTERGENIC);
    }


     /**
     * Create an Annotation obejct for a variant that is upstream or downstream
     * to a gene (default: within 1000 nt).
     * @param trmdl The transcript that the variant is up/downstream to
     * @param pos The chromosomal position of the variant 
     */
    public static Annotation createUpDownstreamAnnotation(TranscriptModel trmdl, int pos) {
	String annot = String.format("%s", trmdl.getGeneSymbol());
	VariantType type=null;
	if (trmdl == null) {
	    System.out.println("createUpDownstreamAnnotation, TranscriptModel argument is null, pos=" + pos);
	    System.exit(1);
	}

	
	if (trmdl.isFivePrimeToGene(pos)) {
	    if (trmdl.isPlusStrand()) {
		type=VariantType.UPSTREAM;
	    } else {
		type=VariantType.DOWNSTREAM;
	    }
	} else if (trmdl.isThreePrimeToGene(pos)) {
	    if (trmdl.isMinusStrand()) {
		type=VariantType.UPSTREAM;
	    } else {
		type=VariantType.DOWNSTREAM;
	    }
	}
	return new Annotation(trmdl,annot,type);
    }


}