package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

public class NucleotideDeletionTest {

	@Test
	public void testLengthOneWithDeletedSeq() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithSequence(true, 123, 0, 123, 0, "A");
		Assert.assertEquals("(124delA)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithDeletedSeqLength() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithLength(true, 123, 0, 123, 0, 1);
		Assert.assertEquals("(124del1)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithBlankDeletedSeqSpec() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithoutSeqDescription(true, 123, 0, 123, 0);
		Assert.assertEquals("(124del)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDeletedSeq() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "AC");
		Assert.assertEquals("(124_125delAC)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDeletedSeqLength() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		Assert.assertEquals("(124_125del2)", del.toHGVSString());
	}

	@Test
	public void testToString() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		System.out.println(del);
	}

}
