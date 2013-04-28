package jannovar.reference;


import java.util.TreeMap;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

import jannovar.annotation.AnnotatedVariantFactory;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.Annotation;

import jannovar.reference.KnownGene;

import jannovar.reference.Translator;
import jannovar.exception.AnnotationException;

/** The following classes contain static functions used to calculate the 
    annotations for certain classes of mutation. These functions are put into
    separate classes just to keep things tidy and comprehensible in this class.
*/
import jannovar.annotation.DeletionAnnotation;
import jannovar.annotation.InsertionAnnotation;
import jannovar.annotation.SingleNucleotideSubstitution;
import jannovar.annotation.BlockSubstitution;
import jannovar.annotation.SpliceAnnotation;
import jannovar.annotation.UTR3Annotation;


/**
 * This class encapsulates a chromosome and all of the genes its contains.
 * It is intended to be used together with the KnownGene class to make 
 * a list of gene models that will be used to annotate chromosomal variants.
 * The genes are stored in a TreeMap (A Java implementation of the red-black
 * tree), allowing them to be found quickly on the basis of the position of 
 * the chromosomal variant. Also, we can find the neighbors (5' and 3') of 
 * the closest gene in order to find the right and left genes of intergenic
 * variants and to find the correct gene in the cases of complex regions of the
 * chromosome with one gene located in the intron of the next or with overlapping
 * genes. 
 * <P>
 * Note that the key of the tree map corresponds to the 5' most position of 
 * the KnownGene. The value is a list
 * (ArrayList) of {@link jannovar.reference.KnownGene KnownGene} objects. 
 * This is because multiple KnownGenes may share the same transcription
 * start (e.g., multiple splice forms of the same gene).
 * <P>
 * Note that this class contains some of the annotation functions of Annovar. It was not
 * attempted to reimplement all of the copious functionality of that nice program,
 * just enough to annotate variants found in VCF files. Some notes in particular
 * <UL>
 * <LI> The -seq_padding functionality of annovar was ignored
 * </UL>
 * @author Peter N Robinson
 * @version 0.12 (28 April, 2013)
 */
public class Chromosome {
    /** Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. 
     TODO. Add more flexible way of dealing with scaffolds etc.*/
    private byte chromosome;
    /** Alternative String for Chromosome. Use for scaffolds and "random" chromosomes. TODO: Refactor */
    private String chromosomeString=null;
    /** TreeMap with all of the genes ({@link jannovar.reference.KnownGene KnownGene}
     * objects) of this chromosome. The key is an
     * Integer value representing the transcription start site (txstart) of the transcript. 
     * Note that we need to use an Array of KnownGenes because there can be multiple 
     * KnownGenes that share the same transcription start site.
     * (e.g., multiple isoforms of the same gene).*/
    private TreeMap<Integer,ArrayList<KnownGene>> geneTreeMap=null;
    /** Total number of KnownGenes on the chromosome including multiple transcripts of the same gene. */
    private int n_genes;
    /** The number of keys (gene 5' positions) to search in either direction. Note that if we just use a SPAN of
     *5 genes, then we tend to miss some annotations in areas of lots of transcripts. TODO figure out the
     * best value for SPAN to compromise between getting all annotations and speed. Most of the missed annotations
     * seem to be intronic or downstream types, and may not be interesting for the Jannovar anyway.*/
    private static final int SPAN = 20;
    /** The distance threshold in nucleotides for calling a variant upstream/downstream to a gene, */
    private static final int NEARGENE = 1000;
  
    /** Class object encapsulating rules to translate DNA. */
    private Translator translator = null;
    /**
     * This object will be used to prioritize the annotations and to choose the one(s) to
     * report. For instance, if we have both an intronic and a nonsense mutation, just
     * report the nonsense mutation. Note that the object will be initialized once in the constructor
     * of the Chromosome class and will be reset for each new annotation, rather than creating
     * a new object for each variation. Also note that the constructor takes an integer value
     * with which the lists of potential annotations get initialized. We will take 2*SPAN because
     * this is the maximum number of annotations any variant can get with this program.
     */
    private AnnotatedVariantFactory annovarFactory = null;
    
    /**
     * The constructor expects to get a byte representing 1..22 or 23=X_CHROMSOME, or
     * 24=Y_CHROMOSOME (see {@link jannovar.common.Constants Constants}).
     */
    public Chromosome(byte c) {
	this.chromosome = c;
	this.geneTreeMap = new TreeMap<Integer,ArrayList<KnownGene>>();
	this.translator = Translator.getTranslator();
	this.n_genes=0;
	this.annovarFactory = new AnnotatedVariantFactory(2*SPAN); /* the argument is the initial capacity of the arrayLists of vars */
    }

    /**
     * Add a gene model to this chromosome. 
     @param kg A knownGene (note that there is one knownGene entry for each isoform of a gene).
     */
    public void addGene(KnownGene kg) {
	int pos = kg.getTXStart();
	ArrayList<KnownGene> lst = null;
	/* 1. There is already a KnownGene with this txStart */
	if (this.geneTreeMap.containsKey(pos)) {
	    lst = geneTreeMap.get(pos);
	    lst.add(kg);
	}
	/* 2. This is the first knownGene with this txStart */
	else {
	    lst =  new 	ArrayList<KnownGene>();
	    lst.add(kg);
	    this.geneTreeMap.put(pos,lst);
	}
	n_genes++;
    }

