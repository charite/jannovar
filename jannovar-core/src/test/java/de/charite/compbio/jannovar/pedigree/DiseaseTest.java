package de.charite.compbio.jannovar.pedigree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;

public class DiseaseTest {

	@Test
	public void testToInt() {
		Assertions.assertEquals(Disease.UNKNOWN.toInt(), 0);
		Assertions.assertEquals(Disease.UNAFFECTED.toInt(), 1);
		Assertions.assertEquals(Disease.AFFECTED.toInt(), 2);
	}

	@Test
	public void toDisease() throws PedParseException {
		Assertions.assertEquals(Disease.toDisease("0"), Disease.UNKNOWN);
		Assertions.assertEquals(Disease.toDisease("1"), Disease.UNAFFECTED);
		Assertions.assertEquals(Disease.toDisease("2"), Disease.AFFECTED);
	}

	@Test
	public void toDiseaseThrows() throws PedParseException {
		Assertions.assertThrows(PedParseException.class, () -> {
			Assertions.assertEquals(Disease.toDisease("3"), Disease.UNKNOWN);
		});
	}

}
