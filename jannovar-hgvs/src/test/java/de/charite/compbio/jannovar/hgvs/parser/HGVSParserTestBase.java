package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class HGVSParserTestBase {

	/**
	 * Parse the given string and return the parser.
	 *
	 * @param inputString
	 *            HGVS string to parse
	 * @param mode
	 *            to enter into
	 * @param trace
	 *            whether or not to activate debug tracing
	 */
	protected Antlr4HGVSParser buildParserForString(String inputString, int mode, boolean trace) {
		if (trace) {
			ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
			HGVSLexer l = new HGVSLexer(inputStream);
			System.err.println(l.getAllTokens());
		}
		if (trace) {
			HGVSLexer lexer = new HGVSLexer(new ANTLRInputStream(inputString));
			lexer.pushMode(mode);
			System.err.println("Lexer tokens");
			for (Token t : lexer.getAllTokens())
				System.err.println("\t" + t.getText() + "\t" + t);
			System.err.println("END OF LEXER TOKENS");
		}
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
		HGVSLexer l = new HGVSLexer(inputStream);
		l.pushMode(mode);
		Antlr4HGVSParser p = new Antlr4HGVSParser(new CommonTokenStream(l));
		p.setTrace(trace);
		p.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
			}
		});
		return p;
	}


}
