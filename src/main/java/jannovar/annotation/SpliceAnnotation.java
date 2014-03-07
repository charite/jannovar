package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class is intended to provide a static method to generate annotations for splice mutations. This method is put in
 * its own class only for convenience and to at least have a name that is easy to find.
 * <P>
 * Annovar reports the two nucleotides on the boundary of the exon as being splice mutations, which is not entirely
 * accurate, although mutations in this part of exons can indeed disrupt proper splicing. Instead, jannovar takes the
 * SPLICING_THRESHOLD nucleotides on the intronic side.
 * 
 * @version 0.14 (06 March, 2014)
 * @author Peter N Robinson, Marten Jäger
 */

public class SpliceAnnotation {
	/**
	 * Number of nucleotides away from exon/intron boundary to be considered as potential splicing mutation.
	 */
	public final static int SPLICING_THRESHOLD = 2;

	/**
	 * Determine if the variant under consideration represents a splice variant, defined as being in the
	 * SPLICING_THRESHOLD nucleotides within the exon/intron boundry. If so, return true, otherwise, return false.<br>
	 * <br>
	 * splice boundary<br>
	 * 		|    |<br>
	 * ----------||||||||||||||||--------------<br>
	 * 		  * 		--> Y<br>
	 *     * 			--> N<br>
	 *      * 			--> Y<br>
	 * 			 * 		--> N<br>
	 *    ---- 			--> Y<br>
	 *        ----- 	--> Y<br>
	 *       --- 		--> Y<br>
	 *           ----- 	--> N<br>
	 *   ---------- 	--> Y<br>
	 * 
	 * 
	 * @param k
	 *            Exon number in transcript model represented by tmdl
	 * @param start
	 *            begin position of variant
	 * @param end
	 *            end position of variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @param tmdl
	 *            Transcript model to be checked for splice mutation for current chromosomal variant.
	 * @return <code>true</code> if plus strand variant
	 */
	public static boolean isSpliceVariant(TranscriptModel tmdl, int start, int end, String ref, String alt, int k) {
		// first check if there are spolice sites at all
		if (tmdl.getExonCount() == 1) {
			return false;
		} else {
			if (tmdl.isMinusStrand()) { // switch start & end coordinates
				int tmp = start;
				start = end;
				end = tmp;
			}
			int exonStart = tmdl.getExonStart(k);
			int exonEnd = tmdl.getExonEnd(k);
			// System.out.println(String.format("k: %d\tstart: %d\tend: %d\te.start: %d\te.end: %d", k, start, end,
			// exonStart, exonEnd));
			// check if start and end are inside the exon
			if (start >= exonStart && end <= exonEnd)
				return false;

			// check if the start or end overlaps with the 5' intronic splicing region (2-bp)
			if (k > 0) { // not for the first exon
				if ((exonStart - start) > 0 && (exonStart - start) <= SPLICING_THRESHOLD) // start in region
					return true;
				if ((exonStart - end) > 0 && (exonStart - end) <= SPLICING_THRESHOLD) // end in region
					return true;
				if (start <= exonStart - SPLICING_THRESHOLD && end >= exonStart) // variant overspans the splice region
					return true;
			}
			// same for the 3' intronic splicing region
			if (k < tmdl.getExonCount()) { // not for the last exon
				if ((start - exonEnd) > 0 && (start - exonEnd) <= SPLICING_THRESHOLD) // start in region
					return true;
				if ((end - exonEnd) > 0 && (end - exonEnd) <= SPLICING_THRESHOLD) // end in region
					return true;
				if (start <= exonEnd - SPLICING_THRESHOLD && end >= exonEnd) // variant overspans the splice region
					return true;
			}
		}
		return false;
	}

