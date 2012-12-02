package exomizer.annotation;

import exomizer.reference.KnownGene;
import exomizer.reference.Annotation;
import exomizer.reference.Translator;
import exomizer.exception.AnnotationException;

/**
 * This class is intended to provide a static method to generate annotations for single
 * nucleotide substitution  mutations. This method is put in its own class only for 
 * convenience and to at least have a name that is easy to find.
 * @version 0.02 (2 December, 2012)
 * @author Peter N Robinson
 */

public class SingleNucleotideSubstitution {


     /**
     * Creates annotation for a single-nucleotide substitution on the plus strand.
     * @param kgl The known gene that corresponds to the deletion caused by the variant.
     * @param frame_s 0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
     * @param wtnt3 Nucleotide sequence of wildtype codon
     * @param wtnt3_after Nucleotide sequence of codon following that affected by variant
     * @param ref sequence of wildtype sequence
     * @param var alternate sequence (should be '-')
     * @param refvarstart Position of the variant in the CDS of the known gene
     * @param exonNumber Number of the affected exon (zero-based).
     * @return An annotation corresponding to the deletion.
     */
    public static Annotation getAnnotationPlusStrand(KnownGene kgl,int frame_s, String wtnt3,String wtnt3_after,
		String ref, String var,int refvarstart,int exonNumber) throws AnnotationException {
	String annotation = null;
	Translator translator = Translator.getTranslator(); /* Singleton */
	 //$do_trim = 3;   # Trim first 3 nt of post_pad for variant, as wtnt3_after is being added here.
	String canno=null; // cDNA annotation.
	String panno=null;
	String varnt3=null;
	int refcdsstart = kgl.getRefCDSStart(); /* position of start codon in transcript. */

	//exonNumber++; /* Correct to one-based numbering, here used only for annotation! */

	//System.out.println("refcdsstart=" + refcdsstart);
	if (ref.length() != 1) {
	    throw new AnnotationException(String.format("Error: Malformed reference sequence (%s) for SNV annotation of %s",
							ref,kgl.getName2()));
	} else if (var.length() != 1) {
	     throw new AnnotationException(String.format("Error: Malformed variant sequence (%s) for SNV annotation of %s",
							var,kgl.getName2()));
	}
	char refc = ref.charAt(0);
	char varc = var.charAt(0);
	if (frame_s == 1) {
	    //$varnt3 = $wtnt3[0] . $obs . $wtnt3[2];
	    varnt3 = String.format("%c%c%c",wtnt3.charAt(0),varc,wtnt3.charAt(2));
	    //$canno = "c.$wtnt3[1]" . ($refvarstart-$refcdsstart+1) . $obs;
	    canno = String.format("c.%d%c>%c",(refvarstart - refcdsstart+1),wtnt3.charAt(1),varc);
	    if (refc != wtnt3.charAt(1)) {
		char strand = kgl.getStrand();
		String wrng = String.format("WARNING: ALLELE MISMATCH: strand=%c user-specified-allele=\"%s\" exomizer-inferred-allele=\"%s\"",
					    strand,ref,wtnt3.charAt(1));
		canno = String.format("%s/%s",canno,wrng);
		throw new AnnotationException(canno);
	    }
	} else if (frame_s == 2) {
	    //$varnt3 = $wtnt3[0] . $wtnt3[1]. $obs;
	    varnt3 = String.format("%c%c%c",wtnt3.charAt(0),wtnt3.charAt(1),varc);
	    //$canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[2] . ">" . $obs;
	    canno = String.format("c.%d%c>%c",(refvarstart - refcdsstart+1),wtnt3.charAt(2),varc);
	    if (refc != wtnt3.charAt(2)) {
		char strand = kgl.getStrand();
		String wrng = String.format("WARNING: ALLELE MISMATCH: strand=%c user-specified-allele=\"%s\" exomizer-inferred-allele=\"%s\"",
					    strand,ref,wtnt3.charAt(2));
		canno = String.format("%s/%s",canno,wrng);
		throw new AnnotationException(canno);
	    }
	} else { /* i.e., frame_s == 0 */
	    //$varnt3 = $obs . $wtnt3[1] . $wtnt3[2];
	    varnt3 = String.format("%c%c%c",varc, wtnt3.charAt(1),wtnt3.charAt(2));
	    // $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[0] . ">" . $obs;
	    canno =  String.format("c.%d%c>%c",(refvarstart - refcdsstart+1),wtnt3.charAt(0),varc);
	    //System.out.println("wtnt3=" + wtnt3 + " varnt3=" + varnt3 + " canno=" + canno);
	    //System.out.println(kgl.getName2() + ":" + kgl.getName());
	    //System.out.println("refvarstart=" + refvarstart + " refcdsstart = " + refcdsstart);
	    //kgl.debugPrint();
	    if (refc  != wtnt3.charAt(0)) {
		char strand = kgl.getStrand();
		String wrng = String.format("WARNING: ALLELE MISMATCH: strand=\"%c\" user-specified-allele=\"%s\" exomizer-inferred-allele=\"%s\"",
					    strand,ref,wtnt3.charAt(0));
		canno = String.format("%s/%s",canno,wrng);
		throw new AnnotationException(canno);
	    }
	}
	String wtaa = translator.translateDNA(wtnt3);
	String varaa = translator.translateDNA(varnt3);
	int aavarpos = (int)Math.floor((refvarstart-kgl.getRefCDSStart())/3)+1;

	
	if (wtaa.equals(varaa)) {
	    //$wtaa eq '*' and ($wtaa, $varaa) = qw/X X/;		#change * to X in the output NO! Not HGVS conform
	    //$function->{$index}{ssnv} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";
	    panno = String.format("%s:%s:exon%d:%s:p.%s%d%s",kgl.getName2(),kgl.getName(),exonNumber,canno,wtaa,aavarpos,varaa);
	    Annotation ann = Annotation.createSynonymousSNVAnnotation(panno);
	    return ann;
	} else if (varaa.equals("*")) {
	    //$function->{$index}{stopgain} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa${varpos}X,";
	    panno = String.format("%s:%s:exon%d:%s:p.%s%d*",kgl.getName2(),kgl.getName(),exonNumber,canno,wtaa,aavarpos);
	    Annotation ann = Annotation.createStopGainAnnotation(panno);
	    return ann;
	} else if (wtaa.equals("*")) {
	    //$function->{$index}{stoploss} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos$varaa,";
	    panno = String.format("%s:%s:exon%d:%s:p.*%d%s",kgl.getName2(),kgl.getName(),exonNumber,canno,aavarpos,varaa);
	    Annotation ann = Annotation.createStopLossAnnotation(panno);
	    return ann;
	} else { /* Missense */
	    //    $function->{$index}{nssnv} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";
	    panno = String.format("%s:exon%d:%s:p.%s%d%s",kgl.getName(),exonNumber,canno,wtaa,aavarpos,varaa);
	    Annotation ann = Annotation.createMissenseSNVAnnotation(kgl,refvarstart,panno);
	    return ann;
	}
	
	
    } 

}