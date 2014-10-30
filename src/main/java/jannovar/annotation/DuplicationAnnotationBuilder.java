package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.reference.TranscriptModel;
import jannovar.reference.Translator;

// TODO: currently, the handling of tandem repeats is not as recommended
/**
 * This class is intended to provide a static method to generate annotations for duplication mutations. e.g. these
 * cases:
 *
 * g.7_8dup (or g.7_8dupTG, not g.5_6dup, not g.8_9insTG) denotes a TG duplication in the TG-tandem repeat sequence
 * changing ACTTTGTGCC to ACTTTGTGTGCC
 *
 * g.7_8[4] (or g.5_6[4], or g.5TG[4], not g.7_10dup) is the preferred description of the addition of two extra TG's to
 * the variable TG repeated sequence changing ACTTTGTGCC to ACTTTGTGTGTGCC
 *
 * @version 0.04 (22 April, 2014)
 * @author Peter N Robinson, Marten Jäger
 */
public class DuplicationAnnotationBuilder {
	/**
	 * Annotates an insertion variant that is an duplication. The methods of this class are called from
	 * {@link jannovar.annotation.InsertionAnnotationBuilder InsertionAnnotation} if that class determines that the insertion
	 * is equal to the preceding nucleotides in the reference sequence. That is, in addition to the conditions for a
	 * insertion variant, the duplication variant requires a similar sequence to the insertion (before or) after the
	 * insertion.
	 *
	 * There are two possible duplication insertions with or without frameshift causation. e.g. inserting an additional
	 * 'C' in the sequence 'ACC,GAG' at position 2 would cause a frameshift, whereas insertion of 'CCG' at position 2
	 * just inserts an additional triple 'ACC GCC GAG'.
	 *
	 * if (var.length() % 3 == 0) { /* ORF CONSERVING if(startPosMutationInCDS.length() % 3 == 0){ /* SIMPLE DUPLICATION
	 * OF CODONS } else { /* substitution from original AA to AAs if(wtaa.equals("*")) { /* Mutation affects the
	 * wildtype stop codon int idx = varaa.indexOf("*"); if (idx < 0) { } /* Substitution } }else { /* FRAMESHIFT *
	 * short p.(Arg97fs)) denotes a frame shifting change with Arginine-97 as the first affected amino acid }
	 *
	 *
	 * @param tm
	 *            The transcriptmodel / gene in which the current mutation is contained
	 * @param frameShift
	 *            the location within the frame (0,1,2) in which mutation occurs
	 * @param wtnt3
	 *            The three nucleotides of codon affected by start of mutation
	 * @param var
	 *            alternate nucleotide sequence (the duplication)
	 * @param startPos
	 *            The startposition of the duplication (zero based)
	 * @param endPos
	 *            The endposition of the duplication (zero based)
	 * @param exonNumber
	 *            Number (one-based) of affected exon.
	 * @return an {@link jannovar.annotation.Annotation Annotation} object representing the current variant
	 * @throws AnnotationException
	 */
	public static Annotation getAnnotation(TranscriptModel tm, int frameShift, String wtnt3, String var, int startPos,
			int endPos, int exonNumber) throws AnnotationException {
		String annot;
		Annotation ann;
		if (startPos > tm.getRefCDSEnd()) {
			ann = Annotation.createEmptyAnnotation();
			ann.setVarType(VariantType.UTR3);
			ann.setGeneSymbol(tm.getGeneSymbol());
			int distance = startPos - tm.getRefCDSEnd();
			if (var.length() == 1)
				annot = String.format("%s:exon%d:c.*%ddup", tm.getName(), exonNumber, distance);
			else
				annot = String.format("%s:exon%d:c.*%d_*%ddup", tm.getName(), exonNumber, distance,
						distance + var.length() - 1);
			ann.setVariantAnnotation(annot);
			ann.setGeneID(tm.getGeneID());
			return ann;
		}
		Translator translator = Translator.getTranslator(); /* Singleton */
		int refcdsstart = tm.getRefCDSStart();

		/**/
		int newpos = shiftToThreePrime(tm, var, startPos, endPos);
		if (newpos != startPos) {
			startPos = newpos;
			endPos = startPos + var.length() - 1;
		}

		int cdsEndPos = endPos - refcdsstart + 1;
		int cdsStartPos = cdsEndPos - var.length() + 1;

		/**
		 * aavarpos is now the FIRST position (one-based) of the amino-acid sequence that was duplicated.
		 */
		int aaVarStartPos = cdsStartPos % 3 == 0 ? (int) Math.floor(cdsStartPos / 3) : (int) Math
				.floor(cdsStartPos / 3) + 1;

		// debugDuplication(trmdl,frame_s, wtnt3, var, startpos, exonNumber,aaVarStartPos);
		/* get coding DNA HGVS string */
		String canno;
		// if(var.length() == 1) // long version
		// canno = String.format("c.%ddup%s", cdsStartPos, var);
		// else
		// canno = String.format("c.%d_%ddup%s", cdsStartPos,cdsEndPos, var);
		if (var.length() == 1)
			canno = String.format("c.%ddup", cdsStartPos);
		else
			canno = String.format("c.%d_%ddup", cdsStartPos, cdsEndPos);

		// System.out.println("canno: " + canno);
		// System.out.println("var.length mod 3:" + var.length() % 3);
		// System.out.println((3 - (var.length() % 3)));
		// System.out.println(((3 - (var.length() % 3)) % 3));
		// System.out.println("startpos: " + startpos);
		// System.out.println("pos: " + (startpos - 1 + ((3 - (var.length() % 3)) % 3)));
		// System.out.println("length: " + trmdl.getCdnaSequence().length());
		// System.out.println(trmdl.getAccessionNumber());
		// System.out.println("frame_s: " + frame_s);
		// System.out.println(trmdl.getCdnaSequence().length() - 3);
		// System.out.println(trmdl.getCdnaSequence().substring(trmdl.getCdnaSequence().length() - 3));
		// trmdl.setSequence(trmdl.getCdnaSequence() + "AAAAAAAA");
		/* now create the protein HGVS string */

		/* generate in-frame snippet for translation and correct for '-'-strand */
		if (tm.isMinusStrand()) {
			/* Re-adjust the wildtype nucleotides for minus strand */
			// wtnt3 = trmdl.getWTCodonNucleotides(startpos - 1 + ((3 - (var.length() % 3)) % 3), frame_s);
			// System.out.println("startpos2: " + startpos);
			wtnt3 = tm.getWTCodonNucleotides(startPos, frameShift);
		}
		String varnt3 = getVarNT3(tm, wtnt3, var, frameShift);

		String wtaa = translator.translateDNA(wtnt3);
		String varaa = translator.translateDNA(varnt3);

		// System.out.println("wt na: " + wtnt3 + "\t" + wtaa);
		// System.out.println("var na: " + varnt3 + "\t" + varaa);

		if (var.length() % 3 == 0) { /* ORF CONSERVING */
			if ((cdsStartPos - 1) % 3 == 0) { /*
											 * SIMPLE DUPLICATION OF CODONS, e.g., nucleotide position 4 starts a codon,
											 * and (4-1)%3==0.
											 */
				String wtaaDupStart = translator.translateDNA(var.substring(0, 3));
				String wtaaDupEnd = translator.translateDNA(var.substring(var.length() - 3));
				if (var.length() == 3) {
					// Three nucleotides affected, inframe, single aminoacid duplication.
					annot = singleAminoAcidInframeDuplication(tm.getName(), exonNumber, canno, wtaaDupStart,
							aaVarStartPos);
				} else {
					int aaEndPos = aaVarStartPos + (var.length() / 3) - 1; /* last amino acid of duplicated WT seq. */

					String wtAA_after = translator.translateDNA(tm.getCDNASequence().substring(
							startPos + var.length() - 1, startPos + var.length() + 2));
					if (wtAA_after.equals("*"))
						annot = String.format("%s:exon%d:%s:p.*%dext*%d", tm.getName(), exonNumber, canno,
								aaEndPos + 1, var.length() / 3);
					else
						annot = multipleAminoAcidInframeDuplication(tm.getName(), exonNumber, canno, wtaaDupStart,
								aaVarStartPos, wtaaDupEnd, aaEndPos);

				}
				ann = new Annotation(tm, annot, VariantType.NON_FS_DUPLICATION, cdsStartPos);
			} else { /* substitution from original AA to AAs */
				if (wtaa.equals("*")) { /* Mutation affects the wildtype stop codon */
					int idx = varaa.indexOf("*");
					if (idx < 0) {
						annot = String.format("%s:exon%d:%s:p.*%d%sext*?", tm.getName(), exonNumber, canno,
								aaVarStartPos, varaa);
					} else {
						annot = String.format("%s:exon%d:%s:p.*%ddelins%s", tm.getName(), exonNumber, canno,
								aaVarStartPos, varaa.substring(0, idx + 1));
					}
				} else {
					/* substitution starts not on frame */
					annot = shiftedInFrameDuplication(tm, exonNumber, canno, var, endPos, aaVarStartPos, frameShift);
				}
				/* Substitution */
				ann = new Annotation(tm, annot, VariantType.NON_FS_DUPLICATION, cdsStartPos);
			}
		} else { /*
				 * FRAMESHIFT short p.(Arg97fs)) denotes a frame shifting change with Arginine-97 as the first affected
				 * amino acid
				 */
			if (wtaa.charAt(0) == '*' && varaa.charAt(0) == '*')
				annot = String.format("%s:exon%d:%s:p.(=)", tm.getName(), exonNumber, canno, wtaa, aaVarStartPos);
			else
				annot = String.format("%s:exon%d:%s:p.%s%dfs", tm.getName(), exonNumber, canno, wtaa, aaVarStartPos);
			// System.out.println("FS wtaa="+wtaa);
			// System.out.println(annot);
			ann = new Annotation(tm, annot, VariantType.FS_DUPLICATION, cdsStartPos);
		}
		return ann;
	}

