package de.charite.compbio.jannovar.hgnc;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;

// TODO(holtgrewe): better documentation

/**
 * Representation of the relevant entries from one record from the <tt>hgnc_complete_set.txt</tt> file.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public class HGNCRecord {

	/** Official HGNC ID */
	private final String hgncID;

	/** HGNC-approved HUGO symbol */
	private final String symbol;

	/** HGNC name */
	private final String name;

	/** HGNC-approved aliases */
	private final ImmutableList<String> aliasSymbols;

	/** Previous symbols */
	private final ImmutableList<String> prevSymbol;

	/** Entrez ID for gene */
	private final String entrezID;

	/** Ensemble gene ID */
	private final String ensemblGeneID;

	/** VEGA gene ID */
	private final String vegaID;

	/** UCSC gene ID */
	private final String ucscID;

	/** ENA gene ID */
	private final String enaID;

	/** RefSeq accession */
	private final String refseqAccession;

	/** CCDS IDs */
	private final ImmutableList<String> ccdsIDs;

	/** Uniprot IDs */
	private final ImmutableList<String> uniprotIDs;

	/** PubMed IDs */
	private final ImmutableList<String> pubmedIDs;

	/** Mouse Genome Database ID */
	private final String mgdID;

	/** Rat Genome Database ID */
	private final String rgdID;

	/** COSMIC gene ID */
	private final String cosmicID;

	/** OMIM gene ID */
	private final String omimID;

	public HGNCRecord(String hgncID, String symbol, String name, Collection<String> aliasSymbols,
			Collection<String> prevSymbol, String entrezID, String ensemblGeneID, String vegaID, String ucscID,
			String enaID, String refseqAccession, Collection<String> ccdsIDs, Collection<String> uniprotIDs,
			Collection<String> pubmedIDs, String mgdID, String rgdID, String cosmicID, String omimID) {
		super();
		this.hgncID = hgncID;
		this.symbol = symbol;
		this.name = name;
		this.aliasSymbols = ImmutableList.copyOf(aliasSymbols);
		this.prevSymbol = ImmutableList.copyOf(prevSymbol);
		this.entrezID = entrezID;
		this.ensemblGeneID = ensemblGeneID;
		this.vegaID = vegaID;
		this.ucscID = ucscID;
		this.enaID = enaID;
		this.refseqAccession = refseqAccession;
		this.ccdsIDs = ImmutableList.copyOf(ccdsIDs);
		this.uniprotIDs = ImmutableList.copyOf(uniprotIDs);
		this.pubmedIDs = ImmutableList.copyOf(pubmedIDs);
		this.mgdID = mgdID;
		this.rgdID = rgdID;
		this.cosmicID = cosmicID;
		this.omimID = omimID;
	}

	public String getHgncID() {
		return hgncID;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public ImmutableList<String> getAliasSymbols() {
		return aliasSymbols;
	}

	public ImmutableList<String> getPrevSymbol() {
		return prevSymbol;
	}

	public String getEntrezID() {
		return entrezID;
	}

	public String getEnsemblGeneID() {
		return ensemblGeneID;
	}

	public String getVegaID() {
		return vegaID;
	}

	public String getUCSCID() {
		return ucscID;
	}

	public String getEnaID() {
		return enaID;
	}

	public String getRefseqAccession() {
		return refseqAccession;
	}

	public ImmutableList<String> getCCDSIDs() {
		return ccdsIDs;
	}

	public ImmutableList<String> getUniprotIDs() {
		return uniprotIDs;
	}

	public ImmutableList<String> getPubmedIDs() {
		return pubmedIDs;
	}

	public String getMGDID() {
		return mgdID;
	}

	public String getRGDID() {
		return rgdID;
	}

	public String getCosmicID() {
		return cosmicID;
	}

	public String getOmimID() {
		return omimID;
	}

	@Override
	public String toString() {
		return "HGNCRecord [hgncID=" + hgncID + ", symbol=" + symbol + ", name=" + name + ", aliasSymbols="
				+ aliasSymbols + ", prevSymbol=" + prevSymbol + ", entrezID=" + entrezID + ", ensemblGeneID="
				+ ensemblGeneID + ", vegaID=" + vegaID + ", ucscID=" + ucscID + ", enaID=" + enaID
				+ ", refseqAccession=" + refseqAccession + ", ccdsIDs=" + ccdsIDs + ", uniprotIDs=" + uniprotIDs
				+ ", pubmedIDs=" + pubmedIDs + ", mgdID=" + mgdID + ", rgdID=" + rgdID + ", cosmicID=" + cosmicID
				+ ", omimID=" + omimID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasSymbols == null) ? 0 : aliasSymbols.hashCode());
		result = prime * result + ((ccdsIDs == null) ? 0 : ccdsIDs.hashCode());
		result = prime * result + ((cosmicID == null) ? 0 : cosmicID.hashCode());
		result = prime * result + ((enaID == null) ? 0 : enaID.hashCode());
		result = prime * result + ((ensemblGeneID == null) ? 0 : ensemblGeneID.hashCode());
		result = prime * result + ((entrezID == null) ? 0 : entrezID.hashCode());
		result = prime * result + ((hgncID == null) ? 0 : hgncID.hashCode());
		result = prime * result + ((mgdID == null) ? 0 : mgdID.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((omimID == null) ? 0 : omimID.hashCode());
		result = prime * result + ((prevSymbol == null) ? 0 : prevSymbol.hashCode());
		result = prime * result + ((pubmedIDs == null) ? 0 : pubmedIDs.hashCode());
		result = prime * result + ((refseqAccession == null) ? 0 : refseqAccession.hashCode());
		result = prime * result + ((rgdID == null) ? 0 : rgdID.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((ucscID == null) ? 0 : ucscID.hashCode());
		result = prime * result + ((uniprotIDs == null) ? 0 : uniprotIDs.hashCode());
		result = prime * result + ((vegaID == null) ? 0 : vegaID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HGNCRecord other = (HGNCRecord) obj;
		if (aliasSymbols == null) {
			if (other.aliasSymbols != null)
				return false;
		} else if (!aliasSymbols.equals(other.aliasSymbols))
			return false;
		if (ccdsIDs == null) {
			if (other.ccdsIDs != null)
				return false;
		} else if (!ccdsIDs.equals(other.ccdsIDs))
			return false;
		if (cosmicID == null) {
			if (other.cosmicID != null)
				return false;
		} else if (!cosmicID.equals(other.cosmicID))
			return false;
		if (enaID == null) {
			if (other.enaID != null)
				return false;
		} else if (!enaID.equals(other.enaID))
			return false;
		if (ensemblGeneID == null) {
			if (other.ensemblGeneID != null)
				return false;
		} else if (!ensemblGeneID.equals(other.ensemblGeneID))
			return false;
		if (entrezID == null) {
			if (other.entrezID != null)
				return false;
		} else if (!entrezID.equals(other.entrezID))
			return false;
		if (hgncID == null) {
			if (other.hgncID != null)
				return false;
		} else if (!hgncID.equals(other.hgncID))
			return false;
		if (mgdID == null) {
			if (other.mgdID != null)
				return false;
		} else if (!mgdID.equals(other.mgdID))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (omimID == null) {
			if (other.omimID != null)
				return false;
		} else if (!omimID.equals(other.omimID))
			return false;
		if (prevSymbol == null) {
			if (other.prevSymbol != null)
				return false;
		} else if (!prevSymbol.equals(other.prevSymbol))
			return false;
		if (pubmedIDs == null) {
			if (other.pubmedIDs != null)
				return false;
		} else if (!pubmedIDs.equals(other.pubmedIDs))
			return false;
		if (refseqAccession == null) {
			if (other.refseqAccession != null)
				return false;
		} else if (!refseqAccession.equals(other.refseqAccession))
			return false;
		if (rgdID == null) {
			if (other.rgdID != null)
				return false;
		} else if (!rgdID.equals(other.rgdID))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (ucscID == null) {
			if (other.ucscID != null)
				return false;
		} else if (!ucscID.equals(other.ucscID))
			return false;
		if (uniprotIDs == null) {
			if (other.uniprotIDs != null)
				return false;
		} else if (!uniprotIDs.equals(other.uniprotIDs))
			return false;
		if (vegaID == null) {
			if (other.vegaID != null)
				return false;
		} else if (!vegaID.equals(other.vegaID))
			return false;
		return true;
	}

}
