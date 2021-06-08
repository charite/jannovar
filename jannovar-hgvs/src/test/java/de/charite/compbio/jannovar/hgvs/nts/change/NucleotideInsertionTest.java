package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NucleotideInsertionTest {

	@Test
	public void testLengthOneWithInsertedSeq() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "CGA");
		Assertions.assertEquals("(124_125insCGA)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithInsertedSeqLength() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 1);
		Assertions.assertEquals("(124_125ins1)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithBlankInsertedSeqSpec() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 3);
		Assertions.assertEquals("(124_125ins3)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithInsertedSeq() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "AC");
		Assertions.assertEquals("(124_125insAC)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithInsertedSeqLength() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		Assertions.assertEquals("(124_125ins2)", del.toHGVSString());
	}

}
