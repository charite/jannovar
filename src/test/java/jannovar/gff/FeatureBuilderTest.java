package jannovar.gff;

import jannovar.common.FeatureType;

import org.junit.Test;
import org.testng.Assert;

public class FeatureBuilderTest {

	/**
	 * Simple test for the FeatureBuilder().
	 */
	@Test
	public void testMake() {
		FeatureBuilder builder = new FeatureBuilder();
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

		Feature feature = builder.make();
		Assert.assertEquals(feature.sequenceID, "seq-id");
		Assert.assertEquals(feature.end, 100);
		Assert.assertEquals(feature.phase, 0);
		Assert.assertEquals(feature.score, 0.0);
		Assert.assertEquals(feature.source, "foo");
		Assert.assertEquals(feature.start, 1);
		Assert.assertEquals(feature.strand, true);
		Assert.assertEquals(feature.type, FeatureType.CDS);
		Assert.assertEquals(feature.attributes.size(), 2);
		Assert.assertEquals(feature.attributes.get("key0"), "value0");
		Assert.assertEquals(feature.attributes.get("key1"), "value1");
	}

}
