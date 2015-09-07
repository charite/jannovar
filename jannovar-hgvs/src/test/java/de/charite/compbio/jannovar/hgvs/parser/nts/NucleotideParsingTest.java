package de.charite.compbio.jannovar.hgvs.parser.nts;

import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParsingTestBase;

/**
 * Systematically test parsing of nucleotide changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideParsingTest extends HGVSParsingTestBase {

	public static String PREFIX = "NM_000109.3:";

	@Test
	public void testNucleotideSingleVarSubstitution() {
		String[] types = { "c.", "m.", "n.", "g.", "r." };
		String[] changes = { "76A>C", "-14G>C", "88+1G>T", "89-2A>C", "*46T>C" };
		for (String t : types)
			for (String s : changes)
				parseString(PREFIX + t + s);
	}

}
