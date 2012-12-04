package exomizer.reference;

import exomizer.common.Constants;
import exomizer.reference.KnownGene;

/**
 * This class encapsulates a single annotation and includes basically three pieces of information:
 * <OL>
 * <LI>The variant type: frameshift, synonymous substitution, etc
 * <LI>The gene symbol
 * <LI>A string representing the actual variant
 * </OL>
 * <P>
 * This class also includes functionality for combining the multiple
 * annotations assigned to one variant (e.g., annotations corresponding to the
 * various isoforms of one gene) by means of methods that are meant to be called
 * by the {@link exomizer.reference.AnnotatedVar AnnotatedVar} class.
 * <P>
 * @author Peter N Robinson
 * @version 0.03 (2 December 2012)
 */
public class Annotation implements Constants, Comparable<Annotation> {
    /** The type of the variant being annotated, using the constants in {@link exomizer.common.Constants Constants},
	e.g., MISSENSE, 5UTR, etc. */
    private byte varType;
    /** The position of the variant in the ORF or mRNA, if applicable. This field is used to
	sort exonic variants.*/
    private int rvarstart;

    /** The string representing the actual annotation, but
     * not including the gene symbol. For instance, for the
     * complete annotation "KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q",
     * this field would include "uc001aim.1:exon18:c.T2287C:p.X763Q"*/
    private String variantAnnotation=null;

    /** The gene symbol for this annotation. Thus, for the complete
     *  annotation "KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q", this
     *	field would contain simply "KIAA1751".
     * <P>
     * Note that for intergenic annotations, where two gene symbols are used to
     * identify the upstream and downstream genes, the symbols are stored
     * directly in the variable {@link #variantAnnotation}. Here, the field
     * is used only for annotations that can be unambiguously linked to
     * a single gene.
     */
    private String geneSymbol=null;
  
    /**
     * Return a byte constant the corresponds to the type of the variation. This will be one of the
     * constants in {@link exomizer.common.Constants Constants},
     * e.g., MISSENSE, 5UTR, etc. 
     */
    public byte getVariantType() { return this.varType; }
    /**
     * This function resets the variant type, and should only be used by the AnnotatedVar class for
     * certain cases of resolving precedence, e.g., if there is already a noncoding RNA intronic
     * annotation, and we get a new annotation for a coding isoform of the same gene. */
    public void setVarType(byte typ) { this.varType = typ; }

     /**
     * @return type of this Annotation (one of the constants such as INTERGENIC from {@link exomizer.common.Constants Constants}).
     */
    public byte getVarType() {
	return this.varType;
    }

    /**
     * @return The gene symbol (e.g., FBN1) for the gene affected by this Annotation.
     */
    public String getGeneSymbol() { return this.geneSymbol; }



    /**
     * Get annotation String. Note, does not include the gene symbol.
     * @return String with the annotation, for instance, "uc001aim.1:exon18:c.T2287C:p.X763Q"
     */
    public String getVariantAnnotation() { return this.variantAnnotation; }

    /**
     * Get full annotation with gene symbol
     */
    public String getSymbolAndAnnotation() {
	if (geneSymbol==null && variantAnnotation != null)
	    return variantAnnotation;
	return String.format("%s:%s",geneSymbol,variantAnnotation);
    }



    /**
     * Resets the annotation. This method is intended to be used by the 
     * {@link exomizer.reference.AnnotatedVar AnnotatedVar}
     * class during the process of summarizing Annotations.
     * @param s A String representing the new annotation.
     */
    public void setVariantAnnotation(String s) { this.variantAnnotation = s; }

    /**
     * Checks whether this annotation is equivalent to the
     * annotation being passed as an argument. This may be the
     * case say for two intronic annotations from different
     * isoforms of a gene where we do not care what the exact position is.
     * @param other An annotation to be checked for equivalence.
     */
    public boolean equals(Annotation other) {
	if (varType == other.varType 
	    && ( ( geneSymbol != null  && geneSymbol.equals(other.geneSymbol) )
		 || ( geneSymbol == null && other.geneSymbol == null) )
	    && variantAnnotation != null
	    && variantAnnotation.equals(other.variantAnnotation))
	    return true;
	else
	    return false;
    }


    /** The (private) constructor is intended to be used 
	only by static factory methods. */
    private Annotation() {
	this.rvarstart=0;
    }


