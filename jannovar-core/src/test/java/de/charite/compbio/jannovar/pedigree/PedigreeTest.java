package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;

/**
 * Unit tests for the {@link Pedigree} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class PedigreeTest {

	PedFileContents pedFileContents;

	@Before
	public void setUp() {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("fam", "father", "0", "0", Sex.MALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "mother", "0", "0", Sex.FEMALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "son", "father", "mother", Sex.MALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "daughter", "father", "mother", Sex.FEMALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam2", "other", "0", "0", Sex.FEMALE, Disease.UNKNOWN));
		this.pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(), individuals.build());
	}

	@Test
	public void testConstruction() throws PedParseException {
		Pedigree pedigree = new Pedigree(pedFileContents, "fam");

		Assert.assertEquals("fam", pedigree.getName());

		Assert.assertEquals(4, pedigree.getMembers().size());

		Assert.assertEquals("father", pedigree.getMembers().get(0).getName());
		Assert.assertEquals("mother", pedigree.getMembers().get(1).getName());
		Assert.assertEquals("son", pedigree.getMembers().get(2).getName());
		Assert.assertEquals("daughter", pedigree.getMembers().get(3).getName());

		Assert.assertSame(null, pedigree.getMembers().get(0).getFather());
		Assert.assertSame(null, pedigree.getMembers().get(1).getFather());
		Assert.assertSame(pedigree.getMembers().get(0), pedigree.getMembers().get(2).getFather());
		Assert.assertSame(pedigree.getMembers().get(0), pedigree.getMembers().get(3).getFather());

		Assert.assertSame(null, pedigree.getMembers().get(0).getMother());
		Assert.assertSame(null, pedigree.getMembers().get(1).getMother());
		Assert.assertSame(pedigree.getMembers().get(1), pedigree.getMembers().get(2).getMother());
		Assert.assertSame(pedigree.getMembers().get(1), pedigree.getMembers().get(3).getMother());

		Assert.assertEquals(Sex.MALE, pedigree.getMembers().get(0).getSex());
		Assert.assertEquals(Sex.FEMALE, pedigree.getMembers().get(1).getSex());
		Assert.assertEquals(Sex.MALE, pedigree.getMembers().get(2).getSex());
		Assert.assertEquals(Sex.FEMALE, pedigree.getMembers().get(3).getSex());

		Assert.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(0).getDisease());
		Assert.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(1).getDisease());
		Assert.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(2).getDisease());
		Assert.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(3).getDisease());

		Assert.assertEquals(0, pedigree.getMembers().get(0).getExtraFields().size());
		Assert.assertEquals(0, pedigree.getMembers().get(1).getExtraFields().size());
		Assert.assertEquals(0, pedigree.getMembers().get(2).getExtraFields().size());
		Assert.assertEquals(0, pedigree.getMembers().get(3).getExtraFields().size());
	}

}
