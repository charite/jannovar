package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;

/**
 * Extends the generated Antlr4HGVSLexer to bail out at the first lexing error.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSLexer extends Antlr4HGVSLexer {

	public HGVSLexer(CharStream input) {
		super(input);
	}

	@Override
	public void recover(LexerNoViableAltException e) {
		throw new RuntimeException(e); // bail out
	}

}
