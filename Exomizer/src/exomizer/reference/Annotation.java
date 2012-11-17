package exomizer.reference;

import exomizer.common.Constants;
import exomizer.reference.KnownGene;

/**
 * This class encapsulates a single annotation and includes basically two pieces of information:
 * <OL>
 * <LI>The variant type: frameshift, synonymous substitution, etc
 * <LI>A string representing the actualy variant
 * </OL>
 * This class is meant to be used in place of the hash {@code %function} in Annovar. Typically,
 * variants are entered into that has with lines such as
 * <P>
 * {@code  $function->\{$index\}\{ssnv\} .= "$geneidmap->\{$seqid\}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";}
 * <P>
 * This has then stores the potentially multiple variants for any one chromosomal variant, and these are then
 * printed to the variant and exonic variant files.
 * @author Peter N Robinson
 * @version 0.01 (6 October 2012)
 */
public class Annotation implements Constants {
    /** The type of the variant being annotated, using the constants in {@link exomizer.common.Constants Constants},
	e.g., MISSENSE, 5UTR, etc. */
    private byte varType;

    private String variantType=null;
    private String variantAnnotation=null;

    public String getType() { return this.variantType; }


    public Annotation(String type, String anno) {
	this.variantType=type;
	this.variantAnnotation=anno;
	System.out.println("TYPE = " + type + " anno=" + anno);
	System.exit(1);
    }

    /** This constructor is intended to be used only by static factory methods. */
    private Annotation() {
    }


    /**
     * This factory method creates an annotation object for an intergenic variant that is located
     * between two genes and is not nearby (threshold default of 1000 nt) to either of them. For
     * example the annotation should look like
     * <PRE>HGVS=LOC100288069(dist=39337),LINC00115(dist=8181)</PRE>
     * @param leftGene Gene that is 5' to the variant
     * @param rightGene Gene that is 3' to the variant
     * @param pos position of the variant (should be the start position)
     * @return Annotation object for internenic variant
     */
    public static Annotation createIntergenicAnnotation(KnownGene leftGene, KnownGene rightGene, int pos) {
	Annotation ann = new Annotation();
	ann.varType=INTERGENIC;
	/* Note that either the leftGene or the rightGene can be null, if the variant is located
	   5' (3') to all variants on a chromosome. */
	if (leftGene == null) {
	    int distR =  rightGene.getTXStart() - pos;
	    ann.variantAnnotation = String.format("HGVS=NONE(dist=NONE),%s(dist=%d)",
						  rightGene.getName2(),distR);
	} else if (rightGene == null) {
	    int distL =  pos - leftGene.getTXEnd();
	    ann.variantAnnotation = String.format("HGVS=%s(dist=%d),NONE(dist=NONE)",
						  leftGene.getName2(),distL);

	} else {
	    int distR =  rightGene.getTXStart() - pos;
	    int distL =  pos - leftGene.getTXEnd();
	    ann.variantAnnotation = String.format("HGVS=%s(dist=%d),%s(dist=%d)",
						  leftGene.getName2(),distL,rightGene.getName2(),distR);
	}
	return ann;
    }



    public static Annotation createUpDownstreamAnnotation(KnownGene kg, int pos) {
	Annotation ann = new Annotation();
	ann.variantAnnotation = String.format("HGVS=%s", kg.getName2());
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
     */
    public static Annotation createSplicingAnnotation(String anno) {
	Annotation ann = new Annotation();
	ann.varType = SPLICING;
	ann.variantAnnotation = anno;
	return ann;
    }


    public static Annotation createUTR5Annotation(String name2) {
	Annotation ann = new Annotation();
	ann.varType = UTR5;
	ann.variantAnnotation = String.format("HGVS=%s", name2);
	return ann;

    }

    public static Annotation createUTR3Annotation(String genesymbol, String accession) {
	Annotation ann = new Annotation();
	ann.varType = UTR3;
	ann.variantAnnotation = String.format("HGVS=%s;%s", genesymbol,accession);
	return ann;

    }

     public static Annotation createIntronicAnnotation(String name2) {
	Annotation ann = new Annotation();
	ann.varType = INTRONIC;
	ann.variantAnnotation = String.format("HGVS=%s", name2);
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
     public static Annotation creatFrameshiftDeletionAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = FS_DELETION;
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

     /**
     * Use this factory method for annotations of frameshift deletion mutations.
     */
     public static Annotation createFrameshiftDelAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = FS_DELETION;
	ann.variantAnnotation = msg;
	return ann;
     }


    public static Annotation createSynonymousSNVAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = SYNONYMOUS;
	ann.variantAnnotation = msg;
	return ann;
     }

     public static Annotation createMissenseSNVAnnotation(String msg) {
	Annotation ann = new Annotation();
	ann.varType = MISSENSE;
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
     * @return type of this Annotation (one of the constants such as INTERGENIC from {@link exomizer.common.Constants Constants}).
     */
    public byte getVarType() {
	return this.varType;
    }


    public String getVariantTypeAsString() { 
	String s="";
	switch(this.varType) {
	case INTERGENIC: s="INTERGENIC";break;
	case DOWNSTREAM: s="DOWNSTREAM";break;
	case INTRONIC: s="INTRONIC";break;
	case UPSTREAM: s="UPSTREAM"; break;
	case ncRNA_EXONIC: s="ncRNA_exonic"; break;
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
    public String getVariantAnnotation() { return this.variantAnnotation; }


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
}