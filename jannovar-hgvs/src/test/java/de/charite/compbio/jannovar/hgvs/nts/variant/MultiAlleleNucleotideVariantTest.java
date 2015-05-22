package de.charite.compbio.jannovar.hgvs.nts.variant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;

public class MultiAlleleNucleotideVariantTest {

	private MultiAlleleNucleotideVariant singleAlleleVariant;
	private MultiAlleleNucleotideVariant multiAlleleVariant;
	private NucleotideChangeAllele firstAllele;
	private NucleotideChangeAllele secondAllele;

	@Before
	public void setUp() throws Exception {
		firstAllele = NucleotideChangeAllele.build(VariantConfiguration.IN_CIS,
				NucleotideSubstitution.build(false, 99, "A", "G"),
				NucleotideSubstitution.build(false, 300, "A", "C"));
		secondAllele = NucleotideChangeAllele.build(VariantConfiguration.IN_CIS,
				NucleotideSubstitution.build(false, 33, "T", "C"),
				NucleotideSubstitution.build(false, 300, "A", "G"));

		singleAlleleVariant = new MultiAlleleNucleotideVariant(SequenceType.CODING_DNA, "REF",
				ImmutableList.of(firstAllele));
		multiAlleleVariant = new MultiAlleleNucleotideVariant(SequenceType.CODING_DNA, "REF", ImmutableList.of(
				firstAllele, secondAllele));
	}

	public void testStaticFactoryMakeSingleChangeVariant() {
		Assert.assertEquals(singleAlleleVariant, SingleAlleleNucleotideVariant.makeSingleChangeVariant(
				SequenceType.CODING_DNA, "REF", NucleotideSubstitution.build(false, 99, "A", "G")));
	}

	public void testStaticFactoryBuild() {
		Assert.assertEquals(multiAlleleVariant, SingleAlleleNucleotideVariant.build(SequenceType.CODING_DNA, "REF",
				VariantConfiguration.IN_CIS, NucleotideSubstitution.build(false, 99, "C", "T"),
				NucleotideSubstitution.build(false, 300, "A", "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assert.assertEquals("REF:c.[100A>G;301A>C]", singleAlleleVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("REF:c.[100A>G;301A>C]", singleAlleleVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("REF:c.[100A>G;301A>C]", singleAlleleVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assert.assertEquals("REF:c.[100A>G;301A>C];[34T>C;301A>G]",
				multiAlleleVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("REF:c.[100A>G;301A>C];[34T>C;301A>G]",
				multiAlleleVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("REF:c.[100A>G;301A>C];[34T>C;301A>G]", multiAlleleVariant.toHGVSString());
	}

}
