package de.charite.compbio.jannovar.vardbs.clinvar;

/**
 * Information about the source of a variant in a {@link ClinVarAnnotation}
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarSourceInfo {

    /** Datababase name */
    private final String dbName;
    
    /** ID of variant in DB */
    private final String dbId;

    public ClinVarSourceInfo(String dbName, String dbId) {
        super();
        this.dbName = dbName;
        this.dbId = dbId;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbId() {
        return dbId;
    }

    @Override
    public String toString() {
        return "ClinVarAnnotationSourceInfo [dbName=" + dbName + ", dbId=" + dbId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
        result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
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
        ClinVarSourceInfo other = (ClinVarSourceInfo) obj;
        if (dbId == null) {
            if (other.dbId != null)
                return false;
        } else if (!dbId.equals(other.dbId))
            return false;
        if (dbName == null) {
            if (other.dbName != null)
                return false;
        } else if (!dbName.equals(other.dbName))
            return false;
        return true;
    }

}
