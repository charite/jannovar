package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;


/**
 * Encapsulates a list of {@link jannovar.annotation.Annotation Annotation} objects associated 
 * with a {@link jannovar.exome.Variant Variant} and provides some access functions that
 * summarize, sort, or display the objects. Note that rarely, a variant annotation is made to more
 * than one Gene symbol. In this case, we represent the affected gene as a comma-separated list of symbols.
 * @author Peter N Robinson
 * @version 0.14 (30 June, 2013)
 */
public class AnnotationList {
    /** A list of all the {@link jannovar.annotation.Annotation Annotation} objects associated 
	with a {@link jannovar.exome.Variant Variant} object.*/
    private ArrayList<Annotation> annotationList;

    /**
     * Representative type of the  {@link jannovar.exome.Variant Variant}. If the Variant has
     * multiple {@link jannovar.annotation.Annotation Annotation} objects, then the 
     * Annotation most likely to be pathogenic is taken to be its type.
     */
    private VariantType type = null;

    /**
     * This flag is set to true for those rare variants that have 
     * annotations for multiple genes. The flag then alters the
     * behavior of the function {@link #getVariantAnnotation},
     * which produces a list of all annotations in separate parentheses
     * for each gene.
     */
    private boolean hasMultipleGeneSymbols = false;

    public AnnotationList() {
	this.annotationList = new ArrayList<Annotation>();
    }

    public AnnotationList(ArrayList<Annotation> lst) {
	this.annotationList = new ArrayList<Annotation>();
	this.annotationList.addAll(lst);
    }

    /**
     * Appends all of the  {@link jannovar.annotation.Annotation Annotation} objects
     * in {@code lst} to {@link #annotationList}.
     * @param lst Listof Annotations to be appended.
     */
    public void addAnnotations(ArrayList<Annotation> lst) {
	this.annotationList.addAll(lst);
    }

    /**
     * Get a list of all individual
     * {@link jannovar.annotation.Annotation Annotation}
     * objects.
     */
    public ArrayList<Annotation> getAnnotationList() {
	return this.annotationList;
    }
    
    /**
     * This method is called if a variant affects multiple genes (the default is
     * false and this method sets the flag {@link #hasMultipleGeneSymbols} to true.
     */
    public void setHasMultipleGeneSymbols() { this.hasMultipleGeneSymbols = true; }

     /**
     * If there are multiple annotations, this function
     * sorts them. This function also sets the overall variant type (the most
     * pathogenic single type found among all annotations).
     */
    public void  sortAnnotations() throws AnnotationException {
	if (this.annotationList.size()==0)  {
	    throw new AnnotationException("No data for annotation");
	} else {
	    java.util.Collections.sort(this.annotationList);
	}
    }


    /**
     * Get an annotation for a single transcript (this will be used to annotated VCF files).
     * Note that we will return an annotation that matches with the overall type of this
     * annotation, in case there are multiple annotations for this variant (e.g., if there
     * are nonsense and synonymous annotation, return nonsense).
     */
    public String getSingleTranscriptAnnotation() throws AnnotationException {
	/* Need an exception for UTR53, which means that we have UTR3 of one
	   annotation and UTR5 for another. */
	if (this.type == VariantType.UTR53) {
	    for (Annotation a : this.annotationList) {
		if (a.getVariantType() == VariantType.UTR3)
		    return a.getVariantAnnotation();
	    }
	}
	/* Now for the rest of the variant types. */

	for (Annotation a : this.annotationList) {
	    
	    if (a.getVariantType() == this.type) {
		/* Note that since Intronic is just the gene symbol, we return only that.
		   Otherwise, we show the gene symbol, and in parens, the annotation. */
		if (this.type == VariantType.INTRONIC)
		    return a.getVariantAnnotation();
		else
		    return a.getSymbolAndAnnotation();//getVariantAnnotation();
	    }
	}
	String e = null;
	if (this.annotationList.size()==0) {
	    e = String.format("[AnnotationList] could not retrieve matching annotation for %s",
				 this.type);
	} else {
	    Annotation ann = this.annotationList.get(0);
	    int n = this.annotationList.size();
	    e = String.format("%s (type: %s) [from a total of %d annotations]", ann.getVariantAnnotation(), this.type,n);
	}
	/*
	System.out.println("Total annotations: " + annotationList.size());
	for (Annotation a : annotationList) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] \"" + a.getGeneSymbol() + "\" -> " + a.getVariantAnnotation());
	    }*/
	throw new AnnotationException(e);

    }

