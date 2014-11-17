package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.GenomeChange;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptModel;
import jannovar.util.Translator;

// TODO(holtgrem): Add case for non-coding transcripts.
// TODO(holtgrem): Add deleted nucleotide base values for non-transcript parts?

/**
 * This class provides static methods to generate annotations for deletions in exons.
 *
 * <h2>Older Comment</h2>
 *
 * This class provides static methods to generate annotations for deletion mutations. Updated on 27 December 2013 to
 * provide HGVS conformation annotations for frameshirt deletion mutations. Note that if we have the following VCF line:
 *
 * <pre>
 * chr11	76895771	.	GGAGGCGGGGACACCAGGGCCTG	G	55.5	.	DP=9;VDB...
 * </pre>
 *
 * then the position refers to the nucleotide right before the deletion. That is, the first nucleotide [G]GAGGC.. (the
 * one that is enclosed in square brackets) has the position 76895771, and the deletion begins at chromosomal position
 * 76895772 and comprises 22 bases: GAG-GCG-GGG-ACA-CCA-GGG-CCT-G. (Note we are using one-based numbering here). This
 * particular deletion corresponds to NM_001127179(MYO7A_v001):c.3515_3536del
 * NM_001127179(MYO7A_i001):p.(Gly1172Glufs*34).
 *
 * @version 0.17 (14 January, 2014)
 * @author Peter N Robinson
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */

public class DeletionAnnotationBuilder {

	/**
	 * Returns a {@link Annotation} for the deletion {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @note In some cases, the position of deletions cannot be normalized/shifted since we have no sequence for the
	 *       intronic regions.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for, must describe a deletion in
	 *            <code>transcript</code>
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe an insertion
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Guard against invalid genome change.
		if (change.getRef().length() == 0 || change.getAlt().length() != 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a deletion.");

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());

		// Forward everything to the helper.
		return new DeletionAnnotationBuilderHelper(transcript, change).build();
	}



	// public static Annotation buildAnnotationIntron(TranscriptInfo transcript, GenomeChange change)
	// throws ProjectionException {
	// TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
	//
	// // Compute location annotation string.
	// int intronNumber = projector.locateIntron(change.getPos());
	// assert (intronNumber != INVALID_INTRON_ID);
	// String locAnno = String.format("%s:intron%d", transcript.accession, intronNumber);
	//
	// // Compute cDNA annotation string.
	// GenomeInterval exonRegion = transcript.exonRegions[intronNumber]; // is 0-based
	// int posOffset = change.getPos().withPositionType(PositionType.ZERO_BASED)
	// .differenceTo(exonRegion.getGenomeEndPos()) + 1; // 1-based position in intron
	// TranscriptPosition lastExonPos = projector.genomeToTranscriptPos(exonRegion.getGenomeEndPos().shifted(-1));
	// int exonBasePos = lastExonPos.withPositionType(PositionType.ONE_BASED).getPos();
	// String cDNAAnno = String.format("c.%d+%d_%d+%ddel%s", exonBasePos, posOffset, exonBasePos, posOffset
	// + change.getAlt().length(), change.getAlt());
	//
	// // Build annotation.
	// return new Annotation(transcript.transcriptModel, String.format("%s:%s", locAnno, cDNAAnno),
	// VariantType.INTRONIC);
	// }

	/**
	 * Creates annotation for a single-nucleotide deletion.
	 *
	 * @param kgl
	 *            The known gene that corresponds to the deletion caused by the variant.
	 * @param frameShift
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param wtnt3After
	 *            Nucleotide sequence of codon following that affected by variant
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refVarStart
	 *            Position of the variant in the entire transcript (one-based)
	 * @param exonNumber
	 *            Number of the affected exon.
	 * @return An annotation corresponding to the deletion.
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getAnnotationSingleNucleotide(TranscriptModel kgl, int frameShift, String wtnt3,
			String wtnt3After, String ref, String var, int refVarStart, int exonNumber) throws AnnotationException {
		// shift
		if (kgl.isPlusStrand())
			while (kgl.getCDNASequence().charAt(refVarStart - 2) == kgl.getCDNASequence().charAt(
					refVarStart + ref.length() - 2)) {
				refVarStart++;
				ref = new StringBuilder().append(ref.substring(1)).append(ref.charAt(0)).toString();

			}
		else
			while (kgl.getCDNASequence().charAt(refVarStart - 1) == kgl.getCDNASequence().charAt(
					refVarStart + ref.length() - 1)) {
				refVarStart++;
				ref = new StringBuilder().append(ref.substring(1)).append(ref.charAt(0)).toString();

			}

		Translator translator = Translator.getTranslator(); /* Singleton */
		// varnt3 is the codon affected by the deletion, it is the codon that
		// results from the deletion at the same position in the aa as the wt codon was.
		String varnt3;
		int posVariantInCDS = refVarStart - kgl.getRefCDSStart() + 1; /* position of deletion within coding sequence */
		/*
		 * Note that posVariantInCDS is one-based. If pos%3==0, we are at the last base of a codon, and we can just
		 * divide by 3; otherwise, we need to take the floor, because we are at the first or second base of a codon.
		 */
		int aavarpos;
		if ((posVariantInCDS % 3) == 0)
			aavarpos = posVariantInCDS / 3;
		else
			aavarpos = (int) Math.floor(posVariantInCDS / 3) + 1; /* position of deletion in protein */
		/*
		 * System.out.println(kgl.getGeneSymbol() + "(" + kgl.getAccessionNumber() + ") " + " frame_s=" + frame_s +
		 * "; wtnt3=" + wtnt3 + "; wtnt3_after=" + wtnt3_after + "; ref=" + ref + ";  alt="+var +
		 * "; refvarstart=  "+refvarstart);
		 */

