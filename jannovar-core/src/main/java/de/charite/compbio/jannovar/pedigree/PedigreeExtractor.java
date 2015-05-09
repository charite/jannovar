package de.charite.compbio.jannovar.pedigree;

import java.util.HashMap;

import com.google.common.collect.ImmutableList;

// TODO(holtgrew): Test me!
// TODO(holtgrew): Convenience class for parsing Pedigree files?

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
		for (PedPerson pedPerson : contents.getIndividuals()) {
			if (!"0".equals(pedPerson.getFather()) && !contents.getNameToPerson().containsKey(pedPerson.getFather()))
				throw new PedParseException("Unknown individual identifier for father: " + pedPerson.getFather());
			if (!"0".equals(pedPerson.getMother()) && !contents.getNameToPerson().containsKey(pedPerson.getMother()))
				throw new PedParseException("Unknown individual identifier for mother: " + pedPerson.getMother());
		}

		// construct all Person objects, we use a trick for the construction of immutable Person objects while still
		// allowing potential cycles
		ImmutableList.Builder<Person> builder = new ImmutableList.Builder<Person>();
		HashMap<String, Person> existing = new HashMap<String, Person>();
		for (PedPerson pedPerson : contents.getIndividuals())
			if (pedPerson.getPedigree().equals(name)) {
				if (existing.containsKey(pedPerson.getName()))
					builder.add(existing.get(pedPerson.getName()));
				else
					builder.add(new Person(pedPerson, contents, existing));
			}

		return builder.build();
	}
}
