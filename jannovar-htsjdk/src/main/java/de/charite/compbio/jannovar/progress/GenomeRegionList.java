package de.charite.compbio.jannovar.progress;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A list of {@link GenomeRegion} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final public class GenomeRegionList {

	/** List of {@link GenomeRegion} objects */
	ImmutableList<GenomeRegion> genomeRegions;
	/** Mapping from contig name to {@link GenomeRegion} */
	ImmutableMap<String, GenomeRegion> nameToRegion;

	public GenomeRegionList(Iterable<GenomeRegion> regions) {
		ImmutableList.Builder<GenomeRegion> listBuilder = new ImmutableList.Builder<>();
		ImmutableMap.Builder<String, GenomeRegion> mapBuilder = new ImmutableMap.Builder<>();

		for (GenomeRegion region : regions) {
			listBuilder.add(region);
			mapBuilder.put(region.getContig(), region);
		}

		this.genomeRegions = listBuilder.build();
		this.nameToRegion = mapBuilder.build();
	}
	
	/**
	 * @return Number of bases up to position (contig, pos).
	 */
	public long lengthUpTo(String contig, int pos) {
		if (getGenomeRegion(contig) == null)
			throw new IllegalArgumentException("Contig " + contig + " not found");
		
		long result = 0;
		for (GenomeRegion region : genomeRegions) {
			if (region.getContig().equals(contig)) {
				result += pos;
				break;
			} else {
				result += region.length();
			}
		}
		return result;
	}
	
	public long totalLength() {
		return this.genomeRegions.stream().mapToLong(r -> r.length()).sum();
	}
	
	public GenomeRegion getGenomeRegion(String name) {
		return this.nameToRegion.getOrDefault(name, null);
	}

	public ImmutableList<GenomeRegion> getGenomeRegions() {
		return genomeRegions;
	}

	public void setGenomeRegions(ImmutableList<GenomeRegion> genomeRegions) {
		this.genomeRegions = genomeRegions;
	}

	public ImmutableMap<String, GenomeRegion> getNameToRegion() {
		return nameToRegion;
	}

	public void setNameToRegion(ImmutableMap<String, GenomeRegion> nameToRegion) {
		this.nameToRegion = nameToRegion;
	}

	@Override
	public String toString() {
		return "GenomeRegionList [genomeRegions=" + genomeRegions + ", nameToRegion=" + nameToRegion + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((genomeRegions == null) ? 0 : genomeRegions.hashCode());
		result = prime * result + ((nameToRegion == null) ? 0 : nameToRegion.hashCode());
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
		GenomeRegionList other = (GenomeRegionList) obj;
		if (genomeRegions == null) {
			if (other.genomeRegions != null)
				return false;
		} else if (!genomeRegions.equals(other.genomeRegions))
			return false;
		if (nameToRegion == null) {
			if (other.nameToRegion != null)
				return false;
		} else if (!nameToRegion.equals(other.nameToRegion))
			return false;
		return true;
	}

}
