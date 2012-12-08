package exomizer.reference;

import exomizer.common.Constants;
import exomizer.reference.KnownGene;
import exomizer.exception.AnnotationException;

import java.util.ArrayList;
import java.util.HashSet;

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
 * {@link exomizer.reference.Annotation Annotation} object before returning it).
 * <P>
 * For each class of Variant, there is a function that returns a single {@link exomizer.reference.Annotation Annotation} object.
 * These functions are called summarizeABC(), where ABC is Intronic, Exonic, etc., representing the precedence classes.
 * @version 0.07 December 8, 2012
 * @author Peter N Robinson
 */

public class AnnotatedVar implements Constants {
   

    
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for exonic variation. */
    private ArrayList<Annotation> annotation_Exonic =null;
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for ncRNA variation. */
    private ArrayList<Annotation> annotation_ncRNA = null;
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for UTR5 or UTR3 variation. */
    private ArrayList<Annotation> annotation_UTR = null;
     /** List of all {@link exomizer.reference.Annotation Annotation} objects found for intronic variation in
	 protein coding RNAs.. */
    private ArrayList<Annotation> annotation_Intronic = null;
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for intronic variation in ncRNAs. */
    private ArrayList<Annotation> annotation_ncrnaIntronic = null;
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for upstream variation. */
    private ArrayList<Annotation> annotation_Upstream = null;
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for downstream variation. */
    private ArrayList<Annotation> annotation_Downstream = null; 
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for intergenic variation. */
    private ArrayList<Annotation> annotation_Intergenic = null;
    /** List of all {@link exomizer.reference.Annotation Annotation} objects found for probably erroneous data. */
    private ArrayList<Annotation> annotation_Error = null;
    /** Set of all gene symbols used for the current annotation (usually one, but if the size of this set
	is greater than one, then there qare annotations to multiple genes and we will need to use
	special treatment).*/
    private HashSet<String> geneSymbolSet=null;


