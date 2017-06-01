package de.charite.compbio.jannovar.vardbs.generic_tsv;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import htsjdk.variant.vcf.VCFHeaderLineType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for generic TSV annotation.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationOptions extends DBAnnotationOptions {

	/**
	 * Construct new TSV annotation configuration from command line option value.
	 * 
	 * <p>
	 * The value must have the format:
	 * <code>pathToTsvFile:oneBasedOffset:colContig:colStart:colEnd:colRef(or=0):
	 * colAlt(or=0):colValue:fieldType:fieldName:fieldDescription:accumulationStrategy</code>
	 * </p>
	 * 
	 * @param strValue
	 *            String to parse from
	 * @return Constructed {@link BedAnnotationOptions} from the given string value.
	 */
	public static GenericTSVAnnotationOptions parseFrom(String strValue) {
		String tokens[] = strValue.split(":");
		if (tokens.length != 13) {
			throw new RuntimeException("Wrong number of tokens tokens in TSV annotation configuration " + strValue
					+ " expected 13 but was " + tokens.length);
		}

		final String pathTsvFile = tokens[0];
		final int oneBasedOffset = Integer.parseInt(tokens[1]);
		final int colContig = Integer.parseInt(tokens[2]);
		final int colStart = Integer.parseInt(tokens[3]);
		final int colEnd = Integer.parseInt(tokens[4]);
		final int colRef = Integer.parseInt(tokens[5]);
		final int colAlt = Integer.parseInt(tokens[6]);
		final boolean refAnnotated = "R".equals(tokens[7]);
		final int colValue = Integer.parseInt(tokens[8]);
		final String fieldType = tokens[9];
		final String fieldName = tokens[10];
		final String fieldDescription = tokens[11];
		final String accStrategy = tokens[12];

		final boolean ovlAsIdentical = (colRef == 0) || (colAlt == 0);
		final GenericTSVAnnotationTarget target = ((colRef == 0) || (colAlt == 0)) ? GenericTSVAnnotationTarget.POSITION
				: GenericTSVAnnotationTarget.VARIANT;

		return new GenericTSVAnnotationOptions(true, ovlAsIdentical, "", MultipleMatchBehaviour.BEST_ONLY,
				new File(pathTsvFile), target, (oneBasedOffset != 0), colContig, colStart, colEnd, colRef, colAlt,
				refAnnotated, ImmutableList.of(fieldName),
				ImmutableMap.of(fieldName,
						new GenericTSVValueColumnDescription(colValue, VCFHeaderLineType.valueOf(fieldType), fieldName,
								fieldDescription, GenericTSVAccumulationStrategy.valueOf(accStrategy))));
	}

	/** File with TSV annotations. */
	private File tsvFile;

	/** Configuration of annotation target. */
	private GenericTSVAnnotationTarget annotationTarget = GenericTSVAnnotationTarget.VARIANT;

	/** Whether or not coordinates are 1-based. */
	private boolean oneBasedPositions = true;

	/** 1-based index of column with contig name */
	private int contigColumnIndex = 1;

	/** 1-based index of column with begin position. */
	private int beginColumnIndex = 2;

	/** 1-based index of column with end position. */
	private int endColumnIndex = 3;

	/** 1-based index of column with reference allele, 0 for none. */
	private int refAlleleColumnIndex = 4;

	/** 1-based index of column with variant allele, 0 for none. */
	private int altAlleleColumnIndex = 5;

	/** Whether or not the ref allele can be annotated. */
	private boolean refAlleleAnnotated = false;

	/** Column description names as ordered as in file. */
	private List<String> columnNames = new ArrayList<>();

	/** Description of value columns. */
	private Map<String, GenericTSVValueColumnDescription> valueColumnDescriptions = new HashMap<>();

	public GenericTSVAnnotationOptions(boolean reportOverlapping, boolean reportOverlappingAsIdentical,
			String identifierPrefix, MultipleMatchBehaviour multiMatchBehaviour, File tsvFile,
			GenericTSVAnnotationTarget annotationTarget, boolean oneBasedPositions, int contigColumnIndex,
			int beginColumnIndex, int endColumnIndex, int refAlleleColumnIndex, int altAlleleColumnIndex,
			boolean refAlleleAnnotated, List<String> columnNames,
			Map<String, GenericTSVValueColumnDescription> valueColumnDescriptions) {
		super(reportOverlapping, reportOverlappingAsIdentical, identifierPrefix, multiMatchBehaviour);

		this.tsvFile = tsvFile;
		this.annotationTarget = annotationTarget;
		this.oneBasedPositions = oneBasedPositions;
		this.contigColumnIndex = contigColumnIndex;
		this.beginColumnIndex = beginColumnIndex;
		this.endColumnIndex = endColumnIndex;
		this.refAlleleColumnIndex = refAlleleColumnIndex;
		this.altAlleleColumnIndex = altAlleleColumnIndex;
		this.refAlleleAnnotated = refAlleleAnnotated;
		this.columnNames = columnNames;
		this.valueColumnDescriptions = valueColumnDescriptions;
	}

	public File getTsvFile() {
		return tsvFile;
	}

	public void setTsvFile(File tsvFile) {
		this.tsvFile = tsvFile;
	}

	public GenericTSVAnnotationTarget getAnnotationTarget() {
		return annotationTarget;
	}

	public void setAnnotationTarget(GenericTSVAnnotationTarget annotationTarget) {
		this.annotationTarget = annotationTarget;
	}

	public boolean isOneBasedPositions() {
		return oneBasedPositions;
	}

	public void setOneBasedPositions(boolean oneBasedPositions) {
		this.oneBasedPositions = oneBasedPositions;
	}

	public int getContigColumnIndex() {
		return contigColumnIndex;
	}

	public void setContigColumnIndex(int contigColumnIndex) {
		this.contigColumnIndex = contigColumnIndex;
	}

	public int getBeginColumnIndex() {
		return beginColumnIndex;
	}

	public void setBeginColumnIndex(int beginColumnIndex) {
		this.beginColumnIndex = beginColumnIndex;
	}

	public int getEndColumnIndex() {
		return endColumnIndex;
	}

	public void setEndColumnIndex(int endColumnIndex) {
		this.endColumnIndex = endColumnIndex;
	}

	public int getRefAlleleColumnIndex() {
		return refAlleleColumnIndex;
	}

	public void setRefAlleleColumnIndex(int refAlleleColumnIndex) {
		this.refAlleleColumnIndex = refAlleleColumnIndex;
	}

	public int getAltAlleleColumnIndex() {
		return altAlleleColumnIndex;
	}

	public void setAltAlleleColumnIndex(int altAlleleColumnIndex) {
		this.altAlleleColumnIndex = altAlleleColumnIndex;
	}

	public boolean isRefAlleleAnnotated() {
		return refAlleleAnnotated;
	}

	public void setRefAlleleAnnotated(boolean refAlleleAnnotated) {
		this.refAlleleAnnotated = refAlleleAnnotated;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public Map<String, GenericTSVValueColumnDescription> getValueColumnDescriptions() {
		return valueColumnDescriptions;
	}

	public void setValueColumnDescriptions(Map<String, GenericTSVValueColumnDescription> valueColumnDescriptions) {
		this.valueColumnDescriptions = valueColumnDescriptions;
	}

	@Override
	public String toString() {
		return "GenericTSVAnnotationOptions [tsvFile=" + tsvFile + ", annotationTarget=" + annotationTarget
				+ ", oneBasedPositions=" + oneBasedPositions + ", contigColumnIndex=" + contigColumnIndex
				+ ", beginColumnIndex=" + beginColumnIndex + ", endColumnIndex=" + endColumnIndex
				+ ", refAlleleColumnIndex=" + refAlleleColumnIndex + ", altAlleleColumnIndex=" + altAlleleColumnIndex
				+ ", refAlleleAnnotated=" + refAlleleAnnotated + ", columnNames=" + columnNames
				+ ", valueColumnDescriptions=" + valueColumnDescriptions + "]";
	}

}
