package jannovar.gff;

import jannovar.common.FeatureType;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Class for building immutable {@link Feature} objects field-by-field.
 *
 * In this sense, it is similar to {@link StringBuilder} for building {@link String} objects.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public final class FeatureBuilder {
	/** {@link Feature#sequenceID} of next generated {@link Feature} */
	private String sequenceID = null;

	/** {@link Feature#source} of next generated {@link Feature} */
	private String source = null;

	/** {@link Feature#type} of next generated {@link Feature} */
	private FeatureType type = null;

	/** {@link Feature#start} of next generated {@link Feature} */
	private int start = -1;

	/** {@link Feature#end} of next generated {@link Feature} */
	private int end = -1;

	/** {@link Feature#score} of next generated {@link Feature} */
	private double score = 0.0;

	/** {@link Feature#strand} of next generated {@link Feature} */
	private boolean strand = false;

	/** {@link Feature#phase} of next generated {@link Feature} */
	private byte phase = 0;

	/** {@link Feature#attributes} of next generated {@link Feature} */
	private HashMap<String, String> attributes = new HashMap<String, String>();

	/** Reset builder to initial state. */
	public void reset() {
		sequenceID = null;
		source = null;
		type = null;
		start = -1;
		end = -1;
		score = 0.0;
		strand = false;
		phase = 0;
		clearAttributes();
	}

	/** @return {@link Feature} with the currently set values. */
	public Feature make() {
		return new Feature(sequenceID, source, type, start, end, score, strand, phase,
				buildImmutableAttributes(attributes));
	}

	/** @return {@link ImmutableMap} version of {@link #attributes} */
	private ImmutableMap<String, String> buildImmutableAttributes(HashMap<String, String> attributes) {
		ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
		for (Map.Entry<String, String> entry : attributes.entrySet())
			builder.put(entry.getKey(), entry.getValue());
		return builder.build();
	}

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
	public boolean isStrand() {
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
}
