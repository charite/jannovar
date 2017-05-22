package de.charite.compbio.jannovar.vardbs.generic_tsv;

/**
 * Enumeration for describing annotation target (either position only or variant, e.g.
 * <code>C&gt;T</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum GenericTSVAnnotationTarget {
	/** TSV file annotates a position. */
	POSITION,
	/** TSV file annotates a variant allele. */
	VARIANT;
}