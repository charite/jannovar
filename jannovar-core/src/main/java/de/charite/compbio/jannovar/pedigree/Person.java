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
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class Person {

	/** the individual's name */
	private final String name;

	/** the individual's father, or <code>null</code> if father is not in pedigree */
	private final Person father;

	/** the individual's mother, or <code>null</code> if mother is not in pedigree */
	private final Person mother;

	/** the individual's sex */
	private final Sex sex;

	/** the individual's disease state */
	private final Disease disease;

	/** extra fields from the PED file */
	private final ImmutableList<String> extraFields;

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
		existing.put(pedPerson.getName(), this);

		this.name = pedPerson.getName();
		this.sex = pedPerson.getSex();
		this.disease = pedPerson.getDisease();
		this.extraFields = pedPerson.getExtraFields();

		// construct father and mother if necessary, construction will put them into existing
		if (!"0".equals(pedPerson.getFather()) && !existing.containsKey(pedPerson.getFather()))
			new Person(pedFileContents.getNameToPerson().get(pedPerson.getFather()), pedFileContents, existing);
		if (!"0".equals(pedPerson.getMother()) && !existing.containsKey(pedPerson.getMother()))
			new Person(pedFileContents.getNameToPerson().get(pedPerson.getMother()), pedFileContents, existing);

		this.father = existing.get(pedPerson.getFather());
		this.mother = existing.get(pedPerson.getMother());
	}

	/** @return the individual's name */
	public String getName() {
		return name;
	}

	/** @return the individual's father, or <code>null</code> if father is not in pedigree */
	public Person getFather() {
		return father;
	}

	/** @return the individual's mother, or <code>null</code> if mother is not in pedigree */
	public Person getMother() {
		return mother;
	}

	/** @return the individual's sex */
	public Sex getSex() {
		return sex;
	}

	/** @return the individual's disease state */
	public Disease getDisease() {
		return disease;
	}

	/** @return extra fields from the PED file */
	public ImmutableList<String> getExtraFields() {
		return extraFields;
	}

	/**
	 * @return <code>true</code> if the person is a founder (neither mother nor father in {@link Pedigree})
	 */
	public boolean isFounder() {
		return (father == null && mother == null);
	}

	/**
	 * @return <code>true</code> if the given person is male
	 */
	public boolean isMale() {
		return sex == Sex.MALE;
	}

	/**
	 * @return <code>true</code> if the given person is female
	 */
	public boolean isFemale() {
		return sex == Sex.FEMALE;
	}

	/**
	 * @return <code>true</code> if the given person is affected
	 */
	public boolean isAffected() {
		return disease == Disease.AFFECTED;
	}

	/**
	 * @return <code>true</code> if the given person is unaffected
	 */
	public boolean isUnaffected() {
		return disease == Disease.UNAFFECTED;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", father=" + father + ", mother=" + mother + ", sex=" + sex + ", disease="
				+ disease + ", extraFields=" + extraFields + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + ((extraFields == null) ? 0 : extraFields.hashCode());
		result = prime * result + ((father == null) ? 0 : father.hashCode());
		result = prime * result + ((mother == null) ? 0 : mother.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (disease != other.disease)
			return false;
		if (extraFields == null) {
			if (other.extraFields != null)
				return false;
		} else if (!extraFields.equals(other.extraFields))
			return false;
		if (father == null) {
			if (other.father != null)
				return false;
		} else if (!father.equals(other.father))
			return false;
		if (mother == null) {
			if (other.mother != null)
				return false;
		} else if (!mother.equals(other.mother))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sex != other.sex)
			return false;
		return true;
	}

}
