package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * Builder for {@link ClinVarAnnotation}
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotationBuilder {

	/** Mapping to allele, -1 is no mapping, 0 is reference, 1 is first alt */
	private String hgvsVariant;

	/** HGVS variant string */
	private int alleleMapping;

	/** ID of annotation source */
	private String source;

	/** Origin of the annotation */
	private ClinVarOrigin origin;

	/** Significance level */
	private ClinVarSignificance significance;

	/** Name of disease DB used */
	private String diseaseDB;

	/** ID of disease in DB */
	private String diseaseDBID;

	/** Name of disease in DB */
	private String diseaseDBName;

	/** Revision status of the variant */
	private ClinVarRevisionStatus revisionStatus;

	/** Clinical accession of the variant */
	private String clinicalAccession;

	public ClinVarAnnotationBuilder() {
		hgvsVariant = null;
		alleleMapping = -1;
		source = null;
		origin = null;
		significance = ClinVarSignificance.UNCERTAIN;
		diseaseDB = null;
		diseaseDBID = null;
		diseaseDBName = null;
		revisionStatus = ClinVarRevisionStatus.NO_CRITERIA;
		clinicalAccession = null;
	}

	public ClinVarAnnotation build() {
		return new ClinVarAnnotation(hgvsVariant, alleleMapping, source, origin, significance, diseaseDB, diseaseDBID,
				diseaseDBName, revisionStatus, clinicalAccession);
	}

	public String getHgvsVariant() {
		return hgvsVariant;
	}

	public void setHgvsVariant(String hvsVariant) {
		this.hgvsVariant = hvsVariant;
	}

	public int getAlleleMapping() {
		return alleleMapping;
	}

	public void setAlleleMapping(int alleleMapping) {
		this.alleleMapping = alleleMapping;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ClinVarOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(ClinVarOrigin origin) {
		this.origin = origin;
	}

	public ClinVarSignificance getSignificance() {
		return significance;
	}

	public void setSignificance(ClinVarSignificance significance) {
		this.significance = significance;
	}

	public String getDiseaseDB() {
		return diseaseDB;
	}

	public void setDiseaseDB(String diseaseDB) {
		this.diseaseDB = diseaseDB;
	}

	public String getDiseaseDBID() {
		return diseaseDBID;
	}

	public void setDiseaseDBID(String diseaseDBID) {
		this.diseaseDBID = diseaseDBID;
	}

	public String getDiseaseDBName() {
		return diseaseDBName;
	}

	public void setDiseaseDBName(String diseaseDBName) {
		this.diseaseDBName = diseaseDBName;
	}

	public ClinVarRevisionStatus getRevisionStatus() {
		return revisionStatus;
	}

	public void setRevisionStatus(ClinVarRevisionStatus revisionStatus) {
		this.revisionStatus = revisionStatus;
	}

	public String getclinicalAccession() {
		return clinicalAccession;
	}

	public void setClinicalAccession(String clinicalAccession) {
		this.clinicalAccession = clinicalAccession;
	}

	@Override
	public String toString() {
		return "ClinVarAnnotationBuilder [hvsVariant=" + hgvsVariant + ", alleleMapping=" + alleleMapping + ", source="
				+ source + ", origin=" + origin + ", significance=" + significance + ", diseaseDB=" + diseaseDB
				+ ", diseaseDBID=" + diseaseDBID + ", diseaseDBName=" + diseaseDBName + ", revisionStatus="
				+ revisionStatus + ", clinicalAccession=" + clinicalAccession + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clinicalAccession == null) ? 0 : clinicalAccession.hashCode());
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
		ClinVarAnnotationBuilder other = (ClinVarAnnotationBuilder) obj;
		if (clinicalAccession == null) {
			if (other.clinicalAccession != null)
				return false;
		} else if (!clinicalAccession.equals(other.clinicalAccession))
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
