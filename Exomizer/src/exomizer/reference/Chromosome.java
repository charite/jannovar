package exomizer.reference;


import java.util.TreeMap;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;


import exomizer.reference.KnownGene;
import exomizer.reference.Translator;
import exomizer.exception.AnnotationException;

/** The following classes contain static functions used to calculate the 
    annotations for certain classes of mutation. These functions are put into
    separate classes just to keep things tidy and comprehensible in this class.
*/
import exomizer.annotation.DeletionAnnotation;
import exomizer.annotation.InsertionAnnotation;
import exomizer.annotation.SingleNucleotideSubstitution;
import exomizer.annotation.BlockSubstitution;
import exomizer.annotation.SpliceAnnotation;


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
 * Note that the key of the tree map corresponds to the 5' most position of the KnownGene. The value is a list
 * (ArrayList) of {@link exomizer.reference.KnownGene KnownGene} objects. 
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
 * @version 0.03 (Nov. 15, 2012)
 */
public class Chromosome {
    /** Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. 
     TODO. Add more flexible way of dealing with scaffolds etc.*/
    private byte chromosome;
    /** Alternative String for Chromosome. Use for scaffolds and "random" chromosomes. TODO: Refactor */
    private String chromosomeString=null;
    /** TreeMap with all of the genes ({@link exomizer.reference.KnownGene KnownGene} objects) of this chromosome. The key is an
     Integer value representing the transcription start site (txstart) of the transcript. Note that we need to
     use an Array of KnownGenes because there can be multiple KnownGenes that share the same transcription start site.
     (e.g., multiple isoforms of the same gene).*/
    private TreeMap<Integer,ArrayList<KnownGene>> geneTreeMap=null;
    /** Total number of KnownGenes on the chromosome including multiple transcripts of the same gene. */
    private int n_genes;
    /** The number of keys (gene 5' positions) to search in either direction. */
    private static final int SPAN = 5;
    /** The distance threshold in nucleotides for calling a variant upstream/downstream to a gene, */
    private static final int NEARGENE = 1000;
  
    /** Class object encapsulating rules to translate DNA. */
    private Translator translator = null;
    

    public Chromosome(byte c) {
	this.chromosome = c;
	this.geneTreeMap = new TreeMap<Integer,ArrayList<KnownGene>>();
	this.translator = Translator.getTranslator();
	this.n_genes=0;
    }

