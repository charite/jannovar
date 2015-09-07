package de.charite.compbio.jannovar.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.factories.TestJannovarDataFactory;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class GeneWiseInheritanceFilterTest {
	private Pedigree pedigreeAffectedChild;
	private Pedigree pedigreeAffectedTwoAffected;
	private JannovarData jannovarDB;
	private List<VariantContext> variants;
	final Path inheritanceFilterVCFPath = Paths.get("src/test/resources/inheritanceFilterTest.vcf");
	private VCFFileReader reader;

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "Eva", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // Mother
		individuals.add(new PedPerson("ped", "Adam", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // Father
		individuals.add(new PedPerson("ped", "Seth", "Adam", "Eva", Sex.MALE, Disease.AFFECTED)); // Child
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		pedigreeAffectedChild = new Pedigree(pedFileContents, "ped");

		individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "Eva", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // Mother
		individuals.add(new PedPerson("ped", "Adam", "0", "0", Sex.MALE, Disease.AFFECTED)); // Father
		individuals.add(new PedPerson("ped", "Seth", "Adam", "Eva", Sex.MALE, Disease.AFFECTED)); // Child
		pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(), individuals.build());
		pedigreeAffectedTwoAffected = new Pedigree(pedFileContents, "ped");

		jannovarDB = new TestJannovarDataFactory().getJannovarData();

		reader = new VCFFileReader(inheritanceFilterVCFPath.toFile(),false);
		variants = new ArrayList<VariantContext>();
		for (VariantContext variantContext : reader) {
			variants.add(variantContext);
		}

	}

	@Test
	public void pedigreeAffectedChildADTest() {
		WriterFilterHelper helper = new WriterFilterHelper();
		GeneWiseInheritanceFilter filter = new GeneWiseInheritanceFilter(pedigreeAffectedChild, jannovarDB,
				ImmutableSet.of(ModeOfInheritance.AUTOSOMAL_DOMINANT), helper);
		List<FlaggedVariant> flaggedVariants = getFlaggedVariants();
		try {
			for (FlaggedVariant fv : flaggedVariants) {
				filter.put(fv);
			}
			filter.finish();
		} catch (FilterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<VariantContext> vars = helper.getVariants();
		
		Assert.assertTrue(!vars.isEmpty());
		Assert.assertTrue(vars.size() == 1);
		Assert.assertEquals(vars.iterator().next().getStart(), 145515898);

	}
	
	@Test
	public void pedigreeAffectedChildARTest() {
		WriterFilterHelper helper = new WriterFilterHelper();
		GeneWiseInheritanceFilter filter = new GeneWiseInheritanceFilter(pedigreeAffectedChild, jannovarDB,
				ImmutableSet.of(ModeOfInheritance.AUTOSOMAL_RECESSIVE), helper);
		List<FlaggedVariant> flaggedVariants = getFlaggedVariants();
		try {
			for (FlaggedVariant fv : flaggedVariants) {
				filter.put(fv);
			}
			filter.finish();
		} catch (FilterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<VariantContext> vars = helper.getVariants();
		
		Assert.assertTrue(!vars.isEmpty());
		Assert.assertTrue(vars.size() == 3);
		Iterator<VariantContext> vcIt = vars.iterator();
		Assert.assertEquals(vcIt.next().getStart(), 145513532);
		Assert.assertEquals(vcIt.next().getStart(), 145513534);
		Assert.assertEquals(vcIt.next().getStart(), 123239370);

	}
	
	@Test
	public void pedigreeTwoAffectedADTest() {
		WriterFilterHelper helper = new WriterFilterHelper();
		GeneWiseInheritanceFilter filter = new GeneWiseInheritanceFilter(pedigreeAffectedTwoAffected, jannovarDB,
				ImmutableSet.of(ModeOfInheritance.AUTOSOMAL_DOMINANT), helper);
		List<FlaggedVariant> flaggedVariants = getFlaggedVariants();
		try {
			for (FlaggedVariant fv : flaggedVariants) {
				filter.put(fv);
			}
			filter.finish();
		} catch (FilterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<VariantContext> vars = helper.getVariants();
		
		Assert.assertTrue(!vars.isEmpty());
		Assert.assertTrue(vars.size() == 1);
		Assert.assertEquals(vars.iterator().next().getStart(), 145513532);

	}
	
	@Test
	public void pedigreeTwoAffectedARTest() {
		WriterFilterHelper helper = new WriterFilterHelper();
		GeneWiseInheritanceFilter filter = new GeneWiseInheritanceFilter(pedigreeAffectedTwoAffected, jannovarDB,
				ImmutableSet.of(ModeOfInheritance.AUTOSOMAL_RECESSIVE), helper);
		List<FlaggedVariant> flaggedVariants = getFlaggedVariants();
		try {
			for (FlaggedVariant fv : flaggedVariants) {
				filter.put(fv);
			}
			filter.finish();
		} catch (FilterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<VariantContext> vars = helper.getVariants();
		
		Assert.assertTrue(!vars.isEmpty());
		Assert.assertTrue(vars.size() == 1);
		Iterator<VariantContext> vcIt = vars.iterator();
		Assert.assertEquals(vcIt.next().getStart(), 123357972);

	}

	private List<FlaggedVariant> getFlaggedVariants() {
		List<FlaggedVariant> output = new ArrayList<FlaggedVariant>();
		for (VariantContext vc : variants) {
			output.add(new FlaggedVariant(vc));
		}
		return output;
	}

}
