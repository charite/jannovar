package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;

/**
 * Driver code for parsing HGVS strings into HGVSVariant objects.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserDriver {

	private static final Logger LOGGER = LoggerFactory.getLogger(HGVSParserDriver.class);

	private boolean debug = false;

	public HGVSVariant parseHGVSString(String inputString) {
		LOGGER.info("Parsing input string " + inputString);
		HGVSParser parser = getParser(inputString);
		HGVSParserListenerImpl listener = new HGVSParserListenerImpl();
		parser.addParseListener(listener);
		ParseTree tree = parser.hgvs_variant();
		System.err.println(tree.toStringTree(parser));
		return listener.getVariant();
	}

	private HGVSParser getParser(String inputString) {
		if (debug) {
			ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
			HGVSLexer l = new HGVSLexer(inputStream);
			System.err.println(l.getAllTokens());
		}
		if (debug) {
			HGVSLexer lexer = new HGVSLexer(new ANTLRInputStream(inputString));
			//			lexer.pushMode(mode);
			System.err.println("Lexer tokens");
			for (Token t : lexer.getAllTokens())
				System.err.println("\t" + t.getText() + "\t" + t);
			System.err.println("END OF LEXTER TOKENS");
		}
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
		HGVSLexer l = new HGVSLexer(inputStream);
		//		l.pushMode(mode);
		HGVSParser p = new HGVSParser(new CommonTokenStream(l));
		p.setTrace(debug);
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
