package de.charite.compbio.jannovar.impl.parse.gtfgff;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class FeatureRecordTest {

	@Test
	public void test() {
		FeatureRecord record = new FeatureRecord("seqID", "source", "type", 10, 20, "score", FeatureRecord.Strand.FORWARD, 0,
			new HashMap<String, String>());

		Assert.assertEquals("seqID", record.getSeqID());
		Assert.assertEquals("source", record.getSource());
		Assert.assertEquals("type", record.getType());
		Assert.assertEquals(10, record.getBegin());
		Assert.assertEquals(20, record.getEnd());
		Assert.assertEquals("score", record.getScore());
		Assert.assertEquals(FeatureRecord.Strand.FORWARD, record.getStrand());
		Assert.assertEquals(0, record.getPhase());
		Assert.assertEquals(0, record.getAttributes().size());
	}

}
