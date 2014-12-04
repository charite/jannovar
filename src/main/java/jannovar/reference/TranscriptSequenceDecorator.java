package jannovar.reference;

import jannovar.common.Immutable;


// TODO(holtgrem): Test this class!

/**
 * Decorator for {@link TranscriptInfo} that helps with operations on its sequence.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class TranscriptSequenceDecorator {

	/** The wrapped {@link TranscriptInfo}. */
	public final TranscriptInfo transcript;

	public TranscriptSequenceDecorator(TranscriptInfo transcript) {
		this.transcript = transcript;
	}

	/**
	 * Update the base given by <code>frameShift</code> in the given codon string <code>transcriptCodon</code> to
	 * <code>targetNC</code> and return updated codon.
	 *
	 * @param transcriptCodon
	 *            the wild type codon nucleotide string from the codon
	 * @param frameShift
	 *            the frame within the codon
	 * @param targetNC
	 *            the target nucleotide
	 * @return variant codon string
	 */
	public static String codonWithUpdatedBase(String transcriptCodon, int frameShift, char targetNC) {
		assert (0 <= frameShift && frameShift <= 2);
		if (frameShift == 0)
			return String.format("%c%c%c", targetNC, transcriptCodon.charAt(1), transcriptCodon.charAt(2));
		else if (frameShift == 1)
			return String.format("%c%c%c", transcriptCodon.charAt(0), targetNC, transcriptCodon.charAt(2));
		else
			return String.format("%c%c%c", transcriptCodon.charAt(0), transcriptCodon.charAt(1), targetNC);
	}

	/**
	 * Insert the string in <code>insertion</code> at the position given by <code>frameShift</code> in the given codon
	 * string <code>transcriptCodon</code> to <code>targetNC</code> and return the updated nucleic string.
	 *
	 * @param transcriptNTs
	 *            the wild type nucleotide string from the codon
	 * @param frameShift
	 *            the frame within the codon
	 * @param insertion
	 *            the String with the nucleotides
	 * @return variant codon string
	 */
	public static String nucleotidesWithInsertion(String transcriptNTs, int frameShift, String insertion) {
		assert (0 <= frameShift && frameShift <= 2);
		if (frameShift == 0)
			return String.format("%s%s", insertion, transcriptNTs);
		else if (frameShift == 1)
			return String.format("%c%s%s", transcriptNTs.charAt(0), insertion, transcriptNTs.substring(1));
		else
			return String.format("%c%c%s%s", transcriptNTs.charAt(0), transcriptNTs.charAt(1), insertion,
					transcriptNTs.substring(2));
	}

	/**
	 * Returns the codon (String of length 3) for a change at a given position
	 *
	 * @param txPos
	 *            transcript position of the change
	 * @param cdsPosition
	 *            CDS position of the change
	 * @return the codon affected by a change at the given position
	 */
	public String getCodonAt(TranscriptPosition txPos, CDSPosition cdsPos) {
		int frameShift = cdsPos.pos % 3;
		int codonStart = txPos.pos - frameShift; // codon start in transcript string
		return transcript.sequence.substring(codonStart, codonStart + 3);
	}

	/**
	 * Returns a number of codons (String of length 3 * <code>len</code>) starting from the affected one by the change
	 * at <code>txPos</code>/<code>cdsPos</code>.
	 *
	 * If there is not a sufficient number of nucleotides in the transcript string then return fewer, only up to the
	 * end.
	 *
	 * @param txPos
	 *            transcript position of the change
	 * @param cdsPosition
	 *            CDS position of the change
	 * @param count
	 *            number of codons to return
	 * @return the codon affected by a change at the given position
	 */
	public String getCodonsStartingFrom(TranscriptPosition txPos, CDSPosition cdsPos, int count) {
		int frameShift = cdsPos.pos % 3;
		int codonStart = txPos.pos - frameShift; // codon start in transcript string
		int endPos = codonStart + 3 * count;
		if (endPos > transcript.sequence.length())
			endPos = transcript.sequence.length();
		return transcript.sequence.substring(codonStart, endPos);
	}

	/**
	 * Returns all codons (String of length 3 * <code>len</code>) starting from the affected one by the change at
	 * <code>txPos</code>/<code>cdsPos</code>.
	 *
	 * @param txPos
	 *            transcript position of the change
	 * @param cdsPosition
	 *            CDS position of the change
	 * @return the codon affected by a change at the given position
	 */
	public String getCodonsStartingFrom(TranscriptPosition txPos, CDSPosition cdsPos) {
		return getCodonsStartingFrom(txPos, cdsPos, transcript.sequence.length());
	}
}
