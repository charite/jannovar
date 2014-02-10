package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;
import jannovar.exception.AnnotationException;

/**
 * This class provides static methods to generate annotations for deletion
 * mutations. Updated on 27 December 2013 to provide HGVS conformation
 * annotations for frameshirt deletion mutations.
 * Note that if we have the following VCF line:
 * <pre>
 * chr11	76895771	.	GGAGGCGGGGACACCAGGGCCTG	G	55.5	.	DP=9;VDB...
 * </pre>
 * then the position refers to the nucleotide right before the deletion. That is, the first nucleotide [G]GAGGC..
 * (the one that is enclosed in square brackets) has the position 76895771, and the deletion begins at
 * chromosomal position 76895772 and comprises 22 bases: GAG-GCG-GGG-ACA-CCA-GGG-CCT-G. (Note we are using
 * one-based numbering here).
 * This particular deletion corresponds to  NM_001127179(MYO7A_v001):c.3515_3536del
 * NM_001127179(MYO7A_i001):p.(Gly1172Glufs*34).
 * @version 0.16 (14 January, 2014)
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
     * @throws jannovar.exception.AnnotationException
     */
    public static Annotation getAnnotationSingleNucleotide(TranscriptModel kgl,int frame_s,
							   String wtnt3,String wtnt3_after,
							   String ref, String var,int refvarstart,
							   int exonNumber) 
	throws AnnotationException 
    {
	String annotation = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	// varnt3 is the codon affected by the deletion, it is the codon that
	// results from the deletion at the same position in the aa as the wt codon was.
	String varnt3;
	int posVariantInCDS = refvarstart-kgl.getRefCDSStart()+1; /* position of deletion within coding sequence */
	int aavarpos = (int)Math.floor(posVariantInCDS/3)+1; /* position of deletion in protein */
	/*System.out.println(kgl.getGeneSymbol() + "(" + kgl.getAccessionNumber() + ") " +
			   " frame_s=" + frame_s + "; wtnt3=" + wtnt3 + "; wtnt3_after=" + wtnt3_after
			   + "; ref=" + ref + ";  alt="+var + "; refvarstart=  "+refvarstart); */
			   
	/* Note that in some pathological cases, wtnt3_after is null. This is the case with
	 * chr11	64366391	.	TG	T, which affects multiple transcripts of
	 * the SLC22A12 gene including uc009ypr.1. The deletion of a G affects a sequence TGC-TG
	 * where the transcript ends abruptly with the 2 nucleotide partial transcript TG, so that
	 * wtnt3=TGC and wtnt3_after=null. In cases like this, we will just return the nucleotide
	 * deletion and not attempt to translate to protein. */
	if (wtnt3_after==null || wtnt3_after.length()<3) {
	    String canno = String.format("%s:exon%d:c.%ddel%s",kgl.getName(),
					 exonNumber,posVariantInCDS,ref);
	    Annotation ann = new Annotation(kgl,canno,VariantType.FS_DELETION,posVariantInCDS);
	    return ann;
	}

	if (frame_s == 1) {
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(0), wtnt3.charAt(2),wtnt3_after.charAt(0));
	} else if (frame_s == 2) {
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(0), wtnt3.charAt(1),wtnt3_after.charAt(0));
	} else {
	    varnt3 = String.format("%c%c%s",wtnt3.charAt(1), wtnt3.charAt(2),wtnt3_after.charAt(0));
	}

	String wtaa = translator.translateDNA(wtnt3);
	String varaa = translator.translateDNA(varnt3);
	
	/* The following gives us the cDNA annotation */
	String canno = String.format("c.%ddel%s",posVariantInCDS,ref);
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
		/* A deletion affecting an amino-acid in the middle of the protein and leading to a frameshift */
		String fsdel_ann = String.format("%s:exon%d:%s:p.%s%dfs",kgl.getName(),
						 exonNumber,canno,wtaa,aavarpos);
		Annotation ann = new Annotation(kgl, fsdel_ann,VariantType.FS_DELETION,posVariantInCDS);
		return ann;
	    }
	}
    }


     /**
     * Creates annotation for a deletion of more than one nucleotide.
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
     * @param refvarstart start position of the variant in the CDS of the known gene
     * @param refvarend end position of the variant in the CDS of the known gene
     * @param exonNumber Number of the affected exon (one-based: TODO chekc this).
     * @return {@link Annotation} object corresponding to deletion variant
     * @throws jannovar.exception.AnnotationException
     */
    public static Annotation getMultinucleotideDeletionAnnotation(TranscriptModel kgl,int frame_s, String wtnt3,String wtnt3_after,
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
		varposend = (int)Math.floor( cdslen / 3); 
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
//	    varposend = (int)Math.floor(( refvarend- refcdsstart)/3) + 1;
	    int posMutationInCDS = refvarstart-refcdsstart+1; /* start pos of mutation with respect to CDS begin */
	    canno = String.format("c.%d_%ddel",posMutationInCDS,refvarend-refcdsstart+1);
	    try {
		panno = shiftedFrameDeletion(kgl,exonNumber,canno,ref,posMutationInCDS,aavarpos,frame_s);
	    } catch (AnnotationException e) {
		System.err.println("Exception while annotating frame-shift deletion: " + canno);
		panno=canno; /* just supply the cDNA annotation if there was an error. */
	    }
	    Annotation ann = new Annotation(kgl, panno,VariantType.FS_DELETION,posMutationInCDS);
	    return ann;
	}
    }

    /**
     * Gets the correct annotation for a deletion that has led to a
     * frameshift, such as p.(Gln40Profs*18), which results from 
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
	// Also include the 3UTR because some deletions extend the
	// mutant coding sequence beyond the stop codon.
	String orf = trmdl.getCodingSequencePlus3UTR();

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
	    System.out.println("rest = "+ rest + ", restlen="+restlen);
	    System.out.println("prefix ="+ prefix);
	    System.out.println("wt:" + wtaa);
	    System.out.println("mt:" + mutaa);
	    trmdl.debugPrintCDS();
	*/
	int aapos = aaVarStartPos;
	int endk = mutaa.length();
	String annot;
	for (int k=0;k<endk;++k) {
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