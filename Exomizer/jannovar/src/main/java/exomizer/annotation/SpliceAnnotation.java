package exomizer.annotation;

import exomizer.reference.KnownGene;
import exomizer.reference.Annotation;
import exomizer.reference.Translator;


/**
 * This class is intended to provide a static method to generate annotations for splice
 * mutations. This method is put in its own class only for convenience and to at least
 * have a name that is easy to find.
 * <P> Annovar reports the two nucleotides on the boundary of the exon as being splice
 * mutations, which is not entirely accurate, although mutations in this part of exons
 * can indeed disrupt proper splicing. Instead, jannovar takes the SPLICING_THRESHOLD
 * nucleotides on the intronic side.
 * @version 0.06 (December 13, 2012)
 * @author Peter N Robinson
 */

public class SpliceAnnotation {
    /** Number of nucleotides away from exon/intron boundary to be considered as potential splicing mutation. */
    public final static int SPLICING_THRESHOLD=2;

    /**
     * Determine if the variant under consideration  (which the calling
     * code has determined to be on the plus strand) represents a splice variant, defined as 
     * being in the SPLICING_THRESHOLD nucleotides within the exon/intron boundry. If so,
     * return true, otherwise, return false.
     * @param k Exon number in gene represented by kgl
     * @param kgl Gene to be checked for splice mutation for current chromosomal variant.
     */
    public static boolean isSpliceVariantPlusStrand(KnownGene kgl, int start, int end, String ref, String alt, int k) {
	if (kgl.getExonCount() == 1) return false; /* Single-exon genes do not have introns */
	int exonend = kgl.getExonEnd(k);
	int exonstart = kgl.getExonStart(k);
	if (k==0 && start > exonend  && start <= exonend+SPLICING_THRESHOLD) {
	    /* In annovar: start >= exonend-SPLICING_THRESHOLD+1  instead of start > exonend */
	    /* variation is located right after (3' to) first exon. For instance, if 
	       SPLICING_THRESHOLD is 2, we get the first 2 nucleotides of the following intron*/
	    return true;
	} else if (k == kgl.getExonCount()-1 && start >= exonstart - SPLICING_THRESHOLD 
		   && start < exonstart) {
	    /* In annovar, we had start <= exonstart + SPLICING_THRESHOLD -1 instead of start < exonstart */
	    /* variation is located right before (5' to) the last exon, +/- SPLICING_THRESHOLD
	       nucleotides of the exon/intron boundary */
	    return true;
	} else if (k>0 && k < kgl.getExonCount()-1) {
	    /* interior exon */
	    if (start >= exonstart -SPLICING_THRESHOLD && start < exonstart )
		/* variation is located at 5' end of exon in splicing region */
		return true;
	    else if (start > exonend  &&  start <= exonend + SPLICING_THRESHOLD)
		/* variation is located at 3' end of exon in splicing region */
		return true;
	}
	/* Now repeat the above calculations for "end", the end position of the variation.
	* TODO: in many cases, start==end, this calculation is then superfluous. Refactor.*/
	if (k==0 && end > exonend && end <= exonend+SPLICING_THRESHOLD) {
	    /* variation is located right after (3' to) first exon. For instance, if 
	       SPLICING_THRESHOLD is 2, we get the first 2 nucleotides of the following intron*/
	    return true;
	} else if (k == kgl.getExonCount()-1 
		   && end >= exonstart - SPLICING_THRESHOLD 
		   && end < exonstart) {
	    /* variation is located right before (5' to) the last exon, +/- SPLICING_THRESHOLD
	       nucleotides of the exon/intron boundary */
	    return true;
	} else if (k>0 && k < kgl.getExonCount()-1) {
	    /* interior exon */
	    if (end >= exonstart -SPLICING_THRESHOLD && end < exonstart)
		/* variation is located at 5' end of exon in splicing region */
		return true;
	    else if (end > exonend  &&  end <= exonend + SPLICING_THRESHOLD)
		/* variation is located at 3' end of exon in splicing region */
		return true;
	}
	/* Check whether start/end are different and overlap with splice region. */
	if (k==0 && start <= exonend && end > exonend) {
	    /* first exon, start is 5' to exon/intron boundry and end is 3' to boundary */
	    return true;
	} else if (k == kgl.getExonCount()-1 && start < exonstart && end >= exonstart) {
	    /* last exon, start is 5' to exon/intron boundry and end is 3' to boundary */
	    return true;
	} else if (k>0 && k < kgl.getExonCount() -1) {
	     /* interior exon */
	    if (start < exonstart && end >= exonstart) {
		/* variant overlaps 5' exon/intron boundary */
		return true;
	    } else if (start <= exonend && end > exonend) {
		/* variant overlaps 3' exon/intron boundary */
		return true;
	    }
	}
	return false; /* This variant does not lead to a splicing mutation */
    }



