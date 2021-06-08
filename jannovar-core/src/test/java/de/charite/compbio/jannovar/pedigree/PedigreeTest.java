package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Pedigree} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class PedigreeTest {

	PedFileContents pedFileContents;

	@BeforeEach
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

		Assertions.assertEquals("fam", pedigree.getName());

		Assertions.assertEquals(4, pedigree.getMembers().size());

		Assertions.assertEquals("father", pedigree.getMembers().get(0).getName());
		Assertions.assertEquals("mother", pedigree.getMembers().get(1).getName());
		Assertions.assertEquals("son", pedigree.getMembers().get(2).getName());
		Assertions.assertEquals("daughter", pedigree.getMembers().get(3).getName());

		Assertions.assertSame(null, pedigree.getMembers().get(0).getFather());
		Assertions.assertSame(null, pedigree.getMembers().get(1).getFather());
		Assertions.assertSame(pedigree.getMembers().get(0), pedigree.getMembers().get(2).getFather());
		Assertions.assertSame(pedigree.getMembers().get(0), pedigree.getMembers().get(3).getFather());

		Assertions.assertSame(null, pedigree.getMembers().get(0).getMother());
		Assertions.assertSame(null, pedigree.getMembers().get(1).getMother());
		Assertions.assertSame(pedigree.getMembers().get(1), pedigree.getMembers().get(2).getMother());
		Assertions.assertSame(pedigree.getMembers().get(1), pedigree.getMembers().get(3).getMother());

		Assertions.assertEquals(Sex.MALE, pedigree.getMembers().get(0).getSex());
		Assertions.assertEquals(Sex.FEMALE, pedigree.getMembers().get(1).getSex());
		Assertions.assertEquals(Sex.MALE, pedigree.getMembers().get(2).getSex());
		Assertions.assertEquals(Sex.FEMALE, pedigree.getMembers().get(3).getSex());

		Assertions.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(0).getDisease());
		Assertions.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(1).getDisease());
		Assertions.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(2).getDisease());
		Assertions.assertEquals(Disease.UNKNOWN, pedigree.getMembers().get(3).getDisease());

		Assertions.assertEquals(0, pedigree.getMembers().get(0).getExtraFields().size());
		Assertions.assertEquals(0, pedigree.getMembers().get(1).getExtraFields().size());
		Assertions.assertEquals(0, pedigree.getMembers().get(2).getExtraFields().size());
		Assertions.assertEquals(0, pedigree.getMembers().get(3).getExtraFields().size());
	}

}
