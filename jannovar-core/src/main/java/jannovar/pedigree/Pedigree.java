package jannovar.pedigree;

import jannovar.Immutable;

import com.google.common.collect.ImmutableList;

// TODO(holtgrem): Test me!
// TODO(holtgrem): Reordering of the pedigree members according to a list of individual names

/**
 * Represent one pedigree from a PED file.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class Pedigree {

	/** the pedigree's name */
	public final String name;

	/** the pedigree's members */
	public final ImmutableList<Person> members;

	/**
	 * Initialize the object with the given values
	 *
	 * @param name
	 *            the name of the pedigree/family
	 * @param members
	 *            list of the members
	 */
	public Pedigree(String name, ImmutableList<Person> members) {
		this.name = name;
		this.members = members;
	}

	/**
	 * Initialize the object with the members of <code>contents</code> that have the pedigree name equal to
	 * <code>pedigreeName</code>.
	 *
	 * @param contents
	 *            contents from the pedigree file
	 * @param pedigreeName
	 *            name of the pedigree to extract
	 * @throws PedParseException
	 *             in the case of problems with references to individuals for mother and father
	 */
	public Pedigree(PedFileContents contents, String pedigreeName) throws PedParseException {
		this.name = pedigreeName;
		this.members = new PedigreeExtractor(pedigreeName, contents).invoke();
	}

}
