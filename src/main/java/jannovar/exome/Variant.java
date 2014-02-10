package jannovar.exome;

import java.util.ArrayList;

import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
import jannovar.common.Constants;
import jannovar.common.Genotype;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.genotype.GenotypeCall;
import jannovar.reference.Chromosome;

/** A class that is used to hold information about the individual variants 
 *  as parsed from the VCF file.
 * @author Peter Robinson
 * @version 0.31 (22 January, 2014)
 */
public class Variant implements Comparable<Variant>, Constants {
    /** chromosome; 23=X, 24=Y */
    private byte chromosome;
    /** position along chromosome; */
    private int position; 
    /** Reference sequence (a single nucleotide for a SNV, more for some other types of variant) */
    private String ref;
    /** Variant sequence (called "ALT", alternate, in VCF files). */
    private String alt;
    /**  The genotype of this variant (note that {@link jannovar.genotype.GenotypeCall GenotypeCall}
     * objects can hold a single genotype for single-sample VCF files or multiple
     * genotypes for for VCF files with multiple samples.
     */
    private GenotypeCall genotype=null;
    /** The Quality score for the Variant call expressed as a Phred Score (QUAL column of the VCF file). */
    private float Phred;
   
    /** {@link jannovar.annotation.AnnotationList AnnotationList} object resulting from 
	Jannovar-type annotation of this variant. */
    private AnnotationList annotList=null;

   
     /**
     * @param c The chromosome (note: X=23, Y=24)
     * @param p Position of the variant
     * @param r Reference nucleotide
     * @param alternate variant (alt) nucleotide
     * @param gtype The Genotype call (single or multiple sample)
     * @param qual The PHRED quality of the variant call.
    */
    public Variant(byte c, int p, String r, String alternate, GenotypeCall gtype, float qual) {
	this.chromosome = c;
	this.position=p;
	this.ref = r;
	this.alt = alternate;
	this.genotype = gtype;
	this.Phred = qual;
    }

    /**
     * Create an annotation for this variant.
     * Client code needs to pass in the correct
     * {@link jannovar.reference.Chromosome Chromosome} object.
     * @param c The Chromosome object representing the location of the variant.
     * @throws jannovar.exception.AnnotationException
     */
    public void annotate(Chromosome c) throws AnnotationException {
	this.annotList  = c.getAnnotationList(this.position,this.ref,this.alt);
    }
   

    // ###########   SETTERS ######################### //
    /** Initialize the {@link #chromosome} field
     * @param chr A string representation of the chromosome such as chr3 or chrX
     */
    public void setChromosome(byte chr)  {
	this.chromosome = chr;
    }
     /** Initialize the {@link #position} field
     * @param p Position of the alternate sequence on the chromosome
     */
    public void setPosition(int p) {
	this.position = p;
    }
    /**
     * Initialize the {@link #ref} field
     * @param s sequence of reference
     */
    public void setRef(String s) {
	this.ref = s;
    }

    public void setGenotype(GenotypeCall gtype) {
	this.genotype = gtype;
    }

     /**
     * Initialize the {@link #alt} field
     * @param s sequence of variant
     */
    public void setVar(String s) {
	this.alt = s;
    }
    
    /**
     * Set the {@link jannovar.annotation.AnnotationList AnnotationList} object for this variant. 
     * This method is intended to provide transcript-
     * level annotation for the variants, for example, to annotate the chromosomal
     * variant {@code chr7:34889222:T>C} to {@code NPSR1(uc003teh.1:exon10:c.1171T>C:p.*391R) (Type:STOPLOSS)}.
     * @param a An annotationList object representing annotations of all affected transcripts.
     */
    public void setAnnotation(AnnotationList a) {
	this.annotList = a;
    }

    /**
     * Some variants are located in positions where multiple genes overlap.
     * @return true if the variant is located within more than one gene
     */
    public boolean affectsMultipleGenes() {
	return this.annotList.hasMultipleGeneSymbols();
    }

   
    // ###########   GETTERS ######################### //
    /**
     * @return the 5' position of this variant on its chromosome.
     */
    public int get_position() { return position; }
    /**
     * @return The reference base or bases of this variant
     */
    public String get_ref() { return ref; }
    /**
     * The alternate base or bases of this variant.
     */
    public String get_alt() { return alt; }
    /**
     * Get the genesymbol of the gene associated with this variant, if possible 
     */
    public String getGeneSymbol() { 
	if (this.annotList != null)  {
	    return annotList.getGeneSymbol();
	} else {
	    return ".";
	} 
    }