		/*
		 * Note that in some pathological cases, wtnt3_after is null. This is the case with chr11 64366391 . TG T, which
		 * affects multiple transcripts of the SLC22A12 gene including uc009ypr.1. The deletion of a G affects a
		 * sequence TGC-TG where the transcript ends abruptly with the 2 nucleotide partial transcript TG, so that
		 * wtnt3=TGC and wtnt3_after=null. In cases like this, we will just return the nucleotide deletion and not
		 * attempt to translate to protein.
		 */
		if (wtnt3After == null || wtnt3After.length() < 3) {
			String canno = String.format("%s:exon%d:c.%ddel", kgl.getName(), exonNumber, posVariantInCDS);
			Annotation ann = new Annotation(kgl, canno, VariantType.FS_DELETION, posVariantInCDS);
			return ann;
		}

		if (frameShift == 1) {
			varnt3 = String.format("%c%c%s", wtnt3.charAt(0), wtnt3.charAt(2), wtnt3After.charAt(0));
		} else if (frameShift == 2) {
			varnt3 = String.format("%c%c%s", wtnt3.charAt(0), wtnt3.charAt(1), wtnt3After.charAt(0));
		} else {
			varnt3 = String.format("%c%c%s", wtnt3.charAt(1), wtnt3.charAt(2), wtnt3After.charAt(0));
		}

		String wtaa = translator.translateDNA(wtnt3);
		String varaa = translator.translateDNA(varnt3);

