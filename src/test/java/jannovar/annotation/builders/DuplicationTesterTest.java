package jannovar.annotation.builders;

import jannovar.annotation.builders.DuplicationTester;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DuplicationTesterTest {

	private String ref;
	private String insertion;

	@Before
	public void setUp() {
		this.ref = "CGATCGAT";
		this.insertion = "AT";
	}

	@Test
	public void testFalse() {
		Assert.assertFalse(DuplicationTester.isDuplication(ref, insertion, 3));
		Assert.assertFalse(DuplicationTester.isDuplication(ref, insertion, 5));
	}

	@Test
	public void testOnBorders() {
		Assert.assertFalse(DuplicationTester.isDuplication(ref, insertion, 0));
		Assert.assertFalse(DuplicationTester.isDuplication(ref, insertion, 1));
		Assert.assertFalse(DuplicationTester.isDuplication(ref, insertion, 7));
		Assert.assertTrue(DuplicationTester.isDuplication(ref, insertion, 8));
	}

	@Test
	public void testBeforeTrue() {
		Assert.assertTrue(DuplicationTester.isDuplication(ref, insertion, 4));
	}

	@Test
	public void testAfterTrue() {
		Assert.assertTrue(DuplicationTester.isDuplication(ref, insertion, 2));
		Assert.assertTrue(DuplicationTester.isDuplication(ref, insertion, 6));
	}

}
