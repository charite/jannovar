package de.charite.compbio.jannovar.impl.parse;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FASTAParserTest {

	InputStream stream;
	String lines;

	@Before
	public void setUp() throws Exception {
		lines = ">1 comment 1\nACGT\nAACT\n\nACGT\n>2 comment 2\nAA\n\nAA\n\n";
		stream = new ByteArrayInputStream(lines.getBytes());
	}

	@Test
	public void test() throws IOException {
		FASTAParser parser = new FASTAParser(stream);

		FASTARecord first = parser.next();
		Assert.assertEquals("1", first.getID());
		Assert.assertEquals("comment 1", first.getComment());
		Assert.assertEquals("ACGTAACTACGT", first.getSequence());

		FASTARecord second = parser.next();
		Assert.assertEquals("2", second.getID());
		Assert.assertEquals("comment 2", second.getComment());
		Assert.assertEquals("AAAA", second.getSequence());

		FASTARecord third = parser.next();
		Assert.assertNull(third);
	}

}
