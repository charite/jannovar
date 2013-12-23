/**
 * 
 */
package jannovar.annotation;


import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;

/**
 * This class is intended to provide a static method to generate annotations for
 * duplication mutations.
 * TODO currently the handling of tandem-repeats is not as recommended <br>
 * e.g. these cases:<br>
 * g.7_8dup (or g.7_8dupTG, not g.5_6dup, not g.8_9insTG) denotes a TG duplication in 
 * the TG-tandem repeat sequence changing ACTTTGTGCC to ACTTTGTGTGCC <br>
 * g.7_8[4] (or g.5_6[4], or g.5TG[4], not g.7_10dup) is the preferred description of  
 * the addition of two extra TG's to the variable TG repeated sequence changing ACTTTGTGCC to ACTTTGTGTGTGCC
 * 
 * @author mjaeger
 * @version 0.2 (20-12-2013)
 */
public class DuplicationAnnotation {

    /**
     * Annotates an insertion variant which is an duplication. The fact that a
     * variant is a duplication variant has been identified by the fact that the
     * start and end positions of the variant are equal and the reference
     * sequence is indicated as "-". <br>
     * e.g. additionally to the conditions for a insertion variant, the
     * duplication variant requires a similar sequence to the insertion (before
     * or) after the insertion.
     * <P>
     * There are two possible duplication insertions with or without frameshift
     * causation. e.g. inserting an additional 'C' in the sequence 'ACC,GAG' at
     * position 2 would cause a frameshift, whereas insertion of 'CCG' at
     * position 2 just inserts an additional triplicate 'ACC GCC GAG'.
     * <P>
     * 
     * if (var.length() % 3 == 0) { /* ORF CONSERVING 
     * 	if(startPosMutationInCDS.length() % 3 == 0){ /* SIMPLE DUPLICATION OF CODONS
     * 	}else{ /* substitution from original AA to AAs
     * 		if(wtaa.equals("*")) { /* Mutation affects the wildtype stop codon
     * 			int idx = varaa.indexOf("*");
     * 			if (idx < 0) {
     * 		}
     * 		/* Substitution
     * 	}
     * }else { /* FRAMESHIFT
     *      	 * short p.(Arg97fs)) denotes a frame shifting change with Arginine-97 as the first affected amino acid
     * }
     * 
     * 
     * @param trmdl  The transcriptmodel / gene in which the current mutation is contained
     * @param frame_s the location within the frame (0,1,2) in which mutation occurs
     * @param wtnt3  The three nucleotides of codon affected by start of mutation
     * @param wtnt3_after  the three nucleotides of the codon following codon affected by  mutation
     * @param ref reference nucleotide sequence ("-")
     * @param var alternate nucleotide sequence (the duplication)
     * @param refvarstart  The nucleotide position just upstream of the variant with respect to the cDNA (one-based)
     * @param exonNumber    Number (one-based) of affected exon.
     * @return an {@link jannovar.annotation.Annotation Annotation} object representing the current variant
     * @throws AnnotationException
     */
    public static Annotation getAnnotation(TranscriptModel trmdl, int frame_s, String wtnt3,
					   String wtnt3_after, String ref, String var, 
					   int refvarstart, int exonNumber) throws AnnotationException 
    {
	String annot;
	Annotation ann = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	/* For the '-'-strand fix the position in the reference */
	if(trmdl.isMinusStrand()){
	    refvarstart = refvarstart + var.length()+1;
	}
	// Note: refvarstart refers to the cDNA position, not the coding (CDS) position.
	int refcdsstart = trmdl.getRefCDSStart();
	int startPosMutationInCDS = refvarstart - refcdsstart + 1;
	// Since refvarstart is the position just upstream of the duplication, if we substract the 
	// length of the variant we need to add back one.
	int duplicationStartPos = refvarstart-var.length()- refcdsstart+1; // First nucleotide of last duplicated unit
	int duplicationEndPos = refvarstart - refcdsstart; // Last nucleotide of last duplicated unit


	int aavarpos =  startPosMutationInCDS % 3 == 0 ? 
	    (int) Math.floor(startPosMutationInCDS / 3) : 
	    (int) Math.floor(startPosMutationInCDS / 3) +1;
	
	//		if(trmdl.isMinusStrand())
	//			aavarpos += startPosMutationInCDS % 3;
	
	/* get coding DNA HGVS string */
	String canno;
	if(var.length() == 1)
	    canno = String.format("c.%ddup%s", duplicationStartPos, var); 
	else
	    canno = String.format("c.%d_%ddup%s", duplicationStartPos, duplicationEndPos, var);
	
	/* now the protein HGVS string */
	
	/* generate in frame snippet for translation and correct for '-'-strand */
	String varnt3 = null;
	if (trmdl.isPlusStrand()) {
	    frame_s = 2 - frame_s;
	    if (frame_s == 0) { /* duplication located at 0-1-INS-2 part of codon */
		varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
	    } else if (frame_s == 2) {
		varnt3 = String.format("%s%s", var, wtnt3);
	    } else { /* i.e., frame_s == 0 */
		varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
	    }
	}else if (trmdl.isMinusStrand()) {
	    //			wtnt3 = trmdl.getWTCodonNucleotides(refvarstart-1, frame_s);
	    //			wtnt3 = (new StringBuilder(wtnt3)).reverse().toString();
	    wtnt3 = trmdl.getWTCodonNucleotides(refvarstart-1+((3 - (var.length() % 3)) % 3), frame_s);
	    frame_s = 2 - frame_s;
	    if (frame_s == 2) {
		varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
	    } else if (frame_s == 1) {
		varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
	    } else { /* i.e., frame_s == 0 */
		varnt3 = String.format("%s%s", wtnt3, var);
	    }
	    //			frame_s = 2 - frame_s;
	}
	
	String wtaa = translator.translateDNA(wtnt3);		
	String varaa = translator.translateDNA(varnt3);
	
	if (var.length() % 3 == 0) { /* ORF CONSERVING */
	    if((startPosMutationInCDS-1) % 3 == 0){ /* SIMPLE DUPLICATION OF CODONS */ 
		
		String wtaaDupStart = translator.translateDNA(var.substring(0,3));
		String wtaaDupEnd	= translator.translateDNA(var.substring(var.length()-3));
		if (varaa.length() - 1 == 1) {
		    // the aavarpos must be decreased because we want
		    // the duplicated AA position, not the affected
		    // position
		    annot = String.format("%s:exon%d:%s:p.%s%ddup", trmdl.getName(), exonNumber, canno, wtaaDupStart,
					  aavarpos - 1);
		} else {
		    annot = String.format("%s:exon%d:%s:p.%s%d_%s%ddup", trmdl.getName(), exonNumber,
					  canno, wtaaDupStart, aavarpos - varaa.length() +1,
					  wtaaDupEnd, aavarpos-1);
		}
		ann = new Annotation(trmdl, annot, VariantType.NON_FS_DUPLICATION, startPosMutationInCDS);
	    }else{ /* substitution from original AA to AAs */
		if(wtaa.equals("*")) { /* Mutation affects the wildtype stop codon */
		    int idx = varaa.indexOf("*");
		    if (idx < 0) {
			annot = String.format("%s:exon%d:%s:p.*%d%sext*?", trmdl.getName(), exonNumber,
					      canno, aavarpos, varaa);
		    }else{
			
			annot = String.format("%s:exon%d:%s:p.*%ddelins%s", trmdl.getName(), exonNumber,
					      canno, aavarpos, varaa.substring(0, idx+1));
		    }
		}else{
		    annot = String.format("%s:exon%d:%s:p.%s%ddelins%s", trmdl.getName(), exonNumber,
					  canno, wtaa, aavarpos, varaa);
		}
		/* Substitution */
		ann = new Annotation(trmdl, annot, VariantType.NON_FS_DUPLICATION, startPosMutationInCDS);
	    }
	}else { /* FRAMESHIFT 
		 * short p.(Arg97fs)) denotes a frame shifting change with Arginine-97 as the first affected amino acid */ 
	    
	    annot = String.format("%s:exon%d:%s:p.%s%dfs", trmdl.getName(), exonNumber,
				  canno, wtaa, aavarpos);
	    ann = new Annotation(trmdl, annot, VariantType.FS_DUPLICATION, startPosMutationInCDS);
	}
	return ann;
    }
    
}
/* eof */