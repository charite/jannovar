package de.charite.compbio.jannovar.vardbs.base;

import htsjdk.variant.vcf.VCFHeader;

/**
 * Extend {@link VCFHeader} object with headers for a given database
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public abstract class VCFHeaderExtender {

	/** Options for header extension */
	protected DBAnnotationOptions options;

	public VCFHeaderExtender(DBAnnotationOptions options) {
		this.options = options;
	}

	/**
	 * @return Default prefix to use for header entries
	 */
	public abstract String getDefaultPrefix();

	/**
	 * Add header entries with a given prefix.
	 * 
	 * @param header
	 *            The {@link VCFHeader} to extend.
	 * @param prefix
	 *            A String prefix to prepend to header record names.
	 */
	public abstract void addHeaders(VCFHeader header, String prefix);

	/**
	 * Add headers with default prefix
	 */
	public void addHeaders(VCFHeader header) {
		addHeaders(header, getDefaultPrefix());
	}

}