	/**
	 * Determine if the variant under consideration (which the calling code has determined to be on the plus strand)
	 * represents a splice variant, defined as being in the SPLICING_THRESHOLD nucleotides within the exon/intron
	 * boundry. If so, return true, otherwise, return false.
	 * 
	 * @param k
	 *            Exon number in gene represented by kgl
	 * @param start
	 *            begin position of variant
	 * @param end
	 *            end position of variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @param kgl
	 *            Gene to be checked for splice mutation for current chromosomal variant.
	 * @return <code>true</code> if plus strand variant
	 */
	public static boolean isSpliceVariantPlusStrand(TranscriptModel kgl, int start, int end, String ref, String alt, int k) {
		if (kgl.getExonCount() == 1)
			return false; /* Single-exon genes do not have introns */
		int exonend = kgl.getExonEnd(k);
		int exonstart = kgl.getExonStart(k);
		if (k == 0 && start > exonend && start <= exonend + SPLICING_THRESHOLD) {
			/*
			 * In annovar: start >= exonend-SPLICING_THRESHOLD+1 instead of
			 * start > exonend
			 */
			/*
			 * variation is located right after (3' to) first exon. For
			 * instance, if SPLICING_THRESHOLD is 2, we get the first 2
			 * nucleotides of the following intron
			 */
			return true;
		} else if (k == kgl.getExonCount() - 1 && start >= exonstart - SPLICING_THRESHOLD && start < exonstart) {
			/*
			 * In annovar, we had start <= exonstart + SPLICING_THRESHOLD -1
			 * instead of start < exonstart
			 */
			/*
			 * variation is located right before (5' to) the last exon, +/-
			 * SPLICING_THRESHOLD nucleotides of the exon/intron boundary
			 */
			return true;
		} else if (k > 0 && k < kgl.getExonCount() - 1) {
			/* interior exon */
			if (start >= exonstart - SPLICING_THRESHOLD && start < exonstart)
				/* variation is located at 5' end of exon in splicing region */
				return true;
			else if (start > exonend && start <= exonend + SPLICING_THRESHOLD)
				/* variation is located at 3' end of exon in splicing region */
				return true;
		}
		/*
		 * Now repeat the above calculations for "end", the end position of the
		 * variation. TODO: in many cases, start==end, this calculation is then
		 * superfluous. Refactor.
		 */
		if (k == 0 && end > exonend && end <= exonend + SPLICING_THRESHOLD) {
			/*
			 * variation is located right after (3' to) first exon. For
			 * instance, if SPLICING_THRESHOLD is 2, we get the first 2
			 * nucleotides of the following intron
			 */
			return true;
		} else if (k == kgl.getExonCount() - 1 && end >= exonstart - SPLICING_THRESHOLD && end < exonstart) {
			/*
			 * variation is located right before (5' to) the last exon, +/-
			 * SPLICING_THRESHOLD nucleotides of the exon/intron boundary
			 */
			return true;
		} else if (k > 0 && k < kgl.getExonCount() - 1) {
			/* interior exon */
			if (end >= exonstart - SPLICING_THRESHOLD && end < exonstart)
				/* variation is located at 5' end of exon in splicing region */
				return true;
			else if (end > exonend && end <= exonend + SPLICING_THRESHOLD)
				/* variation is located at 3' end of exon in splicing region */
				return true;
		}
		/* Check whether start/end are different and overlap with splice region. */
		if (k == 0 && start <= exonend && end > exonend) {
			/*
			 * first exon, start is 5' to exon/intron boundry and end is 3' to
			 * boundary
			 */
			return true;
		} else if (k == kgl.getExonCount() - 1 && start < exonstart && end >= exonstart) {
			/*
			 * last exon, start is 5' to exon/intron boundry and end is 3' to
			 * boundary
			 */
			return true;
		} else if (k > 0 && k < kgl.getExonCount() - 1) {
			/* interior exon */
			if (start < exonstart && end >= exonstart) {
				/* variant overlaps 5' exon/intron boundary */
				return true;
			} else if (start <= exonend && end > exonend) {
				/* variant overlaps 3' exon/intron boundary */
				return true;
			}
		}
		return false; /* This variant does not lead to a splicing mutation */
	}

