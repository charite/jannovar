package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class provides static methods to generate annotations for non-coding
 * RNA variants. 
 * @version 0.03 (7 July, 2013)
 * @author Peter N Robinson
 */

public class NoncodingAnnotation {


    
 /**
     * Return an annotation for a UTR3  mutation for a gene.
     * @param trmdl Gene with splice mutation for current chromosomal variant.
     * @param rvarstart start position of the variant
     * @param ref reference sequence
     * @param alt variant sequence
     * @param exonNumber number of the exon affected by the variant (zero-based).
     * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to a 3' UTR variant.
     */
    public static Annotation createNoncodingExonicAnnotation(TranscriptModel trmdl, int rvarstart, String ref, String alt,int exonNumber) {
	String annotation = null;
	exonNumber++; /* correct from zero-based to one-based numbering */
	if (alt.equals("-")) {
	   /* i.e., deletion  */
	    if (ref.length() == 1) {
		annotation = String.format("%s:exon%d:n.%ddel%s",trmdl.getName(),exonNumber,rvarstart,ref);
	    } else {
		int d2 = rvarstart + ref.length() - 1;
		annotation = String.format("%s:exon%d:n.%d_%ddel%s",trmdl.getName(),exonNumber,rvarstart,d2,ref);
		    } 
	} else if (ref.equals("-")) {
	    /* i.e., insertion */
	    int d2=rvarstart + 1; /* get end of insertion */ 
	    annotation = String.format("%s:exon%d:n.%d_%dins%s",trmdl.getName(),exonNumber,rvarstart,d2,alt);
	   
	} else {
	    /* i.e., substitution. The following code will be correct for SNPs, and may need to 
	       be improved in the future for block substitutions. */
	    annotation = String.format("%s:exon%d:n.%d%s>%s",trmdl.getName(),exonNumber,rvarstart,ref,alt);
	   
	}
	Annotation ann = new Annotation(trmdl, annotation, VariantType.ncRNA_EXONIC,rvarstart);
	ann.setGeneID( trmdl.getGeneID() );
	ann.setGeneSymbol( trmdl.getGeneSymbol() );
	return ann;
    }



}