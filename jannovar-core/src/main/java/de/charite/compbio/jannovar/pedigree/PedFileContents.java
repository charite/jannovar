package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.Immutable;

/**
 * Represents the contents of a pedigree file.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public class PedFileContents {

	/** headers for extra columns (column 7 and beyond) */
	private final ImmutableList<String> extraColumnHeaders;

	/** the individuals in the PED file */
	private final ImmutableList<PedPerson> individuals;

	/** mapping of name to PedPerson */
	private final ImmutableMap<String, PedPerson> nameToPerson; // TODO(holtgrew): Test this!

	public PedFileContents(ImmutableList<String> extraColumnHeaders, ImmutableList<PedPerson> individuals) {
		this.extraColumnHeaders = extraColumnHeaders;
		this.individuals = individuals;

		ImmutableMap.Builder<String, PedPerson> builder = new ImmutableMap.Builder<String, PedPerson>();
		for (PedPerson p : individuals)
			builder.put(p.getName(), p);
		this.nameToPerson = builder.build();
	}

	/** @return headers for extra columns (column 7 and beyond) */
	public ImmutableList<String> getExtraColumnHeaders() {
		return extraColumnHeaders;
	}

	/** @return the individuals in the PED file */
	public ImmutableList<PedPerson> getIndividuals() {
		return individuals;
	}

	/** @return mapping of name to PedPerson */
	public ImmutableMap<String, PedPerson> getNameToPerson() {
		return nameToPerson;
	}

}
