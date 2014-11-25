package jannovar.annotation.builders;

import jannovar.annotation.Annotation;
import jannovar.common.VariantType;
import jannovar.exception.ProjectionException;
import jannovar.reference.AminoAcidChange;
import jannovar.reference.AminoAcidChangeNormalizer;
import jannovar.reference.CDSPosition;
import jannovar.reference.DuplicationTester;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.HGVSPositionBuilder;
import jannovar.reference.PositionType;
import jannovar.reference.TranscriptInfo;
import jannovar.reference.TranscriptPosition;
import jannovar.util.Translator;

/**
 * Helper class that allows to remove various copy and paste code in {@link InsertionAnnotationBuilder}.
 */
class InsertionAnnotationBuilderHelper extends AnnotationBuilderHelper {

	InsertionAnnotationBuilderHelper(TranscriptInfo transcript, GenomeChange change) {
		super(transcript, change);
	}

	@Override
	Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		if (!transcript.isCoding())
			return buildNonCodingAnnotation();

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
		if (!so.liesInExon(change.getPos()))
			return String.format("%s:%sins%s", locAnno, dnaAnno, change.getAlt());

		// For building the HGVS string in transcript locations, we have to check for duplications.
		//
		// The super class constructor will normalize the insertion. Thus, we can work with he assumption that any
		// duplicated characters are left of the insertion.
		TranscriptPosition txPos;
		try {
			txPos = projector.genomeToTranscriptPos(change.getPos());
		} catch (ProjectionException e) {
			throw new Error("Bug: at this point, the position must be a transcript position");
		}
		if (DuplicationTester.isDuplication(transcript.sequence, change.getAlt(), txPos.getPos())) {
			HGVSPositionBuilder posBuilder = new HGVSPositionBuilder(transcript);
			char prefix = transcript.isCoding() ? 'c' : 'n';
			String dnaAnno = null; // override this.dnaAnno
			if (change.getAlt().length() == 1) {
				dnaAnno = String.format("%c.%sdup", prefix, posBuilder.getCDNAPosStr(change.getPos().shifted(-1)));
			} else {
				dnaAnno = String.format("%c.%s_%sdup", prefix,
						posBuilder.getCDNAPosStr(change.getPos().shifted(-change.getAlt().length())),
						posBuilder.getCDNAPosStr(change.getPos().shifted(-1)));
			}

			return String.format("%s:%s", locAnno, dnaAnno);
		} else {
			return String.format("%s:%sins%s", locAnno, dnaAnno, change.getAlt());
		}
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

