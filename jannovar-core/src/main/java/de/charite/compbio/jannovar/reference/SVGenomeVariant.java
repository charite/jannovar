package de.charite.compbio.jannovar.reference;

/**
 * Representation of a structural variant on the genome level as defined in VCF.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@
 */
public abstract class SVGenomeVariant implements SVDescription {

	/**
	 * Genomic start/first position.
	 */
	protected final GenomePosition genomePos;
	/**
	 * Genomic end/second position.
	 */
	protected final GenomePosition genomePos2;
	/**
	 * Lower bound of confidence interval around {@link #genomePos}.
	 */
	protected final int posCILowerBound;
	/**
	 * Upper bound of confidence interval around {@link #genomePos}.
	 */
	protected final int posCIUpperBound;
	/**
	 * Lower bound of confidence interval around {@link #genomePos2}.
	 */
	protected final int pos2CILowerBound;
	/**
	 * Upper bound of confidence interval around {@link #genomePos2}.
	 */
	protected final int pos2CIUpperBound;

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
	public SVGenomeVariant(GenomePosition genomePos, GenomePosition genomePos2, int posCILowerBound,
						   int posCIUpperBound, int pos2CILowerBound, int pos2CIUpperBound) {
		this.genomePos = genomePos;
		this.genomePos2 = genomePos2;
		this.posCILowerBound = posCILowerBound;
		this.posCIUpperBound = posCIUpperBound;
		this.pos2CILowerBound = pos2CILowerBound;
		this.pos2CIUpperBound = pos2CIUpperBound;
	}

	/**
	 * Return this variant projected to the given {@link Strand}, at least at the start position.
	 */
	public abstract SVGenomeVariant withStrand(Strand strand);

	/**
	 * Return genome interval (from mid points, ignoring confidence interval).
	 */
	public GenomeInterval getGenomeInterval() {
		if (genomePos.getChr() != genomePos2.getChr()) {
			throw new IllegalArgumentException("Cannot compute genome interval if start chrom is not end chrom");
		} else if (genomePos.isLeq(genomePos2)) {
			return new GenomeInterval(genomePos, genomePos2.differenceTo(genomePos));
		} else {
			return new GenomeInterval(genomePos2, genomePos.differenceTo(genomePos2));
		}
	}

	/**
	 * Return genomic start/first position.
	 */
	public GenomePosition getGenomePos() {
		return genomePos;
	}

	/**
	 * Return genomic end/second position.
	 */
	public GenomePosition getGenomePos2() {
		return genomePos2;
	}

	/**
	 * Return lower bound of confidence interval around {@link #genomePos}.
	 */
	public int getPosCILowerBound() {
		return posCILowerBound;
	}

	/**
	 * Return upper bound of confidence interval around {@link #genomePos}.
	 */
	public int getPosCIUpperBound() {
		return posCIUpperBound;
	}

	/**
	 * Return lower bound of confidence interval around {@link #genomePos2}.
	 */
	public int getPos2CILowerBound() {
		return pos2CILowerBound;
	}

	/**
	 * Return upper bound of confidence interval around {@link #genomePos2}.
	 */
	public int getPos2CIUpperBound() {
		return pos2CIUpperBound;
	}

	@Override
	public String getChrName() {
		return genomePos.getChrName();
	}

	@Override
	public int getChr() {
		return genomePos.getChr();
	}

	@Override
	public int getPos() {
		return genomePos.getPos();
	}

	@Override
	public String getChr2Name() {
		return genomePos.getChrName();
	}

	@Override
	public int getChr2() {
		return genomePos2.getChr();
	}

	@Override
	public int getPos2() {
		return genomePos2.getPos();
	}

}
