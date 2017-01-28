package de.charite.compbio.jannovar.pedigree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.Sex;

public class SexTest {

	@Test
	public void testToPlink() {
		assertEquals(Sex.UNKNOWN.toInt(), 0);
		assertEquals(Sex.MALE.toInt(), 1);
		assertEquals(Sex.FEMALE.toInt(), 2);
	}

	@Test
	public void toSex() throws PedParseException {
		assertEquals(Sex.toSex("0"), Sex.UNKNOWN);
		assertEquals(Sex.toSex("3"), Sex.UNKNOWN);
		assertEquals(Sex.toSex("anything-really"), Sex.UNKNOWN);
		assertEquals(Sex.toSex("1"), Sex.MALE);
		assertEquals(Sex.toSex("2"), Sex.FEMALE);
	}

}
