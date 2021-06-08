package de.charite.compbio.jannovar.hgvs.nts.variant;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SingleAlleleNucleotideVariantTest {

	private SingleAlleleNucleotideVariant singleChangeVariant;
	private SingleAlleleNucleotideVariant multiChangeVariant;

	@BeforeEach
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
		Assertions.assertEquals(singleChangeVariant, SingleAlleleNucleotideVariant.makeSingleChangeVariant(
			SequenceType.CODING_DNA, "REF", NucleotideSubstitution.build(false, 99, "A", "G")));
	}

	public void testStaticFactoryBuild() {
		Assertions.assertEquals(multiChangeVariant, SingleAlleleNucleotideVariant.build(SequenceType.CODING_DNA, "REF",
			VariantConfiguration.IN_CIS, NucleotideSubstitution.build(false, 99, "C", "T"),
			NucleotideSubstitution.build(false, 300, "A", "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assertions.assertEquals("REF:c.100A>G", singleChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("REF:c.100A>G", singleChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("REF:c.100A>G", singleChangeVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assertions.assertEquals("REF:c.[100C>T;301A>G]", multiChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("REF:c.[100C>T;301A>G]", multiChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("REF:c.[100C>T;301A>G]", multiChangeVariant.toHGVSString());
	}

}