    /**
     * @return the NCBI Entrez Gene ID 
     */
    public int getEntrezGeneID() {
	return annotList.getEntrezGeneID();
    }
   
    /** 
     * @return true if this variant is a nonsynonymous substitution (missense).
     */
    public boolean is_missense_variant() { 
	if (annotList == null)
	    return false;
	else return (annotList.getVariantType() == VariantType.MISSENSE);
    }

    /**
     * Synonymous variant defined as a single nucleotide variant within a coding
     * sequence that does not change the encoded amino acid.
     * @return true if this variant is a SYNONYMOUS variant
     */
    public boolean isSynonymousVariant() {
	if (annotList == null)
	    return false;
	else return (annotList.getVariantType() == VariantType.SYNONYMOUS);
    }

   


    /**
     * A transition is purine <-> purine or pyrimidine <-> pyrimidine.
     * Only applies to single nucleotide subsitutions.
     * @return true if the variant is a SNV and a transition.
     */
    public boolean isTransition() {
	if (! is_single_nucleotide_variant () )
	    return false;
	/* purine to purine change */
	if (this.ref.equals("A") && this.alt.equals("G"))
	    return true;
	else if (this.ref.equals("G") && this.alt.equals("A"))
	    return true;
	/* pyrimidine to pyrimidine change */
	if (this.ref.equals("C") && this.alt.equals("T"))
	    return true;
	else if (this.ref.equals("T") && this.alt.equals("C"))
	    return true;
	/* If we get here, the variant must be a transversion. */
	return false;
    }

     /**
     * A transversion is purine <-> pyrimidine.
     * Only applies to single nucleotide subsitutions.
     * @return true if the variant is a SNV and a transversion.
     */
    public boolean isTransversion() {
	if (! is_single_nucleotide_variant () )
	    return false;
	/* purine to purine change */
	if (this.ref.equals("A") && this.alt.equals("G"))
	    return false;
	else if (this.ref.equals("G") && this.alt.equals("A"))
	    return false;
	/* pyrimidine to pyrimidine change */
	if (this.ref.equals("C") && this.alt.equals("T"))
	    return false;
	else if (this.ref.equals("T") && this.alt.equals("C"))
	    return false;
	/* If we get here, the variant must be a SNV and a transversion. */
	return true;
    }

  
    /**
     * @return true if both the reference and the alternate sequence have a length of one nucleotide.
     */
    public boolean is_single_nucleotide_variant () { return (this.ref.length()==1 && this.alt.length()==1); }
    
    /**
     * @return an integer representation of the chromosome  (note: X=23, Y=24, MT=25).
     */
    public int get_chromosome() { return chromosome; }
    /**
     * @return an byte representation of the chromosome, e.g., 1,2,...,22 (note: X=23, Y=24, MT=25).
     */
    public byte getChromosomeAsByte() { return chromosome; }

    public String get_chromosome_as_string() {
	if (this.chromosome == X_CHROMOSOME)
	    return "chrX";
	else if (this.chromosome == Y_CHROMOSOME)
	    return "chrY";
	else if (this.chromosome ==  M_CHROMOSOME)
	    return "chrM";
	else
	    return String.format("chr%d",this.chromosome);
    }

    /**
     * @return true if the variant is located on the X chromosome.
     */
    public boolean is_X_chromosomal() { return this.chromosome == X_CHROMOSOME;  }
    
     /**
     * @return true if the variant is located on the Y chromosome.
     */
    public boolean is_Y_chromosomal() { return this.chromosome == Y_CHROMOSOME;  }
      /**
     * @return true if the variant is located on the mitochondrion.
     */
    public boolean is_mitochondrial() { return this.chromosome == M_CHROMOSOME;  }



