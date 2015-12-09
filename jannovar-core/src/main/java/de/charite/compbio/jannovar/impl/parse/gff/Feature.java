package de.charite.compbio.jannovar.impl.parse.gff;

import java.util.HashMap;

/**
 * Class for representing one line out of a GFF file.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 */
public final class Feature {

	/** ID of the landmark used to establish the coordinate system for the current feature. (e.g. chromosom id) **/
	private String sequenceID = null;

	/**
	 * A free text qualifier intended to describe the algorithm or operating procedure that generated this feature.
	 * (e.g. "GenScan", "NCBI-Annotator")
	 **/
	private String source = null;

	/** The type of the feature. Constrained a term from the "lite" version of the Sequence Ontology **/
	private FeatureType type = null;

	/**
	 * Start coordinate of the feature. Given in positive 1-based integer coordinates, relative to the landmark given in
	 * {@link #sequenceID}.
	 **/
	private int start = -1;

	/**
	 * End coordinate of the feature. Given in positive 1-based integer coordinates, relative to the landmark given in
	 * {@link #sequenceID}.
	 **/
	private int end = -1;

	/** The score of the feature. **/
	private double score = 0.0;

	/**
	 * The strand of the feature. <code>true</code> for positive strand (relative to the {@link #sequenceID landmark}),
	 * <code>false</code> for minus strand.
	 **/
	private boolean strand = false;

	/** For features of {@link #type} 'CDS' this indicates the offset of the reading frame. **/
	private byte phase = 0;

	/** A List of feature attributes. Were the key is the tag. */
	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * @return the sequenceID
	 */
	public String getSequenceID() {
		return sequenceID;
	}

	/**
	 * @param sequenceID
	 *            the sequenceID to set
	 */
	public void setSequenceID(String sequenceID) {
		this.sequenceID = sequenceID;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the type
	 */
	public FeatureType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FeatureType type) {
		this.type = type;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the strand
	 */
	public boolean getStrand() {
		return strand;
	}

	/**
	 * @param strand
	 *            the strand to set
	 */
	public void setStrand(boolean strand) {
		this.strand = strand;
	}

	/**
	 * @return the phase
	 */
	public byte getPhase() {
		return phase;
	}

	/**
	 * @param phase
	 *            the phase to set
	 */
	public void setPhase(byte phase) {
		this.phase = phase;
	}

	/**
	 * Adds a new attribute to the list of attributes.
	 *
	 * If the attribute id already exists, it is String-appended. Multiple attributes of the same type are indicated by
	 * separating the values with the comma <code>","</code> character. There are some predefined meanings for the
	 * attribute tags:
	 *
	 * <pre>
	 * ID Name Alias Parent Target Gap Derives_from Note Dbref Ontology_term
	 * </pre>
	 *
	 * @param id
	 *            attribute ID
	 * @param value
	 *            attribute value
	 */
	public void addAttribute(String id, String value) {
		if (this.attributes.containsKey(id))
			value = this.attributes.get(id) + "," + value;
		this.attributes.put(id, value);
	}

	/**
	 * @return the attributes
	 */
	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Clear the attributes hash map.
	 */
	public void clearAttributes() {
		attributes.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Feature [sequence_id=").append(sequenceID).append(", source=").append(source).append(", type=")
				.append(type).append(", start=").append(start).append(", end=").append(end).append(", score=")
				.append(score).append(", strand=").append(strand).append(", phase=").append(phase)
				.append(", attributes=").append(attributes).append("]");
		return builder.toString();
	}

	/**
	 * @return {@link String} with the GFF representation of the {@link Feature}.
	 */
	public String toLine() {
		boolean wroteFirst = false;
		StringBuilder builder = new StringBuilder();
		builder.append(sequenceID).append("\t");
		builder.append(source).append("\t");
		builder.append(FeatureType.toString(type)).append("\t");
		builder.append(start).append("\t");
		builder.append(end).append("\t");
		builder.append((score != 0.0 ? score : ".")).append("\t");
		builder.append(strand ? "+" : "-").append("\t");
		builder.append(phase > -1 ? phase : ".").append("\t");
		for (String key : this.attributes.keySet()) {
			if (wroteFirst)
				builder.append(";");
			builder.append(key).append("=").append(this.attributes.get(key));
			wroteFirst = true;
		}

		return builder.toString();
	}
}
