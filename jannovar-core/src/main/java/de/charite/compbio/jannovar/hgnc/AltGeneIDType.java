package de.charite.compbio.jannovar.hgnc;

/**
 * Enum for describing an alternative gene ID type
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum AltGeneIDType {

	/** Official HGNC identifier */
	HGNC_ID,
	/** Official HGNC symbol */
	HGNC_SYMBOL,
	/** HGNC aliases */
	HGNC_ALIAS,
	/** Previously used HGNC ids */
	HGNC_PREVIOUS,
	/** Entrez ID */
	ENTREZ_ID,
	/** ENSEMBL gene id */
	ENSEMBL_GENE_ID,
	/** VEGA id */
	VEGA_ID,
	/** UCSC id */
	UCSC_ID,
	/** ENA id */
	ENA_ID,
	/** RefSeq accession */
	REFSEQ_ACCESSION,
	/** CCDS ids */
	CCDS_ID,
	/** Uniprot ids */
	UNIPROT_ID,
	/** PubMed ids */
	PUBMED_ID,
	/** MGD id */
	MGD_ID,
	/** RGD id */
	RGD_ID,
	/** COSMIC id */
	COSMIC_ID,
	/** OMIM id */
	OMIM_ID;

	/**
	 * @return <code>true</code> if containing multiple ids
	 */
	public boolean isMulti() {
		switch (this) {
		case HGNC_ALIAS:
		case HGNC_PREVIOUS:
		case CCDS_ID:
		case UNIPROT_ID:
		case PUBMED_ID:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return separator character used between values
	 */
	public char getSeparator() {
		return '|';
	}

}
