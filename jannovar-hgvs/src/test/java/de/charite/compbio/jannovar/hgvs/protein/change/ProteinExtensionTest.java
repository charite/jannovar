package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

public class ProteinExtensionTest {

	private ProteinPointLocation position;
	private ProteinExtension normalExtension;
	private ProteinExtension noTerExtension;

	@Before
	public void setUp() throws Exception {
		position = new ProteinPointLocation("A", 123, 0, false);
		normalExtension = new ProteinExtension(false, position, "T", 23);
		noTerExtension = new ProteinExtension(false, position, "T", ProteinExtension.LEN_NO_TER);
	}

	@Test
	public void testBuildNormalExtension() {
		Assert.assertEquals(normalExtension, ProteinExtension.build(false, position, "T", 23));
	}

	@Test
	public void testBuildNoTerExtension() {
		Assert.assertEquals(noTerExtension, ProteinExtension.buildWithoutTerminal(false, position, "T"));
	}

	@Test
	public void testNormalExtensionToHGVSString() {
		Assert.assertEquals("Ala124Thrext*23", normalExtension.toHGVSString());
		Assert.assertEquals("Ala124Thrext*23", normalExtension.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("A124Text*23", normalExtension.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testNoTerExtensionToHGVSString() {
		Assert.assertEquals("Ala124Thrext*?", noTerExtension.toHGVSString());
		Assert.assertEquals("Ala124Thrext*?", noTerExtension.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("A124Text*?", noTerExtension.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
