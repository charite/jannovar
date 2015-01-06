package jannovar.pedigree;

/**
 * Helper class for checking a {@link GenotypeList} for compatibility with a {@link Pedigree} and autosomal recessive
 * compound mode of inheritance.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
class CompatibilityCheckerAusosomalRecessiveCompoundHet {

	/** the pedigree to use for the checking */
	public final Pedigree pedigree;

	/** the genotype call list to use for the checking */
	public final GenotypeList list;

	public CompatibilityCheckerAusosomalRecessiveCompoundHet(Pedigree pedigree, GenotypeList list) {
		this.pedigree = pedigree;
		this.list = list;
	}

	public boolean run() {
		// TODO Auto-generated method stub
		return false;
	}

}
