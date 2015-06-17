package de.charite.compbio.jannovar.hgvs.nts.variant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;

public class NucleotideChangeAlleleTest {

	private NucleotideChangeAllele singletonAllele;
	private NucleotideChangeAllele multiChangeAllele;

	@Before
	public void setUp() {
		singletonAllele = new NucleotideChangeAllele(VariantConfiguration.IN_CIS,
				ImmutableList.of(NucleotideSubstitution.build(false, 100, "C", "A")));
		multiChangeAllele = new NucleotideChangeAllele(VariantConfiguration.CHIMERIC, ImmutableList.of(
				NucleotideSubstitution.build(false, 100, "C", "A"),
				NucleotideSubstitution.build(false, 99, "C", "A")));
	}

	@Test
	public void testSingletonStaticFactory() {
		Assert.assertEquals(singletonAllele, NucleotideChangeAllele.singleChangeAllele(NucleotideSubstitution
				.build(false, 100, "C", "A")));
	}

	@Test
	public void testMultiChangeStaticFactory() {
		Assert.assertEquals(
				multiChangeAllele,
				NucleotideChangeAllele.build(VariantConfiguration.CHIMERIC,
						NucleotideSubstitution.build(false, 100, "C", "A"),
						NucleotideSubstitution.build(false, 99, "C", "A")));
	}

	@Test
	public void testSingletonToHGVSString() {
		Assert.assertEquals("[101C>A]", singletonAllele.toHGVSString());
		Assert.assertEquals("[101C>A]", singletonAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("[101C>A]", singletonAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testMultVariantToHGVSString() {
		Assert.assertEquals("[101C>A//100C>A]", multiChangeAllele.toHGVSString());
		Assert.assertEquals("[101C>A//100C>A]", multiChangeAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("[101C>A//100C>A]", multiChangeAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