		return new CDSExonicAnnotationBuilder(this).build();
	}

	/**
	 * Helper class for generating annotations for exonic CDS variants.
	 *
	 * We use this helper class to simplify the access to the parameters such as {@link #cdsPos} etc.
	 */
	private class CDSExonicAnnotationBuilder {
		final InsertionAnnotationBuilderHelper owner;

		final Translator t = Translator.getTranslator();

		// wild type CDS nucleotide sequence
		final String wtCDSSeq;
		// variant CDS nucleotide sequence
		final String varCDSSeq;

		// wild type amino acid sequence
		final String wtAASeq;
		// position of stop codon in wtAASeq, or -1 if none
		final int wtAAStopPos;
		// variant amino acid sequence
		final String varAASeq;
		// position of stop codon in varAASeq, or -1 if none
		final int varAAStopPos;

		// Insertion position on CDS.
		final CDSPosition insertPos;
		// Insert position in amino acid string.
		final int varAAInsertPos; // TODO(holtgrem): replace with aaChange.pos

		// We keep the following three variables as state of the algorithm since we do not have easy-to-use triples in
		// Java.

		// the variant type, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		VariantType varType;
		// the amino acid change, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		AminoAcidChange aaChange;
		// the protein annotation, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		String protAnno;

		public CDSExonicAnnotationBuilder(InsertionAnnotationBuilderHelper owner) {
			this.owner = owner;

			this.wtCDSSeq = projector.getTranscriptStartingAtCDS();
			this.varCDSSeq = seqChangeHelper.getCDSWithChange(change);

			// Get position of insertion on CDS level, will obtain AA change pos after normalization.
			this.insertPos = projector.projectGenomeToCDSPosition(change.getPos()).withPositionType(
					PositionType.ZERO_BASED);

			// TODO(holtgrem): Not translating in the cases we don't need it might save time
			// Translate the variant CDS sequence and look for stop codon.
			this.wtAASeq = t.translateDNA(wtCDSSeq);
			this.wtAAStopPos = wtAASeq.indexOf('*', this.insertPos.getPos() / 3);
			this.varAASeq = t.translateDNA(varCDSSeq);
			this.varAAStopPos = varAASeq.indexOf('*', this.insertPos.getPos() / 3);

			// Build initial aaChange. This is correct for non-FS insertions, and the first affected bases for FS
			// insertions
			final int insertAAPos = this.insertPos.getPos() / 3;
			final int delta = (this.insertPos.getFrameshift() == 0 ? 0 : 1);
			int insertAALength = ((change.getAlt().length() + 2) / 3) + delta;
			if (insertAAPos + insertAALength > varAASeq.length())
				insertAALength = varAASeq.length() - insertAAPos;
			final String delAA = wtAASeq.substring(insertAAPos, insertAAPos + delta);
			final String insertAA = varAASeq.substring(insertAAPos, insertAAPos + insertAALength);
			this.aaChange = new AminoAcidChange(insertAAPos, delAA, insertAA);
			this.aaChange = AminoAcidChangeNormalizer.truncateAltAfterStopCodon(aaChange);
			this.aaChange = AminoAcidChangeNormalizer.truncateBothSides(aaChange);
			this.aaChange = AminoAcidChangeNormalizer.shiftInsertion(aaChange, wtAASeq);
			// Obtain amino acid insertion position.
			this.varAAInsertPos = this.aaChange.pos;
		}

		public Annotation build() {
			// Guard against the case that aaChange describes a "" -> "" change (synonymous change in stop codon).
			if (aaChange.ref.length() == 0 && aaChange.alt.length() == 0) {
				protAnno = "p.=";
				varType = VariantType.SYNONYMOUS;
			} else {
				// We do not have the corner case of "">"" but can go on with frameshift/non-frameshift distinction.
				if (change.getAlt().length() % 3 == 0)
					handleNonFrameShiftCase();
				else
					handleFrameShiftCase();
			}

			return new Annotation(transcript.transcriptModel, String.format("%s:%s", ncHGVS(), protAnno), varType);
		}

		private void handleFrameShiftCase() {
			// Differentiate the cases where the WT has a stop codon at the change position.
			if (wtAASeq.charAt(varAAInsertPos) == '*')
				handleFrameShiftCaseWTStartWithStopCodon();
			else
				handleFrameShiftCaseWTStartsWithNoStopCodon();
		}

		private void handleFrameShiftCaseWTStartWithStopCodon() {
			// The WT has a stop codon at the insert position.
			if (varAAStopPos == varAAInsertPos) {
				// The variant peptide also starts with a stop codon, is synonymous frameshift insertion.
				protAnno = "p.=";
				varType = VariantType.SYNONYMOUS;
			} else if (varAAStopPos > varAAInsertPos) {
				// The variant peptide contains a stop codon but does not start with it, is frameshift insertion. In
				// this case we cannot really differentiate this from a non-frameshift insertion but we still call
				// it
				// so.
				protAnno = String.format("p.*%d%sext*%d", varAAInsertPos + 1,
						t.toLong(varAASeq.charAt(varAAInsertPos)), (varAAStopPos - varAAInsertPos));
				varType = VariantType.FS_INSERTION;
			} else {
				// The variant AA does not contain a stop codon, is stop loss.
				protAnno = String.format("p.%s%d%sfs*?", t.toLong(varAASeq.charAt(varAAInsertPos)), varAAInsertPos + 1,
						t.toLong(varAASeq.charAt(varAAInsertPos)));
				varType = VariantType.STOPLOSS;
			}
		}

		private void handleFrameShiftCaseWTStartsWithNoStopCodon() {
			// The wild type peptide does not start with a stop codon.
			if (varAAInsertPos == 0) {
				// The mutation affects the start codon, is start loss.
				protAnno = "p.0?";
				varType = VariantType.START_LOSS;
			} else {
				// The start codon is not affected.
				if (varAAStopPos == varAAInsertPos) {
					// The insertion directly creates a stop codon, is stop gain.
					protAnno = String.format("p.%s%d*", t.toLong(wtAASeq.charAt(varAAInsertPos)), varAAInsertPos + 1);
					varType = VariantType.STOPGAIN;
				} else if (varAAStopPos > varAAInsertPos) {
					// The insertion is a frameshift variant that leads to a transcript still having a stop codon,
					// simple frameshift insertion.
					protAnno = String.format("p.%s%d%sfs*%d", t.toLong(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos + 1, t.toLong(varAASeq.charAt(varAAInsertPos)),
							(varAAStopPos + 1 - varAAInsertPos));
					varType = VariantType.FS_INSERTION;
				} else {
					// The insertion is a frameshift variant that leads to the loss of the stop codon, is stop loss.
					protAnno = String.format("p.%s%d%sfs*?", t.toLong(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos + 1, t.toLong(varAASeq.charAt(varAAInsertPos)));
					varType = VariantType.STOPLOSS;
				}
			}
		}

		private void handleNonFrameShiftCase() {
			if (wtAASeq.charAt(varAAInsertPos) == '*')
				handleNonFrameShiftCaseStartsWithStopCodon();
			else
				handleNonFrameShiftCaseStartsWithNoStopCodon();
		}

		private void handleNonFrameShiftCaseStartsWithStopCodon() {
			// WT stop codon is subjected to insertion (start codon untouched).
			if (varAAStopPos == 0) {
				// varAA starts with a stop codon
				protAnno = "p.=";
				varType = VariantType.SYNONYMOUS;
			} else if (varAAStopPos > 0) {
				// varAA contains a stop codon
				protAnno = String.format("p.*%d%sext*%d", aaChange.pos + 1, t.toLong(varAASeq.charAt(aaChange.pos)),
						(varAAStopPos - aaChange.pos)); // last is stop codon AA pos
				varType = VariantType.STOPLOSS;
			} else {
				// varAA contains no stop codon
				protAnno = String.format("p.%s%d%sfs*?", t.toLong(wtAASeq.charAt(aaChange.pos)), aaChange.pos + 1,
						t.toLong(varAASeq.charAt(aaChange.pos)));
				varType = VariantType.STOPLOSS;
			}
		}

		private void handleNonFrameShiftCaseStartsWithNoStopCodon() {
			// WT stop codon is not subjected to insertion.
			if (aaChange.pos == 0) {
				// The mutation affects the start codon, is start loss (in the case of keeping the start codon
				// intact, we would have jumped into a shifted duplication case earlier.
				protAnno = "p.0?";
				varType = VariantType.START_LOSS;
			} else {
				// The start codon is not affected. Since it is a non-FS insertion, the stop codon cannot be
				// affected.
				if (varAAStopPos == varAAInsertPos) {
					// The insertion directly starts with a stop codon, is stop gain.
					protAnno = String.format("p.%s%d*", t.toLong(wtAASeq.charAt(varAAInsertPos)), varAAInsertPos + 1);
					varType = VariantType.STOPGAIN;
				} else {
					if (varAAStopPos != -1 && wtAAStopPos != -1
							&& varAASeq.length() - varAAStopPos != wtAASeq.length() - wtAAStopPos) {
						// The insertion does not directly start with a stop codon but the insertion leads to a stop
						// codon in the affected amino acids. This leads to an "delins" protein annotation.
						protAnno = String.format("p.%s%d_%s%ddelins%s", t.toLong(wtAASeq.charAt(varAAInsertPos)),
								varAAInsertPos + 1, t.toLong(wtAASeq.charAt(varAAInsertPos + 1)), varAAInsertPos + 2,
								t.toLong(varAASeq.substring(varAAInsertPos, varAAStopPos)));
						varType = VariantType.STOPGAIN;
					} else {
						// The changes on the amino acid level do not lead to a new stop codon, is non-FS insertion.

						// Differentiate the ins and the delins case.
						if (aaChange.ref.equals("")) {
							// Clean insertion.
							if (DuplicationTester.isDuplication(wtAASeq, aaChange.alt, varAAInsertPos)) {
								// We have a duplication, can only be duplication of AAs to the left because of
								// normalization in CDSExonicAnnotationBuilder constructor.
								if (aaChange.alt.length() == 1) {
									protAnno = String.format("p.%s%ddup", t.toLong(wtAASeq.charAt(varAAInsertPos - 1)),
											varAAInsertPos);
								} else {
									protAnno = String.format("p.%s%d_%s%ddup",
											t.toLong(wtAASeq.charAt(varAAInsertPos - aaChange.alt.length())),
											varAAInsertPos - aaChange.alt.length() + 1,
											t.toLong(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos);
								}
								varType = VariantType.NON_FS_DUPLICATION;
							} else {
								// We have a simple insertion.
								protAnno = String.format("p.%s%d_%s%dins%s",
										t.toLong(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos,
										t.toLong(wtAASeq.charAt(varAAInsertPos)), varAAInsertPos + 1,
										t.toLong(aaChange.alt));
								varType = VariantType.NON_FS_INSERTION;
							}
						} else {
							// The delins/substitution case.
							protAnno = String.format("p.%s%ddelins%s", t.toLong(aaChange.ref), varAAInsertPos + 1,
									t.toLong(aaChange.alt));
							varType = VariantType.NON_FS_SUBSTITUTION;
						}
					}
				}
			}
		}
	}

}
