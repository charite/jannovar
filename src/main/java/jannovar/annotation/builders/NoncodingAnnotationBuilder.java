package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class provides static methods to generate annotations for non-coding RNA variants.
 * 
 * @version 0.04 (15 April, 2014)
 * @author Peter N Robinson, Marten Jäger
 */
public class NoncodingAnnotationBuilder {

	/**
	 * Return an annotation for a UTR3 mutation for a gene.
	 * 
	 * @param tm
	 *            Gene with splice mutation for current chromosomal variant.
	 * @param rVarStart
	 *            start position of the variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @param exonNumber
	 *            number of the exon affected by the variant (zero-based).
	 * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to a 3' UTR variant.
	 */
	public static Annotation createNoncodingExonicAnnotation(TranscriptModel tm, int rVarStart, String ref, String alt, int exonNumber) {
		// shift
		if (!(ref.length() == 1 && alt.length() == 1 && !alt.equals("-"))) { // only for non singlenucleotide
																				// substitutions
			while (rVarStart + ref.length() < tm.getCDNASequence().length() && tm.getCDNASequence().charAt(rVarStart - 1) == tm.getCDNASequence().charAt(rVarStart + ref.length() - 1)) {
				rVarStart++;
				ref = new StringBuilder().append(ref.substring(1)).append(ref.charAt(0)).toString();
			}
		}

		String annotation = null;
		exonNumber++; /* correct from zero-based to one-based numbering */
		if (alt.equals("-")) {
			/* i.e., deletion  */
			if (ref.length() == 1) {
				annotation = String.format("%s:exon%d:n.%ddel", tm.getName(), exonNumber, rVarStart);
			} else {
				int d2 = rVarStart + ref.length() - 1;
				annotation = String.format("%s:exon%d:n.%d_%ddel", tm.getName(), exonNumber, rVarStart, d2);
			}
		} else if (ref.equals("-")) {
			/* i.e., insertion */
			int d2 = rVarStart + 1; /* get end of insertion */
			annotation = String.format("%s:exon%d:n.%d_%dins%s", tm.getName(), exonNumber, rVarStart, d2, alt);

		} else {
			/* i.e., substitution. The following code will be correct for SNPs, and may need to 
			   be improved in the future for block substitutions. */
			annotation = String.format("%s:exon%d:n.%d%s>%s", tm.getName(), exonNumber, rVarStart, ref, alt);

		}
		Annotation ann = new Annotation(tm, annotation, VariantType.ncRNA_EXONIC, rVarStart);
		ann.setGeneID(tm.getGeneID());
		ann.setGeneSymbol(tm.getGeneSymbol());
		return ann;
	}

}