package exomizer.reference;


import java.util.TreeMap;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;




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
 * @author Peter N Robinson
 * @version 0.01 (Sept. 19, 2012)
 */
public class Chromosome {
    /** Chromosome. chr1...chr22 are 1..22, chrX=23, chrY=24, mito=25. Ignore other chromosomes. 
     TODO. Add more flexible way of dealing with scaffolds etc.*/
    private byte chromosome;
    /** Alternative String for Chromosome. USe for scaffolds and "random" chromosomes. TODO: Refactor */
    private String chromosomeString=null;
    /** TreeMap with all of the genes ({@link exomizer.io.KGLine KGLine} objects) of this chromosome. */
    private TreeMap<Integer,KnownGene> geneTreeMap=null;
    /** The number of keys (gene 5' positions) to search in either direction. */
    private static final int SPAN = 5;
    /** The distance threshold in nucleotides for calling a variant upstream/downstream to a gene, */
    private static final int NEARGENE = 1000;
    /** Number of nucleotides away from exon/intron boundary to be considered as potential splicing mutation. */
    public final static int SPLICING_THRESHOLD=2;
    

    public Chromosome(byte c) {
	this.chromosome = c;
	this.geneTreeMap = new TreeMap<Integer,KnownGene>();
    }

    /**
     * Add a gene model to this chromosome. 
     */
    public void addGene(KnownGene kg) {
	int pos = kg.getTXStart();
	this.geneTreeMap.put(pos,kg);
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
     * Roughly spekaing, we take the following steps in this method in order to find out whether the
     * variant is genic or intergenic and identify the involved genes (which are stored in the {@link #geneTreeMap} object of this class).
     * Then, other functions are called to characterize the precise variant.
     * <OL>
     * <LI>The position of the variant can be either within a gene ("genic") or between
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
     * Annovar does in its bins.</LI>
     * </OL>
     * @param position The start position of the variant on this chromosome
     * @param ref String representation of the reference sequence affected by the variant
     * @param alt String representation of the variant (alt) sequence
     */
    public void getAnnotation(int position,String ref, String alt) {
	System.out.println(String.format("getAnnotation for %d ref: \"%s\" alt \"%s\"",position,ref,alt));


	int distL=Integer.MAX_VALUE; // distance to closest gene on left (5') side of variant (annovar: $distl)
	int distR=Integer.MAX_VALUE; // distance to closest gene on right (3') side of variant (annovar: $distr)
	int genePositionL; // position of closest gene on left (5') side of variant (annovar: $genel)
	int genePositionR; // position of closest gene on right (3') side of variant (annovar: $gener)
	boolean foundgenic=false; //variant already found in genic region (between start and end position of a gene)
	HashSet<Integer> upstream = new HashSet<Integer>();
	HashSet<Integer> downstream = new HashSet<Integer>();
	HashSet<Integer> ncRNA = new HashSet<Integer>();
	HashSet<Integer> splicing = new HashSet<Integer>();

	HashSet<Integer> utr5 = new HashSet<Integer>();
	HashSet<Integer> utr3 = new HashSet<Integer>();
	HashMap<Integer,String> splicing_anno = new HashMap<Integer,String>();
	HashMap<Integer,String> exonic = new HashMap<Integer,String>();
	// Define start and end positions of variant
	int start = position;
	int end = start + ref.length() - 1;
	
	Integer midpos=null, leftpos=null, rightpos=null;
	Map.Entry<Integer,KnownGene> entr = this.geneTreeMap.ceilingEntry(position);
	if (entr == null) {
	    entr = this.geneTreeMap.floorEntry(position);
	}
	if (entr == null) {
	    System.err.println("Error: Could not get either floor or ceiling for variant at position " + position
			       + " on chromosome " + 	this.chromosome );
	    System.exit(1);
	}
	midpos = entr.getKey();

	leftpos = midpos;
	for (int i = SPAN; i>0; i--) {
	    entr = this.geneTreeMap.lowerEntry(leftpos);
	    Integer ii = entr.getKey();
	    if (ii == null) 
		break; // no more positions, leftpos is now the lowest of this chromosome
	    else
		leftpos = ii;
	}
	rightpos = midpos;
	for (int i = SPAN; i>0; i--) {
	    entr = this.geneTreeMap.higherEntry(rightpos);
	    Integer ii = entr.getKey();
	    if (ii == null) 
		break; // no more positions, rightpos is now the highest of this chromosome
	    else
		rightpos = ii;
	}
	/* When we get here, leftpos, midpos, and rightpos define a range of positions on this
	   Chromosome at which we want to search. midpos is the most likely location of the 
	   gene corresponding to our variant, but theoretically the genes corresponding to 
	   the variant could be located at other places in the range. */
	System.out.println(String.format("Left, mid, right: %d/%d/%d",leftpos,midpos,rightpos));

	Integer iter = leftpos;
	while ( iter != rightpos ) {
	    boolean currentGeneIsNonCoding=false; // in annovar: $current_ncRNA
	    KnownGene kgl = this.geneTreeMap.get(iter);
	    // 	($name, $dbstrand, $txstart, $txend, $cdsstart, $cdsend, $exonstart, $exonend, $name2)
	    char dbstrand = kgl.getStrand();
	    String name = kgl.getUCSCID();
	    int txstart = kgl.getTXStart();
	    int txend   = kgl.getTXEnd();
	    int cdsstart = kgl.getCDSStart();
	    int cdsend = kgl.getCDSEnd();
	    int exoncount = kgl.getExonCount();
	    if (! foundgenic) {  //this variant has not hit a genic region yet
		// "start"  of variant is 3' to "txend" of this gene
		if (start > txend) {
		    if (distL > (start - txend) ) {
			distL = start - txend; // distance to nearest gene to "left" side.
			genePositionL = iter; // Key of this gene in this.geneTreeMap
		    }
		    //	defined $distl or $distl = $start-$txend and $genel=$name2;
		    //$distl > $start-$txend and $distl = $start-$txend and $genel=$name2;	#identify left closest gene
		}
		if (end < txstart) {
		    if (distR > (txstart - end) ) {
			distR = txstart - end;
			genePositionR = iter;
			// defined $distr or $distr = $txstart-$end and $gener=$name2;
			// $distr > $txstart-$end and $distr = $txstart-$end and $gener=$name2;	#identify right closest gene
		    }
		}
	    }
	    // When we arrive here, we may have adjusted the distR etc.
	    if (end < txstart) {  // "end" of variant is 5' to "txstart" of gene
		//variant ---
		//gene		<-*----*->
		if (foundgenic) {
		    /* We have already found a gene such that this variant is genic. The variant
		       is 5' to the current gene. Therefore, there is no overlap and we can stop
		       the search. In annovar: $foundgenic and last; (found right bin) */
		    break;
		}
		if (end > txstart - NEARGENE) { 
		    // we are near to upstream/downstream gene.
		    if (dbstrand == '+') 
			upstream.add(iter);
		    else
			downstream.add(iter); 
		} else {
		    /* we are NOT near to upstream/downstream gene. and thus 
		       transcript is too far away from end, we can end the search
		       for the best bin/gene location. */
		    break; /* Break out for loop over iter */
		}
	    } else if (start > txend) {
		/* i.e., "start" is 3' to "txend" of gene */
		if (! foundgenic && start < txend + NEARGENE)
		    // we are near to upstream/downstream gene.
		    if (dbstrand == '+') 
			upstream.add(iter);
		    else
			downstream.add(iter); 
	    } else {
		/* We now must be in a genic region */
		if (! kgl.isCodingGene() ) {
		    /* this is either noncoding RNA or maybe bad annotation in UCSC */
		    if (start >= txstart &&  start <= txend || 
			end >= txstart &&  end <= txend      ||
			start <= txstart && end >= txend) {
			ncRNA.add(iter);
			foundgenic=true;
		    }
		    currentGeneIsNonCoding = true;
		    // TODO Annovar code sets cdsstart/cdsend to txstart/txend here.
		    // Do we need this?
		}
		int cumlenintron = 0; // cumulative length of introns at a given exon
		int cumlenexon=0; // cumulative length of exons at a given exon
		int rcdsstart=0; // start of CDS within reference RNA sequence.
		int rvarstart=-1; // start of variant within reference RNA sequence
		int rvarend=-1; //end of variant within reference RNA sequence
		boolean foundexonic=false; // have we found the variant to lie in an exon yet?
		if (kgl.isPlusStrand()) {
		    for (int k=0; k< exoncount;++k) {
			if (k>0)
			    cumlenintron += kgl.getLengthOfIntron(k);
			cumlenexon += kgl.getLengthOfExon(k);
			if (cdsstart >= kgl.getExonStart(k)) {
			    /* Calculate CDS start within mRNA sequence accurately
			       by taking intron length into account.
			       TODO: Put this into KGLine */
			    rcdsstart = cdsstart - txstart - cumlenintron + 1;
			    if (cdsstart <= kgl.getExonEnd(k)) {
				/* "cdsstart" is thus contained within this exon */
				cumlenexon = kgl.getExonEnd(k) - cdsstart + 1;
			    }
			}
			if (isSpliceVariant(kgl,start,end,ref,alt,k)) {
			    splicing.add(iter); //////
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
				    String anno = String.format("exon:%d:c.%d-%d%s>%s",k+1,cumlenexon,exonstart-start,ref,alt);
				    splicing_anno.put(iter,anno);
				} else if (start > exonend && start <= exonend + SPLICING_THRESHOLD)  {
				    /* #-------<---->-*--------<-->-- mutation right after exon end */
				    String anno = String.format("exon%d:c.%d+%d$s>$s",k+1,cumlenexon,start-exonend,ref,alt);
				    //$splicing_anno{$name2} .= "$name:exon${\($k+1)}:c.$lenexon+" . ($start-$exonend[$k]) . "$ref>$obs,";
				    splicing_anno.put(iter,anno);
				}
			    }
			}
			/* Finished with splicing calculation */
			if (start < kgl.getExonStart(k)) {
			    if (end >= kgl.getExonStart(k)) {	/* Variation starts 5' to exon and ends within exon */ 
				rvarstart = kgl.getExonStart(k)-txstart-cumlenintron+1;
				for (int m=k;m<exoncount-1;++m) {
				    if (m>k) {
					cumlenintron +=  kgl.getLengthOfIntron(m);
				    }
				    if (end < kgl.getExonStart(m) ) {
					/* #query           --------
					   #gene     <--**---******---****---->
					*/
					rvarend = kgl.getExonEnd(m-1) - txstart - cumlenintron + 1 + kgl.getLengthOfIntron(m);
					break;
				    } else if (end <= kgl.getExonEnd(m) ) {
					/*#query           -----------
					  #gene     <--**---******---****---->
					*/
					rvarend = end-txstart-cumlenintron+1;
					break;
				    }
	
				} /* for (int m.... */
				if (rvarend<0) { /* i.e., rvarend has not be initialized yet */
				    rvarend = txend-txstart-cumlenintron+1;
				    /* if this value is longer than transcript length, 
				       it suggests whole gene deletion. */
				}
				/* here the trick begins to differentiate UTR versus coding exonic */

				if (end < cdsstart) {	
				    /* usually disrupt/change 5' UTR region, unless the UTR
				       per se is also separated by introns
				       #query  ----
				       #gene     <--*---*->
				    */
				    utr5.add(iter);  /* positive strand for UTR5 */
				} else if (start > cdsend) {
				    /* disrupt/change 3' UTR region 
				       #query             ----
				       #gene     <--*---*->
				    */
				    utr3.add(iter); /* positive strand for UTR3 */
				} else {									
				    //$exonic{$name2}++;
				    exonic.put(iter,"TODO-got mutation");
				    if (! currentGeneIsNonCoding && alt != null && alt.length()>0) {
					// push @{$refseqvar{$name}}, [$rcdsstart, $rvarstart, $rvarend, '+', $i, $k+1, $nextline];	
					; // TODO
				    }
				    foundgenic=true;
				    break;
				}
			    } /* if end >= kgl.getExonStart(k)   */
			} /* if (start < kgl.getExonStart(k)) */

		    } /* iterator over exons */
		} /* if is plus strand */
	    } /* if not found genic */

	}// while (iter != rightpos
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




}
/* EoF*/