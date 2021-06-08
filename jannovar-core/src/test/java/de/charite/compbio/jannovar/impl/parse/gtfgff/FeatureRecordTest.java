package de.charite.compbio.jannovar.impl.parse.gtfgff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class FeatureRecordTest {

	@Test
	public void test() {
		FeatureRecord record = new FeatureRecord("seqID", "source", "type", 10, 20, "score", FeatureRecord.Strand.FORWARD, 0,
			new HashMap<String, String>());

		Assertions.assertEquals("seqID", record.getSeqID());
		Assertions.assertEquals("source", record.getSource());
		Assertions.assertEquals("type", record.getType());
		Assertions.assertEquals(10, record.getBegin());
		Assertions.assertEquals(20, record.getEnd());
		Assertions.assertEquals("score", record.getScore());
		Assertions.assertEquals(FeatureRecord.Strand.FORWARD, record.getStrand());
		Assertions.assertEquals(0, record.getPhase());
		Assertions.assertEquals(0, record.getAttributes().size());
	}

}
