package jannovar.annotation;


import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class is intended to provide a static method to generate annotations for INTRONIC variants.
 * The main work is in finding the distance to the nearest 5' and 3' 
 * @version 0.05 (31 December)
 * @author Peter N Robinson
 */

public class IntronicAnnotation {


    /**
     * Create an intronic annotation with a description of the length to the
     * neighboring exon boundaries. When we get here, the start of exon k is 3' 
     * to the variant on the chromosome and the end of exon k-1 is 5' to the
     * variant.
     * We indicate the distance to the exon boundary exclusive of the exon but
     * inclusive of the first position of the variant. That is, for
     * a variant at the indicated position CGGgttag[t/c]g, the distance to the
     * final (upper-case) G of the previous exon is 6. This makes a standard
     * intronic variant  CGG[g/a]ttagtg have the expected numbering (+1, in this case).
     * @param trmdl The affected transcript
     * @param k Number of exone (zero based numbering) that is 3' to the variant on chromosome
     * @param start begin position of variant (one-based numbering)
     * @param end end position of variant
     */
    public static Annotation createIntronicAnnotation(TranscriptModel trmdl, int k, int start, int end){
	String accession = trmdl.getAccessionNumber();

	//System.out.println(trmdl.getGeneSymbol() + ":" +accession + ", starnd=" + trmdl.getStrand() + " exon ct = " +trmdl.getExonCount() );
	//System.out.println("K="+k + ", start="+start+", end="+end+" exonend="+exonend+", exonsstart="+exonstart);
	int exonend;  /* also one-based numbering */
	int exonstart; 
	int distL;
	int distR;
	String annot = null;;
	if (trmdl.isPlusStrand() ) {
	    exonend = trmdl.getExonEnd(k-1);  /* also one-based numbering */
	    exonstart = trmdl.getExonStart(k); 
	    distL = start - exonend;
	    distR = exonstart - end;
	    annot = String.format("%s:dist to exon%d=%d;dist to exon%d=%d",accession,k,distL,k+1,distR);
	} else {
	    exonend = trmdl.getExonEnd(k);  /* also one-based numbering */
	    exonstart = trmdl.getExonStart(k+1); 
	    distL = start - exonend;
	    distR = exonstart - end;
	    int rightexon = trmdl.getExonCount() - k;
	    int leftexon = rightexon-1;
	    annot = String.format("%s:dist to exon%d=%d;dist to exon%d=%d",accession,leftexon,distR,rightexon,distL);
	}
	int m = Math.min(distR,distL);
	Annotation ann = new Annotation(trmdl,annot, VariantType.INTRONIC);
	ann.setDistanceToNearestExon(m);
	return ann;
    }


    /**
     * Create an intronic annotation with a description of the length to the
     * neighboring exon boundaries.
     * @param trmdl The affected transcript
     * @param k exon number (zero based numbering) that is 3' to the variant on chromosome
     * @param start begin position of variant
     * @param end end position of variant
     */
    public static Annotation createNcRNAIntronicAnnotation(TranscriptModel trmdl, int k, int start, int end){
	Annotation ann = IntronicAnnotation.createIntronicAnnotation(trmdl,k, start,end);
	ann.setVarType(VariantType.ncRNA_INTRONIC);
	return ann;
    }

   
}
