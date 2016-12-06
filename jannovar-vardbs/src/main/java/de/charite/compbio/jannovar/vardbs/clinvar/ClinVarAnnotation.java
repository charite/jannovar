package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * One annotation entry
 * 
 * One allele can have multiple annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotation {

	/** Mapping to allele, -1 is no mapping, 0 is reference, 1 is first alt */
	final private String hgvsVariant;

	/** HGVS variant string */
	final private int alleleMapping;

	/** ID of annotation source */
	final private String source;

	/** Origin of the annotation */
	final private ClinVarOrigin origin;

	/** Significance level */
	final private ClinVarSignificance significance;

	/** Name of disease DB used */
	final private String diseaseDB;

	/** ID of disease in DB */
	final private String diseaseDBID;

	/** Name of disease in DB */
	final private String diseaseDBName;

	/** Revision status of the variant */
	final private ClinVarRevisionStatus revisionStatus;

	/** Clinical accession of the variant */
	final private String ClinicalAccession;

	public ClinVarAnnotation(String hgvsVariant, int alleleMapping, String source, ClinVarOrigin origin,
			ClinVarSignificance significance, String diseaseDB, String diseaseDBID, String diseaseDBName,
			ClinVarRevisionStatus revisionStatus, String clinicalAccession) {
		super();
		this.hgvsVariant = hgvsVariant;
		this.alleleMapping = alleleMapping;
		this.source = source;
		this.origin = origin;
		this.significance = significance;
		this.diseaseDB = diseaseDB;
		this.diseaseDBID = diseaseDBID;
		this.diseaseDBName = diseaseDBName;
		this.revisionStatus = revisionStatus;
		ClinicalAccession = clinicalAccession;
	}

	public String getHgvsVariant() {
		return hgvsVariant;
	}

	public int getAlleleMapping() {
		return alleleMapping;
	}

	public String getSource() {
		return source;
	}

	public ClinVarOrigin getOrigin() {
		return origin;
	}

	public ClinVarSignificance getSignificance() {
		return significance;
	}

	public String getDiseaseDB() {
		return diseaseDB;
	}

	public String getDiseaseDBID() {
		return diseaseDBID;
	}

	public String getDiseaseDBName() {
		return diseaseDBName;
	}

	public ClinVarRevisionStatus getRevisionStatus() {
		return revisionStatus;
	}

	public String getClinicalAccession() {
		return ClinicalAccession;
	}

	@Override
	public String toString() {
		return "ClinVarAnnotation [hgvsVariant=" + hgvsVariant + ", alleleMapping=" + alleleMapping + ", source=" + source
				+ ", origin=" + origin + ", significance=" + significance + ", diseaseDB=" + diseaseDB
				+ ", diseaseDBID=" + diseaseDBID + ", diseaseDBName=" + diseaseDBName + ", revisionStatus="
				+ revisionStatus + ", ClinicalAccession=" + ClinicalAccession + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ClinicalAccession == null) ? 0 : ClinicalAccession.hashCode());
		result = prime * result + alleleMapping;
		result = prime * result + ((diseaseDB == null) ? 0 : diseaseDB.hashCode());
		result = prime * result + ((diseaseDBID == null) ? 0 : diseaseDBID.hashCode());
		result = prime * result + ((diseaseDBName == null) ? 0 : diseaseDBName.hashCode());
		result = prime * result + ((hgvsVariant == null) ? 0 : hgvsVariant.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((revisionStatus == null) ? 0 : revisionStatus.hashCode());
		result = prime * result + ((significance == null) ? 0 : significance.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		ClinVarAnnotation other = (ClinVarAnnotation) obj;
		if (ClinicalAccession == null) {
			if (other.ClinicalAccession != null)
				return false;
		} else if (!ClinicalAccession.equals(other.ClinicalAccession))
			return false;
		if (alleleMapping != other.alleleMapping)
			return false;
		if (diseaseDB == null) {
			if (other.diseaseDB != null)
				return false;
		} else if (!diseaseDB.equals(other.diseaseDB))
			return false;
		if (diseaseDBID == null) {
			if (other.diseaseDBID != null)
				return false;
		} else if (!diseaseDBID.equals(other.diseaseDBID))
			return false;
		if (diseaseDBName == null) {
			if (other.diseaseDBName != null)
				return false;
		} else if (!diseaseDBName.equals(other.diseaseDBName))
			return false;
		if (hgvsVariant == null) {
			if (other.hgvsVariant != null)
				return false;
		} else if (!hgvsVariant.equals(other.hgvsVariant))
			return false;
		if (origin != other.origin)
			return false;
		if (revisionStatus != other.revisionStatus)
			return false;
		if (significance != other.significance)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

}
