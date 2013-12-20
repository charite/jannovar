package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;
import jannovar.exception.AnnotationException;

/**
 * This class is intended to provide a static method to generate annotations for insertion
 * mutations. This method is put in its own class only for convenience and to at least
 * have a name that is easy to find.
 * @version 0.06 (7 July, 2013)
 * @author Peter N Robinson
 */

public class InsertionAnnotation {

    /**
     * Annotates an insertion variant. The fact that a variant is an insertion variant
     * has been identified by the fact that the start and end positition of the variant 
     * are equal and the reference sequence is indicated as "-".
     * <P>
     * The insertion coordinate system in ANNOVAR always uses "position after the current site"
     * in positive strand, this is okay
     * in negative strand, the "after current site" becomes "before current site" during transcription
     * therefore, appropriate handling is necessary to take this into account
     * for example, for a trinucleotide GCC with frameshift of 1 and insertion of CCT
     * in positive strand, it is G-CTT-CC
     * but if the transcript is in negative strand, the genomic sequence should be GC-CCT-C, and transcript is G-AGG-GC
     * <P>
     * @param trmdl The gene in which the current mutation is contained
     * @param frame_s the location within the frame (0,1,2) in which mutation occurs
     * @param wtnt3 The three nucleotides of codon affected by start of mutation
     * @param wtnt3_after the three nucleotides of the codon following codon affected by mutation
	 * @param ref - never used, could be removed
	 * @param var
     * @param refvarstart The start position of the variant with respect to the CDS of the mRNA
     * @param exonNumber Number (one-based) of affected exon.
     * @return an {@link jannovar.annotation.Annotation Annotation} object representing the current variant
     */
    
