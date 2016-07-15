package de.charite.compbio.jannovar.vardbs.base;

/**
 * Configuration for annotating variants with information from databases.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBAnnotationOptions {

	/** Enum for representing behaviour in the case of multiple matches */
	public enum MultipleMatchBehaviour {
		/** Annotate best one (highest frequency) only */
		BEST_ONLY,
		/** Annotate best one (highest frequency), give alternative ones as an additional list */
		BEST_AND_ALL // TODO
	}

	/**
	 * @return Default options
	 */
	public static DBAnnotationOptions createDefaults() {
		return new DBAnnotationOptions(true, false, "", MultipleMatchBehaviour.BEST_ONLY);
	}

	/** Whether or not to report overlapping variants at all (default: true) */
	private boolean reportOverlapping;
	/** Whether or not to consider overlapping variants as identical (behaviour of other tools, default: false */
	private boolean reportOverlappingAsMatching;
	/** Prefix for identifiers, e.g. "DBSNP_" */
	private String identifierPrefix;
	/** Behaviour on multiple matching annotations */
	private MultipleMatchBehaviour multiMatchBehaviour;

	public DBAnnotationOptions(boolean reportOverlapping, boolean reportOverlappingAsIdentical, String identifierPrefix,
			MultipleMatchBehaviour multiMatchBehaviour) {
		this.reportOverlapping = reportOverlapping;
		this.reportOverlappingAsMatching = reportOverlappingAsIdentical;
		this.identifierPrefix = identifierPrefix;
		this.multiMatchBehaviour = multiMatchBehaviour;

		if (multiMatchBehaviour == MultipleMatchBehaviour.BEST_AND_ALL)
			throw new RuntimeException("Multi-match behaviour BEST_AND_ALL not implemented yet!");
	}

	public boolean isReportOverlapping() {
		return reportOverlapping;
	}

	public void setReportOverlapping(boolean reportOverlapping) {
		this.reportOverlapping = reportOverlapping;
	}

	public boolean isReportOverlappingAsMatching() {
		return reportOverlappingAsMatching;
	}

	public void setReportOverlappingAsMatching(boolean reportOverlappingAsIdentical) {
		this.reportOverlappingAsMatching = reportOverlappingAsIdentical;
	}

	public String getVCFIdentifierPrefix() {
		return identifierPrefix;
	}

	public void setIdentifierPrefix(String identifierPrefix) {
		this.identifierPrefix = identifierPrefix;
	}

	public MultipleMatchBehaviour getMultiMatchBehaviour() {
		return multiMatchBehaviour;
	}

	public void setMultiMatchBehaviour(MultipleMatchBehaviour multiMatchBehaviour) {
		this.multiMatchBehaviour = multiMatchBehaviour;
	}

	public String getIdentifierPrefix() {
		return identifierPrefix;
	}

	@Override
	public String toString() {
		return "DBAnnotationOptions [reportOverlapping=" + reportOverlapping + ", reportOverlappingAsMatching="
				+ reportOverlappingAsMatching + ", identifierPrefix=" + identifierPrefix + ", multiMatchBehaviour="
				+ multiMatchBehaviour + "]";
	}

}
