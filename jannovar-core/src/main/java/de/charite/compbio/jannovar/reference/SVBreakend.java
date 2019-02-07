package de.charite.compbio.jannovar.reference;

/**
 * Breakend.
 * <p>
 * Note that breakends describe non-linear events and are slightly different.  E.g., when changing the strand, only the
 * strand of the first position will be changed.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class SVBreakend extends SVGenomeVariant {

	/**
	 * Describe the end point of the breakend.
	 */
	public enum Side {
		/**
		 * Left breakend
		 */
		LEFT_END,
		/**
		 * Right breakend
		 */
		RIGHT_END
	}

	/**
	 * Bases located in the reference at first/start pos.
	 */
	private final String posRefBases;

	/**
	 * Bases located in the reference at secon/end pos.
	 */
	private final String pos2RefBases;

	/**
	 * The side of the breakend.
	 */
	private final Side side;

	/**
	 * Initialize all fields.
	 *
	 * @param genomePos        Genome start/first position.
	 * @param genomePos2       Genome second/end position.
	 * @param posCILowerBound  Lower bound of confidence interval around {@code genomePos}.
	 * @param posCIUpperBound  Upper bound of confidence interval around {@code genomePos}.
	 * @param pos2CILowerBound Lower bound of confidence interval around {@code genomePos2}.
	 * @param pos2CIUpperBound Upper bound of confidence interval around {@code genomePos2}.
	 * @param posRefBases      Bases in the reference at {@code genomePos}.
	 * @param pos2RefBases     Bases in the reference at {@code genomePos2}.
	 * @param side             The breakend side.
	 */
	public SVBreakend(GenomePosition genomePos, GenomePosition genomePos2, int posCILowerBound, int posCIUpperBound,
					  int pos2CILowerBound, int pos2CIUpperBound, String posRefBases, String pos2RefBases,
					  Side side) {
		super(genomePos, genomePos2, posCILowerBound, posCIUpperBound, pos2CILowerBound, pos2CIUpperBound);
		this.posRefBases = posRefBases;
		this.pos2RefBases = pos2RefBases;
		this.side = side;
	}

	@Override
	public SVDescription.Type getType() {
		return SVDescription.Type.BND;
	}


	@Override
	public SVBreakend withStrand(Strand strand) {
		if (genomePos.getStrand() != strand) {
			return new SVBreakend(
				genomePos.withStrand(strand),
				genomePos2,
				posCIUpperBound,
				posCILowerBound,
				pos2CILowerBound,
				pos2CIUpperBound,
				posRefBases,
				pos2RefBases,
				side
			);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		return "SVBreakend{" +
			"genomePos=" + genomePos +
			", genomePos2=" + genomePos2 +
			", posCILowerBound=" + posCILowerBound +
			", posCIUpperBound=" + posCIUpperBound +
			", pos2CILowerBound=" + pos2CILowerBound +
			", pos2CIUpperBound=" + pos2CIUpperBound +
			", posRefBases=" + posRefBases +
			", pos2RefBases=" + pos2RefBases +
			", side=" + side +
			'}';
	}

}
