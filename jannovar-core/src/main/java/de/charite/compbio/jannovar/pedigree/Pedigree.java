package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.Immutable;

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
	private final String name;

	/** the pedigree's members */
	private final ImmutableList<Person> members;

	/** mapping from member name to member */
	private final ImmutableMap<String, IndexedPerson> nameToMember;

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
			mapBuilder.put(person.getName(), new IndexedPerson(i++, person));
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

	/** @return the pedigree's name */
	public String getName() {
		return name;
	}

	/** @return the pedigree's members */
	public ImmutableList<Person> getMembers() {
		return members;
	}

	/** @return mapping from member name to member */
	public ImmutableMap<String, IndexedPerson> getNameToMember() {
		return nameToMember;
	}

	/**
	 * @return a pedigree with one affected sample
	 */
	public static Pedigree constructSingleSamplePedigree(String sampleName) {
		final Person person = new Person(sampleName, null, null, Sex.UNKNOWN, Disease.AFFECTED);
		return new Pedigree("pedigree", ImmutableList.of(person));
	}

	/**
	 * @return <code>true</code> if the pedigree contains a sample with the given <code>name</code>.
	 */
	public boolean hasPerson(String name) {
		return nameToMember.containsKey(name);
	}

	/**
	 * @return list of members, in the same order as in {@link #members}.
	 */
	public ImmutableList<String> getNames() {
		ImmutableList.Builder<String> builder = new ImmutableList.Builder<String>();
		for (Person p : members)
			builder.add(p.getName());
		return builder.build();
	}

	@Override
	public String toString() {
		return "Pedigree [name=" + name + ", members=" + members + ", nameToMember=" + nameToMember + "]";
	}

	public static class IndexedPerson {
		private final int idx;
		private final Person person;

		public IndexedPerson(int idx, Person person) {
			this.idx = idx;
			this.person = person;
		}

		public int getIdx() {
			return idx;
		}

		public Person getPerson() {
			return person;
		}
	}

}
