package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;
import jannovar.exception.AnnotationException;

/**
 * This class provides static methods to generate annotations for deletion
 * mutations. Updated on 27 December 2013 to provide HGVS conformation
 * annotations for frameshirt deletion mutations.
 * @version 0.13 (27 December, 2013)
 * @author Peter N Robinson
 */

public class DeletionAnnotation {

    /**
     * Creates annotation for a single-nucleotide deletion.
     * @param kgl The known gene that corresponds to the deletion caused by the variant.
     * @param frame_s 0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
     * @param wtnt3 Nucleotide sequence of wildtype codon
     * @param wtnt3_after Nucleotide sequence of codon following that affected by variant
     * @param ref sequence of wildtype sequence
     * @param var alternate sequence (should be '-')
     * @param refvarstart Position of the variant in the CDS of the known gene
     * @param exonNumber Number of the affected exon.
     * @return An annotation corresponding to the deletion.
     */
    public static Annotation getAnnotationSingleNucleotide(TranscriptModel kgl,int frame_s,
							   String wtnt3,String wtnt3_after,
							   String ref, String var,int refvarstart,
							   int exonNumber) 
	throws AnnotationException 
    {
	String annotation = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	// deletedNT is the first deleted nucleotide
	char deletedNT=' ';
	// varnt3 is the codon affected by the deletion, it is the codon that
	// results from the deletion at the same position in the aa as the wt codon was.
	String varnt3=null;
	if (frame_s == 1) {
	    deletedNT = wtnt3.charAt(1);
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(0), wtnt3.charAt(2),wtnt3_after.charAt(0));
	} else if (frame_s == 2) {
	    deletedNT = wtnt3.charAt(2);
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(0), wtnt3.charAt(1),wtnt3_after.charAt(0));
	} else {
	    deletedNT = wtnt3.charAt(0);
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(1), wtnt3.charAt(2),wtnt3_after.charAt(0));
	}
	String wtaa = translator.translateDNA(wtnt3);
	String varaa = translator.translateDNA(varnt3);
	int posVariantInCDS = refvarstart-kgl.getRefCDSStart();
	int aavarpos = (int)Math.floor(posVariantInCDS/3)+1;
	
	/* The following gives us the cDNA annotation */
	String canno = String.format("c.%ddel%c",(refvarstart-kgl.getRefCDSStart()+1),deletedNT);
	/* Now create amino-acid annotation */
	if (wtaa.equals("*")) { /* mutation on stop codon */ 
	    if (varaa.startsWith("*")) { /* #stop codon is still stop codon 	if ($varaa =~ m/\* /)   */
		String nfsdel_ann = String.format("%s:exon%d:%s:p.X%dX",kgl.getName(),
						     exonNumber,canno,aavarpos);
		Annotation ann= new Annotation(kgl,nfsdel_ann,VariantType.NON_FS_DELETION,posVariantInCDS);
		return ann;
	    } else {	 /* stop codon is lost */
		String stoploss_ann = String.format("%s:exon%d:%s:p.X%d%s",kgl.getName(),
						    exonNumber,canno,aavarpos,varaa);
		Annotation ann = new Annotation(kgl,stoploss_ann,VariantType.STOPLOSS,posVariantInCDS);
		return ann;
	    }
	} else {
	    if (varaa.contains("*")) { /* new stop codon created */
		String stopgain_ann = String.format("%s:exon%d:%s:p.%s%dX",kgl.getName(),
						  exonNumber,canno,wtaa, aavarpos);
		Annotation ann = new Annotation(kgl,stopgain_ann,VariantType.STOPGAIN,posVariantInCDS);
		return ann;
	    } else {
		String fsdel_ann = String.format("%s:exon%d:%s:p.%s%dfs",kgl.getName(),
						  exonNumber,canno,wtaa,aavarpos);
		/*  $function->{$index}{fsdel} .= 
		    "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . "fs,"; */
		Annotation ann = new Annotation(kgl, fsdel_ann,VariantType.FS_DELETION,posVariantInCDS);
	
		return ann;
	    }
	}

    }


     /**
     * Creates annotation for a block deletion.
     * This is recognized by the fact that the ref sequence has a length greater than one
     * and the variant sequence is "-".
     * <P>
     * Note that with the $firstcodondel option set to true, annovar reports deletions that
     * affect the first amino acid as ABC:uc001ab:wholegene (FSDEL). We will not follow annovar
     * here, but rather report such as deletion as with any other amino acid.
     * @param kgl The known gene that corresponds to the deletion caused by the variant.
     * @param frame_s 0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
     * @param wtnt3 Nucleotide sequence of wildtype codon
     * @param wtnt3_after Nucleotide sequence of codon following that affected by variant
     * @param ref sequence of wildtype sequence
     * @param var alternate sequence (should be '-')
     * @param refvarstart Position of the variant in the CDS of the known gene
     * @param exonNumber Number of the affected exon (one-based: TODO chekc this).
     * @return An annotation corresponding to the deletion.
     */
    public static Annotation getAnnotationBlock(TranscriptModel kgl,int frame_s, String wtnt3,String wtnt3_after,
						String ref, String var,int refvarstart,int refvarend, int exonNumber)
	throws AnnotationException  {
	String annotation = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	char deletedNT=' ';
	String varnt3=null;
	String canno = null;
	String panno = null;
	String wtaa = translator.translateDNA(wtnt3);
	int refcdsstart = kgl.getRefCDSStart();
	int cdslen = kgl.getCDSLength();

	int aavarpos = (int)Math.floor((refvarstart-kgl.getRefCDSStart())/3)+1;
	int varposend = -1; // 	the position of the last amino acid in the deletion
	int posVariantInCDS = refvarstart-kgl.getRefCDSStart();

	if (refvarstart <=refcdsstart) { /* first amino acid deleted */
	    if (refvarend >= cdslen  + refcdsstart) { // i.e., 3' portion of the gene is deleted
		varposend = (int)Math.floor( cdslen / 3); //int ($cdslen->{$seqid}/3);
		//$canno = "c." . ($refvarstart-$refcdsstart+1) . "_" . ($cdslen->{$seqid}+$refcdsstart-1) . "del";
		canno = String.format("c.%d_%ddel",refvarstart-refcdsstart,cdslen+refcdsstart-1);
					
	    } else { /* deletion encompasses less than entire CDS */
		varposend = (int)Math.floor(( refvarend-refcdsstart)/3) + 1;
		canno = String.format("c.1_%ddel",refvarend-refvarstart+1);
	    }
	    panno = String.format("%s:exon%d:%s:p.%d_%ddel",kgl.getName(), exonNumber,canno,aavarpos,varposend);
	    Annotation ann = new Annotation(kgl,panno,VariantType.FS_SUBSTITUTION, posVariantInCDS);
	    return ann;
	} else if (refvarend >= cdslen + refcdsstart) { 
	    /* -------------------------------------------------------------------- *
	     * if we get here, then the 3' part of the gene is deleted              *
	     * -------------------------------------------------------------------- */
	    varposend = (int)Math.floor(cdslen/3);	
	    canno = String.format("c.%d_%ddel",refvarstart - refcdsstart +1, cdslen + refcdsstart -1);
	    panno = String.format("%s:exon%d:%s:p.%d_%ddel",kgl.getName(),
				  exonNumber,canno,aavarpos,varposend);
	    Annotation ann = new Annotation(kgl,panno,VariantType.FS_SUBSTITUTION, posVariantInCDS);
	    return ann;
	} else if ((refvarend-refvarstart+1) % 3 == 0) { 
	    /* -------------------------------------------------------------------- *
	     * Non-frameshift deletion within the body of the mRNA                  *
	     * -------------------------------------------------------------------- */
	    varposend = (int)Math.floor(( refvarend- refcdsstart)/3) + 1;
	    posVariantInCDS = refvarstart-refcdsstart+1; /* start pos of mutation */
	    canno = String.format("c.%d_%ddel",posVariantInCDS,refvarend-refcdsstart+1);
	    panno =  String.format("%s:exon%d:%s:p.%d_%ddel",kgl.getName(), exonNumber,canno,aavarpos,varposend);
	   
	    Annotation ann= new Annotation(kgl,panno,VariantType.NON_FS_DELETION,posVariantInCDS);
	   
	    return ann;
	} else {
	    /* -------------------------------------------------------------------- *
	     * Frameshift deletion within the body of the mRNA                      *
	     * -------------------------------------------------------------------- */
	    varposend = (int)Math.floor(( refvarend- refcdsstart)/3) + 1;
	    int posMutationInCDS = refvarstart-refcdsstart+1; /* start pos of mutation with respect to CDS begin */
	    canno = String.format("c.%d_%ddel",posMutationInCDS,refvarend-refcdsstart+1);
	    panno = shiftedFrameDeletion(kgl,exonNumber,canno,ref,posMutationInCDS,aavarpos,frame_s);
	    Annotation ann = new Annotation(kgl, panno,VariantType.FS_DELETION,posMutationInCDS);
	    return ann;
	}
    }

    /**
     * Gets the correct annotation for a deletion that has led to a
     * framshift, such as p.(Gln40Profs*18), which results from 
     * a deletion of k nucleotides where k is not a multiple of 3.
     */
     private static String shiftedFrameDeletion(TranscriptModel trmdl, 
						int exonNumber,
						String cDNAanno, 
						String ref, 
						int posMutationInCDS,
						int aaVarStartPos,
						int frame_s)
	throws AnnotationException
    {
	Translator translator = Translator.getTranslator(); /* Singleton */

	int len = ref.length();
	// Get the complete coding sequence.
	String orf = trmdl.getCodingSequence();
	int start = posMutationInCDS-1;  // Convert 1-based to 0-based
	int endpos = start + ref.length(); // endpos is now 0-based and points to one after the deletion.
	String deletion = orf.substring(start,endpos);
	// Get the part of the codon that comes before the deletion
	String prefix = orf.substring(start-frame_s,start);
	// We do not know when the new sequence will differ from the wt sequence.
	// Try at least 10 amino acids.
	int restlen = (orf.length()-endpos)>30 ? 30 : orf.length()-endpos;
	String rest = orf.substring(endpos, endpos+restlen);
	String wt = prefix+deletion + rest;
	String mut = prefix + rest;
	String wtaa = translator.translateDNA(wt);
	String mutaa = translator.translateDNA(mut);
	/*
	  System.out.println("start=" + (start+1) + ", end="+(endpos+1));
	  System.out.println("deletion ="+ deletion);
	  System.out.println("rest = "+ rest);
	  System.out.println("prefix ="+ prefix);
	  System.out.println("wt:" + wtaa);
	  System.out.println("mt:" + mutaa);
	  trmdl.debugPrintCDS();
	*/
	int aapos = aaVarStartPos;
	int k = 0;
	int endk = mutaa.length();
	String annot = null;
	for (k=0;k<endk;++k) {
	    if (wtaa.charAt(k) != mutaa.charAt(k)) {
		annot = String.format("%s:exon%d:%s:p.%c%d%cfs", 
				      trmdl.getName(), exonNumber,
				      cDNAanno, wtaa.charAt(k), aapos,mutaa.charAt(k));
		return annot;// e.g.   p.(Gln40Profs*18)
	    } else {
		aapos++;
	    }
	}
	// if we get here, all amino acids were the same.
	// probably some weird nomenclature.
	//panno =  String.format("%s:exon%d:%s:p.%d_%ddel",kgl.getName(),
	//			   exonNumber,canno,aavarpos,varposend);
	annot = String.format("%s:exon%d:%s:p.%c%ddelins%c", trmdl.getName(), exonNumber,
			      cDNAanno, wtaa.charAt(0), aaVarStartPos, mutaa.charAt(0));
	return annot;
    }



}