	/**
	 * Determine if the variant under consideration (which the calling code has determined to be on the plus strand)
	 * represents a splice variant, defined as being in the SPLICING_THRESHOLD nucleotides within the exon/intron
	 * boundry. If so, return true, otherwise, return false.
	 * 
	 * @param k
	 *            Exon number in gene represented by kgl
	 * @param start
	 *            begin position of variant
	 * @param end
	 *            end position of variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @param kgl
	 *            Gene to be checked for splice mutation for current chromosomal variant.
	 * @return <code>true</code> if minus strand variant
	 */
	public static boolean isSpliceVariantMinusStrand(TranscriptModel kgl, int start, int end, String ref, String alt, int k) {
		if (kgl.getExonCount() == 1)
			return false; /*
							* Single-exon genes do not have introns: if (@exonstart
							* != 1)
							*/
		int exonend = kgl.getExonEnd(k);
		int exonstart = kgl.getExonStart(k);
		int exoncount = kgl.getExonCount();

		// The following checks if start is within 2 nucleotides
		// of appropriate exon boundaries to be a splice mutation.

		if (k == 0 /* This is the last exon of a gene on the minus strand */
				/*
				 * the following two lines give us the last two bp of the last
				 * intron (Splice acceptor sequence).
				 */
				&& start > exonend && start <= exonend + SPLICING_THRESHOLD) {
			return true;
		} else if (k == (exoncount - 1) /* first exon of gene on minus strand */
				&& start >= exonstart - SPLICING_THRESHOLD && start < exonstart) { /*
																					 * first
																					 * two
																					 * nt
																					 * of
																					 * splice
																					 * donor
																					 */
			return true;
		} else if (k > 0 && k < (exoncount - 1)) { /* internal exon */
			if (start >= exonstart - SPLICING_THRESHOLD && start < exonstart) { /*
																				 * splice
																				 * donor
																				 * ,
																				 * minus
																				 * strand
																				 */
				return true;
			}
			if (start > exonend && start <= exonend + SPLICING_THRESHOLD) /*
																			* splice
																			* acceptor
																			* ,
																			* minus
																			* strand
																			*/
				return true;
		}
		if (k == 0 /* last exon of gene on minus strand */
				&& end > exonend && end <= exonend + SPLICING_THRESHOLD) {
			return true;
		} else if (k == (exoncount - 1) /* first exon of gene on minus strand */
				&& end >= exonstart - SPLICING_THRESHOLD && end < exonstart) {
			return true;
		} else if (k > 0 && k < (exoncount - 1)) { /* internal exon */
			if (end >= exonstart - SPLICING_THRESHOLD && end < exonstart) {
				return true;
			}
			if (end > exonend && end <= exonend + SPLICING_THRESHOLD) {
				return true;
			}
		}

		/* overlap with splice sequence at exon/intron boundary */
		if (k == 0 && start <= exonend && end > exonend) {
			/*
			 * last exon on minus strand. start is within exon and end is in
			 * intron
			 */
			return true;
		} else if (k == (exoncount - 1) && start < exonstart && end >= exonstart) {
			/*
			 * first exon for minus-strand gene, start is within intron and end
			 * is in exon
			 */
			return true;
		} else if (k > 0 && k < (exoncount - 1)) {
			/* "inner exon" */
			if (start < exonstart && end >= exonstart) {
				return true;
			}
			if (start <= exonend && end > exonend) {
				return true;
			}
		}
		return false; /* This variant does not lead to a splicing mutation */
	}

