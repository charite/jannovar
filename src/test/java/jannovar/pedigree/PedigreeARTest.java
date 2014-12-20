package jannovar.pedigree;

import jannovar.common.Genotype;
import jannovar.exception.PedParseException;
import jannovar.exome.Variant;
import jannovar.genotype.GenotypeCall;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.HG19RefDictBuilder;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the following pedigree
 *
 * <pre>
 * ped1 father 0 0 1 1
 * ped1 mother 0 0 2 1
 * ped1 son1 father mother 1 2
 * ped1 son2 father mother 1 1
 * ped1 dau1 father mother 2 2
 * ped1 dau2 father mother 2 1
 * </pre>
 *
 * @author Peter Robinson <peter.robinson@charite.de>
 */
public class PedigreeARTest {
	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	static private Pedigree pedigree = null;

	@BeforeClass
	public static void setUp() throws IOException, PedParseException {
		PedFileParser parser = null;
		parser = new PedFileParser();
		java.net.URL url = PedigreeADTest.class.getResource("/TestPedigreeAR.ped");
		String path = url.getPath();
		pedigree = parser.parseFile(path);
	}

	@AfterClass
	public static void releaseResources() {
		pedigree = null;
		System.gc();
	}

	@Test
	public void testSizeOfPedigree() {
		int n = pedigree.getNumberOfIndividualsInPedigree();
		Assert.assertEquals(6, n);
	}

	private GenotypeCall constructGenotypeCall2(Genotype... calls) {
		ArrayList<Genotype> lst = new ArrayList<Genotype>();
		for (Genotype g : calls)
			lst.add(g);
		return new GenotypeCall(lst, null);
	}

	private Variant constructGenotypeCall(Genotype... calls) {
		float dummyPhred = 100f;
		ArrayList<Genotype> lst = new ArrayList<Genotype>();
		for (Genotype g : calls) {
			lst.add(g);
		}
		GenotypeCall gc = new GenotypeCall(lst, null);
		Variant v = new Variant(refDict, 1, 1, "A", "C", gc, dummyPhred, "");

		return v;
	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	 */
	@Test
	public void testARinheritance1() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/* mg3 is compatible with linkage. */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF);

		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
		Assert.assertEquals(true, b);

	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	 */
	@Test
	public void testARinheritance1homozygousOnly() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/* mg3 is compatible with linkage. */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF);

		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessiveHomozygous(lst);
		Assert.assertEquals(true, b);

	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants. Does not have a compound het
	 */
	@Test
	public void testARinheritance1CompoundHEt() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/* mg3 is compatible with linkage. */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF);

		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessiveCompoundHet(lst);
		Assert.assertEquals(false, b);

	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	 */
	@Test
	public void testARinheritance3() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/* mg3 is compatible with linkage */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF);
		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(true, b);
	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	 */
	@Test
	public void testARinheritance3HomozygousOnly() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/* mg3 is compatible with linkage */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF);
		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessiveHomozygous(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(true, b);
	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants. Does not have compound het
	 */
	@Test
	public void testARinheritance3CompoundHet() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/* mg3 is compatible with linkage */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT, Genotype.HOMOZYGOUS_REF);
		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessiveCompoundHet(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(false, b);
	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	 */
	@Test
	public void testARinheritance4() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/*
		 * mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET
		 */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF);
		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(false, b);
	}

	/**
	 * Test one compatible HOMOZYGOUS_ALT variant plus two irrelevant second variants.
	 */
	@Test
	public void testARinheritance4HomozygousOnly() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT);
		/*
		 * mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET
		 */
		Variant mg3 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_ALT,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF);
		lst.add(mg1);
		lst.add(mg2);
		lst.add(mg3);
		boolean b = pedigree.isCompatibleWithAutosomalRecessiveHomozygous(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(false, b);
	}

	/**
	 * Test the following pedigree
	 *
	 * <pre>
	 * ped1 father 0 0 1 1
	 * ped1 mother 0 0 2 1
	 * ped1 son1 father mother 1 2
	 * ped1 son2 father mother 1 1
	 * ped1 dau1 father mother 2 2
	 * ped1 dau2 father mother 2 1
	 * </pre>
	 *
	 * Here, mg1 is paternal het, mg2 is maternal het
	 */
	@Test
	public void testARinheritanceCompoundHet1() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF);
		/*
		 * mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET
		 */
		lst.add(mg1);
		lst.add(mg2);

		boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(true, b);
	}

	/**
	 * Test the following pedigree
	 *
	 * <pre>
	 * ped1 father 0 0 1 1
	 * ped1 mother 0 0 2 1
	 * ped1 son1 father mother 1 2
	 * ped1 son2 father mother 1 1
	 * ped1 dau1 father mother 2 2
	 * ped1 dau2 father mother 2 1
	 * </pre>
	 *
	 * Here, mg1 is paternal het, mg2 is maternal het
	 */
	@Test
	public void testARinheritanceCompoundHet1special() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF);
		/*
		 * mg3 is not compatible with linkage, only one affected is HOMOZYGOUS ALT, the other is HET
		 */
		lst.add(mg1);
		lst.add(mg2);

		boolean b = pedigree.isCompatibleWithAutosomalRecessiveCompoundHet(lst);
		// pedigree.debugPrint();
		Assert.assertEquals(true, b);
	}

	/**
	 * Test the following pedigree
	 *
	 * <pre>
	 * ped1 father 0 0 1 1
	 * ped1 mother 0 0 2 1
	 * ped1 son1 father mother 1 2
	 * ped1 son2 father mother 1 1
	 * ped1 dau1 father mother 2 2
	 * ped1 dau2 father mother 2 1
	 * </pre>
	 *
	 * Here, mg1 is paternal het, mg2 is maternal het, but one of the unaffecteds is compound het (dau2) Thus, the
	 * variant is not compatible!
	 */
	@Test
	public void testARinheritanceCompoundHet2() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		lst.add(mg1);
		lst.add(mg2);
		boolean b = pedigree.isCompatibleWithAutosomalRecessive(lst);
		Assert.assertEquals(false, b);
	}

	/**
	 * Test the following pedigree
	 *
	 * <pre>
	 * ped1 father 0 0 1 1
	 * ped1 mother 0 0 2 1
	 * ped1 son1 father mother 1 2
	 * ped1 son2 father mother 1 1
	 * ped1 dau1 father mother 2 2
	 * ped1 dau2 father mother 2 1
	 * </pre>
	 *
	 * Here, mg1 is paternal het, mg2 is maternal het, but one of the unaffecteds is compound het (dau2) Thus, the
	 * variant is not compatible!
	 */
	@Test
	public void testARinheritanceCompoundHet2special() {
		ArrayList<Variant> lst = new ArrayList<Variant>();
		Variant mg1 = constructGenotypeCall(Genotype.HETEROZYGOUS, Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS,
				Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		Variant mg2 = constructGenotypeCall(Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS,
				Genotype.HOMOZYGOUS_REF, Genotype.HETEROZYGOUS, Genotype.HETEROZYGOUS);
		lst.add(mg1);
		lst.add(mg2);
		boolean b = pedigree.isCompatibleWithAutosomalRecessiveCompoundHet(lst);
		Assert.assertEquals(false, b);
	}

}
