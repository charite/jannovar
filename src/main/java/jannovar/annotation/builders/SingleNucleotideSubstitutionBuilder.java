package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.exception.InvalidGenomeChange;
import jannovar.reference.GenomeChange;
import jannovar.reference.TranscriptInfo;

/**
 * This class provides static methods to generate annotations for SNVs in exons.
 *
 * <h1>Caveats with Mismatches Between mRNA and Reference Sequence</h1>
 *
 * Note that there are numerous variants in positions that show a discrepancy between the genomic sequence of hg19 and
 * the UCSC knownGene mRNA sequences. This is a difficult issue because it is uncertain which sequence is correct.
 * Annovar uses its own version of the knownGeneMrna.txt file that is made to conform exactly with the genome sequence.
 *
 * However, by inspection, the mRNA sequence actually appears to be the correct one. Therefore, our strategy is as
 * follows:
 *
 * <ul>
 * <li>for the changed bases (i.e., the REF bases from the VCF file), we use these bases instead of the mRNA bases,</li>
 * <li>for all other bases (including the neighboring ones, also those used for translating into amino acids), we use
 * the mRNA bases, and</li>
 * <li>a warning is added to the annotation whenever a discrepancy between the VCF REF value and the mRNA bases is
 * detected.</li>
 * </ul>
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Marten Jäger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class SingleNucleotideSubstitutionBuilder {

	/**
	 * Returns a {@link Annotation} for the SNV {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for
	 * @param change
	 *            {@link GenomeChange} to compute the annotation for
	 * @return annotation for the given change to the given transcript
	 *
	 * @throws InvalidGenomeChange
	 *             if there are problems with the position in <code>change</code> (position out of CDS) or when
	 *             <code>change</code> does not describe a SNV. In the case that <code>change</code> contains an
	 *             inconsistent nucleotide change (<code>change.ref</code> does not equal the amino acid at the given
	 *             position) then the returned {@link Annotation} will contain a warning in the annotation.
	 */
	public static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change) throws InvalidGenomeChange {
		// guard against invalid genome change
		if (change.ref.length() != 1 || change.alt.length() != 1)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a SNV.");

		// project the strand of change to the same strand as transcript
		change = change.withStrand(transcript.getStrand());

		// Forward everything to the helper.
		return new SingleNucleotideSubstitutionBuilderHelper(transcript, change).build();
	}

}
