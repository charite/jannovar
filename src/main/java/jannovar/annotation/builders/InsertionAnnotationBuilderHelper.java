package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.ProjectionException;
import jannovar.reference.CDSPosition;
import jannovar.reference.DuplicationTester;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptPosition;
import jannovar.reference.TranscriptSequenceDecorator;
import jannovar.util.Translator;

/**
 * Helper class that allows to remove various copy and paste code in {@link InsertionAnnotationBuilder}.
 */
class InsertionAnnotationBuilderHelper extends AnnotationBuilderHelper {
	/** Override insertion base annotation string in the case of shifting insertions. */
	private String hgvsInsOverride = null;

	InsertionAnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		super(transcript, change);
	}

	@Override
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		// We have the base left and/or right of the insertion to determine the cases.
		final GenomePosition pos = change.getPos();
		final GenomePosition lPos = change.getPos().shifted(-1);
		if (so.liesInCDSExon(lPos) && so.liesInCDSExon(pos) && so.liesInCDS(lPos) && so.liesInCDS(pos))
			return buildCDSExonicAnnotation(); // can affect amino acids
		else if ((so.liesInCDSIntron(lPos) || so.liesInCDSIntron(pos)) && so.liesInCDS(lPos) && so.liesInCDS(pos))
			return buildIntronicAnnotation(); // intron but no exon => intronic variant
		else if (so.liesInFivePrimeUTR(lPos) || so.liesInThreePrimeUTR(pos))
			return buildUTRAnnotation();
		else if (so.liesInUpstreamRegion(lPos) || so.liesInDownstreamRegion(pos))
			return buildUpOrDownstreamAnnotation();
		else
			return buildIntergenicAnnotation();
	}

	@Override
	String ncHGVS() {
		if (hgvsInsOverride == null)
			return String.format("%s:%sins%s", locAnno, dnaAnno, change.getAlt());
		else
			return String.format("%s:%sins%s", locAnno, dnaAnno, hgvsInsOverride);
	}

	private Annotation buildCDSExonicAnnotation() {
		// Translate the change position to the transcript and the CDS position.
		TranscriptPosition txPos;
		CDSPosition cdsPos;
		try {
			txPos = projector.genomeToTranscriptPos(change.getPos());
			cdsPos = projector.genomeToCDSPos(change.getPos());
		} catch (ProjectionException e) {
			throw new Error("Bug: exonic CDS positions must be translatable to transcript and CDs positions");
		}

		return new CDSExonicAnnotationBuilder(this, txPos, cdsPos).build();
	}

	/**
	 * Helper class for generating annotations for exonic CDS variants.
	 *
	 * We use this helper class to simplify the access to the parameters such as {@link #cdsPos} etc.
	 */
	private class CDSExonicAnnotationBuilder {
		final InsertionAnnotationBuilderHelper owner;

		final Translator t = Translator.getTranslator();

		final CDSPosition cdsPos;
		final String wtNT;
		final String wtAA;
		final String varNT;
		final String varAA;
		final int varAAPos;
		final int varAAIdx;

		public CDSExonicAnnotationBuilder(InsertionAnnotationBuilderHelper owner, TranscriptPosition txPos,
				CDSPosition cdsPos) {
			this.owner = owner;
			this.cdsPos = cdsPos;

			// TODO(holtgrem): Get sequence of smaller environment to save memory?
			// Get wild type codons (the affected plus some upstream) and translate into amino acids.
			this.wtNT = seqDecorator.getCodonsStartingFrom(txPos, cdsPos);
			this.wtAA = Translator.getTranslator().translateDNA(wtNT);

			// Get variant NT string and translate into amino acids.
			this.varNT = TranscriptSequenceDecorator.nucleotidesWithInsertion(wtNT, cdsPos.getPos() % 3,
					change.getAlt());
			this.varAA = Translator.getTranslator().translateDNA(varNT);
			// TODO(holtgrem): use the *Normalizer classes for shifting
			// Shift amino acid insertion to the right (3' end of transcript) as far as possible.
			int varAAPos = cdsPos.getPos() / 3; // original change in AA as position in protein
			int varAAIdx = 0; // original change in AA as index in varAA
			while (varAAIdx + 1 < varAA.length() && varAA.charAt(varAAIdx) == wtAA.charAt(varAAIdx)) {
				++varAAPos;
				++varAAIdx;
			}
			this.varAAPos = varAAPos;
			this.varAAIdx = varAAIdx;
		}

		public Annotation build() {
			if (owner.change.getAlt().length() % 3 == 0)
				return buildNonFrameShiftAnnotation();
			else
				return buildFrameShiftAnnotation();
		}

		private Annotation buildFrameShiftAnnotation() {
			// The code below builds the protein annotation string and the variant type.
			String protAnno = null;
			VariantType varType = null;
			// Check whether there is a stop codon in the variant peptide.
			int stopCodonVarAAIdx = varAA.indexOf("*");

			if (wtAA.startsWith("*")) {
				// The WT peptide starts with a stop codon. Can be frameshift insertion or stop loss.
				if (stopCodonVarAAIdx == 0) {
					// The variant peptide also starts with a stop codon, is frameshift insertion.
					protAnno = "p.=";
					varType = VariantType.SYNONYMOUS;
				} else if (stopCodonVarAAIdx > 0) {
					// The variant peptide contains a stop codon but does not start with it, is frameshift insertion. In
					// this case we cannot really differentiate this from a non-frameshift insertion but we still call
					// it
					// so.
					protAnno = String.format("p.*%d%sext*%d", varAAPos + 1, t.toLong(varAA.charAt(varAAIdx)),
							(stopCodonVarAAIdx - varAAIdx)); // last is stop codon AA pos
					varType = VariantType.FS_INSERTION;
				} else {
					// The variant AA does not contain a stop codon, is stop loss.
					protAnno = String.format("p.%s%d%sfs*?", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1,
							t.toLong(varAA.charAt(varAAIdx)));
					varType = VariantType.STOPLOSS;
				}
			} else {
				// The wild type peptide does not start with a stop codon.
				if (cdsPos.getPos() / 3 == 0) {
					// The mutation affects the start codon, is start loss.
					protAnno = "p.0?";
					varType = VariantType.START_LOSS;
				} else {
					// The start codon is not affected.
					if (stopCodonVarAAIdx == 0) {
						// The insertion directly creates a stop codon, is stop gain.
						protAnno = String.format("p.%s%d*", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1);
						varType = VariantType.STOPGAIN;
					} else if (stopCodonVarAAIdx > 0) {
						// The insertion is a frameshift variant that leads to a transcript still having a stop codon,
						// simple frameshift insertion.
						protAnno = String.format("p.%s%d%sfs*%d", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1,
								t.toLong(varAA.charAt(varAAIdx)), (stopCodonVarAAIdx + 1 - varAAIdx)); // last is stop
																										// codon
																										// AA pos
						varType = VariantType.FS_INSERTION;
					} else {
						// The insertion is a frameshift variant that leads to the loss of the stop codon, is stop loss.
						protAnno = String.format("p.%s%d%sfs*?", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1,
								t.toLong(varAA.charAt(varAAIdx)));
						varType = VariantType.STOPLOSS;
					}
				}
			}

			// Glue together the annotation string and return the annotation.
			String annotationString = String.format("%s:%s", owner.ncHGVS(), protAnno);
			return new Annotation(transcript.transcriptModel, annotationString, varType, cdsPos.getPos() + 1);
		}

		private Annotation buildNonFrameShiftAnnotation() {
			// The code below builds the protein annotation string and the variant type.
			String protAnno = null;
			VariantType varType = null;
			// Check whether there is a stop codon in the variant peptide.
			int stopCodonVarAAIdx = varAA.indexOf("*");
			// Compute position of stop codon in WT AA string.
			int stopCodonWTAAIdx = wtAA.indexOf("*");

			if (wtAA.charAt(0) == '*') {
				// WT stop codon is subjected to insertion (start codon untouched).
				if (stopCodonVarAAIdx == 0) {
					// varAA starts with a stop codon
					protAnno = "p.=";
					varType = VariantType.SYNONYMOUS;
				} else if (stopCodonVarAAIdx > 0) {
					// varAA contains a stop codon
					protAnno = String.format("p.*%d%sext*%d", varAAPos + 1, t.toLong(varAA.charAt(varAAIdx)),
							(stopCodonVarAAIdx - varAAIdx)); // last is stop codon AA pos
					varType = VariantType.NON_FS_INSERTION;
				} else {
					// varAA contains no stop codon
					protAnno = String.format("p.%s%d%sfs*?", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1,
							t.toLong(varAA.charAt(varAAIdx)));
					varType = VariantType.STOPLOSS;
				}
			} else {
				// WT stop codon is not subjected to insertion.
				if (cdsPos.getPos() / 3 == 0) {
					// The mutation affects the start codon, is start loss (in the case of keeping the start codon
					// intact, we would have jumped into a shifted duplication case earlier.
					protAnno = "p.0?";
					varType = VariantType.START_LOSS;
				} else {
					// The start codon is not affected. Since it is a non-FS insertion, the stop codon cannot be
					// affected.
					if (stopCodonVarAAIdx == 0) {
						// The insertion directly starts with a stop codon, is stop gain.
						protAnno = String.format("p.%s%d*", t.toLong(wtAA.charAt(varAAIdx)), varAAPos + 1);
						varType = VariantType.STOPGAIN;
					} else {
						if (varAA.length() - stopCodonVarAAIdx != wtAA.length() - stopCodonWTAAIdx) {
							// The insertion does not directly start with a stop codon but the insertion leads to a stop
							// codon in the affected amino acids. This leads to an "delins" protein annotation.
							protAnno = String.format("p.%s%d_%s%ddelins%s", t.toLong(wtAA.charAt(varAAIdx)),
									varAAPos + 1, t.toLong(wtAA.charAt(varAAIdx + 1)), varAAPos + 2,
									t.toLong(varAA.substring(varAAIdx, stopCodonVarAAIdx)));
							varType = VariantType.STOPGAIN;
						} else {
							// The changes on the amino acid level do not lead to a new stop codon, is non-FS insertion.

							// Compute the insertion AA string and check whether the insertion actually is a
							// duplication.
							String insAA = varAA.substring(varAAIdx, varAAIdx + change.getAlt().length() / 3);

							if (DuplicationTester.isDuplication(wtAA, insAA, varAAIdx)) {
								// We have a duplication, can only be duplication of AAs to the left because of
								// shifting.
								protAnno = String.format("p.%s%d_%s%ddup",
										t.toLong(wtAA.charAt(varAAIdx - insAA.length())),
										varAAPos - insAA.length() + 1,
										t.toLong(wtAA.charAt(varAAIdx - insAA.length() + 1)), varAAPos - insAA.length()
												+ 2);
								varType = VariantType.NON_FS_DUPLICATION;
							} else {
								// We have a simple insertion.
								protAnno = String.format("p.%s%d_%s%dins%s", t.toLong(wtAA.charAt(varAAIdx)),
										varAAPos + 1, t.toLong(wtAA.charAt(varAAIdx + 1)), varAAPos + 2,
										t.toLong(insAA));
								varType = VariantType.NON_FS_INSERTION;
							}
						}
					}
				}
			}

			// Glue together the annotation string and return the annotation.
			String annotationString = String.format("%s:%s", owner.ncHGVS(), protAnno);
			return new Annotation(transcript.transcriptModel, annotationString, varType, cdsPos.getPos() + 1);
		}
	}

}