    /**
     * This function returns the quality of the first sample in the VCF file.
     * @return The PHRED quality of this variant call.
     */
    public float getVariantPhredScore() { return this.Phred; }

    /**
     * This function returns the quality of the first sample in the VCF file.
     * @return The PHRED quality of this variant call.
     */
    public float getVariantGenotypeQualityIndividualN(int n) { return this.genotype.getQualityInIndividualN(n); }

    /**
     * @return the Read Depth (DP) of this variant (in first or only individual in VCF file)
     */
    public int getVariantReadDepth() {return this.genotype.getReadDepthInIndividualN(0); }
     /**
     * @return the Read Depth (DP) of this variant (in  individual n of VCF file)
     */
    public int getVariantReadDepthIndividualN(int n) {return this.genotype.getReadDepthInIndividualN(n); }

    public boolean isHomozygousAlt() { return this.genotype.isHomozygousAltInIndividualN(0); }

    public boolean isHomozygousAltInIndividualN(int n) { return this.genotype.isHomozygousAltInIndividualN(n); }

    public boolean isHeterozygous() { return this.genotype.isHeterozygousInIndividualN(0); }

    public boolean isHeterozygousInIndividualN(int n) { return this.genotype.isHeterozygousInIndividualN(n); }
    /**
     * @return true if the variant belongs to a class that is non-exonic and nonsplicing. */
    public boolean isOffExomeTarget() {
	VariantType vt = getVariantTypeConstant();
	switch (vt){
	case INTRONIC:
	case ncRNA_INTRONIC:
	case UPSTREAM:
	case DOWNSTREAM:
	case INTERGENIC:
	    return true;
	default:
	    return false;
	}
    }

    public boolean isMissingInIndividualN(int n) { return this.genotype.isMissingInIndividualN(n); }


    /**
     * @return the {@link jannovar.genotype.GenotypeCall GenotypeCall} object corresponding to this variant.
     */
    public GenotypeCall getGenotype() { return this.genotype; }

    /**
     * The Genotype coresponds to one of HOMOZYGOUS_REF (0/0), 
     * HOMOZYGOUS_ALT (1/1), HETEROZYGOUS (0/1), 
     * NOT_OBSERVED (./.), ERROR, UNINITIALIZED.
     * @return the {@link jannovar.common.Genotype Genotype} object corresponding to this variant.
     */
    public Genotype getGenotypeInIndividualN(int n) {
	return this.genotype.getGenotypeInIndividualN(n);
    }

    /**
     * @return A string representing the genotype of this variant.
     */
    public String getGenotypeAsString() {
	return this.genotype.get_genotype_as_string();
    }
    
    /**
     * This function uses the function of the same name of the 
     * the {@link jannovar.genotype.GenotypeCall GenotypeCall} object
     * corresponding to this variant.
     * @return A list of genotype calls, e.g., "0/0","0/1","1/1"
     */
    public ArrayList<String> getGenotypeList() {
	return this.genotype.getGenotypeList();
    }

    public String get_position_as_string() { return Integer.toString(this.position); }
   
    /**
     * @return variant expressed in chromosomal coordinates 
     */
    public String get_chromosomal_variant() {
	StringBuilder sb = new StringBuilder();
	sb.append( get_chromosome_as_string() );
	sb.append(":g.");
	sb.append(this.position + ref + ">" + alt);
	return sb.toString();
    }


    /**
     * Get representation of current variant as a string. This method
     * retrieves the annotation of the variant stored in the
     * {@link jannovar.annotation.Annotation Annotation} object. If this
     * is not initialized (which should never happen), it returns ".".
     *<p>
     * Note: This function returns a String with one representative annotation
     * for the current variant
     * <p>
     * If client code wants to get a list of each individual annotation, it should instead call 
     * the function {@link #getAnnotationList}.
     * @return The annotation of the current variant.
     */
    public String getAnnotation()
    {
	try { 
	    if (this.annotList != null)
		return this.annotList.getVariantAnnotation();
	     else return ".";
	} catch (AnnotationException e) {
	    return "error retrieving annotation";
	}
    }

