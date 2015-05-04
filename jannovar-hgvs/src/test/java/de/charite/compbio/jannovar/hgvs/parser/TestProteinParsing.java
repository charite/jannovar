package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Hgvs_variantContext;

/**
 * Systematically test parsing of protein changes
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class TestProteinParsing {

	protected Hgvs_variantContext parseString(String inputString) {
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
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
		try {
			return p.hgvs_variant();
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Could not parse \"" + inputString + "\"", e);
		}
	}

	@Test
	public void testProteinSingleVarTrivialChanges() {
		String[] arr = { "p.0", "p.?", "p.0", "p.0?" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinSingleVarSubstitutions() {
		String[] arr = { "p.G33L", "p.*34Gext*34", "p.*34GextTer34", "p.Ter34GextTer34", "p.*34*",
				"p.Met1ValextMet-12", "p.Met1?", "p.Met1ext-5" };
		for (String s : arr)
			parseString(s);
	}

}
