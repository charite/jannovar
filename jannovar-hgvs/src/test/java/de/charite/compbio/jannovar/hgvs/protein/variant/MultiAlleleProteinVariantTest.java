package de.charite.compbio.jannovar.hgvs.protein.variant;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiAlleleProteinVariantTest {

	private MultiAlleleProteinVariant singleAlleleVariant;
	private MultiAlleleProteinVariant multiAlleleVariant;
	private ProteinChangeAllele firstAllele;
	private ProteinChangeAllele secondAllele;

	@BeforeEach
	public void setUp() throws Exception {
		firstAllele = ProteinChangeAllele.build(VariantConfiguration.IN_CIS,
			ProteinSubstitution.build(false, "A", 99, "G"), ProteinSubstitution.build(false, "T", 300, "R"));
		secondAllele = ProteinChangeAllele.build(VariantConfiguration.IN_CIS,
			ProteinSubstitution.build(false, "R", 33, "V"), ProteinSubstitution.build(false, "L", 300, "K"));

		singleAlleleVariant = new MultiAlleleProteinVariant("PROTEIN", ImmutableList.of(firstAllele));
		multiAlleleVariant = new MultiAlleleProteinVariant("PROTEIN", ImmutableList.of(firstAllele, secondAllele));
	}

	public void testStaticFactoryMakeSingleChangeVariant() {
		Assertions.assertEquals(
			singleAlleleVariant,
			SingleAlleleProteinVariant.makeSingleChangeVariant("PROTEIN",
				ProteinSubstitution.build(false, "A", 99, "G")));
	}

	public void testStaticFactoryBuild() {
		Assertions.assertEquals(multiAlleleVariant, SingleAlleleProteinVariant.build("PROTEIN",
			VariantConfiguration.IN_CIS, ProteinSubstitution.build(false, "C", 99, "T"),
			ProteinSubstitution.build(false, "A", 300, "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assertions.assertEquals("PROTEIN:p.[A100G;T301R]", singleAlleleVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("PROTEIN:p.[Ala100Gly;Thr301Arg]",
			singleAlleleVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("PROTEIN:p.[Ala100Gly;Thr301Arg]", singleAlleleVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assertions.assertEquals("PROTEIN:p.[A100G;T301R];[R34V;L301K]",
			multiAlleleVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("PROTEIN:p.[Ala100Gly;Thr301Arg];[Arg34Val;Leu301Lys]",
			multiAlleleVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("PROTEIN:p.[Ala100Gly;Thr301Arg];[Arg34Val;Leu301Lys]", multiAlleleVariant.toHGVSString());
	}

}
