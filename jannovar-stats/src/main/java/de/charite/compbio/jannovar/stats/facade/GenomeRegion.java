package de.charite.compbio.jannovar.stats.facade;

/**
 * Rough classification of genomic region for statistics computation
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum GenomeRegion {
	/** Upstream */
	UPSTREAM,
	/** 5' UTR */
	UTR5,
	/** Exonic */
	EXONIC,
	/** Intronic */
	INTRONIC,
	/** 3' UTR */
	UTR3,
	/** Downstream */
	DOWNSTREAM,
	/** Intergenic */
	INTERGENIC;
}
