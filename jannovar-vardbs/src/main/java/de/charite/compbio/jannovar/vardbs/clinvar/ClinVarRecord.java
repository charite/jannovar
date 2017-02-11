package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

/**
 * Represents on entry in the Clinvar VCF database file
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarRecord {

    // Fields up to the INFO column

    /** Name of the chromosome */
    final private String chrom;
    /** Position of the variant, 0-based */
    final private int pos;
    /** ID of the variant */
    final private String id;
    /** Reference sequence */
    final private String ref;
    /** Alternative alleles in cluster */
    final private ImmutableList<String> alt;
    // TODO: enable the following two settings as well
    /** Whether or not there is an OMIM/OMIA annotation */
    // final private boolean hasOmim;
    /** Whether or not it is validated */
    // final private boolean isValidated;

    // Entries of the INFO column

    /** Annotations, by index of reference */
    final private ImmutableListMultimap<Integer, ClinVarAnnotation> annotations;

    public ClinVarRecord(String chrom, int pos, String id, String ref, List<String> alt,
            Collection<ClinVarAnnotation> annotations) {
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = ImmutableList.copyOf(alt);

        ImmutableListMultimap.Builder<Integer, ClinVarAnnotation> builder = ImmutableListMultimap.builder();
        for (ClinVarAnnotation anno : annotations)
            builder.put(anno.getAlleleMapping(), anno);
        this.annotations = builder.build();
    }

    public String getChrom() {
        return chrom;
    }

    public int getPos() {
        return pos;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public ImmutableList<String> getAlt() {
        return alt;
    }

    public ImmutableListMultimap<Integer, ClinVarAnnotation> getAnnotations() {
        return annotations;
    }

    @Override
    public String toString() {
        return "ClinVarRecord [chrom=" + chrom + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
                + ", annotations=" + annotations + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alt == null) ? 0 : alt.hashCode());
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
        result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + pos;
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
        ClinVarRecord other = (ClinVarRecord) obj;
        if (alt == null) {
            if (other.alt != null)
                return false;
        } else if (!alt.equals(other.alt))
            return false;
        if (annotations == null) {
            if (other.annotations != null)
                return false;
        } else if (!annotations.equals(other.annotations))
            return false;
        if (chrom == null) {
            if (other.chrom != null)
                return false;
        } else if (!chrom.equals(other.chrom))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (pos != other.pos)
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        return true;
    }

}
