package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProteinFrameshiftTest {

	private ProteinPointLocation position;
	private ProteinFrameshift fullFrameshift;
	private ProteinFrameshift noTerFrameshift;
	private ProteinFrameshift shortFrameshift;

	@BeforeEach
	public void setUp() throws Exception {
		position = new ProteinPointLocation("A", 123, 0, false);
		fullFrameshift = new ProteinFrameshift(false, position, "T", 23);
		noTerFrameshift = new ProteinFrameshift(false, position, "T", ProteinFrameshift.LEN_NO_TER);
		shortFrameshift = new ProteinFrameshift(false, position, null, ProteinFrameshift.LEN_SHORT);
	}

	@Test
	public void testConstructFullFrameshift() {
		Assertions.assertEquals(fullFrameshift, ProteinFrameshift.build(false, position, "T", 23));
	}

	@Test
	public void testConstructNoTerFrameshift() {
		Assertions.assertEquals(noTerFrameshift, ProteinFrameshift.buildWithoutTerminal(false, position, "T"));
	}

	@Test
	public void testConstructShortFrameshift() {
		Assertions.assertEquals(shortFrameshift, ProteinFrameshift.buildShort(false, position));
	}

	@Test
	public void testFullFrameshiftToHGVSString() {
		Assertions.assertEquals("A124Tfs*23", fullFrameshift.toHGVSString());
		Assertions.assertEquals("Ala124Thrfs*23", fullFrameshift.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("A124Tfs*23", fullFrameshift.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testNoTerFrameshiftToHGVSString() {
		Assertions.assertEquals("A124Tfs*?", noTerFrameshift.toHGVSString());
		Assertions.assertEquals("Ala124Thrfs*?", noTerFrameshift.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("A124Tfs*?", noTerFrameshift.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testShortFrameshiftToHGVSString() {
		Assertions.assertEquals("A124fs", shortFrameshift.toHGVSString());
		Assertions.assertEquals("Ala124fs", shortFrameshift.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assertions.assertEquals("A124fs", shortFrameshift.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