    /**
     * @return String representation of name of chromosome, e.g., chr2
     */
    public String getChromosomeName() {
	if (chromosomeString != null) return chromosomeString;
	else return String.format("chr%d",chromosome);
    }
    
    
    /**
     * In annovar, genomic bins are used to determine which genes to search
     * for variants. This is prefere
     * <P>
     * The position of the variant can be either within a gene ("genic") or between
     * two genes. Given some complexities of genomic architecture, this is not always a simple
     * question to answer (e.g., overlapping genes or location of one gene in an intron of
     * another gene). Annovar's manswer to this is to use "bins" of 100,000 nucleotides and to
     * search over all bins until either a genic match is found or the two neighboring genes
     * are found. We instead us a Java TreeMap, which is an implementation of a red-black tree. The TreeMap method
     * {@code} ceilingEntry(K key)} returns a key-value mapping associated with the least key greater than or equal 
     * to the given key, or null if there is no such key. We use this to get an entry in the TreeMap that is close
     * to the position of the sought after variant. If this method returns null, we search with {@code floorEntry}. Then,
     * we use the key returned by this together with the methods {@code higherKey} and {@code lowerKey} to get
     * keys in the tree map that span a range of SPAN entries. We then search amongst these entries much the same as
     * Annovar does in its bins.
     * <P>
     * The strategy is to put the KnownGene nearest to position at index 0 of the ArrayList that is to be returned. 
     * Then, we add up to SPAN genes on each side, going out centrifugally. This
     * will increase the probability that only one gene has to be tested, and if not reduce the total number.
     * @param position the Position of the mutation on the current chromosome.
     * @return list of KnownGenes that are candidates for being affected by variant at this position
     */
    private ArrayList<KnownGene> getBinRange(int position) {
	ArrayList<KnownGene> kgList = new ArrayList<KnownGene>(); /* list of KnownGenes in range. */
	KnownGene currentKG=null;
	ArrayList<KnownGene> currentKGlist=null;
	
	Integer midpos=null, leftpos=null, rightpos=null;
	Map.Entry<Integer,ArrayList<KnownGene>> entrL = this.geneTreeMap.ceilingEntry(position);
	Map.Entry<Integer,ArrayList<KnownGene>> entrR = null;
	if (entrL == null) {
	    entrL = this.geneTreeMap.floorEntry(position);
	}
	if (entrL == null) {
	    System.err.println("Error: Could not get either floor or ceiling for variant at position " + position
			       + " on chromosome " + 	this.chromosome );
	    System.exit(1);
	}
	midpos = entrL.getKey();
	currentKGlist = entrL.getValue();
	for (KnownGene KG : currentKGlist)
	    kgList.add(KG);
	
	leftpos = midpos;
	rightpos = midpos;
	for (int i = SPAN; i>0; i--) {
	    entrL = this.geneTreeMap.lowerEntry(leftpos);
	    entrR = this.geneTreeMap.higherEntry(rightpos);
	    /* Note that entrL may be null if this variant is located 5' to the 
	       first gene on this chromosome. */
	    Integer iL = null;
	    if (entrL != null)
		iL = entrL.getKey();
	    if (iL != null) {
		currentKGlist = entrL.getValue();
		for (KnownGene KG : currentKGlist)
		    kgList.add(KG);
		leftpos = iL;
	    } /* Note that if iL==null then there are no more positions on 5'
	       * end of the chromosome, leftpos is now the lowest of this chromosome,
	       * just do nothing here.
	       */
	    Integer iR = null;
	    if (entrR != null)
		iR = entrR.getKey();
	    if (iR != null) {
		currentKGlist = entrR.getValue();
		for ( KnownGene KG : currentKGlist)
		    kgList.add(KG);
		rightpos = iR;
	    } /* Note that if iR==null, then there are no more positions on
	       * the 3' end of the chromosome, rightpos is thus the highest of
	       * this chromosome. Just do nothing here (the for loop will
	       * then get the rest of the leftpos and not do anything here).
	       */
	}
	
	
	/* When we get here, leftpos, midpos, and rightpos define a range of positions on this
	   Chromosome at which we want to search. midpos is the most likely location of the 
	   gene corresponding to our variant, but theoretically the genes corresponding to 
	   the variant could be located at other places in the range.We return kgList, an
	   array list of knownGene objects that surround the position of the variant, and
	   will now be used by the function getAnnotations to calculate the annotation of the
	   variant.*/
	//System.out.println(String.format("Left, mid, right: %d/%d/%d",leftpos,midpos,rightpos));
	return kgList;
    }

    /**
     * @return Number of genes contained in this chromosome.
     */
    public int getNumberOfGenes() { return this.n_genes; }

   


    /**
     * Just for refactor
     */
    public Annotation getAnnotation(int position,String ref, String alt) throws AnnotationException  { 
	return null; 
    }