		/* The following gives us the cDNA annotation */
		String canno = String.format("c.%ddel", posVariantInCDS);
		/* Now create amino-acid annotation */
		if (wtaa.equals("*")) { /* mutation on stop codon */
			if (varaa.startsWith("*")) { /* #stop codon is still stop codon if ($varaa =~ m/\* /) */
				String nfsdel_ann = String.format("%s:exon%d:%s:p.X%dX", kgl.getName(), exonNumber, canno, aavarpos);
				Annotation ann = new Annotation(kgl, nfsdel_ann, VariantType.NON_FS_DELETION, posVariantInCDS);
				return ann;
			} else { /* stop codon is lost */
				String stoploss_ann = String.format("%s:exon%d:%s:p.X%d%s", kgl.getName(), exonNumber, canno, aavarpos,
						varaa);
				Annotation ann = new Annotation(kgl, stoploss_ann, VariantType.STOPLOSS, posVariantInCDS);
				return ann;
			}
		} else {
			if (varaa.contains("*")) { /* new stop codon created */
				String stopgain_ann = String.format("%s:exon%d:%s:p.%s%dX", kgl.getName(), exonNumber, canno, wtaa,
						aavarpos);
				Annotation ann = new Annotation(kgl, stopgain_ann, VariantType.STOPGAIN, posVariantInCDS);
				return ann;
			} else {
				/* A deletion affecting an amino-acid in the middle of the protein and leading to a frameshift */
				String fsdel_ann = String.format("%s:exon%d:%s:p.%s%dfs", kgl.getName(), exonNumber, canno, wtaa,
						aavarpos);
				Annotation ann = new Annotation(kgl, fsdel_ann, VariantType.FS_DELETION, posVariantInCDS);
				return ann;
			}
		}
	}

	/**
	 * Creates annotation for a deletion of more than one nucleotide. This is recognized by the fact that the ref
	 * sequence has a length greater than one and the variant sequence is "-".
	 * <P>
	 * Note that with the $firstcodondel option set to true, annovar reports deletions that affect the first amino acid
	 * as ABC:uc001ab:wholegene (FSDEL). We will not follow annovar here, but rather report such as deletion as with any
	 * other amino acid.
	 *
	 * @param kgl
	 *            The known gene that corresponds to the deletion caused by the variant.
	 * @param frameShift
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param wtnt3After
	 *            Nucleotide sequence of codon following that affected by variant
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refVarStart
	 *            start position of the variant in the mRNA of the transcript
	 * @param refVarEnd
	 *            end position of the variant in the mRNA of the transcript
	 * @param exonNumber
	 *            Number of the affected exon (one-based: TODO chekc this).
	 * @return {@link Annotation} object corresponding to deletion variant
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getMultinucleotideDeletionAnnotation(TranscriptModel kgl, int frameShift, String wtnt3,
			String wtnt3After, String ref, String var, int refVarStart, int refVarEnd, int exonNumber)
			throws AnnotationException {
		// shift
		while (kgl.getCDNASequence().length() > refVarStart + ref.length()
				&& kgl.getCDNASequence().charAt(refVarStart - 1) == kgl.getCDNASequence().charAt(
						refVarStart + ref.length() - 1)) {
			refVarStart++;
			refVarEnd++;
			ref = new StringBuilder().append(ref.substring(1)).append(ref.charAt(0)).toString();
			frameShift++;
		}
		frameShift = frameShift % 3;

		Translator translator = Translator.getTranslator(); /* Singleton */
		String canno = null;
		String protAnno = null;
		String wtaa = translator.translateDNA(wtnt3);
		int refCDSStart = kgl.getRefCDSStart();
		int cdsLen = kgl.getCDSLength();
		// Following correction on 30 Mar 2014.
		// int aavarpos = (int)Math.floor((refvarstart-kgl.getRefCDSStart())/3)+1;
		/*
		 * Note that posVariantInCDS is one-based. If pos%3==0, we are at the last base of a codon, and we can just
		 * divide by 3; otherwise, we need to take the floor, because we are at the first or second base of a codon.
		 */
		int posVariantInCDS = refVarStart - kgl.getRefCDSStart() + 1; /* position of deletion within coding sequence */
		int aaVarPos;
		if ((posVariantInCDS % 3) == 0)
			aaVarPos = posVariantInCDS / 3;
		else
			aaVarPos = (int) Math.floor(posVariantInCDS / 3) + 1; /* position of deletion in protein */

		int varPosEnd = -1; // the position of the last amino acid in the deletion

		if (refVarStart <= refCDSStart) { /* first amino acid deleted */
			if (refVarEnd >= cdsLen + refCDSStart) { // i.e., 3' portion of the gene is deleted
				varPosEnd = (int) Math.floor(cdsLen / 3);
				canno = String.format("c.%d_%ddel", refVarStart - refCDSStart, cdsLen + refCDSStart - 1);

			} else { /* deletion encompasses less than entire CDS */
				varPosEnd = (int) Math.floor((refVarEnd - refCDSStart) / 3) + 1;
				canno = String.format("c.1_%ddel", refVarEnd - refVarStart + 1);
			}
			protAnno = String.format("%s:exon%d:%s:p.%d_%ddel", kgl.getName(), exonNumber, canno, aaVarPos, varPosEnd);
			Annotation ann = new Annotation(kgl, protAnno, VariantType.FS_SUBSTITUTION, posVariantInCDS);
			return ann;
		} else if (refVarEnd >= cdsLen + refCDSStart - 1) {
			// if we get here, then the 3' part of the gene is deleted
			varPosEnd = (int) Math.floor(cdsLen / 3);
			// System.out.println("ref=" + ref + ", var=" +var);
			canno = String.format("c.%d_%ddel", refVarStart - refCDSStart + 1, cdsLen + refCDSStart - 1);
			protAnno = String.format("%s:exon%d:%s:p.%s%d_*%ddel", kgl.getName(), exonNumber, canno, wtaa.charAt(0),
					aaVarPos, varPosEnd);
			Annotation ann = new Annotation(kgl, protAnno, VariantType.STOPLOSS, posVariantInCDS);
			// panno = String.format("%s:exon%d:%s:p.%d_%ddel", kgl.getName(), exonNumber, canno, aavarpos, varposend);
			// Annotation ann = new Annotation(kgl, panno, VariantType.FS_SUBSTITUTION, posVariantInCDS);
			return ann;
		} else if ((refVarEnd - refVarStart + 1) % 3 == 0) {
			// --------------------------------------------------------------
			// non-frameshift deletion within the body of the mRNA
			// --------------------------------------------------------------
			varPosEnd = (int) Math.floor((refVarEnd - refCDSStart) / 3) + 1;
			posVariantInCDS = refVarStart - refCDSStart + 1; /* start pos of mutation */
			canno = String.format("c.%d_%ddel", posVariantInCDS, refVarEnd - refCDSStart + 1);
			if (aaVarPos == varPosEnd) {
				if (frameShift == 0) {
					protAnno = String
							.format("%s:exon%d:%s:p.%s%ddel", kgl.getName(), exonNumber, canno, wtaa, aaVarPos);
				} else {
					protAnno = String.format("%s:exon%d:%s:p.%ddel", kgl.getName(), exonNumber, canno, aaVarPos);

				}
			} else { /* i.e., two or more amino acids are deleted */
				// int end_frame_s = (frame_s + var.length() - 1)%3;
				if (frameShift == 0) {
					String endcodon = kgl.getCodonAt(refVarEnd - 2, frameShift); // ??????
					String endAA = translator.translateDNA(endcodon);
					// System.out.println("END codon=" + endcodon + " aa=" + endaa + " frame_s = " + frame_s);// +
					// " end_frame_s = " + end_frame_s);
					// System.out.println("refvarstart=" + refvarstart + ", refvarend = " + refvarend);
					protAnno = String.format("%s:exon%d:%s:p.%s%d_%s%ddel", kgl.getName(), exonNumber, canno, wtaa,
							aaVarPos, endAA, varPosEnd);
				} else {
					String startCodon = kgl.getCodonAt(refVarStart, frameShift);
					String endCodon = kgl.getCodonAt(refVarEnd + 1, frameShift); // ??????

					String startAA = translator.translateDNA(startCodon);
					String endAA = translator.translateDNA(endCodon);
					String mutCodon = kgl.getCDNASequence().substring(refVarStart - frameShift - 1, refVarStart - 1)
							+ kgl.getCDNASequence().substring(refVarEnd, refVarEnd + (3 - frameShift));
					String mutAA = translator.translateDNA(mutCodon);
					boolean newsameAA = false;
					/* check that the 'new' aminoacid does'nt match the start or end AA */
					// System.out.println("refvarstart: " + refvarstart);
					// System.out.println("refvarend: " + refvarend);
					// System.out.println("cds end: " + kgl.getRefCDSEnd());
					// System.out.println("ref.length: " + ref.length());
					// System.out.println("frame_s: " + frame_s);
					// System.out.println("kgl: " + kgl);
					while (startAA.equals(mutAA)) {
						// this is a somewhat dirty hack. If the end of the deletion is outside of the CDS, we will just
						// skip the adaption.
						if (refVarStart + ref.length() > kgl.getRefCDSEnd() - frameShift)
							break;
						refVarStart += 3;
						startAA = translator.translateDNA(kgl.getCodonAt(refVarStart, frameShift));
						mutAA = translator.translateDNA(kgl.getCodonAt(refVarStart + ref.length(), frameShift));
						aaVarPos++;
						newsameAA = true;
					}
					if (endAA.equals(mutAA) && aaVarPos <= varPosEnd) {
						endCodon = kgl.getCodonAt(refVarEnd + 1 - 3, frameShift);
						endAA = translator.translateDNA(endCodon);
						varPosEnd--;
						newsameAA = true;
					}

					if (aaVarPos >= varPosEnd)
						protAnno = String.format("%s:exon%d:%s:p.%s%ddel", kgl.getName(), exonNumber, canno, startAA,
								aaVarPos);
					else if (newsameAA) // if the combined codon replaces the first or last original AA
						protAnno = String.format("%s:exon%d:%s:p.%s%d_%s%ddel", kgl.getName(), exonNumber, canno,
								startAA, aaVarPos, endAA, varPosEnd);
					else
						protAnno = String.format("%s:exon%d:%s:p.%s%d_%s%ddelins%s", kgl.getName(), exonNumber, canno,
								startAA, aaVarPos, endAA, varPosEnd, mutAA);
				}
			}
			return new Annotation(kgl, protAnno, VariantType.NON_FS_DELETION, posVariantInCDS);
		} else {
			// --------------------------------------------------------------
			// frameshift deletion within the body of the mRNA
			// --------------------------------------------------------------
			int posMutationInCDS = refVarStart - refCDSStart + 1; /* start pos of mutation with respect to CDS begin */
			boolean isStoploss = false;
			// deletion affects STOP codon
			if (kgl.getRefCDSEnd() - 3 < refVarStart + ref.length())
				isStoploss = true;
			// deletion stretches inside from CDS into 3#UTR
			if (kgl.getRefCDSEnd() < refVarStart + ref.length()) {
				canno = String.format("c.%d_*%ddel", posMutationInCDS, kgl.getRefCDSEnd()
						- (refVarStart + ref.length()));
			} else {

				// varposend = (int)Math.floor(( refvarend- refcdsstart)/3) + 1;
				canno = String.format("c.%d_%ddel", posMutationInCDS, refVarEnd - refCDSStart + 1);
			}
			// System.out.println(kgl.getAccessionNumber() + ":" + canno);
			try {
				protAnno = shiftedFrameDeletion(kgl, exonNumber, canno, ref, posMutationInCDS, aaVarPos, frameShift);
			} catch (AnnotationException e) {
				System.err.println("Exception while annotating frame-shift deletion: " + canno);
				protAnno = canno; /* just supply the cDNA annotation if there was an error. */
			}
			Annotation ann = isStoploss ? new Annotation(kgl, protAnno, VariantType.STOPLOSS, posMutationInCDS)
					: new Annotation(kgl, protAnno, VariantType.FS_DELETION, posMutationInCDS);
			return ann;
		}
	}

	/**
	 * Gets the correct annotation for a deletion that has led to a frameshift, such as p.(Gln40Profs*18), which results
	 * from a deletion of k nucleotides where k is not a multiple of 3.
	 */
	private static String shiftedFrameDeletion(TranscriptModel trmdl, int exonNumber, String cDNAanno, String ref,
			int posMutationInCDS, int aaVarStartPos, int frameShift) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */

		// Get the complete coding sequence.
		// Also include the 3UTR because some deletions extend the
		// mutant coding sequence beyond the stop codon.
		String orf = trmdl.getCodingSequencePlus3UTR();

		int start = posMutationInCDS - 1; // Convert 1-based to 0-based
		int endpos = start + ref.length(); // endpos is now 0-based and points to one after the deletion.
		// int endpos = start + ref.length() > orf.length() ? orf.length() : start + ref.length();

		// System.out.println("start: " + start + "\tend: " + endpos);
		// System.out.println("orf: " + orf.length() + "\tref: " + ref.length());
		String deletion = orf.substring(start, endpos);
		// Get the part of the codon that comes before the deletion
		String prefix = orf.substring(start - frameShift, start);

		// We do not know when the new sequence will differ from the wt sequence.
		// Try at least 30 amino acids.
		int restlen = (orf.length() - endpos) > 30 ? 30 : orf.length() - endpos;
		String rest = orf.substring(endpos, endpos + restlen);
		String wt = prefix + deletion + rest;
		String mut = prefix + rest;
		String wtaa = translator.translateDNA(wt);
		String mutaa = translator.translateDNA(mut);

		int aapos = aaVarStartPos;
		int endk = mutaa.length();

		// System.out.println(trmdl.getAccessionNumber());
		// System.out.println("wt na: " + wt + "\taa: " + wtaa);
		// System.out.println("mut na: " + mut + "\taa: " + mutaa);
		// System.out.println("posMutationInCDS: " + posMutationInCDS + "\tref.length: " + ref.length() +
		// "\tCdna.length: " + trmdl.getCdnaSequence().length());
		// System.out.println("RefCDSend: " + trmdl.getRefCDSEnd() + "\tCDSLength: " + trmdl.getCDSLength() +
		// "\tRefCDSStart: " + trmdl.getRefCDSStart());

		// We have to check if the deletion affects the stop-codon additionally we have to check if the STop codon
		// is coplettly deletet, or if there pop-up a new STOP codon, or if there is not enpough sequence info to
		// predict this and then just say, there is a new STOP at the deletion STart (e.g. if the 3'UTR is missing and
		// only one or two bp are left)

		String annot = null;
		// if ((trmdl.getRefCDSStart() - 2 + ref.length() + posMutationInCDS - trmdl.getCdnaSequence().length()) < 3 ||
		// (wtaa.contains("*") && !mutaa.contains("*"))) {
		if (trmdl.getRefCDSEnd() - (trmdl.getRefCDSStart() - 2 + ref.length() + posMutationInCDS) < 3
				|| (wtaa.substring(0, ref.length() / 3 + 1).contains("*") && !mutaa.contains("*"))) {
			// System.out.println("RefCDSEnd: " + trmdl.getRefCDSEnd() + "\t" + (trmdl.getRefCDSStart() - 2 +
			// ref.length() + posMutationInCDS));
			int k;
			for (k = 0; k < endk; ++k) {
				if (wtaa.charAt(k) == mutaa.charAt(k)) {
					aapos++;

				}
			}
			int aaend_frame = (posMutationInCDS + ref.length()) % 3;
			int aaendPos = aaend_frame == 0 ? (posMutationInCDS + ref.length()) / 3
					: (posMutationInCDS + ref.length()) / 3 + 1;
			annot = String.format("%s:exon%d:%s:p.%c%d_%c%ddel", trmdl.getName(), exonNumber, cDNAanno, wtaa.charAt(k),
					aapos, wtaa.charAt(wtaa.length() - 1), aaendPos);
			return annot;// e.g. p.(Gln40Profs*18)
		}

		for (int k = 0; k < endk; ++k) {
			if (wtaa.charAt(k) != mutaa.charAt(k)) {
				annot = String.format("%s:exon%d:%s:p.%c%dfs", trmdl.getName(), exonNumber, cDNAanno, wtaa.charAt(k),
						aapos);
				return annot;// e.g. p.(Gln40Profs*18)
			} else {
				aapos++;
			}
		}
		// --------------------------------------------------------------
		// if we get here, all amino acids were the same.
		// --------------------------------------------------------------
		// probably some weird nomenclature.
		// panno = String.format("%s:exon%d:%s:p.%d_%ddel",kgl.getName(),
		// exonNumber,canno,aavarpos,varposend);
		annot = String.format("%s:exon%d:%s:p.%c%ddelins%c", trmdl.getName(), exonNumber, cDNAanno, wtaa.charAt(0),
				aaVarStartPos, mutaa.charAt(0));
		return annot;
	}

	/**
	 * Creates annotation for a single-nucleotide deletion.
	 *
	 * @param kgl
	 *            The known gene that corresponds to the deletion caused by the variant.
	 * @param frame_s
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param wtnt3_after
	 *            Nucleotide sequence of codon following that affected by variant
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refvarstart
	 *            Position of the variant in the entire transcript (one-based)
	 * @param exonNumber
	 *            Number of the affected exon.
	 * @return An annotation corresponding to the deletion.
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getAnnotationSingleNucleotideLong(TranscriptModel kgl, int frame_s, String wtnt3,
			String wtnt3_after, String ref, String var, int refvarstart, int exonNumber) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */
		// varnt3 is the codon affected by the deletion, it is the codon that
		// results from the deletion at the same position in the aa as the wt codon was.
		String varnt3;
		int posVariantInCDS = refvarstart - kgl.getRefCDSStart() + 1; /* position of deletion within coding sequence */
		/*
		 * Note that posVariantInCDS is one-based. If pos%3==0, we are at the last base of a codon, and we can just
		 * divide by 3; otherwise, we need to take the floor, because we are at the first or second base of a codon.
		 */
		int aavarpos;
		if ((posVariantInCDS % 3) == 0)
			aavarpos = posVariantInCDS / 3;
		else
			aavarpos = (int) Math.floor(posVariantInCDS / 3) + 1; /* position of deletion in protein */
		/*
		 * System.out.println(kgl.getGeneSymbol() + "(" + kgl.getAccessionNumber() + ") " + " frame_s=" + frame_s +
		 * "; wtnt3=" + wtnt3 + "; wtnt3_after=" + wtnt3_after + "; ref=" + ref + ";  alt="+var +
		 * "; refvarstart=  "+refvarstart);
		 */

		/*
		 * Note that in some pathological cases, wtnt3_after is null. This is the case with chr11 64366391 . TG T, which
		 * affects multiple transcripts of the SLC22A12 gene including uc009ypr.1. The deletion of a G affects a
		 * sequence TGC-TG where the transcript ends abruptly with the 2 nucleotide partial transcript TG, so that
		 * wtnt3=TGC and wtnt3_after=null. In cases like this, we will just return the nucleotide deletion and not
		 * attempt to translate to protein.
		 */
		if (wtnt3_after == null || wtnt3_after.length() < 3) {
			String canno = String.format("%s:exon%d:c.%ddel%s", kgl.getName(), exonNumber, posVariantInCDS, ref);
			Annotation ann = new Annotation(kgl, canno, VariantType.FS_DELETION, posVariantInCDS);
			return ann;
		}

		if (frame_s == 1) {
			varnt3 = String.format("%c%c%s", wtnt3.charAt(0), wtnt3.charAt(2), wtnt3_after.charAt(0));
		} else if (frame_s == 2) {
			varnt3 = String.format("%c%c%s", wtnt3.charAt(0), wtnt3.charAt(1), wtnt3_after.charAt(0));
		} else {
			varnt3 = String.format("%c%c%s", wtnt3.charAt(1), wtnt3.charAt(2), wtnt3_after.charAt(0));
		}

		String wtaa = translator.translateDNA(wtnt3);
		String varaa = translator.translateDNA(varnt3);

		/* The following gives us the cDNA annotation */
		String canno = String.format("c.%ddel%s", posVariantInCDS, ref);
		/* Now create amino-acid annotation */
		if (wtaa.equals("*")) { /* mutation on stop codon */
			if (varaa.startsWith("*")) { /* #stop codon is still stop codon if ($varaa =~ m/\* /) */
				String nfsdel_ann = String.format("%s:exon%d:%s:p.X%dX", kgl.getName(), exonNumber, canno, aavarpos);
				Annotation ann = new Annotation(kgl, nfsdel_ann, VariantType.NON_FS_DELETION, posVariantInCDS);
				return ann;
			} else { /* stop codon is lost */
				String stoploss_ann = String.format("%s:exon%d:%s:p.X%d%s", kgl.getName(), exonNumber, canno, aavarpos,
						varaa);
				Annotation ann = new Annotation(kgl, stoploss_ann, VariantType.STOPLOSS, posVariantInCDS);
				return ann;
			}
		} else {
			if (varaa.contains("*")) { /* new stop codon created */
				String stopgain_ann = String.format("%s:exon%d:%s:p.%s%dX", kgl.getName(), exonNumber, canno, wtaa,
						aavarpos);
				Annotation ann = new Annotation(kgl, stopgain_ann, VariantType.STOPGAIN, posVariantInCDS);
				return ann;
			} else {
				/* A deletion affecting an amino-acid in the middle of the protein and leading to a frameshift */
				String fsdel_ann = String.format("%s:exon%d:%s:p.%s%dfs", kgl.getName(), exonNumber, canno, wtaa,
						aavarpos);
				Annotation ann = new Annotation(kgl, fsdel_ann, VariantType.FS_DELETION, posVariantInCDS);
				return ann;
			}
		}
	}

	/**
	 * Creates annotation for a deletion of more than one nucleotide. This is recognized by the fact that the ref
	 * sequence has a length greater than one and the variant sequence is "-".
	 * <P>
	 * Note that with the $firstcodondel option set to true, annovar reports deletions that affect the first amino acid
	 * as ABC:uc001ab:wholegene (FSDEL). We will not follow annovar here, but rather report such as deletion as with any
	 * other amino acid.
	 *
	 * @param tm
	 *            The known gene that corresponds to the deletion caused by the variant.
	 * @param frameShift
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param wtnt3_after
	 *            Nucleotide sequence of codon following that affected by variant
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refVarStart
	 *            start position of the variant in the mRNA of the transcript
	 * @param refVarEnd
	 *            end position of the variant in the mRNA of the transcript
	 * @param exonNumber
	 *            Number of the affected exon (one-based: TODO chekc this).
	 * @return {@link Annotation} object corresponding to deletion variant
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getMultinucleotideDeletionAnnotationLong(TranscriptModel tm, int frameShift, String wtnt3,
			String wtnt3_after, String ref, String var, int refVarStart, int refVarEnd, int exonNumber)
			throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */
		String cDNAAnno = null;
		String protAnno = null;
		String wtaa = translator.translateDNA(wtnt3);
		int refcdsstart = tm.getRefCDSStart();
		int cdslen = tm.getCDSLength();
		// Following correction on 30 Mar 2014.
		// int aavarpos = (int)Math.floor((refvarstart-kgl.getRefCDSStart())/3)+1;
		/*
		 * Note that posVariantInCDS is one-based. If pos%3==0, we are at the last base of a codon, and we can just
		 * divide by 3; otherwise, we need to take the floor, because we are at the first or second base of a codon.
		 */
		int posVariantInCDS = refVarStart - tm.getRefCDSStart() + 1; /* position of deletion within coding sequence */
		int aavarpos;
		if ((posVariantInCDS % 3) == 0)
			aavarpos = posVariantInCDS / 3;
		else
			aavarpos = (int) Math.floor(posVariantInCDS / 3) + 1; /* position of deletion in protein */
		// aavarpos = (int) Math.ceil(posVariantInCDS/3);
		int varPosEnd = -1; // the position of the last amino acid in the deletion
		// / int posVariantInCDS = refvarstart-kgl.getRefCDSStart(); - Why was there no "1" here?

		// System.out.println("refvarend = " + refvarend + ", cdslen="+cdslen + "refcdsstart=" + refcdsstart +
		// "( cdslen + refcdsstart )= " + (cdslen + refcdsstart));

		if (refVarStart <= refcdsstart) { /* first amino acid deleted */
			if (refVarEnd >= cdslen + refcdsstart) { // i.e., 3' portion of the gene is deleted
				varPosEnd = (int) Math.floor(cdslen / 3);
				cDNAAnno = String.format("c.%d_%ddel%s", refVarStart - refcdsstart, cdslen + refcdsstart - 1, ref);

			} else { /* deletion encompasses less than entire CDS */
				varPosEnd = (int) Math.floor((refVarEnd - refcdsstart) / 3) + 1;
				cDNAAnno = String.format("c.1_%ddel%s", refVarEnd - refVarStart + 1, ref);
			}
			protAnno = String
					.format("%s:exon%d:%s:p.%d_%ddel", tm.getName(), exonNumber, cDNAAnno, aavarpos, varPosEnd);
			Annotation ann = new Annotation(tm, protAnno, VariantType.FS_SUBSTITUTION, posVariantInCDS);
			return ann;
		} else if (refVarEnd >= cdslen + refcdsstart - 1) {
			// --------------------------------------------------------------
			// if we get here, then the 3' part of the gene is deleted
			// --------------------------------------------------------------
			varPosEnd = (int) Math.floor(cdslen / 3);
			// System.out.println("ref=" + ref + ", var=" +var);
			cDNAAnno = String.format("c.%d_%ddel%s", refVarStart - refcdsstart + 1, cdslen + refcdsstart - 1, ref);
			protAnno = String
					.format("%s:exon%d:%s:p.%d_%ddel", tm.getName(), exonNumber, cDNAAnno, aavarpos, varPosEnd);
			Annotation ann = new Annotation(tm, protAnno, VariantType.FS_SUBSTITUTION, posVariantInCDS);
			return ann;
		} else if ((refVarEnd - refVarStart + 1) % 3 == 0) {
			// --------------------------------------------------------------
			// non-frameshift deletion within the body of the mRNA
			// --------------------------------------------------------------
			varPosEnd = (int) Math.floor((refVarEnd - refcdsstart) / 3) + 1;
			posVariantInCDS = refVarStart - refcdsstart + 1; /* start pos of mutation */
			cDNAAnno = String.format("c.%d_%ddel%s", posVariantInCDS, refVarEnd - refcdsstart + 1, ref);
			if (aavarpos == varPosEnd) {
				if (frameShift == 0) {
					protAnno = String.format("%s:exon%d:%s:p.%s%ddel", tm.getName(), exonNumber, cDNAAnno, wtaa,
							aavarpos);
				} else {
					protAnno = String.format("%s:exon%d:%s:p.%ddel", tm.getName(), exonNumber, cDNAAnno, aavarpos);

				}
			} else { /* i.e., two or more amino acids are deleted */
				// int end_frame_s = (frame_s + var.length() - 1)%3;
				if (frameShift == 0) {
					String endcodon = tm.getCodonAt(refVarEnd - 2, frameShift); // ??????
					String endaa = translator.translateDNA(endcodon);
					// System.out.println("END codon=" + endcodon + " aa=" + endaa + " frame_s = " + frame_s);// +
					// " end_frame_s = " + end_frame_s);
					// System.out.println("refvarstart=" + refvarstart + ", refvarend = " + refvarend);
					protAnno = String.format("%s:exon%d:%s:p.%s%d_%s%ddel", tm.getName(), exonNumber, cDNAAnno, wtaa,
							aavarpos, endaa, varPosEnd);
				} else {
					String endcodon = tm.getCodonAt(refVarEnd + 1, frameShift); // ??????
					String endaa = translator.translateDNA(endcodon);
					String mutcodon = null;
					if (frameShift == 1) {
						mutcodon = String.format("%c%s", wtnt3.charAt(2), endcodon.substring(0, 2));
					} else {
						mutcodon = String.format("%s%c", wtnt3.substring(1, 3), endcodon.charAt(0));
					}
					String mutaa = translator.translateDNA(mutcodon);
					protAnno = String.format("%s:exon%d:%s:p.%s%d_%s%ddel%s", tm.getName(), exonNumber, cDNAAnno, wtaa,
							aavarpos, endaa, varPosEnd, mutaa);
				}
			}
			// System.out.println(panno);
			Annotation ann = new Annotation(tm, protAnno, VariantType.NON_FS_DELETION, posVariantInCDS);

			return ann;
		} else {
			// --------------------------------------------------------------
			// frameshift deletion within the body of the mRNA
			// --------------------------------------------------------------
			// varposend = (int)Math.floor(( refvarend- refcdsstart)/3) + 1;
			int posMutationInCDS = refVarStart - refcdsstart + 1; /* start pos of mutation with respect to CDS begin */
			cDNAAnno = String.format("c.%d_%ddel%s", posMutationInCDS, refVarEnd - refcdsstart + 1, ref);
			try {
				protAnno = shiftedFrameDeletionLong(tm, exonNumber, cDNAAnno, ref, posMutationInCDS, aavarpos,
						frameShift);
			} catch (AnnotationException e) {
				System.err.println("Exception while annotating frame-shift deletion: " + cDNAAnno);
				protAnno = cDNAAnno; /* just supply the cDNA annotation if there was an error. */
			}
			Annotation ann = new Annotation(tm, protAnno, VariantType.FS_DELETION, posMutationInCDS);
			return ann;
		}
	}

	/**
	 * Gets the correct annotation for a deletion that has led to a frameshift, such as p.(Gln40Profs*18), which results
	 * from a deletion of k nucleotides where k is not a multiple of 3.
	 */
	private static String shiftedFrameDeletionLong(TranscriptModel trmdl, int exonNumber, String cDNAanno, String ref,
			int posMutationInCDS, int aaVarStartPos, int frameShift) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */

		// Get the complete coding sequence.
		// Also include the 3UTR because some deletions extend the
		// mutant coding sequence beyond the stop codon.
		String orf = trmdl.getCodingSequencePlus3UTR();

		int start = posMutationInCDS - 1; // Convert 1-based to 0-based
		int endpos = start + ref.length(); // endpos is now 0-based and points to one after the deletion.

		String deletion = orf.substring(start, endpos);
		// Get the part of the codon that comes before the deletion
		String prefix = orf.substring(start - frameShift, start);
		// We do not know when the new sequence will differ from the wt sequence.
		// Try at least 10 amino acids.
		int restlen = (orf.length() - endpos) > 30 ? 30 : orf.length() - endpos;
		String rest = orf.substring(endpos, endpos + restlen);
		String wt = prefix + deletion + rest;
		String mut = prefix + rest;
		String wtaa = translator.translateDNA(wt);
		String mutaa = translator.translateDNA(mut);

		/*
		 * System.out.println("start=" + (start+1) + ", end="+(endpos+1)); System.out.println("deletion ="+ deletion);
		 * System.out.println("rest = "+ rest + ", restlen="+restlen); System.out.println("prefix ="+ prefix);
		 * System.out.println("wt:" + wtaa); System.out.println("mt:" + mutaa); trmdl.debugPrintCDS();
		 */
		int aapos = aaVarStartPos;
		int endk = mutaa.length();

		// We have to check if the deletion affects the stop-codon additionally we have to check if the STop codon
		// is coplettly deletet, or if there pop-up a new STOP codon, or if there is not enpough sequence info to
		// predict this and then just say, there is a new STOP at the deletion STart (e.g. if the 3'UTR is missing and
		// only one or two bp are left)

		String annot = null;
		if ((wtaa.contains("*") && !mutaa.contains("*"))) {
			int k;
			for (k = 0; k < endk; ++k) {
				if (wtaa.charAt(k) == mutaa.charAt(k)) {
					aapos++;

				}
			}
			int aaend_frame = (posMutationInCDS + ref.length()) % 3;
			int aaendPos = aaend_frame == 0 ? (posMutationInCDS + ref.length()) / 3
					: (posMutationInCDS + ref.length()) / 3 + 1;
			annot = String.format("%s:exon%d:%s:p.%c%d_*%ddel", trmdl.getName(), exonNumber, cDNAanno, wtaa.charAt(k),
					aapos, aaendPos);
			return annot;// e.g. p.(Gln40Profs*18)
		}

		for (int k = 0; k < endk; ++k) {
			if (wtaa.charAt(k) != mutaa.charAt(k)) {
				annot = String.format("%s:exon%d:%s:p.%c%d%cfs", trmdl.getName(), exonNumber, cDNAanno, wtaa.charAt(k),
						aapos, mutaa.charAt(k));
				return annot;// e.g. p.(Gln40Profs*18)
			} else {
				aapos++;
			}
		}
		// if we get here, all amino acids were the same.
		// probably some weird nomenclature.
		// panno = String.format("%s:exon%d:%s:p.%d_%ddel",kgl.getName(),
		// exonNumber,canno,aavarpos,varposend);
		annot = String.format("%s:exon%d:%s:p.%c%ddelins%c", trmdl.getName(), exonNumber, cDNAanno, wtaa.charAt(0),
				aaVarStartPos, mutaa.charAt(0));
		return annot;
	}

}