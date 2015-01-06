package jannovar.pedigree;

import jannovar.Immutable;

import com.google.common.collect.ImmutableList;

/**
 * Represents the contents of a pedigree file.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public class PedFileContents {

	/** headers for extra columns (column 7 and beyond) */
	final public ImmutableList<String> extraColumnHeaders;

	/** the individuals in the PED file */
	final public ImmutableList<PedPerson> individuals;

	public PedFileContents(ImmutableList<String> extraColumnHeaders, ImmutableList<PedPerson> individuals) {
		this.extraColumnHeaders = extraColumnHeaders;
		this.individuals = individuals;
	}

}
