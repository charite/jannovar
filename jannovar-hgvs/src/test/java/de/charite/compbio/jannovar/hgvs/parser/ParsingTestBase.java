package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Hgvs_variantContext;

/**
 * Base class for parsing tests.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ParsingTestBase {

	public ParsingTestBase() {
		super();
	}

	protected Hgvs_variantContext parseString(String inputString) {
		return parseString(inputString, false);
	}

	protected Hgvs_variantContext parseString(String inputString, boolean trace) {
		if (trace) {
			ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
			HGVSLexer l = new HGVSLexer(inputStream);
			System.err.println(l.getAllTokens());
		}
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
		HGVSLexer l = new HGVSLexer(inputStream);
		HGVSParser p = new HGVSParser(new CommonTokenStream(l));
		p.setTrace(trace);
		p.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
			}
		});
		try {
			return p.hgvs_variant();
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Could not parse \"" + inputString + "\"", e);
		}
	}

}