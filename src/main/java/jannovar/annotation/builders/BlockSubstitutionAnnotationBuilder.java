package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.Chromosome;
import jannovar.reference.GenomeChange;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptModel;
import jannovar.util.Translator;

/**
 * This class is intended to provide a static method to generate annotations for block substitution mutations. This
 * method is put in its own class only for convenience and to at least have a name that is easy to find.
 *
 * Block substitutions are recognized in the calling class {@link Chromosome} by the fact that the length of the variant
 * sequence is greater than 1.
 *
 * @version 0.08 (22 April, 2014)
 * @author Peter N Robinson
 * @author Marten Jäger
 */
public class BlockSubstitutionAnnotationBuilder {

	/**
	 * Returns a {@link Annotation} for the block substitution {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for, must describe a block substitution in
	 *            <code>transcript</code>
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe a block substitution
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// Guard against invalid genome change.
		if (change.getRef().length() == 0 || change.getAlt().length() == 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a block substitution.");

		// Project the change to the same strand as transcript, reverse-complementing the REF/ALT strings.
		change = change.withStrand(transcript.getStrand());

		// Forward everything to the helper.
		return new BlockSubstitutionAnnotationBuilderHelper(transcript, change).build();
	}

	/**
	 * Creates annotation for a block substitution on the plus strand.
	 *
	 * @param tm
	 *            The transcript model that corresponds to the deletion caused by the variant.
	 * @param frame_s
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refVarStart
	 *            Position of the variant in the CDS of the known gene
	 * @param refVarEnd
	 *            Position of the end of the variant in the CDS of the known gene
	 * @param exonNumber
	 *            Number of the affected exon (one-based: TODO chekc this).
	 * @return An annotation corresponding to the deletion.
	 */
	public static Annotation getAnnotationPlusStrand(TranscriptModel tm, int frame_s, String wtnt3, String ref, String var, int refVarStart, int refVarEnd, int exonNumber) throws AnnotationException {
		String cDNAAnno = null; // cDNA annotation.
		String protAnno = null; // protein annotation
		int refCDSStart = tm.getRefCDSStart(); // position of start codon in transcript.
		int startPosMutationInCDS = refVarStart - refCDSStart + 1;

		cDNAAnno = String.format("c.%d_%ddelins%s", refVarStart - refCDSStart + 1, refVarEnd - refCDSStart + 1, var);
		if ((refVarEnd - refVarStart + 1 - var.length()) % 3 == 0) {
			protAnno = String.format("%s:%s:exon:%d:%s", tm.getGeneSymbol(), tm.getName(), exonNumber, cDNAAnno);
			return new Annotation(tm, protAnno, VariantType.NON_FS_SUBSTITUTION, refVarStart);
		} else { // i.e., frameshift
			protAnno = String.format("%s:exon:%d:%s", tm.getName(), exonNumber, cDNAAnno);
			return new Annotation(tm, protAnno, VariantType.FS_SUBSTITUTION, startPosMutationInCDS);
		}
	}

