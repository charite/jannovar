package de.charite.compbio.jannovar.vardbs.dbsnp;

import de.charite.compbio.jannovar.Immutable;

/**
 * Information about a gene in DBSNP (name and Entrez ID)
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class DBSNPGeneInfo {

	/** Name of the gene */
	final private String symbol;
	/** ID of the gene */
	final private int id;

	public DBSNPGeneInfo(String symbol, int id) {
		super();
		this.symbol = symbol;
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		DBSNPGeneInfo other = (DBSNPGeneInfo) obj;
		if (id != other.id)
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DBSNPGeneInfo [symbol=" + symbol + ", id=" + id + "]";
	}

}
