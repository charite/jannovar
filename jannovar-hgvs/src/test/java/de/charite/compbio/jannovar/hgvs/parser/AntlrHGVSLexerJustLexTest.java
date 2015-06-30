package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;

/**
 * Feeds valid change descriptions to the lexer to see whether this works at least.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class AntlrHGVSLexerJustLexTest extends AntlrHGVSJustParseAndLexBase {

	@Test
	public void testLexingOnNTStrings() {
		for (String ntString : NT_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(ntString);
			Antlr4HGVSLexer l = new Antlr4HGVSLexer(inputStream);
			l.getAllTokens();
		}
	}

	@Test
	public void testLexingOnLegacyStrings() {
		for (String ntString : LEGACY_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(ntString);
			Antlr4HGVSLexer l = new Antlr4HGVSLexer(inputStream);
			l.getAllTokens();
		}
	}

	@Test
	public void testLexingOnProteinStrings() {
		for (String proteinString : PROTEIN_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(proteinString);
			Antlr4HGVSLexer l = new Antlr4HGVSLexer(inputStream);
			l.getAllTokens();
		}
	}

}
