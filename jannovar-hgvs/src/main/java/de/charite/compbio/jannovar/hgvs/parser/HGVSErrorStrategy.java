package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;

public class HGVSErrorStrategy extends DefaultErrorStrategy {

	@Override
	protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
		throw new HGVSParsingException(e);
	}

}
