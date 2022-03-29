package de.charite.compbio.jannovar.reference;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import de.charite.compbio.jannovar.Immutable;
import java.io.Serializable;
import java.util.Map;

/**
 * The information representing a transcript model.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class TranscriptModel implements Serializable, Comparable<TranscriptModel> {

	/**
	 * Accession number of the transcript (e.g., the UCSC knownGene id - uc011nca.2). The version number may be
	 * included.
	 */
	private final String accession;

	/**
	 * Gene symbol of the known Gene. Can be null for some genes. Note that in annovar, $name2 corresponds to the
	 * geneSymbol if available, otherwise the kgID is used.
	 */
	private final String geneSymbol;

	/**
	 * Genomic interval with transcript begin/end.
	 */
	private final GenomeInterval txRegion;

	/**
	 * Genomic interval with CDS begin/end.
	 *
	 * <b>Note</b> that in Jannovar, the CDS region includes the start and stop codon.
	 */
	private final GenomeInterval cdsRegion;

	/**
	 * Genomic intervals with the exons, order is dictated by strand of transcript.
	 */
	private final ImmutableList<GenomeInterval> exonRegions;

	/**
	 * cDNA sequence of the spliced RNA of this known gene transcript.
	 */
	private final String sequence;

	/**
	 * The gene ID, from Ensembl (<code>"ENS[MUS]*G0+([0-9]+)"</code>), Entrez ("<code>ENTREZ([0-9]+)</code>
	 * "), RefSeq ("<code>gene([0-9]+)</code>").
	 *
	 * <code>null</code> for no available gene ID.
	 */
	private final String geneID;

	/**
	 * Alternative gene IDs, as parsed from RefSeq GFF3 file
	 * <p>
	 * See {@link #getAltGeneIDs()} for more information
	 */
	private final ImmutableSortedMap<String, String> altGeneIDs;

	/**
	 * The transcript support level of the this transcript (the lower the better).
	 *
	 * @see TranscriptSupportLevels
	 * @see <a href="http://www.ensembl.org/Help/Glossary?id=492">http://www.ensembl.org/Help/Glossary?id=492</a>
	 */
	private final int transcriptSupportLevel;

	/**
	 * The alignment of the transcript sequence to the genomic exon region.
	 */
	private final Alignment seqAlignment;

	/**
	 * Whether or not the transcript aligns with mismatches to the reference.
	 */
	private final boolean hasSubstitutions;

	/**
	 * Whether or not the transcript aligns with indels to the reference.
	 */
	private final boolean hasIndels;

	/**
	 * Class version (for serialization).
	 */
	private static final long serialVersionUID = 4L;

	/**
	 * Initialize the {@link TranscriptModel} object from the given parameters.
	 */
	public TranscriptModel(String accession, String geneSymbol, GenomeInterval txRegion,
		GenomeInterval cdsRegion, ImmutableList<GenomeInterval> exonRegions, String sequence,
		String geneID, int transcriptSupportLevel, boolean hasIndels, boolean hasSubstitutions) {
		this(accession, geneSymbol, txRegion, cdsRegion, exonRegions, sequence, geneID,
			transcriptSupportLevel, hasSubstitutions, hasIndels, ImmutableMap.of(),
			Alignment.createUngappedAlignment(sequence.length()));
	}

	/**
	 * Initialize the {@link TranscriptModel} object from the given parameters.
	 */
	public TranscriptModel(String accession, String geneSymbol, GenomeInterval txRegion,
		GenomeInterval cdsRegion, ImmutableList<GenomeInterval> exonRegions, String sequence,
		String geneID, int transcriptSupportLevel, boolean hasSubstitutions, boolean hasIndels,
		Map<String, String> altGeneIDs, Alignment seqAlignment) {
		this.accession = accession;
		this.geneSymbol = geneSymbol;
		this.txRegion = txRegion;
		this.cdsRegion = cdsRegion;
		this.exonRegions = exonRegions;
		this.sequence = sequence;
		this.geneID = geneID;
		this.transcriptSupportLevel = transcriptSupportLevel;
		this.altGeneIDs = ImmutableSortedMap.copyOf(altGeneIDs);
		this.seqAlignment = seqAlignment;
		this.hasSubstitutions = hasSubstitutions;
		this.hasIndels = hasIndels;
		checkForConsistency();
	}

	/**
	 * @return accession number
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * @return the gene symbol
	 */
	public String getGeneSymbol() {
		return geneSymbol;
	}

	/**
	 * @return transcript's genomic region
	 */
	public GenomeInterval getTXRegion() {
		return txRegion;
	}

	/**
	 * @return CDS genomic region
	 */
	public GenomeInterval getCDSRegion() {
		return cdsRegion;
	}

	/**
	 * @return genomic intervals with the exons, order is dictated by strand of transcript.
	 */
	public ImmutableList<GenomeInterval> getExonRegions() {
		return exonRegions;
	}

	/**
	 * @return mDNA sequence of the spliced RNA of this known gene transcript.
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @return mDNA sequence of the spliced RNA of this known gene transcript with leading and
	 * trailing sequences removed.
	 */
	public String getTrimmedSequence() {
		final Alignment ali = seqAlignment;
		return sequence.substring(ali.refLeadingGapLength(), sequence.length() - ali.refTrailingGapLength());
	}

	/**
	 * @Return the sequence alignment to the exonic genome reference
	 */
	public Alignment getSeqAlignment() { return seqAlignment; }

	/**
	 * @return The gene ID, from Ensembl (<code>"ENS[MUS]*G0+([0-9]+)"</code>), Entrez ("<code>ENTREZ([0-9]+)</code>
	 * "), RefSeq ("<code>gene([0-9]+)</code>"). <code>null</code> for no available gene ID.
	 */
	public String getGeneID() {
		return geneID;
	}

	/**
	 * Return mapping containing alternative gene IDs, as parsed from RefSeq GFF3 file
	 * <p>
	 * The alternative identifiers used are the values of {@code AltGeneIDType} converted to strings.
	 */
	public ImmutableSortedMap<String, String> getAltGeneIDs() {
		return altGeneIDs;
	}

	/**
	 * @return transcript support level of the this transcript (the lower the better).
	 * @see TranscriptSupportLevels
	 * @see <a href="http://www.ensembl.org/Help/Glossary?id=492">http://www.ensembl.org/Help/Glossary?id=492</a>
	 */
	public int getTranscriptSupportLevel() {
		return transcriptSupportLevel;
	}

	/**
	 * @return the strand of the transcript
	 */
	public Strand getStrand() {
		return txRegion.getStrand();
	}

	/**
	 * @return the chromosome of the transcript
	 */
	public int getChr() {
		return txRegion.getChr();
	}

	/**
	 * @return <tt>true</tt> if this is a gene-coding transcript, marked by <tt>cdsRegion</tt> being empty.
	 */
	public boolean isCoding() {
		return (this.cdsRegion.getBeginPos() < this.cdsRegion.getEndPos());
	}

	/**
	 * @return <tt>true</tt> if this transcript is on the mitochondrial chromosome
	 */
	public boolean isMitochondrial() {
		return this.txRegion.getChr() == 25;
	}

	/**
	 * @return the length of the coding exon sequence
	 */
	public int cdsTranscriptLength() {
		int result = 0;
		for (GenomeInterval region : exonRegions)
			result += region.intersection(cdsRegion).length();
		return result;
	}

	/**
	 * @return the sum of the exon sequence lengths
	 */
	public int transcriptLength() {
		int result = 0;
		for (GenomeInterval region : exonRegions)
			result += region.length();
		return result;
	}

	/**
	 * @return whether aligns with mismatches
	 */
	public boolean isHasSubstitutions() {
		return hasSubstitutions;
	}

	/**
	 * @return whether aligns with indels
	 */
	public boolean isHasIndels() {
		return hasIndels;
	}

	/**
	 * @param i 0-based index of the intron's region to return
	 * @return {@link GenomeInterval} with the intron's region
	 */
	public GenomeInterval intronRegion(int i) {
		// TODO(holtgrem): test me!
		GenomeInterval exonRegionL = exonRegions.get(i);
		GenomeInterval exonRegionR = exonRegions.get(i + 1);
		return new GenomeInterval(exonRegionL.refDict, exonRegionL.getStrand(), exonRegionL.getChr(),
			exonRegionL.getEndPos(), exonRegionR.getBeginPos(), PositionType.ZERO_BASED);
	}

	/**
	 * Ensures that the strands are consistent.
	 */
	private void checkForConsistency() {
		Strand strand = txRegion.getStrand();
		assert (txRegion.getStrand() == strand);
		assert (cdsRegion.getStrand() == strand);
		for (GenomeInterval region : exonRegions)
			assert (region.getStrand() == strand);
	}

	@Override
	public String toString() {
		return accession + "(" + txRegion + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accession == null) ? 0 : accession.hashCode());
		result = prime * result + ((cdsRegion == null) ? 0 : cdsRegion.hashCode());
		result = prime * result + ((exonRegions == null) ? 0 : exonRegions.hashCode());
		result = prime * result + ((geneID == null) ? 0 : geneID.hashCode());
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + transcriptSupportLevel;
		result = prime * result + ((txRegion == null) ? 0 : txRegion.hashCode());
		result = prime * result + ((seqAlignment == null) ? 0 : seqAlignment.hashCode());
		result = prime * result + ((seqAlignment == null) ? 0 : seqAlignment.hashCode());
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
		TranscriptModel other = (TranscriptModel) obj;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
			return false;
		if (cdsRegion == null) {
			if (other.cdsRegion != null)
				return false;
		} else if (!cdsRegion.equals(other.cdsRegion))
			return false;
		if (exonRegions == null) {
			if (other.exonRegions != null)
				return false;
		} else if (!exonRegions.equals(other.exonRegions))
			return false;
		if (geneID == null) {
			if (other.geneID != null)
				return false;
		} else if (!geneID.equals(other.geneID))
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (transcriptSupportLevel != other.transcriptSupportLevel)
			return false;
		if (txRegion == null) {
			if (other.txRegion != null)
				return false;
		} else if (!txRegion.equals(other.txRegion))
			return false;
		if (seqAlignment == null) {
			if (other.seqAlignment != null)
				return false;
		} else if (!seqAlignment.equals(other.seqAlignment))
			return false;
		return true;
	}

	public int compareTo(TranscriptModel o) {
		int result = -1;
		if (geneID != null && o.geneID != null) {
			result = geneID.compareTo(o.geneID);
			if (result != 0)
				return result;
		}

		result = geneSymbol.compareTo(o.geneSymbol);
		if (result != 0)
			return result;

		return accession.compareTo(o.accession);
	}

}
