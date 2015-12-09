package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

// TODO(holtgrewe): Add support for "c.[83G=/83G>C]"

/**
 * Feeds valid HGSV descriptions to the parser to see whether that works.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class AntlrHGVSParserJustParseTest extends AntlrHGVSJustParseAndLexBase {

	public static String PREFIX = "NM_000109.3:";

	@Test
	public void testParsingOnNTStrings() throws Exception {
		for (String ntString : NT_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(PREFIX + ntString);
			HGVSLexer l = new HGVSLexer(inputStream);
			Antlr4HGVSParser p = new Antlr4HGVSParser(new CommonTokenStream(l));
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
				throw new Exception("Problem parsing \"" + PREFIX + ntString + "\"", e);
			}
		}
	}

	@Test
	public void testParsingOnLegacyStrings() throws Exception {
		for (String ntString : LEGACY_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(PREFIX + ntString);
			Antlr4HGVSLexer l = new Antlr4HGVSLexer(inputStream);
			Antlr4HGVSParser p = new Antlr4HGVSParser(new CommonTokenStream(l));
			p.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
				}
			});
			try {
				p.legacy_variant();
			} catch (IllegalStateException e) {
				throw new Exception("Problem parsing \"" + PREFIX + ntString + "\"", e);
			}
		}
	}

	@Test
	public void testParsingOnProteinStrings() throws Exception {
		// for (String proteinString : new String[] { "p.Met1" }) {
		for (String proteinString : PROTEIN_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(PREFIX + proteinString);
			Antlr4HGVSLexer l = new Antlr4HGVSLexer(inputStream);
			Antlr4HGVSParser p = new Antlr4HGVSParser(new CommonTokenStream(l));
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
				throw new Exception("Problem parsing \"" + PREFIX + proteinString + "\"", e);
			}
		}
	}
}
