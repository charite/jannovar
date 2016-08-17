package de.charite.compbio.jannovar.vardbs.dbsnp;

import de.charite.compbio.jannovar.Immutable;

/**
 * Information about a DBSNP VCF file
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
final public class DBSNPInfo {

	/** File creation date */
	final String fileDate;
	/** Source */
	final String source;
	/** dbSNP build ID */
	final int dbSNPBuildID;
	/** Reference name */
	final String reference;
	/** State of phasing */
	final String phasing;

	public DBSNPInfo(String fileDate, String source, int dbSNPBuildID, String reference, String phasing) {
		this.fileDate = fileDate;
		this.source = source;
		this.dbSNPBuildID = dbSNPBuildID;
		this.reference = reference;
		this.phasing = phasing;
	}

	public String getFileDate() {
		return fileDate;
	}

	public String getSource() {
		return source;
	}

	public int getDbSNPBuildID() {
		return dbSNPBuildID;
	}

	public String getReference() {
		return reference;
	}

	public String getPhasing() {
		return phasing;
	}

	@Override
	public String toString() {
		return "DBSNPInfo [fileDate=" + fileDate + ", source=" + source + ", dbSNPBuildID=" + dbSNPBuildID
				+ ", reference=" + reference + ", phasing=" + phasing + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dbSNPBuildID;
		result = prime * result + ((fileDate == null) ? 0 : fileDate.hashCode());
		result = prime * result + ((phasing == null) ? 0 : phasing.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		DBSNPInfo other = (DBSNPInfo) obj;
		if (dbSNPBuildID != other.dbSNPBuildID)
			return false;
		if (fileDate == null) {
			if (other.fileDate != null)
				return false;
		} else if (!fileDate.equals(other.fileDate))
			return false;
		if (phasing == null) {
			if (other.phasing != null)
				return false;
		} else if (!phasing.equals(other.phasing))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

}
