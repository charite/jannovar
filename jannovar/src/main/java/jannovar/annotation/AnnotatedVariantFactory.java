package jannovar.annotation;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class collects all the information about a variant and its annotations and 
 * calculates the final annotations for a given variant. The 
 * {@link jannovar.reference.Chromosome Chromosome} objects each use an instance of
 * this class to assemble a list of {@link jannovar.annotation.Annotation Annotation} objects
 * for each {@link jannovar.exome.Variant Variant}. Each  {@link jannovar.exome.Variant Variant} should
 * receive at least one {@link jannovar.annotation.Annotation Annotation}, but variants that affect
 * multiple transcripts will have multiple annotations. 
 * <P>
 * This class creates one {@link jannovar.annotation.AnnotationList AnnotationList} object for each
 * variant, that can return both an ArrayList of annotations, a string containing all the variants
 * as well as an HTML unordered list that can be used for displaying the variants.
 * <P>
 * The default preference for annotations is thus
 * <OL>
 * <LI><B>exonic</B>: variant overlaps a coding exon (does not include 5' or 3' UTR, and also does not include synonymous).
 * <LI><B>splicing</B>: variant is within 2-bp of a splicing junction (same precedence as exonic).
 * <LI><B>ncRNA</B>: variant overlaps a transcript without coding annotation in the gene definition 
 * <LI><B>UTR5</B>: variant overlaps a 5' untranslated region 
 * <LI><B>UTR3</B>: variant overlaps a 3' untranslated region 
 * <LI><B>synonymous</B> synonymous substitution
 * <LI><B>intronic</B>:	variant overlaps an intron 
 * <LI><B>upstream</B>: variant overlaps 1-kb region upstream of transcription start site
 * <LI><B>downstream</B>: variant overlaps 1-kb region downtream of transcription end site (use -neargene to change this)
 * <LI><B>intergenic</B>: variant is in intergenic region 
 * </OL>
 * Note that the class of <B>exonic</B> and <B>splicing</B> mutations as defined here comprises the class of "obvious candidates"
 * for pathogenic mutations, i.e., NS/SS/I, nonsynonymous, splice site, indel.
 * <P>
 * One object of this class is created for each variant we want to annotate. The {@link jannovar.reference.Chromosome Chromosome}
 * class goes through a list of genes in the vicinity of the variant and adds one {@link jannovar.annotation.Annotation Annotation}
 * object for each gene. These are essentially candidates for the actual correct annotation of the variant, but we can
 * only decide what the correct annotation is once we have seen enough candidates. Therefore, once we have gone
 * through the candidates, this class decides what the best annotation is and returns the corresponding 
 * {@link jannovar.annotation.Annotation Annotation} object (in some cases, this class may modify the 
 * {@link jannovar.annotation.Annotation Annotation} object before returning it).
 * <P>
 * For each class of Variant, there is a function that returns a single {@link jannovar.annotation.Annotation Annotation} object.
 * These functions are called summarizeABC(), where ABC is Intronic, Exonic, etc., representing the precedence classes.
 * @version 0.15 (April 28, 2013)
 * @author Peter N Robinson
 */

public class AnnotatedVariantFactory implements Constants {
   
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for exonic variation. */
    private ArrayList<Annotation> annotation_Exonic =null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for ncRNA variation. */
    private ArrayList<Annotation> annotation_ncRNA = null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for UTR5 or UTR3 variation. */
    private ArrayList<Annotation> annotation_UTR = null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for synonymous variation. */
    private ArrayList<Annotation> annotation_Synonymous = null;
     /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for intronic variation in
	 protein coding RNAs.. */
    private ArrayList<Annotation> annotation_Intronic = null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for intronic variation in ncRNAs. */
    private ArrayList<Annotation> annotation_ncrnaIntronic = null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for upstream variation. */
    private ArrayList<Annotation> annotation_Upstream = null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for downstream variation. */
    private ArrayList<Annotation> annotation_Downstream = null; 
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for intergenic variation. */
    private ArrayList<Annotation> annotation_Intergenic = null;
    /** List of all {@link jannovar.annotation.Annotation Annotation} objects found for probably erroneous data. */
    private ArrayList<Annotation> annotation_Error = null;
    /** Set of all gene symbols used for the current annotation (usually one, but if the size of this set
	is greater than one, then there qare annotations to multiple genes and we will need to use
	special treatment).*/
    private HashSet<String> geneSymbolSet=null;


