package de.charite.compbio.jannovar.hgvs.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;

/**
 * ParseTreeListener used when parsing HGVS.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
class HGVSParserListenerImpl extends HGVSParserBaseListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(HGVSParserListenerImpl.class);

	public HGVSVariant getVariant() {
		return null;
	}

}
