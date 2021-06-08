package de.charite.compbio.jannovar.impl.parse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FASTAParserTest {

	InputStream stream;
	String lines;

	@BeforeEach
	public void setUp() throws Exception {
		lines = ">1 comment 1\nACGT\nAACT\n\nACGT\n>2 comment 2\nAA\n\nAA\n\n";
		stream = new ByteArrayInputStream(lines.getBytes());
	}

	@Test
	public void test() throws IOException {
		FASTAParser parser = new FASTAParser(stream);

		FASTARecord first = parser.next();
		Assertions.assertEquals("1", first.getID());
		Assertions.assertEquals("comment 1", first.getComment());
		Assertions.assertEquals("ACGTAACTACGT", first.getSequence());

		FASTARecord second = parser.next();
		Assertions.assertEquals("2", second.getID());
		Assertions.assertEquals("comment 2", second.getComment());
		Assertions.assertEquals("AAAA", second.getSequence());

		FASTARecord third = parser.next();
		Assertions.assertNull(third);
	}

}