	/**
	 * Note that according to the HGVS, for all descriptions the most 3' position possible is arbitrarily assigned to
	 * have been changed. This means that for duplications on the minus strand of a gene, we may need to shift the
	 * position of the variant to 3'. This method does so.
	 *
	 * @param tm
	 *            The affected transcript
	 * @param var
	 *            The variant (the duplicated sequence)
	 * @param startPos
	 *            zero-based start position of duplication
	 * @param endPos
	 *            zero-based end position of duplication
	 * @return potentially shifted startpos (zerobased) of variant.
	 */
	private static int shiftToThreePrime(TranscriptModel tm, String var, int startPos, int endPos)
			throws AnnotationException {
		String cCNA = tm.getCDNASequence();
		int len = var.length();
		if ((endPos - startPos) != len) {
			String s = String.format("[DuplicationAnnotation.java] shiftToThreePrime: "
					+ "Error in sequence length; gene=%s, var=%s, len=%d, end=%d,start=%d", tm.getGeneSymbol(), var,
					len, endPos, startPos);
			throw new AnnotationException(s);
		}
		String dup = cCNA.substring(startPos, endPos);
		// System.out.println(trmdl.getGeneSymbol() + "[startpos="+startpos+"]: " + dup);
		while ((endPos + len) < cCNA.length() && dup.equals(cCNA.substring(startPos + len, endPos + len))) {
			startPos += len;
			endPos += len;
			// System.out.println(trmdl.getGeneSymbol() + "[startpos="+(startpos)+"; endpos="+(endpos)+"]: "
			// + cdna.substring(startpos,endpos));
		}
		startPos++; // revert to one-based numbers
		return startPos;
	}

