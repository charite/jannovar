package exomizer.reference;


import java.util.TreeMap;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;


import exomizer.reference.KnownGene;
import exomizer.reference.Translator;

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
 * Note that this class contains some of the annotation functions of Annovar. It was not
 * attempted to reimplement all of the copious functionality of that nice program,
 * just enough to annotate variants found in VCF files. Some notes in particular
 * <UL>
 * <LI> The -seq_padding functionality of annovar was ignored
 * </UL>
 * @author Peter N Robinson
 * @version 0.02 (Oct. 3, 2012)
 */
public class Chromosome {
    /** Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. 
     TODO. Add more flexible way of dealing with scaffolds etc.*/
    private byte chromosome;
    /** Alternative String for Chromosome. Use for scaffolds and "random" chromosomes. TODO: Refactor */
    private String chromosomeString=null;
    /** TreeMap with all of the genes ({@link exomizer.reference.KnownGene KnownGene} objects) of this chromosome. The key is an
     Integer value representing the transcription start site (txstart) of the transcript.*/
    private TreeMap<Integer,KnownGene> geneTreeMap=null;
    /** The number of keys (gene 5' positions) to search in either direction. */
    private static final int SPAN = 5;
    /** The distance threshold in nucleotides for calling a variant upstream/downstream to a gene, */
    private static final int NEARGENE = 1000;
    /** Number of nucleotides away from exon/intron boundary to be considered as potential splicing mutation. */
    public final static int SPLICING_THRESHOLD=2;
    /** Class object encapsulating rules to translate DNA. */
    private Translator translator = null;
    

    public Chromosome(byte c) {
	this.chromosome = c;
	this.geneTreeMap = new TreeMap<Integer,KnownGene>();
	this.translator = Translator.getTranslator();
    }

    /**
     * Add a gene model to this chromosome. 
     */
    public void addGene(KnownGene kg) {
	int pos = kg.getTXStart();
	this.geneTreeMap.put(pos,kg);
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
	ArrayList<KnownGene> kgList = new ArrayList<KnownGene>();
	KnownGene currentKG=null;
	
	Integer midpos=null, leftpos=null, rightpos=null;
	Map.Entry<Integer,KnownGene> entrL = this.geneTreeMap.ceilingEntry(position);
	Map.Entry<Integer,KnownGene> entrR = null;
	if (entrL == null) {
	    entrL = this.geneTreeMap.floorEntry(position);
	}
	if (entrL == null) {
	    System.err.println("Error: Could not get either floor or ceiling for variant at position " + position
			       + " on chromosome " + 	this.chromosome );
	    System.exit(1);
	}
	midpos = entrL.getKey();
	currentKG = entrL.getValue();
	kgList.add(currentKG);
	
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
		currentKG = entrL.getValue();
		kgList.add(currentKG);
		leftpos = iL;
	    } /* Note that if iL==null then there are no more positions on 5'
	       * end of the chromosome, leftpos is now the lowest of this chromosome,
	       * just do nothing here.
	       */
	    Integer iR = null;
	    if (entrR != null)
		iR = entrR.getKey();
	    if (iR != null) {
		currentKG = entrR.getValue();
		kgList.add(currentKG);
		rightpos = iR;
	    } /* Note that if iR==null, then there are no more positions on
	       * the 3' end of the chromosome, rightpos is thus the higest of
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
    public int getNumberOfGenes() { return this.geneTreeMap.size(); }

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
    public ArrayList<Annotation> getAnnotation(int position,String ref, String alt) {
	System.out.println(String.format("getAnnotation for %d ref: \"%s\" alt \"%s\"",position,ref,alt));
	//int distL=Integer.MAX_VALUE; // distance to closest gene on left (5') side of variant (annovar: $distl)
	//int distR=Integer.MAX_VALUE; // distance to closest gene on right (3') side of variant (annovar: $distr)
	//int genePositionL; // position of closest gene on left (5') side of variant (annovar: $genel)
	//int genePositionR; // position of closest gene on right (3') side of variant (annovar: $gener)
	KnownGene leftNeighbor=null; /* gene to 5' side of variant (may be null if variant lies within a gene) */
	KnownGene rightNeighbor=null; /* gene to 3' side of variant (may be null if variant lies within a gene) */
	/* Note, the following two variables are use to know when we can stop the search: we have already found
	   a 5' and a 3' neighboring gene surrounding the variant. Annovar goes 5' to 3', but we are going
	   centrifugally away from the position of the variant when deciding which gene to test next. Therefore,
	   we need a slightly different strategy to decide whedn to quit the search (In annovar, the search is
	   stopped when we have already found an intragenidc mutation and we hit a gene that is 3' to the variant).*/
	boolean foundFivePrimeNeighbor=false;
	boolean foundThreePrimeNeighbor=false;
	
	ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();

	


