package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.util.HashMap;

import org.junit.Test;

import de.charite.compbio.jannovar.impl.parse.gtfgff.FeatureRecord;

import org.junit.Assert;

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
