package de.charite.compbio.jannovar.vardbs.base;

/**
 * Configuration for annotating variants with information from databases.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class DBAnnotationOptions {

	/**
	 * @return Default options
	 */
	static DBAnnotationOptions createDefaults() {
		return new DBAnnotationOptions(true, false, "");
	}

	/** Whether or not to report overlapping variants at all (default: true) */
	private boolean reportOverlapping;
	/** Whether or not to consider overlapping variants as identical (behaviour of other tools, default: false */
	private boolean reportOverlappingAsIdentical;
	/** Prefix for identifiers, e.g. "RS_" */
	private String identifierPrefix;

	public DBAnnotationOptions(boolean reportOverlapping, boolean reportOverlappingAsIdentical,
			String identifierPrefix) {
		this.reportOverlapping = reportOverlapping;
		this.reportOverlappingAsIdentical = reportOverlappingAsIdentical;
		this.identifierPrefix = identifierPrefix;
	}

	public boolean isReportOverlapping() {
		return reportOverlapping;
	}

	public void setReportOverlapping(boolean reportOverlapping) {
		this.reportOverlapping = reportOverlapping;
	}

	public boolean isReportOverlappingAsIdentical() {
		return reportOverlappingAsIdentical;
	}

	public void setReportOverlappingAsIdentical(boolean reportOverlappingAsIdentical) {
		this.reportOverlappingAsIdentical = reportOverlappingAsIdentical;
	}

	public String getVCFIdentifierPrefix() {
		return identifierPrefix;
	}

	public void setIdentifierPrefix(String identifierPrefix) {
		this.identifierPrefix = identifierPrefix;
	}

	@Override
	public String toString() {
		return "DBAnnotationOptions [reportOverlapping=" + reportOverlapping + ", reportOverlappingAsIdentical="
				+ reportOverlappingAsIdentical + ", identifierPrefix=" + identifierPrefix + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifierPrefix == null) ? 0 : identifierPrefix.hashCode());
		result = prime * result + (reportOverlapping ? 1231 : 1237);
		result = prime * result + (reportOverlappingAsIdentical ? 1231 : 1237);
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
		DBAnnotationOptions other = (DBAnnotationOptions) obj;
		if (identifierPrefix == null) {
			if (other.identifierPrefix != null)
				return false;
		} else if (!identifierPrefix.equals(other.identifierPrefix))
			return false;
		if (reportOverlapping != other.reportOverlapping)
			return false;
		if (reportOverlappingAsIdentical != other.reportOverlappingAsIdentical)
			return false;
		return true;
	}

}
