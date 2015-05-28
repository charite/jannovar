package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;

/**
 * Feeds valid change descriptions to the lexer to see whether this works at least.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSLexerJustLexTest extends JustParseAndLexBase {

	@Test
	public void testLexingOnNTStrings() {
		for (String ntString : NT_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(ntString);
			HGVSLexer l = new HGVSLexer(inputStream);
			l.getAllTokens();
		}
	}

	// Debug code.
	// @Test
	// public void testLexingOnMyProtein() {
	// String[] arr = { "p.[(Ala25Thr; Gly28Val)]" };
	// for (String ntString : arr) {
	// ANTLRInputStream inputStream = new ANTLRInputStream(ntString);
	// HGVSLexer l = new HGVSLexer(inputStream);
	// System.err.println(l.getAllTokens());
	// }
	// }

	@Test
	public void testLexingOnProteinStrings() {
		for (String proteinString : PROTEIN_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(proteinString);
			HGVSLexer l = new HGVSLexer(inputStream);
			l.getAllTokens();
		}
	}

}
