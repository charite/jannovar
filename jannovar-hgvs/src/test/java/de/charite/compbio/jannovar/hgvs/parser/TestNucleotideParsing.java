package de.charite.compbio.jannovar.hgvs.parser;

import org.junit.Test;

/**
 * Systematically test parsing of nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class TestNucleotideParsing extends ParsingTestBase {

	@Test
	public void testProteinSingleVarSubstitution() {
		String[] types = { "c.", "m.", "n.", "g.", "r." };
		String[] changes = { "76A>C", "-14G>C", "88+1G>T", "89-2A>C", "*46T" };
		for (String t : types)
			for (String s : changes)
				parseString(t + s);
	}

}
