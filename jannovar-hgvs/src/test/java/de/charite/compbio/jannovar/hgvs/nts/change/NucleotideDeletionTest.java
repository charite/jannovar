package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideDeletionTest {

	@Test
	public void testLengthOneWithDeletedSeq() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithSequence(true, 123, 0, 123, 0, "A");
		Assertions.assertEquals("(124delA)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithDeletedSeqLength() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithLength(true, 123, 0, 123, 0, 1);
		Assertions.assertEquals("(124del1)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithBlankDeletedSeqSpec() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithoutSeqDescription(true, 123, 0, 123, 0);
		Assertions.assertEquals("(124del)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDeletedSeq() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "AC");
		Assertions.assertEquals("(124_125delAC)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithDeletedSeqLength() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		Assertions.assertEquals("(124_125del2)", del.toHGVSString());
	}

	@Test
	public void testToString() {
		NucleotideDeletion del = NucleotideDeletion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		System.out.println(del);
	}

}