    /**
     * @return an annotation consiting of the gene symbol and a list of all affected transcripts 
     * with the HGVS mutation nomenclature.
     */
    public String getVariantAnnotation() throws AnnotationException {
	if (this.type == VariantType.DOWNSTREAM || this.type == VariantType.UPSTREAM) {
	    return getUpDownstreamAnnotation();
	} else if (this.type == VariantType.INTERGENIC) {
	    return getIntergenicAnnotation();
	}  else if (this.type == VariantType.INTRONIC || this.type == VariantType.ncRNA_INTRONIC) {
	    return getIntronicAnnotation();
	} else if (this.type == VariantType.UTR3 || this.type == VariantType.UTR5 || this.type == VariantType.UTR53) {
	    return getUTRAnnotation();
	} else if (this.type == VariantType.ncRNA_EXONIC) {
	    return getNoncodingRnaAnnotation();
	} else if (this.hasMultipleGeneSymbols) {
	    return getCombinedAnnotationForVariantAffectingMultipleGenes();
	} else { /* The default gets called for everything else--EXONIC*/		
	    StringBuilder sb = new StringBuilder();
	    /* The annotation begins as (e.g.) RNF207(uc001amg.3:exon17:c.1718A>G:p.N573S...
	       If there are multiple transcript annotations they are separated by comma.
	       After the last annotation, there is a closing parenthesis. */
	    boolean needGeneSymbol = true; /* flag to show that we still need to add the gene symbol */
	    for (int j=0;j<this.annotationList.size();++j) {
		Annotation ann  = this.annotationList.get(j);
		if (! ann.isCodingExonic())
		    continue; /* this skips over intronic annotations of alternative transcripts
				 for variants that have at least one exonic annotation */
		if (needGeneSymbol) {
		      sb.append(String.format("%s(%s", ann.getGeneSymbol(), ann.getVariantAnnotation()));
		      needGeneSymbol = false;
		} else {
		    sb.append("," + ann.getVariantAnnotation());
		}
	    }
	    sb.append(")");
	    return sb.toString();
	}
    }

    /**
     * Returns the gene symbol of the annotations. If multiple genes are affected,
     * it returns the string "MultipleGenes". Probably this needs to be refactored.
     */
    public String getGeneSymbol() {
	if (this.hasMultipleGeneSymbols) {
	    return getMultipleGeneList();
	} else if (this.annotationList == null) {
	    System.err.println("error-annotationListNull");
	    System.out.println("VarType = " + type);
	} else if (this.annotationList.size()==0) {
	    System.err.println("error-annotationList-zero size");
	    System.out.println("VarType = " + type);
	} else {
	    Annotation ann = this.annotationList.get(0);
	    if (ann == null) {
		System.err.println("error-annotationObjectNull");
		System.out.println("VarType = " + type);
	    } else { 
		return ann.getGeneSymbol();
	    }
	}
	return "?";
    }

    /**
     * TODO: Some variants have multiple genes affected. For the most part,
     * this affects noncoding transcripts and not the transcripts typically interesting
     * in exome sequencing. However, we may want to refactor the interface to return
     * a list of ids in thus future.
     * @return EntrezGene id of gene affected by variant
     */
    public int getEntrezGeneID() {
	return this.annotationList.get(0).getEntrezGeneID();
    }

