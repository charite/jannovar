package de.charite.compbio.jannovar.reference;

/**
 * Insertion of novel sequence relative to the reference.
 * <p>
 * For insertions, the second position is equal to the first, as is the confidence interval around the second position.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class SVInsertion extends SVGenomeVariant {

	/**
	 * Initialize all fields.
	 *
	 * @param genomePos       Genome start/first position.
	 * @param posCILowerBound Lower bound of confidence interval around {@code genomePos}.
	 * @param posCIUpperBound Upper bound of confidence interval around {@code genomePos}.
	 */
	public SVInsertion(GenomePosition genomePos, int posCILowerBound, int posCIUpperBound) {
		super(genomePos, genomePos, posCILowerBound, posCIUpperBound, posCILowerBound, posCIUpperBound);
	}

	@Override
	public SVDescription.Type getType() {
		return SVDescription.Type.INS;
	}

	@Override
	public SVInsertion withStrand(Strand strand) {
		if (genomePos.getStrand() != strand) {
			return new SVInsertion(
				genomePos.withStrand(strand),
				this.posCIUpperBound,
				this.posCILowerBound
			);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		return "SVInsertion{" +
			"genomePos=" + genomePos +
			", posCILowerBound=" + posCILowerBound +
			", posCIUpperBound=" + posCIUpperBound +
			'}';
	}

}