    /**
     * This factory method creates an annotation object for an intergenic variant that is located
     * between two genes and is not nearby (threshold default of 1000 nt) to either of them. For
     * example the annotation should look like
     * <PRE>HGVS=LOC100288069(dist=39337),LINC00115(dist=8181)</PRE>
     * @param leftGene Gene that is 5' to the variant
     * @param rightGene Gene that is 3' to the variant
     * @param startpos 5' position of the variant (should be the start position)
     * @param endpos 3' position of the variant (should be the end position)
     * @return Annotation object for internenic variant
     */
    public static Annotation createIntergenicAnnotation(KnownGene leftGene, KnownGene rightGene, int startpos, int endpos) {
	Annotation ann = new Annotation();
	//System.out.println(String.format("Left:%s, right:%s, start %d end %d",leftGene.getName2(),rightGene.getName2(),startpos,endpos));
	ann.varType=INTERGENIC;
	/* Note that either the leftGene or the rightGene can be null, if the variant is located
	   5' (3') to all variants on a chromosome. */
	if (leftGene == null) {
	    int distR =  rightGene.getTXStart() - endpos;
	    ann.variantAnnotation = String.format("NONE(dist=NONE),%s(dist=%d)",
						  rightGene.getName2(),distR);
	} else if (rightGene == null) {
	    int distL =  startpos - leftGene.getTXEnd();
	    ann.variantAnnotation = String.format("%s(dist=%d),NONE(dist=NONE)",
						  leftGene.getName2(),distL);

	} else {
	    int distR =  rightGene.getTXStart() - endpos;
	    int distL =  startpos - leftGene.getTXEnd();
	    ann.variantAnnotation = String.format("%s(dist=%d),%s(dist=%d)",
						  leftGene.getName2(),distL,rightGene.getName2(),distR);
	}
	return ann;
    }

    /**
     * This method is used by {@link exomizer.reference.AnnotatedVar AnnotatedVar} to
     * create a single Downstream annotation if there are multiple different
     * annotations made
     */
    public static Annotation getSummaryDownstreamAnnotation(String name) {
	Annotation ann = new Annotation();
	ann.varType=DOWNSTREAM;
	ann.variantAnnotation = name;
	return ann;
    }

    public static Annotation createUpDownstreamAnnotation(KnownGene kg, int pos) {
	Annotation ann = new Annotation();
	if (kg == null) {
	    System.out.println("createUpDownstreamAnnotation, knownGene argument is null, pos=" + pos);
	    System.exit(1);
	}
	ann.variantAnnotation = String.format("%s", kg.getName2());
	ann.geneSymbol = kg.getName2();
	if (kg.isFivePrimeToGene(pos)) {
	    if (kg.isPlusStrand()) {
		ann.varType=UPSTREAM;
	    } else {
		ann.varType=DOWNSTREAM;
	    }
	} else if (kg.isThreePrimeToGene(pos)) {
	    if (kg.isMinusStrand()) {
		ann.varType=UPSTREAM;
	    } else {
		ann.varType=DOWNSTREAM;
	    }
	}
	return ann;
    }

    public static Annotation createNonCodingExonicRnaAnnotation(String name2) {
	Annotation ann = new Annotation();
	ann.varType = ncRNA_EXONIC;
	ann.variantAnnotation = String.format("HGVS=%s", name2);
	return ann;
    }


    /**
     * This factory method does little more than assign the annotation
     * string passed as an argument and to set the varType to SPLICING.
     * For now, it seems more convenient to calculate the annotation
     * string in client code.
     * @param kgl A knownGene in which this splicing mutation has been found
     * @param refvarstart Position of variant within transcript (used to sort)
     * @param anno A string representing the splice mutation, e.g., uc003gqp.4:exon6:c.1366-1A>T
     */
    public static Annotation createSplicingAnnotation(KnownGene kgl, int refvarstart, String anno) {
	Annotation ann = new Annotation();
	ann.varType = SPLICING;
	ann.variantAnnotation = anno;
	ann.rvarstart = refvarstart;
	ann.geneSymbol = kgl.getName2();
	return ann;
    }


    public static Annotation createUTR5Annotation(String name2,String accession) {
	Annotation ann = new Annotation();
	ann.varType = UTR5;
	ann.variantAnnotation = String.format("HGVS=%s;%s", name2,accession);
	return ann;

    }

    public static Annotation createUTR3Annotation(String genesymbol, String accession) {
	Annotation ann = new Annotation();
	ann.varType = UTR3;
	ann.variantAnnotation = String.format("HGVS=%s;%s", genesymbol,accession);
	return ann;

    }


  

    public static Annotation createNoncodingIntronicAnnotation(String name2) {
	Annotation ann = new Annotation();
	ann.varType = ncRNA_INTRONIC;
	ann.variantAnnotation = String.format("%s", name2);
	return ann;
     }

     public static Annotation createIntronicAnnotation(String name2) {
	Annotation ann = new Annotation();
	ann.varType = INTRONIC;
	ann.variantAnnotation = String.format("%s", name2);
	return ann;
     }

