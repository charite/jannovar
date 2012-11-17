package exomizer.reference;

import exomizer.common.Constants;
import exomizer.reference.KnownGene;

import java.util.ArrayList;


/**
 * This class collects all the information about a variant and its annotations and 
 * calculates the final annotations for a given variant. It uses the annotations
 * that were calculated for each of the genes in the vicinity of the variant and
 * decides upon the best final variant using heuristics that were adapted from the
 * annovar package. The main problems seem to arise for deciding how long to search
 * for neighboring genes and intergenic variants. For instance, a problem arose for
 * a variant that is located 5' to gene B, and then gene A has different transcripts.
 * One of the transcripts of gene A is short and thus, the variant is located 3' to
 * this transcript. On the other hand, another transcript of gene A is longer, and the
 * variant is intronic to this one. Therefore, we basically need to look at a number
 * of different annotations, and then decide what the most relevant annotations are.
 * If there is a clear exonic annotation, then usually this is OK and we can stop
 * looking further (this is done in the {@link exomizer.reference.Chromosome Chromosome}
 * class).
 * <P>
 * The default preference for annotations is thus
 * <OL>
 * <LI><B>exonic</B>: variant overlaps a coding exon (does not include 5' or 3' UTR).
 * <LI><B>splicing</B>: variant is within 2-bp of a splicing junction (same precedence as exonic).
 * <LI><B>ncRNA</B>: variant overlaps a transcript without coding annotation in the gene definition 
 * <LI><B>UTR5</B>: variant overlaps a 5' untranslated region 
 * <LI><B>UTR3</B>: variant overlaps a 3' untranslated region 
 * <LI><B>intronic</B>:	variant overlaps an intron 
 * <LI><B>upstream</B>: variant overlaps 1-kb region upstream of transcription start site
 * <LI><B>downstream</B>: variant overlaps 1-kb region downtream of transcription end site (use -neargene to change this)
 * <LI><B>intergenic</B>: variant is in intergenic region 
 * </OL>
 * One object of this class is created for each variant we want to annotate. The {@link exomizer.reference.Chromosome Chromosome}
 * class goes through a list of genes in the vicinity of the variant and adds one {@link exomizer.reference.Annotation Annotation}
 * object for each gene. These are essentially candidates for the actual correct annotation of the variant, but we can
 * only decide what the correct annotation is once we have seen enough candidates. Therefore, once we have gone
 * through the candidates, this class decides what the best annotation is and returns the corresponding 
 * {@link exomizer.reference.Annotation Annotation} object (in some cases, this class may modify the 
 {@link exomizer.reference.Annotation Annotation} object before returning it).
 * @version 0.01 November 15, 2012
 * @author Peter N Robinson
 */

public class AnnotatedVar {
    /** These private constants are used to help keep the variants sorted in a HashMap. */
    private final static int EXONIC_PRECEDENCE = 0;
    private final static int SPLICING_PRECEDENCE = 0;
    private final static int ncRNA_PRECEDENCE = 2;
    private final static int UTR5_PRECEDENCE = 3;
    private final static int UTR3_PRECEDENCE = 4;
    private final static int INTRONIC_PRECEDENCE = 5;
    private final static int UPSTREAM_PRECEDENCE = 6;
    private final static int DOWNSTREAM_PRECEDENCE = 7;
    private final static int INTERGENIC_PRECEDENCE = 8;

    
    /** List of all {@link exomizer.reference.Annotation Annotation}'s found to date for the current variation. */
    private ArrayList<Annotation> annotation_list = null;
    /** Best (lowest) precedence value found to data for any annotation. */
    private int bestPrecedence = Integer.MAX_VALUE;

    public AnnotatedVar() {
	this.annotation_list = new ArrayList<Annotation>();
    }

    /**
     * to do
     */

    public static Annotation chooseBestAnnotation(ArrayList<Annotation> ann_list) {

	return null;

    }


}