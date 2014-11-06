package jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DuplicationCheckerTest {

	private String ref;
	private String insertion;

	@Before
	public void setUp() {
		this.ref = "CGATCGAT";
		this.insertion = "AT";
	}

	@Test
	public void testFalse() {
		Assert.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 3));
		Assert.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 5));
	}

	@Test
	public void testOnBorders() {
		Assert.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 0));
		Assert.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 1));
		Assert.assertFalse(DuplicationChecker.isDuplication(ref, insertion, 7));
		Assert.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 8));
	}

	@Test
	public void testBeforeTrue() {
		Assert.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 4));
	}

	@Test
	public void testAfterTrue() {
		Assert.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 2));
		Assert.assertTrue(DuplicationChecker.isDuplication(ref, insertion, 6));
	}

}
