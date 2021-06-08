package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinRange;
import de.charite.compbio.jannovar.hgvs.protein.ProteinSeqDescription;

// TODO(holtgrewe): Remove seqDesc?

/**
 * Unchanged mark inside protein.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProteinUnchanged extends ProteinChange {

	/**
	 * range of one or more amino acids that are deleted
	 */
	private final ProteinRange range;
	/**
	 * specification of the deleted characters, can be null
	 */
	private final ProteinSeqDescription seqDesc;

	/**
	 * Construct ProteinDeletion without length and sequence information
	 */
	public static ProteinUnchanged buildWithoutSeqDescription(boolean onlyPredicted, String firstAA, int firstPos,
                                                              String lastAA, int lastPos) {
		return new ProteinUnchanged(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos));
	}

	/**
	 * Construct ProteinDeletion with length information
	 */
	public static ProteinUnchanged buildWithLength(boolean onlyPredicted, String firstAA, int firstPos,
                                                   String lastAA, int lastPos, int length) {
		return new ProteinUnchanged(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), length);
	}

	/**
	 * Construct ProteinDeletion with sequence
	 */
	public static ProteinUnchanged buildWithSequence(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
                                                     int lastPos, String seq) {
		return new ProteinUnchanged(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos), seq);
	}

	/**
	 * Construct ProteinDeletion without length and sequence information
	 */
	public ProteinUnchanged(boolean onlyPredicted, ProteinRange range) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = new ProteinSeqDescription();
	}

	/**
	 * Construct ProteinDeletion with length information
	 */
	public ProteinUnchanged(boolean onlyPredicted, ProteinRange range, int length) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = new ProteinSeqDescription(length);
	}

	/**
	 * Construct ProteinDeletion with sequence information
	 */
	public ProteinUnchanged(boolean onlyPredicted, ProteinRange range, String seq) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = new ProteinSeqDescription(seq);
	}

	private ProteinUnchanged(boolean onlyPredicted, ProteinRange range, ProteinSeqDescription seqDesc) {
		super(onlyPredicted);
		this.range = range;
		this.seqDesc = seqDesc;
	}

	/**
	 * @return deleted range in the protein
	 */
	public ProteinRange getRange() {
		return range;
	}

	/**
	 * @return description of the deleted sequence
	 */
	public ProteinSeqDescription getSeqDesc() {
		return seqDesc;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfOnlyPredicted(range.toHGVSString(code) + "=");
	}

	@Override
	public ProteinChange withOnlyPredicted(boolean onlyPredicted) {
		return new ProteinUnchanged(onlyPredicted, this.range, this.seqDesc);
	}

}
