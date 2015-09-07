package de.charite.compbio.jannovar.hgvs.protein.variant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;

public class ProteinChangeAlleleTest {

	private ProteinChangeAllele singletonAllele;
	private ProteinChangeAllele multiChangeAllele;

	@Before
	public void setUp() {
		singletonAllele = new ProteinChangeAllele(VariantConfiguration.IN_CIS, ImmutableList.of(ProteinSubstitution
				.build(false, "L", 100, "R")));
		multiChangeAllele = new ProteinChangeAllele(VariantConfiguration.CHIMERIC, ImmutableList.of(
				ProteinSubstitution.build(false, "L", 100, "R"), ProteinSubstitution.build(false, "A", 99, "G")));
	}

	@Test
	public void testSingletonStaticFactory() {
		Assert.assertEquals(singletonAllele,
				ProteinChangeAllele.singleChangeAllele(ProteinSubstitution.build(false, "L", 100, "R")));
	}

	@Test
	public void testMultiChangeStaticFactory() {
		Assert.assertEquals(multiChangeAllele, ProteinChangeAllele.build(VariantConfiguration.CHIMERIC,
				ProteinSubstitution.build(false, "L", 100, "R"), ProteinSubstitution.build(false, "A", 99, "G")));
	}

	@Test
	public void testSingletonToHGVSString() {
		Assert.assertEquals("[Leu101Arg]", singletonAllele.toHGVSString());
		Assert.assertEquals("[Leu101Arg]", singletonAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("[L101R]", singletonAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testMultVariantToHGVSString() {
		Assert.assertEquals("[Leu101Arg//Ala100Gly]", multiChangeAllele.toHGVSString());
		Assert.assertEquals("[Leu101Arg//Ala100Gly]", multiChangeAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("[L101R//A100G]", multiChangeAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