     /**
     * Determine if the variant under consideration  (which the calling
     * code has determined to be on the plus strand) represents a splice variant, defined as 
     * being in the SPLICING_THRESHOLD nucleotides within the exon/intron boundry. If so,
     * return true, otherwise, return false.
     * @param k Exon number in gene represented by kgl
     * @param kgl Gene to be checked for splice mutation for current chromosomal variant.
     */
    public static boolean isSpliceVariantMinusStrand(KnownGene kgl, int start, int end, String ref, String alt, int k) {
	if (kgl.getExonCount() == 1) return false; /* Single-exon genes do not have introns: if (@exonstart != 1) */
	int exonend = kgl.getExonEnd(k);
	int exonstart = kgl.getExonStart(k);
	int exoncount = kgl.getExonCount();

	//The following checks if start is within 2 nucleotides
	// of appropriate exon boundaries to be a splice mutation.

	if (k==0 /* This is the last exon of a gene on the minus strand */
	    /* the following two lines give us the last two bp of the last intron and the
	     first two bp of the last exon*/
	    && start >exonend   /* was start >= exonend - SPLICING_THRESHOLD +1 in annovar */
	    && start <= exonend + SPLICING_THRESHOLD) {
	    return true;
	} else if (k==(exoncount-1)  /* first exon of gene on minus strand */
		   && start >= exonstart - SPLICING_THRESHOLD
		   && start < exonstart) { /* was start <= exonstart + SPLICING_THRESHOLD-1 in annovar */
	    return true;
	} else if (k>0 && k<(exoncount -1)) {  /* internal exon */
	    if (start >= exonstart - SPLICING_THRESHOLD
		&& start < exonstart ) { /* splice donor, minus strand */
		return true;
	    }	
	    if (start > exonend 
		&& start <= exonend + SPLICING_THRESHOLD) /* splice acceptor, minus strand */
		return true;
	}
	
	if (k==0 /* last exon of gene on minus strand */
	    && end > exonend
	    && end <= exonend + SPLICING_THRESHOLD) {
	    return true;
	} else if (k==(exoncount-1)
		   && end >= exonstart-SPLICING_THRESHOLD
		   && end < exonstart) {
	    return true;
	} else if (k>0 && k<(exoncount - 1)) { /* internal exon */
	    if (end >= exonstart - SPLICING_THRESHOLD
		&& end < exonstart) {
		return true;
	    }
	    if (end > exonend 
		&& end <= exonend + SPLICING_THRESHOLD) {
		return true;
	    }
	}

	/* overlap with splice sequence at exon/intron boundary */
	if (k==0 && start <= exonend && end > exonend) {
	    /* last exon on minus strand. start is within exon and end is in intron */
	    return true;
	} else if (k == (exoncount-1) && start < exonstart && end >= exonstart) {
	    /* first exon for minuis-strand gene, start is within intron and end is in exon */
	    return true;
	} else if (k>0 && k < (exoncount -1)) {
	    /* "inner exon" */
	    if  (start < exonstart && end >= exonstart) {
		return true;
	    }
	    if (start <= exonend && end > exonend) {
		  System.out.println("LOGICAL ERROR HERE; FIX ME HERE D: " + start + " exonstart=" + exonstart);
		return true;
	    }
	}
	return false; /* This variant does not lead to a splicing mutation */
    }



  
       /**
     * Return an annotation for a splice mutation for a gene on the plus strand.
     * <P> The logic is that if a mutation is at position IVSN-1 or IVSN-2 (where N is the
     * number of the following exon (k+1 in the code below, since k is zero-based), then
     * the mutation can be right in front of the exon or right after the exon
     * <OL>
     * <LI>Acceptor mutation: recognize this because the position of the mutation is within one or two 
     * (SPLICING_THRESHOLD) nucleotides of the start of the exon.  We then have c.XYZ-1G>T, or something
     * similar, where XYZ is the first position of the exon; this is calculated by 
     * {@code cumlenexon -= (exonend - exonstart);} (think about it!). The position of the
     * mutation (-1 or -2) is calculated by {@code exonstart-start}.
     * <LI>Donor mutation. This is something like c.XYZ+2T>C, where XYZ is the last position
     * of the exon, which is simply {@code cumlenexon}. The position of the mutation (+1 or +2)
     * is calculated by start-exonend.
     * </OL>
     * At the moment, no specific annotation is made for other kinds of splice mutation (e.g., a
     * deletion of several base pairs surrounding a splice site. For now, we will just use the gene
     * symbol as the annotation together with the variant type SPLICING. This can be improved in the future.
     * <P>
     *  I am unsure what this comment in annovar is supposed to mean:
     * if name2 is already a splicing variant, but its detailed annotation 
     * (like c150-2A>G) is not available, 
     * and if this splicing leads to amino acid change (rather than UTR change)
     * @param kgl Gene with splice mutation for current chromosomal variant.
     * @param start start position of the variant
     * @param  end position of the variant
     * @param ref reference sequence
     * @param alt variant sequence
     * @param k number (zero-based) of the affected exon.
     * @param cumlenexon cumulative length up the end of exon k
     * @return An {@link exomizer.reference.Annotation Annotation} object corresponding to the splice mutation.
     */
    public static Annotation getSpliceAnnotationPlusStrand(KnownGene kgl, int start, int end, String ref, String alt, int k, int cumlenexon) {
	int cdsstart = kgl.getCDSStart();
	if (start == end && start >= cdsstart) { /* single-nucleotide variant */
	    int exonend = kgl.getExonEnd(k);
	    int exonstart = kgl.getExonStart(k);
	    if (start >= exonstart -SPLICING_THRESHOLD  && start < exonstart) {
		/*  #------*-<---->------- mutation located right in front of exon */
		cumlenexon -= (exonend - exonstart);
		/*  Above, we had $lenexon += ($exonend[$k]-$exonstart[$k]+1); take back but for 1.*/
		String anno = String.format("%s:exon%d:c.%d-%d%s>%s",kgl.getName(),
					    k+1,cumlenexon,exonstart-start,ref,alt);
		int refvarstart = cumlenexon; /* position of mutation in CDS */
		Annotation ann = Annotation.createSplicingAnnotation(kgl,refvarstart,anno);
		return ann;
		/* Annovar:$splicing_anno{$name2} .= "$name:exon${\($k+1)}:c.$lenexon-" . ($exonstart[$k]-$start) . "$ref>$obs,"; */
	    } else if (start > exonend && start <= exonend + SPLICING_THRESHOLD)  {
		/* #-------<---->-*--------<-->-- mutation right after exon end */
		String anno = String.format("%s:exon%d:c.%d+%d%s>%s",kgl.getName(),
					    k+1,cumlenexon,start-exonend,ref,alt);
		//$splicing_anno{$name2} .= "$name:exon${\($k+1)}:c.$lenexon+" . ($start-$exonend[$k]) . "$ref>$obs,";
		int refvarstart = cumlenexon;
		Annotation ann = Annotation.createSplicingAnnotation(kgl,refvarstart,anno);
		return ann;
	    } 
	} 
	/* If we get here, the is a complicated splice mutation not covered by the above cases.*/
	String annot = String.format("%s:exon%d:complicated splice mutation",kgl.getName(),k+1);
	Annotation ann = Annotation.createSplicingAnnotation(kgl, 0,annot);
	return ann;
	
    }