    /**
     * Main entry point to getting Annovar-type annotations for a
     * variant identified by chromosomal coordinates. When we get to
     * this point, the client code has identified the right chromosome,
     * and we are provided the coordinates on that chromosome.
     * <P>
     * The strategy for finding annotations is based on the perl code in Annovar.
     * Roughly speaking, we take the following steps in this method in order to find out whether the
     * variant is genic or intergenic and identify the involved genes (which are stored in the {@link #geneTreeMap} object of this class).
     * Then, other functions are called to characterize the precise variant.
     * <OL>
     * <LI>Use the method {@link #getBinRange} to get a list of KnownGenes to be tested (Corresponds roughly to 
     {@code for my $nextbin ($bin1 .. $bin2)} in Annovar) </LI>
     * </OL>
     * @param position The start position of the variant on this chromosome
     * @param ref String representation of the reference sequence affected by the variant
     * @param alt String representation of the variant (alt) sequence
     * @return a list of {@link jannovar.annotation.Annotation Annotation} objects corresponding to the mutation described by the object 
     * (often just one annotation, but potentially multiple ones).
     */
    public AnnotationList getAnnotationList(int position,String ref, String alt) throws AnnotationException {
	
	KnownGene leftNeighbor=null; /* gene to 5' side of variant (may be null if variant lies within a gene) */
	KnownGene rightNeighbor=null; /* gene to 3' side of variant (may be null if variant lies within a gene) */
	/* Note, the following two variables are use to know when we can stop the search: we have already found
	   a 5' and a 3' neighboring gene surrounding the variant. Annovar goes 5' to 3', but we are going
	   centrifugally away from the position of the variant when deciding which gene to test next. Therefore,
	   we need a slightly different strategy to decide whedn to quit the search (In annovar, the search is
	   stopped when we have already found an intragenidc mutation and we hit a gene that is 3' to the variant).*/
	boolean foundFivePrimeNeighbor=false;
	boolean foundThreePrimeNeighbor=false;

	/* The following command "resets" the annovar object */
	this.annovarFactory.clearAnnotationLists();

	// Define start and end positions of variant
	int start = position;
	int end = start + ref.length() - 1;
	
	boolean foundgenic=false; //variant already found in genic region (between start and end position of a gene)
	
	/** Get KnownGenes that are located in vicinity of position. */
	ArrayList<KnownGene> candidateGenes = getBinRange(position);
	
	for (KnownGene kgl : candidateGenes) {
	    //System.out.println(String.format("Top of for loop: %S[%s][%c]", kgl.getName2(),kgl.getName(), kgl.getStrand()));
	    boolean currentGeneIsNonCoding=false; // in annovar: $current_ncRNA
	    String name = kgl.getKnownGeneID();
	    int txstart = kgl.getTXStart();
	    int txend   = kgl.getTXEnd();
	    int cdsstart = kgl.getCDSStart();
	    int cdsend = kgl.getCDSEnd();
	    int exoncount = kgl.getExonCount();
	    String name2 = kgl.getName2();
	   
	    /* ***************************************************************************************** *
	     * The following code block is executed if the variant has not hit a genic region yet and    *
	     * it basically updates information about the nearest 5' (left) and 3' (right) neighbor.     *
	     * This information is useful for "intergenic" variants.                                     *
	     * ***************************************************************************************** */
	    if (! foundgenic) {  //this variant has 
		// "start"  of variant is 3' to "txend" of this gene, thus the gene is a LEFT neighbor to the var
		if (kgl.isThreePrimeToGene(start)) {
		    if (leftNeighbor==null) {
			leftNeighbor=kgl;
		    } else { // already have a 3' neighbor. Get the closest one
			int dist = kgl.getDistanceToThreePrimeTerminus(start);
			int prev = leftNeighbor.getDistanceToThreePrimeTerminus(start);
			if (dist < prev)
			    leftNeighbor = kgl;
		    }
		}
		// "end" of variant is 5' to txStart of gene, thus the gene is a RIGHT neighbor to the var
		if (kgl.isFivePrimeToGene(end)) {
		    if (rightNeighbor == null) {
			rightNeighbor = kgl;
		    } else {
			int dist = kgl.getDistanceToFivePrimeTerminus(end);
			int prev = rightNeighbor.getDistanceToFivePrimeTerminus(end);
			if (dist < prev)
			    rightNeighbor = kgl;
		    }
		}
	    }
	    /* When we get here, leftNeighbor and rightNeighbor are the closest 5' and 3' genes to
	       the variant to date. They may be null if we have already found a "genic" variant. */
	    //System.out.println(String.format("\tbla %s, txStart:%d, about to check for 5'/3'/genic location:", 
	    // kgl.getName2(), kgl.getTXStart()));
	    if (kgl.isFivePrimeToGene(end)) {  // "end" of variant is 5' to "txstart" of gene
		//variant ---
		//gene		<-*----*->
		//System.out.println(String.format("\tbla Variant at %d is 5' to gene: %s",start,kgl.getName2()));
		foundThreePrimeNeighbor=true; /* gene is 3' neighbor */
		if (foundgenic) { continue; }
		/* We have already found a gene such that this variant is genic. */
		else if (foundFivePrimeNeighbor) continue; /* we have found genes outside of the variant on 5' and 3' sides */
		else continue; /* go to next round, continue search */
	    } else if (kgl.isThreePrimeToGene(start) ) {
		/* i.e., "start" is 3' to "txend" of gene */
		//System.out.println(String.format("\tbla Variant at %d is 3' to gene: %s",start,kgl.getName2()));
		foundFivePrimeNeighbor=true; /* gene is 5' neighbor to var */
		if (foundgenic) {
		    continue; 
		} else if (foundThreePrimeNeighbor) { 
		    //System.out.println("foudn 3' neighbior true");
		    continue;  /* we have found genes outside of the variant on 5' and 3' sides */
		}
		else {
		    //System.out.println("\tbla about to continue to next round");
		    continue;  /* go to next round, continue search */
		}
	    } else {
		/* We now must be in a genic region. Note that we will use a slightly
		 * different strategy from Annovar here. Do not distinguish between 
		 * coding and noncoding RNAs here, but do it in the following functions.
		 * Note: I changed the following commented out code. on 7 December 2012.*/
		/*
	
		if (! kgl.isCodingGene() ) {
		if (start >= txstart &&  start <= txend ||  
			end >= txstart &&  end <= txend      ||  
			start <= txstart && end >= txend) {     
			Annotation ann = Annotation.createNonCodingExonicRnaAnnotation(kgl,start,ref,alt); 
			annovar.addNonCodingExonicRnaAnnotation(ann);
			continue; 
			//foundgenic=true;
		    }
		    currentGeneIsNonCoding = true;
		    // TODO Annovar code sets cdsstart/cdsend to txstart/txend here.
		    // Do we need this?
		}
		End, section commented out on 7 December 2012
		*/
	
		if (kgl.isPlusStrand()) {	
		    getPlusStrandAnnotation(position,ref, alt, kgl);
		    if (annovarFactory.hasGenic()) {
			foundgenic=true;
		    }
		} else if (kgl.isMinusStrand()) {
		    getMinusStrandAnnotation(position,ref, alt, kgl);	
		    if (annovarFactory.hasGenic()) {
			foundgenic=true;
		    }
		}
	    }
      	}/* for (KnownGene kgl : candidateGenes) */
	/** If we arrive here and there are no annotations in the list, then
	    we should at least have a rightNeighbor and a leftNeighbor. 
	    We first check if one of these is upstream or downstream.
	    If not, then we have an intergenic variant. */
	if (annovarFactory.isEmpty()) {
	    if (leftNeighbor != null && leftNeighbor.isNearThreePrimeEnd(start,NEARGENE) ) {
		/** The following function creates an upstream or downstream annotation as appropriate. */
		Annotation ann = Annotation.createUpDownstreamAnnotation(leftNeighbor,start);
		annovarFactory.addUpDownstreamAnnotation(ann);
	    } 
	    
	    if (rightNeighbor != null && rightNeighbor.isNearFivePrimeEnd(end,NEARGENE)) {
		/** The following function creates an upstream or downstream annotation as appropriate. */
		    Annotation ann = Annotation.createUpDownstreamAnnotation(rightNeighbor,end);
		    annovarFactory.addUpDownstreamAnnotation(ann);
	    }
	    /* If we get here, and annotation_list is still empty, then the variant is not
	       nearby to any gene (i.e., it is not upstream/downstream). Therefore, the variant
	       is intergenic */
	    if (annovarFactory.isEmpty()) {
		Annotation ann = Annotation.createIntergenicAnnotation(leftNeighbor,rightNeighbor,start,end);
		annovarFactory.addIntergenicAnnotation(ann);
	    }
	}
	//annovar.debugPrint();
	return annovarFactory.getAnnotationList();
    }


