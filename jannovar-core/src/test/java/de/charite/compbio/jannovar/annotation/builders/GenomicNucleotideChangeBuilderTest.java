package de.charite.compbio.jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.Strand;

public class GenomicNucleotideChangeBuilderTest {

	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();
	private GenomeVariant varIns;
	private GenomeVariant varDel;
	private GenomeVariant varSNV;
	private GenomeVariant varSub;
	private GenomeVariant varInv;

	@Before
	public void setUp() throws Exception {
		varIns = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 100), "", "CGAT");
		varDel = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 100), "CGAT", "");
		varSNV = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 100), "C", "T");
		varSub = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 100), "CGAT", "TTTT");
		varInv = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 100), "CGAT", "ATCG");
	}

	@Test
	public void testInsertion() {
		Assert.assertEquals("100_101insCGAT", new GenomicNucleotideChangeBuilder(varIns).build().toHGVSString());
	}

	@Test
	public void testDeletion() {
		Assert.assertEquals("101_104delCGAT", new GenomicNucleotideChangeBuilder(varDel).build().toHGVSString());
	}

	@Test
	public void testSNV() {
		Assert.assertEquals("101C>T", new GenomicNucleotideChangeBuilder(varSNV).build().toHGVSString());
	}

	@Test
	public void testSubstitution() {
		Assert.assertEquals("101_103delCGAinsTTT", new GenomicNucleotideChangeBuilder(varSub).build().toHGVSString());
	}

	@Test
	public void testInversion() {
		Assert.assertEquals("101_104inv", new GenomicNucleotideChangeBuilder(varInv).build().toHGVSString());
	}

}
