package de.charite.compbio.jannovar.hgvs.protein.variant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;

public class SingleAlleleProteinVariantTest {

	private SingleAlleleProteinVariant singleChangeVariant;
	private SingleAlleleProteinVariant multiChangeVariant;

	@Before
	public void setUp() throws Exception {
		singleChangeVariant = new SingleAlleleProteinVariant("PROTEIN", VariantConfiguration.IN_CIS,
				ImmutableList.of(ProteinSubstitution.build(false, "A", 99, "G")));
		multiChangeVariant = new SingleAlleleProteinVariant("PROTEIN", VariantConfiguration.IN_CIS, ImmutableList.of(
				ProteinSubstitution.build(false, "C", 99, "T"), ProteinSubstitution.build(false, "A", 300, "G")));
	}

	public void testStaticFactoryMakeSingleChangeVariant() {
		Assert.assertEquals(
				singleChangeVariant,
				SingleAlleleProteinVariant.makeSingleChangeVariant("PROTEIN",
						ProteinSubstitution.build(false, "A", 99, "G")));
	}

	public void testStaticFactoryBuild() {
		Assert.assertEquals(multiChangeVariant, SingleAlleleProteinVariant.build("PROTEIN",
				VariantConfiguration.IN_CIS, ProteinSubstitution.build(false, "C", 99, "T"),
				ProteinSubstitution.build(false, "A", 300, "G")));
	}

	@Test
	public void testToHGVSStringSingleChange() {
		Assert.assertEquals("PROTEIN:p.A100G", singleChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("PROTEIN:p.Ala100Gly", singleChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("PROTEIN:p.Ala100Gly", singleChangeVariant.toHGVSString());
	}

	@Test
	public void testToHGVSStringMultiChange() {
		Assert.assertEquals("PROTEIN:p.[C100T;A301G]", multiChangeVariant.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("PROTEIN:p.[Cys100Thr;Ala301Gly]",
				multiChangeVariant.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("PROTEIN:p.[Cys100Thr;Ala301Gly]", multiChangeVariant.toHGVSString());
	}

}
