package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

// TODO(holtgrewe): Remove seqDesc?

/**
 * In-frame deletion of a protein (i.e., without frameshift, but can destroy codons).
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinDeletion extends ProteinChange {

	/** range of one or more amino acids that are deleted */
	private final ProteinRange range;
	/** specification of the deleted characters, can be null */
	private final ProteinSeqDescription seqDesc;

	/** Construct ProteinDeletion without length and sequence information */
	public static ProteinDeletion buildWithoutSeqDescription(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos));
	}

	/** Construct ProteinDeletion with length information */
	public static ProteinDeletion buildWithLength(boolean onlyPredicted, String firstAA, int firstPos,
			String lastAA, int lastPos, int length) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), length);
	}

	/** Construct ProteinDeletion with sequence */
	public static ProteinDeletion buildWithSequence(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, String seq) {
		return new ProteinDeletion(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), seq);
	}

	/** Construct ProteinDeletion without length and sequence information */
	public ProteinDeletion(boolean onlyPredicted, ProteinRange range) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = new ProteinSeqDescription();
	}

	/** Construct ProteinDeletion with length information */
	public ProteinDeletion(boolean onlyPredicted, ProteinRange range, int length) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = new ProteinSeqDescription(length);
	}

	/** Construct ProteinDeletion with sequence information */
	public ProteinDeletion(boolean onlyPredicted, ProteinRange range, String seq) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = new ProteinSeqDescription(seq);
	}

	/** @return deleted range in the protein */
	public ProteinRange getRange() {
		return range;
	}

	/** @return description of the deleted sequence */
	public ProteinSeqDescription getSeqDesc() {
		return seqDesc;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfOnlyPredicted(range.toHGVSString(code) + "del" + seqDesc.toHGVSString(code));
	}

}
