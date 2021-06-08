package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PedigreeQueryDecoratorTest {

	Pedigree pedigree;
	PedigreeQueryDecorator decorator;

	@BeforeEach
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("fam", "father", "0", "0", Sex.MALE, Disease.UNAFFECTED));
		individuals.add(new PedPerson("fam", "mother", "0", "0", Sex.FEMALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "son", "father", "mother", Sex.MALE, Disease.AFFECTED));
		individuals.add(new PedPerson("fam", "daughter", "father", "mother", Sex.FEMALE, Disease.UNKNOWN));
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
			individuals.build());

		this.pedigree = new Pedigree(pedFileContents, "fam");
		this.decorator = new PedigreeQueryDecorator(pedigree);
	}

	@Test
	public void testIsParentOfAffected() {
		Assertions.assertTrue(decorator.isParentOfAffected(pedigree.getMembers().get(0)));
		Assertions.assertTrue(decorator.isParentOfAffected(pedigree.getMembers().get(1)));
		Assertions.assertFalse(decorator.isParentOfAffected(pedigree.getMembers().get(2)));
		Assertions.assertFalse(decorator.isParentOfAffected(pedigree.getMembers().get(3)));
	}

	@Test
	public void testGetUnaffectedNames() {
		Assertions.assertEquals(ImmutableSet.of("father"), decorator.getUnaffectedNames());
	}

	@Test
	public void testGetParentNames() {
		Assertions.assertEquals(ImmutableSet.of("father", "mother"), decorator.getParentNames());
	}

	@Test
	public void testGetParents() {
		Assertions.assertEquals(ImmutableList.of(pedigree.getMembers().get(0), pedigree.getMembers().get(1)), decorator.getParents());
	}

	@Test
	public void testGetNumberOfParents() {
		Assertions.assertEquals(2, decorator.getNumberOfParents());
	}

	@Test
	public void testGetNumberOfAffecteds() {
		Assertions.assertEquals(1, decorator.getNumberOfAffecteds());
	}

	@Test
	public void testGetNumberOfUnaffecteds() {
		Assertions.assertEquals(1, decorator.getNumberOfUnaffecteds());
	}

}
