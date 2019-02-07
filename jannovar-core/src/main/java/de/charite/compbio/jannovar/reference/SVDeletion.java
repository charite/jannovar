package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.Immutable;

/**
 * Deletion relative to the reference.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class SVDeletion extends SVGenomeVariant {

	/**
	 * Initialize all fields.
	 *
	 * @param genomePos        Genome start/first position.
	 * @param genomePos2       Genome second/end position.
	 * @param posCILowerBound  Lower bound of confidence interval around {@code genomePos}.
	 * @param posCIUpperBound  Upper bound of confidence interval around {@code genomePos}.
	 * @param pos2CILowerBound Lower bound of confidence interval around {@code genomePos2}.
	 * @param pos2CIUpperBound Upper bound of confidence interval around {@code genomePos2}.
	 * @throws IllegalArgumentException if {@code genomePos} and {@code genomePos2} are on different chromosomes and
	 *                                  {@code genomePos} must not be right on {@code genomePos}.
	 */
	public SVDeletion(GenomePosition genomePos, GenomePosition genomePos2, int posCILowerBound, int posCIUpperBound,
					  int pos2CILowerBound, int pos2CIUpperBound) {
		super(genomePos, genomePos2, posCILowerBound, posCIUpperBound, pos2CILowerBound, pos2CIUpperBound);
		if (genomePos.getChr() != genomePos2.getChr()) {
			throw new IllegalArgumentException("genomePos and genomePos2 must be on the same strand");
		} else if (genomePos.isGt(genomePos2)) {
			throw new IllegalArgumentException("genomePos cannot be right of genomePos2");
		}
	}

	@Override
	public SVDescription.Type getType() {
		return SVDescription.Type.DEL;
	}

	@Override
	public SVDeletion withStrand(Strand strand) {
		if (genomePos.getStrand() != strand) {
			return new SVDeletion(
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
		return "SVDeletion{" +
			"genomePos=" + genomePos +
			", genomePos2=" + genomePos2 +
			", posCILowerBound=" + posCILowerBound +
			", posCIUpperBound=" + posCIUpperBound +
			", pos2CILowerBound=" + pos2CILowerBound +
			", pos2CIUpperBound=" + pos2CIUpperBound +
			'}';
	}

}
