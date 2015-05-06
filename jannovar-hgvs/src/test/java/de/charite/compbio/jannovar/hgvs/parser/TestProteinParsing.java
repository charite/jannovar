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
		// p.setTrace(true);
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

	@Test
	public void testProteinSingleVarDeletion() {
		String[] arr = { "p.G33del", "p.Glu123del", "p.G33_A127del", "p.Glu33_Ala127del" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinSingleVarDuplication() {
		String[] arr = { "p.G33dup", "p.Glu123dup" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinSingleVarVaryingShortSequenceRepeat() {
		String[] arr = { "p.G33(3_6)", "p.Glu123(3_6)" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinSingleVarInsertion() {
		String[] arr = { "p.G33_L34insGlu", "p.Glu33_Lys34insG", "p.G33_L34ins17", "p.Glu33_Lys34ins17" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinSingleVarDelIns() {
		String[] arr = { "p.G33delins17", "p.Glu33_Lys34delins17", "p.G33delinsG", "p.Glu33_Lys34delinsGlu" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinShortFrameShift() {
		String[] arr = { "p.G33fs", "p.Arg97fs" };
		for (String s : arr)
			parseString(s);
	}

	@Test
	public void testProteinLongFrameShift() {
		String[] arr = { "p.Arg97Profs*23", "p.A97Pfs*23" };
		for (String s : arr)
			parseString(s);
	}

}
