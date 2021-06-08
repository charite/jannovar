package de.charite.compbio.jannovar.hgvs.protein.variant;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SingleAlleleProteinVariantTest {

	private SingleAlleleProteinVariant singleChangeVariant;
	private SingleAlleleProteinVariant multiChangeVariant;

	@BeforeEach
	public void setUp() throws Exception {
		singleChangeVariant = new SingleAlleleProteinVariant("PROTEIN", VariantConfiguration.IN_CIS,
			ImmutableList.of(ProteinSubstitution.build(false, "A", 99, "G")));
		multiChangeVariant = new SingleAlleleProteinVariant("PROTEIN", VariantConfiguration.IN_CIS, ImmutableList.of(
			ProteinSubstitution.build(false, "C", 99, "T"), ProteinSubstitution.build(false, "A", 300, "G")));
	}

	public void testStaticFactoryMakeSingleChangeVariant() {
		Assertions.assertEquals(
			singleChangeVariant,
			SingleAlleleProteinVariant.makeSingleChangeVariant("PROTEIN",
				ProteinSubstitution.build(false, "A", 99, "G")));
	}

	public void testStaticFactoryBuild() {
		Assertions.assertEquals(multiChangeVariant, SingleAlleleProteinVariant.build("PROTEIN",
			VariantConfiguration.IN_CIS, ProteinSubstitution.build(false, "C", 99, "T"),
			ProteinSubstitution.build(false, "A", 300, "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assertions.assertEquals("PROTEIN:p.A100G", singleChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("PROTEIN:p.Ala100Gly", singleChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("PROTEIN:p.Ala100Gly", singleChangeVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assertions.assertEquals("PROTEIN:p.[C100T;A301G]", multiChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assertions.assertEquals("PROTEIN:p.[Cys100Thr;Ala301Gly]",
			multiChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("PROTEIN:p.[Cys100Thr;Ala301Gly]", multiChangeVariant.toHGVSString());
	}

}
