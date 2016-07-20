package de.charite.compbio.jannovar.progress;

/**
 * A region on a genome, can be a whole chromosome
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenomeRegion {

	/** Name of the containing chromosome */
	private final String contig;
	/** 0-based begin position of half-open interval */
	private final int beginPos;
	/** 0-based end position of half-open interval */
	private final int endPos;

	public GenomeRegion(String contig, int beginPos, int endPos) {
		super();
		this.contig = contig;
		this.beginPos = beginPos;
		this.endPos = endPos;
	}

	public String getContig() {
		return contig;
	}

	public int getBeginPos() {
		return beginPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public int length() {
		return endPos - beginPos;
	}
	
	@Override
	public String toString() {
		return "GenomeRegion [contig=" + contig + ", beginPos=" + beginPos + ", endPos=" + endPos + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginPos;
		result = prime * result + ((contig == null) ? 0 : contig.hashCode());
		result = prime * result + endPos;
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
		GenomeRegion other = (GenomeRegion) obj;
		if (beginPos != other.beginPos)
			return false;
		if (contig == null) {
			if (other.contig != null)
				return false;
		} else if (!contig.equals(other.contig))
			return false;
		if (endPos != other.endPos)
			return false;
		return true;
	}

}