    /**
     * Add a gene model to this chromosome. 
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
     * @return a list of {@link exomizer.reference.Annotation Annotation} objects corresponding to the mutation described by the object 
     * (often just one annotation, but potentially multiple ones).
     */
    public ArrayList<Annotation> getAnnotation(int position,String ref, String alt) throws AnnotationException {
	
	KnownGene leftNeighbor=null; /* gene to 5' side of variant (may be null if variant lies within a gene) */
	KnownGene rightNeighbor=null; /* gene to 3' side of variant (may be null if variant lies within a gene) */
	/* Note, the following two variables are use to know when we can stop the search: we have already found
	   a 5' and a 3' neighboring gene surrounding the variant. Annovar goes 5' to 3', but we are going
	   centrifugally away from the position of the variant when deciding which gene to test next. Therefore,
	   we need a slightly different strategy to decide whedn to quit the search (In annovar, the search is
	   stopped when we have already found an intragenidc mutation and we hit a gene that is 3' to the variant).*/
	boolean foundFivePrimeNeighbor=false;
	boolean foundThreePrimeNeighbor=false;

	AnnotatedVar annovar = new AnnotatedVar();
	
	ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();

	// Define start and end positions of variant
	int start = position;
	int end = start + ref.length() - 1;
	
	boolean foundgenic=false; //variant already found in genic region (between start and end position of a gene)
	
	/** Get KnownGenes that are located in vicinity of position. */
	ArrayList<KnownGene> candidateGenes = getBinRange(position);
	
	for (KnownGene kgl : candidateGenes) {
	    boolean currentGeneIsNonCoding=false; // in annovar: $current_ncRNA
	    // 	($name, $dbstrand, $txstart, $txend, $cdsstart, $cdsend, $exonstart, $exonend, $name2)
	    String name = kgl.getKnownGeneID();
	    int txstart = kgl.getTXStart();
	    int txend   = kgl.getTXEnd();
	    int cdsstart = kgl.getCDSStart();
	    int cdsend = kgl.getCDSEnd();
	    int exoncount = kgl.getExonCount();
	    String name2 = kgl.getName2();
	    //System.out.println("Bla LOOKING AT KGL \"" + kgl.getName2() + "\" (" + kgl.getName() + ")[" + kgl.getStrand() + "]");
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
		if (foundgenic) break;
		/* We have already found a gene such that this variant is genic. */
		else if (foundFivePrimeNeighbor) continue; /* we have found genes outside of the variant on 5' and 3' sides */
		else continue; /* go to next round, continue search */
	    } else if (kgl.isThreePrimeToGene(start) ) {
		/* i.e., "start" is 3' to "txend" of gene */
		//System.out.println(String.format("\tbla Variant at %d is 3' to gene: %s",start,kgl.getName2()));
		foundFivePrimeNeighbor=true; /* gene is 5' neighbor to var */
		if (foundgenic) {
		    //System.out.println("found genic is true"); 
		    break; 
		} else if (foundThreePrimeNeighbor) { 
		    //System.out.println("foudn 3' neighbior true");
		    continue;  /* we have found genes outside of the variant on 5' and 3' sides */
		}
		else {
		    //System.out.println("\tbla about to continue to next round");
		    continue;  /* go to next round, continue search */
		}
	    } else {
		/* We now must be in a genic region */
		//System.out.println("bla in genic region");
		if (! kgl.isCodingGene() ) {
		    /* this is either noncoding RNA or maybe bad annotation in UCSC */
		    if (start >= txstart &&  start <= txend ||   /* start is within transcript */
			end >= txstart &&  end <= txend      ||  /* end is within transcript */
			start <= txstart && end >= txend) {      /* variant completely contains transcript */
			Annotation ann = Annotation.createNonCodingExonicRnaAnnotation(name2);
			annotation_list.add(ann); 
			foundgenic=true;
		    }
		    currentGeneIsNonCoding = true;
		    // TODO Annovar code sets cdsstart/cdsend to txstart/txend here.
		    // Do we need this?
		}
	
		if (kgl.isPlusStrand()) {	
		    ArrayList<Annotation> cdsAnnots = getPlusStrandCodingSequenceAnnotation(position,ref, alt, kgl);	
		    for (Annotation a : cdsAnnots) {
			if (a.isGenic())
			    foundgenic=true;
			annotation_list.add(a);
		    }
		} else if (kgl.isMinusStrand()) {
		    ArrayList<Annotation> cdsAnnots = getMinusStrandCodingSequenceAnnotation(position,ref, alt, kgl);	
		    for (Annotation a : cdsAnnots) {
			if (a.isGenic())
			    foundgenic=true;
			annotation_list.add(a);
		    }
		}
	    }

		/* Finished with splicing calculation */
		
	    //	} /* if not found genic */
	
	}/* for (KnownGene kgl : candidateGenes) */
	/** If we arrive here and there are no annotations in the list, then
	    we should at least have a rightNeighbor and a leftNeighbor. 
	    We first check if one of these is upstream or downstream.
	    If not, then we have an intergenic variant. */
	if (annotation_list.isEmpty()) {
	    if (leftNeighbor != null && leftNeighbor.isNearThreePrimeEnd(start,NEARGENE) ) {
		/** The following function creates an upstream or downstream annotation as appropriate. */
		Annotation ann = Annotation.createUpDownstreamAnnotation(leftNeighbor,start);
		annotation_list.add(ann);
	    } 
	    
	    if (rightNeighbor != null && rightNeighbor.isNearFivePrimeEnd(start,NEARGENE)) {
		/** The following function creates an upstream or downstream annotation as appropriate. */
		    Annotation ann = Annotation.createUpDownstreamAnnotation(leftNeighbor,start);
		    annotation_list.add(ann);
	    }
	    /* If we get here, and annotation_list is still empty, then the variant is not
	       nearby to any gene (i.e., it is not upstream/downstream). Therefore, the variant
	       is intergenic */
	    if (annotation_list.isEmpty()) {
		Annotation ann = Annotation.createIntergenicAnnotation(leftNeighbor,rightNeighbor,start);
		annotation_list.add(ann);
	    }
	}
	return annotation_list;
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
    public ArrayList<Annotation> getPlusStrandCodingSequenceAnnotation(int position,String ref, String alt, KnownGene kgl)
	throws AnnotationException  {

	//System.out.println("BLA, getPLusStrand for gene " + kgl.getName2() + "/" + kgl.getName());
	//System.out.println(String.format("BLA, position=%d, ref=%s, alt=%s",position,ref,alt));
	ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();
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
		annotation_list.add(ann);
	    }
	    if (start < kgl.getExonStart(k)) {
		//System.out.println(String.format("BLA, start=%d, exon[%d] start=%d for gene %s ",start,k,kgl.getExonStart(k), kgl.getName2()));
		//System.out.println(String.format("BLA, end=%d",end));
		if (end >= kgl.getExonStart(k)) {	
		    /* Overlap: Variation starts 5' to exon and ends within exon */ 
		    /* 1) Get the start position of the variant w.r.t. transcript (rvarstart) */
		    /* $rvarstart = $exonstart[$k]-$txstart-$lenintron+1; */
		    rvarstart = kgl.getExonStart(k) - kgl.getTXStart() -  cumlenintron + 1;
		    // System.out.println("1 HERE rvarstart os " + rvarstart);
		    /* 2) Get the end position of the variant w.r.t. the transcript (rvarend) */
		    rvarend   = kgl.getRVarEnd(end, k, cumlenintron);
		    if (end < cdsstart) {	
			/* 3) Variant disrupts/changes 5' UTR region.
			 * Rarely, if the 5' UTR is also separated by introns, the variant 
			 * is more complex.
			 #query  ----
			 #gene     <--*---*->
			*/
			Annotation ann = Annotation.createUTR5Annotation(name2);
			annotation_list.add(ann);
			  /* Annovar: $utr5{$name2}++;
			     positive strand for UTR5 */
		    } else if (start > cdsend) {
			/* 4) he variant disrupts/changes 3' UTR region 
			   #query             ----
			   #gene     <--*---*->
			*/
			Annotation ann = Annotation.createUTR3Annotation(name2,name);
			annotation_list.add(ann);
			/* positive strand for UTR3 */
		    } else {	
			/*  5) If we get here, the variant is located within an exon.
			 For now, add this as "exonic/name2 to the list, and also add the
			specific annotation. TODO: Figure out later best way of doing this.
			Annovar: $exonic{$name2}++; */								
		
			//annotation_list.add(new Annotation("exonic",name2));
			if ( kgl.isCodingGene() && alt != null && alt.length()>0) {
			    /* Annovar puts all exonic variants into an array and annotates them later
			     * we will instead get the annotation right here and add it to the
			     * exonic HashMap.
			     * Annovar:	not $current_ncRNA and $obs and 
			     *     push @{$refseqvar{$name}}, [$rcdsstart, $rvarstart, $rvarend, '+', $i, $k+1, $nextline];
			     */
			    /* Note k in the following is the number (zero-based) of affected exon */
			  ArrayList<Annotation> exonicAnns = annotateExonicVariants(rvarstart,rvarend,start,end,ref,alt,k,kgl);
			  for (Annotation a : exonicAnns) {
			      annotation_list.add(a);
				// TODO do we need to increment foundcoding??
			  }
			} else {
			    System.out.println("WARNING TO DO, exonic in noncodingn gene");
			    System.exit(1);
			}
		    }
		    break; /* break out of for loop of exons (k) */
		    
		} else if (k>0 && start > kgl.getExonEnd(k-1)) {  /* i.e., variant is intronic */
		    /* Annovar: $intronic{$name2}++; $foundgenic++; last; */
		    Annotation ann = Annotation.createIntronicAnnotation(name2);
		    System.out.println("INTRON!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    annotation_list.add(ann);
		    break; /* break out of for loop of exons (k) */
		}
	    } /* if (start < kgl.getExonStart(k)) */ else if (start <= kgl.getExonEnd(k)) {
		/* i.e., start of variant is not located 5' to exon start but is located 5' to exon end */
		/* annovar: elsif ($start <= $exonend[$k]) {	#exonic */
		/* $rvarstart = $start-$txstart-$lenintron+1; */
		  /* 1) Get the start position of the variant w.r.t. transcript (rvarstart) */
		rvarstart = start - kgl.getTXStart() - cumlenintron + 1;
		
		    /* 2) Get the end position of the variant w.r.t. the transcript (rvarend) */
		rvarend = kgl.getRVarEnd(end, k,cumlenintron);
		/*
	for my $m ($k .. @exonstart-1) {
			$m > $k and $lenintron += ($exonstart[$m]-$exonend[$m-1]-1);
			if ($end < $exonstart[$m]) {
			#query              ------
			#gene     <--**---******---****---->
				  $rvarend = $exonend[$m-1]-$txstart-$lenintron+1 + ($exonstart[$m]-$exonend[$m-1]-1);
				  last;
			} elsif ($end <= $exonend[$m]) {
			#query           -----------
			#gene     <--**---******---****---->
			$rvarend = $end-$txstart-$lenintron+1;
			last;
			}
		}## for
		
		*/
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
					
		//#here is the trick begins to differentiate UTR versus coding exonic
		if (end < cdsstart) {					
		    /* #usually disrupt/change 5' UTR region, unless the UTR per se is also separated by introns 
		     * #query  ----
		     * #gene     <--*---*->
		     * Annovar: $utr5{$name2}++; #positive strand for UTR5
		     */
		    Annotation ann = Annotation.createUTR5Annotation(name2);
		    annotation_list.add(ann);
		} else if (start > cdsend) {
		    /* #query             ----
		     * #gene     <--*---*->
		     * Annovar: $utr3{$name2}++; #positive strand for UTR3
		     */
		    Annotation ann = Annotation.createUTR3Annotation(name2,name);
		    annotation_list.add(ann);
		} else {
		    //annotation_list.add(new Annotation("exonic",name2));
		    /* Annovar: $exonic{$name2}++; */

		    if ( kgl.isCodingGene() && alt != null && alt.length()>0) {
			    /* Annovar puts all exonic variants into an array and annotates them later
			     * we will instead get the annotation right here and add it to the
			     * exonic HashMap.
			     * Annovar:	not $current_ncRNA and $obs and 
			     *     push @{$refseqvar{$name}}, [$rcdsstart, $rvarstart, $rvarend, '+', $i, $k+1, $nextline];
			     * #queryindex, refseq CDS start, refseq variant start
			     */
			    /* Note k in the following is the number (zero-based) of affected exon */
			    ArrayList<Annotation> exonicAnns = annotateExonicVariants(rvarstart,rvarend,start,end,ref,alt,k,kgl);
			    for (Annotation a : exonicAnns) {
				annotation_list.add(a);
				// TODO do we need to increment foundcoding??
			    }
		    } else {
			    System.out.println("WARNING TO DO, exonic in noncoding gene");
			    System.exit(1);
			}
		}
		/* annovar: $foundgenic++; */
		//break;
		continue; // go to next knownGene
	    }
	} /* iterator over exons */
	return annotation_list;
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
    public ArrayList<Annotation> getMinusStrandCodingSequenceAnnotation(int position,String ref, String alt, KnownGene kgl)
	throws AnnotationException  {
	//System.out.println("BLA, getMinusStrand for gene " + kgl.getName2() + "/" + kgl.getName());
	//System.out.println(String.format("BLA, position=%d, ref=%s, alt=%s",position,ref,alt));
	ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();
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

	    // TODO -- Still all the logic.
	    System.out.println("Warning: " + kgl.getName2() + " is minus strand genic (not yet implemented)");
	    System.exit(1);

	} /* end for loop over exons (k) */




