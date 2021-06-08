package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProteinExtensionTest {

	private ProteinPointLocation position;
	private ProteinExtension normalExtension;
	private ProteinExtension noTerExtension;

	@BeforeEach
	public void setUp() throws Exception {
		position = new ProteinPointLocation("A", 123, 0, false);
		normalExtension = new ProteinExtension(false, position, "T", 23);
		noTerExtension = new ProteinExtension(false, position, "T", ProteinExtension.LEN_NO_TER);
	}

	@Test
	public void testBuildNormalExtension() {
		Assertions.assertEquals(normalExtension, ProteinExtension.build(false, position, "T", 23));
	}

	@Test
	public void testBuildNoTerExtension() {
		Assertions.assertEquals(noTerExtension, ProteinExtension.buildWithoutTerminal(false, position, "T"));
	}

	@Test
	public void testNormalExtensionToHGVSString() {
		Assertions.assertEquals("A124Text*23", normalExtension.toHGVSString());
		Assertions.assertEquals("Ala124Thrext*23", normalExtension.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("A124Text*23", normalExtension.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testNoTerExtensionToHGVSString() {
		Assertions.assertEquals("A124Text*?", noTerExtension.toHGVSString());
		Assertions.assertEquals("Ala124Thrext*?", noTerExtension.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("A124Text*?", noTerExtension.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
