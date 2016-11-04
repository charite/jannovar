package de.charite.compbio.jannovar.pedigree;

import java.util.HashSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.Immutable;

// TODO(holtgrem): Test me!

/**
 * Decorator of {@link Pedigree} that allows for the easy querying.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 */
@Immutable
public class PedigreeQueryDecorator {

	/** the pedigree */
	private final Pedigree pedigree;

	/**
	 * Initialize decorator.
	 */
	public PedigreeQueryDecorator(Pedigree pedigree) {
		this.pedigree = pedigree;
	}

	/** @return the decorated pedigree */
	public Pedigree getPedigree() {
		return pedigree;
	}

	/**
	 * @param person
	 *            the person to check
	 * @return <code>true</code> if the nth person in the PED file is parent of an affected child
	 */
	public boolean isParentOfAffected(Person person) {
		for (Person member : pedigree.getMembers())
			if (member.getFather() == person || member.getMother() == person)
				return true;
		return false;
	}

	/**
	 * @return set with the name of the unaffected persons
	 */
	public ImmutableSet<String> getUnaffectedNames() {
		ImmutableSet.Builder<String> resultNames = new ImmutableSet.Builder<String>();
		for (Person member : pedigree.getMembers())
			if (member.getDisease() == Disease.UNAFFECTED)
				resultNames.add(member.getName());
		return resultNames.build();
	}

	/**
	 * @return set with the name of the parents
	 */
	public ImmutableSet<String> getParentNames() {
		ImmutableSet.Builder<String> parentNames = new ImmutableSet.Builder<String>();
		for (Person member : pedigree.getMembers()) {
			if (member.getFather() != null)
				parentNames.add(member.getFather().getName());
			if (member.getMother() != null)
				parentNames.add(member.getMother().getName());
		}
		return parentNames.build();
	}

	/**
	 * @return set with the name of the parents from affected females.
	 */
	public ImmutableSet<String> getAffectedFemaleParentNames() {
		ImmutableSet.Builder<String> parentNames = new ImmutableSet.Builder<String>();
		for (Person member : pedigree.getMembers()) {
			if (member.isAffected() && member.isFemale()) {
				if (member.getFather() != null)
					parentNames.add(member.getFather().getName());
				if (member.getMother() != null)
					parentNames.add(member.getMother().getName());
			}
		}
		return parentNames.build();
	}

	/**
	 * @return set with the name of the parents from affected males.
	 */
	public ImmutableSet<String> getAffectedMaleParentNames() {
		ImmutableSet.Builder<String> parentNames = new ImmutableSet.Builder<String>();
		for (Person member : pedigree.getMembers()) {
			if (member.isAffected() && member.isMale()) {
				if (member.getFather() != null)
					parentNames.add(member.getFather().getName());
				if (member.getMother() != null)
					parentNames.add(member.getMother().getName());
			}
		}
		return parentNames.build();
	}

	/**
	 * @return list of parents in the same order as in {@link Pedigree#members pedigree.getMembers()}
	 */
	public ImmutableList<Person> getParents() {
		ImmutableSet<String> parentNames = getParentNames();

		ImmutableList.Builder<Person> builder = new ImmutableList.Builder<Person>();
		for (Person member : pedigree.getMembers())
			if (parentNames.contains(member.getName()))
				builder.add(member);
		return builder.build();
	}

	/**
	 * @return number of parents in pedigree
	 */
	public int getNumberOfParents() {
		HashSet<String> parentNames = new HashSet<String>();
		for (Person member : pedigree.getMembers()) {
			if (member.getFather() != null)
				parentNames.add(member.getFather().getName());
			if (member.getMother() != null)
				parentNames.add(member.getMother().getName());
		}
		return parentNames.size();
	}

	/**
	 * @return number of affected individuals in the pedigree
	 */
	public int getNumberOfAffecteds() {
		int result = 0;
		for (Person member : pedigree.getMembers())
			if (member.getDisease() == Disease.AFFECTED)
				result += 1;
		return result;
	}

	/**
	 * @return number of unaffected individuals in the pedigree
	 */
	public int getNumberOfUnaffecteds() {
		int result = 0;
		for (Person member : pedigree.getMembers())
			if (member.getDisease() == Disease.UNAFFECTED)
				result += 1;
		return result;
	}

	/**
	 * @return sibling map for each {@link Person} in {@link Pedigree}, both parents must be in {@link Pedigree} and the
	 *         same pedigree
	 */
	public ImmutableMap<Person, ImmutableList<Person>> buildSiblings() {
		ImmutableMap.Builder<Person, ImmutableList<Person>> mapBuilder = new ImmutableMap.Builder<Person, ImmutableList<Person>>();

		for (Person p1 : pedigree.getMembers()) {
			if (p1.getMother() == null || p1.getFather() == null)
				continue;
			ImmutableList.Builder<Person> listBuilder = new ImmutableList.Builder<Person>();
			for (Person p2 : pedigree.getMembers()) {
				if (p1.equals(p2) || !p1.getMother().equals(p2.getMother()) || !p1.getFather().equals(p2.getFather()))
					continue;
				listBuilder.add(p2);
			}
			mapBuilder.put(p1, listBuilder.build());
		}

		return mapBuilder.build();
	}

}