    public VariantType getVariantType() {
	return this.type;
    }

    public void setMostPathogenicVariantType(VariantType vt) {
	this.type = vt;
    }

     /**
     * @return true if there are currently no annotations. 
     */
    public boolean isEmpty() { return this.annotationList.size() == 0; }


    /**
     * For annotations that affect multiple genes (i.e., multiple
     * gene symbols), return String with a comma-separated list of the
     * symbols. It is assumed that this function is call only for 
     * cases with multiple annotations.
     */
    public String getMultipleGeneList() {
	StringBuilder sb = new StringBuilder();
	/** First we need to get a list of the genesymbols. */
	HashSet<String> geneSymbolSet = new HashSet<String>();
	for (Annotation a : annotationList) {
	    geneSymbolSet.add(a.getGeneSymbol());
	}
	Iterator<String> it = geneSymbolSet.iterator();
	int i=0;
	while (it.hasNext()) {
	    String s = it.next();
	    if (i>0)
		sb.append(", " + s);
	    else
		sb.append(s);
	    i++;
	}
	return sb.toString();

    }


    /**
     * Note that it is pretty rare to have an annotation that affects multiple
     * genes (although it is common to have a variant affect multiple transcripts
     * that all have the same gene symbol). Therefore, for these rare cases we
     * have this function, that basically first gets a set of all the gene symbols and
     * then sorts the output accordingly.
     * @return String with the combined annotation.
     */
    public String  getCombinedAnnotationForVariantAffectingMultipleGenes() throws AnnotationException {
	StringBuilder sb = new StringBuilder();
	/** First we need to get a list of the genesymbols. */
	HashSet<String> geneSymbolSet = new HashSet<String>();
	for (Annotation a : annotationList) {
	    geneSymbolSet.add(a.getGeneSymbol());
	}
	/* Second we need to sort the annotations according to gene symbol.
	* Note that they already should be sorted according to position.*/

	for (String s: geneSymbolSet) {
	    ArrayList<String> tmp = new ArrayList<String>();
	    Annotation ann = this.annotationList.get(0);
	    for (int j=0;j<this.annotationList.size();++j) {
		ann  = this.annotationList.get(j);
		if (ann == null)
		    throw new AnnotationException("[AnnotationList.java]Annotation is null");
		String sym = ann.getGeneSymbol();
		if (sym.equals(s))
		    tmp.add(ann.getVariantAnnotation());
	    }
	    if (tmp.size()==0) {
		continue; /* This can happen if there are multiple genes with missense, ncRNA, synonymous etc 
			     annotations. */
	    }
	    sb.append(s + "(" + tmp.get(0));
	    for (int i = 1; i<tmp.size();++i) {
		sb.append("," + tmp.get(i));
	    }
	    sb.append(")");
	}
	return sb.toString();
    }


    /**
     * This method returns a String for up/downstream variants.
     * 
     */
    private String getUpDownstreamAnnotation() {
	Annotation ann = this.annotationList.get(0);
	StringBuilder sb = new StringBuilder();
	sb.append( ann.getVariantAnnotation());
	for (int j=1;j<this.annotationList.size();++j) {
	    ann  = this.annotationList.get(j);
	    sb.append("," + ann.getVariantAnnotation());
	}
	return sb.toString();
    }
    

    /**
     * This method returns an Intergenic annotation. There should only
     * be one such annotation per variant.
     * @return A String representing an INTERGENIC annotation
     */
    private String getIntergenicAnnotation()  {
	Annotation ann = this.annotationList.get(0);
	return ann.getVariantAnnotation();
    }



    /**
     * For variants that affect multiple transcripts, we sometimes want to
     * list all of the gene symbols alphabetically.
     * @return alphabetical list of gene symbols affected by the current variant.
     */
    private ArrayList<String> getSortedListOfGeneSymbols() {
	HashSet<String> set = new HashSet<String>();
	ArrayList<String> list = new ArrayList<String>();
	for (Annotation a : annotationList) {
		String s = a.getVariantAnnotation();
		if (! set.contains(s)) {
		    set.add(s);
		    list.add(s);
		}
	}
	Collections.sort(list);
	return list;
    }


