package jannovar.annotation;


import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;


/**
 * This class provides static methods to generate annotations for UTR5 and UTR3
 * variants (5' untranslated region). 
 * <P> 
 * The Human Genome Variation Society nomenclature for <b>5' UTR variants</b> works as follows.
 * <P>
 * A G to A substitution 12 nts upstream of the ATG translation initiation codon
 * is refered to as <b>c.-12G>A</b>. To get this far, we first calculate rvarstart in the
 * class {@link jannovar.reference.Chromosome Chromosome} as  
 * {@code rvarstart = kgl.getExonStart(k) - kgl.getTXStart() -  cumlenintron + 1;}. Assuming for
 * instance that the 5' UTR has two exons, and that the start codon as well as the 
 * <P> 
 * The Human Genome Variation Society nomenclature for <b>3' UTR variants</b> works as follows.
 * <P>
 * ....
 * @version 0.07 (27 June, 2013)
 * @author Peter N Robinson
 */

public class UTRAnnotation {
    /**
     * Return an annotation for a UTR3  mutation for a gene. TODO figure out +/- strand strategy
     * <P> See the description of this class for comments on the TODO. For now, KISS
     * @param trmdl Gene with splice mutation for current chromosomal variant.
     * @param rvarstart start position of the variant
     * @param ref reference sequence
     * @param alt variant sequence
     * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to a 3' UTR variant.
     */
    public static Annotation createUTR3Annotation(TranscriptModel trmdl, int rvarstart, String ref, String alt) {
	String genesymbol = trmdl.getGeneSymbol();
	int rcdsend = trmdl.getRefCDSEnd();
	Annotation ann = Annotation.createEmptyAnnotation();
	ann.setVarType(VariantType.UTR3);
	ann.setGeneSymbol( trmdl.getGeneSymbol() );
	if (ref.length()>1) {
	    /* TODO We are not yet ready to annotate complex variants in the UTR5, in this
	    * case just use the gene name. Improve this later.*/
	    ann.setVariantAnnotation( trmdl.getName() );
	} else {
	    int distance =  rvarstart - trmdl.getRefCDSEnd();
	    String annotation = String.format("%s:c.*%d%s>%s",trmdl.getName(),distance,ref,alt);
	    ann.setVariantAnnotation(annotation);

	}
	ann.setGeneID( trmdl.getEntrezGeneID() );
	return ann;
    }


    /**
     * Return an annotation for a 5' UTR  mutation for a gene. 
     * <P> See the description of this class for comments on the TODO. For now, KISS
     * @param trmdl Gene with splice mutation for current chromosomal variant.
     * @param rvarstart start position of the variant with respect to begin of transcript.  
     * @param ref reference sequence
     * @param alt variant sequence
     * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to a 5' UTR variant.
     */
    public static Annotation createUTR5Annotation(TranscriptModel trmdl, int rvarstart, String ref, String alt) {
	String genesymbol = trmdl.getGeneSymbol();
	Annotation ann = Annotation.createEmptyAnnotation();
	ann.setVarType(VariantType.UTR5);
	ann.setGeneSymbol( trmdl.getGeneSymbol() );
	if (ref.length()>1) {
	    /* We are not yet ready to annotate complex variants in the UTR5, in this
	    * case just use the gene name. Improve this later.*/
	    ann.setVariantAnnotation( trmdl.getName() );
	} else {
	    int distance = trmdl.getRefCDSStart()  - rvarstart;
	    String annotation = String.format("%s:c.-%d%s>%s",trmdl.getName(),distance,ref,alt);
	    ann.setVariantAnnotation(annotation);
	}
	ann.setGeneID( trmdl.getEntrezGeneID() );
	return ann;
    }

}