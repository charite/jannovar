package jannovar.pedigree;

import jannovar.reference.TranscriptInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class GenotypeListTest {

	TranscriptInfo transcript;
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
		this.transcript = null;
		this.names = ImmutableList.of("father", "mother", "son", "daughter");
		this.calls = null;
		this.list = new GenotypeList(transcript, names, calls);
	}

	@Test
	public void testIsNamesEqual() {
		Assert.assertTrue(list.isNamesEqual(pedigree1));
		Assert.assertFalse(list.isNamesEqual(pedigree2));
	}

}
