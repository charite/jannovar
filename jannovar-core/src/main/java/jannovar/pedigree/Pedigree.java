package jannovar.pedigree;

import jannovar.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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

	/** mapping from member name to member */
	public final ImmutableMap<String, IndexedPerson> nameToMember;

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

		ImmutableMap.Builder<String, IndexedPerson> mapBuilder = new ImmutableMap.Builder<String, IndexedPerson>();
		int i = 0;
		for (Person person : members)
			mapBuilder.put(person.name, new IndexedPerson(i++, person));
		this.nameToMember = mapBuilder.build();
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
		this(pedigreeName, new PedigreeExtractor(pedigreeName, contents).run());
	}

	public static class IndexedPerson {
		public final int idx;
		public final Person person;

		public IndexedPerson(int idx, Person person) {
			this.idx = idx;
			this.person = person;
		}
	}

}
