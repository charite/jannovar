package de.charite.compbio.jannovar.vardbs.generic_tsv;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for building {@link GenericTSVRecord} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVRecordBuilder {

	private String contig;
	private int pos;
	private String ref;
	private String alt;
	private ArrayList<Object> values;

	public GenericTSVRecordBuilder() {
		this.contig = null;
		this.pos = -1;
		this.ref = null;
		this.alt = null;
		this.values = new ArrayList<>();
	}

	public GenericTSVRecord build() {
		return new GenericTSVRecord(contig, pos, ref, alt, values);
	}

	public String getContig() {
		return contig;
	}

	public void setContig(String contig) {
		this.contig = contig;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public ArrayList<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = new ArrayList<>(values);
	}

	@Override
	public String toString() {
		return "GenericTSVRecordBuilder [contig=" + contig + ", pos=" + pos
				+ ", ref=" + ref + ", alt=" + alt + ", values=" + values + "]";
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
		GenericTSVRecordBuilder other = (GenericTSVRecordBuilder) obj;
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
