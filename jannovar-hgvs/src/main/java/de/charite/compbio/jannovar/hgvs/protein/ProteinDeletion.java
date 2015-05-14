package de.charite.compbio.jannovar.hgvs.protein;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

// TODO(holtgrew): ProteinSeqSpecification should also store whether to display at all and we should integrate this here

/**
 * In-frame deletion of a protein (i.e., without frameshift, but can destroy codons).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinDeletion extends ProteinChange {

	/** range of one or more amino acids that are deleted */
	private final ProteinRange range;
	/** specification of the deleted characters, can be null */
	private final ProteinSeqDescription seqSpec;

	public static ProteinDeletion build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA, int lastPos) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos));
	}

	public static ProteinDeletion build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, int length) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), length);
	}

	public static ProteinDeletion build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, String seq) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), seq);
	}

	public ProteinDeletion(boolean onlyPredicted, ProteinRange range) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = new ProteinSeqDescription();
	}

	public ProteinDeletion(boolean onlyPredicted, ProteinRange range, int length) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = new ProteinSeqDescription(length);
	}

	public ProteinDeletion(boolean onlyPredicted, ProteinRange range, String seq) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = new ProteinSeqDescription(seq);
	}

	public ProteinRange getRange() {
		return range;
	}

	public ProteinSeqDescription getSeqSpec() {
		return seqSpec;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfPredicted(range.toHGVSString(code) + "del" + seqSpec.toHGVSString(code));
	}

}