    /** Flag to state that we have at least one exonic variant. */
    private boolean hasExonic;
    private boolean hasNcRna;
    private boolean hasUTR5;
    private boolean hasUTR3;
    private boolean hasIntronic;
    private boolean hasNcrnaIntronic;
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
	this.hasNcrnaIntronic=false;
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
     * <P>
     * ToDo Return list with annotations for each isoform.
     */
    public ArrayList<Annotation> getAnnotationList() {
	
	if (hasExonic) {
	    return annotation_Exonic;
	} else if (hasNcRna) {
	    return annotation_ncRNA;
	} else if (hasUTR5) {
	    return annotation_UTR;
	} else if (hasUTR3) {
	    return annotation_UTR;
	} else if (hasIntronic) {
	    return annotation_Intronic;
	} else if (hasNcrnaIntronic) {
	    return annotation_ncrnaIntronic;
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

    /**
     * This function will return a single {@link exomizer.reference.Annotation Annotation} 
     * object that represents the most highly prioritized annotations. If there are multiple
     * annotations with the same priority, they will be combined into a single 
     * {@link exomizer.reference.Annotation Annotation} object that is designed to be
     * displayed.
     * @return An {@link exomizer.reference.Annotation Annotation} object with the most highly prioiritized annotations.
     */
    public Annotation getAnnotation() throws AnnotationException {
	
	if (hasExonic) {
	    return summarizeExonic(); 
	} else if (hasNcRna) {
	    return summarizeNoncodingRNA();
	} else if (hasUTR5 || hasUTR3) {
	    return summarizeUTR();
	} else if (hasIntronic) {
	    return summarizeIntronic();
	} else if (hasNcrnaIntronic) {
	    return summarizeNcRnaIntronic();
	} else if (hasUpstream) {
	    return summarizeUpstream();
	} else if (hasDownstream) {
	    return summarizeDownstream(); 
	} else if (hasIntergenic) {
	    return summarizeIntergenic();
	} else if (hasError) {
	    return summarizeError();
	}
	/** Should never get here */
	System.err.println("Error AnnotatedVar: Did not find any annotation");
	// TODO-- add Exception!

	return null;
    }

    
    private String concatenateWithSemicolon(ArrayList<String> lst) {
	StringBuilder sb = new StringBuilder();
	sb.append(lst.get(0));
	for (int i=1;i<lst.size();++i) {
	    sb.append(";" + lst.get(i));
	}
	return sb.toString();
    }

     private String concatenateWithComma(ArrayList<String> lst) {
	StringBuilder sb = new StringBuilder();
	sb.append(lst.get(0));
	for (int i=1;i<lst.size();++i) {
	    sb.append("," + lst.get(i));
	}
	return sb.toString();
    }

   

    /**
     * This function will combine multiple intronic
     * annotations, e.g., "TRIM22,TRIM5" for a variant
     * that is located in the intron of these two different
     * genes. */
    private Annotation summarizeIntronic() throws AnnotationException {
	if (this.annotation_Intronic.size() == 0) {
	    throw new AnnotationException("No data for intronic annotation");
	} else if (annotation_Intronic.size()==1) {
	    return annotation_Intronic.get(0);
	} else {
	    ArrayList<String> symbol_list = new ArrayList<String>();
	    for (Annotation a : annotation_Intronic) {
		String s = a.getVariantAnnotation();
		symbol_list.add(s);
	    }
	    java.util.Collections.sort(symbol_list);
	    String s = symbol_list.get(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(s);
	    for (int i=1;i<symbol_list.size();++i) {
		sb.append("," + symbol_list.get(i));
	    }
	    Annotation ann = Annotation.createIntronicAnnotation(sb.toString());
	    return ann;
	}
    }


      /**
     * This function will combine multiple UTR3
     * annotations*/
    private Annotation summarizeUTR() throws AnnotationException {
	byte type;
	if (hasUTR5 && ! hasUTR3)
	    type = UTR5;
	else if (hasUTR3 && ! hasUTR5)
	    type =  UTR3;
	else 
	    type = UTR53; /* combination */
		
	ArrayList<String> symbol_list = new ArrayList<String>();
	HashSet<String> seen = new HashSet<String>();
	for (Annotation a : annotation_UTR) {
	    String s = a.getVariantAnnotation();
	    if (seen.contains(s)) continue;
	    seen.add(s);
	    symbol_list.add(s);
	}
	java.util.Collections.sort(symbol_list);
	String s = symbol_list.get(0);
	StringBuilder sb = new StringBuilder();
	sb.append(s);
	for (int i=1;i<symbol_list.size();++i) {
	    sb.append("," + symbol_list.get(i));
	}
	Annotation ann = Annotation.createSummaryAnnotation(sb.toString(),type);
	return ann;



    }






    /**
     * Checks if there is a 5' or 3' UTR annotation for the
     * same gene
     * @param sym The Gene Symbol of the gene we are searching for
     * @return true if there is a 3' annotation for the gene denoted by {@code sym}.
     */
    private boolean exists_UTRAnnotation(String sym) {
	for (Annotation ann : this.annotation_UTR) {
	    if (sym.equals(ann.getGeneSymbol()))
		return true;
	}
	return false;
    }


  


    /**
     * This function checks whether there exist UTR mutations from
     * coding genes from different transcripts of genes with
     * ncRNA annotations. If so, it returns the ITR annotations, this
     * is the default behaviour of annovar.
     */
    private Annotation  summarizeNoncodingRNA() throws AnnotationException {
	HashSet<String> symbols = new HashSet<String>();
	for (Annotation a : this.annotation_ncRNA) {
	    String symbol = a.getGeneSymbol();
	    if (exists_UTRAnnotation(symbol))
		return summarizeUTR();
	}
	/* If we get here, there were no UTR mutations for the same gene */	

	if (this.annotation_ncRNA.size() == 0) {
	    throw new AnnotationException("No data for ncRNA annotation");
	} else if (annotation_ncRNA.size()==1) {
	    return annotation_ncRNA.get(0);
	} else {
	    ArrayList<String> symbol_list = new ArrayList<String>();
	    HashSet<String> seen = new HashSet<String>();
	    for (Annotation a : annotation_ncRNA) {
		String s = a.getVariantAnnotation();
		if (seen.contains(s)) continue;
		seen.add(s);
		symbol_list.add(s);
	    }
	    java.util.Collections.sort(symbol_list);
	    String s = symbol_list.get(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(s);
	    for (int i=1;i<symbol_list.size();++i) {
		sb.append("," + symbol_list.get(i));
	    }
	    Annotation ann = Annotation.createSummaryAnnotation(sb.toString(),ncRNA_EXONIC);
	    return ann;
	}
    }



    
    /**
     * This function will combine multiple ncRNA intronic
     * annotations, e.g., "AK126491,LOC100132987" for a variant
     * that is located in the intron of these two different
     * ncRNA genes. */
    private Annotation summarizeNcRnaIntronic() throws AnnotationException {
	if (this.annotation_ncrnaIntronic.size() == 0) {
	    throw new AnnotationException("No data for ncRNA intronic annotation");
	} else if (annotation_ncrnaIntronic.size()==1)
	    return annotation_ncrnaIntronic.get(0);
	else {
	    Annotation ann = annotation_ncrnaIntronic.get(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(ann.getVariantAnnotation());
	    for (int i=1;i<annotation_ncrnaIntronic.size();++i) {
		sb.append(";" + annotation_ncrnaIntronic.get(i).getVariantAnnotation());
	    }
	    return ann;
	}
    }


    public void addNonCodingExonicRnaAnnotation(Annotation ann){
	this.annotation_ncRNA.add(ann);
	this.hasNcRna=true;
	this.hasGenicMutation=true;
	this.annotationCount++;
    }


    public void addUTR5Annotation(Annotation ann){
	this.annotation_UTR.add(ann);
	this.hasUTR5=true;
	this.hasGenicMutation=true;
	this.annotationCount++;
    }

    public void addUTR3Annotation(Annotation ann){
	this.annotation_UTR.add(ann);
	this.hasUTR3=true;
	this.hasGenicMutation=true;
	this.annotationCount++;
    }

    /**
     * This function is used to register an Annotation for
     * a variant that is located between two genes. From the program
     * logic, only one such Annotation should be added per variant.
     * <P>
     * @param ann An Annotation with type INTERGENIC
     */
     public void addIntergenicAnnotation(Annotation ann){
	this.annotation_Intergenic.add(ann);
	this.hasIntergenic=true;
	this.annotationCount++;
    }


     /**
     * This function is used to register an Annotation for
     * a variant that is located between two genes. From the program
     * logic, only one such Annotation should be added per variant.
     * <P>
     * @param ann An Annotation with type INTERGENIC
     */
     public void addNonCodingRNAExonicAnnotation(Annotation ann){
	this.annotation_ncRNA.add(ann);
	this.hasNcRna=true;
	this.annotationCount++;
     }

    /**
     * This method returns an Intergenic annotation. There should only
     * be one such annotation per variant.
     * @return An Annotation object of type INTERGENIC
     */
    public Annotation summarizeIntergenic() throws AnnotationException {
	if (this.annotation_Intergenic.size() == 0) {
	    throw new AnnotationException("No data for intergenic annotation");
	}
	return this.annotation_Intergenic.get(0);
    }


    public void addExonicAnnotation(Annotation ann){
	for (Annotation a: this.annotation_Exonic) {
	    if (a.equals(ann)) return;
	}
	this.annotation_Exonic.add(ann);
	this.geneSymbolSet.add(ann.getGeneSymbol());
	this.hasExonic=true;
	this.hasGenicMutation=true;
	this.annotationCount++;
    }

    /**
     * Add an annotation for a noncoding RNA transcript that is affected by
     * a splice mutation.
     */
    public void addNcRNASplicing(Annotation ann) {
	String s = String.format("%s[nc_transcript_variant]",ann.getVariantAnnotation());
	ann.setVariantAnnotation(s);
	this.annotation_ncRNA.add(ann);


    }


    /**
     * If there are multiple exonic annotations, this function
     * combines them (using a semicolon as a separator), and
     * returns a single annotation object.
     */
    public Annotation summarizeExonic() throws AnnotationException {
	if (this.annotation_Exonic.size()==0)  {
	    throw new AnnotationException("No data for exonic annotation");
	} else if (this.annotation_Exonic.size()==1) {
	    Annotation ann = this.annotation_Exonic.get(0);
	    String s = String.format("%s(%s)",ann.getGeneSymbol(),ann.getVariantAnnotation());
	    ann.setVariantAnnotation(s);
	    return ann;
	} else {
	    java.util.Collections.sort(this.annotation_Exonic);
	    Annotation ann = this.annotation_Exonic.get(0);
	    StringBuilder sb = new StringBuilder();
	    String symbol = ann.getGeneSymbol();
	    if (geneSymbolSet.size()!=1) {
		StringBuilder sb2 = new StringBuilder();
		for (String s: geneSymbolSet) {
		    sb2.append(s+"/");
		}
		throw new AnnotationException("Error: Multiple gene symbols in exonic annotation: " +
					      symbol + " and " + sb2.toString());
	    }
	    sb.append(symbol + "(");
	    sb.append( ann.getVariantAnnotation());
	    for (int j=1;j<this.annotation_Exonic.size();++j) {
		ann  = this.annotation_Exonic.get(j);
		sb.append("," + ann.getVariantAnnotation());
	    }
	    sb.append(")");
	    ann.setVariantAnnotation(sb.toString());
	    return ann;
	}
    }


    /**
     * Adds an annotation for an intronic variant. Note that if the
     * same intronic annotation already exists, nothing is done, i.e.,
     * this method avoids duplicate annotations. 
     * <P>
     * Note that if multiple annotations are added for the same gene, and
     * one annotation is INTRONIC and the other is ncRNA_INTRONIC,
     * then we only store the INTRONIC annotation.
     */
    public void addIntronicAnnotation(Annotation ann){
	if (ann.getVarType() == INTRONIC) {
	    for (Annotation a: this.annotation_Intronic) {
		if (a.equals(ann)) return; /* already have identical annotation */
	    }
	    this.annotation_Intronic.add(ann);
	    this.hasIntronic=true;
	} else if (ann.getVarType() == ncRNA_INTRONIC) {
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
     * @return A single Annotation object representing one or more errors.
     */
    public Annotation summarizeError() throws AnnotationException {
	if (this.annotation_Error.size()==0)
	    throw new AnnotationException("No data for error annotation");
	else if (this.annotation_Error.size()==1)
	    return this.annotation_Error.get(0);
	else {
	    StringBuilder sb = new StringBuilder();
	    Annotation ann = this.annotation_Error.get(0);
	    sb.append(ann.getVariantAnnotation());
	    for (int j=1;j<this.annotation_Error.size();++j) {
		sb.append(";" + this.annotation_Error.get(j).getVariantAnnotation());
	    }
	    ann.setVariantAnnotation(sb.toString());
	    return ann;
	}
    }

    

    /**
     * Adds an annotation for an upstream or downstream variant. Note
     * that currently, we add only one such annotation for each gene, that is,
     * we do not add a separate annotation for each isoform of a gene. This
     * method avaoid such duplicate annotations. 
     * @param ann The annotation that is to be added to the list of annotations for the current sequence variant.
     */
    public void addUpDownstreamAnnotation(Annotation ann){
	byte type = ann.getVariantType();
	if (type == exomizer.common.Constants.DOWNSTREAM) {
	    for (Annotation a: annotation_Downstream) {
		if (a.equals(ann)) return;
	    }
	    this.annotation_Downstream.add(ann);
	    this.hasDownstream=true;
	} else if (type == exomizer.common.Constants.UPSTREAM) {
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
     * This method returns a single annotation object for upstream variants.
     * According to the logic of the rest of the code, there should be one
     * and exactly one upstream annotation if this method gets called, but
     * a check for empty annotations is put in, and multiple upstream annotations
     * get combined into one Annotation object before returning them.
     */
    public Annotation summarizeUpstream() throws AnnotationException {
	if (this.annotation_Upstream.size()==0)  {
	    throw new AnnotationException("No data for upstream annotation");
	} else if (this.annotation_Upstream.size()==1) {
	    return this.annotation_Upstream.get(0);
	} else {
	    java.util.Collections.sort(this.annotation_Upstream);
	    Annotation ann = this.annotation_Upstream.get(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append( ann.getVariantAnnotation());
	    for (int j=1;j<this.annotation_Upstream.size();++j) {
		ann  = this.annotation_Upstream.get(j);
		sb.append("," + ann.getVariantAnnotation());
	    }
	    ann.setVariantAnnotation(sb.toString());
	    return ann;
	}
    }

    /**
     * This method returns a single annotation object for downstream variants.
     * According to the logic of the rest of the code, there should be one
     * and exactly one upstream annotation if this method gets called, but
     * a check for empty annotations is put in, and multiple downstream annotations
     * get combined into one Annotation object before returning them.
     */
    public Annotation summarizeDownstream() throws AnnotationException {
	if (this.annotation_Downstream.size()==0)  {
	    throw new AnnotationException("No data for downstream annotation");
	} else if (this.annotation_Downstream.size()==1) {
	    return this.annotation_Downstream.get(0);
	} else {
	    java.util.Collections.sort(this.annotation_Downstream);
	    Annotation ann = this.annotation_Downstream.get(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append( ann.getVariantAnnotation());
	    for (int j=1;j<this.annotation_Downstream.size();++j) {
		ann  = this.annotation_Downstream.get(j);
		sb.append("," + ann.getVariantAnnotation());
	    }
	    ann.setVariantAnnotation(sb.toString());
	    return ann;
	}
    }

    /**
     * Print out all annotations we have for debugging purposes (before summarization)
     */
    public void debugPrint() {
	System.out.println("AnnotatedVar.java:debugPrint");
	System.out.println("Total annotiation: " + annotationCount);
	for (Annotation a : annotation_Exonic) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_Exonic) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
	}
	for (Annotation a : annotation_ncRNA) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] " + a.getGeneSymbol() + " -> " + a.getVariantAnnotation());
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