	/**
	 * Creates annotation for a block substitution on the plus strand. Both ref and var are blocks.
	 *
	 * @param tm
	 *            The TranscriptModel that corresponds to the deletion caused by the variant.
	 * @param frameShift
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refVarStart
	 *            Position of the variant in the CDS of the known gene
	 * @param refVarEnd
	 *            Position of the end of the variant in the CDS of the known gene
	 * @param exonNumber
	 *            Number of the affected exon (one-based: TODO chekc this).
	 * @return An annotation corresponding to the deletion.
	 */
	public static Annotation getAnnotationBlockPlusStrand(TranscriptModel tm, int frameShift, String wtnt3, String ref, String var, int refVarStart, int refVarEnd, int exonNumber) throws AnnotationException {
		// shift / remove Overlapp ref/var
		int i = 0;
		while (ref.charAt(i) == var.charAt(i)) {
			i++;
			refVarStart++;
			frameShift++;
		}
		frameShift = frameShift % 3;
		int iend = ref.length();
		int diffnt = ref.length() - var.length();
		while (iend > i && ref.charAt(iend - 1) == var.charAt(iend - 1 - diffnt)) {
			iend--;
			refVarEnd--;
		}
		ref = ref.substring(i, iend);
		if (i == iend)
			var = "-";
		else
			var = var.substring(i, iend - diffnt);

		Translator translator = Translator.getTranslator(); // Singleton
		String cDNAAnno = null; // cDNA annotation.
		String protAnno = null; // protein annotation
		int refCDSStart = tm.getRefCDSStart(); // position of start codon in transcript.
		int startPosMutationInCDS = refVarStart - refCDSStart + 1;
		int posVariantInCDS = refVarStart - tm.getRefCDSStart() + 1; // position of deletion within coding sequence
		int varPosEnd = (int) Math.floor((refVarEnd - refCDSStart) / 3) + 1;
		String wtaa = translator.translateDNA(wtnt3);
		int aavarpos = ((posVariantInCDS % 3) == 0) ? posVariantInCDS / 3 : (int) Math.floor(posVariantInCDS / 3) + 1;

		if (ref.length() == 1 && var.length() == 1)
			cDNAAnno = String.format("%s:exon%d:c.%d%s>%s", tm.getName(), exonNumber, refVarStart - refCDSStart + 1, ref, var);
		else
			cDNAAnno = String.format("%s:exon%d:c.%d_%ddelins%s", tm.getName(), exonNumber, refVarStart - refCDSStart + 1, refVarEnd - refCDSStart + 1, var);

		if ((refVarEnd - refVarStart + 1 - var.length()) % 3 == 0) {
			int endframe_s = (frameShift + (ref.length() % 3)) % 3;

			String wtntend = tm.getCodonAt(refVarEnd + 1, endframe_s);
			String wtntSeq = tm.getCDNASequence().substring(refVarStart - frameShift - 1, refVarEnd + (3 - endframe_s));
			String mutntSeq = String.format("%s%s%s", wtnt3.substring(0, frameShift), var, wtntend.substring(endframe_s));

			String wtAAseq = translator.translateDNA(wtntSeq);
			String mutAAseq = translator.translateDNA(mutntSeq);
			if (wtAAseq.equals(mutAAseq)) {
				protAnno = String.format("%s:p.(=)", cDNAAnno);
			} else {
				int idx = 0;
				while (idx < wtAAseq.length() && wtAAseq.charAt(idx) == mutAAseq.charAt(idx)) {
					idx++;
				}

				int xdi = wtAAseq.length();
				int diff = wtAAseq.length() - mutAAseq.length();
				while (xdi > idx && wtAAseq.charAt(xdi - 1) == mutAAseq.charAt(xdi - 1 - diff)) {
					xdi--;
				}
				int stopIdx = mutAAseq.indexOf("*");
				if (stopIdx >= 0)
					protAnno = String.format("%s:p.%s%d_%s%ddelins%s", cDNAAnno, wtAAseq.charAt(idx), aavarpos + idx,
							wtAAseq.charAt(xdi - 1), varPosEnd, mutAAseq.substring(idx, stopIdx + 1));
				else if (idx < wtAAseq.length())
					protAnno = String.format("%s:p.%s%d_%s%ddelins%s", cDNAAnno, wtAAseq.charAt(idx), aavarpos + idx,
							wtAAseq.charAt(xdi - 1), varPosEnd, mutAAseq.substring(idx, xdi - diff));
				else {
					String wtntSeqAfter = tm.getCDNASequence().substring(refVarEnd + (3 - endframe_s), refVarEnd + (3 - endframe_s) + 3);
					String wtAAseqAfter = translator.translateDNA(wtntSeqAfter);
					protAnno = String.format("%s:p.%s%d_%s%dins%s", cDNAAnno, wtAAseq.charAt(idx - 1), aavarpos + idx
							- 1, wtAAseqAfter, varPosEnd + 1, mutAAseq.substring(idx, xdi - diff));
				}
			}
			return new Annotation(tm, protAnno, VariantType.NON_FS_SUBSTITUTION, startPosMutationInCDS);
		} else {
			// aaVarStartPos is now the FIRST position (one-based) of the amino-acid sequence that was duplicated.
			int aaVarStartPos = startPosMutationInCDS % 3 == 0 ? (int) Math.floor(startPosMutationInCDS / 3) : (int) Math.floor(startPosMutationInCDS / 3) + 1;
			protAnno = String.format("%s:p.%s%dfs", cDNAAnno, wtaa, aaVarStartPos);
			return new Annotation(tm, protAnno, VariantType.FS_SUBSTITUTION, startPosMutationInCDS);
		}
	}
}
