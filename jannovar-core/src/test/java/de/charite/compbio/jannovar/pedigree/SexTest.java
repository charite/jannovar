package de.charite.compbio.jannovar.pedigree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class SexTest {

	@Test
	public void testToPlink() {
		Assertions.assertEquals(Sex.UNKNOWN.toInt(), 0);
		Assertions.assertEquals(Sex.MALE.toInt(), 1);
		Assertions.assertEquals(Sex.FEMALE.toInt(), 2);
	}

	@Test
	public void toSex() throws PedParseException {
		Assertions.assertEquals(Sex.toSex("0"), Sex.UNKNOWN);
		Assertions.assertEquals(Sex.toSex("3"), Sex.UNKNOWN);
		Assertions.assertEquals(Sex.toSex("anything-really"), Sex.UNKNOWN);
		Assertions.assertEquals(Sex.toSex("1"), Sex.MALE);
		Assertions.assertEquals(Sex.toSex("2"), Sex.FEMALE);
	}

}
