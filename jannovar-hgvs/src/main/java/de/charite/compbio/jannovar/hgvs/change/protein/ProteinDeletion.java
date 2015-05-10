package de.charite.compbio.jannovar.hgvs.change.protein;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

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
	private final ProteinSeqSpecification seqSpec;

	public static ProteinDeletion build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA, int lastPos) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstPos, firstAA, lastPos, lastAA));
	}

	public static ProteinDeletion build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, int length) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstPos, firstAA, lastPos, lastAA), length);
	}

	public static ProteinDeletion build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, String seq) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstPos, firstAA, lastPos, lastAA), seq);
	}

	public ProteinDeletion(boolean onlyPredicted, ProteinRange range) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = new ProteinSeqSpecification();
	}

	public ProteinDeletion(boolean onlyPredicted, ProteinRange range, int length) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = new ProteinSeqSpecification(length);
	}

	public ProteinDeletion(boolean onlyPredicted, ProteinRange range, String seq) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = new ProteinSeqSpecification(seq);
	}

	public ProteinRange getRange() {
		return range;
	}

	public ProteinSeqSpecification getSeqSpec() {
		return seqSpec;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfPredicted(range.toHGVSString(code) + "del" + seqSpec.toHGVSString(code));
	}

}
