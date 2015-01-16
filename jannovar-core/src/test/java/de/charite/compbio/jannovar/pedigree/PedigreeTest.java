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
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
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

		Assert.assertEquals("fam", pedigree.name);

		Assert.assertEquals(4, pedigree.members.size());

		Assert.assertEquals("father", pedigree.members.get(0).name);
		Assert.assertEquals("mother", pedigree.members.get(1).name);
		Assert.assertEquals("son", pedigree.members.get(2).name);
		Assert.assertEquals("daughter", pedigree.members.get(3).name);

		Assert.assertSame(null, pedigree.members.get(0).father);
		Assert.assertSame(null, pedigree.members.get(1).father);
		Assert.assertSame(pedigree.members.get(0), pedigree.members.get(2).father);
		Assert.assertSame(pedigree.members.get(0), pedigree.members.get(3).father);

		Assert.assertSame(null, pedigree.members.get(0).mother);
		Assert.assertSame(null, pedigree.members.get(1).mother);
		Assert.assertSame(pedigree.members.get(1), pedigree.members.get(2).mother);
		Assert.assertSame(pedigree.members.get(1), pedigree.members.get(3).mother);

		Assert.assertEquals(Sex.MALE, pedigree.members.get(0).sex);
		Assert.assertEquals(Sex.FEMALE, pedigree.members.get(1).sex);
		Assert.assertEquals(Sex.MALE, pedigree.members.get(2).sex);
		Assert.assertEquals(Sex.FEMALE, pedigree.members.get(3).sex);

		Assert.assertEquals(Disease.UNKNOWN, pedigree.members.get(0).disease);
		Assert.assertEquals(Disease.UNKNOWN, pedigree.members.get(1).disease);
		Assert.assertEquals(Disease.UNKNOWN, pedigree.members.get(2).disease);
		Assert.assertEquals(Disease.UNKNOWN, pedigree.members.get(3).disease);

		Assert.assertEquals(0, pedigree.members.get(0).extraFields.size());
		Assert.assertEquals(0, pedigree.members.get(1).extraFields.size());
		Assert.assertEquals(0, pedigree.members.get(2).extraFields.size());
		Assert.assertEquals(0, pedigree.members.get(3).extraFields.size());
	}

}