    /** Flag to state that we have at least one exonic variant. */
    private boolean hasExonic;
    /** Flag to state we have at least one splicing variant  */
    private boolean hasSplicing;
    /** Flag to state that we have at least one noncoding RNA variant. */
    private boolean hasNcRna;
    /** Flag to state that we have at least one UTR5 variant. */
    private boolean hasUTR5;
    /** Flag to state that we have at least one UTR3 variant. */
    private boolean hasUTR3;
    /** Flag to state that we have at least one nonsynonymous exonic variant. */
    private boolean hasSynonymous;
    /** Flag to state that we have at least one intronic variant. */
    private boolean hasIntronic;
    /** Flag to state that we have at least one noncoding RNA intronic variant. */
    private boolean hasNcrnaIntronic;
    /** Flag to state that we have at least one upstream variant. */
    private boolean hasUpstream;
    /** Flag to state that we have at least one downstream variant. */
    private boolean hasDownstream;
    /** Flag to state that we have at least one intergenic variant. */
    private boolean hasIntergenic;
    /** Flag to state that we have at least one error annotation. */
    private boolean hasError;
    /**
     * True if we have at least one annotation for the classes ncRNA_EXONIC
     * SPLICING, UTR5, UTR3, EXONIC, INTRONIC
     */
    private boolean hasGenicMutation;

    /** The current number of annotations for the variant being annotated */
    private int annotationCount;

    

    public AnnotatedVariantFactory(int initialCapacity) {

	this.annotation_Exonic = new ArrayList<Annotation>(initialCapacity);
	this.annotation_Synonymous = new ArrayList<Annotation>(initialCapacity);
	this.annotation_ncRNA =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_ncrnaIntronic = new ArrayList<Annotation>(initialCapacity);
	this.annotation_UTR =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Intronic =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Upstream =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Downstream =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Intergenic =  new ArrayList<Annotation>(initialCapacity);
	this.annotation_Error =  new ArrayList<Annotation>(initialCapacity);
	this.geneSymbolSet = new HashSet<String>();
    }

    /**
     * This function should be called before a new variant is annotation
     * in order to clear the lists used to store Annotations.
     */
    public void clearAnnotationLists() {
	this.annotation_Exonic.clear();
	this.annotation_Synonymous.clear();
	this.annotation_ncRNA.clear();
	this.annotation_ncrnaIntronic.clear();
	this.annotation_UTR.clear();
	this.annotation_Intronic.clear();
	this.annotation_Upstream.clear();
	this.annotation_Downstream.clear();
	this.annotation_Intergenic.clear();
	this.annotation_Error.clear();
	this.geneSymbolSet.clear();
	this.hasExonic=false;
	this.hasNcRna=false;
	this.hasUTR5=false;
	this.hasUTR3=false;
	this.hasIntronic=false;
	this.hasSynonymous=false;
	this.hasNcrnaIntronic=false;
	this.hasUpstream=false;
	this.hasDownstream=false;
	this.hasIntergenic=false;
	this.hasError=false;
	this.hasGenicMutation=false;
	this.annotationCount=0;
	
    }

    /**
     * @return The number of {@link jannovar.annotation.Annotation Annotation} 
     * objects for the current variant.
     */
    public int getAnnotationCount() { return this.annotationCount; }

    /**
     * Note that this function is used by {@link jannovar.reference.Chromosome Chromosome}
     * during the construction of an {@link jannovar.annotation.AnnotationList AnnotationList}
     * for a given {@link jannovar.exome.Variant Variant}.
     * @return true if there are currently no annotations. 
     */
    public boolean isEmpty() { return this.annotationCount == 0; }

    /**
     * @return true if there is a nonsynonymous, splice site, or insertion/deletion variant
     */
    public boolean isNS_SS_I() { return hasExonic || hasSplicing; }

    /**
     * True if we have at least one annotation for the classes ncRNA_EXONIC
     * SPLICING, UTR5, UTR3, EXONIC, INTRONIC
     */
    public boolean hasGenic() { return this.hasGenicMutation; }

