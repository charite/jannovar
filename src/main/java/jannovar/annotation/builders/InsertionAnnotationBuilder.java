package jannovar.annotation.builders;

import static jannovar.reference.TranscriptProjectionDecorator.INVALID_EXON_ID;
import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.InvalidGenomeChange;
import jannovar.exception.ProjectionException;
import jannovar.reference.CDSPosition;
import jannovar.reference.GenomeChange;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptModel;
import jannovar.reference.TranscriptPosition;
import jannovar.reference.TranscriptProjectionDecorator;
import jannovar.reference.TranscriptSequenceDecorator;
import jannovar.reference.TranscriptSpliceSiteDecorator;
import jannovar.util.Translator;

/**
 * This class provides static methods to generate annotations for insertions in exons.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jäger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InsertionAnnotationBuilder {

	// TODO(holtgrem): Can we end up in start codon? We forward to the

	/**
	 * Returns a {@link Annotation} for the insertion {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * This function can only be used for insertion {@link GenomeChange}s that fall into the CDS of the
	 * {@link TranscriptInfo}. Use the {@link UTRAnnotationBuilder} for variants in the non-CDS/UTR region of the
	 * transcript. However, this function might forward to the {@link UTRAnnotationBuilder} or
	 * {@link SpliceAnnotationBuilder} if the mutation is shifted into the UTR or splice site.
	 *
	 * <h2>Duplications</h2>
	 *
	 * Insertions can also introduce duplications. This function checks for the insertion described in
	 * <code>change</code> to be a duplication and forwards to {@link DuplicationAnnotationBuilder} in this case.
	 *
	 * <h2>Shifting of Insertions</h2>
	 *
	 * In the case of ambiguities, the HGVS specification requires the variant to be shifted towards the 3' end of the
	 * transcript ("rightmost" position). This can cause an insertion to be shifted into the 3' UTR or a splice site.
	 * The function detects this and forwards to the {@link UTRAnnotationBuilder}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe an insertion
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Guard against invalid genome change.
		if (change.getRef().length() != 0 || change.getAlt().length() == 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe an insertion.");

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());

		// Ensure that the position falls into the CDS region.
		if (!transcript.cdsRegion.contains(change.getPos()))
			throw new InvalidGenomeChange("GenomeChange " + change + " does not fall into CDS region "
					+ transcript.cdsRegion);

		// Project genome position to transcript and CDS position and handle inconsistent positions.
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptPosition txPos = null;
		CDSPosition cdsPos = null;
		try {
			txPos = projector.genomeToTranscriptPos(change.getPos()); // position in tx region
			cdsPos = projector.genomeToCDSPos(change.getPos()); // position in CDS region
		} catch (ProjectionException e) {
			throw new InvalidGenomeChange("Problems with GenomeChange:" + e.getMessage());
		}

		// Check whether the insertion describes a duplication and forward to the DuplicationAnnotationBuilder.
		if (DuplicationTester.isDuplication(transcript.sequence, change.getAlt(),
				txPos.withPositionType(PositionType.ZERO_BASED).getPos()))
			return DuplicationAnnotationBuilder.buildAnnotation(transcript, change);

		// Shift the variant towards the 3' end (right) of the transcript in the case of ambiguities.
		GenomeChange origChange = change; // for detecting update
		change = GenomeChangeNormalizer.normalizeInsertion(transcript, change, txPos);
		if (!change.getPos().equals(origChange.getPos())) { // update change if necessary
			// Handle the case where we ended up around an exon border and forward to SpliceAnnotationBuilder.
			TranscriptSpliceSiteDecorator splicingDetector = new TranscriptSpliceSiteDecorator(transcript);
			if (splicingDetector.doesChangeAffectSpliceSite(change))
				return SpliceAnnotationBuilder.buildAnnotation(transcript, change);
			// Handle the case where we ended up in the 3' UTR and forward to UTRAnnotationBuilder.
			if (!transcript.cdsRegion.contains(change.getPos()))
				return UTRAnnotationBuilder.buildAnnotation(transcript, change);
			// Update the transcription and CDS position previously computed to the situation after the change.
			try {
				txPos = projector.genomeToTranscriptPos(change.getPos()); // position in tx region
				cdsPos = projector.genomeToCDSPos(change.getPos()); // position in CDS region
			} catch (ProjectionException e) {
				throw new InvalidGenomeChange("Problems with GenomeChange:" + e.getMessage());
			}
		}

		// Obtain exon ID.
		int exonID = INVALID_EXON_ID;
		try {
			exonID = projector.locateExon(change.getPos());
		} catch (ProjectionException e) {
			throw new InvalidGenomeChange("Problem translating GenomeChange to exon: " + e.getMessage());
		}
		if (exonID == INVALID_EXON_ID)
			throw new InvalidGenomeChange("GenomeChange " + change.getPos() + " does not point to exon.");
		// Translate exon ID to reference order and make 1-based for display.
		exonID = projector.exonIDInReferenceOrder(exonID) + 1;

		// If we reach here then change describes an insertion and the position points into the CDS of the given
		// transcript. We can now create an Annotation for this GenomeChange without catching exceptions.
		return buildAnnotation(transcript, change, txPos, cdsPos, exonID);
	}

	/**
	 * Build insert annotation from <code>transcript</code>, <code>change</code>, and the position on the transcript and
	 * CDS.
	 *
	 * @param transcript
	 *            information of the transcript to generate annotation for
	 * @param change
	 *            genome change, consistently describes a SNV in a CDS and exon region
	 * @param txPos
	 *            transcript position of the genome change
	 * @param cdsPos
	 *            CDS position of the genome change
	 * @param exonNumber
	 *            1-based exon number for display
	 * @return the {@link Annotation} describing the {@link GenomeChange} in <code>change</code>
	 */
	private static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change, TranscriptPosition txPos,
			CDSPosition cdsPos, int exonNumber) {
		// ensure that we work with 0-based positions
		txPos = txPos.withPositionType(PositionType.ZERO_BASED);
		cdsPos = cdsPos.withPositionType(PositionType.ZERO_BASED);

		if (change.getAlt().length() % 3 == 0)
			return buildNoFrameShiftAnnotation(transcript, change, txPos, cdsPos, exonNumber);
		else
			return buildFrameShiftAnnotation(transcript, change, txPos, cdsPos, exonNumber);
	}

	/**
	 * Annotation building implementation, frame shift case.
	 *
	 * @see {@link InsertionAnnotationBuilder#buildAnnotation(TranscriptInfo, GenomeChange, TranscriptPosition,
	 *      CDSPosition int)}
	 */
	private static Annotation buildFrameShiftAnnotation(TranscriptInfo transcript, GenomeChange change,
			TranscriptPosition txPos, CDSPosition cdsPos, int exonNumber) {
		TranscriptSequenceDecorator seqDecorator = new TranscriptSequenceDecorator(transcript);
		// Get shortcut for Translator singleton instance.
		Translator t = Translator.getTranslator();

		// Build location and cDNA annotation strings.
		String locAnno = String.format("%s:exon%d", transcript.accession, exonNumber);
		String cDNAAnno = String.format("c.%d_%dins%s", cdsPos.getPos(), cdsPos.getPos() + 1, change.getAlt());

		// Get wild type codons (the affected plus some upstream) and translate into amino acids.
		String wtNT = seqDecorator.getCodonsStartingFrom(txPos, cdsPos);
		String wtAA = Translator.getTranslator().translateDNA(wtNT);
		// Get variant NT string and translate into amino acids.
		String varNT = TranscriptSequenceDecorator.nucleotidesWithInsertion(wtNT, cdsPos.getPos() % 3, change.getAlt());
		String varAA = Translator.getTranslator().translateDNA(varNT);
		// Shift amino acid insertion to the right (3' end of transcript) as far as possible.
		int varAAPos = cdsPos.getPos() / 3; // original change in AA as position in protein
		int varAAIdx = 0; // original change in AA as index in varAA
		while (varAAIdx + 1 < varAA.length() && varAA.charAt(varAAIdx) == wtAA.charAt(varAAIdx)) {
			++varAAPos;
			++varAAIdx;
		}

		// The code below builds the protein annotation string and the variant type.
		String protAnno = null;
		VariantType varType = null;
		// Check whether there is a stop codon in the variant peptide.
		int idx = varAA.indexOf("*");

		if (wtAA.startsWith("*")) {
			// The WT peptide starts with a stop codon. Can be frameshift insertion or stop loss.
			if (idx == 0) {
				// The variant peptide also starts with a stop codon, is frameshift insertion.
				protAnno = "p.(=)";
				varType = VariantType.FS_INSERTION;
			} else if (idx > 0) {
				// The variant peptide contains a stop codon but does not start with it, is frameshift insertion. In
				// this case we cannot really differentiate this from a non-frameshift insertion but we still call it
				// so.
				//
				// We delete all AA after the stop codon but keep the AAs before in the amino acid annotation.
				varAA = String.format("%sX", varAA.substring(0, idx + 1));
				protAnno = String.format("p.X%ddelins%s", varAAPos + 1, t.toLong(varAA.charAt(varAAIdx)));
				varType = VariantType.FS_INSERTION;
			} else {
				// The variant AA does not contain a stop codon, is stop loss.
				protAnno = String.format("p.X%ddelins%s", varAAPos + 1, t.toLong(varAA.charAt(varAAIdx)));
				varType = VariantType.STOPLOSS;
			}
		} else {
			// The wild type peptide does not start with a stop codon.
			if (cdsPos.getPos() / 3 == 0) {
				// The mutation affects the start codon, is start loss.
				// TODO(holtgrem): Mutalyzer says p.?, by HGVS we could say "p.0? -- probably no protein is produced"
				protAnno = String.format("p.%s%d?", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1);
				varType = VariantType.START_LOSS;
			} else {
				// The start codon is not affected.
				if (idx == 0) {
					// The insertion directly creates a stop codon, is stop gain.
					protAnno = String.format("p.%s%d*", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1);
					varType = VariantType.STOPGAIN;
				} else if (idx > 0) {
					// The insertion is a frameshift variant that leads to a transcript still having a stop codon,
					// simple frameshift insertion.
					protAnno = String.format("p.%s%d%sfs*%d", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1,
							t.toLong(varAA.charAt(varAAIdx)), (idx + 1 - varAAIdx)); // last is stop codon AA pos
					varType = VariantType.FS_INSERTION;
				} else {
					// The insertion is a frameshift variant that leads to the loss of the stop codon, is stop loss.
					protAnno = String.format("p.%s%d%sfs*?", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1,
							t.toLong(varAA.charAt(varAAIdx)));
					varType = VariantType.STOPLOSS;
				}
			}
		}

		// Glue together the annotation string and return the annotation.
		String annotationString = String.format("%s:%s:%s", locAnno, cDNAAnno, protAnno);
		return new Annotation(transcript.transcriptModel, annotationString, varType, cdsPos.getPos() + 1);
	}

	/**
	 * Annotation building implementatino, no frame shift case.
	 *
	 * @see {@link InsertionAnnotationBuilder#buildAnnotation(TranscriptInfo, GenomeChange, TranscriptPosition,
	 *      CDSPosition int)}
	 */
	private static Annotation buildNoFrameShiftAnnotation(TranscriptInfo transcript, GenomeChange change,
			TranscriptPosition txPos, CDSPosition cdsPos, int exonNumber) {
		assert (false);
		return null;
	}

	//
	// old code below
	//

	/**
	 * Annotates an insertion variant. The fact that a variant is an insertion variant has been identified by the fact
	 * that the start and end position of the variant are equal and the reference sequence is indicated as "-".
	 *
	 * The insertion coordinate system uses "position after the current site"
	 *
	 * <ul>
	 * <li>"+" strand: refvarstart is the one-based number of the nucleotide just before the insertion.
	 * <li>"-" strand, the "after current site" becomes "before current site" during transcription. Therefore,
	 * appropriate handling is necessary to take this into account for example, for a trinucleotide GCC with frameshift
	 * of 1 and insertion of CCT in positive strand, it is G-CTT-CC but if the transcript is in negative strand, the
	 * genomic sequence should be GC-CCT-C, and transcript is G-AGG-GC
	 * </ul>
	 *
	 * @param tm
	 *            The gene in which the current mutation is contained
	 * @param frameShift
	 *            the location within the frame (0,1,2) in which mutation occurs
	 * @param wtnt3
	 *            The three nucleotides of codon affected by start of mutation
	 * @param wtnt3After
	 *            the three nucleotides of the codon following codon affected by mutation
	 * @param ref
	 *            - never used, could be removed
	 * @param var
	 * @param refVarStart
	 *            The start position of the variant with respect to the cNDA of the mRNA (see comments for "+"/"-"
	 *            strand)
	 * @param exonNumber
	 *            Number (one-based) of affected exon.
	 * @return an {@link jannovar.annotation.Annotation Annotation} object representing the current variant
	 *
	 * @throws jannovar.exception.AnnotationException
	 */

	public static Annotation getAnnotation(TranscriptModel tm, int frameShift, String wtnt3, String wtnt3After,
			String ref, String var, int refVarStart, int exonNumber) throws AnnotationException {

		/* for transcriptmodels on the '-' strand the mRNA position has to be adapted */
		if (tm.isMinusStrand())
			refVarStart--;

		// check if the insertion is actually a duplication,
		if (tm.isPlusStrand()) {

			/*
			 * "bla".substring(x,y) The substring begins at the x and extends to the character at index y - 1. Thus is
			 * we have ACGTACGT => ACGTGTACGT there is an insertion of GT c.3_4dupGT in this case, ref="-", var="GT",
			 * refvarstart="5"
			 */

			/*
			 * Note that the following two positions refer to the cDNA sequence, not to the coding sequence (CDS). To
			 * get the coding sequence position, we need to subtract the start position of the CDS. The following two
			 * variables are zero-based numbering, that is, we can use them to search in Java strings.
			 */
			int potentialDuplicationStartPos = refVarStart - var.length() - 1; // go back length of insertion
																				// (var.length()).
			int potentialDuplicationEndPos = refVarStart - 1; // pos right after insertion

			if (tm.getCDNASequence().substring(potentialDuplicationStartPos, potentialDuplicationEndPos).equals(var)) {
				Annotation ann = DuplicationAnnotationBuilder.getAnnotation(tm, frameShift, wtnt3, var,
						potentialDuplicationStartPos, potentialDuplicationEndPos, exonNumber);
				return ann;
			}
			/*
			 * Note that some duplications are located after the indicated position of the variant in the VCF file. The
			 * VCF convention seems to be to show the first duplicated base, whereas the HGVS convention is to show the
			 * last possible duplicated base or sequence.
			 */
			int pos = refVarStart - 1; /* the minus one is needed because Java strings are zero-based. */
			int varlen = var.length();
			boolean haveDuplication = false;
			// System.out.println("substr: '" + trmdl.getCdnaSequence().substring(pos, pos + var.length()) + "'");
			// System.out.println("ref: '" + ref + "'var: '" + var + "'");
			while (tm.getCDNASequence().length() > pos + var.length()
					&& tm.getCDNASequence().substring(pos, pos + var.length()).equals(var)) {
				// System.out.println(" " + pos + "\t" + (pos + var.length()) + "\tvs " +
				// trmdl.getCdnaSequence().length());
				pos += varlen;
				frameShift += varlen;
				haveDuplication = true;
			}
			if (haveDuplication) {
				int endpos = pos + var.length();
				frameShift = (frameShift % 3);
				// wtnt3 represents the three nucleotides of the wildtype codon.
				// try to get three new nucleotides affected by the duplication.
				// if it does not work, chicken out and just keep the previous ones
				// to avoid a dump
				String newwtnt3 = tm.getWTCodonNucleotides(refVarStart, frameShift);
				if (newwtnt3 != null && newwtnt3.length() == 3) {
					wtnt3 = newwtnt3;
				}
				Annotation ann = DuplicationAnnotationBuilder.getAnnotation(tm, frameShift, wtnt3, var, pos, endpos,
						exonNumber);
				return ann;
			}
		} else { /* i.e., we are on the minus strand. */
			/*
			 * If this variant is a duplication, then refvarstart is the last nucleotide position (one-based) on the
			 * chromosome of the segment that is duplicated. It is thus the first nucleotide of the duplication from the
			 * perspecitve of a gene on the "-" strand.
			 */
			// For example
			// chr1 177 . G GCAG
			// the genomic sequence has CAG at 175-177
			// The VCF tages the last G at 177 and uses it as an anchor to show the insertion.
			// if the gene is minus strand we have
			// ATTAGCCGCAGTTACAT
			// --------***-----
			// the duplicated sequence is
			// ATTAGCCGCAGCAGTTACAT
			// --------******-----
			int potentialDuplicationStartPos = refVarStart + 1; // go back length of insertion (var.length()).
			int potentialDuplicationEndPos = refVarStart + var.length() + 1; // pos right after insertion
			if (potentialDuplicationStartPos >= var.length()
					&& potentialDuplicationEndPos < tm.getMRNALength()
					&& tm.getCDNASequence().substring(potentialDuplicationStartPos, potentialDuplicationEndPos)
							.equals(var)) {
				Annotation ann = DuplicationAnnotationBuilder.getAnnotation(tm, frameShift, wtnt3, var,
						potentialDuplicationStartPos, potentialDuplicationEndPos, exonNumber);
				return ann;
			}

		}
		// in the case of ambiguities, shift towards 3' end of reference coordinate system
		if (tm.isPlusStrand()) {
			int idx = 0;
			// while (var.length() > idx + 1 && trmdl.getCdnaSequence().charAt(refvarstart) == var.charAt(idx)) {
			while (var.length() > idx && refVarStart < tm.getCDNASequence().length()
					&& tm.getCDNASequence().charAt(refVarStart) == var.charAt(idx)) {
				refVarStart++;
				idx++;
				frameShift++;
				var = new StringBuilder().append(var.substring(1)).append(var.charAt(0)).toString();

			}
		} else {
			int idx = 0;
			// while (var.length() > idx + 1 && trmdl.getCdnaSequence().charAt(refvarstart) == var.charAt(0)) {
			while (var.length() > idx && refVarStart < tm.getCDNASequence().length()
					&& tm.getCDNASequence().charAt(refVarStart) == var.charAt(0)) {
				refVarStart++;
				idx++;
				frameShift++;
				var = new StringBuilder().append(var.substring(1)).append(var.charAt(0)).toString();
			}

		}
		// after position correction check if we are still in the CDS
		if (refVarStart >= tm.getRefCDSEnd()) {
			return UTRAnnotationBuilder.createUTR3Annotation(tm, refVarStart, ref, var);
		}
		frameShift = frameShift % 3;

		int refcdsstart = tm.getRefCDSStart();
		int startPosMutationInCDS = refVarStart - refcdsstart + 1;
		int aavarpos = (int) Math.floor(startPosMutationInCDS / 3) + 1;

		Translator translator = Translator.getTranslator(); /* Singleton */
		String varnt3 = null;
		if (tm.isPlusStrand()) {
			if (frameShift == 1) { /* insertion located at 0-1-INS-2 part of codon */
				varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
			} else if (frameShift == 2) {
				varnt3 = String.format("%s%s", wtnt3, var);
			} else { /* i.e., frame_s == 0 */
				varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
			}
		} else if (tm.isMinusStrand()) {
			if (frameShift == 1) {
				varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
			} else if (frameShift == 2) {
				varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
			} else { /* i.e., frame_s == 0 */
				varnt3 = String.format("%s%s", var, wtnt3);
			}
		}
		String wtaa = translator.translateDNA(wtnt3);
		String wtaa_after = null;
		if (wtnt3After != null && wtnt3After.length() > 0) {
			wtaa_after = translator.translateDNA(wtnt3After);
		}

		/*
		 * wtaa_after could be undefined, if the current aa is the stop codon (X) example:17 53588444 53588444 - T
		 */
		String varaa = translator.translateDNA(varnt3);
		// int refcdsstart = trmdl.getRefCDSStart() ;

		// -------------------------------- end OLD --------------------------------------//

		// new: at least the next 5 codons are used and tarnaslated
		int nupstreamnuc = var.length() + 36;
		int end = tm.getCDNASequence().length() >= refVarStart + nupstreamnuc - frameShift ? refVarStart + nupstreamnuc
				- frameShift : tm.getCDNASequence().length();
		String wtnt = tm.isPlusStrand() ? tm.getCDNASequence().substring(refVarStart - 1 - frameShift, end) : tm
				.getCDNASequence().substring(refVarStart - frameShift, end);

		String varnt = null;
		if (tm.isPlusStrand()) {
			if (frameShift == 1) { /* insertion located at 0-1-INS-2 part of codon */
				varnt = String.format("%c%c%s%s", wtnt.charAt(0), wtnt.charAt(1), var, wtnt.substring(2));
			} else if (frameShift == 2) {
				varnt = String.format("%s%s%s", wtnt.substring(0, 3), var, wtnt.substring(3));
			} else { /* i.e., frame_s == 0 */
				varnt = String.format("%c%s%s", wtnt.charAt(0), var, wtnt.substring(1));
			}
		} else if (tm.isMinusStrand()) {
			if (frameShift == 1) {
				varnt = String.format("%c%s%s", wtnt.charAt(0), var, wtnt.substring(1));
			} else if (frameShift == 2) {
				varnt = String.format("%c%c%s%s", wtnt.charAt(0), wtnt.charAt(1), var, wtnt.substring(2));
			} else { /* i.e., frame_s == 0 */
				varnt = String.format("%s%s", var, wtnt);
			}
		}

		wtaa = translator.translateDNA(wtnt);
		varaa = translator.translateDNA(varnt);

		// System.out.println("wtnt: " + wtnt + "\tvarnt: " + varnt);
		// System.out.println("wtaa: " + wtaa + "\tvaraa: " + varaa);
		int i = 0;
		while (i < wtaa.length() - 1 && i < varaa.length() && wtaa.charAt(i) == varaa.charAt(i)) {
			i++;
		}

		// int aavarpos = (int) Math.floor((refvarstart-refcdsstart)/3)+1;
		// int startPosMutationInCDS = refvarstart-refcdsstart+1;

		/*
		 * Annovar: $canno = "c." . ($refvarstart-$refcdsstart+1) . "_" . ($refvarstart-$refcdsstart+2) . "ins$obs";
		 */
		String canno = String.format("c.%d_%dins%s", startPosMutationInCDS, refVarStart - refcdsstart + 2, var);
		/* If length of insertion is a multiple of 3 */
		if (var.length() % 3 == 0) {
			if (wtaa.equals("*")) { /* Mutation affects the wildtype stop codon */
				int idx = varaa.indexOf("*");
				if (idx >= 0) {
					/*
					 * delete all aa after stop codon, but keep the aa before Note we use an asterisk (*) to denote the
					 * stop codon according to HGVS nomenclature annovar: $varaa =~ s/\*.* /X/;
					 */
					varaa = String.format("%s*", varaa.substring(0, idx + 1));
					String annot = String.format("%s:exon:%d:%s:p.X%ddelins%s", tm.getName(), exonNumber, canno,
							aavarpos, varaa);

					/*
					 * $function->{$index}{nfsins} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.X$varpos" .
					 * "delins$varaa,"; #stop codon is stil present
					 */
					// Annotation ann =
					// Annotation.createNonFrameshiftInsertionAnnotation(trmdl,startPosMutationInCDS,annot);
					Annotation ann = new Annotation(tm, annot, VariantType.NON_FS_INSERTION, startPosMutationInCDS);
					return ann;
				} else {
					/*
					 * Mutation => stop codon is lost, i.e., STOPLOSS Annovar:
					 * $seqid:exon$exonpos:$canno:p.X$varpos" . "delins$varaa";
					 */
					String annot = String.format("%s:exon%d:%s:p.X%ddelins%s", tm.getName(), exonNumber, canno,
							aavarpos, varaa.charAt(i));
					// Annotation ann = Annotation.createStopLossAnnotation(trmdl,startPosMutationInCDS,annot);
					Annotation ann = new Annotation(tm, annot, VariantType.STOPLOSS, startPosMutationInCDS);
					return ann;
				}
			} else { /* i.w., wtaa is not equal to '*' */
				int idx = varaa.indexOf("*");
				if (idx >= 0) { /* corresponds to annovar: if ($varaa =~ m/\* /) { */
					varaa = String.format("%s*", varaa.substring(0, idx + 1));
					/* i.e., delete all aa after stop codon, but keep the aa before */
					// System.out.println("wtaa: " + wtaa);
					// System.out.println("wtnt: " + wtnt + "\tvarnt: " + varnt);
					// System.out.println("varaa: " + varaa);
					// System.out.println(String.format("%s:exon%d:%s:p.%s%d_%s%d%ins%s", trmdl.getName(), exonNumber,
					// canno, wtaa.charAt(i), aavarpos, varaa));
					String annot;
					// if(wtnt.length() == 0){
					// annot = String.format("%s:exon%d:%s:p.%s%ddelins%s", trmdl.getName(), exonNumber, canno,
					// wtaa.charAt(i), aavarpos, varaa);
					// }else
					annot = String.format("%s:exon%d:%s:p.%s%ddelins%s", tm.getName(), exonNumber, canno,
							wtaa.charAt(i), aavarpos, varaa);

					Annotation ann = new Annotation(tm, annot, VariantType.STOPGAIN, startPosMutationInCDS);
					return ann;
				} else { // no new 'STOP' codon inserted
					String annot;
					// check for Proteine Duplicates - only possible if there are enough nucleotides before
					String ref5upAA = "";
					if (refVarStart - frameShift - var.length() >= 0) {
						String ref5up;
						if (tm.isPlusStrand()) // correct for "-"-strand
							ref5up = tm.getCDNASequence().substring(refVarStart - 1 - frameShift - var.length(),
									refVarStart - frameShift - 1);
						else
							ref5up = tm.getCDNASequence().substring(refVarStart - frameShift - var.length(),
									refVarStart - frameShift);
						ref5upAA = translator.translateDNA(ref5up);

					}
					// System.out.println("ref5upAA: " + ref5upAA);
					// System.out.println("varaa: " + varaa);
					// System.out.println("i: " + i);
					// System.out.println("varlength: " + var.length());
					if (ref5upAA.equals(varaa.substring(i, (var.length() / 3) + i))) { // is the inserted AA a
																						// Duplication ...?
						if (var.length() / 3 == 1)
							annot = String.format("%s:exon%d:%s:p.%s%ddup", tm.getName(), exonNumber, canno, ref5upAA,
									aavarpos - 1 + i);
						else
							annot = String.format("%s:exon%d:%s:p.%s%d_%s%ddup", tm.getName(), exonNumber, canno,
									ref5upAA.charAt(0), aavarpos - 1 - (var.length() / 3) + i,
									ref5upAA.charAt(ref5upAA.length() - 1), aavarpos - 1 + i);
					} else {
						String construct = wtaa.substring(0, i) + varaa.substring(i, (var.length() / 3) + i)
								+ wtaa.substring(i);
						if (varaa.equals(construct)) { // is it possible to simply generate the varAA by inserting the
														// 'new' AA in the RefAA?
							if (i > 0) {
								// System.out.println(trmdl.getChromosomeAsString());
								// System.out.println(trmdl.getGeneSymbol());
								// System.out.println(trmdl.getChromosomalCoordinates(refvarstart, refvarstart)[0] +
								// "\t" + trmdl.getChromosomalCoordinates(refvarstart, refvarstart)[1]);
								// System.out.println("ref: " + ref);
								// System.out.println("alt: " + var);
								// System.out.println("i: " + i);
								// System.out.println("wtaa: " + wtaa.length() + "\t" + wtaa);
								// System.out.println("varaa: " + varaa.length() + "\t" + varaa);
								annot = String.format("%s:exon%d:%s:p.%s%d_%s%dins%s", tm.getName(), exonNumber, canno,
										wtaa.charAt(i - 1), aavarpos - 1, wtaa.charAt(i), aavarpos, varaa.charAt(i));
							} else {
								// System.out.println("ref: " + ref + "\tvar: " + var);
								// System.out.println(trmdl.getName());
								// System.out.println("exon: " + exonNumber);
								// System.out.println("canno: " + canno);
								// System.out.println("ref5upAA: " + ref5upAA);
								// System.out.println("aavarpos: " + aavarpos);
								// System.out.println("i: " + i);
								// System.out.println("wtaa length: " + wtaa.length());
								// System.out.println("wtaa.charAt(i): " + wtaa.charAt(i));
								// System.out.println("aavarpos: " + aavarpos);
								// System.out.println("varaa.charAt(i): " + varaa.charAt(i));
								// if the insertion is at the first position we need the AA before
								annot = String.format("%s:exon%d:%s:p.%s%d_%s%dins%s", tm.getName(), exonNumber, canno,
										ref5upAA, aavarpos - 1, wtaa.charAt(i), aavarpos, varaa.charAt(i));
							}
						} else
							annot = String.format("%s:exon%d:%s:p.%s%ddelins%s", tm.getName(), exonNumber, canno,
									wtaa.charAt(i), aavarpos, varaa.substring(i, (var.length() / 3) + 1 + i));
					}
					Annotation ann = new Annotation(tm, annot, VariantType.NON_FS_INSERTION, startPosMutationInCDS);
					return ann;
				}
			}
		} else { /* i.e., length of variant is not a multiple of 3 */
			if (wtaa.startsWith("*")) { /* mutation on stop codon */
				int idx = varaa.indexOf("*"); /* corresponds to : if ($varaa =~ m/\* /) { */
				if (idx == 0) {
					String annot = String.format("%s:exon%d:%s:p.=", tm.getName(), exonNumber, canno);
					Annotation ann = new Annotation(tm, annot, VariantType.FS_INSERTION, startPosMutationInCDS);
					return ann;
				}

				if (idx > 0) {
					/*
					 * in reality, this cannot be differentiated from non-frameshift insertion, but we'll still call it
					 * frameshift
					 */
					/*
					 * delete all aa after stop codon, but keep the aa before annovar: $varaa =~ s/\*.* /X/;
					 */
					varaa = String.format("%sX", varaa.substring(0, idx + 1));
					String annot = String.format("%s:exon%d:%s:p.X%ddelins%s", tm.getName(), exonNumber, canno,
							aavarpos, varaa.charAt(i));
					Annotation ann = new Annotation(tm, annot, VariantType.FS_INSERTION, startPosMutationInCDS);
					return ann;
				} else { /* var aa is not stop (*) */
					String annot = String.format("%s:exon%d:%s:p.X%ddelins%s", tm.getName(), exonNumber, canno,
							aavarpos, varaa.charAt(i));
					Annotation ann = new Annotation(tm, annot, VariantType.STOPLOSS, startPosMutationInCDS);
					return ann;
				}
			} else { /* i.e., wtaa not a stop codon */
				int idx = varaa.indexOf("*");
				if (idx >= 0 && idx < Math.ceil((double) var.length() / 3)) {
					/** Note use of asterisk (*) to denote stop codon as per HGVS recommendation. */
					varaa = String.format("%s*", varaa.substring(0, idx + 1));
					/*
					 * "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos" . "_$wtaa_after" . ($varpos+1) .
					 * "delins$varaa,";
					 */

					String annot = String.format("%s:exon%d:%s:p.%s%d_%s%ddelins%s", tm.getName(), exonNumber, canno,
							wtaa.charAt(i), aavarpos, wtaa_after, (aavarpos + 1), varaa);
					Annotation ann = new Annotation(tm, annot, VariantType.STOPGAIN, startPosMutationInCDS);
					return ann;
				} else if (aavarpos == 1) { /* mutation of start codon */
					String annot = String.format("%s:exon%d:%s:p.%s%d?", tm.getName(), exonNumber, canno,
							wtaa.charAt(i), aavarpos);
					Annotation ann = new Annotation(tm, annot, VariantType.START_LOSS, startPosMutationInCDS);
					return ann;

				} else {
					String annot;
					// if its at the last position and
					if ((tm.isPlusStrand() && frameShift != 1) | (tm.isMinusStrand() && frameShift != 2))
						annot = String.format("%s:exon%d:%s:p.%s%dfs", tm.getName(), exonNumber, canno, wtaa.charAt(i),
								aavarpos);
					else
						annot = String.format("%s:exon%d:%s:p.%s%dfs", tm.getName(), exonNumber, canno, wtaa.charAt(i),
								aavarpos + 1);
					Annotation ann = new Annotation(tm, annot, VariantType.FS_INSERTION, startPosMutationInCDS);
					return ann;

				}
			}
		}
	}
}
