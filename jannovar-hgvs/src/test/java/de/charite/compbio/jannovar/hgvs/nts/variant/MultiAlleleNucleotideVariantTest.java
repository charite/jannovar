package de.charite.compbio.jannovar.hgvs.nts.variant;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiAlleleNucleotideVariantTest {

	private MultiAlleleNucleotideVariant singleAlleleVariant;
	private MultiAlleleNucleotideVariant multiAlleleVariant;
	private NucleotideChangeAllele firstAllele;
	private NucleotideChangeAllele secondAllele;

	@BeforeEach
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
		Assertions.assertEquals(singleAlleleVariant, SingleAlleleNucleotideVariant.makeSingleChangeVariant(
			SequenceType.CODING_DNA, "REF", NucleotideSubstitution.build(false, 99, "A", "G")));
	}

	public void testStaticFactoryBuild() {
		Assertions.assertEquals(multiAlleleVariant, SingleAlleleNucleotideVariant.build(SequenceType.CODING_DNA, "REF",
			VariantConfiguration.IN_CIS, NucleotideSubstitution.build(false, 99, "C", "T"),
			NucleotideSubstitution.build(false, 300, "A", "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assertions.assertEquals("REF:c.[100A>G;301A>C]", singleAlleleVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("REF:c.[100A>G;301A>C]", singleAlleleVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("REF:c.[100A>G;301A>C]", singleAlleleVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assertions.assertEquals("REF:c.[100A>G;301A>C];[34T>C;301A>G]",
			multiAlleleVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("REF:c.[100A>G;301A>C];[34T>C;301A>G]",
			multiAlleleVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("REF:c.[100A>G;301A>C];[34T>C;301A>G]", multiAlleleVariant.toHGVSString());
	}

}