	/**
	 * This function retrieves the codon that contains the start of the duplication in the variant sequence. If L is the
	 * length of the duplication, this method will return a string of length L+3 with the three nucleotides surrounding
	 * the duplication.
	 */
	private static String getVarNT3(TranscriptModel tm, String wtnt3, String var, int frameShift) {
		String varnt3;
		if (tm.isPlusStrand()) {
			frameShift = 2 - frameShift;
			if (frameShift == 0) { /* duplication located at 0-1-INS-2 part of codon */
				varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
			} else if (frameShift == 2) {
				varnt3 = String.format("%s%s", var, wtnt3);
			} else { /* i.e., frame_s == 0, duplication located at 0-INS-1-2 part of codon */
				varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
			}
		} else {
			frameShift = 2 - frameShift;
			if (frameShift == 2) {
				varnt3 = String.format("%c%s%c%c", wtnt3.charAt(0), var, wtnt3.charAt(1), wtnt3.charAt(2));
			} else if (frameShift == 1) {
				varnt3 = String.format("%c%c%s%c", wtnt3.charAt(0), wtnt3.charAt(1), var, wtnt3.charAt(2));
			} else { /* i.e., frame_s == 0 */
				varnt3 = String.format("%s%s", wtnt3, var);
			}
			// frame_s = 2 - frame_s;
		}
		return varnt3;
	}

	/**
	 * This function is for a duplication that affects one triplet of nucleotides and thus changes one aminoacid. for
	 * example OR2T3(uc001iel.1:exon1:c.769_771dupTTC:p.F257dup)
	 *
	 * @param transcriptID
	 *            The accession number of the affected transcript, e.g., uc001iel.1
	 * @param exonNumber
	 *            One-based number of the exon
	 * @param cDNAanno
	 *            Annotation of the cDNA, e.g., c.769_771dupTTC
	 * @param wtAA
	 *            Wildtype amino acid that has been duplicated, e.g., F
	 * @param aaVarPos
	 *            The position of the wildtype amino acid that has been duplicated.
	 */
	private static String singleAminoAcidInframeDuplication(String transcriptID, int exonNumber, String cDNAanno,
			String wtAA, int aaVarPos) {
		String a = String.format("%s:exon%d:%s:p.%s%ddup", transcriptID, exonNumber, cDNAanno, wtAA, aaVarPos);
		return a;
	}

