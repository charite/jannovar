package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;
import jannovar.exception.AnnotationException;

/**
 * This class is intended to provide a static method to generate annotations for
 * block substitution  mutations. This method is put in its own class only for 
 * convenience and to at least have a name that is easy to find.
 * <P>
 * Block substitutions are recognized in the calling class {@link jannovar.reference.Chromosome Chromosome}
 * by the fact that the length of the variant sequence is greater than 1.
 * @version 0.07 (7 July, 2013)
 * @author Peter N Robinson
 */

public class BlockSubstitution {



     /**
     * Creates annotation for a block substitution on the plus strand.
     * @param kgl The transcript model that corresponds to the deletion caused by the variant.
     * @param frame_s 0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
     * @param wtnt3 Nucleotide sequence of wildtype codon
     * @param wtnt3_after Nucleotide sequence of codon following that affected by variant
     * @param ref sequence of wildtype sequence
     * @param var alternate sequence (should be '-')
     * @param refvarstart Position of the variant in the CDS of the known gene
     * @param refvarend Position of the end of the variant in the CDS of the known gene
     * @param exonNumber Number of the affected exon (one-based: TODO chekc this).
     * @return An annotation corresponding to the deletion.
     */
    public static Annotation getAnnotationPlusStrand(TranscriptModel kgl,int frame_s, String wtnt3,String wtnt3_after,
						     String ref, String var,int refvarstart,int refvarend, 
						     int exonNumber) throws AnnotationException {
	String annotation = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	String canno=null; // cDNA annotation.
	String panno=null;
	String varnt3=null;
	int refcdsstart = kgl.getRefCDSStart(); /* position of start codon in transcript. */
	int startPosMutationInCDS = refvarstart-refcdsstart+1;

	if (frame_s == 1) {
	    //$varnt3 = $wtnt3[0] . $obs . $wtnt3[2];
	    varnt3 = String.format("%c%s%c",wtnt3.charAt(0),var,wtnt3.charAt(2));
	} else if (frame_s == 2) {
	    //$varnt3 = $wtnt3[0] . $wtnt3[1] . $obs;
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(0),wtnt3.charAt(1),var);
	}  else {
	    //$varnt3 = $obs . $wtnt3[1] . $wtnt3[2];
	    varnt3 = String.format("%s%c%c",var, wtnt3.charAt(1),wtnt3.charAt(2));
	}
	canno = String.format("c.%d_%ddelins%s",refvarstart - refcdsstart +1, refvarend-refcdsstart+1,var);
	if ((refvarend-refvarstart+1-var.length()) % 3 == 0)  {
	    panno =   String.format("%s:%s:exon:%d:%s",kgl.getGeneSymbol(),kgl.getName(),exonNumber,canno);
	    Annotation ann = new Annotation(kgl,panno,VariantType.NON_FS_SUBSTITUTION,refvarstart);
	    return ann;
	} else {  /* i.e., frameshift */
	    /* $function->{$index}{fssub} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:c." . 
	       ($refvarstart-$refcdsstart+1) . "_" . ($refvarend-$refcdsstart+1) . "delins$obs,"; */
	    panno =   String.format("%s:exon:%d:%s",kgl.getName(),exonNumber,canno);
	    //Annotation ann = Annotation.createFrameShiftSubstitionAnnotation(kgl,startPosMutationInCDS,panno);
	    System.out.println("Panno=" + panno);
	    Annotation ann = new Annotation(kgl,panno,VariantType.FS_SUBSTITUTION, startPosMutationInCDS);
	    return ann;
					
	}
    }


     /**
     * Creates annotation for a block substitution on the plus strand. Both ref and var are blocks.
     * @param kgl The TranscriptModel that corresponds to the deletion caused by the variant.
     * @param frame_s 0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
     * @param wtnt3 Nucleotide sequence of wildtype codon
     * @param wtnt3_after Nucleotide sequence of codon following that affected by variant
     * @param ref sequence of wildtype sequence
     * @param var alternate sequence (should be '-')
     * @param refvarstart Position of the variant in the CDS of the known gene
     * @param refvarend Position of the end of the variant in the CDS of the known gene
     * @param exonNumber Number of the affected exon (one-based: TODO chekc this).
     * @return An annotation corresponding to the deletion.
     */
    public static Annotation getAnnotationBlockPlusStrand(TranscriptModel kgl,int frame_s, String wtnt3,String wtnt3_after,
						     String ref, String var,int refvarstart,int refvarend, 
						     int exonNumber) throws AnnotationException {

	String annotation = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	String canno=null; // cDNA annotation.
	String panno=null;
	String varnt3=null;
	int refcdsstart = kgl.getRefCDSStart(); /* position of start codon in transcript. */
	int startPosMutationInCDS = refvarstart-refcdsstart+1;
	//int cdsEndPos = endpos - refcdsstart + 1;
	//int cdsStartPos = cdsEndPos - var.length() + 1;
	int cdsEndPos = startPosMutationInCDS + var.length() - 1;

	if ((refvarend-refvarstart+1-var.length()) % 3 == 0) {
	    canno = String.format("%s:%s:exon%d:c.%d_%ddelins%s",kgl.getGeneSymbol(),kgl.getName(),exonNumber,
				  startPosMutationInCDS,refvarend-refcdsstart+1,var);
	    Annotation ann = new Annotation(kgl,canno,VariantType.NON_FS_SUBSTITUTION,startPosMutationInCDS);
	    return ann;
	} else {
	    canno = String.format("%s:exon%d:c.%d_%ddelins%s",kgl.getName(),exonNumber,
				  refvarstart-refcdsstart+1,refvarend-refcdsstart+1,var);
	    /** aavarpos is now the FIRST position (one-based) of the amino-acid sequence
		that was duplicated.*/
	    int aaVarStartPos =  startPosMutationInCDS % 3 == 0 ? 
		(int) Math.floor(startPosMutationInCDS/ 3) : 
		(int) Math.floor(startPosMutationInCDS/ 3) +1;
	    /* generate in-frame snippet for translation and correct for '-'-strand */
	    if (kgl.isMinusStrand()) {
		/* Re-adjust the wildtype nucleotides for minus strand */
		wtnt3 = kgl.getWTCodonNucleotides(startPosMutationInCDS-1+((3 - (var.length() % 3)) % 3), frame_s);
	    }
	    //String varnt3 =  DuplicationAnnotation.getVarNt3(kgl,wtnt3,var,frame_s);
	    String wtaa = translator.translateDNA(wtnt3);		
	    //String varaa = translator.translateDNA(varnt3);
	    panno = String.format("%s:p.%s%dfs",canno,wtaa,aaVarStartPos);
	    Annotation ann = new Annotation(kgl,panno,VariantType.FS_SUBSTITUTION,startPosMutationInCDS);
	    return ann;
	}
    }

       

}
/* eof */