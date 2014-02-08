package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;


/**
 * Encapsulates a list of {@link jannovar.annotation.Annotation Annotation} objects associated 
 * with a {@link jannovar.exome.Variant Variant} and provides some access functions that
 * summarize, sort, or display the objects. Note that rarely, a variant annotation is made to more
 * than one Gene symbol. In this case, we represent the affected gene as a comma-separated list of symbols.
 * <P>
 * The list {@link #annotationList} contains all of the
 * {@link jannovar.annotation.Annotation Annotation} objects but they are sorted according to
 * priority. We can take advantage of this if we want to return only those annotations that
 * belong to the highest priority class by noting the class of the first annotation, and
 * not returning any annotation  of a lower priority level.
 * @author Peter N Robinson
 * @version 0.19 (31 December, 2013)
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

    /** Flag to initial sort the {@link Annotation}s for the multisample output. */
    private boolean isPrioritySorted = false;
    
    /**
     * Prevent unwanted initialization of an empty
     * AnnotationList object by making the default constructor private.
     */
    private AnnotationList() {
	/* no-op */
    }

    /**
     * Construction of AnnotationList objects is performed by an
     * {@link jannovar.annotation.AnnotatedVariantFactory AnnotatedVariantFactory} 
     * object based on the transcripts that are identified by the Interval
     * tree search. All annotations that affect one variant are passed to
     * the constructor.
     * @param lst List of all annotations that affect the current variant.
     */
    public AnnotationList(ArrayList<Annotation> lst) {
	this.annotationList = new ArrayList<Annotation>();
	this.annotationList.addAll(lst);
    }

   

    /**
     * Get a list of all individual
     * {@link jannovar.annotation.Annotation Annotation}
     * objects that affect the variant that owns this AnnotationList.
     * @return {@link ArrayList} of {@link Annotation}s
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
     * @return true if the variant is located within multiple (overlapping) genes.
     */
    public boolean hasMultipleGeneSymbols()  { return this.hasMultipleGeneSymbols; }

     /**
     * If there are multiple annotations, this function
     * sorts them. This function also sets the overall variant type (the most
     * pathogenic single type found among all annotations).
     * @throws jannovar.exception.AnnotationException
     */
    public void sortAnnotations() throws AnnotationException {
    	if(this.isPrioritySorted)
    		return;
	if (this.annotationList.isEmpty())  {
	    throw new AnnotationException("No data for annotation");
	} else {
	    java.util.Collections.sort(this.annotationList);
	    this.isPrioritySorted = true;
	}
    }
    
    
    
    public String getAllTranscriptVariantEffects() throws AnnotationException {
    	if (this.annotationList.isEmpty()) {
    	    String e = String.format("[AnnotationList] Error: No Annotations found");
    	    throw new AnnotationException(e);
    	}
    	if(!this.isPrioritySorted)
    		this.sortAnnotations();
    	StringBuffer buff = new StringBuffer();
    	boolean hasFirst = false;
    	for (Annotation anno : this.annotationList) {
    		if(hasFirst)
    			buff.append(",");
			buff.append(anno.getVariantType());
			hasFirst = true;
		}
    	return buff.toString();
    }
    
    /**
     * Get the annotations for all transcript (this will be used to annotate VCF files).
     * If there are more than one annotation a comma separated list of annotations will be returned.
     * The annotations are sorted according to the pathogenicity of the variation.
     * @return String representation of all annotations
     * @throws jannovar.exception.AnnotationException
     */
    public String getAllTranscriptAnnotations() throws AnnotationException {
    	if (this.annotationList.isEmpty()) {
    	    String e = String.format("[AnnotationList] Error: No Annotations found");
    	    throw new AnnotationException(e);
    	}
    	if(!this.isPrioritySorted)
    		this.sortAnnotations();
    	StringBuffer buff = new StringBuffer();
    	boolean hasFirst = false;
    	for (Annotation anno : this.annotationList) {
    		if(hasFirst)
    			buff.append(",");
			buff.append(anno.getSymbolAndAnnotation());
			hasFirst = true;
		}
    	return buff.toString();
    }

    /**
     * Get an annotation for a single transcript (this will be used to annotated VCF files).
     * Note that we will return an annotation that matches with the overall type of this
     * annotation, in case there are multiple annotations for this variant (e.g., if there
     * are nonsense and synonymous annotation, return nonsense).
     * @return annotation for a single transcript
     * @throws jannovar.exception.AnnotationException
     */
    public String getSingleTranscriptAnnotation() throws AnnotationException {
	if (this.annotationList.isEmpty()) {
	    String e = String.format("[AnnotationList] Error: No Annotations found");
	    throw new AnnotationException(e);
	}
	Annotation a = this.annotationList.get(0);
	return a.getSymbolAndAnnotation();
    }



    /**
     * @return the distance of the variant to the nearest exon.
     */
    public int getDistanceFromExon() {
	if (this.annotationList.isEmpty()) {
	    return 0; /* should never happen */
	}
	int mindist=Integer.MAX_VALUE;
	for (Annotation a : this.annotationList) {
	    int dist = a.getDistanceToNearestExon();
	    if (dist==0)
		return 0;
	    else if (dist < mindist)
		mindist = dist;
	}
	return mindist;
    }





    /**
     * @return an annotation consisting of the gene symbol and a list of all affected transcripts 
     * with the HGVS mutation nomenclature.
     * @throws jannovar.exception.AnnotationException
     */
    public String getVariantAnnotation() throws AnnotationException {
	if (this.annotationList.isEmpty()) {
	    String e = String.format("[AnnotationList] Error: No Annotations found");
	    throw new AnnotationException(e);
	}
	/* Make a list for the class of annotations with the highest priority */
	ArrayList<Annotation> priorityList = new ArrayList<Annotation>();
	VariantType tp = this.annotationList.get(0).getVariantType();
	int topPriority = VariantType.priorityLevel(tp);
	int lastIdx = this.annotationList.size() - 1;
	VariantType lastType = this.annotationList.get(lastIdx).getVariantType();
	if (tp.equals(lastType)) {
	    priorityList = this.annotationList;
	} else {
	    /* i.e., there are annotations of different types in the list */
	    for (Annotation a : this.annotationList) {
	        int level = VariantType.priorityLevel(a.getVariantType());
		if (level == topPriority)
		    priorityList.add(a);
		else
		    break;
	    }		
	}
	/* When we get here, priorityList has all of the annotations we want to return. */
	if (topPriority == 1) {
	    return getExonicAnnotations(priorityList);
	}
	if (this.type == VariantType.ncRNA_EXONIC || this.type == VariantType.ncRNA_SPLICING) {
	    return getNoncodingRnaAnnotation(priorityList);
	} else if (this.type == VariantType.UTR3 || this.type == VariantType.UTR5) {
	    return getUTRAnnotation(priorityList);
	} else if (this.type == VariantType.SYNONYMOUS) {
	     return getExonicAnnotations(priorityList);
	}   else if (this.type == VariantType.INTRONIC || this.type == VariantType.ncRNA_INTRONIC) {
	    return getIntronicAnnotation(priorityList);
	} else if (this.type == VariantType.DOWNSTREAM || this.type == VariantType.UPSTREAM) {
	    return getUpDownstreamAnnotation(priorityList);
	} else if (this.type == VariantType.INTERGENIC) {
	    Annotation ann = priorityList.get(0);
	    return ann.getVariantAnnotation();
	}  else if (this.hasMultipleGeneSymbols) {
	    return getCombinedAnnotationForVariantAffectingMultipleGenes();
	} else { 
	    throw new AnnotationException("[AnnotationList] Could not find annotation class");
	}
    }

    private String getExonicAnnotations(ArrayList<Annotation> lst) {
	StringBuilder sb = new StringBuilder();
	/* The annotation begins as (e.g.) RNF207(uc001amg.3:exon17:c.1718A>G:p.N573S...
	   If there are multiple transcript annotations they are separated by comma.
	   After the last annotation, there is a closing parenthesis. */
	boolean needGeneSymbol = true; /* flag to show that we still need to add the gene symbol */
	for (int j=0;j<lst.size();++j) {
	    Annotation ann  = lst.get(j);
	    if (needGeneSymbol) {
		sb.append(String.format("%s(%s", ann.getGeneSymbol(), ann.getVariantAnnotation()));
		needGeneSymbol = false;
	    } else {
		sb.append(",").append(ann.getVariantAnnotation());
	    }
	}
	sb.append(")");
	return sb.toString();
    }


    /**
     * Returns the gene symbol of the annotations. If multiple genes are 
     * affected, it returns the Gene symbol of the most highly prioritized gene.
     * @return gene symbol of the annotations
     */
    public String getGeneSymbol() {
	/*if (this.hasMultipleGeneSymbols) {
	    return getMultipleGeneList();
	    } else */
	if (this.annotationList == null) {
	    System.err.println("error-annotationListNull");
	    System.out.println("VarType = " + type);
	} else if (this.annotationList.isEmpty()) {
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

    /**
     * If the {@link AnnotationList} has multiple Annotation objects, 
     * then the Annotation most likely to be pathogenic is returned.
     * @return most pathogenic {@link VariantType} in the list
     */
    public VariantType getVariantType() {
	return this.type;
    }

    public void setMostPathogenicVariantType(VariantType vt) {
	this.type = vt;
    }

     /**
     * @return true if there are currently no annotations. 
     */
    public boolean isEmpty() { return this.annotationList.isEmpty(); }


    /**
     * For annotations that affect multiple genes (i.e., multiple
     * gene symbols), return String with a comma-separated list of the
     * symbols. It is assumed that this function is call only for 
     * cases with multiple annotations.
     * @return all gene symbols of affected genes
     */
    public String getMultipleGeneList() {
	StringBuilder sb = new StringBuilder();                                                                                      
	/** First we need to get a list of the genesymbols. */
	HashSet<String> geneSymbolSet = new HashSet<String>();
	int i=0;
	for (Annotation a : annotationList) {
	    if (!geneSymbolSet.contains(a.getGeneSymbol())){
		if (i>0)
		    sb.append(", ");
		i++;
		sb.append(a.getGeneSymbol());
	    }
	    geneSymbolSet.add(a.getGeneSymbol());
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
     * @throws jannovar.exception.AnnotationException
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
	    Annotation ann;
	    for (int j=0;j<this.annotationList.size();++j) {
		ann  = this.annotationList.get(j);
		if (ann == null)
		    throw new AnnotationException("[AnnotationList.java]Annotation is null");
		String sym = ann.getGeneSymbol();
		if (sym.equals(s))
		    tmp.add(ann.getVariantAnnotation());
	    }
	    if (tmp.isEmpty()) {
		continue; /* This can happen if there are multiple genes with missense, ncRNA, synonymous etc 
			     annotations. */
	    }
	    sb.append(s).append("(").append(tmp.get(0));
	    for (int i = 1; i<tmp.size();++i) {
		sb.append(",").append(tmp.get(i));
	    }
	    sb.append(")");
	}
	return sb.toString();
    }


    /**
     * This method returns a String for up/downstream variants.
     * Note that in some cases, we have up and downstream annotations.
     * Note that UPSTREAM has a higher priority than DOWNSTREAM
     */
    private String getUpDownstreamAnnotation(ArrayList<Annotation> lst) {
	boolean first = true;
	HashSet<String> seen = new HashSet<String>();
	StringBuilder sb = new StringBuilder();
	for (Annotation a : lst) {
	    String sym = a.getGeneSymbol();
	    if (seen.contains(sym))
		continue;
	    else {
		seen.add(sym);
		if (first) {
		    sb.append( a.getVariantAnnotation() );
		    first = false;
		} else {
		    sb.append(",").append(a.getVariantAnnotation());
		}
	    }
	}
	return sb.toString();
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
    private String getIntronicAnnotation(ArrayList<Annotation> lst) {
	   StringBuilder sb = new StringBuilder();
	if (! hasMultipleGeneSymbols) { /* just a single gene affected */
	    Annotation ann = lst.get(0);
	    sb.append(ann.getGeneSymbol());
	    sb.append("(");
	    sb.append(ann.getVariantAnnotation());
	    sb.append(")");
	} else { /* variant is in intron of multiple genes, get one annotation each */
	    HashSet<String> seen = new HashSet<String>(); 
	    boolean hasFirst = false;
	    for (Annotation a: lst) {
		String sym = a.getGeneSymbol();
		if (seen.contains(sym)) {
		    continue;
		} else {
		    seen.add(sym);
		    if (hasFirst) 
				sb.append(",");
		    sb.append(a.getGeneSymbol());
		    sb.append("(");
		    sb.append(a.getVariantAnnotation());
		    sb.append(")");
			hasFirst = true;
		    
		}
	    }
	}
	    return sb.toString();
    }


    /**
     * This function returns a String representing ncRNA annotations.  
     * It basically just lists the annotations in order.
     * Note that this function assumes that there are no UTR annotations
     * (because these are priority). This should have been decided by the 
     * calling function.
     */
    private String getNoncodingRnaAnnotation(ArrayList<Annotation> lst) {
	StringBuilder sb = new StringBuilder();
	boolean notfirst=false;
	for (Annotation a: lst) {
	    if (notfirst) { sb.append(",");} else notfirst=true;
	    sb.append(a.getGeneSymbol());
	    sb.append("(");
	    sb.append(a.getVariantAnnotation());
	    sb.append(")");
	}
	    
	return sb.toString();
    }


       /**
     * This function will combine multiple UTR3/UTR5 annotations.
     * For now, we will just show the genesymbols (like annovar).
     */
    private String getUTRAnnotation(ArrayList<Annotation> lst) throws AnnotationException {
	//ArrayList<String> symbol_list = new ArrayList<String>();
	HashSet<String> seen = new HashSet<String>();
	for (Annotation a : lst) {
	    String s = a.getGeneSymbol();
	    if (seen.contains(s)) continue;
	    seen.add(s);
	    //symbol_list.add(s);
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
	for (int j=0;j<lst.size();++j) {
	    Annotation ann  = lst.get(j);
	    if (! ann.isUTRVariant())
		continue; /* this skips over non UTR annotations of alternative transcripts
			     for variants that have at least one UTR annotation. Note this
			     will break for variants affecting multiple genes.*/
	    if (needGeneSymbol) {
		sb.append(String.format("%s(%s", ann.getGeneSymbol(), ann.getVariantAnnotation()));
		needGeneSymbol = false;
	    } else {
		sb.append(",").append(ann.getVariantAnnotation());
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
	    int level = VariantType.priorityLevel(a.getVariantType());
	    System.out.println("[" + a.getVariantTypeAsString() + "] \"" + a.getGeneSymbol() + "\" -> " 
			       + a.getVariantAnnotation() + " [" + level + "]");
	}
	System.out.println("*******");
    }


}