	//HashSet<Integer> splicing = new HashSet<Integer>();
	
	HashSet<Integer> utr5 = new HashSet<Integer>();
	HashSet<Integer> utr3 = new HashSet<Integer>();
	HashMap<Integer,String> splicing_anno = new HashMap<Integer,String>();
	HashMap<Integer,String> exonic = new HashMap<Integer,String>();
	// Define start and end positions of variant
	int start = position;
	int end = start + ref.length() - 1;
	
	boolean foundgenic=false; //variant already found in genic region (between start and end position of a gene)
	
	/** Get KnownGenes that are located in vicinity of position. */
	ArrayList<KnownGene> candidateGenes = getBinRange(position);
	
	for (KnownGene kgl : candidateGenes) {
	    boolean currentGeneIsNonCoding=false; // in annovar: $current_ncRNA
	    System.out.println("Bla LOOKING AT KGL " + kgl.getName2());
	    // 	($name, $dbstrand, $txstart, $txend, $cdsstart, $cdsend, $exonstart, $exonend, $name2)
	    //char dbstrand = kgl.getStrand();
	    String name = kgl.getKnownGeneID();
	    int txstart = kgl.getTXStart();
	    int txend   = kgl.getTXEnd();
	    int cdsstart = kgl.getCDSStart();
	    int cdsend = kgl.getCDSEnd();
	    int exoncount = kgl.getExonCount();
	    String name2 = kgl.getName2();
	    System.out.println("Got KG Name=" + name + ": " + kgl.getName2() + " strand:" + kgl.getStrand());
	    if (! foundgenic) {  //this variant has not hit a genic region yet
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
	       the variant to date. They may be null if we have already found a gene in which the variant is
	       located */
	   	System.out.println("bla before ifs");
		System.out.println("bla txStart:" + kgl.getTXStart());
	    if (kgl.isFivePrimeToGene(end)) {  // "end" of variant is 5' to "txstart" of gene
		//variant ---
		//gene		<-*----*->
		System.out.println("bla is 5' to gene");
		foundThreePrimeNeighbor=true; /* gene is 3' neighbor */
		if (foundgenic) break;
		/* We have already found a gene such that this variant is genic. */
		else if (foundFivePrimeNeighbor) break; /* we have found genes outside of the variant on 5' and 3' sides */
		else continue; /* go to next round, continue search */
	    } else if (kgl.isThreePrimeToGene(start) ) {
		/* i.e., "start" is 3' to "txend" of gene */
		System.out.println("bla is 3' to gene");
		System.out.println("kgl txStart: " + kgl.getTXStart());
		System.out.println("kgl txEnd: " + kgl.getTXEnd());


		foundFivePrimeNeighbor=true; /* gene is 5' neighbor to var */
		if (foundgenic) break;
		else if (foundThreePrimeNeighbor) break;  /* we have found genes outside of the variant on 5' and 3' sides */
		else continue;  /* go to next round, continue search */
	    } else {
		/* We now must be in a genic region */
		System.out.println("bla in genic region");
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
		    System.out.println("Warning: " + kgl.getName2() + " is minus strand genic (not yet implemented)");
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
     * combined with the Minusn strand functionalities, but separating them
     * makes things easier to comprehend and debug.
     * @param position The start position of the variant on this chromosome
     * @param ref String representation of the reference sequence affected by the variant
     * @param alt String representation of the variant (alt) sequence
     */
    public ArrayList<Annotation> getPlusStrandCodingSequenceAnnotation(int position,String ref, String alt, KnownGene kgl) {

	System.out.println("BLA, getPLusStrand for gene " + kgl.getName2());
	System.out.println(String.format("BLA, position=%d, ref=%s, alt=%s",position,ref,alt));
	ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();
	int txstart = kgl.getTXStart();
	int txend   = kgl.getTXEnd();
	int cdsstart = kgl.getCDSStart();
	int cdsend = kgl.getCDSEnd();
	int exoncount = kgl.getExonCount();
	String name2 = kgl.getName2();

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
	    System.out.println("BLA getPlusStrandCodingSequenceAnnotation");
	    if (k>0)
		cumlenintron += kgl.getLengthOfIntron(k);
	    cumlenexon += kgl.getLengthOfExon(k);
	    if (cdsstart >= kgl.getExonStart(k) && cdsstart <= kgl.getExonEnd(k)) {
		/* "cdsstart" is thus contained within this exon */
		cumlenexon = kgl.getExonEnd(k) - cdsstart + 1;
	    }
	    /* 1) First check whether variant is a splice variant */
	    System.out.println("BLA, About to check for splice for gene " + kgl.getName2());
	    if (isSpliceVariant(kgl,start,end,ref,alt,k)) {
		System.out.println("BLA, IS splice");
		/* Note Annovar has 	$splicing{$name2}++; at this point
		 * We will not do this, but will add to annotation_list depending
		 * on what type of splicing mutation we have. */
		/* I am unsure what this comment in annovar is supposed to mean:
		   if name2 is already a splicing variant, but its detailed annotation 
		   (like c150-2A>G) is not available, 
		   and if this splicing leads to amino acid change (rather than UTR change) */
		if (start == end && start >= cdsstart) { /* single-nucleotide variant */
		    int exonend = kgl.getExonEnd(k);
		    int exonstart = kgl.getExonStart(k);
		    if (start >= exonstart -SPLICING_THRESHOLD  && start < exonstart) {
			/*  #------*-<---->------- mutation located right in front of exon */
			cumlenexon -= (exonend - exonstart);
			/*  Above, we had $lenexon += ($exonend[$k]-$exonstart[$k]+1); take back but for 1.*/
			String anno = String.format("HGVS=%s(%s:exon:%d:c.%d-%d%s>%s)",kgl.getName2(),kgl.getName(),
						    k+1,cumlenexon,exonstart-start,ref,alt);
			Annotation ann = Annotation.createSplicingAnnotation(anno);
			annotation_list.add(ann);
			/* Annovar:$splicing_anno{$name2} .= "$name:exon${\($k+1)}:c.$lenexon-" . ($exonstart[$k]-$start) . "$ref>$obs,"; */
		    } else if (start > exonend && start <= exonend + SPLICING_THRESHOLD)  {
			/* #-------<---->-*--------<-->-- mutation right after exon end */
			String anno = String.format("exon%d:c.%d+%d$s>$s",k+1,cumlenexon,start-exonend,ref,alt);
			//$splicing_anno{$name2} .= "$name:exon${\($k+1)}:c.$lenexon+" . ($start-$exonend[$k]) . "$ref>$obs,";
			Annotation ann = Annotation.createSplicingAnnotation(anno);
			annotation_list.add(ann);
		    }
		}
	    }
	    if (start < kgl.getExonStart(k)) {
		System.out.println(String.format("BLA, start=%d, exon[%d] start=%d for gene %s ",start,k,kgl.getExonStart(k), kgl.getName2()));
		System.out.println(String.format("BLA, end=%d",end));
		if (end >= kgl.getExonStart(k)) {	
		    /* Overlap: Variation starts 5' to exon and ends within exon */ 
		    /* 1) Get the start position of the variant w.r.t. transcript (rvarstart) */
		    rvarstart = kgl.getRVarStart(kgl.getExonStart(k), cumlenintron);
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
			Annotation ann = Annotation.createUTR3Annotation(name2);
			annotation_list.add(ann);
			/* positive strand for UTR3 */
		    } else {	
			/*  5) If we get here, the variant is located within an exon.
			 For now, add this as "exonic/name2 to the list, and also add the
			specific annotation. TODO: Figure out later best way of doing this.
			Annovar: $exonic{$name2}++; */								
		
			annotation_list.add(new Annotation("exonic",name2));
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
			}
		    }
		    break;
		    
		} else if (k>0 && start > kgl.getExonEnd(k-1)) {  /* i.e., variant is intronic */
		    /* Annovar: $intronic{$name2}++; $foundgenic++; last; */
		    Annotation ann = Annotation.createIntronicAnnotation(name2);
		    System.out.println("INTRON!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    annotation_list.add(ann);
		    break;
		}
	    } /* if (start < kgl.getExonStart(k)) */ else if (start <= kgl.getExonEnd(k)) {
		/* i.e., start of variant is not located 5' to exon start but is located 5' to exon end */
		/* annovar: elsif ($start <= $exonend[$k]) {	#exonic */
		  /* 1) Get the start position of the variant w.r.t. transcript (rvarstart) */
		rvarstart = kgl.getRVarStart(kgl.getExonStart(k), cumlenintron);
		    /* 2) Get the end position of the variant w.r.t. the transcript (rvarend) */
		rvarend = kgl.getRVarEnd(end, k,cumlenintron);
					
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
		    Annotation ann = Annotation.createUTR3Annotation(name2);
		    annotation_list.add(ann);
		} else {
		    annotation_list.add(new Annotation("exonic",name2));
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
		    }
		}
		/* annovar: $foundgenic++; */
		break;
	    }
	} /* iterator over exons */
	return annotation_list;
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
		int start, int end, String ref, String var, int exonNumber, KnownGene kgl) {
       ArrayList<Annotation> annotation_list = new ArrayList<Annotation>();
       /*  Annovar declarations:
	* my ($wtnt3, $wtnt3_after, @wtnt3, $varnt3, $wtaa, $wtaa_after, $varaa, $varpos);		
	* #wtaa_after is the aa after the wtaa
	my ($chr, $start, $end, $ref, $obs);
	my $canno;
	
	my ($pre_pad, $post_pad, $wt_aa_pad, $var_aa_pad);  # Hold padded seq
	my $refcdsend = $cdslen->{$seqid} + $refcdsstart - 1;  # the end of the CDS
	
	my @nextline = split (/\s+/, $nextline);
	* ($chr, $start, $end, $ref, $obs) = @nextline[@avcolumn];
	($ref, $obs) = (uc $ref, uc $obs);
	$zerostart and $start++;
	$chr =~ s/^chr//;
	* 
	* */
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
       if (start == end) {
	   if (ref.equals("-") ) {	
	       Annotation  insrt = annotateInsertionVariant(kgl,frame_s, wtnt3,wtnt3_after,ref,
						       var,refvarstart,exonNumber);
	       annotation_list.add(insrt);
	   } else if (var.equals("-") ) { /* i.e., single nucleotide deletion */
	       //$do_trim = 3;   # Trim first 3 nt of post_pad for variant, as wtnt3_after is being added here.
	       char deletedNT=' ';
	       String varnt3=null;
	       if (frame_s == 1) {
		   deletedNT = wtnt3.charAt(1);
		   varnt3 = String.format("%c%c%s",wtnt3.charAt(0), wtnt3.charAt(2),wtnt3_after);
		   /* $deletent = $wtnt3[1];
		      $varnt3 = $wtnt3[0].$wtnt3[2].$wtnt3_after; */
	       } else if (frame_s == 2) {
		   deletedNT = wtnt3.charAt(2);
		   varnt3 = String.format("%c%c%s",wtnt3.charAt(0), wtnt3.charAt(1),wtnt3_after);
		   /* $deletent = $wtnt3[2];
		      $varnt3 = $wtnt3[0].$wtnt3[1].$wtnt3_after; */
	       } else {
		   deletedNT = wtnt3.charAt(0);
		   varnt3 = String.format("%c%c%s",wtnt3.charAt(1), wtnt3.charAt(2),wtnt3_after);
		   /*$deletent = $wtnt3[0];
		     $varnt3 = $wtnt3[1].$wtnt3[2].$wtnt3_after; */
	       }
	       String wtaa = translator.translateDNA(wtnt3);
	       String varaa = translator.translateDNA(varnt3);
	       int aavarpos = (int)Math.floor((refvarstart-kgl.getRefCDSStart())/3)+1;
	       /*$varpos) =   int(($refvarstart-$refcdsstart)/3)+1; */
	       String canno = String.format("c.%ddel%c",(refvarstart-kgl.getRefCDSStart()+1),deletedNT);
	       /* $canno = "c." . ($refvarstart-$refcdsstart+1) . "del$deletent"; */
	       if (wtaa.equals("*")) { /* #mutation on stop codon */ 
		   if (varaa.startsWith("*")) { /* #stop codon is still stop codon 	if ($varaa =~ m/\* /)   */
		   String nfsdel_ann = String.format("%s:%s:exon%d:%s:p.X%dX,",kgl.getName2(),kgl.getName(),
						     exonNumber,canno,aavarpos);
		   Annotation ann = Annotation.createNonFrameshiftDeletionAnnotation(nfsdel_ann);
		   annotation_list.add(ann);
		   /* Annovar: "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . "X,";	
		      #changed fsdel to nfsdel on 2011feb19 */
		   } else {	 /* stop codon is lost */
		       String stoploss_ann = String.format("%s:%s:exon%d:%s:p.X%d%s,",kgl.getName2(),kgl.getName(),
						     exonNumber,canno,aavarpos,varaa);
		       /* $function->{$index}{stoploss} .= 
		      "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . "$varaa,"; */
		       Annotation ann = Annotation.createStopLossAnnotation(stoploss_ann);
		       annotation_list.add(ann);
		   }
	       } else {
		   if (varaa.contains("*")) { /* new stop codon created */
		       String annotation = String.format("%s:%s:exon%d:%s:p.%s%dX,",kgl.getName2(),kgl.getName(),
							 exonNumber,canno,wtaa, aavarpos);
		       /* $function->{$index}{stopgain} .= 
			  "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . "X,"; */
		       annotation_list.add(new Annotation("stopgain",annotation));
		   } else {
		        String annotation = String.format("%s:%s:exon%d:%s:p.%s%dfs,",kgl.getName2(),kgl.getName(),
							 exonNumber,canno,wtaa,aavarpos);
			/*  $function->{$index}{fsdel} .= 
			    "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . "fs,"; */
			annotation_list.add(new Annotation("fsdel",annotation));
		   }
	       }
	       //$is_fs++;
	   }
       } /* if (start==end) */	
       
       return annotation_list;
   }
    
    /**
     * Determine if the variant under consideration represents a splice variant, defined as 
     * being in the SPLICING_THRESHOLD nucleotides within the exon/intron boundry. If so,
     * return true, otherwise, return false.
     * @param k Exon number in gene represented by kgl
     * @param kgl Gene to be checked for splice mutation for current chromosomal variant.
     */
    private boolean isSpliceVariant(KnownGene kgl, int start, int end, String ref, String alt, int k) {
	if (kgl.getExonCount() == 1) return false; /* Single-exon genes do not have introns */
	int exonend = kgl.getExonEnd(k);
	int exonstart = kgl.getExonStart(k);
	if (k==0 && start >= exonend-SPLICING_THRESHOLD+1 && start <= exonend+SPLICING_THRESHOLD) {
	    /* variation is located right after (3' to) first exon. For instance, if 
	       SPLICING_THRESHOLD is 2, we get the last two nucleotides of the first (zeroth)
	       exon and the first 2 nucleotides of the following intron*/
	    return true;
	} else if (k == kgl.getExonCount()-1 && start >= exonstart - SPLICING_THRESHOLD 
		   && start <= exonstart + SPLICING_THRESHOLD -1) {
	    /* variation is located right before (5' to) the last exon, +/- SPLICING_THRESHOLD
	       nucleotides of the exon/intron boundary */
	    return true;
	} else if (k>0 && k < kgl.getExonCount()-1) {
	    /* interior exon */
	    if (start >= exonstart -SPLICING_THRESHOLD && start <= exonstart + SPLICING_THRESHOLD - 1)
		/* variation is located at 5' end of exon in splicing region */
		return true;
	    else if (start >= exonend - SPLICING_THRESHOLD + 1 &&  start <= exonend + SPLICING_THRESHOLD)
		/* variation is located at 3' end of exon in splicing region */
		return true;
	}
	/* Now repeat the above calculations for "end", the end position of the variation.
	* TODO: in many cases, start==end, this calculation is then superfluous. Refactor.*/
	if (k==0 && end >= exonend-SPLICING_THRESHOLD+1 && end <= exonend+SPLICING_THRESHOLD) {
	    /* variation is located right after (3' to) first exon. For instance, if 
	       SPLICING_THRESHOLD is 2, we get the last two nucleotides of the first (zeroth)
	       exon and the first 2 nucleotides of the following intron*/
	    return true;
	} else if (k == kgl.getExonCount()-1 && end >= exonstart - SPLICING_THRESHOLD 
		   && end <= exonstart + SPLICING_THRESHOLD -1) {
	    /* variation is located right before (5' to) the last exon, +/- SPLICING_THRESHOLD
	       nucleotides of the exon/intron boundary */
	    return true;
	} else if (k>0 && k < kgl.getExonCount()-1) {
	    /* interior exon */
	    if (end >= exonstart -SPLICING_THRESHOLD && end <= exonstart + SPLICING_THRESHOLD - 1)
		/* variation is located at 5' end of exon in splicing region */
		return true;
	    else if (end >= exonend - SPLICING_THRESHOLD + 1 &&  end <= exonend + SPLICING_THRESHOLD)
		/* variation is located at 3' end of exon in splicing region */
		return true;
	}
	/* Check whether start/end are different and overlap with splice region. */
	if (k==0 && start <= exonend && end >= exonend) {
	    /* first exon, start is 5' to exon/intron boundry and end is 3' to boundary */
	    return true;
	} else if (k == kgl.getExonCount()-1 && start <= exonstart && end >= exonstart) {
	    /* last exon, start is 5' to exon/intron boundry and end is 3' to boundary */
	    return true;
	} else if (k>0 && k < kgl.getExonCount() -1) {
	     /* interior exon */
	    if (start <= exonstart && end >= exonstart) {
		/* variant overlaps 5' exon/intron boundary */
		return true;
	    } else if (start <= exonend && end >= exonend) {
		/* variant overlaps 3' exon/intron boundary */
		return true;
	    }
	}
	return false; /* This variant does not lead to a splicing mutation */
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
	
	/**
	 * Annotates an insertion variant. The fact that a variant is an insertion variant
	 * has been identified by the fact that the start and end positition of the variant 
	 * are equal and the reference sequence is indicated as "-".
	 * <P>
	 * The insertion coordinate system in ANNOVAR always uses "position after the current site"
	* in positive strand, this is okay
	* in negative strand, the "after current site" becomes "before current site" during transcription
	* therefore, appropriate handling is necessary to take this into account
	* for example, for a trinucleotide GCC with frameshift of 1 and insertion of CCT
	* in positive strand, it is G-CTT-CC
	* but if the transcript is in negative strand, the genomic sequence should be GC-CCT-C, and transcript is G-AGG-GC
	* <P>
	* @param kgl The gene in which the current mutation is contained
	* @param frame_s the location within the frame (0,1,2) in which mutation occurs
	* @param wtnt3 The three nucleotides of codon affected by start of mutation
	* @param wtnt3_after the three nucleotides of the codon following codon affected by mutation
	* @param refvarstart The start position of the variant with respect to the CDS of the mRNA
	* @param exonNumber Number (one-based) of affected exon.
	* @return an {@link exomizer.reference.Annotation Annotation} object representing the current variant
	*/
	
	private Annotation annotateInsertionVariant(KnownGene kgl,int frame_s, String wtnt3,String wtnt3_after,
		String ref, String var,int refvarstart,int exonNumber){
			String annotation = null;
			String annovarClass = null;
		String varnt3 = null;
		if (kgl.isPlusStrand() ) {
			if (frame_s == 1) { /* insertion located at 0-1-INS-2 part of codon */
				varnt3 = String.format("%c%c%s%c",wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
				// . $wtnt3[1] . $obs . $wtnt3[2];
			} else if (frame_s == 2) {
				varnt3 = String.format("%s%s", wtnt3, var);
			} else { /* i.e., frame_s == 0 */
				varnt3 = String.format("%c%s%c%c",wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
			}
		} else if (kgl.isMinusStrand()) {
			if (frame_s == 1) {
				varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
			} else if (frame_s == 2) {
				varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
			} else { /* i.e., frame_s == 0 */
				varnt3 = String.format("%s%s", var, wtnt3); // $obs . $wtnt3[0] . $wtnt3[1] . $wtnt3[2];
			}
		}
		String wtaa = translator.translateDNA(wtnt3);
		String wtaa_after = null;
		if (wtnt3_after != null && wtnt3_after.length() > 0) {	
			wtaa_after = this.translator.translateDNA(wtnt3_after);
		}
		/* wtaa_after could be undefined, if the current aa is the stop codon (X) 
			 * example:17        53588444        53588444        -       T
			 */
		if (wtaa_after != null && wtaa_after.equals("*"))
			wtaa_after = "X";
		String varaa = this.translator.translateDNA(varnt3);
		int refcdsstart = kgl.getRefCDSStart() ;
		/* annovar $varpos, here aavarpos */
		int aavarpos = (int) Math.floor((refvarstart-refcdsstart)/3)+1;  // TODO CHECK was int (  ... )
					
		/* Annovar: 
		 * 	$canno = "c." . ($refvarstart-$refcdsstart+1) .  "_" . 
		 * 				($refvarstart-$refcdsstart+2) . "ins$obs";		
		 * 		#cDNA level annotation
		 */
		 String canno = String.format("c.%d_%dins%s",refvarstart-refcdsstart+1,refvarstart-refcdsstart+2,var);
		/* If length of insertion is a multiple of 3 */
		if (var.length() % 3 == 0) {
		    if (wtaa.equals("*")) { /* Mutation affects the wildtype stop codon */
			int idx = varaa.indexOf("*");
			if (idx>=0) {
			    /* delete all aa after stop codon, but keep the aa before 
			     * annovar: $varaa =~ s/\*.* /X/; */
			    varaa = String.format("%sX",varaa.substring(0,idx+1));
			    /* Note in annovar $seqid is $name and $geneidmap->{$seqid} is $name2 */
			    annotation = String.format("%s:exon:%d:%s:p.X%ddelins%s",kgl.getName2(),kgl.getName(),
						       exonNumber,canno,aavarpos,varaa);
			    return new Annotation("nfsins", annotation);
			    /* corresponds to 	$function->{$index}{nfsins} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . 
							"delins$varaa,";		#stop codon is stil present */
			} else {
			    /* Mutation => stop codon is lost */
			    /** Corresponds to $function->{$index}{stoploss} .= 
			     * "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . "delins$varaa,";	#stop codon is lost */
			    annotation = String.format("%s:%s:exon%d:%s:p.X%ddelins%s",kgl.getName2(),kgl.getName(),
						       exonNumber,canno,aavarpos,varaa);
			    return new Annotation("stoploss", annotation);
			}
			/* TODO ignore $is_fs here (it is used in annovar just for sequence padding) */
		    } else { /* i.w., wtaa is not equal to '*'  */
			int idx = varaa.indexOf("*");
			if (idx>=0) { /* corresponds to annovar: if ($varaa =~ m/\* /) {  */
			    varaa = String.format("%sX",varaa.substring(0,idx+1));
			    /* $varaa =~ s/\*.* /X/;	#delete all aa after stop codon, but keep the aa before */
			    /*$function->{$index}{stopgain} .= 
			     * "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . "delins$varaa,"; */
			    annotation = String.format("%s:%s:exon%d:%s:p.%s%ddelins%s,",kgl.getName2(),kgl.getName(),
						       exonNumber,canno,wtaa,aavarpos,varaa);
			    return new Annotation("stopgain", annotation);
			    /* Ignore annovar: $is_fs++; */
			} else {
			    /*$function->{$index}{nfsins} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . 
			     * "delins$varaa,"; */
			    annotation = String.format("%s:%s:exon%d:%s:p.%s%ddelins%s,",kgl.getName2(),kgl.getName(),
						       exonNumber,canno,wtaa,aavarpos,varaa);
			    return new Annotation("nfsins", annotation);
			}
		    }
		} else { /* i.e., length of variant is not a multiple of 3 */
		    if (wtaa.equals("*") ) { /* mutation on stop codon */
			int idx = varaa.indexOf("*"); /* corresponds to : if ($varaa =~ m/\* /) {	 */
			if (idx>=0) {
			    /* in reality, this cannot be differentiated from non-frameshift insertion, but we'll still call it frameshift */
			    /* delete all aa after stop codon, but keep the aa before 
			     * annovar: $varaa =~ s/\*.* /X/; */
			    varaa = String.format("%sX",varaa.substring(0,idx+1));
			    annotation = String.format("%s:%s:exon%d:%s:p.X%ddelins%s,",kgl.getName2(),kgl.getName(),
						       exonNumber,canno,aavarpos,varaa);
			    return new Annotation("fsins", annotation);
			    /* $function->{$index}{fsins} .= 
			     * "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . "delins$varaa,"; */
			} else { /* var aa is not stop (*) */
			    /* $function->{$index}{stoploss} .= 
			     * "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" . "delins$varaa,"; */
			    annotation = String.format("%s:%s:exon%d:%s:p.X%ddelins%s,",kgl.getName2(),kgl.getName(),
						       exonNumber,canno,aavarpos,varaa);
			    return new Annotation("stoploss",annotation);
			}
		    } else { /* i.e., wtaa not a stop codon */
			int idx = varaa.indexOf("*");
			if (idx>=0) {
			    varaa = String.format("%sX",varaa.substring(0,idx+1));
			    annotation = String.format("%s:%s:exon%d:%s:p.%s%d_%s%ddelins%s", kgl.getName2(),kgl.getName(),
						       exonNumber,canno,wtaa,aavarpos,wtaa_after,(aavarpos+1),varaa);
			    return new Annotation("stopgain",annotation);
			    /*"$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . 
			     * 	"_$wtaa_after" . ($varpos+1) . "delins$varaa,"; */
			} else {
			    annotation = String.format("%s:%s:exon%d:%s:p.%s%dfs,",
						       kgl.getName2(),kgl.getName(),exonNumber,canno,wtaa,aavarpos);
			    return new Annotation("fsins", annotation);
			    /* $function->{$index}{fsins} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . "fs,";*/
			}
		    }
		    /* ignore this in annovar: $is_fs++; */
		}	
		/* We should never get here */
		//return null;
	}
					
								


}
/* EoF*/
