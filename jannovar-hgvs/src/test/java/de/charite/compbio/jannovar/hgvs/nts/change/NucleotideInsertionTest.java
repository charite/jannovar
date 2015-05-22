package de.charite.compbio.jannovar.hgvs.nts.change;

import org.junit.Assert;
import org.junit.Test;

public class NucleotideInsertionTest {

	@Test
	public void testLengthOneWithInsertedSeq() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "CGA");
		Assert.assertEquals("(124_125insCGA)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithInsertedSeqLength() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 1);
		Assert.assertEquals("(124_125ins1)", del.toHGVSString());
	}

	@Test
	public void testLengthOneWithBlankInsertedSeqSpec() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 3);
		Assert.assertEquals("(124_125ins3)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithInsertedSeq() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithSequence(true, 123, 0, 124, 0, "AC");
		Assert.assertEquals("(124_125insAC)", del.toHGVSString());
	}

	@Test
	public void testLengthTwoWithInsertedSeqLength() {
		NucleotideInsertion del = NucleotideInsertion.buildWithOffsetWithLength(true, 123, 0, 124, 0, 2);
		Assert.assertEquals("(124_125ins2)", del.toHGVSString());
	}

}