    /**
     * Main entry point to getting Annovar-type annotations for a
     * variant identified by chromosomal coordinates for a KnownGene that
     * is transcribed from the plus strand. This could theoretically be
     * combined with the Minus strand functionalities, but separating them
     * makes things easier to comprehend and debug.
     * @param position The start position of the variant on this chromosome
     * @param ref String representation of the reference sequence affected by the variant
     * @param alt String representation of the variant (alt) sequence
     */
    public void getPlusStrandAnnotation(int position,String ref, String alt, KnownGene kgl)
	throws AnnotationException  {

	/*System.out.println(String.format("BLA, getPLusStrand for %s [%s] at position=%d, ref=%s, alt=%s",
	  kgl.getName2(),kgl.getName(),position,ref,alt)); */

	int txstart = kgl.getTXStart();
	int txend   = kgl.getTXEnd();
	int cdsstart = kgl.getCDSStart();
	int cdsend = kgl.getCDSEnd();
	int exoncount = kgl.getExonCount();
	String name2 = kgl.getName2(); /* the gene symbol */
	String name = kgl.getName(); /* the ucsc knowngene id */
	int start = position;
	int end = start + ref.length() - 1;

	int cumlenintron = 0; // cumulative length of introns at a given exon
	int cumlenexon=0; // cumulative length of exons at a given exon
	int rcdsstart=0; // start of CDS within reference RNA sequence.
	int rvarstart=-1; // start of variant within reference RNA sequence
	int rvarend=-1; //end of variant within reference RNA sequence
	boolean foundexonic=false; // have we found the variant to lie in an exon yet?
	rcdsstart = kgl.getRefCDSStart();

	for (int k=0; k< exoncount;++k) {
	    //System.out.println("BLA getPlusStrandCodingSequenceAnnotation exon " + k);
	    if (k>0)
		cumlenintron += kgl.getLengthOfIntron(k);
	    cumlenexon += kgl.getLengthOfExon(k);
	    if (cdsstart >= kgl.getExonStart(k) && cdsstart <= kgl.getExonEnd(k)) {
		/* "cdsstart" is thus contained within this exon */
		cumlenexon = kgl.getExonEnd(k) - cdsstart + 1;
	    }
	    /* 1) First check whether variant is a splice variant */
	    //System.out.println("BLA, About to check for splice for gene " + kgl.getName2());
	    //isSpliceVariantPositiveStrand(KnownGene kgl, int start, int end, String ref, String alt, int k) {
	    if (SpliceAnnotation.isSpliceVariantPlusStrand(kgl,start,end,ref,alt,k)) {
		Annotation ann  = SpliceAnnotation.getSpliceAnnotationPlusStrand(kgl,start,end,ref,alt,k,cumlenexon);
		if (kgl.isCodingGene()) {
		    annovarFactory.addExonicAnnotation(ann);
		} else {
		    annovarFactory.addNcRNASplicing(ann);
		}
		return; // we are done with this variant/KnownGene combination.
	    }
	    if (start < kgl.getExonStart(k)) {
		//System.out.println(String.format("BLA, start=%d, end=%d,exon[%d] start=%d for gene %s ",
		//start,end,k,kgl.getExonStart(k), kgl.getName2()));
		/* --------------------------------------------------------------------------- *
		 * The variant is not a splice mutation (because of the above code), and it    *
		 * begins before the start position of exon k. Therefore, there are several    *
		 * possibilities. 1) It overlaps with the start of exon k (then, we have that  *
		 * end >=exonstart(k). It could be in the 5'UTR, 3UTR, or a coding exon.       *
		 * --------------------------------------------------------------------------- */
		if (end >= kgl.getExonStart(k)) {	
		    /* Overlap: Variation starts 5' to exon and ends within exon */ 
		    /* 1) Get the start position of the variant w.r.t. transcript (rvarstart) */
		    /* $rvarstart = $exonstart[$k]-$txstart-$lenintron+1; */
		    rvarstart = kgl.getExonStart(k) - kgl.getTXStart() -  cumlenintron + 1;
		    // System.out.println("1 HERE rvarstart os " + rvarstart);
		    /* 2) Get the end position of the variant w.r.t. the transcript (rvarend) */
		    rvarend   = kgl.getRVarEnd(end, k, cumlenintron);
		    if (end < cdsstart && kgl.isCodingGene()) {	
			/* 3) Variant disrupts/changes 5' UTR region.
			 * Rarely, if the 5' UTR is also separated by introns, the variant 
			 * is more complex.
			 #query  ----
			 #gene     <--*---*->
			*/
			Annotation ann = Annotation.createUTR5Annotation(kgl,rvarstart,ref,alt);
			annovarFactory.addUTR5Annotation(ann);
			
			  /* Annovar: $utr5{$name2}++;
			     positive strand for UTR5 */
		    } else if (start > cdsend  && kgl.isCodingGene()) {
			/* 4) The variant disrupts/changes 3' UTR region 
			   #query             ----
			   #gene     <--*---*->
			*/
			Annotation ann = UTR3Annotation.getUTR3Annotation(kgl,start,end,ref,alt);
			annovarFactory.addUTR3Annotation(ann);
			
			/* positive strand for UTR3 */
		    } else {	
			/*  5) If we get here, the variant is located within an exon.
			 *     Annovar: $exonic{$name2}++; 								
			 *     Annovar:	not $current_ncRNA and $obs and 
			 *     push @{$refseqvar{$name}}, [$rcdsstart, $rvarstart, $rvarend, '+', $i, $k+1, $nextline];
			 */
			/* Note k in the following is the number (zero-based) of affected exon */
			annotateExonicVariants(rvarstart,rvarend,start,end,ref,alt,k,kgl);
		    }
		    break; /* break out of for loop of exons (k) */
		    
		} else if (k>0 && start > kgl.getExonEnd(k-1)) {  
		    /* ----------------------------------------------------------------------- *
		     * if we get here, then start < exonstart(k) and also start > exonend(k-1) *
		     * This means that the variant is INTRONIC of a coding or noncoding RNA    *
		     * ----------------------------------------------------------------------- */
		    Annotation ann = null;
		    if (kgl.isCodingGene() )
			 ann = Annotation.createIntronicAnnotation(name2);
		     else
			 ann = Annotation.createNoncodingIntronicAnnotation(name2);
		    annovarFactory.addIntronicAnnotation(ann);
		    return; /* Done with this annotation */
		}
	    } /* end; if (start < kgl.getExonStart(k)) */ 
	    else if (start <= kgl.getExonEnd(k)) {
		/* ----------------------------------------------------------------------- *
		 * If we get here, then the start >= exonstart(k) and start <=exonend(k).  *
		 * Thus, the start of the variant is located within exon k. The following  *
		 * code then calculates the start (rvarstart) and end (rvarend) position   *
		 * of the variant within the RNA.                                          *
		 * ----------------------------------------------------------------------- */
		/* $rvarstart = $start-$txstart-$lenintron+1; */
		rvarstart = start - kgl.getTXStart() - cumlenintron + 1;
		rvarend = kgl.getRVarEnd(end, k,cumlenintron);
		/* ----------------------------------------------------------------------- *
		 * We now search for the end of the mutation. We know that we have found   *
		 * the end when 1) end < exonstart(m) TODO CHEKC THIS or 2) end<exonend(m) *
		 * In the latter case, both the start and the end of the mutation are      *
		 * located within exon k. We can then break out of the for loop after we   *
		 * have calculated rvarstart and rvarend.                                  *
		 * ----------------------------------------------------------------------- */
		for (int m=k;m<kgl.getExonCount();++m) {
		    if (m>k) {
			cumlenintron += kgl.getLengthOfIntron(m);
		    }
		    if (end < kgl.getExonStart(m)) {
			//#query              ------
			//#gene     <--**---******---****---->
			rvarend = kgl.getExonEnd(m-1) - kgl.getTXStart() - cumlenintron + 1 + kgl.getLengthOfIntron(m-1);
			//$rvarend = $exonend[$m-1]-$txstart-$lenintron+1 + ($exonstart[$m]-$exonend[$m-1]-1);
			break;
		    } else if (end < kgl.getExonEnd(m)) {
			//#query           -----------
			//#gene     <--**---******---****---->
			//$rvarend = $end-$txstart-$lenintron+1;
			rvarend = end -  kgl.getTXStart() - cumlenintron + 1;
			break; // last;
		    }
		}
		/*
		  if (not defined $rvarend) {
			$rvarend = $txend-$txstart-$lenintron+1;		
			#if this value is longer than transcript length, it suggest whole gene deletion
		}
		*/
		if (rvarend < 0) { // i.e., uninitialized 
		    rvarend = end - kgl.getTXStart() - cumlenintron + 1;
		}
		

		/* ------------------------------------------------------------------------- *
		 * If we get here, the variant is located somewhere in a exon. There are     *
		 * several possibilities: 1) Noncoding RNA, 2) UTR5, 3) UTR3, 4) Exonic in   *
		 * a coding gene within the actual coding sequence (not UTR).                *
		 * ------------------------------------------------------------------------- */
		if (kgl.isNonCodingGene()) {
		    Annotation ann = Annotation.createNonCodingExonicRnaAnnotation(kgl, rvarstart,ref,alt);
		    annovarFactory.addNonCodingRNAExonicAnnotation(ann);
		} else	if (end < cdsstart ) {					
		    /* #usually disrupt/change 5' UTR region, unless the UTR per se is also separated by introns 
		     * #query  ----
		     * #gene     <--*---*->
		     * Annovar: $utr5{$name2}++; #positive strand for UTR5
		     */
		    Annotation ann = Annotation.createUTR5Annotation(kgl,rvarstart,ref,alt);
		    annovarFactory.addUTR5Annotation(ann);
		    
		} else if (start > cdsend) {
		    /* #query             ----
		     * #gene     <--*---*->
		     * Annovar: $utr3{$name2}++; #positive strand for UTR3
		     */
		    Annotation ann = UTR3Annotation.getUTR3Annotation(kgl,start,end,ref,alt);
		    annovarFactory.addUTR3Annotation(ann);
		} else {
		    /* Note that the following function adds annotations to annovar */
		    annotateExonicVariants(rvarstart,rvarend,start,end,ref,alt,k+1,kgl);
		}
	    }
	} /* iterator over exons */
    }


