package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

/**
 * Feeds valid HGSV descriptions to the parser to see whether that works.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class TestHGVSParserJustParse extends JustParseAndLexBase {

	@Test
	public void testParsingOnNTStrings() throws Exception {
		for (String ntString : NT_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(ntString);
			HGVSLexer l = new HGVSLexer(inputStream);
			HGVSParser p = new HGVSParser(new CommonTokenStream(l));
			p.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
				}
			});
			try {
				p.hgvs_variant();
			} catch (IllegalStateException e) {
				throw new Exception("Problem parsing \"" + ntString + "\"", e);
			}
		}
	}

	@Test
	public void testParsingOnProteinStrings() throws Exception {
		for (String proteinString : PROTEIN_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(proteinString);
			HGVSLexer l = new HGVSLexer(inputStream);
			HGVSParser p = new HGVSParser(new CommonTokenStream(l));
			p.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
				}
			});
			try {
				p.hgvs_variant();
				System.err.println("Could parse \"" + proteinString + "\"");
			} catch (IllegalStateException e) {
				throw new Exception("Problem parsing \"" + proteinString + "\"", e);
			}
		}
	}
}