    /**
     * This function returns an annotation for a single transcript
     * affected by the variant, returning the variant annotation being ranked
     * with the highest priority. In contrast, the function
     * {@link #getAnnotation} returns a summarized version of annotations of
     * all transcripts.
     * @return a representative annotation of one transcript.
     */
    public String getRepresentativeAnnotation() {
	try { 
	    return this.annotList.getSingleTranscriptAnnotation();
	} catch (AnnotationException e) {
	    return "error retrieving annotation";
	}
    }


    /**
     * This function returns a list of all of the 
     * {@link jannovar.annotation.Annotation Annotation} objects
     * that have been associated with the current variant. This function
     * can be called if client code wants to display one line for each
     * affected transcript, e.g., 
     * <ul>
     * <li>LTF(uc003cpr.3:exon5:c.30_31insAAG:p.R10delinsRR)
     * <li>LTF(uc003cpq.3:exon2:c.69_70insAAG:p.R23delinsRR)
     * <li>LTF(uc010hjh.3:exon2:c.69_70insAAG:p.R23delinsRR)
     * </ul>
     * <P>
     * If client code wants instead to display just
     * a single string that summarizes all of the annotations, it should
     * call the function {@link #getAnnotation}.
     */
    public ArrayList<String> getAnnotationList() {
	ArrayList<String> A = new ArrayList<String>();
	if (this.annotList == null) {
	    A.add(".");
	    return A;
	}
	ArrayList<Annotation> alist = this.annotList.getAnnotationList();

	for (Annotation ann : alist) {
	    String s = ann.getVariantAnnotation();
	    A.add(s);
	}
	return A;
    }

    /**
     * @return List of {@link jannovar.annotation.Annotation Annotation} objects.
     */
    public ArrayList<Annotation> getAnnotationObjectList() {
	return this.annotList.getAnnotationList();
    }

    /**
     * @return A list of all annotations for this variant, in the form GeneSymbol(annotation)
     */
    public ArrayList<String> getAnnotationListWithGeneSymbol() {
	if (this.annotList == null) {
	    ArrayList<String> A = new ArrayList<String>();
	    A.add(".");
	    return A;
	}
	ArrayList<Annotation> alist = this.annotList.getAnnotationList();
	ArrayList<String> A = new ArrayList<String>();
	for (Annotation ann : alist) {
	    String s = ann.getVariantAnnotation();
	    String sym = ann.getGeneSymbol();
	    A.add(String.format("%s (%s)",s,sym));
	}
	return A;
    }

    /**
     * Returns a list of annotations for this variant together with their type.
     * @return A list of all annotations for this variant, in the form type|annotation
     */
    public ArrayList<String> getAnnotationListWithAnnotationClass() {
	if (this.annotList == null) {
	    ArrayList<String> A = new ArrayList<String>();
	    A.add(".");
	    return A;
	}
	ArrayList<Annotation> alist = this.annotList.getAnnotationList();
	ArrayList<String> A = new ArrayList<String>();
	for (Annotation ann : alist) {
	    String s = ann.getVariantAnnotation();
	    String typ = ann.getVariantTypeAsString();
	    A.add(String.format("%s|%s",typ,s));
	}
	return A;
    }






    /**
     * This method combines the fields of the String array s using
     * a colon (":") as a separator, similar to the way that the Perl
     * function join works. However, this function removes the first
     * element of the array because it contains the transcript id
     * in our application (e.g., uc003szt.3). For certain types of
     * annotation, however, there is only one field (intronic annotations
     * are currently handled this way). If so, return just this field.
     */
    private String combineWithoutID(String[] s)
    {
	int k=s.length;
	if (k==0)
	    return null;
	if (k==1)
	    return s[0];
	StringBuilder out=new StringBuilder();
	out.append(s[1]);
	for (int x=2;x<k;++x)
	    out.append(":").append(s[x]);
	return out.toString();
    }

