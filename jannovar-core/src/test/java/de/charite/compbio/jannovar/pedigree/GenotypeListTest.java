package de.charite.compbio.jannovar.pedigree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.reference.GenomeInterval;

public class GenotypeListTest {

	String geneName;
	GenomeInterval genomeRegion;
	ImmutableList<String> names;
	ImmutableList<ImmutableList<Genotype>> calls;
	Pedigree pedigree1, pedigree2;
	/** the {@link GenotypeList} under test */
	GenotypeList list;

	@Before
	public void setUp() throws PedParseException {

		// create pedigrees (with 4 and 5 people)
		ImmutableList.Builder<PedPerson> personList = new ImmutableList.Builder<PedPerson>();
		personList.add(new PedPerson("FAM", "father", "0", "0", Sex.MALE, Disease.UNKNOWN));
		personList.add(new PedPerson("FAM", "mother", "0", "0", Sex.MALE, Disease.UNKNOWN));
		personList.add(new PedPerson("FAM", "son", "father", "mother", Sex.MALE, Disease.UNKNOWN));
		personList.add(new PedPerson("FAM", "daughter", "father", "mother", Sex.FEMALE, Disease.UNKNOWN));
		this.pedigree1 = new Pedigree(new PedFileContents(new ImmutableList.Builder<String>().build(),
				personList.build()), "FAM");
		personList.add(new PedPerson("FAM", "third", "father", "mother", Sex.FEMALE, Disease.UNKNOWN));
		this.pedigree2 = new Pedigree(new PedFileContents(new ImmutableList.Builder<String>().build(),
				personList.build()), "FAM");

		// create GenotypeList
		this.geneName = null;
		this.genomeRegion = null;
		this.names = ImmutableList.of("father", "mother", "son", "daughter");
		this.calls = null;
		this.list = new GenotypeList(geneName, genomeRegion, names, calls);
	}

	@Test
	public void testIsNamesEqual() {
		Assert.assertTrue(list.namesEqual(pedigree1));
		Assert.assertFalse(list.namesEqual(pedigree2));
	}

}
