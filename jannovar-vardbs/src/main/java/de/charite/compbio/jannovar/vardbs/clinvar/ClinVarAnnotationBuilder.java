package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.ArrayList;
import java.util.List;

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

    /** Origin of the annotation */
    private List<ClinVarOrigin> origin;

    /** Variant source information */
    private List<ClinVarSourceInfo> sourceInfos;

    /** Disease information */
    private List<ClinVarDiseaseInfo> diseaseInfos;

    public ClinVarAnnotationBuilder() {
        hgvsVariant = null;
        alleleMapping = -1;
        origin = new ArrayList<>();
        sourceInfos = new ArrayList<>();
        diseaseInfos = new ArrayList<>();
    }

    public ClinVarAnnotation build() {
        return new ClinVarAnnotation(hgvsVariant, alleleMapping, sourceInfos, origin, diseaseInfos);
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

    public List<ClinVarOrigin> getOrigin() {
        return origin;
    }

    public void setOrigin(List<ClinVarOrigin> origin) {
        this.origin = origin;
    }

    public List<ClinVarSourceInfo> getSourceInfos() {
        return sourceInfos;
    }

    public void setSourceInfos(List<ClinVarSourceInfo> sourceInfos) {
        this.sourceInfos = sourceInfos;
    }

    public List<ClinVarDiseaseInfo> getDiseaseInfos() {
        return diseaseInfos;
    }

    public void setDiseaseInfos(List<ClinVarDiseaseInfo> diseaseInfos) {
        this.diseaseInfos = diseaseInfos;
    }

    @Override
    public String toString() {
        return "ClinVarAnnotationBuilder [hgvsVariant=" + hgvsVariant + ", alleleMapping=" + alleleMapping + ", origin="
                + origin + ", sourceInfos=" + sourceInfos + ", diseaseInfos=" + diseaseInfos + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + alleleMapping;
        result = prime * result + ((diseaseInfos == null) ? 0 : diseaseInfos.hashCode());
        result = prime * result + ((hgvsVariant == null) ? 0 : hgvsVariant.hashCode());
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + ((sourceInfos == null) ? 0 : sourceInfos.hashCode());
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
        if (alleleMapping != other.alleleMapping)
            return false;
        if (diseaseInfos == null) {
            if (other.diseaseInfos != null)
                return false;
        } else if (!diseaseInfos.equals(other.diseaseInfos))
            return false;
        if (hgvsVariant == null) {
            if (other.hgvsVariant != null)
                return false;
        } else if (!hgvsVariant.equals(other.hgvsVariant))
            return false;
        if (origin == null) {
            if (other.origin != null)
                return false;
        } else if (!origin.equals(other.origin))
            return false;
        if (sourceInfos == null) {
            if (other.sourceInfos != null)
                return false;
        } else if (!sourceInfos.equals(other.sourceInfos))
            return false;
        return true;
    }

}
