package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.util.Map;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Immutable class for describing a record from a GFF or GTF file.
 * 
 * When comparing, features on the forward strand come before features on the reverse strand.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class FeatureRecord implements Comparable<FeatureRecord> {

	/** Type for describing record strand */
	public enum Strand {
		/** Record is on forward strand */
		FORWARD,
		/** Record is on reverse strand */
		REVERSE
	}

	/** Sequence ID of the record */
	private final String seqID;

	/** Source of the record */
	private final String source;

	/** Type of the record */
	private final String type;

	/** 0-based begin position of the record */
	private final int begin;

	/** End position of the record */
	private final int end;

	/** Score value of the record */
	private final String score;

	/** Strand of the record */
	private final Strand strand;

	/** Record's phase, 0, 1, or 2 */
	private final int phase;

	/** Record's attributes */
	private ImmutableMap<String, String> attributes;

	/**
	 * Initialize the <code>GFFRecord</code>
	 */
	public FeatureRecord(String seqID, String source, String type, int begin, int end, String score, Strand strand,
			int phase, Map<String, String> attributes) {
		this.seqID = seqID;
		this.source = source;
		this.type = type;
		this.begin = begin;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attributes = ImmutableMap.copyOf(attributes);
	}

	public ImmutableMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(ImmutableMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getSeqID() {
		return seqID;
	}

	public String getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getScore() {
		return score;
	}

	public Strand getStrand() {
		return strand;
	}

	public int getPhase() {
		return phase;
	}

	@Override
	public String toString() {
		return "FeatureRecord [seqID=" + seqID + ", source=" + source + ", type=" + type + ", begin=" + begin + ", end="
				+ end + ", score=" + score + ", strand=" + strand + ", phase=" + phase + ", attributes="
				+ ImmutableSortedMap.copyOf(attributes) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + phase;
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		result = prime * result + ((seqID == null) ? 0 : seqID.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((strand == null) ? 0 : strand.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		FeatureRecord other = (FeatureRecord) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		if (phase != other.phase)
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		if (seqID == null) {
			if (other.seqID != null)
				return false;
		} else if (!seqID.equals(other.seqID))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (strand != other.strand)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public int compareTo(FeatureRecord o) {
		return ComparisonChain.start().compare(seqID, o.seqID).compare(strand, o.strand).compare(begin, o.begin)
				.compare(end, o.end).result();
	}

}
