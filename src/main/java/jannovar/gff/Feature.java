package jannovar.gff;

import jannovar.common.FeatureType;
import jannovar.common.Immutable;

import com.google.common.collect.ImmutableMap;

/**
 * Immutable data for one line of a GTF/GFF file.
 *
 * The Feature stores the data of one line in a GTF of GFF file. The GTF format is an extension or flavor of the GFF
 * format. They both share the first eight columns:
 *
 * <ul>
 * <li>Col.1: sequence ID</li>
 * <li>Col.2: source</li>
 * <li>Col.3: type</li>
 * <li>Col.4 & 5: start and end - in 1-based integer coordinates</li>
 * <li>Col.6: score</li>
 * <li>Col.7: strand - '+' for the positive strand, '-' for the minus strand. For unknown strands a '?' can be stored</li>
 * <li>Col.8: phase</li>
 * </ul>
 *
 * For features of type "CDS", the phase indicates where the feature begins with reference to the reading frame. The
 * phase is one of the integers 0, 1, or 2, indicating the number of bases that should be removed from the beginning of
 * this feature to reach the first base of the next codon.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class Feature {

	/** ID of the landmark used to establish the coordinate system for the current feature. (e.g. chromosom id) **/
	public final String sequenceID;

	/**
	 * A free text qualifier intended to describe the algorithm or operating procedure that generated this feature.
	 * (e.g. "GenScan", "NCBI-Annotator")
	 **/
	public final String source;

	/** The type of the feature. Constrained a term from the "lite" version of the Sequence Ontology **/
	public final FeatureType type;

	/**
	 * Start coordinate of the feature. Given in positive 1-based integer coordinates, relative to the landmark given in
	 * {@link #sequenceID}.
	 **/
	public final int start;

	/**
	 * End coordinate of the feature. Given in positive 1-based integer coordinates, relative to the landmark given in
	 * {@link #sequenceID}.
	 **/
	public final int end;

	/** The score of the feature. **/
	public final double score;

	/**
	 * The strand of the feature. <code>true</code> for positive strand (relative to the {@link #sequenceID landmark}),
	 * <code>false</code> for minus strand.
	 **/
	public final boolean strand;

	/** For features of {@link #type} 'CDS' this indicates the offset of the reading frame. **/
	public final byte phase;

	/** A List of feature attributes. Were the key is the tag. */
	public final ImmutableMap<String, String> attributes;

	public Feature(String sequenceID, String source, FeatureType type, int start, int end, double score,
			boolean strand, byte phase, ImmutableMap<String, String> attributes) {
		this.sequenceID = sequenceID;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attributes = attributes;
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
