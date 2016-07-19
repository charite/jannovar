package de.charite.compbio.jannovar.mendel.filter;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Simple representation of a gene.
 *
 * Genes are identified by their name, {@link #equals} and {@link #hashCode} only consider the field {@link #name}!
 */
class Gene {
	private final String name;
	private final ImmutableList<TranscriptModel> transcripts;
	private final ReferenceDictionary refDict;
	private final GenomeInterval region;

	public Gene(ReferenceDictionary refDict, String name, ImmutableList<TranscriptModel> transcripts) {
		this.refDict = refDict;
		this.name = name;
		this.transcripts = transcripts;
		this.region = buildGeneRegion();
	}

	public String getName() {
		return name;
	}

	public ImmutableList<TranscriptModel> getTranscripts() {
		return transcripts;
	}

	public ReferenceDictionary getRefDict() {
		return refDict;
	}

	public GenomeInterval getRegion() {
		return region;
	}

	/**
	 * @return {@link GenomeInterval} of this gene (smallest begin and largest end position of all transcripts).
	 */
	private GenomeInterval buildGeneRegion() {
		if (transcripts.isEmpty())
			return null;

		GenomeInterval region = transcripts.get(0).getTXRegion().withStrand(Strand.FWD);
		for (TranscriptModel tm : transcripts)
			region = mergeRegions(region, tm.getTXRegion());
		return region;
	}

	/**
	 * @return {@link GenomeInterval} from the smaller begin to the larger end position of <code>lhs</code> and
	 *         <code>rhs</code>.
	 */
	private GenomeInterval mergeRegions(GenomeInterval lhs, GenomeInterval rhs) {
		lhs = lhs.withStrand(Strand.FWD);
		rhs = rhs.withStrand(Strand.FWD);
		return new GenomeInterval(lhs.getGenomeBeginPos().getRefDict(), Strand.FWD, lhs.getGenomeBeginPos().getChr(), Math.min(
lhs.getBeginPos(), rhs.getBeginPos()), Math.max(lhs.getEndPos(), rhs.getEndPos()));
	}

	@Override
	public String toString() {
		return this.name + "(" + this.region + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Gene other = (Gene) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}