package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeQueryDecorator;
import de.charite.compbio.jannovar.pedigree.Sex;

public class PedigreeQueryDecoratorTest {

	Pedigree pedigree;
	PedigreeQueryDecorator decorator;

	@Before
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
		Assert.assertTrue(decorator.isParentOfAffected(pedigree.getMembers().get(0)));
		Assert.assertTrue(decorator.isParentOfAffected(pedigree.getMembers().get(1)));
		Assert.assertFalse(decorator.isParentOfAffected(pedigree.getMembers().get(2)));
		Assert.assertFalse(decorator.isParentOfAffected(pedigree.getMembers().get(3)));
	}

	@Test
	public void testGetUnaffectedNames() {
		Assert.assertEquals(ImmutableSet.of("father"), decorator.getUnaffectedNames());
	}

	@Test
	public void testGetParentNames() {
		Assert.assertEquals(ImmutableSet.of("father", "mother"), decorator.getParentNames());
	}

	@Test
	public void testGetParents() {
		Assert.assertEquals(ImmutableList.of(pedigree.getMembers().get(0), pedigree.getMembers().get(1)), decorator.getParents());
	}

	@Test
	public void testGetNumberOfParents() {
		Assert.assertEquals(2, decorator.getNumberOfParents());
	}

	@Test
	public void testGetNumberOfAffecteds() {
		Assert.assertEquals(1, decorator.getNumberOfAffecteds());
	}

	@Test
	public void testGetNumberOfUnaffecteds() {
		Assert.assertEquals(1, decorator.getNumberOfUnaffecteds());
	}

}