     /**
     * Main entry point to getting Annovar-type annotations for a
     * variant identified by chromosomal coordinates for a KnownGene that
     * is transcribed from the minus strand. This could theoretically be
     * combined with the Minus strand functionalities, but separating them
     * makes things easier to comprehend and debug.
     * @param position The start position of the variant on this chromosome
     * @param ref String representation of the reference sequence affected by the variant
     * @param alt String representation of the variant (alt) sequence
     */
    public void getMinusStrandAnnotation(int position,String ref, String alt, KnownGene kgl)
	throws AnnotationException  {
	
	/*System.out.println(String.format("BLA, getMinusString: %s[%s], position=%d, ref=%s, alt=%s",
	  kgl.getName2(),kgl.getName() ,position,ref,alt));   */

	int txstart = kgl.getTXStart();
	int txend   = kgl.getTXEnd();
	int cdsstart = kgl.getCDSStart();
	int cdsend = kgl.getCDSEnd();
	int exoncount = kgl.getExonCount();
	String name2 = kgl.getName2(); /* the gene symbol */
	String name = kgl.getName(); /* the ucsc knowngene id */
	int start = position;
	int end = start + ref.length() - 1;

	int cumlenintron = 0; // cumulative length of introns at a given exon
	int cumlenexon=0; // cumulative length of exons at a given exon
	int rcdsstart=0; // start of CDS within reference RNA sequence.
	int rvarstart=-1; // start of variant within reference RNA sequence
	int rvarend=-1; //end of variant within reference RNA sequence
	boolean foundexonic=false; // have we found the variant to lie in an exon yet?
	rcdsstart = kgl.getRefCDSStart();

	/*************************************************************************************** *
	 * Iterate over all exons of the gene. Start with the 3'-most exon, which is the first   *
	 * exon for genes transcribed from the minus strand.                                     *
	 * ************************************************************************************* */
	for (int k = exoncount-1; k>=0; k--) {
	    if (k < exoncount-1) {
		cumlenintron += kgl.getExonStart(k+1)-kgl.getExonEnd(k)-1;
	    }
	    cumlenexon +=  kgl.getExonEnd(k)-kgl.getExonStart(k)+1;
	    if (cdsend <= kgl.getExonEnd(k) ) {	 // calculate CDS start accurately by considering intron length
		rcdsstart = txend-cdsend-cumlenintron+1;
		if (cdsend >= kgl.getExonStart(k)) {  //CDS start within this exon
		    cumlenexon = cdsend-kgl.getExonStart(k)+1;
		} 
	    }

	    /* 1) First check whether variant is a splice variant */
	    //System.out.println("BLA, About to check for splice for gene " + kgl.getName2());
	    if (SpliceAnnotation.isSpliceVariantMinusStrand(kgl,start,end,ref,alt,k)) {
		Annotation ann  = SpliceAnnotation.getSpliceAnnotationMinusStrand(kgl,start,end,ref,alt,k,cumlenexon);
		if (kgl.isCodingGene()) {
		    annovarFactory.addExonicAnnotation(ann);
		} else {
		    annovarFactory.addNcRNASplicing(ann);
		}
		return; /* We are done with this annotation. */
	    }
	    /* --------------------------------------------------------------------------- *
	     * The variant is not a splice mutation (because of the above code), and it    *
	     * begins after the end position of exon k (on the minus strand. Therefore,    *
	     * there are several possibilities. 1) It overlaps with the end of exon k      *
	     * (then, we have that start >=exonend(k). It could be in the 5'UTR, 3UTR, or  *
	     * a coding exon.                                                              *
	     * --------------------------------------------------------------------------- */
	    if (end > kgl.getExonEnd(k)) {
		if (start <= kgl.getExonEnd(k)) {
		     /* Overlap: Variation starts 5' to exon and ends within exon */ 
		    rvarstart =  kgl.getTXEnd() - kgl.getExonEnd(k)  -  cumlenintron + 1;
		    //  $rvarstart = $txend-$exonend[$k]-$lenintron+1;
		    for ( int m=k;m>=0;m--) {
			if (m<k)
			    cumlenintron += kgl.getExonStart(m+1) - kgl.getExonEnd(m) - 1;
			// $m < $k and $lenintron += ($exonstart[$m+1]-$exonend[$m]-1);
			if (start > kgl.getExonEnd(m)) {
			    //query           --------
			    //gene     <--**---******---****---->
			    // $rvarend = $txend-$exonstart[$m+1]+1-$lenintron + ($exonstart[$m+1]-$exonend[$m]-1);	#fixed this 2011feb18
			    rvarend = kgl.getTXEnd() - kgl.getExonStart(m+1) + 1 - cumlenintron + 
				(kgl.getExonStart(m+1) - kgl.getExonEnd(m) - 1);
			    break;
			} else if (start >= kgl.getExonStart(m)) {
			    //query               ----
			    //gene     <--**---******---****---->
			    //$rvarend = $txend-$start-$lenintron+1;
			    rvarend = kgl.getTXEnd() - start - cumlenintron + 1;
			    break;
			}
		    }
		    if (rvarend < 0) {
			//if rvarend is not found, then the whole tail of gene is covered
			rvarend = kgl.getTXEnd() - kgl.getTXStart() - cumlenintron + 1;
		    }
		    /* ------------------------------------------------------------- *
		     * When we get here, the variant overlaps an exon. It can be an  *
		     * exon of a noncoding gene, a UTR5, UTR3, or a CDS exon.        *
		     * ------------------------------------------------------------- */
		    if (kgl.isNonCodingGene()) {
			Annotation ann = Annotation.createNonCodingExonicRnaAnnotation(kgl,rvarstart,ref, alt);
			annovarFactory.addNonCodingRNAExonicAnnotation(ann);
			return;
		    } else if (end < cdsstart ) {
			//query  ----
			//gene     <--*---*->
			// Note this is UTR3 on negative strand
			Annotation ann = UTR3Annotation.getUTR3Annotation(kgl,start,end,ref,alt);
			annovarFactory.addUTR3Annotation(ann);
			return; /* done with this annotation. */
		    } else if (start > cdsend) {
			//query             ----
			//gene     <--*---*->
			// Note this is UTR5 on negative strand
			Annotation ann = Annotation.createUTR5Annotation(kgl,rvarstart,ref,alt);
			annovarFactory.addUTR5Annotation(ann);
			return; /* done with this annotation. */
		    } 	else {
			/* ------------------------------------------------------------- *
			 * If we get here, the variant is located within a CDS exon.     *
			 * In difference to annovar, we do not distinguish here whether  *
			 * the variant is coding or noncoding, that will be done in the  *
			 * following function.  Note k in the following is the number    *
			 * (zero-based) of affected exon                                 *
			 * ------------------------------------------------------------- */
			/*** 
			 * Annovar for minus strand:
			 * $exonic{$name2}++;
			 * not $current_ncRNA and $obs and 
			 push @{$refseqvar{$name}}, [$rcdsstart, $rvarstart, $rvarend, '-', $i, @exonstart-$k, $nextline];
			 compare to 
			 push @{$refseqvar{$name}}, [$rcdsstart, $rvarstart, $rvarend, '+', $i, $k+1, $nextline];	
			
			*/
			annotateExonicVariants(rvarstart,rvarend,start,end,ref,alt,k,kgl);
			return; /* done with this annotation. */
		    }
		} else if (k < kgl.getExonCount() -1 && end < kgl.getExonStart(k+1)) {
		    //System.out.println("- gene intron kgl=" + kgl.getName2() + ":" + kgl.getName());
		     Annotation ann = null;
		     if (kgl.isCodingGene() )
			 ann = Annotation.createIntronicAnnotation(name2);
		     else
			 ann = Annotation.createNoncodingIntronicAnnotation(name2);
		     annovarFactory.addIntronicAnnotation(ann);
		     return; /* done with this annotation. */
		} 
	    } /* end; if (end > kgl.getExonEnd(k)) */ 
	    else if (end >= kgl.getExonStart(k)) {
		//rvarstart is with respect to cDNA sequence (so rvarstart corresponds to end of variants)
		rvarstart = txend-end-cumlenintron+1;	
		for (int m = k; m >= 0; m--) {
		    if (m < k)
			cumlenintron += kgl.getExonStart(m+1)-kgl.getExonEnd(m)-1;// length of intron
		    if (start > kgl.getExonEnd(m)) {
			//query           ----
			//gene     <--**---******---****---->
			rvarend = txend- kgl.getExonStart(m+1)+1 - cumlenintron + (kgl.getExonStart(m+1)- kgl.getExonEnd(m)-1);	
			break; //finish the cycle of counting exons!!!!!
		    } else if (start >= kgl.getExonStart(m)) { //the start is right located within exon
			//query        -------
			//gene     <--**---******---****---->
			rvarend = txend-start-cumlenintron+1;
			break; //finish the cycle
		    }
		}
		if (rvarend<0) { // i.e., rvarend not initialized, then the whole tail of gene is covered
		    rvarend = txend-txstart-cumlenintron+1;
		}
	
		/* ------------------------------------------------------------------------- *
		 * If we get here, the variant is located somewhere in a exon. There are     *
		 * several possibilities: 1) Noncoding RNA, 2) UTR5, 3) UTR3, 4) Exonic in   *
		 * a coding gene within the actual coding sequence (not UTR).                *
		 * ------------------------------------------------------------------------- */
		if (kgl.isNonCodingGene()) {
		    Annotation ann = Annotation.createNonCodingExonicRnaAnnotation(kgl, rvarstart,ref,alt);
		    annovarFactory.addNonCodingRNAExonicAnnotation(ann);
		    return; /* done with this annotation. */
		} else if (end < cdsstart) { 
		    /* Negative strand, mutation located 5' to CDS start, i.e., 3UTR */
		    //query  ----
		    //gene     <--*---*->
		    Annotation ann = UTR3Annotation.getUTR3Annotation(kgl,start,end,ref,alt);
		    annovarFactory.addUTR3Annotation(ann);
		    return; /* done with this annotation. */
		} else if (start > cdsend) {
		    /* Negative strand, mutation located 3' to CDS end, i.e., 5UTR */
		    //query             ----
		    //gene     <--*---*->
		    //System.out.println(String.format("start:%d, cdsend:%d, gene:%s",start,cdsend,kgl.getName2()));
		    Annotation ann = Annotation.createUTR5Annotation(kgl,rvarstart,ref,alt);
		    annovarFactory.addUTR5Annotation(ann);
		    //$utr5{$name2}++;		#negative strand for UTR3
		} else {
		    annotateExonicVariants(rvarstart,rvarend,start,end,ref,alt,exoncount-k,kgl);
		}
	    }
	} /* iterator over exons */
    }




		  
    /**
     * This method corresponds to Annovar function 
     * {@code sub annotateExonicVariants} {
     * 	my ($refseqvar, $geneidmap, $cdslen, $mrnalen) = @_;
     *   (...)
     * <P>
     * The variable $refseqhash = readSeqFromFASTADB ($refseqvar); in
     * annovar holds cDNA sequences of the mRNAs. In this implementation,
     * the KnownGene objects already have this information.
     * <P>
     * Finally, the $refseqvar in Annovar has the following pieces of information
     *  {@code my ($refcdsstart, $refvarstart, $refvarend, $refstrand, $index, $exonpos, $nextline) = @{$refseqvar->{$seqid}->[$i]};}
     * Note that refcdsstart and refstrand are contained in the KnownGene objects
     * $exonpos is the number (zero-based) of the exon in which the variant was found.
     * $nextline is the entire Annovar-formated line with information about the variant.
     * In contrast to annovar, this function does one annotation at a time
     * Note that the information in $geneidmap, cdslen, and $mrnalen
     * is contained within the KnownGene objects already
     * @param refvarstart The start position of the variant with respect to the CDS of the mRNA
     * @param refvarend The end position of the variant with respect to the CDS of the mRNA
     * @param start chromosomal start position of variant
     * @param end chromosomal end position of variant
     * @param ref sequence of reference
     * @param var sequence of variant (in annovar: $obs)
     * @param exonNumber Number (zero-based) of affected exon.
     * @param kgl Gene in which variant was localized to one of the exons 
     */
    private void annotateExonicVariants(int refvarstart, int refvarend, 
					int start, int end, String ref, 
					String var, int exonNumber, KnownGene kgl) throws AnnotationException {
	
	
	/*System.out.println("bla annotateExonicVariants for KG=" + kgl.getName2() + "/" + kgl.getName());
	   System.out.println("******************************");
	   System.out.println(String.format("\trefvarstart: %d\trefvarend: %d",refvarstart,refvarend));
	   System.out.println(String.format("\tstart: %d\tend: %d",start,end));
	   System.out.println(String.format("\tref=%s\tvar=%s\texon=%d",ref,var,exonNumber));
	   System.out.println("******************************"); */
	/* frame_s indicates frame of variant, can be 0, i.e., on first base of codon, 1, or 2 */
	int frame_s = ((refvarstart-kgl.getRefCDSStart() ) % 3); /* annovar: $fs */
	int frame_end_s = ((refvarend-kgl.getRefCDSStart() ) % 3); /* annovar: $end_fs */
	int refcdsstart = kgl.getRefCDSStart();

	// Needed to complete codon following end of multibase ref seq.
	/* The following checks for database errors where the position of the variant in
	 * the reference sequence is given as longer the actual length of the transcript.*/
	if (refvarstart - frame_s - 1 > kgl.getActualSequenceLength() ) {
	    String s = String.format("%s, refvarstart=%d, frame_s=%d, seq len=%d\n",
				     kgl.getKnownGeneID(), refvarstart,frame_s,kgl.getActualSequenceLength());
	    Annotation ann = Annotation.createErrorAnnotation(s);
	    this.annovarFactory.addErrorAnnotation(ann);
	}
	/*
	  @wtnt3 = split (//, $wtnt3);
	  if (@wtnt3 != 3 and $refvarstart-$fs-1>=0) { 
	  #some times there are database annotation errors (example: chr17:3,141,674-3,141,683), 
	  #so the last coding frame is not complete and as a result, the cDNA sequence is not complete
	  $function->{$index}{unknown} = "UNKNOWN";
	  next;
	  }
	*/
	// wtnt3 represents the three nucleotides of the wildtype codon.
	String wtnt3 = kgl.getWTCodonNucleotides(refvarstart, frame_s);
	if (wtnt3==null) {
	    /* This can happen is the KnownGene.txt gene definition indicates that the mRNA sequence is
	       longer than the actual sequence contained in KnownGeneMrna.txt. This probably reflects
	       and error in genome annotations. */
	    String s = String.format("Discrepancy between mRNA length and genome annotation ",
				     "(variant at pos. %d of transcript with mRNA length %d):%s[%s]", 
				     refvarstart,kgl.getMRNALength(), kgl.getKnownGeneID(), kgl.getName());
	    Annotation ann = Annotation.createErrorAnnotation(s);
	    this.annovarFactory.addErrorAnnotation(ann);
	    return; /* Probably reflects some database error. */

	}
	/* wtnt3_after = Sequence of codon right after the variant. 
	   We may not need this, it was used for padding in annovar */
	String wtnt3_after = kgl.getWTCodonNucleotidesAfterVariant(refvarstart,frame_s);
	/* the following checks some  database annotation errors (example: chr17:3,141,674-3,141,683), 
	 * so the last coding frame is not complete and as a result, the cDNA sequence is not complete */
	if (wtnt3.length() != 3 && refvarstart - frame_s - 1 >= 0) {
	    String s = String.format("%s, wtnt3-length: %d", kgl.getKnownGeneID(), wtnt3.length());
	    Annotation ann = Annotation.createErrorAnnotation(s);
	    this.annovarFactory.addErrorAnnotation(ann);
	    return; /* Probably reflects some database error. */
	}
	/*annovar line 1079 */
	if (kgl.isMinusStrand()) {
	    var = revcom(var);
	    ref = revcom(ref);
	}
	//System.out.println("wtnt3=" + wtnt3);
	if (start == end) { /* SNV or insertion variant */
	    if (ref.equals("-") ) {  /* "-" stands for an insertion at this position */	
		Annotation  insrt = InsertionAnnotation.getAnnotationPlusStrand(kgl,frame_s, wtnt3,wtnt3_after,ref,
										var,refvarstart,exonNumber);
		this.annovarFactory.addExonicAnnotation(insrt);
	    } else if (var.equals("-") ) { /* i.e., single nucleotide deletion */
		Annotation dlt = DeletionAnnotation.getAnnotationSingleNucleotidePlusStrand(kgl,frame_s, wtnt3,wtnt3_after,
											    ref, var,refvarstart,exonNumber);
		this.annovarFactory.addExonicAnnotation(dlt);
	    } else if (var.length()>1) {
		Annotation blck = BlockSubstitution.getAnnotationPlusStrand(kgl,frame_s, wtnt3, wtnt3_after,
									    ref,var,refvarstart, refvarend, 
									    exonNumber);
		this.annovarFactory.addExonicAnnotation(blck);
	    } else {
		//System.out.println("!!!!! SNV ref=" + ref + " var=" + var);
		Annotation mssns = SingleNucleotideSubstitution.getAnnotation(kgl,frame_s, frame_end_s,wtnt3,
									      ref, var,refvarstart,exonNumber);
		this.annovarFactory.addExonicAnnotation(mssns);
	    }
	} /* if (start==end) */
	else if (var.equals("-")) {
	    /* If we get here, then the start position of the variant is not the same as the end position,
	     * i.e., start==end is false, and the variant is "-"; thus there is as
	     * 	deletion variant involving several nucleotides.
	     */
	    Annotation dltmnt = 
		DeletionAnnotation.getAnnotationBlockPlusStrand(kgl, frame_s, wtnt3,wtnt3_after,
								ref, var, refvarstart, refvarend, exonNumber);
	    
	    this.annovarFactory.addExonicAnnotation(dltmnt);
	} else {
	    /* If we get here, then start==end is false and the variant sequence is not "-",
	     * i.e., it is not a deletion. Thus, we have a block substitution event.
	     */
	    String canno = String.format("%s:exon%d:c.%d_%d%s",kgl.getName(),exonNumber,refvarstart-refcdsstart+1,
					 refvarend-refcdsstart+1,var);
	    // 	$canno = "c." . ($refvarstart-$refcdsstart+1) . "_" . ($refvarend-$refcdsstart+1) . "$obs";
	    if ((refvarend - refvarstart+ 1 - var.length())%3==0) {
		/* Non-frameshift substitution */
		Annotation ann = Annotation.createNonFrameShiftSubstitionAnnotation(kgl,refvarstart,canno);
		this.annovarFactory.addExonicAnnotation(ann);
	    } else {
		/* frameshift substitution */
		Annotation ann = Annotation.createFrameShiftSubstitionAnnotation(kgl,refvarstart,canno);
		this.annovarFactory.addExonicAnnotation(ann);
	    }
	}
	return;
    }
    

