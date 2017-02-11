package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * Disease-specific information in a {@link ClinVarAnnotation}
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarDiseaseInfo {
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
    final private String clinicalAccession;

    public ClinVarDiseaseInfo(ClinVarSignificance significance, String diseaseDB, String diseaseDBID,
            String diseaseDBName, ClinVarRevisionStatus revisionStatus, String clinicalAccession) {
        this.significance = significance;
        this.diseaseDB = diseaseDB;
        this.diseaseDBID = diseaseDBID;
        this.diseaseDBName = diseaseDBName;
        this.revisionStatus = revisionStatus;
        this.clinicalAccession = clinicalAccession;
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
        return clinicalAccession;
    }

    @Override
    public String toString() {
        return "ClinVarDiseaseInfo [significance=" + significance + ", diseaseDB=" + diseaseDB + ", diseaseDBID="
                + diseaseDBID + ", diseaseDBName=" + diseaseDBName + ", revisionStatus=" + revisionStatus
                + ", ClinicalAccession=" + clinicalAccession + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clinicalAccession == null) ? 0 : clinicalAccession.hashCode());
        result = prime * result + ((diseaseDB == null) ? 0 : diseaseDB.hashCode());
        result = prime * result + ((diseaseDBID == null) ? 0 : diseaseDBID.hashCode());
        result = prime * result + ((diseaseDBName == null) ? 0 : diseaseDBName.hashCode());
        result = prime * result + ((revisionStatus == null) ? 0 : revisionStatus.hashCode());
        result = prime * result + ((significance == null) ? 0 : significance.hashCode());
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
        ClinVarDiseaseInfo other = (ClinVarDiseaseInfo) obj;
        if (clinicalAccession == null) {
            if (other.clinicalAccession != null)
                return false;
        } else if (!clinicalAccession.equals(other.clinicalAccession))
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
        if (revisionStatus != other.revisionStatus)
            return false;
        if (significance != other.significance)
            return false;
        return true;
    }

}
