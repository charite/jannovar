package exomizer.reference;

import exomizer.common.Constants;
import exomizer.reference.KnownGene;

import java.util.ArrayList;


/**
 * This class collects all the information about a variant and its annotations and 
 * calculates the final annotations for a given variant. It uses the annotations
 * that were calculated for each of the genes in the vicinity of the variant and
 * decides upon the best final variant using heuristics that were adapted from the
 * annovar package. The main problems seem to arise for deciding how long to search
 * for neighboring genes and intergenic variants. For instance, a problem arose for
 * a variant that is located 5' to gene B, and then gene A has different transcripts.
 * One of the transcripts of gene A is short and thus, the variant is located 3' to
 * this transcript. On the other hand, another transcript of gene A is longer, and the
 * variant is intronic to this one. Therefore, we basically need to look at a number
 * of different annotations, and then decide what the most relevant annotations are.
 * If there is a clear exonic annotation, then usually this is OK and we can stop
 * looking further (this is done in the {@link exomizer.reference.Chromosome Chromosome}
 * class).
 * <P>
 * The default preference for annotations is thus
 * <OL>
 * <LI><B>exonic</B>: variant overlaps a coding exon (does not include 5' or 3' UTR).
 * <LI><B>splicing</B>: variant is within 2-bp of a splicing junction (same precedence as exonic).
 * <LI><B>ncRNA</B>: variant overlaps a transcript without coding annotation in the gene definition 
 * <LI><B>UTR5</B>: variant overlaps a 5' untranslated region 
 * <LI><B>UTR3</B>: variant overlaps a 3' untranslated region 
 * <LI><B>intronic</B>:	variant overlaps an intron 
 * <LI><B>upstream</B>: variant overlaps 1-kb region upstream of transcription start site
 * <LI><B>downstream</B>: variant overlaps 1-kb region downtream of transcription end site (use -neargene to change this)
 * <LI><B>intergenic</B>: variant is in intergenic region 
 * </OL>
 * One object of this class is created for each variant we want to annotate. The {@link exomizer.reference.Chromosome Chromosome}
 * class goes through a list of genes in the vicinity of the variant and adds one {@link exomizer.reference.Annotation Annotation}
 * object for each gene. These are essentially candidates for the actual correct annotation of the variant, but we can
 * only decide what the correct annotation is once we have seen enough candidates. Therefore, once we have gone
 * through the candidates, this class decides what the best annotation is and returns the corresponding 
 * {@link exomizer.reference.Annotation Annotation} object (in some cases, this class may modify the 
 {@link exomizer.reference.Annotation Annotation} object before returning it).
 * @version 0.01 November 15, 2012
 * @author Peter N Robinson
 */

public class AnnotatedVar {
   

    
    /** List of all {@link exomizer.reference.Annotation Annotation}'s found to date for the current variation. */
    private ArrayList<Annotation> annotation_list = null;
    /** Best (lowest) precedence value found to data for any annotation. */
    private int bestPrecedence = Integer.MAX_VALUE;

    private ArrayList<Annotation> annotation_Exonic =null;
    private ArrayList<Annotation> annotation_ncRNA = null;
    private ArrayList<Annotation> annotation_UTR5 = null;
    private ArrayList<Annotation> annotation_UTR3 = null;
    private ArrayList<Annotation> annotation_Intronic = null;
    private ArrayList<Annotation> annotation_Upstream = null;
    private ArrayList<Annotation> annotation_Downstream = null; 
    private ArrayList<Annotation> annotation_Intergenic = null;
    private ArrayList<Annotation> annotation_Error = null;

    private boolean hasExonic;
    private boolean hasNcRna;
    private boolean hasUTR5;
    private boolean hasUTR3;
    private boolean hasIntronic;
    private boolean hasUpstream;
    private boolean hasDownstream;
    private boolean hasIntergenic;
    private boolean hasError;
    /**
     * True if we have at least one annotation for the classes ncRNA_EXONIC
     * SPLICING, UTR5, UTR3, EXONIC, INTRONIC
     */
    private boolean hasGenicMutation;

    /** The current number of annotations for the variant being annotated */
    private int annotationCount;

    

