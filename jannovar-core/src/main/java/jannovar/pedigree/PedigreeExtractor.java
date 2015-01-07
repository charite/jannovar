package jannovar.pedigree;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

// TODO(holtgrew): Test me!

/**
 * Helper class for extracting one family from a {@link PedFileContents} object.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class PedigreeExtractor {

	/** name of the pedigree to extract */
	private final String name;

	/** pedigree file contents to work with */
	private final PedFileContents contents;

	public PedigreeExtractor(String name, PedFileContents contents) {
		this.name = name;
		this.contents = contents;
	}

	/**
	 * Invoke extraction of the given pedigree from <code>contents</code> used in construction.
	 *
	 * @return list of {@link Person}s of the given pedigree
	 * @throws PedParseException
	 *             on problems with resolving names of individuals
	 */
	public ImmutableList<Person> run() throws PedParseException {
		ImmutableList.Builder<Person> builder = new ImmutableList.Builder<Person>();

		// build map from person name to person in PED file and in result
		HashMap<String, PedPerson> pedPersonMap = new HashMap<String, PedPerson>();
		HashMap<String, Person> personMap = new HashMap<String, Person>();
		for (PedPerson person : contents.individuals)
			if (person.pedigree.equals(name)) {
				pedPersonMap.put(person.name, person);
				personMap.put(person.name, new Person(person.name, null, null, person.sex, person.disease,
						person.extraFields));
			}

		for (Map.Entry<String, PedPerson> pedEntry : pedPersonMap.entrySet()) {
			Person person = personMap.get(pedEntry.getKey());
			PedPerson pedPerson = pedEntry.getValue();

			if (pedPerson.father.equals("0"))
				person.father = null;
			else if (!pedPersonMap.containsKey(pedPerson.father))
				throw new PedParseException("Unknown individual identifier for father: " + pedPerson.father);
			else
				person.father = personMap.get(pedPerson.father);

			if (pedPerson.mother.equals("0"))
				person.mother = null;
			else if (!pedPersonMap.containsKey(pedPerson.mother))
				throw new PedParseException("Unknown individual identifier for mother: " + pedPerson.mother);
			else
				person.mother = personMap.get(pedPerson.mother);
		}

		return builder.build();
	}
}
