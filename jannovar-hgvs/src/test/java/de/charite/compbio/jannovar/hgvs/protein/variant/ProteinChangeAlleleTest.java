package de.charite.compbio.jannovar.hgvs.protein.variant;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProteinChangeAlleleTest {

	private ProteinChangeAllele singletonAllele;
	private ProteinChangeAllele multiChangeAllele;

	@BeforeEach
	public void setUp() {
		singletonAllele = new ProteinChangeAllele(VariantConfiguration.IN_CIS, ImmutableList.of(ProteinSubstitution
			.build(false, "L", 100, "R")));
		multiChangeAllele = new ProteinChangeAllele(VariantConfiguration.CHIMERIC, ImmutableList.of(
			ProteinSubstitution.build(false, "L", 100, "R"), ProteinSubstitution.build(false, "A", 99, "G")));
	}

	@Test
	public void testSingletonStaticFactory() {
		Assertions.assertEquals(singletonAllele,
			ProteinChangeAllele.singleChangeAllele(ProteinSubstitution.build(false, "L", 100, "R")));
	}

	@Test
	public void testMultiChangeStaticFactory() {
		Assertions.assertEquals(multiChangeAllele, ProteinChangeAllele.build(VariantConfiguration.CHIMERIC,
			ProteinSubstitution.build(false, "L", 100, "R"), ProteinSubstitution.build(false, "A", 99, "G")));
	}

	@Test
	public void testSingletonToHGVSString() {
		Assertions.assertEquals("[Leu101Arg]", singletonAllele.toHGVSString());
		Assertions.assertEquals("[Leu101Arg]", singletonAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("[L101R]", singletonAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testMultVariantToHGVSString() {
		Assertions.assertEquals("[Leu101Arg//Ala100Gly]", multiChangeAllele.toHGVSString());
		Assertions.assertEquals("[Leu101Arg//Ala100Gly]", multiChangeAllele.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("[L101R//A100G]", multiChangeAllele.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
