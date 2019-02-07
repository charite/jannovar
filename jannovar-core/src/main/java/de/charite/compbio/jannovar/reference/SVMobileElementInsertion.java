package de.charite.compbio.jannovar.reference;

/**
 * Insertion of mobile element relative to the reference.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class SVMobileElementInsertion extends SVGenomeVariant {

	/**
	 * Initialize all fields.
	 *
	 * @param genomePos        Genome start/first position.
	 * @param posCILowerBound  Lower bound of confidence interval around {@code genomePos}.
	 * @param posCIUpperBound  Upper bound of confidence interval around {@code genomePos}.
	 */
	public SVMobileElementInsertion(GenomePosition genomePos, int posCILowerBound, int posCIUpperBound) {
		super(genomePos, genomePos, posCILowerBound, posCIUpperBound, posCILowerBound, posCIUpperBound);
	}

	@Override
	public SVDescription.Type getType() {
		return SVDescription.Type.INS_ME;
	}


	@Override
	public SVMobileElementInsertion withStrand(Strand strand) {
		if (genomePos.getStrand() != strand) {
			return new SVMobileElementInsertion(
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
		return "SVMobileElementInsertion{" +
			"genomePos=" + genomePos +
			", posCILowerBound=" + posCILowerBound +
			", posCIUpperBound=" + posCIUpperBound +
			'}';
	}

}
