package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * One annotation entry
 * 
 * One allele can have multiple annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotation {

    /** Mapping to allele, -1 is no mapping, 0 is reference, 1 is first alt */
    final private int alleleMapping;

    /** HGVS variant string */
    final private String hgvsVariant;

    /** Clinvar variant source */
    final private ImmutableList<ClinVarSourceInfo> sourceInfos;

    /** Origin of the annotation */
    final private ImmutableList<ClinVarOrigin> origin;

    /** Clinvar disease db informations */
    final private ImmutableList<ClinVarDiseaseInfo> diseaseInfos;

    public ClinVarAnnotation(String hgvsVariant, int alleleMapping, Collection<ClinVarSourceInfo> sourceInfos,
            Collection<ClinVarOrigin> origin, Collection<ClinVarDiseaseInfo> diseaseInfos) {
        super();
        this.hgvsVariant = hgvsVariant;
        this.alleleMapping = alleleMapping;
        this.sourceInfos = ImmutableList.copyOf(sourceInfos);
        this.origin = ImmutableList.copyOf(origin);
        this.diseaseInfos = ImmutableList.copyOf(diseaseInfos);
    }

    public int getAlleleMapping() {
        return alleleMapping;
    }

    public String getHgvsVariant() {
        return hgvsVariant;
    }

    public ImmutableList<ClinVarSourceInfo> getSourceInfos() {
        return sourceInfos;
    }

    public ImmutableList<ClinVarOrigin> getOrigin() {
        return origin;
    }

    public ImmutableList<ClinVarDiseaseInfo> getDiseaseInfos() {
        return diseaseInfos;
    }

    @Override
    public String toString() {
        return "ClinVarAnnotation [alleleMapping=" + alleleMapping + ", hgvsVariant=" + hgvsVariant + ", sourceInfos="
                + sourceInfos + ", origin=" + origin + ", diseaseInfos=" + diseaseInfos + "]";
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
        ClinVarAnnotation other = (ClinVarAnnotation) obj;
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