    /**
     * Use this factory method for annotations that probably represent database
     * errors or bugs and require manual checking.
     */
     public static Annotation createErrorAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType =  POSSIBLY_ERRONEOUS;
	ann.variantAnnotation = msg;
	return ann;
     }

     /**
     * Use this factory method for annotations of non-frameshift deletion mutations.
     */
     public static Annotation createNonFrameshiftDeletionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = NON_FS_DELETION;
	ann.variantAnnotation = msg;
	return ann;
     }

    /**
     * Use this factory method for annotations of frameshift deletion mutations.
     */
     public static Annotation createFrameshiftDeletionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = FS_DELETION;
	ann.variantAnnotation = msg;
	return ann;
     }

    /**
     * Use this factory method for annotations of non-frameshift deletion mutations.
     */
     public static Annotation createNonFrameshiftInsertionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = NON_FS_INSERTION;
	ann.variantAnnotation = msg;
	return ann;
     }

    /**
     * Use this factory method for annotations of frameshift deletion mutations.
     */
     public static Annotation creatFrameshiftInsertionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = FS_INSERTION;
	ann.variantAnnotation = msg;
	return ann;
     }

    /**
     * Use this factory method for annotations of non-frameshift deletion mutations.
     */
     public static Annotation createStopLossAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = STOPLOSS;
	ann.variantAnnotation = msg;
	return ann;
     }

     /**
     * Use this factory method for annotations of non-frameshift deletion mutations.
     */
     public static Annotation createStopGainAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = STOPGAIN;
	ann.variantAnnotation = msg;
	return ann;
     }

     


    public static Annotation createSynonymousSNVAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = SYNONYMOUS;
	ann.variantAnnotation = msg;
	return ann;
     }

    public static Annotation createMissenseSNVAnnotation(KnownGene kg,int refvarstart,String msg) {
	Annotation ann = new Annotation();
	ann.varType = MISSENSE;
	ann.geneSymbol=kg.getName2();
	ann.rvarstart = refvarstart;
	ann.variantAnnotation = msg;
	return ann;
     }


    public static Annotation createNonFrameShiftSubstitionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = NON_FS_SUBSTITUTION;
	ann.variantAnnotation = msg;
	return ann;
     }

     public static Annotation createFrameShiftSubstitionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = FS_SUBSTITUTION;
	ann.variantAnnotation = msg;
	return ann;
     }
    
   
    /**
     * @return A string representing the variant type (e.g., MISSENSE, STOPGAIN,...)
     */
    public String getVariantTypeAsString() { 
	String s="";
	switch(this.varType) {
	case INTERGENIC: s="INTERGENIC";break;
	case DOWNSTREAM: s="DOWNSTREAM";break;
	case INTRONIC: s="INTRONIC";break;
	case UPSTREAM: s="UPSTREAM"; break;
	case ncRNA_EXONIC: s="ncRNA_exonic"; break;
	case ncRNA_INTRONIC: s="ncRNA_intronic"; break;
	case SPLICING: s="SPLICING"; break;
	case STOPLOSS: s="STOPLOSS"; break;
	case STOPGAIN: s="STOPGAIN"; break;
	case SYNONYMOUS: s="SYNONYMOUS"; break;
	case MISSENSE: s="MISSENSE"; break;
	case NON_FS_SUBSTITUTION: s="NFSSUB"; break;
	case FS_SUBSTITUTION: s="FSSUB"; break;
	case FS_DELETION: s="FSDEL"; break;
	case POSSIBLY_ERRONEOUS: s="Potential database error"; break;
	case UTR5: s="UTR5"; break;
	case UTR3: s="UTR3"; break;
	default: s=String.format("NOT IMPLEMENTED YET, CHECK Annotation.java (Number:%d) annot:%s",varType,variantAnnotation);
	}
	return s;
    }


    

  


    /** Return true if this is a genic mutation
     * TODO: COmplete me!!!
     */
    public boolean isGenic() {
	switch(this.varType) {
	case ncRNA_EXONIC: return true;
	case SPLICING:return true;
	case UTR5:return true;
	case UTR3:return true;
	case EXONIC: return true;
	case INTRONIC: return true;
	default: return false;
	}
    }

    /**
     * Allows exonic mutations to be sorted according to their position
     * in the CDS. For other mutations, rvarstart is 0 and the sorting
     * has no effect.
     */
    public int compareTo(Annotation other) {
        if (rvarstart < other.rvarstart )
            return -1;
        else if (rvarstart  > other.rvarstart)
            return 1;
	else
	    return 0;
    }

}