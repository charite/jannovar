package de.charite.compbio.jannovar.filter;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Simple representation of a gene.
 *
 * Genes are identified by their name, {@link #equals} and {@link #hashCode} only consider the field {@link #name}!
 */
class Gene {
	public final String name;
	public final ImmutableList<TranscriptModel> transcripts;
	public final ReferenceDictionary refDict;
	public final GenomeInterval region;

	public Gene(ReferenceDictionary refDict, String name, ImmutableList<TranscriptModel> transcripts) {
		this.refDict = refDict;
		this.name = name;
		this.transcripts = transcripts;
		this.region = buildGeneRegion();
	}

	/**
	 * @return {@link GenomeInterval} of this gene (smallest begin and largest end position of all transcripts).
	 */
	private GenomeInterval buildGeneRegion() {
		if (transcripts.isEmpty())
			return null;

		GenomeInterval region = transcripts.get(0).txRegion.withStrand(Strand.FWD);
		for (TranscriptModel tm : transcripts)
			region = mergeRegions(region, tm.txRegion);
		return region;
	}

	/**
	 * @return {@link GenomeInterval} from the smaller begin to the larger end position of <code>lhs</code> and
	 *         <code>rhs</code>.
	 */
	private GenomeInterval mergeRegions(GenomeInterval lhs, GenomeInterval rhs) {
		lhs = lhs.withStrand(Strand.FWD);
		rhs = rhs.withStrand(Strand.FWD);
		return new GenomeInterval(lhs.getGenomeBeginPos().refDict, Strand.FWD, lhs.getGenomeBeginPos().chr, Math.min(
				lhs.beginPos, rhs.beginPos), Math.max(lhs.endPos, rhs.endPos));
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