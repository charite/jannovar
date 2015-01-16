package de.charite.compbio.jannovar.pedigree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;

/**
 * An individual from a pedigree file, Java programmer friendly version.
 *
 * Note that a Person is not truly immutable since its mother and father field have to be non-final during its
 * construction. However, since the members only have <code>package</code> visibility, <code>Person</code> is immutable
 * for all practical considerations after construction.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class Person {

	/** the individual's name */
	public final String name;

	/** the individual's father, or <code>null</code> if father is not in pedigree */
	public final Person father;

	/** the individual's mother, or <code>null</code> if mother is not in pedigree */
	public final Person mother;

	/** the individual's sex */
	public final Sex sex;

	/** the individual's disease state */
	public final Disease disease;

	/** extra fields from the PED file */
	public final ImmutableList<String> extraFields;

	/**
	 * Initialize object with the given values.
	 */
	public Person(String name, Person father, Person mother, Sex sex, Disease disease, Collection<String> extraFields) {
		this.name = name;
		this.father = father;
		this.mother = mother;
		this.sex = sex;
		this.disease = disease;
		this.extraFields = ImmutableList.copyOf(extraFields);
	}

	/**
	 * Initialize object with the given values and empty extra fields list.
	 */
	public Person(String name, Person father, Person mother, Sex sex, Disease disease) {
		this(name, father, mother, sex, disease, new ArrayList<String>());
	}

	/**
	 * Constructor used by {@link PedigreeExtractor} for construction of pedigrees with potential cycles.
	 */
	Person(PedPerson pedPerson, PedFileContents pedFileContents, HashMap<String, Person> existing) {
		existing.put(pedPerson.name, this);

		this.name = pedPerson.name;
		this.sex = pedPerson.sex;
		this.disease = pedPerson.disease;
		this.extraFields = pedPerson.extraFields;

		// construct father and mother if necessary, construction will put them into existing
		if (!"0".equals(pedPerson.father) && !existing.containsKey(pedPerson.father))
			new Person(pedFileContents.nameToPerson.get(pedPerson.father), pedFileContents, existing);
		if (!"0".equals(pedPerson.mother) && !existing.containsKey(pedPerson.mother))
			new Person(pedFileContents.nameToPerson.get(pedPerson.mother), pedFileContents, existing);

		this.father = existing.get(pedPerson.father);
		this.mother = existing.get(pedPerson.mother);
	}

	/**
	 * @return <code>true</code> if the person is a founder (neither mother nor father in {@link Pedigree})
	 */
	public boolean isFounder() {
		return (father == null && mother == null);
	}

}
