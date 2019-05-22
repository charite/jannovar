package de.charite.compbio.jannovar.reference;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for building immutable {@link TranscriptModel} objects field-by-field.
 * <p>
 * In this sense, it is similar to {@link StringBuilder} for building {@link String} objects.
 * <p>
 * Usage:
 *
 * <pre>
 * {@link TranscriptModelBuilder} builder = new TranscriptModelBuilder();
 * builder.{@link TranscriptModelBuilder#setStrand setStrand}('-');
 * builder.{@link TranscriptModelBuilder#setAccession setAccession}(&quot;&lt;accession&gt;&quot;);
 * // ...
 * {@link TranscriptModel} transcript = builder.{@link TranscriptModelBuilder#build build}();
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class TranscriptModelBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(TranscriptModelBuilder.class);

	/**
	 * The explicit strand of the target transcript.
	 */
	private Strand strand = Strand.FWD;

	/**
	 * {@code accession} of next {@link TranscriptModel} to build.
	 */
	private String accession = null;

	/**
	 * Optional transcript version.  The accession must be the same as in the transcript FASTA file.  Specifying
	 * the version separately here allows us to store it from the GTF/GFF and then later add it to the accession.
	 */
	private String txVersion = null;

	/**
	 * {@code geneSymbol} of next {@link TranscriptModel} to build.
	 */
	private String geneSymbol = null;

	/**
	 * {@code txRegion} of next {@link TranscriptModel} to build.
	 */
	private GenomeInterval txRegion = null;

	/**
	 * {@code cdsRegion} of next {@link TranscriptModel} to build.
	 */
	private GenomeInterval cdsRegion = null;

	/**
	 * {@code exonRegions} of next {@link TranscriptModel} to build.
	 */
	private ArrayList<GenomeInterval> exonRegions = new ArrayList<GenomeInterval>();

	/**
	 * {@code accession} of next {@link TranscriptModel} to build.
	 */
	private String sequence = null;

	/**
	 * {@code geneID} of next {@link TranscriptModel} to build.
	 */
	private String geneID = null;

	/**
	 * Optional gene version.
	 */
	private String geneVersion = null;

	/**
	 * Map with alternative gene IDs
	 */
	private HashMap<String, String> altGeneIDs = new HashMap<String, String>();

	/**
	 * {@code transcriptSupportLevel} of next {@link TranscriptModel} to build.
	 *
	 * @see TranscriptSupportLevels
	 */
	private int transcriptSupportLevel = TranscriptSupportLevels.NOT_AVAILABLE;

	/**
	 * The alignment to use put into the {@link TranscriptModel}.
	 */
	private Alignment seqAlignment = null;

	/**
	 * The alignment parts.
	 */
	private ArrayList<AlignmentPart> alignmentParts = new ArrayList<>();

	/**
	 * Whether has mismatches.
	 */
	private boolean hasSubstitutions = false;

	/**
	 * Whether has indels.
	 */
	private boolean hasIndels = false;

	/**
	 * Reset the builder into the state after initialization.
	 */
	public void reset() {
		strand = Strand.FWD;
		accession = null;
		txVersion = null;
		geneSymbol = null;
		txRegion = null;
		cdsRegion = null;
		exonRegions.clear();
		sequence = null;
		geneID = null;
		geneVersion = null;
		altGeneIDs.clear();
		transcriptSupportLevel = TranscriptSupportLevels.NOT_AVAILABLE;
		seqAlignment = null;
		hasSubstitutions = false;
		hasIndels = false;
		alignmentParts.clear();
	}

	/**
	 * @return {@link TranscriptModel} with the currently set configuration.
	 */
	public TranscriptModel build() {
		// Build list of immutable exons in the correct order.
		int exonLengthSum = 0;
		ImmutableSortedSet.Builder<GenomeInterval> builder = ImmutableSortedSet.<GenomeInterval>naturalOrder();
		if (exonRegions.size() > 0) {
			if (strand == exonRegions.get(0).getStrand()) {
				for (int i = 0; i < exonRegions.size(); ++i) {
					builder.add(exonRegions.get(i));
					exonLengthSum += exonRegions.get(i).length();
				}
			} else {
				for (int i = 0, j = exonRegions.size() - 1; i < exonRegions.size(); ++i, --j) {
					builder.add(exonRegions.get(j).withStrand(strand));
					exonLengthSum += exonRegions.get(j).length();
				}
			}
		}

		// Build full accession with version if set.
		String fullAccession = accession;
		if (this.txVersion != null) {
			fullAccession += "." + this.txVersion;
		}

		// Build full gene ID with version if set.
		String fullGeneID = geneID;
		if (this.geneVersion != null) {
			fullGeneID += "." + this.geneVersion;
		}

		if (seqAlignment == null) {
			if (!alignmentParts.isEmpty()) {
				seqAlignment = buildAlignment();
			} else {
				seqAlignment = Alignment.createUngappedAlignment(exonLengthSum);
			}
		}

		// Create new TranscriptModel object.
		return new TranscriptModel(fullAccession, geneSymbol, txRegion.withStrand(strand),
			cdsRegion.withStrand(strand), ImmutableList.copyOf(builder.build()), sequence,
			fullGeneID, transcriptSupportLevel, hasSubstitutions, hasIndels, altGeneIDs, seqAlignment);
	}

	/**
	 * Build the {@link Alignment} from the {@link #alignmentParts}.
	 */
	private Alignment buildAlignment() {
		int exonLengthSum = 0;
		for (int i = 0; i < exonRegions.size(); ++i) {
			exonLengthSum += exonRegions.get(i).length();
		}

		// Check that alignmentParts matches exonRegions.
		if (alignmentParts.size() != exonRegions.size()) {
			LOGGER.warn("Number of alignment parts ({}) != number of exons ({}) for {}",
				new Object[] { alignmentParts.size(), exonRegions.size(), accession });
			return Alignment.createUngappedAlignment(exonLengthSum);
		}
		for (int i = 0; i < alignmentParts.size(); ++i) {
			final GenomeInterval exonRegion = exonRegions.get(i).withStrand(Strand.FWD);
			if (alignmentParts.get(i).refBeginPos != exonRegion.getBeginPos()
				|| alignmentParts.get(i).refEndPos != exonRegion.getEndPos()) {
				LOGGER.warn("Exon {} does not match alignment part {} (i={}, tx={})",
					new Object[] { alignmentParts.get(i), exonRegions.get(i), i, accession });
				return Alignment.createUngappedAlignment(exonLengthSum);
			}
		}

		// Build the anchors of the alignment of the transcript to the transcript sequence.
		final List<Anchor> refAnchors = Lists.newArrayList(new Anchor(0, 0));
		final List<Anchor> qryAnchors = Lists.newArrayList(new Anchor(0, 0));
		String prevGapEntry = null;
		for (int i = 0; i < alignmentParts.size(); ++i) {
			final AlignmentPart alignmentPart = alignmentParts.get(i);
			Anchor prevRefAnchor = refAnchors.get(refAnchors.size() - 1);
			Anchor prevQryAnchor = qryAnchors.get(qryAnchors.size() - 1);

			if (prevQryAnchor.getSeqPos() > alignmentPart.txBeginPos) {
				throw new RuntimeException("Local alignments overlap in sequence");
			} else if (prevQryAnchor.getSeqPos() < alignmentPart.txBeginPos) {
				// Insertion of sequence (and into the query) with respect to the transcript (reference).
				final int skipped = alignmentPart.txBeginPos - prevQryAnchor.getSeqPos();
				refAnchors.add(prevRefAnchor.withDeltas(skipped, 0));
				qryAnchors.add(prevQryAnchor.withDeltas(skipped, skipped));
				prevRefAnchor = refAnchors.get(refAnchors.size() - 1);
				prevQryAnchor = qryAnchors.get(qryAnchors.size() - 1);
			}

			for (final String gapEntry: alignmentPart.gap.split(" ")) {
				final char operation = gapEntry.charAt(0);
				final int count = Integer.parseInt(gapEntry.substring(1));

				switch (operation) {
				case 'M':
					if (prevGapEntry == null || !prevGapEntry.startsWith("M")) {
						refAnchors.add(prevRefAnchor.withDeltas(count, count));
						qryAnchors.add(prevQryAnchor.withDeltas(count, count));
					} else {
						// Extend previous "(M)atch block."
						refAnchors.set(refAnchors.size() - 1, prevRefAnchor.withDeltas(count, count));
						qryAnchors.set(qryAnchors.size() - 1, prevQryAnchor.withDeltas(count, count));
					}
					break;
				case 'I':
					refAnchors.add(prevRefAnchor.withDeltas(count, 0));
					qryAnchors.add(prevQryAnchor.withDeltas(count, count));
					break;
				case 'D':
					refAnchors.add(prevRefAnchor.withDeltas(count, count));
					qryAnchors.add(prevQryAnchor.withDeltas(count, 0));
					break;
				default:
					throw new RuntimeException("Unexpected operation: " + gapEntry);
				}

				prevRefAnchor = refAnchors.get(refAnchors.size() - 1);
				prevQryAnchor = qryAnchors.get(qryAnchors.size() - 1);
				prevGapEntry = gapEntry;
			}
		}

		return new Alignment(refAnchors, qryAnchors);
	}

	/**
	 * @return the strand
	 */
	public Strand getStrand() {
		return strand;
	}

	/**
	 * @param strand the strand to set
	 */
	public void setStrand(Strand strand) {
		this.strand = strand;
	}

	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * @param txVersion the version to set
	 */
	public void setTxVersion(String txVersion) {
		this.txVersion = txVersion;
	}

	/**
	 * @return the transcript version
	 */
	public String getTxVersion() {
		return txVersion;
	}

	/**
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}

	/**
	 * @return the geneSymbol
	 */
	public String getGeneSymbol() {
		return geneSymbol;
	}

	/**
	 * @param geneSymbol the geneSymbol to set
	 */
	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	/**
	 * @return the txRegion
	 */
	public GenomeInterval getTXRegion() {
		return txRegion;
	}

	/**
	 * @param txRegion the txRegion to set
	 */
	public void setTXRegion(GenomeInterval txRegion) {
		this.txRegion = txRegion;
	}

	/**
	 * @return the cdsRegion
	 */
	public GenomeInterval getCDSRegion() {
		return cdsRegion;
	}

	/**
	 * @param cdsRegion the cdsRegion to set
	 */
	public void setCDSRegion(GenomeInterval cdsRegion) {
		this.cdsRegion = cdsRegion;
	}

	/**
	 * @return the exonRegions
	 */
	public ArrayList<GenomeInterval> getExonRegions() {
		return exonRegions;
	}

	/**
	 * Clear exon regions list
	 */
	public void clearExonRegions() {
		this.exonRegions.clear();
	}

	/**
	 * @return alternative gene Ids
	 */
	public HashMap<String, String> getAltGeneIDs() {
		return altGeneIDs;
	}

	/**
	 * Clear alternative geneIDs map
	 */
	public void clearAltGeneIDs() {
		this.altGeneIDs.clear();
	}

	/**
	 * @param exonRegion interval to append
	 */
	public void addExonRegion(GenomeInterval exonRegion) {
		this.exonRegions.add(exonRegion);
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the geneID
	 */
	public String getGeneID() {
		return geneID;
	}

	/**
	 * @param geneVersion the geneVersion to set
	 */
	public void setGeneVersion(String geneVersion) {
		this.geneVersion = geneVersion;
	}


	/**
	 * @return the geneVersion
	 */
	public String getGeneVersion() {
		return geneVersion;
	}

	/**
	 * @param geneID the geneID to set
	 */
	public void setGeneID(String geneID) {
		this.geneID = geneID;
	}

	/**
	 * @return current transcript report level
	 * @see TranscriptSupportLevels
	 */
	public int getTranscriptSupportLevel() {
		return transcriptSupportLevel;
	}

	/**
	 * @param transcriptSupportLevel set transcript resource level
	 * @see TranscriptSupportLevels
	 */
	public void setTranscriptSupportLevel(int transcriptSupportLevel) {
		this.transcriptSupportLevel = transcriptSupportLevel;
	}

	/**
	 * @return current alignment
	 * @see Alignment
	 */
	public Alignment getSeqAlignment() {
		return seqAlignment;
	}

	/**
	 * @param seqAlignment the {@link Alignment} to set
	 * @see Alignment
	 */
	public void setSeqAlignment(Alignment seqAlignment) {
		this.seqAlignment = seqAlignment;
	}

	/**
	 * @return current alignment parts
	 * @see AlignmentPart
	 */
	public ArrayList<AlignmentPart> getAlignmentParts() {
		return alignmentParts;
	}

	/**
	 * @param alignmentParts the {@link AlignmentPart}s to set
	 * @see AlignmentPart
	 */
	public void setSeqAlignment(ArrayList<AlignmentPart> alignmentParts) {
		this.alignmentParts = alignmentParts;
	}

	/**
	 * @return The "has mismatches" flag.
	 */
	public boolean isHasSubstitutions() {
		return hasSubstitutions;
	}

	/**
	 * Set "has mismatches flag"
	 *
	 * @param b The value to set the flag to.
	 */
	public void setHasSubstitutions(boolean b) {
		this.hasSubstitutions = b;
	}

	/**
	 * @return The "has indels" flag.
	 */
	public boolean isHasIndels() {
		return hasIndels;
	}

	/**
	 * Set "has indels flag"
	 *
	 * @param b The value to set the flag to.
	 */
	public void setHasIndels(boolean b) {
		this.hasIndels = b;
	}

	/**
	 * Describe a part of an alignment, e.g., as parsed from RefSeq.
	 */
	public static class AlignmentPart {
		/**
		 * 0-based begin position on reference.
		 */
		final public int refBeginPos;

		/**
		 * 0-based begin position on reference.
		 */
		final public int refEndPos;

		/**
		 * 0-based begin position on transcript.
		 */
		final public int txBeginPos;

		/**
		 * 0-based begin position on transcript.
		 */
		final public int txEndPos;

		/**
		 * Description from gaps, as in RefSeq "Gap" tag.
		 */
		final public String gap;

		/**
		 * Construct new alignment part.
		 *
		 * @param refBeginPos Begin position in reference (0-based).
		 * @param refEndPos End position in reference.
		 * @param txBeginPos Begin position in transcript (0-based).
		 * @param txEndPos  End position in transcript.
		 * @param gap Gap description.
		 */
		public AlignmentPart(int refBeginPos, int refEndPos, int txBeginPos, int txEndPos,
			String gap) {
			this.refBeginPos = refBeginPos;
			this.refEndPos = refEndPos;
			this.txBeginPos = txBeginPos;
			this.txEndPos = txEndPos;
			this.gap = gap;
		}

		@Override public String toString() {
			return "AlignmentPart{" + "refBeginPos=" + refBeginPos + ", refEndPos=" + refEndPos
				+ ", txBeginPos=" + txBeginPos + ", txEndPos=" + txEndPos + ", gap='" + gap + '\''
				+ '}';
		}
	}
}
