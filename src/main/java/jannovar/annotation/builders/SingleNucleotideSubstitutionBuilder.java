package jannovar.annotation.builders;

import static jannovar.reference.TranscriptProjectionDecorator.INVALID_EXON_ID;
import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.InvalidGenomeChange;
import jannovar.exception.ProjectionException;
import jannovar.reference.GenomeChange;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptModel;
import jannovar.reference.TranscriptPosition;
import jannovar.reference.TranscriptProjectionDecorator;
import jannovar.util.Translator;

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
	 * Returns a {@link Annotation} for the {@link GenomeChange} in the given {@link TranscriptInfo}.
	 *
	 * This function can only be used for {@link GenomeChange}s that fall into the CDS of the {@link TranscriptInfo}.
	 * Use the {@link UTRAnnotationBuilder} for variants in the non-CDS/UTR region of the transcript.
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
		if (change.getRef().length() != 1 || change.getAlt().length() != 1)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe a SNV.");

		// ensure that the position falls into the CDS region
		if (!transcript.cdsRegion.contains(change.getPos()))
			throw new InvalidGenomeChange("GenomeChange " + change + " does not fall into CDS region "
					+ transcript.cdsRegion);

		// project genome position to transcript and CDS position and handle inconsistent positions
		TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		TranscriptPosition txPos = null;
		TranscriptPosition cdsPos = null;
		try {
			txPos = projector.genomeToTranscriptPos(change.getPos()); // position in tx region
			cdsPos = projector.genomeToCDSPos(change.getPos()); // position in CDS region
		} catch (ProjectionException e) {
			throw new InvalidGenomeChange("Problems with GenomeChange:" + e.getMessage());
		}

		// obtain exon ID
		int exonID = INVALID_EXON_ID;
		try {
			exonID = projector.locateExon(change.getPos());
		} catch (ProjectionException e) {
			throw new InvalidGenomeChange("Problem translating GenomeChange to exon: " + e.getMessage());
		}
		if (exonID == INVALID_EXON_ID)
			throw new InvalidGenomeChange("GenomeChange " + change.getPos() + " does not point to exon.");
		// translate exon ID to reference order and make 1-based for display
		exonID = projector.exonIDInReferenceOrder(exonID) + 1;

		// If we reach here then change describes a SNV and the position points into the CDS of the given transcript.
		// We can now create an Annotation for this GenomeChange without catching exceptions.
		return buildAnnotation(transcript, change, txPos, cdsPos, exonID);
	}

	/**
	 * Build SNV annotation from <code>transcript</code>, <code>change</code> and the position on the transcript.
	 *
	 * The position arguments have already been checked for being valid and no exception is thrown. The only thing that
	 * can go wrong are inconsistencies between the transcript sequence and the variant from the VCF file (in which case
	 * we add a warning to the HGSV annotation string).
	 *
	 * @param transcript
	 *            information of the transcript to generate annotation for
	 * @param change
	 *            genome change, consistently describes a SNV in a CDS and exon region
	 * @param txPos
	 *            transcript position of the genome change
	 * @param cdsPos
	 *            CDS position of the genome change
	 * @param exonNumber
	 *            1-based exon number for display
	 * @return the {@link Annotation} describing the {@link GenomeChange} in <code>change</code>
	 */
	private static Annotation buildAnnotation(TranscriptInfo transcript, GenomeChange change, TranscriptPosition txPos,
			TranscriptPosition cdsPos, int exonNumber) {
		// Ensure that txPos and cdsPos are 0-based.
		txPos = txPos.withPositionType(PositionType.ZERO_BASED);
		cdsPos = cdsPos.withPositionType(PositionType.ZERO_BASED);

		// Check that the WT nucleotide from the transcript is consistent with change.ref and generate a warning message
		// if this is not the case.
		String warningMsg = null;
		if (transcript.sequence.charAt(txPos.getPos()) != change.getRef().charAt(0))
			warningMsg = String.format("WARNING:_mRNA/genome_discrepancy:_%c/%s_strand=%c",
					transcript.sequence.charAt(txPos.getPos()), change.getRef().charAt(0), transcript.getStrand());

		// Compute the frame shift and codon start position.
		int frameShift = cdsPos.getPos() % 3;
		int codonStart = txPos.getPos() - frameShift; // codon start in transcript string
		// Get the transcript codon. From this, we generate the WT and the variant codon. This is important in the case
		// where the transcript differs from the reference. This inconsistency of the reference and the transcript is
		// not necessarily an error in the data base but can also occur in the case of post-transcriptional changes of
		// the transcript.
		String transcriptCodon = transcript.sequence.substring(codonStart, codonStart + 3);
		String wtCodon = updateCodonBase(transcriptCodon, frameShift, change.getRef().charAt(0));
		String varCodon = updateCodonBase(transcriptCodon, frameShift, change.getAlt().charAt(0));

		// Construct the HGSV annotation parts for the transcript location and nucleotides (note that HGSV uses 1-based
		// positions).
		String locAnno = String.format("%s:exon%d", transcript.accession, exonNumber);
		char wtNT = wtCodon.charAt(frameShift); // wild type nucleotide
		char varNT = varCodon.charAt(frameShift); // wild type amino acid
		String cDNAAnno = String.format("c.%d%c>%c", cdsPos.getPos() + 1, wtNT, varNT);
		// Construct annotation part for the protein.
		String wtAA = Translator.getTranslator().translateDNA3(wtCodon);
		String varAA = Translator.getTranslator().translateDNA3(varCodon);
		VariantType varType = computeVariantType(wtAA, varAA);
		String protAnno = String.format("p.%s%d%s", wtAA, cdsPos.getPos() / 3 + 1, varAA);
		if (wtAA.equals(varAA)) // simplify in the case of synonymous SNV
			protAnno = String.format("p.(=)", cdsPos.getPos() / 3 + 1);
		// Glue together the annotations and warning message in annotation if any.
		String annotationStr = String.format("%s:%s:%s", locAnno, cDNAAnno, protAnno);
		if (warningMsg != null)
			annotationStr = String.format("%s:[%s]", annotationStr, warningMsg);

		return new Annotation(transcript.transcriptModel, annotationStr, varType, cdsPos.getPos() + 1);
	}

	/**
	 * @param wtAA
	 *            wild type amino acid
	 * @param varAA
	 *            variant amino acid
	 * @return variant type described by single amino acid change
	 */
	protected static VariantType computeVariantType(String wtAA, String varAA) {
		assert (wtAA.length() == 1 && varAA.length() == 1);
		if (wtAA.equals(varAA))
			return VariantType.SYNONYMOUS;
		else if (wtAA.equals("*"))
			return VariantType.STOPLOSS;
		else if (varAA.equals("*"))
			return VariantType.STOPGAIN;
		else
			return VariantType.MISSENSE;
	}

	/**
	 * @param transcriptCodon
	 *            the wild type codon nucleotide string from the codon
	 * @param frameShift
	 *            the frame within the codon
	 * @param targetNC
	 *            the target nucleotide
	 * @return variant codon string
	 */
	protected static String updateCodonBase(String transcriptCodon, int frameShift, char targetNC) {
		assert (0 <= frameShift && frameShift <= 2);
		if (frameShift == 0)
			return String.format("%c%c%c", targetNC, transcriptCodon.charAt(1), transcriptCodon.charAt(2));
		else if (frameShift == 1)
			return String.format("%c%c%c", transcriptCodon.charAt(0), targetNC, transcriptCodon.charAt(2));
		else
			return String.format("%c%c%c", transcriptCodon.charAt(0), transcriptCodon.charAt(1), targetNC);
	}

	//
	// The code below can go away.
	//

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
	public static Annotation getAnnotation(TranscriptModel tm, int frameShift, int frameEndShift, String wtnt3,
			String ref, String var, int refVarStart, int exonNumber) throws AnnotationException {
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
	public static Annotation getAnnotationMinusStrand(TranscriptModel kgl, int frame_s, String wtnt3, String ref,
			String var, int refvarstart, int exonNumber) throws AnnotationException {
		Translator translator = Translator.getTranslator(); /* Singleton */

		String canno = null; // cDNA annotation.
		String panno = null;
		String varnt3 = null;

		int refcdsstart = kgl.getRefCDSStart(); /* position of start codon in transcript. */
		int cdspos = refvarstart - refcdsstart + 1; /* position of mutation in CDS, numbered from start codon */
		/*
		 * System.out.println(String.format("GetAnnMinus refcdsstart=%d,refvarstart=%d,diff=%d",
		 * refcdsstart,refvarstart,refvarstart-refcdsstart));
		 */
		if (ref.length() != 1) {
			throw new AnnotationException(String.format(
					"Error: Malformed reference sequence (%s) for SNV annotation of %s", ref, kgl.getGeneSymbol()));
		} else if (var.length() != 1) {
			throw new AnnotationException(String.format(
					"Error: Malformed variant sequence (%s) for SNV annotation of %s", var, kgl.getGeneSymbol()));
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
				String wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1),
						strand);
				canno = String.format("%s [%s]", canno, wrng);
			}
		} else if (frame_s == 2) {
			// $varnt3 = $wtnt3[0] . $wtnt3[1]. $obs;
			varnt3 = String.format("%c%c%c", wtnt3.charAt(0), wtnt3.charAt(1), varc);
			// $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[2] . ">" . $obs;
			canno = String.format("c.%d%c>%c", (refvarstart - refcdsstart + 1), wtnt3.charAt(2), varc);
			if (refc != wtnt3.charAt(2)) {
				char strand = kgl.getStrand();
				String wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1),
						strand);
				canno = String.format("%s [%s]", canno, wrng);
			}
		} else { /* i.e., frame_s == 0 */
			// $varnt3 = $obs . $wtnt3[1] . $wtnt3[2];
			varnt3 = String.format("%c%c%c", varc, wtnt3.charAt(1), wtnt3.charAt(2));
			// $canno = "c." . ($refvarstart-$refcdsstart+1) . $wtnt3[0] . ">" . $obs;
			canno = String.format("c.%d%c>%c", (refvarstart - refcdsstart + 1), wtnt3.charAt(0), varc);
			if (refc != wtnt3.charAt(0)) {
				char strand = kgl.getStrand();
				String wrng = String.format("WARNING:_mRNA/genome_discrepancy:_%s/%s_strand=%c", ref, wtnt3.charAt(1),
						strand);
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
