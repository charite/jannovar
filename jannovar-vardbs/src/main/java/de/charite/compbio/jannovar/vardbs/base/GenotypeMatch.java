package de.charite.compbio.jannovar.vardbs.base;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * A class for annotating the match between an observed genotype and a database genotype
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class GenotypeMatch {

	/** Numeric index of the observed allele (in <code>obsVC</code>) */
	final int observedAllele;
	/** Numeric index of the database allele (in <code>dbVC</code>) */
	final int dbAllele;
	/** The observed VariantContext */
	final VariantContext obsVC;
	/** The database VariantContext */
	final VariantContext dbVC;
	/** Whether is a match (if false: overlap only). */
	final boolean isMatch;

	public GenotypeMatch(int observedAllele, int dbAllele, VariantContext obsVC,
			VariantContext dbVC, boolean isMatch) {
		this.observedAllele = observedAllele;
		this.dbAllele = dbAllele;
		this.obsVC = obsVC;
		this.dbVC = dbVC;
		this.isMatch = isMatch;
	}

	public int getObservedAllele() {
		return observedAllele;
	}

	public int getDbAllele() {
		return dbAllele;
	}

	public VariantContext getObsVC() {
		return obsVC;
	}

	public VariantContext getDBVC() {
		return dbVC;
	}

	public boolean isMatch() {
		return isMatch;
	}

	@Override
	public String toString() {
		return "GenotypeMatch [observedAllele=" + observedAllele + ", dbAllele=" + dbAllele
				+ ", obsVC=" + obsVC + ", dbVC=" + dbVC + ", isMatch=" + isMatch + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dbAllele;
		result = prime * result + ((dbVC == null) ? 0 : dbVC.hashCode());
		result = prime * result + (isMatch ? 1231 : 1237);
		result = prime * result + ((obsVC == null) ? 0 : obsVC.hashCode());
		result = prime * result + observedAllele;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GenotypeMatch other = (GenotypeMatch) obj;
		if (dbAllele != other.dbAllele) return false;
		if (dbVC == null) {
			if (other.dbVC != null) return false;
		} else if (!dbVC.equals(other.dbVC)) return false;
		if (isMatch != other.isMatch) return false;
		if (obsVC == null) {
			if (other.obsVC != null) return false;
		} else if (!obsVC.equals(other.obsVC)) return false;
		if (observedAllele != other.observedAllele) return false;
		return true;
	}

}