    public AnnotatedVar(int initialCapacity) {

	this.annotation_Exonic = new ArrayList<Annotation>(initialCapacity);
	this.annotation_ncRNA =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_UTR5 =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_UTR3 = new ArrayList<Annotation>(initialCapacity);
	this.annotation_Intronic =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Upstream =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Downstream =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Intergenic =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Error =  new ArrayList<Annotation>(initialCapacity);

    }

    /**
     * This function should be called before a new variant is annotation
     * in order to clear the lists used to store Annotations.
     */
    public void clearAnnotationLists() {
	this.annotation_Exonic.clear();
	this.annotation_ncRNA .clear();
	this.annotation_UTR5.clear();
	this.annotation_UTR3.clear();
	this.annotation_Intronic.clear();
	this.annotation_Upstream.clear();
	this.annotation_Downstream.clear();
	this.annotation_Intergenic.clear();
	this.annotation_Error.clear();
	this.hasExonic=false;
	this.hasNcRna=false;
	this.hasUTR5=false;
	this.hasUTR3=false;
	this.hasIntronic=false;
	this.hasUpstream=false;
	this.hasDownstream=false;
	this.hasIntergenic=false;
	this.hasError=false;
	this.hasGenicMutation=false;
	this.annotationCount=0;
	
    }


    public int getAnnotationCount() { return this.annotationCount; }

    /**
     * @return true if there are currently no annotations. 
     */
    public boolean isEmpty() { return this.annotationCount == 0; }

   
    /**
     * True if we have at least one annotation for the classes ncRNA_EXONIC
     * SPLICING, UTR5, UTR3, EXONIC, INTRONIC
     */
    public boolean hasGenic() { return this.hasGenicMutation; }

    /**
     * Look for the best single annotation according to the precendence
     * rules. If there are multiple annotations in the same class, combine
     * them.
     */
    public ArrayList<Annotation> getAnnotationList() {
	
	if (hasExonic) {
	    return annotation_Exonic;
	} else if (hasNcRna) {
	    return annotation_ncRNA;
	} else if (hasUTR5) {
	    return annotation_UTR5;
	} else if (hasUTR3) {
	    return annotation_UTR3;
	} else if (hasIntronic) {
	    return annotation_Intronic;
	} else if (hasUpstream) {
	    return annotation_Upstream;
	} else if (hasDownstream) {
	    return annotation_Downstream;
	} else if (hasIntergenic) {
	    return annotation_Intergenic;
	} else if (hasError) {
	    return annotation_Error;
	}
	/** Should never get here */
	System.err.println("Error AnnotatedVar: Did not find any annotation");
	// TODO-- add Exception!

	return null;
    }




    public void addNonCodingExonicRnaAnnotation(Annotation ann){
	this.annotation_ncRNA.add(ann);
	this.hasNcRna=true;
	this.hasGenicMutation=true;
    }


    public void addUTR5Annotation(Annotation ann){
	this.annotation_UTR5.add(ann);
	this.hasUTR5=true;
	this.hasGenicMutation=true;
    }

    public void addUTR3Annotation(Annotation ann){
	this.annotation_UTR3.add(ann);
	this.hasUTR3=true;
	this.hasGenicMutation=true;
    }

     public void addIntergenicAnnotation(Annotation ann){
	this.annotation_Intergenic.add(ann);
	this.hasIntergenic=true;
    }


    public void addExonicAnnotation(Annotation ann){
	this.annotation_Exonic.add(ann);
	this.hasExonic=true;
	this.hasGenicMutation=true;
    }

    public void addIntronicAnnotation(Annotation ann){
	this.annotation_Intronic.add(ann);
	this.hasIntronic=true;
	this.hasGenicMutation=true;
    }

    public void addErrorAnnotation(Annotation ann){
	this.annotation_Error.add(ann);
	this.hasError=true;
     }

    public void addUpDownstreamAnnotation(Annotation ann){
	byte type = ann.getVariantType();
	if (type == exomizer.common.Constants.DOWNSTREAM) {
	    this.annotation_Downstream.add(ann);
	    this.hasDownstream=true;
	} else if (type == exomizer.common.Constants.UPSTREAM) {
	    this.annotation_Upstream.add(ann);
	    this.hasUpstream=true;
	} else {
	    System.err.println("Warning [AnnotatedVar.java]: Was expecting UPSTREAM or DOWNSTREAM" +
			       " type of variant but got " + type);
	    /* TODO -- Add Exception! */
	    System.exit(1);
	}
    }

}