    /**
     * After the {@link jannovar.reference.Chromosome Chromosome} object
     * has added annotations for all of the transcripts that intersect with 
     * the current variant (or a DOWNSTREAM, UPSTREAM, or INTERGENIC annotation
     * if the variant does not intersect with any transcript), it calls
     * this function to return the list of annotations in form of an
     * {@link jannovar.annotation.AnnotationList AnnotationList} object.
     * <P>
     * The strategy is to return all variants that affect coding exons (and only these)
     * if such variants exist, as they are the best candidates. Otherwise, return 
     * all variants that affect other exonic sequences (UTRs, ncRNA). Otherwise,
     * return UPSTREAM and DOWNSTREAM annotations if they exist. Otherwise, return
     * an intergenic Annotation.
     */
    public AnnotationList getAnnotationList() throws AnnotationException {
	AnnotationList annL = null;
	if (hasExonic) {
	    annL = new AnnotationList(annotation_Exonic);
	} else if (hasNcRna || hasUTR5 || hasUTR3 || hasIntronic || hasNcrnaIntronic || hasSynonymous ) {
	    annL = new AnnotationList();
	    if (annotation_ncRNA.size()>0)
		annL.addAnnotations(annotation_ncRNA);
	    if ( annotation_UTR.size()>0)
		annL.addAnnotations(annotation_UTR);
	    if (annotation_Intronic.size()>0)
		annL.addAnnotations(annotation_Intronic);
	    if (annotation_ncrnaIntronic.size()>0)
		annL.addAnnotations(annotation_ncrnaIntronic);
	    if (annotation_Synonymous.size()>0)
		annL.addAnnotations(annotation_Synonymous);
	} else if (hasUpstream || hasDownstream ) {
	    annL = new AnnotationList();
	    if (annotation_Upstream.size()>0)
		annL.addAnnotations(annotation_Upstream);
	    if (annotation_Downstream.size()>0)
		annL.addAnnotations(annotation_Downstream);
	} else if (hasIntergenic) {
	    annL = new AnnotationList(annotation_Intergenic);
	} else if (hasError) {
	    annL = new AnnotationList( annotation_Error );
	} else {
	    /** Should never get here */
	    throw new AnnotationException("Error [AnnotatedVar.java] No annotation found!");
	}
	if (this.geneSymbolSet.size() > 1) {
	    annL.setHasMultipleGeneSymbols();
	}
	annL.sortAnnotations();
	VariantType vt = getMostPathogenicVariantType();
	annL.setMostPathogenicVariantType(vt);
	return annL;
    }

    
     
    /**
     * This function goes through all of the Annotations that have been 
     * entered for the current variant and enters the type of 
     * variant that is deemed to be the most pathogenic. The function
     * follows the priority as set out by annovar.
     * <P>
     * The strategy of the function is to start out with the least
     * pathogenic type (INTERGENIC), and to workthrough all types
     * towards the most pathogenic. After this is finished, the variant
     * type with the most pathogenic annotation is returned.
     * <P>
     * There should always be at least one annotation type. If not
     * return ERROR (should never happen).
     * @return most pathogenic variant type for current variant.
     */
    private VariantType getMostPathogenicVariantType() {
	VariantType vt = null;
	if (hasIntergenic)
	    vt = VariantType.INTERGENIC;
	if (hasNcrnaIntronic)
	    vt = VariantType.ncRNA_INTRONIC;
	if (hasIntronic)
	    vt = VariantType.INTRONIC;
	if (hasUpstream)
	    vt = VariantType.UPSTREAM;
	if (hasDownstream)
	    vt = VariantType.DOWNSTREAM;
	if (hasUTR5 && ! hasUTR3)
	    vt = VariantType.UTR5;
	if (hasUTR3 && ! hasUTR5)
	    vt =  VariantType.UTR3;
	if (hasUTR3 && hasUTR5)
	    vt = VariantType.UTR53; /* combination */
	if (hasNcRna)
	    vt = VariantType.ncRNA_EXONIC;
	if (hasSynonymous)
	    vt = VariantType.SYNONYMOUS;
	if (hasExonic) {
	    /* For now, assume all exonic mutation types are the same. This
	       will affect the combined annotation, but not affecte the detailed
	       list of annotation used in exomizer. */
	    Annotation ann = 	this.annotation_Exonic.get(0);
	    vt = ann.getVariantType();
	}
	/* Finally, if we do not yet have an annotation, or if there was
	   an error someplace, set the type to ERROR. */
	if ( hasError  || vt == null )
	    vt = VariantType.ERROR;
	
	return vt;
    }

    
  


    /**
     * The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function to add a non-coding RNA exon variant.
     * From the program
     * logic, only one such Annotation should be added per variant.
     * @param ann A noncoding RNA exonic annotation object.
     */
     public void addNonCodingRNAExonicAnnotation(Annotation ann){
	this.annotation_ncRNA.add(ann);
	this.hasNcRna=true;
	this.annotationCount++;
     } 

    /**
     * The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function to add a 5' UTR  variant.
     * @param ann A 5' UTR annotation object.
     */
    public void addUTR5Annotation(Annotation ann){
	this.annotation_UTR.add(ann);
	this.hasUTR5=true;
	this.hasGenicMutation=true;
	this.annotationCount++;
    }

    /**
     * The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function to add a 3' UTR  variant.
     * @param ann A 3' UTR annotation object.
     */
    public void addUTR3Annotation(Annotation ann){
	this.annotation_UTR.add(ann);
	this.hasUTR3=true;
	this.hasGenicMutation=true;
	this.annotationCount++;
    }

