package de.charite.compbio.jannovar.hgvs.nts.variant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;

public class SingleAlleleNucleotideVariantTest {

	private SingleAlleleNucleotideVariant singleChangeVariant;
	private SingleAlleleNucleotideVariant multiChangeVariant;

	@Before
	public void setUp() throws Exception {
		singleChangeVariant = new SingleAlleleNucleotideVariant(SequenceType.CODING_DNA, "REF",
				VariantConfiguration.IN_CIS, ImmutableList.of(NucleotideSubstitution.build(false, 99,
						"A", "G")));
		multiChangeVariant = new SingleAlleleNucleotideVariant(SequenceType.CODING_DNA, "REF",
				VariantConfiguration.IN_CIS, ImmutableList.of(
						NucleotideSubstitution.build(false, 99, "C", "T"),
						NucleotideSubstitution.build(false, 300, "A", "G")));
	}

	public void testStaticFactoryMakeSingleChangeVariant() {
		Assert.assertEquals(singleChangeVariant, SingleAlleleNucleotideVariant.makeSingleChangeVariant(
				SequenceType.CODING_DNA, "REF", NucleotideSubstitution.build(false, 99, "A", "G")));
	}

	public void testStaticFactoryBuild() {
		Assert.assertEquals(multiChangeVariant, SingleAlleleNucleotideVariant.build(SequenceType.CODING_DNA, "REF",
				VariantConfiguration.IN_CIS, NucleotideSubstitution.build(false, 99, "C", "T"),
				NucleotideSubstitution.build(false, 300, "A", "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assert.assertEquals("REF:c.100A>G", singleChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("REF:c.100A>G", singleChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("REF:c.100A>G", singleChangeVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assert.assertEquals("REF:c.[100C>T;301A>G]", multiChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("REF:c.[100C>T;301A>G]", multiChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("REF:c.[100C>T;301A>G]", multiChangeVariant.toHGVSString());
	}

}
