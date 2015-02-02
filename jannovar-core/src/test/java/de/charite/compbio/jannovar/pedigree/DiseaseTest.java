package de.charite.compbio.jannovar.pedigree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedParseException;

public class DiseaseTest {

	@Test
	public void testToInt() {
		assertEquals(Disease.UNKNOWN.toInt(), 0);
		assertEquals(Disease.UNAFFECTED.toInt(), 1);
		assertEquals(Disease.AFFECTED.toInt(), 2);
	}

	@Test
	public void toDisease() throws PedParseException {
		assertEquals(Disease.toDisease("0"), Disease.UNKNOWN);
		assertEquals(Disease.toDisease("1"), Disease.UNAFFECTED);
		assertEquals(Disease.toDisease("2"), Disease.AFFECTED);
	}

	@Test(expected = PedParseException.class)
	public void toDiseaseThrows() throws PedParseException {
		assertEquals(Disease.toDisease("3"), Disease.UNKNOWN);
	}

}
