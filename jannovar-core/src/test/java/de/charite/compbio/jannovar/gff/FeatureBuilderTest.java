package de.charite.compbio.jannovar.gff;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.impl.parse.gff.Feature;
import de.charite.compbio.jannovar.impl.parse.gff.FeatureType;

public class FeatureBuilderTest {

	/**
	 * Simple test for the FeatureBuilder().
	 */
	@Test
	public void testMake() {
		Feature builder = new Feature();
		builder.setSequenceID("seq-id");
		builder.setEnd(100);
		builder.setPhase((byte) 0);
		builder.setScore(0.0);
		builder.setSource("foo");
		builder.setStart(1);
		builder.setStrand(true);
		builder.setType(FeatureType.CDS);
		builder.addAttribute("key0", "value0");
		builder.addAttribute("key1", "value1");

		Assert.assertEquals(builder.getSequenceID(), "seq-id");
		Assert.assertEquals(builder.getEnd(), 100);
		Assert.assertEquals(builder.getPhase(), 0);
		Assert.assertEquals(builder.getScore(), 0.0, 0.0);
		Assert.assertEquals(builder.getSource(), "foo");
		Assert.assertEquals(builder.getStart(), 1);
		Assert.assertEquals(builder.getStrand(), true);
		Assert.assertEquals(builder.getType(), FeatureType.CDS);
		Assert.assertEquals(builder.getAttributes().size(), 2);
		Assert.assertEquals(builder.getAttributes().get("key0"), "value0");
		Assert.assertEquals(builder.getAttributes().get("key1"), "value1");
	}

}
