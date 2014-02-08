package jannovar.annotation;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class encapsulates a single annotation and includes four pieces of information:
 * <OL>
 * <LI>The variant type: frameshift, synonymous substitution, etc (see {@link jannovar.common.VariantType VariantType}).
 * <LI>The gene symbol
 * <LI>A string representing the actual variant
 * <LI>The NCBI Entrez Gene id corresponding to the ucsc transcript being annotated.
 * </OL>
 * <P>
 * Each annotation for one transcript corresponds to a single Annotation object. All of the transcripts 
 * affected by a variant are collected by an {@link jannovar.annotation.AnnotationList AnnotationList}
 * object.
 * <P>
 * @author Peter N Robinson
 * @version 0.32 (3 February, 2014)
 */
public class Annotation implements Constants, Comparable<Annotation> {
    /** The type of the variant being annotated, using the constants in  {@link jannovar.common.VariantType VariantType},
	e.g., MISSENSE, 5UTR, etc. */
    private VariantType varType;
    /** The position of the variant in the ORF or mRNA, if applicable. This field is used to
     * sort exonic variants. Note that this variable is used differently for non-coding annotations,
     *  in which case it will indicate the distance of the variant from the nearest exon in nucleotides.
     * The variable is used by two methods, {@link #compareTo}; the sorting function is meant to be used
     * to sort variants within a gene, and is used right now by the class
     * {@link exomizer.annotation.AnnotationList AnnotationList}. One such list is made for each Variant. Therefore,
     * if the sorting method is called for an intergenic variant, they will be sorted by distance to the exon.
     * Since the sorting also includes priority class, this will not be a problem for variants that have both
     * coding and non-coding annotations.
     */
    private int rvarstart;