    /**
     * Write a splice annotation for a gene on the minus strand. When we get here, the calling code
     * has already checked that the mutation is in the 2 nucleotides before or after the 
     * exon/intron boundary.
     * @param kgl Gene with splice mutation for current chromosomal variant.
     * @param start start position of the variant
     * @param  end position of the variant
     * @param ref reference sequence
     * @param alt variant sequence
     * @param k number (zero-based) of the affected exon.
     * @param cumlenexon cumulative length up the end of exon k
     * @return An {@link exomizer.reference.Annotation Annotation} object corresponding to the splice mutation.
     */
    public static Annotation getSpliceAnnotationMinusStrand(KnownGene kgl, int start, int end, String ref, String alt, int k, int cumlenexon) {
	int cdsend = kgl.getCDSEnd();
	int exonend = kgl.getExonEnd(k);
	int exonstart = kgl.getExonStart(k);
	int exoncount = kgl.getExonCount();
	// if ($splicing{$name2} and $start==$end and $start<=$cdsend) {
	if (start == end && start <= cdsend) { /* single nucleotide splice variant */
	    if (start >= exonstart-SPLICING_THRESHOLD && start < exonstart) {
		//------*-<---->---------<-->-------<------>----
		String anno = String.format("%s:exon%d:c.%d+%d%s>%s",kgl.getName(),
					    (exoncount-k+1),cumlenexon,exonstart-start,revcom(ref),revcom(alt));
		//$splicing_anno{$name2} .= "$name:exon${\(@exonstart-$k+1)}:c.$lenexon+" . 
		// ($exonstart[$k]-$start) . revcom($ref) . '>' . revcom ($obs) . ',';
		int refvarstart = cumlenexon; // position of variant in CDS, important for sorting
		Annotation ann = Annotation.createSplicingAnnotation(kgl,refvarstart,anno);
		return ann;
	    } else if (start > exonend && start <= exonend + SPLICING_THRESHOLD) {
		// -------<---->-*--------<-->-------<------>----
		cumlenexon -= (exonend-exonstart); //  $lenexon -= ($exonend[$k]-$exonstart[$k]);
		String anno = String.format("%s:exon%d:c.%d-%d%s>%s",kgl.getName(),
					    (exoncount-k+1),cumlenexon,start-exonend,revcom(ref),revcom(alt));
		//$splicing_anno{$name2} .= "$name:exon${\(@exonstart-$k+1)}:c.$lenexon-" . 
		//($start-$exonend[$k]) . revcom($ref) . '>' . revcom($obs) . ',';
		int refvarstart = cumlenexon; // position of variant in CDS, important for sorting
		Annotation ann = Annotation.createSplicingAnnotation(kgl,refvarstart,anno);
		return ann;
	    }
	}
	/* If we get here, the is a complicated splice mutation not covered by the above cases.*/
	String annot = String.format("%s:exon%d:complicated splice mutation",kgl.getName(),k+1);
	Annotation ann = Annotation.createSplicingAnnotation(kgl,0,annot);
	return ann;
    }


	/**
	 * Return the reverse complement version of a DNA string in upper case.
	 * Note that no checking is done in this code since the parse code checks
	 * for valid DNA and upper-cases the input. This code will break if these
	 * assumptions are not valid.
	 * @param sq original, upper-case cDNA string
	 * @return reverse complement version of the input string sq.
	*/
	private static String revcom(String sq) {
	    if (sq.equals("-")) return sq; /* deletion, insertion do not need rc */
	    StringBuffer sb = new StringBuffer();
	    for (int i = sq.length()-1;i>=0;i--) {
		char c = sq.charAt(i);
		char match=0;
		switch(c) {
		case 'A': match='T'; break;
		case 'C': match='G'; break;
		case 'G': match='C'; break;
		case 'T': match='A'; break;
		}
		if (match>0) sb.append(match);
	    }
	    return sb.toString();
	}
	
	

}