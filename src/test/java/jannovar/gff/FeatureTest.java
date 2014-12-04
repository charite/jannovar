package jannovar.gff;

import jannovar.common.FeatureType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class FeatureTest {

	Feature feature = null;

	@Before
	public void setUp() {
		ImmutableMap<String, String> map = new ImmutableMap.Builder<String, String>().put("key0", "value0")
				.put("key1", "value1").build();
		feature = new Feature("seq-id", "source", FeatureType.CDS, 10, 100, 0.1, true, (byte) 1, map);
	}

	@Test
	public void testValues() {
		Assert.assertEquals(feature.sequenceID, "seq-id");
		Assert.assertEquals(feature.end, 100);
		Assert.assertEquals(feature.phase, 1);
		Assert.assertTrue(feature.score == 0.1);
		Assert.assertEquals(feature.source, "source");
		Assert.assertEquals(feature.start, 10);
		Assert.assertEquals(feature.strand, true);
		Assert.assertEquals(feature.type, FeatureType.CDS);
		Assert.assertEquals(feature.attributes.size(), 2);
		Assert.assertEquals(feature.attributes.get("key0"), "value0");
		Assert.assertEquals(feature.attributes.get("key1"), "value1");
	}

	@Test
	public void testToString() {
		Assert.assertEquals(
				feature.toString(),
				"Feature [sequence_id=seq-id, source=source, type=CDS, start=10, end=100, score=0.1, "
						+ "strand=true, phase=1, attributes={key0=value0, key1=value1}]");
	}

	@Test
	public void testToLine() {
		Assert.assertEquals(feature.toLine(), "seq-id	source	CDS	10	100	0.1	+	1	key0=value0;key1=value1");
	}

}
