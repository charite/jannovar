package de.charite.compbio.jannovar.pedigree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import htsjdk.variant.variantcontext.VariantContext;

public class InheritanceCompatibilityCheckerTest extends AbstractCompatibilityCheckerTest {

	private Set<ModeOfInheritance> adSet;
	private Set<ModeOfInheritance> arSet;
	private Set<ModeOfInheritance> xdSet;
	private Set<ModeOfInheritance> xrSet;
	private Set<ModeOfInheritance> adArSet;
	private Set<ModeOfInheritance> xdXrSet;

	@Before
	public void setUp() throws PedParseException {
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // father
		individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // mother
		individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // son
		individuals.add(new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.UNAFFECTED)); // daughter
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		this.pedigree = new Pedigree(pedFileContents, "ped");

		this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2");

		adSet = new HashSet<ModeOfInheritance>(Arrays.asList(ModeOfInheritance.AUTOSOMAL_DOMINANT));
		arSet = new HashSet<ModeOfInheritance>(Arrays.asList(ModeOfInheritance.AUTOSOMAL_RECESSIVE));
		xdSet = new HashSet<ModeOfInheritance>(Arrays.asList(ModeOfInheritance.X_DOMINANT));
		xrSet = new HashSet<ModeOfInheritance>(Arrays.asList(ModeOfInheritance.X_RECESSIVE));
		adArSet = new HashSet<ModeOfInheritance>(
				Arrays.asList(ModeOfInheritance.AUTOSOMAL_DOMINANT, ModeOfInheritance.AUTOSOMAL_RECESSIVE));
		xdXrSet = new HashSet<ModeOfInheritance>(
				Arrays.asList(ModeOfInheritance.X_DOMINANT, ModeOfInheritance.X_RECESSIVE));

	}

	@Test
	public void checkADTest() throws InheritanceCompatibilityCheckerException {
		InheritanceCompatibilityChecker checker = buildInheritanceCompatibilityChecker(
				ImmutableSet.of(ModeOfInheritance.AUTOSOMAL_DOMINANT));
		List<VariantContext> vcList = getInheritanceVariantContextList(lst(HET, HET, HET, HET), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(REF, REF, REF, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(ALT, ALT, ALT, ALT), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(UKN, UKN, UKN, UKN), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(HET, REF, HET, HET), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(HET, HET, HET, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(HET, REF, REF, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(REF, REF, HET, ALT), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		vcList = getInheritanceVariantContextList(lst(REF, REF, HET, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertEquals(adSet, value);
		}
		vcList = getInheritanceVariantContextList(lst(REF, REF, HET, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertEquals(adSet, value);
		}
		vcList = getInheritanceVariantContextList(lst(REF, UKN, HET, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertEquals(adSet, value);
		}
		vcList = getInheritanceVariantContextList(lst(UKN, UKN, HET, UKN), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertEquals(adSet, value);
		}

	}

	@Test
	public void checkADARTest() throws InheritanceCompatibilityCheckerException {
		InheritanceCompatibilityChecker checker = buildInheritanceCompatibilityChecker(
				ImmutableSet.of(ModeOfInheritance.AUTOSOMAL_DOMINANT, ModeOfInheritance.AUTOSOMAL_RECESSIVE));
		List<VariantContext> vcList = getInheritanceVariantContextList(lst(REF, HET, HET, UKN), lst(HET, REF, HET, REF),
				false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertEquals(arSet, value);
		}
		vcList = getInheritanceVariantContextList(lst(HET, HET, ALT, UKN), lst(REF, REF, HET, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.equals(arSet) || value.equals(adSet));
		}
		
		vcList = getInheritanceVariantContextList(lst(HET, HET, ALT, UKN), lst(REF, REF, HET, REF), true);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty());
		}
		
		vcList = getInheritanceVariantContextList(lst(HET, REF, ALT, UKN), lst(REF, REF, HET, REF), false);
		for (Set<ModeOfInheritance> value : checker.check(vcList).values()) {
			Assert.assertTrue(value.isEmpty() || value.equals(adSet));
		}
	}

	@Test
	public void getAllCompatibleModesTest() throws InheritanceCompatibilityCheckerException {
		InheritanceCompatibilityChecker checker = buildInheritanceCompatibilityChecker(adSet);
		List<VariantContext> vcList = getInheritanceVariantContextList(lst(HET, HET, HET, HET), false);
		Assert.assertTrue(checker.getAllCompatibleModes(vcList).isEmpty());
		vcList = getInheritanceVariantContextList(lst(UKN, UKN, UKN, UKN), false);
		Assert.assertTrue(checker.getAllCompatibleModes(vcList).isEmpty());
		vcList = getInheritanceVariantContextList(lst(UKN, UKN, HET, UKN), false);
		Assert.assertEquals(adSet, checker.getAllCompatibleModes(vcList));
		vcList = getInheritanceVariantContextList(lst(UKN, UKN, ALT, UKN), false);
		Assert.assertEquals(arSet, checker.getAllCompatibleModes(vcList));
		vcList = getInheritanceVariantContextList(lst(UKN, UKN, ALT, UKN), lst(HET, HET, ALT, REF), false);
		Assert.assertEquals(arSet, checker.getAllCompatibleModes(vcList));
		vcList = getInheritanceVariantContextList(lst(REF, HET, HET, UKN), lst(HET, REF, HET, REF), false);
		Assert.assertEquals(arSet, checker.getAllCompatibleModes(vcList));
		vcList = getInheritanceVariantContextList(lst(HET, HET, ALT, HET), lst(REF, REF, HET, REF), false);
		Assert.assertEquals(adArSet, checker.getAllCompatibleModes(vcList));

		vcList = getInheritanceVariantContextList(lst(UKN, HET, ALT, HET), lst(REF, REF, ALT, REF), true);
		Assert.assertEquals(xdXrSet, checker.getAllCompatibleModes(vcList));
		vcList = getInheritanceVariantContextList(lst(UKN, HET, ALT, UKN), true);
		Assert.assertEquals(xrSet, checker.getAllCompatibleModes(vcList));
	}

}
