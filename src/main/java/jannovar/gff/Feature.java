/**
 * 
 */
package jannovar.gff;

import java.util.HashMap;

import jannovar.common.FeatureType;

/**
 * The Feature stores the data of one line in a GFT of GFF file.
 * The GFT format is an extension or flavor of the GFF format. They boith share the first
 * eight columns:<br>
 * Col.1: sequence ID<br>
 * Col.2: source<br>
 * Col.3: type<br>
 * Col.4 & 5: start and end - in 1-based integer coordinates<br>
 * Col.6: score<br>
 * Col.7: strand - '+' for the positive strand, '-' for the minus strand. For unknown strands a '?' can be stored<br>
 * Col.8: phase<br>
 * For features of type "CDS", the phase indicates where the feature begins with reference 
 * to the reading frame. The phase is one of the integers 0, 1, or 2, indicating the number 
 * of bases that should be removed from the beginning of this feature to reach the first 
 * base of the next codon.
 * @author mjaeger
 * @version 0.1
 */
public class Feature {

	/** ID of the landmark used to establish the coordinate system for the current feature. (e.g. chromosom id) **/
	String sequence_id;
	/** A free text qualifier intended to describe the algorithm or operating procedure that generated this feature. (e.g. "GenScan", "NCBI-Annotator") **/
	String source;
	/** The type of the feature. Constrained a term from the "lite" version of the Sequence Ontology **/
	FeatureType type;
	/** Start coordinate of the feature. Given in positive 1-based integer coordinates, relative to the landmark given in {@link #sequence_id}. **/
	int start;
	/** End coordinate of the feature. Given in positive 1-based integer coordinates, relative to the landmark given in {@link #sequence_id}. **/
	int end;
	/** The score of the feature. **/
	double score;
	/** The strand of the feature. <code>true</code> for positive strand (relative to the {@link #sequence_id landmark}), <code>false</code> for minus strand. **/
	boolean strand;
	/** For features of {@link #type} 'CDS' this indicates the offset of the reading frame. **/
	byte phase;
	/** A List of feature attributes. Were the key is the tag. **/
	HashMap<String, String> attributes;
	
	
	public Feature() {
		this.attributes = new HashMap<String, String>();
	}
	
	/**
	 * @return the sequence_id
	 */
	public String getSequence_id() {
		return sequence_id;
	}
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @return the type
	 */
	public FeatureType getType() {
		return type;
	}
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @return the strand
	 */
	public boolean isStrand() {
		return strand;
	}
	/**
	 * @return the strand
	 */
	public boolean getStrand() {
		return strand;
	}
	/**
	 * @return the phase
	 */
	public short getPhase() {
		return phase;
	}
	/**
	 * @return the attributes
	 */
	public HashMap<String, String> getAttributes() {
		return attributes;
	}
	/**
	 * @param sequence_id the sequence_id to set
	 */
	public void setSequence_id(String sequence_id) {
		this.sequence_id = sequence_id;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(FeatureType type) {
		this.type = type;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}
	/**
	 * @param strand the strand to set
	 */
	public void setStrand(boolean strand) {
		this.strand = strand;
	}
	/**
	 * @param phase the phase to set
	 */
	public void setPhase(byte phase) {
		this.phase = phase;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Adds a new attribute to the list of attributes.
	 * If the attribute id already exists, it is String appended.
	 * Multiple attributes of the same type are indicated by separating the values with the comma "," character. 
	 * There are some predefined meanings for the attribute tags:<br>
	 * ID
	 * Name
	 * Alias
	 * Parent
	 * Target
	 * Gap
	 * Derives_from
	 * Note
	 * Dbref
	 * Ontology_term 
	 * @param id - attribute ID
	 * @param value - attribute value
	 */
	public void addAttribute(String id, String value){
		if(this.attributes.containsKey(id))
			value = this.attributes.get(id)+","+value;
		
		this.attributes.put(id, value);
	}
	
	/**
	 * 
	 * @param id  The attribute ID
	 * @return the attribute corresponding to id.
	 */
	public String getAttribute(String id){
		return this.attributes.get(id);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Feature [sequence_id=" + sequence_id + ", source=" + source + ", type=" + type + ", start=" + start
				+ ", end=" + end + ", score=" + score + ", strand=" + strand + ", phase=" + phase + ", attributes="
				+ attributes + "]";
	}
	
	public String toLine(){
		boolean wroteFirst = false;
		StringBuffer buffy = new StringBuffer();
		buffy.append(sequence_id).append("\t");
		buffy.append(source).append("\t");
		buffy.append(FeatureType.toString(type)).append("\t");
		buffy.append(start).append("\t");
		buffy.append(end).append("\t");
		buffy.append((score != 0.0 ? score : ".")).append("\t");
		buffy.append(strand ? "+" : "-").append("\t");
		buffy.append(phase >-1 ? phase : ".").append("\t");
		for (String key : this.attributes.keySet()) {
			if(wroteFirst)
				buffy.append(";");
			buffy.append(key).append("=").append(this.attributes.get(key));
			wroteFirst = true;
		}
		
		return buffy.toString();
	}
}
