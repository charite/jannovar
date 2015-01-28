package de.charite.compbio.jannovar.annotation.builders;

import java.util.ArrayList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeChange;
import de.charite.compbio.jannovar.annotation.VariantType;
import de.charite.compbio.jannovar.impl.util.StringUtil;
import de.charite.compbio.jannovar.impl.util.Translator;
import de.charite.compbio.jannovar.reference.AminoAcidChange;
import de.charite.compbio.jannovar.reference.AminoAcidChangeNormalizer;
import de.charite.compbio.jannovar.reference.CDSPosition;
import de.charite.compbio.jannovar.reference.DuplicationChecker;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.HGVSPositionBuilder;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;

/**
 * Builds {@link Annotation} objects for the insertion {@link GenomeChange} in the given {@link TranscriptInfo}.
 *
 * <h2>Duplications</h2>
 *
 * In the case of insertions that are duplications are annotated as such. These insertions must be in the coding region,
 * such that the duplication can be recognized from the transcript sequence.
 *
 * <h2>Shifting of Insertions</h2>
 *
 * In the case of ambiguities, the HGVS specification requires the variant to be shifted towards the 3' end of the
 * transcript ("rightmost" position). This can cause an insertion to be shifted into the 3' UTR or a splice site.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class InsertionAnnotationBuilder extends AnnotationBuilder {

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            {@link GenomeChange} to build the annotation with
	 * @throws InvalidGenomeChange
	 *             if <code>change</code> did not describe an insertion
	 */
	InsertionAnnotationBuilder(TranscriptModel transcript, GenomeChange change) throws InvalidGenomeChange {
		super(transcript, change);

		// Guard against invalid genome change.
		if (change.ref.length() != 0 || change.alt.length() == 0)
			throw new InvalidGenomeChange("GenomeChange " + change + " does not describe an insertion.");
	}

	@Override
	public Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		if (!transcript.isCoding())
			return buildNonCodingAnnotation();

		// We have the base left and/or right of the insertion to determine the cases.
		final GenomePosition pos = change.pos;
		final GenomePosition lPos = change.pos.shifted(-1);
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
	protected String ncHGVS() {
		if (!so.liesInExon(change.pos))
			return StringUtil.concatenate(locAnno.toHGVSString(), ":", dnaAnno, "ins", change.alt);

		// For building the HGVS string in transcript locations, we have to check for duplications.
		//
		// The super class constructor will normalize the insertion. Thus, we can work with he assumption that any
		// duplicated characters are left of the insertion.
		TranscriptPosition txPos;
		try {
			txPos = projector.genomeToTranscriptPos(change.pos);
		} catch (ProjectionException e) {
			throw new Error("Bug: at this point, the position must be a transcript position");
		}
		if (DuplicationChecker.isDuplication(transcript.sequence, change.alt, txPos.pos)) {
			HGVSPositionBuilder posBuilder = new HGVSPositionBuilder(transcript);
			char prefix = transcript.isCoding() ? 'c' : 'n';
			String dnaAnno = null; // override this.dnaAnno
			if (change.alt.length() == 1) {
				dnaAnno = StringUtil.concatenate(prefix, ".", posBuilder.getCDNAPosStr(change.pos.shifted(-1)), "dup");
			} else {
				dnaAnno = StringUtil.concatenate(prefix, ".",
						posBuilder.getCDNAPosStr(change.pos.shifted(-change.alt.length())), "_",
						posBuilder.getCDNAPosStr(change.pos.shifted(-1)), "dup");
			}

			return StringUtil.concatenate(locAnno.toHGVSString(), ":", dnaAnno);
		} else {
			return StringUtil.concatenate(locAnno.toHGVSString(), ":", dnaAnno, "ins", change.alt);
		}
	}

	private Annotation buildCDSExonicAnnotation() {
		return new CDSExonicAnnotationBuilder().build();
	}

	/**
	 * Helper class for generating annotations for exonic CDS variants.
	 *
	 * We use this helper class to simplify the access to the parameters such as {@link #wtCDSSeq} etc.
	 */
	private class CDSExonicAnnotationBuilder {
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
		ArrayList<VariantType> varTypes = new ArrayList<VariantType>();
		// the amino acid change, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		AminoAcidChange aaChange;
		// the protein annotation, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		String protAnno;

		public CDSExonicAnnotationBuilder() {
			this.wtCDSSeq = projector.getTranscriptStartingAtCDS();
			this.varCDSSeq = seqChangeHelper.getCDSWithChange(change);

			// Get position of insertion on CDS level, will obtain AA change pos after normalization.
			this.insertPos = projector.projectGenomeToCDSPosition(change.pos);

			// TODO(holtgrem): Not translating in the cases we don't need it might save time
			// Translate the variant CDS sequence and look for stop codon.
			this.wtAASeq = t.translateDNA(wtCDSSeq);
			this.wtAAStopPos = wtAASeq.indexOf('*', this.insertPos.pos / 3);
			this.varAASeq = t.translateDNA(varCDSSeq);
			this.varAAStopPos = varAASeq.indexOf('*', this.insertPos.pos / 3);

			// Build initial aaChange. This is correct for non-FS insertions, and the first affected bases for FS
			// insertions
			final int insertAAPos = this.insertPos.pos / 3;
			final int delta = (this.insertPos.getFrameshift() == 0 ? 0 : 1);
			int insertAALength = ((change.alt.length() + 2) / 3) + delta;
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
				varTypes.add(VariantType.SYNONYMOUS);
			} else {
				// We do not have the corner case of "">"" but can go on with frameshift/non-frameshift distinction.
				if (change.alt.length() % 3 == 0)
					handleNonFrameShiftCase();
				else
					handleFrameShiftCase();
			}

			TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
			GenomePosition pos = change.getGenomeInterval().getGenomeBeginPos();
			int txBeginPos = projector.projectGenomeToCDSPosition(pos).pos;

			return new Annotation(varTypes, locAnno, txBeginPos, StringUtil.concatenate(ncHGVS(), ":", protAnno),
					transcript);
		}

		private void handleFrameShiftCase() {
			// Differentiate the cases where the WT has a stop codon at the change position. We also need to guard
			// against an insertion at the end of the amino acid and the case that introduces a non-existing stop codon
			// at the end.
			if (handleInsertionAtEndInCaseOfNoStopCodon())
				return;
			final boolean isInsertionAtEnd = (varAAInsertPos == wtAASeq.length());
			if (!isInsertionAtEnd && wtAASeq.charAt(varAAInsertPos) == '*')
				handleFrameShiftCaseWTStartWithStopCodon();
			else
				handleFrameShiftCaseWTStartsWithNoStopCodon();
		}

		/**
		 * Deal with an insertion to an amino acid string that does not have a stop codon yet.
		 */
		private boolean handleInsertionAtEndInCaseOfNoStopCodon() {
			// TODO(holtgrew): At some point, try to merge these corner cases that are caused by bogus transcript
			// entries into the main cases or decide to ignore these bad cases.

			// Return false if this is not the case this function deals with.
			if (varAAInsertPos != wtAASeq.length() || wtAAStopPos != -1)
				return false;

			// TODO(holtgrew): Check for duplication? This is a very rare corner case with bogus transcript.
			protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos,
					t.toLong(varAASeq.substring(varAAInsertPos - 1, varAASeq.length())));
			if (varAAStopPos != -1)
				varTypes.add(VariantType.STOPGAIN);
			varTypes.add(VariantType.NON_FS_INSERTION);

			return true;
		}

		private void handleFrameShiftCaseWTStartWithStopCodon() {
			// The WT has a stop codon at the insert position.
			if (varAAStopPos == varAAInsertPos) {
				// The variant peptide also starts with a stop codon, is synonymous frameshift insertion.
				protAnno = "p.=";
				varTypes.add(VariantType.SYNONYMOUS);
			} else if (varAAStopPos > varAAInsertPos) {
				// The variant peptide contains a stop codon but does not start with it, is frameshift insertion. In
				// this case we cannot really differentiate this from a non-frameshift insertion but we still call
				// it
				// so.
				protAnno = StringUtil.concatenate("p.*", varAAInsertPos + 1, t.toLong(varAASeq.charAt(varAAInsertPos)),
						"ext*", (varAAStopPos - varAAInsertPos));
				varTypes.add(VariantType.FS_INSERTION);
			} else {
				// The variant AA does not contain a stop codon, is stop loss.
				protAnno = StringUtil.concatenate("p.", t.toLong(varAASeq.charAt(varAAInsertPos)), varAAInsertPos + 1,
						t.toLong(varAASeq.charAt(varAAInsertPos)), "fs*?");
				varTypes.add(VariantType.STOPLOSS);
			}
		}

		private void handleFrameShiftCaseWTStartsWithNoStopCodon() {
			// The wild type peptide does not start with a stop codon.
			if (varAAInsertPos == 0) {
				// The mutation affects the start codon, is start loss.
				protAnno = "p.0?";
				varTypes.add(VariantType.START_LOSS);
			} else {
				// The start codon is not affected.
				if (varAAStopPos == varAAInsertPos) {
					// The insertion directly creates a stop codon, is stop gain.
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos + 1, "*");
					varTypes.add(VariantType.STOPGAIN);
				} else if (varAAStopPos > varAAInsertPos) {
					// The insertion is a frameshift variant that leads to a transcript still having a stop codon,
					// simple frameshift insertion.
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos + 1, t.toLong(varAASeq.charAt(varAAInsertPos)), "fs*",
							(varAAStopPos + 1 - varAAInsertPos));
					varTypes.add(VariantType.FS_INSERTION);
				} else {
					// The insertion is a frameshift variant that leads to the loss of the stop codon, is stop loss.
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos + 1, t.toLong(varAASeq.charAt(varAAInsertPos)), "fs*?");
					varTypes.add(VariantType.STOPLOSS);
				}
			}
		}

		private void handleNonFrameShiftCase() {
			// Differentiate the cases where the WT has a stop codon at the change position. We also need to guard
			// against the insertion being at the end of the encode amino acid string.
			final boolean isInsertionAtEnd = (varAAInsertPos == wtAASeq.length());
			if (!isInsertionAtEnd && wtAASeq.charAt(varAAInsertPos) == '*')
				handleNonFrameShiftCaseStartsWithStopCodon();
			else
				handleNonFrameShiftCaseStartsWithNoStopCodon();
		}

		private void handleNonFrameShiftCaseStartsWithStopCodon() {
			// WT stop codon is subjected to insertion (start codon untouched).
			if (varAAStopPos == 0) {
				// varAA starts with a stop codon
				protAnno = "p.=";
				varTypes.add(VariantType.SYNONYMOUS);
			} else if (varAAStopPos > 0) {
				// varAA contains a stop codon
				protAnno = StringUtil.concatenate("p.*", aaChange.pos + 1, t.toLong(varAASeq.charAt(aaChange.pos)),
						"ext*", (varAAStopPos - aaChange.pos)); // last is stop codon AA pos
				varTypes.add(VariantType.STOPLOSS);
			} else {
				// varAA contains no stop codon
				protAnno = StringUtil.concatenate("p.*", t.toLong(wtAASeq.charAt(aaChange.pos)), aaChange.pos + 1,
						t.toLong(varAASeq.charAt(aaChange.pos)), "fs*?");
				varTypes.add(VariantType.STOPLOSS);
			}
		}

		private void handleNonFrameShiftCaseStartsWithNoStopCodon() {
			// WT stop codon is not subjected to insertion.
			if (aaChange.pos == 0) {
				// The mutation affects the start codon, is start loss (in the case of keeping the start codon
				// intact, we would have jumped into a shifted duplication case earlier.
				protAnno = "p.0?";
				varTypes.add(VariantType.START_LOSS);
				varTypes.add(VariantType.NON_FS_INSERTION);
			} else {
				// The start codon is not affected. Since it is a non-FS insertion, the stop codon cannot be
				// affected.
				if (varAAStopPos == varAAInsertPos) {
					// The insertion directly starts with a stop codon, is stop gain.
					protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos + 1, "*");
					varTypes.add(VariantType.STOPGAIN);
					varTypes.add(VariantType.NON_FS_INSERTION);
				} else {
					if (varAAStopPos != -1 && wtAAStopPos != -1
							&& varAASeq.length() - varAAStopPos != wtAASeq.length() - wtAAStopPos) {
						// The insertion does not directly start with a stop codon but the insertion leads to a stop
						// codon in the affected amino acids. This leads to an "delins" protein annotation.
						protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos)),
								varAAInsertPos + 1, "_", t.toLong(wtAASeq.charAt(varAAInsertPos + 1)),
								varAAInsertPos + 2, "delins",
								t.toLong(varAASeq.substring(varAAInsertPos, varAAStopPos)));
						varTypes.add(VariantType.STOPGAIN);
						varTypes.add(VariantType.NON_FS_INSERTION);
					} else {
						// The changes on the amino acid level do not lead to a new stop codon, is non-FS insertion.

						// Differentiate the ins and the delins case.
						if (aaChange.ref.equals("")) {
							// Clean insertion.
							if (DuplicationChecker.isDuplication(wtAASeq, aaChange.alt, varAAInsertPos)) {
								// We have a duplication, can only be duplication of AAs to the left because of
								// normalization in CDSExonicAnnotationBuilder constructor.
								if (aaChange.alt.length() == 1) {
									protAnno = StringUtil.concatenate("p.",
											t.toLong(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos, "dup");
								} else {
									protAnno = StringUtil.concatenate("p.",
											t.toLong(wtAASeq.charAt(varAAInsertPos - aaChange.alt.length())),
											varAAInsertPos - aaChange.alt.length() + 1, "_",
											t.toLong(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos, "dup");
								}
								varTypes.add(VariantType.NON_FS_DUPLICATION);
							} else {
								// We have a simple insertion.
								protAnno = StringUtil.concatenate("p.", t.toLong(wtAASeq.charAt(varAAInsertPos - 1)),
										varAAInsertPos, "_", t.toLong(wtAASeq.charAt(varAAInsertPos)),
										varAAInsertPos + 1, "ins", t.toLong(aaChange.alt));
								varTypes.add(VariantType.NON_FS_INSERTION);
							}
						} else {
							// The delins/substitution case.
							protAnno = StringUtil.concatenate("p.", t.toLong(aaChange.ref), varAAInsertPos + 1,
									"delins", t.toLong(aaChange.alt));
							varTypes.add(VariantType.NON_FS_SUBSTITUTION);
						}
					}
				}
			}
		}
	}

}
