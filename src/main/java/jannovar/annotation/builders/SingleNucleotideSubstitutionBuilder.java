package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.reference.TranscriptModel;
import jannovar.util.Translator;

/**
 * This class provides static methods to generate annotations for SNVs in exons.
 *
 * Note that there are numerous variants in positions that show a discrepancy between the genomic sequence of hg19 and
 * the UCSC knownGene mRNA sequences. This is a difficult issue because it is uncertain which sequence is correct.
 * Annovar uses its own version of the knownGeneMrna.txt file that is made to conform exactly with the genome sequence.
 *
 * However, by inspection, the mRNA sequence actually appears to be the correct one. Therefore, our strategy is to base
 * annotations upon the mRNA sequence of the original USCS knownGeneMrna.txt file but additionally to report that there
 * is a discrepancy between the genomic sequence (which is the sequence that is typically used for variant calling and
 * thus informs the variant calls in the VCF file) and the UCSC mRNA sequence. We no longer throw an Exception in this
 * case, as in versions of this class up to the 15th of December, 2012 (v. 0.04).
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jäger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class SingleNucleotideSubstitutionBuilder {

	/**
	 * Creates annotation for a single-nucleotide substitution.
	 *
	 * This function decides what strand the affected {@link TranscriptModel} is located on and calls either
	 * {@link #getAnnotationPlusStrand} or {@link #getAnnotationMinusStrand} to do the calculations.
	 *
	 * @param tm
	 *            The {@link TranscriptModel} that is affected by the SNV.
	 * @param frameShift
	 *            0 if variant begins at first base of codon, 1 if it begins at second base, 2 if at third base (same
	 *            for SNV)
	 * @param frameEndShift
	 *            0 Frame at end of variant
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 *
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refVarStart
	 *            Position of the variant in the CDS of the known gene
	 * @param exonNumber
	 *            Number of the affected exon (zero-based).
	 * @return An annotation corresponding to the SNV.
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getAnnotation(TranscriptModel tm, int frameShift, int frameEndShift, String wtnt3, String ref, String var, int refVarStart, int exonNumber) throws AnnotationException {
		if (tm.isPlusStrand())
			return getAnnotationPlusStrand(tm, frameShift, wtnt3, ref, var, refVarStart, exonNumber);
		else
			return getAnnotationPlusStrand(tm, frameShift, wtnt3, ref, var, refVarStart, exonNumber);

	}

	/**
	 * Creates annotation for a single-nucleotide substitution on the plus strand.
	 *
	 * @param tm
	 *            The known gene that corresponds to the deletion caused by the variant.
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
	 * @param exonNumber
	 *            Number of the affected exon (zero-based).
	 * @return An annotation corresponding to the SNV.
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getAnnotationPlusStrand(TranscriptModel tm, int frameShift, String wtnt3, String ref,
			String var, int refVarStart, int exonNumber) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */
		String cDNAAnno = null; // cDNA annotation.
		String protAnno = null;
		String varNT3 = null;
		int refCDSStart = tm.getRefCDSStart(); /* position of start codon in transcript. */
		int cdsPos = refVarStart - refCDSStart + 1; /* position of mutation in CDS, numbered from start codon */

		VariantType varType = null;
		String wrng = null;

		if (ref.length() != 1) {
			throw new AnnotationException(String.format(
					"Error: Malformed reference sequence (%s) for SNV annotation of %s", ref, tm.getGeneSymbol()));
		} else if (var.length() != 1) {
			throw new AnnotationException(String.format(
					"Error: Malformed variant sequence (%s) for SNV annotation of %s", var, tm.getGeneSymbol()));
		}
		char refc = ref.charAt(0);
		char varc = var.charAt(0);
		if (frameShift == 1) {
			// $varnt3 = $wtnt3[0] . $obs . $wtnt3[2];
			varNT3 = String.format("%c%c%c", wtnt3.charAt(0), varc, wtnt3.charAt(2));
			// $canno = "c.$wtnt3[1]" . ($refvarstart-$refcdsstart+1) . $obs;
			cDNAAnno = String.format("c.%d%c>%c", (refVarStart - refCDSStart + 1), wtnt3.charAt(1), varc);
			if (refc != wtnt3.charAt(1)) {
				char strand = tm.getStrand();
				wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1), strand);
			}
		} else if (frameShift == 2) {
			// $varnt3 = $wtnt3[0] . $wtnt3[1]. $obs;
			varNT3 = String.format("%c%c%c", wtnt3.charAt(0), wtnt3.charAt(1), varc);
			// $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[2] . ">" . $obs;
			cDNAAnno = String.format("c.%d%c>%c", (refVarStart - refCDSStart + 1), wtnt3.charAt(2), varc);
			if (refc != wtnt3.charAt(2)) {
				char strand = tm.getStrand();
				wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1), strand);
			}
		} else { /* i.e., frame_s == 0 */
			// $varnt3 = $obs . $wtnt3[1] . $wtnt3[2];
			varNT3 = String.format("%c%c%c", varc, wtnt3.charAt(1), wtnt3.charAt(2));
			// $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[0] . ">" . $obs;
			cDNAAnno = String.format("c.%d%c>%c", (refVarStart - refCDSStart + 1), wtnt3.charAt(0), varc);
			// System.out.println("wtnt3=" + wtnt3 + " varnt3=" + varnt3 + " canno=" + canno);
			// System.out.println(kgl.getGeneSymbol() + ":" + kgl.getName());
			// System.out.println("refvarstart=" + refvarstart + " refcdsstart = " + refcdsstart);
			// kgl.debugPrint();
			if (refc != wtnt3.charAt(0)) {
				char strand = tm.getStrand();
				wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1), strand);
			}
		}
		String wtAA = translator.translateDNA(wtnt3);
		String varAA = translator.translateDNA(varNT3);
		int aaVarPos = (int) Math.floor((refVarStart - tm.getRefCDSStart()) / 3) + 1;

		if (wtAA.equals(varAA)) {
			// $wtaa eq '*' and ($wtaa, $varaa) = qw/X X/; #change * to X in the output NO! Not HGVS conform
			// $function->{$index}{ssnv} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";
			if (wtAA.equals(varAA))
				protAnno = String.format("%s:exon%d:%s:p.=", tm.getName(), exonNumber, cDNAAnno);
			else
				protAnno = String.format("%s:exon%d:%s:p.%s%d%s", tm.getName(), exonNumber, cDNAAnno, wtAA, aaVarPos,
						varAA);
			varType = VariantType.SYNONYMOUS;
		} else if (varAA.equals("*")) {
			// $function->{$index}{stopgain} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa${varpos}X,";
			protAnno = String.format("%s:exon%d:%s:p.%s%d*", tm.getName(), exonNumber, cDNAAnno, wtAA, aaVarPos);
			varType = VariantType.STOPGAIN;
		} else if (wtAA.equals("*")) {
			/* i.e., the wildtype codon is the stop codon */
			protAnno = String.format("%s:exon%d:%s:p.*%d%s", tm.getName(), exonNumber, cDNAAnno, aaVarPos, varAA);
			varType = VariantType.STOPLOSS;
		} else { /* Missense */
			// $function->{$index}{nssnv} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";
			protAnno = String
					.format("%s:exon%d:%s:p.%s%d%s", tm.getName(), exonNumber, cDNAAnno, wtAA, aaVarPos, varAA);
			varType = VariantType.MISSENSE;

		}
		if (wrng != null)
			protAnno = String.format("%s:[%s]", protAnno, wrng);
		return new Annotation(tm, protAnno, varType, cdsPos);
	}

	/**
	 * Creates annotation for a single-nucleotide substitution on the minus strand.
	 *
	 * @param kgl
	 *            The known gene that corresponds to the deletion caused by the variant.
	 * @param frame_s
	 *            0 if deletion begins at first base of codon, 1 if it begins at second base, 2 if at third base
	 * @param wtnt3
	 *            Nucleotide sequence of wildtype codon
	 * @param ref
	 *            sequence of wildtype sequence
	 * @param var
	 *            alternate sequence (should be '-')
	 * @param refvarstart
	 *            Position of the variant in the CDS of the known gene
	 * @param exonNumber
	 *            Number of the affected exon (zero-based, already corrected for being on minus strand).
	 * @return An annotation corresponding to the deletion.
	 * @throws jannovar.exception.AnnotationException
	 */
	public static Annotation getAnnotationMinusStrand(TranscriptModel kgl, int frame_s, String wtnt3, String ref, String var, int refvarstart, int exonNumber) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */

		String canno = null; // cDNA annotation.
		String panno = null;
		String varnt3 = null;

		int refcdsstart = kgl.getRefCDSStart(); /* position of start codon in transcript. */
		int cdspos = refvarstart - refcdsstart + 1; /* position of mutation in CDS, numbered from start codon */
		/*System.out.println(String.format("GetAnnMinus refcdsstart=%d,refvarstart=%d,diff=%d",
		  refcdsstart,refvarstart,refvarstart-refcdsstart));*/
		if (ref.length() != 1) {
			throw new AnnotationException(String.format("Error: Malformed reference sequence (%s) for SNV annotation of %s", ref, kgl.getGeneSymbol()));
		} else if (var.length() != 1) {
			throw new AnnotationException(String.format("Error: Malformed variant sequence (%s) for SNV annotation of %s", var, kgl.getGeneSymbol()));
		}
		char refc = ref.charAt(0);
		char varc = var.charAt(0);
		if (frame_s == 1) {
			// $varnt3 = $wtnt3[0] . $obs . $wtnt3[2];
			varnt3 = String.format("%c%c%c", wtnt3.charAt(0), varc, wtnt3.charAt(2));
			// $canno = "c.$wtnt3[1]" . ($refvarstart-$refcdsstart+1) . $obs;
			canno = String.format("c.%d%c>%c", (refvarstart - refcdsstart + 1), wtnt3.charAt(1), varc);
			if (refc != wtnt3.charAt(1)) {
				char strand = kgl.getStrand();
				String wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1), strand);
				canno = String.format("%s [%s]", canno, wrng);
			}
		} else if (frame_s == 2) {
			// $varnt3 = $wtnt3[0] . $wtnt3[1]. $obs;
			varnt3 = String.format("%c%c%c", wtnt3.charAt(0), wtnt3.charAt(1), varc);
			// $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[2] . ">" . $obs;
			canno = String.format("c.%d%c>%c", (refvarstart - refcdsstart + 1), wtnt3.charAt(2), varc);
			if (refc != wtnt3.charAt(2)) {
				char strand = kgl.getStrand();
				String wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1), strand);
				canno = String.format("%s [%s]", canno, wrng);
			}
		} else { /* i.e., frame_s == 0 */
			// $varnt3 = $obs . $wtnt3[1] . $wtnt3[2];
			varnt3 = String.format("%c%c%c", varc, wtnt3.charAt(1), wtnt3.charAt(2));
			// $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[0] . ">" . $obs;
			canno = String.format("c.%d%c>%c", (refvarstart - refcdsstart + 1), wtnt3.charAt(0), varc);
			if (refc != wtnt3.charAt(0)) {
				char strand = kgl.getStrand();
				String wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1), strand);
				canno = String.format("%s [%s]", canno, wrng);
			}
		}
		String wtaa = translator.translateDNA(wtnt3);
		String varaa = translator.translateDNA(varnt3);
		int aavarpos = (int) Math.floor((refvarstart - kgl.getRefCDSStart()) / 3) + 1;

		if (wtaa.equals(varaa)) {
			if (wtaa.equals(varaa))
				panno = String.format("%s:exon%d:%s:p.=", kgl.getName(), exonNumber, canno);
			else
				panno = String.format("%s:exon%d:%s:p.%s%d%s", kgl.getName(), exonNumber, canno, wtaa, aavarpos, varaa);
			Annotation ann = new Annotation(kgl, panno, VariantType.SYNONYMOUS, cdspos);
			return ann;
		} else if (varaa.equals("*")) {
			// $function->{$index}{stopgain} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa${varpos}X,";
			panno = String.format("%s:exon%d:%s:p.%s%d*", kgl.getName(), exonNumber, canno, wtaa, aavarpos);
			Annotation ann = new Annotation(kgl, panno, VariantType.STOPGAIN, cdspos);
			return ann;
		} else if (wtaa.equals("*")) {
			panno = String.format("%s:exon%d:%s:p.*%d%s", kgl.getName(), exonNumber, canno, aavarpos, varaa);
			Annotation ann = new Annotation(kgl, panno, VariantType.STOPLOSS, cdspos);
			return ann;
		} else { /* Missense */
			// $function->{$index}{nssnv} .= "$geneidmap->{$seqid}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";
			panno = String.format("%s:exon%d:%s:p.%s%d%s", kgl.getName(), exonNumber, canno, wtaa, aavarpos, varaa);
			Annotation ann = new Annotation(kgl, panno, VariantType.MISSENSE, cdspos);
			return ann;
		}

	}

}
/* eof */