    /**
     * This method is intended to be used by programs such as Exomiser that
     * add URLs or other additional information to the annotations. The method
     * works under the assumption that the Annotation object will return a
     * string that may be separated by ":" into fields. The first field should
     * then represent a database identifier such as an UCSC gene ID. This
     * field is then split into a separate string, and separated from the 
     * remaining part of the annotation by a "pipe" ("|") symbol. This should
     * make it easy to create HTML links from the accession number and to display
     * the rest of the annotation as text or part of the link using program-specific
     * logic.
     * <P>
     * note that some variants affect multiple genes because they overlap. Application
     * software generally wants to display this in a special way. We signal this
     * here by adding a third field whose last field in the gene symbol.
     */
    public ArrayList<String> getTranscriptAnnotations() {
	ArrayList<String> A = new ArrayList<String>();
	if (this.annotList == null) {
	    A.add(".|."); /* empty ID, empty annotation. This should actually never happen, but it
			     is better than returning null. */
	    return A;
	}
	boolean mult = this.affectsMultipleGenes();
	ArrayList<Annotation> alist = this.annotList.getAnnotationList();
	for (Annotation ann : alist) {
	    String s = ann.getVariantAnnotation();
	    String F[] = s.split(":");
	    String id  = F[0]; /* this will be something like "uc003szt.3" */
	    s = combineWithoutID(F);
	    if (ann.getVariantType() == VariantType.INTRONIC) {
		s = String.format("%s:intronic",s);
	    }
	    
	    s = String.format("%s|%s",id,s);
	    if (mult) {
		s = String.format("%s|%s",s,ann.getGeneSymbol());
	    }
	    A.add(s);
	}
	return A;
    }


    /**
     * 
     * @return
     * @throws AnnotationException
     */
    public Annotation getMostPathogenicAnnotation() throws AnnotationException{
    	if (this.annotList.isEmpty()) {
    	    String e = String.format("[AnnotationList] Error: No Annotations found");
    	    throw new AnnotationException(e);
    	}
    	return this.annotList.getAnnotationList().get(0);
    }
 


    public String getVariantType() {
	if (this.annotList != null)
	    return this.annotList.getVariantType().toString();
	else return "?";
    }

    /**
     * @see jannovar.common.Constants
     * @return a enum constant representing the variant class (NONSENSE, INTRONIC etc)
     */
    public VariantType getVariantTypeConstant() {
	if (this.annotList != null)
	    return this.annotList.getVariantType();
	else
	    return VariantType.ERROR;
    }

   
   
   
    
    /**
     * @return A String representing the variant in the chromosomal sequence, e.g., chr17:c.73221527G>A
     */
    public String getChromosomalVariant() {
	return String.format("%s:c.%d%s>%s",get_chromosome_as_string(), position, ref, alt);
    }


    /**
     * Represent the Variant and its genotype as a string. This method
     * is intended primarily for debugging, use other access methods to
     * output information about the variant.
     */
    public String toString() {
	StringBuilder sb = new StringBuilder();
	String chr = getChromosomalVariant();
	sb.append("\t"+ chr);
	if (annotList != null)
	    sb.append("\t: "+ getAnnotation() + "\n");
	else
	    sb.append("\tcds mutation not initialized\n");
	sb.append("\tGenotype: " + this.genotype.get_genotype_as_string() + "\n");
	sb.append("\tType: " + get_variant_type_as_string() );
	return sb.toString();
    }
    /**
     * The variant types (e.e., MISSENSE, NONSENSE) are stored internally as byte values.
     * This function converts these byte values into strings.
     * @return A string representing the type of the current variant.
     */
     public String get_variant_type_as_string()
    {
	if (this.annotList == null)
	    return "uninitialized";
	else
	    return this.annotList.getVariantType().toString();
    }


     public int getDistanceFromExon() {
	 return this.annotList.getDistanceFromExon();
     }



    
    /**
     * Sort based on chromosome and position.
     * If these are equal, sort based on the lexicographic
     * order of the reference sequence. If this is equal, sort
     * based on the lexicographic order of the alt sequence.
     */
    @Override
    public int compareTo(Variant other) {
	if (other.chromosome > this.chromosome) return -1;
	else if (other.chromosome < this.chromosome) return 1;
	else if (other.position > this.position) return -1;
	else if (other.position < this.position) return 1;
	else if (! other.ref.equals(this.ref))
	    return other.ref.compareTo(this.ref);
	else if (! other.alt.equals(this.alt))
	    return other.alt.compareTo(this.alt);
	else return 0;
    }
}