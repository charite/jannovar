package de.charite.compbio.jannovar.reference;

/**
 * Inversion of reference sequence
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class SVInversion extends SVGenomeVariant {

	/**
	 * Initialize all fields.
	 *
	 * @param genomePos        Genome start/first position.
	 * @param genomePos2       Genome second/end position.
	 * @param posCILowerBound  Lower bound of confidence interval around {@code genomePos}.
	 * @param posCIUpperBound  Upper bound of confidence interval around {@code genomePos}.
	 * @param pos2CILowerBound Lower bound of confidence interval around {@code genomePos2}.
	 * @param pos2CIUpperBound Upper bound of confidence interval around {@code genomePos2}.
	 */
	public SVInversion(GenomePosition genomePos, GenomePosition genomePos2, int posCILowerBound, int posCIUpperBound,
					   int pos2CILowerBound, int pos2CIUpperBound) {
		super(genomePos, genomePos2, posCILowerBound, posCIUpperBound, pos2CILowerBound, pos2CIUpperBound);
	}

	@Override
	public SVDescription.Type getType() {
		return SVDescription.Type.INV;
	}

	@Override
	public SVInversion withStrand(Strand strand) {
		if (genomePos.getStrand() != strand) {
			return new SVInversion(
				genomePos2.withStrand(strand),
				genomePos.withStrand(strand),
				this.pos2CIUpperBound,
				this.pos2CILowerBound,
				this.posCIUpperBound,
				this.posCILowerBound
			);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		return "SVInversion{" +
			"genomePos=" + genomePos +
			", genomePos2=" + genomePos2 +
			", posCILowerBound=" + posCILowerBound +
			", posCIUpperBound=" + posCIUpperBound +
			", pos2CILowerBound=" + pos2CILowerBound +
			", pos2CIUpperBound=" + pos2CIUpperBound +
			'}';
	}
}
