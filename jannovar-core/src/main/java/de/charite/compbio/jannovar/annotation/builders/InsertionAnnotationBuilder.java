package de.charite.compbio.jannovar.annotation.builders;

import java.util.ArrayList;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDuplication;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInsertion;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinDuplication;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinExtension;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinFrameshift;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinIndel;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinInsertion;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinMiscChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinMiscChangeType;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;
import de.charite.compbio.jannovar.impl.util.Translator;
import de.charite.compbio.jannovar.reference.AminoAcidChange;
import de.charite.compbio.jannovar.reference.AminoAcidChangeNormalizer;
import de.charite.compbio.jannovar.reference.CDSPosition;
import de.charite.compbio.jannovar.reference.DuplicationChecker;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.NucleotidePointLocationBuilder;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;

/**
 * Builds {@link Annotation} objects for the insertion {@link GenomeVariant} in the given {@link TranscriptModel}
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
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class InsertionAnnotationBuilder extends AnnotationBuilder {

	/**
	 * @param transcript
	 *            {@link TranscriptInfo} to build the annotation for
	 * @param change
	 *            {@link GenomeVariant} to build the annotation with
	 * @param options
	 *            the configuration to use for the {@link AnnotationBuilder}
	 * @throws InvalidGenomeVariant
	 *             if <code>change</code> did not describe an insertion
	 */
	InsertionAnnotationBuilder(TranscriptModel transcript, GenomeVariant change, AnnotationBuilderOptions options)
			throws InvalidGenomeVariant {
		super(transcript, change, options);

		// Guard against invalid genome change.
		if (change.getRef().length() != 0 || change.getAlt().length() == 0)
			throw new InvalidGenomeVariant("GenomeChange " + change + " does not describe an insertion.");
	}

	@Override
	public Annotation build() {
		// Go through top-level cases (clustered by how they are handled here) and build annotations for each of them
		// where applicable.

		if (!transcript.isCoding())
			return buildNonCodingAnnotation();

		// We have the base left and/or right of the insertion to determine the cases.
		final GenomePosition pos = change.getGenomePos();
		final GenomePosition lPos = change.getGenomePos().shifted(-1);
		if ((so.liesInCDSExon(lPos) && so.liesInCDSExon(pos)) && so.liesInCDS(lPos) && so.liesInCDS(pos))
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
	protected NucleotideChange getCDSNTChange() {
		if (!so.liesInExon(change.getGenomePos()))
			return new NucleotideInsertion(false, ntChangeRange, new NucleotideSeqDescription(change.getAlt()));

		// For building the HGVS string in transcript locations, we have to check for duplications.
		//
		// The super class constructor will normalize the insertion. Thus, we can work with he assumption that any
		// duplicated characters are left of the insertion.
		TranscriptPosition txPos;
		try {
			txPos = projector.genomeToTranscriptPos(change.getGenomePos());
		} catch (ProjectionException e) {
			throw new Error("Bug: at this point, the position must be a transcript position");
		}
		if (DuplicationChecker.isDuplication(transcript.getSequence(), change.getAlt(), txPos.getPos())) {
			NucleotidePointLocationBuilder posBuilder = new NucleotidePointLocationBuilder(transcript);
			if (change.getAlt().length() == 1) {
				try {
					final NucleotideRange range = new NucleotideRange(posBuilder.getNucleotidePointLocation(projector
							.transcriptToGenomePos(txPos.shifted(-1))), posBuilder.getNucleotidePointLocation(projector
							.transcriptToGenomePos(txPos.shifted(-1))));
					return new NucleotideDuplication(false, range, new NucleotideSeqDescription());
				} catch (ProjectionException e) {
					throw new RuntimeException("Bug: positions should be valid here", e);
				}
			} else {
				try {
					final NucleotidePointLocation firstPos = posBuilder.getNucleotidePointLocation(projector
							.transcriptToGenomePos(txPos.shifted(-change.getAlt().length())));
					final NucleotidePointLocation lastPos = posBuilder.getNucleotidePointLocation(projector
							.transcriptToGenomePos(txPos.shifted(-1)));
					final NucleotideRange range = new NucleotideRange(firstPos, lastPos);
					return new NucleotideDuplication(false, range, new NucleotideSeqDescription());
				} catch (ProjectionException e) {
					throw new RuntimeException("Bug: positions should be valid here", e);
				}
			}
		} else {
			return new NucleotideInsertion(false, ntChangeRange, new NucleotideSeqDescription(change.getAlt()));
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
		ArrayList<VariantEffect> varTypes = new ArrayList<VariantEffect>();
		// the amino acid change, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		AminoAcidChange aaChange;
		// the predicted protein change, updated in handleFrameShiftCase() and handleNonFrameShiftCase()
		ProteinChange proteinChange;

		public CDSExonicAnnotationBuilder() {
			this.wtCDSSeq = projector.getTranscriptStartingAtCDS();
			this.varCDSSeq = seqChangeHelper.getCDSWithGenomeVariant(change);

			// Get position of insertion on CDS level, will obtain AA change pos after normalization.
			this.insertPos = projector.projectGenomeToCDSPosition(change.getGenomePos());

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
			this.varAAInsertPos = this.aaChange.getPos();
		}

		public Annotation build() {
			// Guard against the case that aaChange describes a "" -> "" change (synonymous change in stop codon).
			if (aaChange.getRef().length() == 0 && aaChange.getAlt().length() == 0) {
				proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE);
				varTypes.add(VariantEffect.SYNONYMOUS_VARIANT);
			} else {
				// We do not have the corner case of "">"" but can go on with frameshift/non-frameshift distinction.
				if (change.getAlt().length() % 3 == 0)
					handleNonFrameShiftCase();
				else
					handleFrameShiftCase();
			}

			return new Annotation(transcript, change, varTypes, locAnno, getGenomicNTChange(), getCDSNTChange(),
					proteinChange);
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
			// TODO(holtgrew): This is wrong.
			proteinChange = ProteinInsertion.buildWithSequence(true, toString(wtAASeq.charAt(varAAInsertPos - 1)),
					varAAInsertPos, toString(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos,
					varAASeq.substring(varAAInsertPos - 1, varAASeq.length()));
			if (varAAStopPos != -1)
				varTypes.add(VariantEffect.STOP_GAINED);
			varTypes.add(VariantEffect.INFRAME_INSERTION);

			return true;
		}

		private void handleFrameShiftCaseWTStartWithStopCodon() {
			// The WT has a stop codon at the insert position.
			if (varAAStopPos == varAAInsertPos) {
				// The variant peptide also starts with a stop codon, is synonymous frameshift insertion.
				proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE);
				varTypes.add(VariantEffect.SYNONYMOUS_VARIANT);
			} else if (varAAStopPos > varAAInsertPos) {
				// The variant peptide contains a stop codon but does not start with it, is frameshift insertion. In
				// this case we cannot really differentiate this from a non-frameshift insertion but we still call
				// it so.
				proteinChange = ProteinExtension.build(true, "*", varAAInsertPos,
						toString(varAASeq.charAt(varAAInsertPos)), (varAAStopPos - varAAInsertPos));
				varTypes.add(VariantEffect.FRAMESHIFT_ELONGATION);
			} else {
				// The variant AA does not contain a stop codon, is stop loss.
				proteinChange = ProteinFrameshift.buildWithoutTerminal(true, toString(varAASeq.charAt(varAAInsertPos)),
						varAAInsertPos, toString(varAASeq.charAt(varAAInsertPos)));
				varTypes.add(VariantEffect.STOP_LOST);
			}
		}

		private void handleFrameShiftCaseWTStartsWithNoStopCodon() {
			// The wild type peptide does not start with a stop codon.
			if (varAAInsertPos == 0) {
				// The mutation affects the start codon, is start loss.
				proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_PROTEIN);
				varTypes.add(VariantEffect.START_LOST);
			} else {
				// The start codon is not affected.
				if (varAAStopPos == varAAInsertPos) {
					// The insertion directly creates a stop codon, is stop gain.
					proteinChange = ProteinSubstitution.build(true, toString(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos, "*");
					varTypes.add(VariantEffect.STOP_GAINED);
				} else if (varAAStopPos > varAAInsertPos) {
					// The insertion is a frameshift variant that leads to a transcript still having a stop codon,
					// simple frameshift insertion.
					proteinChange = ProteinFrameshift.build(true, toString(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos, toString(varAASeq.charAt(varAAInsertPos)),
							(varAAStopPos + 1 - varAAInsertPos));

					if (varAASeq.length() > wtAASeq.length())
						varTypes.add(VariantEffect.FRAMESHIFT_ELONGATION);
					else if (varAASeq.length() < wtAASeq.length())
						varTypes.add(VariantEffect.FRAMESHIFT_TRUNCATION);
					else
						varTypes.add(VariantEffect.FRAMESHIFT_VARIANT);
				} else {
					// The insertion is a frameshift variant that leads to the loss of the stop codon, we mark this as
					// frameshift elongation and the "fs*?" indicates that the stop codon is lost.
					proteinChange = ProteinFrameshift.buildWithoutTerminal(true,
							toString(wtAASeq.charAt(varAAInsertPos)), varAAInsertPos,
							toString(varAASeq.charAt(varAAInsertPos)));
					varTypes.add(VariantEffect.FRAMESHIFT_ELONGATION);
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
				proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_CHANGE);
				varTypes.add(VariantEffect.SYNONYMOUS_VARIANT);
			} else if (varAAStopPos > 0) {
				// varAA contains a stop codon
				proteinChange = ProteinExtension.build(true, "*", aaChange.getPos(),
						toString(varAASeq.charAt(aaChange.getPos())), (varAAStopPos - aaChange.getPos()));
				// last is stop codon AA pos
				varTypes.add(VariantEffect.STOP_LOST);
			} else {
				// varAA contains no stop codon
				proteinChange = ProteinExtension.buildWithoutTerminal(true,
						toString(wtAASeq.charAt(aaChange.getPos())), aaChange.getPos(),
						toString(varAASeq.charAt(aaChange.getPos())));
				varTypes.add(VariantEffect.STOP_LOST);
			}
		}

		private void handleNonFrameShiftCaseStartsWithNoStopCodon() {
			// WT stop codon is not subjected to insertion.
			if (aaChange.getPos() == 0) {
				// The mutation affects the start codon, is start loss (in the case of keeping the start codon
				// intact, we would have jumped into a shifted duplication case earlier.
				proteinChange = ProteinMiscChange.build(true, ProteinMiscChangeType.NO_PROTEIN);
				varTypes.add(VariantEffect.START_LOST);
				varTypes.add(VariantEffect.INFRAME_INSERTION);
			} else {
				// The start codon is not affected. Since it is a non-FS insertion, the stop codon cannot be
				// affected.
				if (varAAStopPos == varAAInsertPos) {
					// The insertion directly starts with a stop codon, is stop gain.
					proteinChange = ProteinSubstitution.build(true, toString(wtAASeq.charAt(varAAInsertPos)),
							varAAInsertPos, "*");
					varTypes.add(VariantEffect.STOP_GAINED);

					// Differentiate the case of disruptive and non-disruptive insertions.
					if (insertPos.getPos() % 3 == 0)
						varTypes.add(VariantEffect.INFRAME_INSERTION);
					else
						varTypes.add(VariantEffect.DISRUPTIVE_INFRAME_INSERTION);
				} else {
					if (varAAStopPos != -1 && wtAAStopPos != -1
							&& varAASeq.length() - varAAStopPos != wtAASeq.length() - wtAAStopPos) {
						// The insertion does not directly start with a stop codon but the insertion leads to a stop
						// codon in the affected amino acids. This leads to an "delins" protein annotation.
						proteinChange = ProteinIndel.buildWithSeqDescription(true,
								toString(wtAASeq.charAt(varAAInsertPos)), varAAInsertPos,
								toString(wtAASeq.charAt(varAAInsertPos + 1)), varAAInsertPos + 1,
								new ProteinSeqDescription(),
								new ProteinSeqDescription(varAASeq.substring(varAAInsertPos, varAAStopPos)));
						varTypes.add(VariantEffect.STOP_GAINED);

						addNonFrameshiftInsertionEffect();
					} else {
						// The changes on the amino acid level do not lead to a new stop codon, is non-FS insertion.

						// Differentiate the ins and the delins case.
						if (aaChange.getRef().equals("")) {
							// Clean insertion.
							if (DuplicationChecker.isDuplication(wtAASeq, aaChange.getAlt(), varAAInsertPos)) {
								// We have a duplication, can only be duplication of AAs to the left because of
								// normalization in CDSExonicAnnotationBuilder constructor.
								if (aaChange.getAlt().length() == 1) {
									proteinChange = ProteinDuplication.buildWithSeqDescription(true,
											toString(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos - 1,
											toString(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos - 1,
											new ProteinSeqDescription());
								} else {
									proteinChange = ProteinDuplication.buildWithSeqDescription(true,
											toString(wtAASeq.charAt(varAAInsertPos - aaChange.getAlt().length())),
											varAAInsertPos - aaChange.getAlt().length(),
											toString(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos - 1,
											new ProteinSeqDescription());
								}

								addNonFrameshiftInsertionEffect();
								varTypes.add(VariantEffect.DIRECT_TANDEM_DUPLICATION);
							} else {
								// We have a simple insertion.
								proteinChange = ProteinInsertion.buildWithSequence(true,
										toString(wtAASeq.charAt(varAAInsertPos - 1)), varAAInsertPos - 1,
										toString(wtAASeq.charAt(varAAInsertPos)), varAAInsertPos, aaChange.getAlt());

								addNonFrameshiftInsertionEffect();
							}
						} else {
							// The delins/substitution case.
							proteinChange = ProteinIndel.buildWithSeqDescription(true, aaChange.getRef(),
									varAAInsertPos, aaChange.getRef(), varAAInsertPos, new ProteinSeqDescription(),
									new ProteinSeqDescription(aaChange.getAlt()));
							addNonFrameshiftInsertionEffect();
						}
					}
				}
			}
		}

		/**
		 * Add insertion effect to {@link #varTypes}, depending on whether the insertion is disruptive or not.
		 */
		private void addNonFrameshiftInsertionEffect() {
			// Differentiate the case of disruptive and non-disruptive insertions.
			if (insertPos.getPos() % 3 == 0)
				varTypes.add(VariantEffect.INFRAME_INSERTION);
			else
				varTypes.add(VariantEffect.DISRUPTIVE_INFRAME_INSERTION);
		}

		/** Helper function for char to String conversion. */
		protected String toString(char c) {
			return Character.toString(c);
		}
	}

}
