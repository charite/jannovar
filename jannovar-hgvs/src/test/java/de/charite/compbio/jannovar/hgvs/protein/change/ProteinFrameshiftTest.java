package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

public class ProteinFrameshiftTest {

	private ProteinPointLocation position;
	private ProteinFrameshift fullFrameshift;
	private ProteinFrameshift noTerFrameshift;
	private ProteinFrameshift shortFrameshift;

	@Before
	public void setUp() throws Exception {
		position = new ProteinPointLocation("A", 123, 0, false);
		fullFrameshift = new ProteinFrameshift(false, position, "T", 23);
		noTerFrameshift = new ProteinFrameshift(false, position, "T", ProteinFrameshift.LEN_NO_TER);
		shortFrameshift = new ProteinFrameshift(false, position, null, ProteinFrameshift.LEN_SHORT);
	}

	@Test
	public void testConstructFullFrameshift() {
		Assert.assertEquals(fullFrameshift, ProteinFrameshift.build(false, position, "T", 23));
	}

	@Test
	public void testConstructNoTerFrameshift() {
		Assert.assertEquals(noTerFrameshift, ProteinFrameshift.buildWithoutTerminal(false, position, "T"));
	}

	@Test
	public void testConstructShortFrameshift() {
		Assert.assertEquals(shortFrameshift, ProteinFrameshift.buildShort(false, position));
	}

	@Test
	public void testFullFrameshiftToHGVSString() {
		Assert.assertEquals("Ala124Thrfs*23", fullFrameshift.toHGVSString());
		Assert.assertEquals("Ala124Thrfs*23", fullFrameshift.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("A124Tfs*23", fullFrameshift.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testNoTerFrameshiftToHGVSString() {
		Assert.assertEquals("Ala124Thrfs*?", noTerFrameshift.toHGVSString());
		Assert.assertEquals("Ala124Thrfs*?", noTerFrameshift.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("A124Tfs*?", noTerFrameshift.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

	@Test
	public void testShortFrameshiftToHGVSString() {
		Assert.assertEquals("Ala124fs", shortFrameshift.toHGVSString());
		Assert.assertEquals("Ala124fs", shortFrameshift.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("A124fs", shortFrameshift.toHGVSString(AminoAcidCode.ONE_LETTER));
	}

}
