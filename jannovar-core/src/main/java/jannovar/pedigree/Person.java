package jannovar.pedigree;

import jannovar.Immutable;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

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
	public Person father;

	/** the individual's mother, or <code>null</code> if mother is not in pedigree */
	public Person mother;

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

	public Person getFather() {
		return father;
	}

	public Person getMother() {
		return mother;
	}

	/**
	 * @return <code>true</code> if the person is a founder (neither mother nor father in {@link Pedigree})
	 */
	public boolean isFounder() {
		return (father == null && mother == null);
	}

}
