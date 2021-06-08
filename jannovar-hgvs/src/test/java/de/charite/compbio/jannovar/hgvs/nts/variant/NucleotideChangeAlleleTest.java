package de.charite.compbio.jannovar.hgvs.nts.variant;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NucleotideChangeAlleleTest {

	private NucleotideChangeAllele singletonAllele;
	private NucleotideChangeAllele multiChangeAllele;

	@BeforeEach
	public void setUp() {
		singletonAllele = new NucleotideChangeAllele(VariantConfiguration.IN_CIS,
			ImmutableList.of(NucleotideSubstitution.build(false, 100, "C", "A")));
		multiChangeAllele = new NucleotideChangeAllele(VariantConfiguration.CHIMERIC, ImmutableList.of(
			NucleotideSubstitution.build(false, 100, "C", "A"),
			NucleotideSubstitution.build(false, 99, "C", "A")));
	}

	@Test
	public void testSingletonStaticFactory() {
		Assertions.assertEquals(singletonAllele, NucleotideChangeAllele.singleChangeAllele(NucleotideSubstitution
			.build(false, 100, "C", "A")));
	}

	@Test
	public void testMultiChangeStaticFactory() {
		Assertions.assertEquals(
			multiChangeAllele,
			NucleotideChangeAllele.build(VariantConfiguration.CHIMERIC,
				NucleotideSubstitution.build(false, 100, "C", "A"),
				NucleotideSubstitution.build(false, 99, "C", "A")));
	}

	@Test
	public void testSingletonToHGVSString() {
		Assertions.assertEquals("[101C>A]", singletonAllele.toHGVSString());
		Assertions.assertEquals("[101C>A]", singletonAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("[101C>A]", singletonAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testMultVariantToHGVSString() {
		Assertions.assertEquals("[101C>A//100C>A]", multiChangeAllele.toHGVSString());
		Assertions.assertEquals("[101C>A//100C>A]", multiChangeAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("[101C>A//100C>A]", multiChangeAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
