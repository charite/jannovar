package de.charite.compbio.jannovar.pedigree;

import java.util.HashMap;

import com.google.common.collect.ImmutableList;

// TODO(holtgrew): Test me!

/**
 * Helper class for extracting one family from a {@link PedFileContents} object.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class PedigreeExtractor {

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
		// check that all linked-to mothers and fathers exist
		for (PedPerson pedPerson : contents.individuals) {
			if (!"0".equals(pedPerson.father) && !contents.nameToPerson.containsKey(pedPerson.father))
				throw new PedParseException("Unknown individual identifier for father: " + pedPerson.father);
			if (!"0".equals(pedPerson.mother) && !contents.nameToPerson.containsKey(pedPerson.mother))
				throw new PedParseException("Unknown individual identifier for mother: " + pedPerson.mother);
		}

		// construct all Person objects, we use a trick for the construction of immutable Person objects while still
		// allowing potential cycles
		ImmutableList.Builder<Person> builder = new ImmutableList.Builder<Person>();
		HashMap<String, Person> existing = new HashMap<String, Person>();
		for (PedPerson pedPerson : contents.individuals)
			if (pedPerson.pedigree.equals(name)) {
				if (existing.containsKey(pedPerson.name))
					builder.add(existing.get(pedPerson.name));
				else
					builder.add(new Person(pedPerson, contents, existing));
			}

		return builder.build();
	}
}
