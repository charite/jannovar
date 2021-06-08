package de.charite.compbio.jannovar.reference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnchorTest {

	@Test
	public void testUsage() {
		Anchor anchor = new Anchor(24, 0);
		Assertions.assertEquals(24, anchor.getGapPos());
		Assertions.assertEquals(0, anchor.getSeqPos());
		Assertions.assertEquals("Anchor{gapPos=24, seqPos=0}", anchor.toString());
	}

}