	/**
	 * This function figures out the consequence of a DNA duplication that does not begin at position zero of a codon,
	 * i.e., that is not right in the frame. This can result in a delins or in a simple dup depending on the surrounding
	 * sequence. For example, imagine we have the following duplication. ggaggaggaggaggaggagga (add another gga). but
	 * the frame is gag-gag-gag-...with GAG=Glu/E. Then the effect of the duplication is to add another E to the
	 * aminoacid sequence. The variable frame_s is 2 in this case, because gga starts at nucleotide 2 (zero-based) of
	 * the GAG codon.
	 *
	 * @param tm
	 *            The affected transcript
	 * @param exonNumber
	 *            One-based number of the exon
	 * @param cDNAannot
	 *            Annotation of the cDNA, e.g., c.769_771dupTTC
	 * @param var
	 *            the duplicated sequence
	 * @param endPos
	 *            the end position of the duplication (zero-based)
	 * @param frameShift
	 *            the location within the frame (0,1,2) in which mutation occurs
	 */
	private static String shiftedInFrameDuplication(TranscriptModel tm, int exonNumber, String cDNAanno, String var,
			int endPos, int aaVarStartPos, int frameShift) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */
		int len = var.length();
		if ((len % 3) != 0) {
			String s = String.format("[ERROR] DuplicationAnnotation:shiftedInFrameDuplication - "
					+ "variant length not a multiple of 3: %s (len=%d)", var, len);
			throw new AnnotationException(s);
		}
		int aalen = len / 3;

		String dna = tm.getCDNASequence();
		int start = endPos - var.length();
		String prefix = dna.substring(start - frameShift, start);
		// System.out.println("prefix = " + prefix + ", frame_s="+frame_s + ", var="+var + " endpos=" + endpos);
		// System.out.println(cDNAanno);
		String rest = dna.substring(start, start + len);
		String wt = prefix + rest;
		String mut = prefix + var + rest;
		String wtaa = translator.translateDNA(wt);
		String mutaa = translator.translateDNA(mut);
		if (mutaa.startsWith(wtaa) && (mutaa.indexOf(wtaa, aalen)) > 0)
			return String.format("%s:exon%d:%s:p.%s%ddup", tm.getName(), exonNumber, cDNAanno, wtaa, aaVarStartPos);
		else
			return String.format("%s:exon%d:%s:p.%s%ddelins%s", tm.getName(), exonNumber, cDNAanno, wtaa,
					aaVarStartPos, mutaa);
	}

	/**
	 * HGVS: p.Gly4_Gln6dup in the sequence MKMGHQQQCC denotes a duplication of amino acids Glycine-4 (Gly, G) to
	 * Glutamine-6 (Gln, Q) (i.e. MKMGHQGHQQQCC)
	 *
	 * @param transcriptID
	 *            The accession number of the affected transcript, e.g., uc001iel.1
	 * @param exonNumber
	 *            One-based number of the exon
	 * @param cDNAannot
	 *            Annotation of the cDNA, e.g., c.769_771dupTTC
	 * @param firstWtAA
	 *            First Wildtype amino acid that has been duplicated, e.g., F
	 * @param firstAAVarPos
	 *            The position of the first wildtype amino acid that has been duplicated.
	 * @param lastWtAA
	 *            Last Wildtype amino acid that has been duplicated, e.g., F
	 * @param lastAAVarPos
	 *            The position of the last wildtype amino acid that has been duplicated.
	 */
	private static String multipleAminoAcidInframeDuplication(String transcriptID, int exonNumber, String cDNAannot,
			String firstWtAA, int firstAAVarPos, String lastWtAA, int lastAAVarPos) {
		String a = String.format("%s:exon%d:%s:p.%s%d_%s%ddup", transcriptID, exonNumber, cDNAannot, firstWtAA,
				firstAAVarPos, lastWtAA, lastAAVarPos);
		return a;
	}

	/**
	 * A convenience method for printing out information about duplication annotations. Hopefully useful for
	 * checking/debugging.
	 */
	private static void debugDuplication(TranscriptModel tm, int frameShift, String wtnt3, String var, int refVarStart,
			int exonNumber, int aaVarPos) {
		System.err.println("#--------------- DuplicationAnnotation.java: DEBUG --------------------#");
		int cdsEndPos = refVarStart - tm.getRefCDSStart() + 1;
		int cdsStartPos = cdsEndPos - var.length() + 1;
		System.out.println("cdsStartPos=" + cdsStartPos);
		System.out.println("cdsEndPos=" + cdsEndPos);
		System.out.println("refvarstart=" + refVarStart);
		System.out.println("var.length()=" + var.length());
		System.out.println("wtnt3=" + wtnt3);
		System.out.println("frame_s=" + frameShift);
		System.out.println("exonNumber=" + exonNumber);
		System.out.println("aavarpos=" + aaVarPos);
		tm.debugPrint();
		tm.debugPrintCDS();
	}

}