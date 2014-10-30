package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.reference.TranscriptModel;

/**
 * This class provides static methods to generate annotations for UTR5 and UTR3 variants (5' untranslated region).
 *
 * The Human Genome Variation Society nomenclature for <b>5' UTR variants</b> works as follows.
 *
 * A G to A substitution 12 nts upstream of the ATG translation initiation codon is refered to as <b>c.-12G>A</b>. To
 * get this far, we first calculate rvarstart in the class {@link jannovar.reference.Chromosome Chromosome} as
 * {@code rvarstart = kgl.getExonStart(k) - kgl.getTXStart() -  cumlenintron + 1;}. Assuming for instance that the 5'
 * UTR has two exons, and that the start codon as well as the
 *
 * For deletions in the 5' UTR, Jannovar performs the following calculations. For instance, if we have
 * chrX:7811234AGCTGCG>-, there is deletion of the bases AGCTGCG in the 5' UTR The nucleotides in the mRNA right before
 * the start codon are agacgttg[agctgcg]gaag[START-CODON]. Thus, the bases -11 to -5 are affected, and we annotate
 * <b>VCX(uc004crz.3:c.-11_-5delAGCTGCG)</b>.
 *
 * The Human Genome Variation Society nomenclature for <b>3' UTR variants</b> works as follows.
 *
 * ....
 *
 * @version 0.13 (7 July, 2013)
 * @author Peter N Robinson
 */

public class UTRAnnotationBuilder {
	/**
	 * Return an annotation for a UTR3 mutation for a gene.
	 *
	 * @param tm
	 *            Gene with splice mutation for current chromosomal variant.
	 * @param refVarStart
	 *            start position of the variant
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to a 3' UTR variant.
	 */
	public static Annotation createUTR3Annotation(TranscriptModel tm, int refVarStart, String ref, String alt) {
		// shift
		while (alt.equals("-")
				&& tm.getCDNASequence().length() > refVarStart + ref.length()
				&& tm.getCDNASequence().charAt(refVarStart - 1) == tm.getCDNASequence().charAt(
						refVarStart + ref.length() - 1)) {
			refVarStart++;
			ref = new StringBuilder().append(ref.substring(1)).append(ref.charAt(0)).toString();
		}

		Annotation ann = Annotation.createEmptyAnnotation();
		ann.setVarType(VariantType.UTR3);
		ann.setGeneSymbol(tm.getGeneSymbol());
		int distance = refVarStart - tm.getRefCDSEnd();

		// System.out.println(trmdl.getName()+": Rvarstart=" + rvarstart + "distance=" + distance);

		if (alt.equals("-")) {

			/* i.e., deletion in the UTR3 region */
			if (ref.length() == 1) {
				String annotation = String.format("%s:c.*%ddel", tm.getName(), distance);
				ann.setVariantAnnotation(annotation);
			} else {
				int d2 = distance + ref.length() - 1;
				String annotation = String.format("%s:c.*%d_*%ddel", tm.getName(), distance, d2);
				ann.setVariantAnnotation(annotation);
			}
		} else if (ref.equals("-")) {
			/* i.e., insertion in the UTR3 region */
			int d2 = distance + 1; /* get end of insertion */
			String annotation = String.format("%s:c.*%d_*%dins%s", tm.getName(), distance, d2, alt);
			ann.setVariantAnnotation(annotation);
		} else {

			String annotation = String.format("%s:c.*%d%s>%s", tm.getName(), distance, ref, alt);
			ann.setVariantAnnotation(annotation);

		}
		ann.setGeneID(tm.getGeneID());
		return ann;
	}

	/**
	 * @param tm
	 *            Gene with splice mutation for current chromosomal variant.
	 * @param refVarStart
	 *            start position of the variant with respect to begin of transcript.
	 * @param ref
	 *            reference sequence
	 * @param alt
	 *            variant sequence
	 * @return An {@link jannovar.annotation.Annotation Annotation} object corresponding to a 5' UTR variant.
	 */
	public static Annotation createUTR5Annotation(TranscriptModel tm, int refVarStart, String ref, String alt) {
		Annotation ann = Annotation.createEmptyAnnotation();
		ann.setVarType(VariantType.UTR5);
		ann.setGeneSymbol(tm.getGeneSymbol());
		int distance = tm.getRefCDSStart() - refVarStart;
		if (alt.equals("-")) {
			/* i.e., deletion in the UTR5 region */
			if (ref.length() == 1) {
				String annotation = String.format("%s:c.-%ddel", tm.getName(), distance);
				ann.setVariantAnnotation(annotation);
			} else {
				int d2 = distance - ref.length() + 1;
				String annotation = String.format("%s:c.-%d_-%ddel", tm.getName(), distance, d2);
				ann.setVariantAnnotation(annotation);
			}
		} else if (ref.equals("-")) {
			/* i.e., insertion in the UTR5 region */
			int d2 = distance - 1; /* get end of insertion */
			String annotation = String.format("%s:c.-%d_-%dins%s", tm.getName(), distance, d2, alt);
			ann.setVariantAnnotation(annotation);
		} else {

			String annotation = String.format("%s:c.-%d%s>%s", tm.getName(), distance, ref, alt);
			ann.setVariantAnnotation(annotation);
		}
		ann.setGeneID(tm.getGeneID());
		return ann;
	}

}