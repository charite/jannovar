package jannovar.pedigree;

import jannovar.Immutable;

import java.util.HashSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

// TODO(holtgrem): Test me!

/**
 * Decorator of {@link Pedigree} that allows for the easy querying.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Immutable
public class PedigreeQueryDecorator {

	/** the pedigree */
	public final Pedigree pedigree;

	/**
	 * Initialize decorator.
	 */
	public PedigreeQueryDecorator(Pedigree pedigree) {
		this.pedigree = pedigree;
	}

	/**
	 * @param person
	 *            the person to check
	 * @return <code>true</code> if the nth person in the PED file is parent of an affected child
	 */
	public boolean isParentOfAffected(Person person) {
		for (Person member : pedigree.members)
			if (member.father == person || member.mother == person)
				return true;
		return false;
	}

	/**
	 * @return set with the name of the unaffected persons
	 */
	public ImmutableSet<String> getUnaffectedNames() {
		ImmutableSet.Builder<String> resultNames = new ImmutableSet.Builder<String>();
		for (Person member : pedigree.members)
			if (member.disease == Disease.UNAFFECTED)
				resultNames.add(member.name);
		return resultNames.build();
	}

	/**
	 * @return set with the name of the parents
	 */
	public ImmutableSet<String> getParentNames() {
		ImmutableSet.Builder<String> parentNames = new ImmutableSet.Builder<String>();
		for (Person member : pedigree.members) {
			if (member.father != null)
				parentNames.add(member.father.name);
			if (member.mother != null)
				parentNames.add(member.mother.name);
		}
		return parentNames.build();
	}

	/**
	 * @return list of parents in the same order as in {@link Pedigree#members pedigree.members}
	 */
	public ImmutableList<Person> getParents() {
		ImmutableSet<String> parentNames = getParentNames();

		ImmutableList.Builder<Person> builder = new ImmutableList.Builder<Person>();
		for (Person member : pedigree.members)
			if (parentNames.contains(member.name))
				builder.add(member);
		return builder.build();
	}

	/**
	 * @return number of parents in pedigree
	 */
	public int getNumberOfParents() {
		HashSet<String> parentNames = new HashSet<String>();
		for (Person member : pedigree.members) {
			if (member.father != null)
				parentNames.add(member.father.name);
			if (member.mother != null)
				parentNames.add(member.mother.name);
		}
		return parentNames.size();
	}

	/**
	 * @return number of affected individuals in the pedigree
	 */
	public int getNumberOfAffecteds() {
		int result = 0;
		for (Person member : pedigree.members)
			if (member.disease == Disease.AFFECTED)
				result += 1;
		return result;
	}

	/**
	 * @return number of unaffected individuals in the pedigree
	 */
	public int getNumberOfUnaffecteds() {
		int result = 0;
		for (Person member : pedigree.members)
			if (member.disease == Disease.UNAFFECTED)
				result += 1;
		return result;
	}

}
