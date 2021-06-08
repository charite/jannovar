package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.junit.jupiter.api.Test;

/**
 * Feeds valid change descriptions to the lexer to see whether this works at least.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class AntlrHGVSLexerJustLexTest extends AntlrHGVSJustParseAndLexBase {

	@Test
	public void testLexingOnNTStrings() {
		for (String ntString : NT_STRINGS) {
			CodePointCharStream inputStream = CharStreams.fromString(ntString);
			HGVSLexer l = new HGVSLexer(inputStream);
			l.mode(HGVSLexer.CHANGE_BRANCH);
			l.getAllTokens();
		}
	}

	@Test
	public void testLexingOnLegacyStrings() {
		for (String ntString : LEGACY_STRINGS) {
			CodePointCharStream inputStream = CharStreams.fromString(ntString);
			HGVSLexer l = new HGVSLexer(inputStream);
			l.mode(HGVSLexer.CHANGE_BRANCH);
			l.getAllTokens();
		}
	}

	@Test
	public void testLexingOnProteinStrings() {
		for (String proteinString : PROTEIN_STRINGS) {
			CodePointCharStream inputStream = CharStreams.fromString(proteinString);
			HGVSLexer l = new HGVSLexer(inputStream);
			l.mode(HGVSLexer.AMINO_ACID_CHANGE);
			l.getAllTokens();
		}
	}

}
