package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.Immutable;

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

	/** mapping of name to PedPerson */
	final public ImmutableMap<String, PedPerson> nameToPerson; // TODO(holtgrew): Test this!

	public PedFileContents(ImmutableList<String> extraColumnHeaders, ImmutableList<PedPerson> individuals) {
		this.extraColumnHeaders = extraColumnHeaders;
		this.individuals = individuals;

		ImmutableMap.Builder<String, PedPerson> builder = new ImmutableMap.Builder<String, PedPerson>();
		for (PedPerson p : individuals)
			builder.put(p.name, p);
		this.nameToPerson = builder.build();
	}

}