 /**
     * This method corresponds to Annovar function 
     * {@code sub annotateExonicVariants} {
     * 	my ($refseqvar, $geneidmap, $cdslen, $mrnalen) = @_;
     *  It is called for exonic variants on the minus strand.
     * <P>
     * The variable $refseqhash = readSeqFromFASTADB ($refseqvar); in
     * annovar holds cDNA sequences of the mRNAs. In this implementation,
     * the KnownGene objects already have this information. 
     * <P>
     * Finally, the $refseqvar in Annovar has the following pieces of information
     *  {@code my ($refcdsstart, $refvarstart, $refvarend, $refstrand, $index, $exonpos, $nextline) = @{$refseqvar->{$seqid}->[$i]};}
     * Note that refcdsstart and refstrand are contained in the KnownGene objects
     * $exonpos is the number (zero-based) of the exon in which the variant was found.
     * $nextline is the entire Annovar-formated line with information about the variant.
     * In contrast to annovar, this function does one annotation at a time
     * Note that the information in $geneidmap, cdslen, and $mrnalen
     * is contained within the KnownGene objects already
     * @param refvarstart The start position of the variant with respect to the CDS of the mRNA
     * @param refvarend The end position of the variant with respect to the CDS of the mRNA
     * @param start chromosomal start position of variant
     * @param end chromosomal end position of variant
     * @param ref sequence of reference
     * @param var sequence of variant (in annovar: $obs)
     * @param exonNumber Number (zero-based) of affected exon.
     * @param kgl Gene in which variant was localized to one of the exons 
     */
    private void OLDPROBABLY_DELETE_annotateExonicVariantsMinusStrand(int refvarstart, int refvarend, 
					int start, int end, String ref, 
					String var, int exonNumber, KnownGene kgl) throws AnnotationException {
	
	
	/* System.out.println("bla annotateExonicVariants for KG=" + kgl.getName2() + "/" + kgl.getName());
	   System.out.println("******************************");
	   System.out.println(String.format("\trefvarstart: %d\trefvarend: %d",refvarstart,refvarend));
	   System.out.println(String.format("\tstart: %d\tend: %d",start,end));
	   System.out.println(String.format("\tref=%s\tvar=%s\texon=%d",ref,var,exonNumber));
	   System.out.println("******************************"); */
	/* frame_s indicates frame of variant, can be 0, i.e., on first base of codon, 1, or 2 */
	int frame_s = ((refvarstart-kgl.getRefCDSStart() ) % 3); /* annovar: $fs */
	int frame_end_s = ((refvarend-kgl.getRefCDSStart() ) % 3); /* annovar: $end_fs */
	// Needed to complete codon following end of multibase ref seq.
	/* The following checks for database errors where the position of the variant in
	 * the reference sequence is given as longer the actual length of the transcript.*/
	if (refvarstart - frame_s - 1 > kgl.getActualSequenceLength() ) {
	    String s = String.format("%s, refvarstart=%d, frame_s=%d, seq len=%d\n",
				     kgl.getKnownGeneID(), refvarstart,frame_s,kgl.getActualSequenceLength());
	    Annotation ann = Annotation.createErrorAnnotation(s);
	    this.annovarFactory.addErrorAnnotation(ann);
	}
	/*
	  @wtnt3 = split (//, $wtnt3);
	  if (@wtnt3 != 3 and $refvarstart-$fs-1>=0) { 
	  #some times there are database annotation errors (example: chr17:3,141,674-3,141,683), 
	  #so the last coding frame is not complete and as a result, the cDNA sequence is not complete
	  $function->{$index}{unknown} = "UNKNOWN";
	  next;
	  }
	*/
	// wtnt3 represents the three nucleotides of the wildtype codon.
	String wtnt3 = kgl.getWTCodonNucleotides(refvarstart, frame_s);
	/* wtnt3_after = Sequence of codon right after the variant. 
	   We may not need this, it was used for padding in annovar */
	String wtnt3_after = kgl.getWTCodonNucleotidesAfterVariant(refvarstart,frame_s);
	/* the following checks some  database annotation errors (example: chr17:3,141,674-3,141,683), 
	 * so the last coding frame is not complete and as a result, the cDNA sequence is not complete */
	if (wtnt3.length() != 3 && refvarstart - frame_s - 1 >= 0) {
	    String s = String.format("%s, wtnt3-length: %d", kgl.getKnownGeneID(), wtnt3.length());
	    Annotation ann = Annotation.createErrorAnnotation(s);
	    this.annovarFactory.addErrorAnnotation(ann);
	}
	/*annovar line 1079 */
	if (kgl.isMinusStrand()) {
	    var = revcom(var);
	    ref = revcom(ref);
	}
	//System.out.println("wtnt3=" + wtnt3);
	if (start == end) { /* SNV or insertion variant */
	    if (ref.equals("-") ) {  /* "-" stands for an insertion at this position */	
		Annotation  insrt = InsertionAnnotation.getAnnotationPlusStrand(kgl,frame_s, wtnt3,wtnt3_after,ref,
										var,refvarstart,exonNumber);
		this.annovarFactory.addExonicAnnotation(insrt);
	    } else if (var.equals("-") ) { /* i.e., single nucleotide deletion */
		Annotation dlt = DeletionAnnotation.getAnnotationSingleNucleotidePlusStrand(kgl,frame_s, wtnt3,wtnt3_after,
											    ref, var,refvarstart,exonNumber);
		this.annovarFactory.addExonicAnnotation(dlt);
	    } else if (var.length()>1) {
		Annotation blck = BlockSubstitution.getAnnotationPlusStrand(kgl,frame_s, wtnt3, wtnt3_after,
									    ref,var,refvarstart, refvarend, 
									    exonNumber);
		this.annovarFactory.addExonicAnnotation(blck);
	    } else {
		//System.out.println("!!!!! SNV ref=" + ref + " var=" + var);
		Annotation mssns = SingleNucleotideSubstitution.getAnnotation(kgl,frame_s, frame_end_s,wtnt3,
									      ref, var,refvarstart,exonNumber);
		this.annovarFactory.addExonicAnnotation(mssns);
	    }
	} /* if (start==end) */
	else if (var.equals("-")) {
	    Annotation dltmnt = 
		DeletionAnnotation.getAnnotationBlockPlusStrand(kgl, frame_s, wtnt3,wtnt3_after,
								ref, var, refvarstart, refvarend, exonNumber);
	    
	    this.annovarFactory.addExonicAnnotation(dltmnt);
	}   
	return;
    }
    
    
    /**
     * Return the reverse complement version of a DNA string in upper case.
     * Note that no checking is done in this code since the parse code checks
     * for valid DNA and upper-cases the input. This code will break if these
     * assumptions are not valid.
     * @param sq original, upper-case cDNA string
     * @return reverse complement version of the input string sq.
     */
    private String revcom(String sq) {
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
/* EoF*/