    /** The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function  to register an Annotation for
     * a variant that is located between two genes. From the program
     * logic, only one such Annotation should be added per variant.
     * @param ann An Annotation with type INTERGENIC
     */
     public void addIntergenicAnnotation(Annotation ann){
	this.annotation_Intergenic.add(ann);
	this.hasIntergenic=true;
	this.annotationCount++;
    }


   

  

    /** The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function  to register an Annotation for
     * a variant that affects the coding sequence of an exon. Many different
     * variant types are summarized (NONSYNONYMOUS, DELETION etc.).
      * @param ann An Annotation to be added.
     */
    public void addExonicAnnotation(Annotation ann){
	for (Annotation a: this.annotation_Exonic) {
	    if (a.equals(ann)) return;
	}
	if (ann.getVariantType() == VariantType.SYNONYMOUS) {
	    this.annotation_Synonymous.add(ann);
	    this.hasSynonymous = true;
	} else {
	    this.annotation_Exonic.add(ann);
	    this.hasExonic=true;
	}
	this.geneSymbolSet.add(ann.getGeneSymbol());
	this.hasGenicMutation=true;
	this.annotationCount++;
    }

    /**  The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function  to register an annotation for a noncoding RNA transcript that 
     *is affected by a splice mutation.
     */
    public void addNcRNASplicing(Annotation ann) {
	String s = String.format("%s[nc_transcript_variant]",ann.getVariantAnnotation());
	ann.setVariantAnnotation(s);
	this.annotation_ncRNA.add(ann);
    }

   
    /**  The {@link jannovar.reference.Chromosome Chromosome} class calls this
     * function to add an annotation for an intronic variant. Note that if the
     * same intronic annotation already exists, nothing is done, i.e.,
     * this method avoids duplicate annotations. 
     * @param ann the Intronic annotation to be added.    
     */
    public void addIntronicAnnotation(Annotation ann){
	if (ann.getVariantType() == VariantType.INTRONIC) {
	    for (Annotation a: this.annotation_Intronic) {
		if (a.equals(ann)) return; /* already have identical annotation */
	    }
	    this.annotation_Intronic.add(ann);
	    this.hasIntronic=true;
	} else if (ann.getVariantType() == VariantType.ncRNA_INTRONIC) {
	     for (Annotation a: this.annotation_ncrnaIntronic) {
		 if (a.equals(ann)) return; /* already have identical annotation */
	     }
	     this.annotation_ncrnaIntronic.add(ann);
	     this.hasNcrnaIntronic=true;
	} 
        this.hasGenicMutation=true;
	this.annotationCount++;
    }


    


    /**
     * An error annotation is created in a few cases where there
     * data seem to be inconsistent.
     * @param ann An Annotation object that contains a String representing the error.
     */
    public void addErrorAnnotation(Annotation ann){
       	this.annotation_Error.add(ann);
	this.hasError=true;
	this.annotationCount++;
     }

   

    

    /**
     * Adds an annotation for an upstream or downstream variant. Note
     * that currently, we add only one such annotation for each gene, that is,
     * we do not add a separate annotation for each isoform of a gene. This
     * method avaoid such duplicate annotations. 
     * @param ann The annotation that is to be added to the list of annotations for the current sequence variant.
     */
    public void addUpDownstreamAnnotation(Annotation ann){
	VariantType type = ann.getVariantType();
	if (type == VariantType.DOWNSTREAM) {
	    for (Annotation a: annotation_Downstream) {
		if (a.equals(ann)) return;
	    }
	    this.annotation_Downstream.add(ann);
	    this.hasDownstream=true;
	} else if (type == VariantType.UPSTREAM) {
	    for (Annotation a: annotation_Downstream) {
		if (a.equals(ann)) return;
	    }
	    this.annotation_Upstream.add(ann);
	    this.hasUpstream=true;
	} else {
	    System.err.println("Warning [AnnotatedVar.java]: Was expecting UPSTREAM or DOWNSTREAM" +
			       " type of variant but got " + type);
	    /* TODO -- Add Exception! */
	    System.exit(1);
	}
	this.annotationCount++;
    }

   

  

    /**
     * Print out all annotations we have for debugging purposes (before summarization)
     */
    public void debugPrint() {
	System.out.println("AnnotatedVar.java:debugPrint");
	System.out.println("Total annotiation: " + annotationCount);
	for (Annotation a : annotation_Exonic) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] \"" + a.getGeneSymbol() + "\" -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Synonymous) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] \"" + a.getGeneSymbol() + "\" -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_ncRNA) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] \"" + a.getGeneSymbol() + "\" -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_UTR) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Intronic) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Upstream) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Downstream) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Intergenic) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Error) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}

    }



    
}