	return null;
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
	 * my ($refcdsstart, $refvarstart, $refvarend, $refstrand, $index, $exonpos, $nextline) = @{$refseqvar->{$seqid}->[$i]};
	 * Note that refcdsstart and refstrand are contained in the KnownGene objects
	 * $index is used in Annovar as the overall index of the variant ($i in the outer for loop
	 * of newprocessNextQueryBatchByGene). Not needed in our implementation.
	 * $exonpos is the number (one-based) of the exon in which the variant was found.
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
	 * @param exonNumber Number (one-based) of affected exon.
	 * @param kgl Gene in which variant was localized to one of the exons 
	 */
   private ArrayList<Annotation> annotateExonicVariants(int refvarstart, int refvarend, 
		int start, int end, String ref, String var, int exonNumber, KnownGene kgl) throws AnnotationException {
       ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();

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
	   annotation_list.add(ann);
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
       String wtnt3 = kgl.getWTCodonNucleotides(refvarstart, frame_s);
       /* wtnt3_after = Sequence of codon right after the variant */
       String wtnt3_after = kgl.getWTCodonNucleotidesAfterVariant(refvarstart,frame_s);
       /* the following checks some  database annotation errors (example: chr17:3,141,674-3,141,683), 
	* so the last coding frame is not complete and as a result, the cDNA sequence is not complete */
       if (wtnt3.length() != 3 && refvarstart - frame_s - 1 >= 0) {
	   String s = String.format("%s, wtnt3-length: %d", kgl.getKnownGeneID(), wtnt3.length());
	   Annotation ann = Annotation.createErrorAnnotation(s);
	   annotation_list.add(ann);
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
	       annotation_list.add(insrt);
	   } else if (var.equals("-") ) { /* i.e., single nucleotide deletion */
	       Annotation dlt = DeletionAnnotation.getAnnotationSingleNucleotidePlusStrand(kgl,frame_s, wtnt3,wtnt3_after,
									   ref, var,refvarstart,exonNumber);
	       //$is_fs++;
	   } else if (var.length()>1) {
	       Annotation blck = BlockSubstitution.getAnnotationPlusStrand(kgl,frame_s, wtnt3, wtnt3_after,
									   ref,var,refvarstart, refvarend, 
									   exonNumber);
	       annotation_list.add(blck);
	   } else {
	       Annotation mssns = SingleNucleotideSubstitution.getAnnotationPlusStrand(kgl,frame_s, wtnt3,wtnt3_after,
								    ref, var,refvarstart,exonNumber);
	       annotation_list.add(mssns);

	   }
       } /* if (start==end) */
       else if (var.equals("-")) {
	   Annotation dltmnt = 
	       DeletionAnnotation.getAnnotationBlockPlusStrand(kgl, frame_s, wtnt3,wtnt3_after,
							       ref, var, refvarstart, refvarend, exonNumber);
	   annotation_list.add(dltmnt);
       }
       
       return annotation_list;
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
