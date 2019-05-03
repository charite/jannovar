package de.charite.compbio.jannovar.reference;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AnchorTest {

	@Test public void testUsage() {
		Anchor anchor = new Anchor(24, 0);
		assertEquals(24, anchor.getGapPos());
		assertEquals(0, anchor.getSeqPos());
		assertEquals("Anchor{gapPos=24, seqPos=0}", anchor.toString());
	}

}