    /** The string representing the actual annotation, but
     * not including the gene symbol. For instance, for the
     * complete annotation "KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q",
     * this field would include "uc001aim.1:exon18:c.T2287C:p.X763Q"
     */
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
     * The NCBI Entrez Gene id corresponding to the UCSC knownGene id of the transcript
     * being annotated. Note that for a few cases, there is no entrezGene id, and then,
     * the parser in {@link jannovar.io.UCSCKGParser UCSCKGParser} will enter the value
     * {@link jannovar.common.Constants#UNINITIALIZED_INT}.
     */
    private int entrezGeneID=UNINITIALIZED_INT;
  
    /**
     * Return a byte constant that corresponds to the type of the variation. This will be one of the
     * constants in {@link jannovar.common.Constants Constants},
     * e.g., MISSENSE, 5UTR, etc. 
     * @return the {@link VariantType} the this {@link Annotation}
     */
    public VariantType getVariantType() { return this.varType; }
    /**
     * This function resets the variant type, and should only be used by the AnnotationList class for
     * certain cases of resolving precedence, e.g., if there is already a noncoding RNA intronic
     * annotation, and we get a new annotation for a coding isoform of the same gene.
     * @param typ {@link VariantType} to be set for this {@link Annotation}
     */
    public void setVarType(VariantType typ) { this.varType = typ; }


    /**
     * Construct a new annotation.
     * @param tmdl The transcript affected by the variant
     * @param annotation A complete annotation (without the gene symbol)
     * @param type The class of the variant (e.g., INTRONIC).
     */
    public Annotation(TranscriptModel tmdl, String annotation, VariantType type) {
	this.varType = type;
	this.variantAnnotation = annotation;
	this.geneSymbol = tmdl==null?null:tmdl.getGeneSymbol();
	this.entrezGeneID = tmdl==null?null:tmdl.getGeneID();
    }

    /**
     * Construct a new annotation. This constructor is used by variants that
     * are exonic and have a position in the CDS of the transcript.
     * @param tmdl The transcript affected by the variant
     * @param annotation A complete annotation (without the gene symbol)
     * @param type The class of the variant (e.g., INTRONIC).
     * @param refvarstart start position of variant in the CDS.
     */
    public Annotation(TranscriptModel tmdl, String annotation, VariantType type, int refvarstart) {
	this(tmdl,annotation,type);
	this.rvarstart = refvarstart;
    }

    /**
     * Construct a new annotation.
     * This factory method is used instead of a constructor
     * for intergenic annotations where there is
     * no gene synbol.
     * @param annotation A complete annotation (without the gene symbol)
     * @param type The class of the variant (e.g., INTRONIC).
     * @return the newly created {@link Annotation}
     */
    public static Annotation createIntergenicAnnotation(String annotation, VariantType type) {
	Annotation a = new Annotation();
	a.varType = type;
	a.variantAnnotation = annotation;
	a.geneSymbol=null;
	a.rvarstart=0;
	return a;
    } 

    /**
     * Client code should set the distance to the nearest exon.
     * This should be used only for INTERGENIC, INTRONIC, and UPSTREAM/DOWNSTREAM
     * variants.
     * @param d distance to the nearest exon.
     */
    void setDistanceToNearestExon(int d) {
	this.rvarstart = d;
    }


    /**
     * @return the distance of the variant-annotation to the nearest exon.
     */
    int getDistanceToNearestExon() {
	if (this.varType == VariantType.INTERGENIC ||
	    this.varType == VariantType.INTRONIC ||
	    this.varType == VariantType.UPSTREAM ||
	    this.varType == VariantType.DOWNSTREAM)
	    return this.rvarstart;
	else
	    return 0;
    }


    /**
     * @return The gene symbol (e.g., FBN1) for the gene affected by this Annotation.
     */
    public String getGeneSymbol() { return this.geneSymbol; }
    /**
     * @return the NCBI Entrez ID for the gene.
     */
    public int getEntrezGeneID() { return this.entrezGeneID; }

    /**
     * Get annotation String. Note, does not include the gene symbol.
     * @return String with the annotation, for instance, "uc001aim.1:exon18:c.T2287C:p.X763Q"
     */
    public String getVariantAnnotation() { return this.variantAnnotation; }


    /**
     * Get full annotation with gene symbol. If this Annotation does not have a
     * symbol (e.g., for an intergenic annotation), then just return the annotation string.<br>
     * e.g. "KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q"
     * @return the full annotation
     */
    public String getSymbolAndAnnotation() {
	if (geneSymbol==null && variantAnnotation != null)
	    return variantAnnotation;
	return String.format("%s:%s",geneSymbol,variantAnnotation);
    }

    /**
     * This method returns the accession number of the
     * transcript associated with this variant (if possible).
     * If there is no transcript, e.g., for DOWNSTREAM annotations,
     * then it returns the geneSymbol (if possible). If there is
     * no gene symbol (e.g., for INTERGENIC annotations), it returns "."
     * @return the accession number
     */
    public String getAccessionNumber() {
	if (this.variantAnnotation == null) return ".";
	int i = this.variantAnnotation.indexOf(":");
	if (i>0) {
	    return this.variantAnnotation.substring(0,i);
	}
	if (this.geneSymbol==null)
	    return ".";
	else
	    return this.geneSymbol;
    }



    /**
     * @param s A String representing the new annotation.
     */
    public void setVariantAnnotation(String s) { this.variantAnnotation = s; }

    /**
     * @param symbol The gene symbol for the gene affected by the variant.
     */
    public void setGeneSymbol(String symbol) { this.geneSymbol = symbol; }

    /**
     * TODO. Probably this should be refactored, this field should hold some ID number,
     * but we then need to know if it is from EntrezGene or another source.
     * For now, it is an int that is understood to be an Entrez Gene id, but in the future
     * we might use this to store ENSEMBL identifiers etc.
     * @param id The identifier of the gene affected by the variant (e.g., Entrez Gene ID)
     */
    public void setGeneID(int id) {
	this.entrezGeneID = id;
    }

    /**
     * Checks whether this annotation is equivalent to the
     * annotation being passed as an argument. This may be the
     * case say for two intronic annotations from different
     * isoforms of a gene where we do not care what the exact position is.
     * @param other An annotation to be checked for equivalence.
     * @return true if the geneSymbol and variantAnnotation are equal
     */
    public boolean equals(Annotation other) {
        return varType == other.varType 
                && ( ( geneSymbol != null  && geneSymbol.equals(other.geneSymbol) )
                || ( geneSymbol == null && other.geneSymbol == null) )
                && variantAnnotation != null
                && variantAnnotation.equals(other.variantAnnotation);
    }


    /** The (private) constructor is intended to be used 
     * only by static factory methods. Note that the variable
     * {@code rvarstart} is set to zero by the constructor. Variants that
     * have a defined position in an ORF assign their position to this
     * variable, which allows them to be sorted. Other variants (e.g., downstream),
     * which are not assigned a position, do not need or use this variable.
    */
    private Annotation() {
	this.rvarstart=0;
    }

    /**
     * This method constructs an empty Annotation object. We may be able to 
     * switch to simply the constructor, TODO continue refactoring.
     * @return an empty {@link Annotation}
     */
    public static Annotation createEmptyAnnotation() {
	Annotation a = new Annotation();
	a.rvarstart=0;
	return a;
    }

  
   
    /**
     * @return A string representing the variant type (e.g., MISSENSE, STOPGAIN,...)
     */
    public String getVariantTypeAsString() { 
    	return this.varType.toString();
    }

    /**
     * This function checks if we have a variant that affects the sequence of a coding exon
     * @return true if we have a missense, PTC, splicing, indel variant, or a synonymous change.
     */
    public boolean isCodingExonic() {
	switch(this.varType) {
	case SPLICING: 
	case STOPLOSS: 
	case STOPGAIN: 
	case SYNONYMOUS: 
	case MISSENSE:
	case NON_FS_SUBSTITUTION: 
	case NON_FS_INSERTION:
	case FS_SUBSTITUTION:
	case FS_DELETION: 
	case FS_INSERTION: 
	case NON_FS_DELETION: 
	    return true;
	default:
	    return false;
	}
    }

    /**
     * @return true if this annotation is for a 3' or 5' UTR 
     */
    public boolean isUTRVariant() {
	switch(this.varType) {
	case UTR3:
	case UTR5:
	    return true;
	default:
	    return false;
	}
    }

    /**
     * @return true if the variant affects an exon of an ncRNA
     */
    public boolean isNonCodingRNA() {
        return this.varType == VariantType.ncRNA_EXONIC;
    }


    /**
     * This function allows Annotation objects to be sorted. They are first
     * sorted according to the priority level of the VariantType. If there are
     * multiple Annotations with the same priority level, they are 
     * sorted according to their position
     * in the CDS. For other mutations, rvarstart is 0 and the sorting
     * has no effect.
     * @param other the {@link Annotation} to be compared with
     */
    @Override
    public int compareTo(Annotation other) {
	int mypriority = VariantType.priorityLevel(this.varType);
	int yourpriority = VariantType.priorityLevel(other.varType);
	if (mypriority < yourpriority)
	    return -1;
	else if (mypriority > yourpriority)
	    return 1;
        if (rvarstart < other.rvarstart )
            return -1;
        else if (rvarstart  > other.rvarstart)
            return 1;
	else
	    return 0;
    }

}
