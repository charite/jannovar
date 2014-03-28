package jannovar.annotation;

import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class is intended to provide a static method to generate annotations for INTRONIC variants. The main work is in
 * finding the distance to the nearest 5' and 3'
 * 
 * @version 0.06 (25 March 2014)
 * @author Peter N Robinson, M Jaeger
 */

public class IntronicAnnotation {

	/**
	 * Create an intronic annotation with a description of the length to the neighboring exon boundaries. When we get
	 * here, the start of exon k is 3' to the variant on the chromosome and the end of exon k-1 is 5' to the variant. We
	 * indicate the distance to the exon boundary exclusive of the exon but inclusive of the first position of the
	 * variant. That is, for a variant at the indicated position CGGgttag[t/c]g, the distance to the final (upper-case)
	 * G of the previous exon is 6. This makes a standard intronic variant CGG[g/a]ttagtg have the expected numbering
	 * (+1, in this case).
	 * 
	 * @param trmdl
	 *            The affected transcript
	 * @param k
	 *            Number of exone (zero based numbering) that is 3' to the variant on chromosome
	 * @param start
	 *            begin position of variant (one-based numbering)
	 * @param end
	 *            end position of variant
	 * @param alt
	 * @param ref
	 * @return the {@link Annotation}
	 */
	public static Annotation createIntronicAnnotation(TranscriptModel trmdl, int k, int start, int end, String ref, String alt) {
		String annot;
		String prefix = "";
		String code = "c";
		// String dist;
		int distL;
		int distR;
		int posToCDS;
		if (trmdl.isNonCodingGene())
			code = "n";
		// System.out.println("code: " + code);
		if (trmdl.isPlusStrand()) {

			if (start < trmdl.getCDSStart()) {// 5'UTR
				prefix = "-";
			}
			if (start > trmdl.getCDSEnd()) {// 3'UTR
				prefix = "*";
			}
			distL = start - trmdl.getExonEnd(k - 1);
			distR = trmdl.getExonStart(k) - end;
			// System.out.println(distL + " vs. " + distR);
			if (distL <= distR) {
				// System.out.println("hoho");
				if (trmdl.isNonCodingGene()) {
					posToCDS = trmdl.getDistanceToFivePrimeTerminuscDNA(trmdl.getExonEnd(k - 1));
					prefix = "";
				} else if (start > trmdl.getCDSEnd()) // 3'UTR distance to CDS end
					posToCDS = trmdl.getDistanceToCDSend(trmdl.getExonEnd(k - 1));
				else
					posToCDS = trmdl.getDistanceToCDSstart(trmdl.getExonEnd(k - 1));
				// dist = "+" + distL;
				annot = String.format("%s:intron%d:%s.%s%d+%d%s>%s", trmdl.getAccessionNumber(), k, code, prefix, posToCDS, distL, ref, alt);
			} else {
				if (trmdl.isNonCodingGene()) {
					posToCDS = trmdl.getDistanceToFivePrimeTerminuscDNA(trmdl.getExonStart(k));
					prefix = "";
				} else if (start > trmdl.getCDSEnd())
					posToCDS = trmdl.getDistanceToCDSend(trmdl.getExonStart(k));
				else
					posToCDS = trmdl.getDistanceToCDSstart(trmdl.getExonStart(k));
				// dist = "-" + distR;
				annot = String.format("%s:intron%d:%s.%s%d-%d%s>%s", trmdl.getAccessionNumber(), k, code, prefix, posToCDS, distR, ref, alt);
			}
			// System.out.println("dist to CDS start: " + posToCDS);

		} else { // "-"-Strand
			if (start > trmdl.getCDSEnd()) { // 5'UTR
				prefix = "-";
			}
			if (end < trmdl.getCDSStart()) { // 3'UTR
				prefix = "*";
			}
			// System.out.println("k: " + k);
			distL = trmdl.getExonStart(k + 1) - end;
			// System.out.println(trmdl.getAccessionNumber() + "  start: " + trmdl.getExonStart(k + 2));
			distR = start - trmdl.getExonEnd(k);
			// System.out.println("distL: " + distL + "\tdistR: " + distR);
			if (distL <= distR) {
				if (trmdl.isNonCodingGene()) {
					posToCDS = trmdl.getDistanceToFivePrimeTerminuscDNA(trmdl.getExonStart(k + 1));
					prefix = "";
				} else if (end < trmdl.getCDSStart()) // 3'UTR distance to CDS end
					posToCDS = trmdl.getDistanceToCDSend(trmdl.getExonStart(k + 1));
				else
					posToCDS = trmdl.getDistanceToCDSstart(trmdl.getExonStart(k + 1));
				// dist = "+" + distL;
				annot = String.format("%s:intron%d:%s.%s%d+%d%s>%s", trmdl.getAccessionNumber(), trmdl.getExonCount() - k - 1, code, prefix, posToCDS, distL, ref, alt);
			} else {
				// System.out.println("exonend: " + trmdl.getExonEnd(k));
				// System.out.println("cds start: " + trmdl.getCDSEnd());
				// System.out.println(trmdl);
				if (trmdl.isNonCodingGene()) {
					posToCDS = trmdl.getDistanceToFivePrimeTerminuscDNA(trmdl.getExonEnd(k));
					prefix = "";
				} else if (end < trmdl.getCDSStart()) // 3'UTR distance to CDS end
					posToCDS = trmdl.getDistanceToCDSend(trmdl.getExonEnd(k));
				else
					posToCDS = trmdl.getDistanceToCDSstart(trmdl.getExonEnd(k));
				// dist = "-" + distR;
				annot = String.format("%s:intron%d:%s.%s%d-%d%s>%s", trmdl.getAccessionNumber(), trmdl.getExonCount() - k - 1, code, prefix, posToCDS, distR, ref, alt);
			}
		}
		// System.out.println("Pos to CDSStart: " + posToCDS);
		int m = Math.min(distR, distL);
		Annotation ann = new Annotation(trmdl, annot, VariantType.INTRONIC);
		ann.setDistanceToNearestExon(m);
		return ann;
	}

	/**
	 * Create an intronic annotation with a description of the length to the neighboring exon boundaries.
	 * 
	 * @param trmdl
	 *            The affected transcript
	 * @param k
	 *            exon number (zero based numbering) that is 3' to the variant on chromosome
	 * @param start
	 *            begin position of variant
	 * @param end
	 *            end position of variant
	 * @param alt
	 * @param ref
	 * @return {@link Annotation} object for an intergenic variant
	 */
	public static Annotation createNcRNAIntronicAnnotation(TranscriptModel trmdl, int k, int start, int end, String ref, String alt) {

		Annotation ann = IntronicAnnotation.createIntronicAnnotation(trmdl, k, start, end, ref, alt);
		ann.setVarType(VariantType.ncRNA_INTRONIC);
		return ann;
	}

}
