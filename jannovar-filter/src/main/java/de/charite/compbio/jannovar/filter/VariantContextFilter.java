package de.charite.compbio.jannovar.filter;


/**
 * Filter for {@link FlaggedVariant} objects, designed as a sink.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public interface VariantContextFilter {

	/**
	 * Put a Variant Context into the filter.
	 *
	 * @param vc
	 *            {@link FlaggedVariant} to put into the filter.
	 * @throws FilterException
	 *             on problems during the filtration
	 */
	public void put(FlaggedVariant vc) throws FilterException;

	/**
	 * Mark processing as done, no more variants will come in.
	 *
	 * @throws FilterException
	 *             on problems during the filtration
	 */
	public void finish() throws FilterException;

}
