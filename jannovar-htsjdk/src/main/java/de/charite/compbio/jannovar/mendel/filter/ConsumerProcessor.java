package de.charite.compbio.jannovar.mendel.filter;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Call a function for each variant put into the pipeline step
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class ConsumerProcessor implements VariantContextProcessor {

	/** The logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerProcessor.class);

	/** Write out variant into this consumer */
	private final Consumer<VariantContext> sink;

	public ConsumerProcessor(Consumer<VariantContext> sink) {
		this.sink = sink;
	}

	@Override
	public void put(VariantContext vc) throws VariantContextFilterException {
		LOGGER.trace("Putting VariantContext into consumer " + vc.toString());
		sink.accept(vc);
	}

	@Override
	public void close() {
		// Nothing to do here
	}

}
