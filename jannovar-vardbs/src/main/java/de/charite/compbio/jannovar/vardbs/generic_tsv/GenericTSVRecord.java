package de.charite.compbio.jannovar.vardbs.generic_tsv;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Store entries for one column of a generic TSV file.
 * 
 * Here, we currently only have support for one alternative allele. This reflects more dbNSFP or the
 * CADD score files than VCF files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVRecord {

	/** Name of the chromosome */
	final private String contig;

	/** Position of the variant, 0-based */
	final private int pos;

	/**
	 * Reference sequence, <code>null</code> if locations are annotated instead of variants.
	 */
	final private String ref;
	/**
	 * Alternative allele in record, <code>null</code> if locations are annotated instead of
	 * variants.
	 */
	final private String alt;

	/** Values for each value column from configuration. */
	final private ImmutableList<Object> values;

	public GenericTSVRecord(String contig, int pos, String ref, String alt, List<Object> values) {
		this.contig = contig;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
		this.values = ImmutableList.copyOf(values);
	}

	public String getContig() {
		return contig;
	}

	public int getPos() {
		return pos;
	}

	public String getRef() {
		return ref;
	}

	public String getAlt() {
		return alt;
	}

	public List<Object> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "GenericTSVRecord [chrom=" + contig + ", pos=" + pos + ", ref=" + ref + ", alt=" + alt + ", values="
				+ values + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((contig == null) ? 0 : contig.hashCode());
		result = prime * result + pos;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		GenericTSVRecord other = (GenericTSVRecord) obj;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (contig == null) {
			if (other.contig != null)
				return false;
		} else if (!contig.equals(other.contig))
			return false;
		if (pos != other.pos)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

}