	/**
	 * Return an annotation for a splice mutation for a gene on the plus strand.
	 * <P>
	 * The logic is that if a mutation is at position IVSN-1 or IVSN-2 (where N is the number of the following exon (k+1
	 * in the code below, since k is zero-based), then the mutation can be right in front of the exon or right after the
	 * exon
	 * <OL>
	 * <LI>Acceptor mutation: recognize this because the position of the mutation is within one or two
	 * (SPLICING_THRESHOLD) nucleotides of the start of the exon. We then have c.XYZ-1G>T, or something similar, where
	 * XYZ is the first position of the exon; this is calculated by {@code cumlenexon -= (exonend - exonstart);} (think
	 * about it!). The position of the mutation (-1 or -2) is calculated by {@code exonstart-start}.
	 * <LI>Donor mutation. This is something like c.XYZ+2T>C, where XYZ is the last position of the exon, which is
	 * simply {@code cumlenexon}. The position of the mutation (+1 or +2) is calculated by start-exonend.
	 * </OL>
	 * At the moment, no specific annotation is made for other kinds of splice mutation (e.g., a deletion of several
	 * base pairs surrounding a splice site. For now, we will just use the gene symbol as the annotation together with
	 * the variant type SPLICING. This can be improved in the future.
	 * <P>
	 * I am unsure what this comment in annovar is supposed to mean: if name2 is already a splicing variant, but its
	 * detailed annotation (like c150-2A>G) is not available, and if this splicing leads to amino acid change (rather
	 * than UTR change)
	 * 
	 * @param kgl
	 *            Gene with splice mutation for current chromosomal variant.
	 * @param start
	 *            start position of the variant
	 * @param end
	 *            position of the variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @param k
	 *            number (zero-based) of the affected exon.
	 * @param cumlenexon
	 *            cumulative length up the end of exon k
	 * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to the splice mutation.
	 */
	public static Annotation getSpliceAnnotationPlusStrand(TranscriptModel kgl, int start, int end, String ref, String alt, int k, int cumlenexon) {
		int cdsstart = kgl.getCDSStart();
		String anno;
		if (start == end && start >= cdsstart) { /* single-nucleotide variant */
			int exonend = kgl.getExonEnd(k);
			int exonstart = kgl.getExonStart(k);
			if (start >= exonstart - SPLICING_THRESHOLD && start < exonstart) {
				/*
				 * #------*-<---->------- mutation located right in front of
				 * exon
				 */
				cumlenexon -= (exonend - exonstart);
				/*
				 * Above, we had $lenexon += ($exonend[$k]-$exonstart[$k]+1);
				 * take back but for 1.
				 */
				if (kgl.isNonCodingGene())
					anno = String.format("%s:exon%d:n.%d-%d%s>%s", kgl.getName(), k + 1, cumlenexon, exonstart - start, ref, alt);
				else
					anno = String.format("%s:exon%d:c.%d-%d%s>%s", kgl.getName(), k + 1, cumlenexon, exonstart - start, ref, alt);
				int refvarstart = cumlenexon; /* position of mutation in CDS */
				Annotation ann = new Annotation(kgl, anno, VariantType.SPLICING, refvarstart);
				return ann;
			} else if (start > exonend && start <= exonend + SPLICING_THRESHOLD) {
				/* #-------<---->-*--------<-->-- mutation right after exon end */
				if (kgl.isNonCodingGene())
					anno = String.format("%s:exon%d:n.%d+%d%s>%s", kgl.getName(), k + 1, cumlenexon, start - exonend, ref, alt);
				else
					anno = String.format("%s:exon%d:c.%d+%d%s>%s", kgl.getName(), k + 1, cumlenexon, start - exonend, ref, alt);
				/* anno is now something like uc001alq.2:exon22:c.2818+2G>A */
				int refvarstart = cumlenexon;
				Annotation ann = new Annotation(kgl, anno, VariantType.SPLICING, refvarstart);
				return ann;
			}
		}
		/*
		 * If we get here, the is a complicated splice mutation not covered by
		 * the above cases.
		 */
		anno = String.format("%s:exon%d:complicated splice mutation", kgl.getName(), k + 1);
		// Annotation ann = Annotation.createSplicingAnnotation(kgl, 0,annot);
		Annotation ann = new Annotation(kgl, anno, VariantType.SPLICING, 0);
		return ann;

	}

