package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

// TODO(holtgrewe): Add support for "c.[83G=/83G>C]"

/**
 * Feeds valid HGSV descriptions to the parser to see whether that works.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserJustParseTest extends JustParseAndLexBase {

	public static String PREFIX = "NM_000109.3:";

	@Test
	public void testParsingOnNTStrings() throws Exception {
		for (String ntString : NT_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(PREFIX + ntString);
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
				throw new Exception("Problem parsing \"" + PREFIX + ntString + "\"", e);
			}
		}
	}

	@Test
	public void testParsingOnProteinStrings() throws Exception {
		//		for (String proteinString : new String[] { "p.Met1" }) {
		for (String proteinString : PROTEIN_STRINGS) {
			ANTLRInputStream inputStream = new ANTLRInputStream(PREFIX + proteinString);
			if (proteinString.equals("p.Met1Leu")) {
				HGVSLexer lexer = new HGVSLexer(new ANTLRInputStream(PREFIX + proteinString));
				System.err.println("Lexer tokens");
				for (Token t : lexer.getAllTokens())
					System.err.println("\t" + t.getText() + "\t" + t);
				System.err.println("END OF LEXTER TOKENS");
			}
			HGVSLexer l = new HGVSLexer(inputStream);
			HGVSParser p = new HGVSParser(new CommonTokenStream(l));
			if (proteinString.equals("p.Met1Leu"))
				p.setTrace(true);
			p.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
						int charPositionInLine, String msg, RecognitionException e) {
					throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
				}
			});
			try {
				p.hgvs_variant();
				System.err.println("Could parse \"" + PREFIX + proteinString + "\"");
			} catch (IllegalStateException e) {
				throw new Exception("Problem parsing \"" + PREFIX + proteinString + "\"", e);
			}
		}
	}
}