    /**
     * This function will combine multiple intronic
     * annotations, e.g., "TRIM22,TRIM5" for a variant
     * that is located in the intron of these two different
     * genes. It works for coding and ncRNA intronic annotations.
     */
    private String getIntronicAnnotation() {
	if (! hasMultipleGeneSymbols) { /* just a single gene affected */
	    Annotation ann = annotationList.get(0);
	    return ann.getVariantAnnotation();
	} else { /* variant is in intron of multiple genes. */
	    ArrayList<String> symbol_list = getSortedListOfGeneSymbols();
	    String s = symbol_list.get(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(s);
	    for (int i=1;i<symbol_list.size();++i) {
		sb.append("," + symbol_list.get(i));
	    }
	    return sb.toString();
	}
    }


    /**
     * This function returns a String representing ncRNA annotations.  
     * Note that this function assumes that there are no UTR annotations
     * (because these are priority). This should have been decided by the 
     * calling function.
     */
    private String getNoncodingRnaAnnotation() {
	ArrayList<String> symbol_list = new ArrayList<String>();
	HashSet<String> seen = new HashSet<String>();
	for (Annotation a : annotationList) {
	    if (! a.isNonCodingRNA())
		continue;
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
	return sb.toString();
    }


       /**
     * This function will combine multiple UTR3/UTR5 annotations.
     * For now, we will just show the genesymbols (like annovar).
     */
    private String getUTRAnnotation() throws AnnotationException {
	ArrayList<String> symbol_list = new ArrayList<String>();
	HashSet<String> seen = new HashSet<String>();
	for (Annotation a : annotationList) {
	    if (a.getVariantType() != VariantType.UTR5 && 
		a.getVariantType() != VariantType.UTR3 && 
		a.getVariantType() != VariantType.UTR53) {
		continue; /* Often, there are both UTR and Intronic annotations for the same variant */
	    }
	    String s = a.getGeneSymbol();
	    if (seen.contains(s)) continue;
	    seen.add(s);
	    symbol_list.add(s);
	    if (s==null) {
		String e = "No Gene symbol found for UTR variant";
		throw new AnnotationException(e);
	    }
	}

	StringBuilder sb = new StringBuilder();
	/* The annotation begins as (e.g.) RNF207(uc001amg.3:exon17:c.1718A>G:p.N573S...
	   If there are multiple transcript annotations they are separated by comma.
	   After the last annotation, there is a closing parenthesis. */
	boolean needGeneSymbol = true; /* flag to show that we still need to add the gene symbol */
	for (int j=0;j<this.annotationList.size();++j) {
	    Annotation ann  = this.annotationList.get(j);
	    if (! ann.isUTRVariant())
		continue; /* this skips over non UTR annotations of alternative transcripts
			     for variants that have at least one UTR annotation. Note this
			     will break for variants affecting multiple genes.*/
	    if (needGeneSymbol) {
		sb.append(String.format("%s(%s", ann.getGeneSymbol(), ann.getVariantAnnotation()));
		needGeneSymbol = false;
	    } else {
		sb.append("," + ann.getVariantAnnotation());
	    }
	}
	sb.append(")");
	return sb.toString();
    }


      /**
     * Print out all annotations we have for debugging purposes (before summarization)
     */
    public void debugPrint() {
	System.out.println("AnnotatedList.java:debugPrint");
	System.out.println("Total annotations: " + annotationList.size());
	for (Annotation a : annotationList) {
	    System.out.println("[" + a.getVariantTypeAsString() + "] \"" + a.getGeneSymbol() + "\" -> " + a.getVariantAnnotation());
	}
	System.out.println("*******");
    }


}