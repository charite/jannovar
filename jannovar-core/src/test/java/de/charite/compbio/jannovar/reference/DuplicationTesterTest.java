package de.charite.compbio.jannovar.reference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DuplicationTesterTest {

	private String ref;
	private String insertion;

	@BeforeEach
	public void setUp() {
		this.ref = "CGATCGAT";
		this.insertion = "AT";
	}

	@Test
	public void testFalse() {
		Assertions.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 3));
		Assertions.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 5));
	}

	@Test
	public void testOnBorders() {
		Assertions.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 0));
		Assertions.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 1));
		Assertions.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 7));
		Assertions.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 8));
	}

	@Test
	public void testBeforeTrue() {
		Assertions.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 4));
	}

	@Test
	public void testAfterTrue() {
		Assertions.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 2));
		Assertions.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 6));
	}

}