    public static Annotation  getAnnotationPlusStrand(TranscriptModel trmdl,int frame_s, String wtnt3,String wtnt3_after,
						      String ref, String var,int refvarstart,int exonNumber) throws AnnotationException  {
     	// check if the insertion in truth is a duplication,
    	if(trmdl.isPlusStrand()){
    		if(trmdl.getCdnaSequence().substring(refvarstart-var.length()-1, refvarstart-1).equals(var)){
        		Annotation ann = DuplicationAnnotation.getAnnotation(trmdl, frame_s, wtnt3, wtnt3_after, ref, var, refvarstart, exonNumber);
        		return ann;
        	}
    	}else{
    		if(trmdl.getCdnaSequence().length() > refvarstart+var.length() && trmdl.getCdnaSequence().substring(refvarstart,refvarstart+var.length()).equals(var)){
        		Annotation ann = DuplicationAnnotation.getAnnotation(trmdl, frame_s, wtnt3, wtnt3_after, ref, var, refvarstart, exonNumber);
        		return ann;
        	}
           	
    	}

		/* for transcriptmodels on the '-' strand the mRNA position has to be adapted */
    	if(trmdl.isMinusStrand())
    		refvarstart--;


    	int refcdsstart = trmdl.getRefCDSStart() ;
    	int startPosMutationInCDS = refvarstart-refcdsstart+1; 
    	int aavarpos = (int)Math.floor(startPosMutationInCDS/3)+1;    	
    	String annotation = null;

	//String annovarClass = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	String varnt3 = null;
	if (trmdl.isPlusStrand() ) {
	    if (frame_s == 1) { /* insertion located at 0-1-INS-2 part of codon */
		varnt3 = String.format("%c%c%s%c",wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
	    } else if (frame_s == 2) {
		varnt3 = String.format("%s%s", wtnt3, var);
	    } else { /* i.e., frame_s == 0 */
		varnt3 = String.format("%c%s%c%c",wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
	    }
	} else if (trmdl.isMinusStrand()) {
	    if (frame_s == 1) {
		varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
	    } else if (frame_s == 2) {
		varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
	    } else { /* i.e., frame_s == 0 */
		varnt3 = String.format("%s%s", var, wtnt3); 
	    }
	}
	String wtaa = translator.translateDNA(wtnt3);
	String wtaa_after = null;
	if (wtnt3_after != null && wtnt3_after.length() > 0) {	
	    wtaa_after = translator.translateDNA(wtnt3_after);
	}
	/* wtaa_after could be undefined, if the current aa is the stop codon (X) 
	 * example:17        53588444        53588444        -       T
	 */
	String varaa = translator.translateDNA(varnt3);
//	int refcdsstart = trmdl.getRefCDSStart() ;

//	int aavarpos = (int) Math.floor((refvarstart-refcdsstart)/3)+1;  
//	int startPosMutationInCDS = refvarstart-refcdsstart+1;
	
	/* Annovar: $canno = "c." . ($refvarstart-$refcdsstart+1) .  "_" . 
	 * 				($refvarstart-$refcdsstart+2) . "ins$obs";		
	 */
	String canno = String.format("c.%d_%dins%s",startPosMutationInCDS,refvarstart-refcdsstart+2,var);
	/* If length of insertion is a multiple of 3 */
	if (var.length() % 3 == 0) {
	    if (wtaa.equals("*")) { /* Mutation affects the wildtype stop codon */
		int idx = varaa.indexOf("*");
		if (idx>=0) {
		    /* delete all aa after stop codon, but keep the aa before 
		     * Note we use an asterisk (*) to denote the stop codon according to HGVS nomenclature
		     * annovar: $varaa =~ s/\*.* /X/; */
		    varaa = String.format("%s*",varaa.substring(0,idx+1));
		    String annot = String.format("%s:exon:%d:%s:p.X%ddelins%s",trmdl.getName(),
						 exonNumber,canno,aavarpos,varaa);
		    
		    /* $function->{$index}{nfsins} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . 
		       "delins$varaa,";		#stop codon is stil present */
		    //Annotation ann = Annotation.createNonFrameshiftInsertionAnnotation(trmdl,startPosMutationInCDS,annot);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.NON_FS_INSERTION,startPosMutationInCDS);
		    return ann;
		} else {
		    /* Mutation => stop codon is lost, i.e., STOPLOSS 
		     *  Annovar: $seqid:exon$exonpos:$canno:p.X$varpos" . "delins$varaa";   */
		    String annot = String.format("%s:exon%d:%s:p.X%ddelins%s",trmdl.getName(),
						 exonNumber,canno,aavarpos,varaa);
		    //Annotation ann = Annotation.createStopLossAnnotation(trmdl,startPosMutationInCDS,annot);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.STOPLOSS,startPosMutationInCDS);
		    return ann;
		}
	    } else { /* i.w., wtaa is not equal to '*'  */
		int idx = varaa.indexOf("*");
		if (idx>=0) { /* corresponds to annovar: if ($varaa =~ m/\* /) {  */
		    varaa = String.format("%s*",varaa.substring(0,idx+1));
		    /* i.e., delete all aa after stop codon, but keep the aa before */
		    String annot = String.format("%s:exon%d:%s:p.%s%ddelins%s",trmdl.getName(),
						 exonNumber,canno,wtaa,aavarpos,varaa);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.STOPGAIN,startPosMutationInCDS);
		    return ann;
		} else {
		    /*$function->{$index}{nfsins} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . 
		     * "delins$varaa,"; */
		    String annot = String.format("%s:exon%d:%s:p.%s%ddelins%s",trmdl.getName(),
						 exonNumber,canno,wtaa,aavarpos,varaa);
		    //Annotation ann = Annotation.createNonFrameshiftInsertionAnnotation(trmdl,startPosMutationInCDS,annot);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.NON_FS_INSERTION,startPosMutationInCDS);
		    return ann;
		}
	    }
	} else { /* i.e., length of variant is not a multiple of 3 */
	    if (wtaa.equals("*") ) { /* mutation on stop codon */
		int idx = varaa.indexOf("*"); /* corresponds to : if ($varaa =~ m/\* /) {	 */
		if (idx>=0) {
		    /* in reality, this cannot be differentiated from non-frameshift insertion, but we'll still call it frameshift */
		    /* delete all aa after stop codon, but keep the aa before 
		     * annovar: $varaa =~ s/\*.* /X/; */
		    varaa = String.format("%sX",varaa.substring(0,idx+1));
		    String annot = String.format("%s:exon%d:%s:p.X%ddelins%s",trmdl.getName(),
						 exonNumber,canno,aavarpos,varaa);
		    //Annotation ann = Annotation.createFrameshiftInsertionAnnotation(trmdl,startPosMutationInCDS,annot);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.FS_INSERTION,startPosMutationInCDS);
		    return ann;
		} else { /* var aa is not stop (*) */
		    /* $function->{$index}{stoploss} .= 
		     * "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . "delins$varaa,"; */
		    String annot = String.format("%s:exon%d:%s:p.X%ddelins%s",trmdl.getName(),
						 exonNumber,canno,aavarpos,varaa);
		    //Annotation ann = Annotation.createStopLossAnnotation(trmdl,startPosMutationInCDS,annot);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.STOPLOSS,startPosMutationInCDS);
		    return ann;
		}
	    } else { /* i.e., wtaa not a stop codon */
		int idx = varaa.indexOf("*");
		if (idx>=0) {
		    /** Note use of asterisk (*) to denote stop codon as per HGVS recommendation. */
		    varaa = String.format("%s*",varaa.substring(0,idx+1));
		    /*"$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . 
		     * 	"_$wtaa_after" . ($varpos+1) . "delins$varaa,"; */
		    String annot = String.format("%s:exon%d:%s:p.%s%d_%s%ddelins%s", trmdl.getName(),
						 exonNumber,canno,wtaa,aavarpos,wtaa_after,(aavarpos+1),varaa);
		    Annotation ann = new Annotation(trmdl, annot,VariantType.STOPGAIN, startPosMutationInCDS);
		    return ann;
		} else {
		    String annot = String.format("%s:exon%d:%s:p.%s%dfs",trmdl.getName(),exonNumber,canno,wtaa,aavarpos);
		    //Annotation ann = Annotation.createFrameshiftInsertionAnnotation(trmdl,startPosMutationInCDS,annot);
		    Annotation ann = new Annotation(trmdl,annot,VariantType.FS_INSERTION,startPosMutationInCDS);
		    return ann;
		    
		}
	    }
	}	
    }
}
/* end of file */
