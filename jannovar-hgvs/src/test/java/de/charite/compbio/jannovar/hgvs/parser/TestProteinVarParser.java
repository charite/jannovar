package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

public class TestProteinVarParser {

	@Test
	public void testGarbage() {
		ANTLRInputStream inputStream = new ANTLRInputStream("Garbage");
		HGVSLexer l = new HGVSLexer(inputStream);
		HGVSParser p = new HGVSParser(new CommonTokenStream(l));
		p.setTrace(true);
		p.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
			}
		});
		p.hgvs();
	}

	@Test
	public void testProteinSubtitutionWithRef() {
		ANTLRInputStream inputStream = new ANTLRInputStream("FOO:p.Asn3Leu");
		HGVSLexer l = new HGVSLexer(inputStream);
		HGVSParser p = new HGVSParser(new CommonTokenStream(l));
		p.setTrace(true);
		p.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
			}
		});
		p.hgvs();
	}

	@Test
	public void testProteinSubtitutionWithoutRef() {
		ANTLRInputStream inputStream = new ANTLRInputStream("p.A3L");
		HGVSLexer l = new HGVSLexer(inputStream);
		HGVSParser p = new HGVSParser(new CommonTokenStream(l));
		p.setTrace(true);
		p.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
			}
		});
		p.hgvs();
	}

}