	/**
	 * Write a splice annotation for a gene on the minus strand. When we get here, the calling code has already checked
	 * that the mutation is in the 2 nucleotides before or after the exon/intron boundary.
	 * 
	 * @param kgl
	 *            Gene with splice mutation for current chromosomal variant.
	 * @param start
	 *            start position of the variant
	 * @param end
	 *            position of the variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @param k
	 *            number (zero-based) of the affected exon.
	 * @param cumlenexon
	 *            cumulative length up the end of exon k
	 * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to the splice mutation.
	 */
	public static Annotation getSpliceAnnotationMinusStrand(TranscriptModel kgl, int start, int end, String ref, String alt, int k, int cumlenexon) {
		int cdsend = kgl.getCDSEnd();
		int exonend = kgl.getExonEnd(k);
		int exonstart = kgl.getExonStart(k);
		int exoncount = kgl.getExonCount();
		String anno;
		// if ($splicing{$name2} and $start==$end and $start<=$cdsend) {
		if (start == end && start <= cdsend) { /*
												 * single nucleotide splice
												 * variant
												 */
			if (start >= exonstart - SPLICING_THRESHOLD && start < exonstart) {
				// ------*-<---->---------<-->-------<------>----
				if (kgl.isNonCodingGene())
					anno = String.format("%s:exon%d:n.%d+%d%s>%s", kgl.getName(), (exoncount - k), cumlenexon, exonstart - start, revcom(ref), revcom(alt));
				else
					anno = String.format("%s:exon%d:c.%d+%d%s>%s", kgl.getName(), (exoncount - k), cumlenexon, exonstart - start, revcom(ref), revcom(alt));
				/* anno is now something like uc001alq.2:exon22:c.2818+2G>A */
				int refvarstart = cumlenexon; // position of variant in CDS,
												// important for sorting
				Annotation ann = new Annotation(kgl, anno, VariantType.SPLICING, refvarstart);
				return ann;
			} else if (start > exonend && start <= exonend + SPLICING_THRESHOLD) {
				// -------<---->-*--------<-->-------<------>----
				cumlenexon -= (exonend - exonstart); // $lenexon -=
														// ($exonend[$k]-$exonstart[$k]);
				if (kgl.isNonCodingGene())
					anno = String.format("%s:exon%d:n.%d-%d%s>%s", kgl.getName(), (exoncount - k), cumlenexon, start - exonend, revcom(ref), revcom(alt));
				else
					anno = String.format("%s:exon%d:c.%d-%d%s>%s", kgl.getName(), (exoncount - k), cumlenexon, start - exonend, revcom(ref), revcom(alt));
				/* anno is now something like. uc003pdx.3:exon12:c.1039-1G>C */
				int refvarstart = cumlenexon; // position of variant in CDS,
												// important for sorting
				Annotation ann = new Annotation(kgl, anno, VariantType.SPLICING, refvarstart);
				return ann;
			}
		}
		/*
		 * If we get here, the is a complicated splice mutation not covered by
		 * the above cases.
		 */
		anno = String.format("%s:exon%d:complicated splice mutation", kgl.getName(), k + 1);
		Annotation ann = new Annotation(kgl, anno, VariantType.SPLICING, 0);
		return ann;
	}

	/**
	 * Return the reverse complement version of a DNA string in upper case. Note that no checking is done in this code
	 * since the parse code checks for valid DNA and upper-cases the input. This code will break if these assumptions
	 * are not valid.
	 * 
	 * @param sq
	 *            original, upper-case cDNA string
	 * @return reverse complement version of the input string sq.
	 */
	private static String revcom(String sq) {
		if (sq.equals("-"))
			return sq; /* deletion, insertion do not need rc */
		StringBuffer sb = new StringBuffer();
		for (int i = sq.length() - 1; i >= 0; i--) {
			char c = sq.charAt(i);
			char match = 0;
			switch (c) {
			case 'A':
				match = 'T';
				break;
			case 'C':
				match = 'G';
				break;
			case 'G':
				match = 'C';
				break;
			case 'T':
				match = 'A';
				break;
			}
			if (match > 0)
				sb.append(match);
		}
		return sb.toString();
	}
}
/* end of file */